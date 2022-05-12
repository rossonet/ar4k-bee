package org.ar4k.bee.daemon.httpd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.ar4k.agent.Ar4kAgent;
import org.ar4k.bee.service.ManagedService;
import org.ar4k.bee.utils.LogUtils;

public class ApacheManager implements ManagedService {
	private static final String CONFIG_DIRECTORY = Ar4kAgent.getAppManager().getCacheDirectoryPath() + File.separator
			+ "apache2";
	private static final Path CONFIG_FILE = Paths.get(CONFIG_DIRECTORY + File.separator + "httpd.conf");
	private static final Path ERR_FILE = Paths.get(CONFIG_DIRECTORY + File.separator + "execution.error");
	private static final Path LOG_FILE = Paths.get(CONFIG_DIRECTORY + File.separator + "execution.log");

	private static final Logger logger = Logger.getLogger(ApacheManager.class.getName());

	private static final String[] START_COMMAND = { "/usr/sbin/apachectl", "-D", "FOREGROUND", "-f",
			CONFIG_FILE.toAbsolutePath().toString() };

	private static final String TEMPLATE_FILE = "templates/apache2.conf";

	private Process apacheProcess = null;

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
		apacheProcess = processBuilder.start();
	}

	@Override
	public void stop() throws IOException {
		if (apacheProcess != null && apacheProcess.isAlive()) {
			apacheProcess.destroy();
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ApacheManager [");
		if (apacheProcess != null) {
			builder.append("apacheProcess=");
			builder.append(apacheProcess);
		}
		builder.append("]");
		return builder.toString();
	}

}
