package net.orange.jproxy.render;

import org.lwjgl.opengl.GL46;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.orange.jproxy.JProxy;
import net.orange.jproxy.render.shader.BackgroundRenderer;
import net.orange.jproxy.resource.GifDecoder;
import net.orange.jproxy.resource.JProxyResource;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JProxyRenderer extends ApplicationAdapter {

	SpriteBatch batch;
	BitmapFont font;
	BackgroundRenderer backgroundRenderer;
	Texture image;
	Array<TextureRegion> frames;
	long time;
	int currentFrame;
	boolean buttonPressed;
	boolean isConnectingOrDisconnecting;
	GlyphLayout glyphLayout;
	int currentProxyIndex;
	long lastProxySwitchTime;
	static final long MIN_PROXY_SWITCH_DELAY = 1500;

	@Override
	public void create() {
		batch = new SpriteBatch();
		time = System.currentTimeMillis();
		backgroundRenderer = new BackgroundRenderer();
		font = new FontManager().getFont("fonts/main.otf", 25);
		image = new Texture(Gdx.files.internal(new JProxyResource("icons/round.png").getResourcePath()));
		frames = GifDecoder.decodeGif(Gdx.files.internal(new JProxyResource("icons/loading.gif").getResourcePath()));
		glyphLayout = new GlyphLayout();
		JProxy.INSTANCE.setSelectedProxy(JProxy.INSTANCE.getProxies().get(0));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL46.GL_COLOR_BUFFER_BIT);
		backgroundRenderer.render(((System.currentTimeMillis() - time) / 1000F),
				new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()));
		final float buttonWidth = 150, buttonHeight = 50;
		final var buttonX = Gdx.graphics.getWidth() / 2 - buttonWidth / 2;
		final var buttonY = Gdx.graphics.getHeight() / 2 - 90 - buttonHeight / 2;
		final var gifX = buttonX + buttonWidth / 2 - 40;
		final var gifY = buttonY + 90;
		if (!isConnectingOrDisconnecting && isClicked(buttonX, buttonY, buttonWidth, buttonHeight)) {
			if (!buttonPressed) {
				buttonPressed = true;
				if (JProxy.INSTANCE.isConnected())
					disconnectAsync();
				else
					connectAsync();
			}
		} else
			buttonPressed = false;
		float proxyBoxWidth = 50, proxyBoxHeight = 50;
		final var proxyBoxX = Gdx.graphics.getWidth() - proxyBoxWidth - 30;
		final var proxyBoxY = Gdx.graphics.getHeight() - proxyBoxHeight - 30;
		if (isClicked(proxyBoxX, proxyBoxY, proxyBoxWidth, proxyBoxHeight)
				&& System.currentTimeMillis() - lastProxySwitchTime > MIN_PROXY_SWITCH_DELAY) {
			lastProxySwitchTime = System.currentTimeMillis();
			currentProxyIndex = (currentProxyIndex + 1) % JProxy.INSTANCE.getProxies().size();
			JProxy.INSTANCE.setSelectedProxy(JProxy.INSTANCE.getProxies().get(currentProxyIndex));
			if (JProxy.INSTANCE.isConnected()) {
				disconnectAsync();
				connectAsync();
			}
		}
		batch.begin();
		batch.draw(image, buttonX, buttonY, buttonWidth, buttonHeight);
		var text = JProxy.INSTANCE.isConnected() ? "Disconnect!" : "Connect!";
		glyphLayout.setText(font, text);
		font.setColor(Color.WHITE);
		font.draw(batch, text, buttonX + buttonWidth / 2 - glyphLayout.width / 2,
				buttonY + buttonHeight / 2 + glyphLayout.height / 2);
		if (isConnectingOrDisconnecting)
			batch.draw(frames.get(currentFrame), gifX, gifY, 80, 80);
		float proxyOutlineWidth = proxyBoxWidth + 10, proxyOutlineHeight = proxyBoxHeight + 10;
		float proxyOutlineX = proxyBoxX - 5, proxyOutlineY = proxyBoxY - 5;
		batch.draw(image, proxyOutlineX, proxyOutlineY, proxyOutlineWidth, proxyOutlineHeight);
		try {
			batch.draw(new Texture(Gdx.files
					.internal(new JProxyResource("icons/" + JProxy.INSTANCE.getSelectedProxy().getCountry() + ".png")
							.getResourcePath())),
					proxyBoxX, proxyBoxY, proxyBoxWidth, proxyBoxHeight);
		} catch (GdxRuntimeException e) {
			batch.draw(new Texture(Gdx.files.internal(new JProxyResource("icons/unknown.png").getResourcePath())),
					proxyBoxX, proxyBoxY, proxyBoxWidth, proxyBoxHeight);
		}
		text = "Connected to " + JProxy.INSTANCE.getSelectedProxy().getIp() + ":"
				+ JProxy.INSTANCE.getSelectedProxy().getPort();
		glyphLayout.setText(font, text);
		font.setColor(Color.GREEN);
		if (JProxy.INSTANCE.isConnected() && !isConnectingOrDisconnecting)
			font.draw(batch, text, buttonX + buttonWidth / 2 - glyphLayout.width / 2,
					buttonY + buttonHeight / 2 + glyphLayout.height / 2 - 90);
		batch.end();
		if (isConnectingOrDisconnecting)
			currentFrame = (currentFrame + 1) % frames.size;
	}

	private void connectAsync() {
		isConnectingOrDisconnecting = true;
		new Thread(() -> {
			JProxy.INSTANCE.connect();
			isConnectingOrDisconnecting = false;
		}).start();
	}

	private void disconnectAsync() {
		isConnectingOrDisconnecting = true;
		new Thread(() -> {
			JProxy.INSTANCE.disconnect();
			isConnectingOrDisconnecting = false;
		}).start();
	}

	@Override
	public void dispose() {
		if (JProxy.INSTANCE.isConnected())
			JProxy.INSTANCE.disconnect();
		System.exit(0);
		batch.dispose();
	}

	private boolean isClicked(final float x, final float y, final float width, final float height) {
		if (Gdx.input.isTouched()) {
			final var worldX = Gdx.input.getX();
			final var worldY = Gdx.graphics.getHeight() - Gdx.input.getY();
			return new Rectangle(x, y, width, height).contains(worldX, worldY);
		}
		return false;
	}
}