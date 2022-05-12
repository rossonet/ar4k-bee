package org.ar4k.bee.daemon.bind9;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.ar4k.agent.Ar4kAgent;
import org.ar4k.bee.service.ManagedService;
import org.ar4k.bee.utils.LogUtils;

public class DnsManager implements ManagedService {

	private static final String CONFIG_DIRECTORY = Ar4kAgent.getAppManager().getCacheDirectoryPath() + File.separator
			+ "bind6";

	private static final Path CONFIG_FILE = Paths.get(CONFIG_DIRECTORY + File.separator + "named.conf");

	private static final Path ERR_FILE = Paths.get(CONFIG_DIRECTORY + File.separator + "execution.error");
	private static final Path LOG_FILE = Paths.get(CONFIG_DIRECTORY + File.separator + "execution.log");

	private static final Logger logger = Logger.getLogger(DnsManager.class.getName());

	private static final String[] START_COMMAND = { "/usr/sbin/named", "-4", "-f", "-c",
			CONFIG_FILE.toAbsolutePath().toString(), "-p", "53" };

	private static final String TEMPLATE_FILE = "templates/bind9.conf";

	private Process namedProcess = null;

	private void generateConfigFile() {
		try {
			final Path pathDirectoryConf = Paths.get(CONFIG_DIRECTORY);
			if (!Files.exists(pathDirectoryConf)) {
				Files.createDirectories(pathDirectoryConf);
			}
			final String template = ManagedService.getResourceFileAsString(TEMPLATE_FILE);
			final String elaboratedConfig = popolateConfig(template);
			Ar4kAgent.writeStringToFile(elaboratedConfig, CONFIG_FILE.toAbsolutePath().toString());
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
		namedProcess = processBuilder.start();
	}

	@Override
	public void stop() throws IOException {
		if (namedProcess != null && namedProcess.isAlive()) {
			namedProcess.destroy();
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DnsManager [");
		if (namedProcess != null) {
			builder.append("namedProcess=");
			builder.append(namedProcess);
		}
		builder.append("]");
		return builder.toString();
	}

}
