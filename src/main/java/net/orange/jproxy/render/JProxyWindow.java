package net.orange.jproxy.render;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JProxyWindow {
	
	@Getter @Setter
	Lwjgl3Application window;
	
	public static Lwjgl3Application createWindow(Lwjgl3ApplicationConfiguration config, ApplicationAdapter adapter) {
		return new Lwjgl3Application(adapter, config);
	}
}
