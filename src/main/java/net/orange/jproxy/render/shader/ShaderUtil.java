package net.orange.jproxy.render.shader;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.orange.jproxy.resource.JProxyResource;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShaderUtil {

	static Map<String, ShaderProgram> shaderCache = new HashMap<>();

	public static ShaderProgram loadShader(final JProxyResource vertexShaderPath,
			final JProxyResource fragmentShaderPath) {
		return shaderCache.computeIfAbsent(vertexShaderPath + "|" + fragmentShaderPath, key -> {
			final var vertexShaderSource = Gdx.files.internal(vertexShaderPath.getResourcePath()).readString();
			final var fragmentShaderSource = Gdx.files.internal(fragmentShaderPath.getResourcePath()).readString();
			final var shaderProgram = new ShaderProgram(vertexShaderSource, fragmentShaderSource);
			if (!shaderProgram.isCompiled()) {
				log.error("Error compiling shader: " + shaderProgram.getLog());
				return null;
			}
			return shaderProgram;
		});
	}

	public static void useShader(final ShaderProgram shaderProgram) {
		if (shaderProgram != null) {
			shaderProgram.bind();
		}
	}

	public static void deleteShader(final ShaderProgram shaderProgram) {
		if (shaderProgram != null) {
			shaderProgram.dispose();
		}
	}
}