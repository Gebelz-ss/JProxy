package net.orange.jproxy.resource;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class JProxyResource {
	String resourcePath;

	public JProxyResource(final String resourcePath) {
		this.resourcePath = "%s%c%s%c%s".formatted("assets", '/', "jproxy", '/', resourcePath);
	}
}
