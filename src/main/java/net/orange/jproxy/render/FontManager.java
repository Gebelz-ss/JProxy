package net.orange.jproxy.render;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.orange.jproxy.resource.JProxyResource;

@Slf4j
public class FontManager {
	private final Map<String, BitmapFont> fonts = new ConcurrentHashMap<>();

	public BitmapFont getFont(final String fontName, final int size) {
		final var key = fontName + size;
		return fonts.computeIfAbsent(key, k -> generateFont(new JProxyResource(fontName), size));
	}

	@SneakyThrows
	private BitmapFont generateFont(final JProxyResource resource, final int size) {
		final var fontFile = Gdx.files.internal(resource.getResourcePath());
		final var generator = new FreeTypeFontGenerator(fontFile);
		final var parameter = new FreeTypeFontParameter();
		parameter.size = size;
		final var font = generator.generateFont(parameter);
		generator.dispose();
		return font;
	}

	public void dispose() {
		fonts.values().forEach(BitmapFont::dispose);
		fonts.clear();
	}
}