package net.orange.jproxy.resource;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import lombok.Cleanup;
import lombok.SneakyThrows;

public class GifDecoder {

	@SneakyThrows
	public static Array<TextureRegion> decodeGif(final FileHandle fileHandle) {
		@Cleanup
		final var input = ImageIO.createImageInputStream(fileHandle.read());
		final var reader = ImageIO.getImageReaders(input).next();
		reader.setInput(input);
		final var numFrames = reader.getNumImages(true);
		Array<TextureRegion> frames = new Array<>(numFrames);
		for (var i = 0; i < numFrames; i++) {
			frames.add(convertToTextureRegion(reader.read(i)));
		}
		reader.dispose();
		return frames;
	}

	private static TextureRegion convertToTextureRegion(final BufferedImage frame) {
		final var pixmap = new Pixmap(frame.getWidth(), frame.getHeight(), Pixmap.Format.RGBA8888);
		for (var x = 0; x < frame.getWidth(); x++) {
			for (var y = 0; y < frame.getHeight(); y++) {
				final var rgb = frame.getRGB(x, y);
				pixmap.setColor(((rgb >> 16) & 0xff) / 255f, ((rgb >> 8) & 0xff) / 255f, (rgb & 0xff) / 255f,
						((rgb >> 24) & 0xff) / 255f);
				pixmap.drawPixel(x, y);
			}
		}
		final var texture = new Texture(pixmap);
		final var textureRegion = new TextureRegion(texture);
		pixmap.dispose();
		return textureRegion;
	}
}