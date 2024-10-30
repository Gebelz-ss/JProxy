package net.orange.jproxy.config;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.orange.jproxy.resource.JProxyResource;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class JProxyConfig {
	Lwjgl3ApplicationConfiguration config;

	public static Lwjgl3ApplicationConfiguration createConfig(final String windowTitle, final Vector2 windowSize,
			final JProxyResource windowIcon) {
		final var config = new Lwjgl3ApplicationConfiguration();
		config.setTitle(windowTitle);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
		config.setResizable(false);
		config.setWindowedMode((int) windowSize.x, (int) windowSize.y);
		config.setWindowIcon(windowIcon.getResourcePath());
		return config;
	}
}
