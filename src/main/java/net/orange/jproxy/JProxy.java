package net.orange.jproxy;

import static lombok.AccessLevel.PRIVATE;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.Vector2;
import com.profesorfalken.jpowershell.PowerShell;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import net.orange.jproxy.config.JProxyConfig;
import net.orange.jproxy.proxy.Proxy;
import net.orange.jproxy.render.JProxyRenderer;
import net.orange.jproxy.render.JProxyWindow;
import net.orange.jproxy.resource.JProxyResource;

@FieldDefaults(level = PRIVATE)
@Getter
public enum JProxy {
	INSTANCE;

	List<Proxy> proxies = readFileToList("proxy.txt").stream().map(s -> s.split(":")).filter(parts -> parts.length == 2)
			.map(parts -> new Proxy(parts[0], parts[1].split(" ")[0], parts[1].split(" ")[1]))
			.collect(Collectors.toList());
	@Setter
	Proxy selectedProxy;
	JProxyWindow window = new JProxyWindow();
	JProxyConfig config = new JProxyConfig();
	boolean connected;
	PowerShell powerShell = PowerShell.openSession();

	public void start() {
		config.setConfig(
				JProxyConfig.createConfig("JProxy", new Vector2(800, 600), new JProxyResource("icons/JProxy.png")));
		window.setWindow(JProxyWindow.createWindow(config.getConfig(), new JProxyRenderer()));
	}

	@SneakyThrows
	private List<String> readFileToList(final String filePath) {
		return Files.lines(Paths.get(filePath)).collect(Collectors.toList());
	}

	public void connect() {
		executePowerShellCommand(
				"reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 1 /f");
		executePowerShellCommand(
				"reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyServer /t REG_SZ /d \"http=%s;https=%s\" /f"
						.formatted(selectedProxy.getIp() + ":" + selectedProxy.getPort(),
								selectedProxy.getIp() + ":" + selectedProxy.getPort()));
		connected = true;
	}

	public void disconnect() {
		executePowerShellCommand(
				"reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 0 /f");
		connected = false;
	}

	@SneakyThrows
	private void executePowerShellCommand(String command) {
		powerShell.executeCommand(command);
	}

	public void close() {
		if (powerShell != null) {
			powerShell.close();
		}
	}
}