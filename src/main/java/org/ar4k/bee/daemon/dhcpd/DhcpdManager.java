package org.ar4k.bee.daemon.dhcpd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.ar4k.agent.Ar4kAgent;
import org.ar4k.bee.service.ManagedService;
import org.ar4k.bee.utils.LogUtils;

public class DhcpdManager implements ManagedService {

	private static final String CONFIG_DIRECTORY = Ar4kAgent.getAppManager().getCacheDirectoryPath() + File.separator
			+ "dhcpd";
	private static final Path CONFIG_FILE = Paths.get(CONFIG_DIRECTORY + File.separator + "dhcpd.conf");
	private static final Path ERR_FILE = Paths.get(CONFIG_DIRECTORY + File.separator + "execution.error");

	private static final String LEASES_FILE = CONFIG_DIRECTORY + File.separator + "dhcpd.leases";
	private static final Path LOG_FILE = Paths.get(CONFIG_DIRECTORY + File.separator + "execution.log");

	private static final Logger logger = Logger.getLogger(DhcpdManager.class.getName());

	private static final String PID_FILE = CONFIG_DIRECTORY + File.separator + "dhcpd.pid";

	private static final String[] START_COMMAND = { "/usr/sbin/dhcpd", "-f", "-4", "-pf", PID_FILE, "-cf",
			CONFIG_FILE.toAbsolutePath().toString(), "-lf", LEASES_FILE };

	private static final String TEMPLATE_FILE = "templates/dhcpd.conf";

	private Process dhcpdProcess = null;

	private void generateConfigFile() {
		try {
			final Path pathDirectoryConf = Paths.get(CONFIG_DIRECTORY);
			if (!Files.exists(pathDirectoryConf)) {
				Files.createDirectories(pathDirectoryConf);
			}
			final String template = ManagedService.getResourceFileAsString(TEMPLATE_FILE);
			final String elaboratedConfig = popolateConfig(template);
			Ar4kAgent.writeStringToFile(elaboratedConfig, CONFIG_FILE.toAbsolutePath().toString());
			if (!Files.exists(Paths.get(LEASES_FILE))) {
				Files.createFile(Paths.get(LEASES_FILE));
			}
		} catch (final IOException e) {
			logger.severe(LogUtils.stackTraceToString(e));
		}

	}

	private String popolateConfig(final String template) {
		// TODO Auto-generated method stub
		return template;
	}

	@Override
	public void reload() throws IOException {
		stop();
		start();
	}

	@Override
	public void start() throws IOException {
		generateConfigFile();
		final ProcessBuilder processBuilder = new ProcessBuilder(START_COMMAND);
		processBuilder.directory(new File(CONFIG_DIRECTORY));
		processBuilder.redirectOutput(LOG_FILE.toAbsolutePath().toFile());
		processBuilder.redirectError(ERR_FILE.toAbsolutePath().toFile());
		dhcpdProcess = processBuilder.start();
	}

	@Override
	public void stop() throws IOException {
		if (dhcpdProcess != null && dhcpdProcess.isAlive()) {
			dhcpdProcess.destroy();
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DhcpdManager [");
		if (dhcpdProcess != null) {
			builder.append("dhcpdProcess=");
			builder.append(dhcpdProcess);
		}
		builder.append("]");
		return builder.toString();
	}

}
