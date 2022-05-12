package org.ar4k.bee.daemon.tftp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.ar4k.agent.Ar4kAgent;
import org.ar4k.bee.service.ManagedService;
import org.ar4k.bee.utils.LogUtils;

public class TftpdManager implements ManagedService {
	private static final String DATA_DIRECTORY = Ar4kAgent.getAppManager().getCacheDirectoryPath() + File.separator
			+ "tftpd";
	private static final Path ERR_FILE = Paths.get(DATA_DIRECTORY + File.separator + "execution.error");
	private static final Path LOG_FILE = Paths.get(DATA_DIRECTORY + File.separator + "execution.log");
	private static final Logger logger = Logger.getLogger(TftpdManager.class.getName());
	private static final String[] START_COMMAND = { "/usr/sbin/in.tftpd", DATA_DIRECTORY };

	private Process tftpdProcess = null;

	private void generateDataFile() {
		try {
			final Path pathDirectoryConf = Paths.get(DATA_DIRECTORY);
			if (!Files.exists(pathDirectoryConf)) {
				Files.createDirectories(pathDirectoryConf);
			}
		} catch (final IOException e) {
			logger.severe(LogUtils.stackTraceToString(e));
		}

	}

	@Override
	public void reload() throws IOException {
		stop();
		start();
	}

	@Override
	public void start() throws IOException {
		generateDataFile();
		final ProcessBuilder processBuilder = new ProcessBuilder(START_COMMAND);
		processBuilder.directory(new File(DATA_DIRECTORY));
		processBuilder.redirectOutput(LOG_FILE.toAbsolutePath().toFile());
		processBuilder.redirectError(ERR_FILE.toAbsolutePath().toFile());
		tftpdProcess = processBuilder.start();
	}

	@Override
	public void stop() throws IOException {
		if (tftpdProcess != null && tftpdProcess.isAlive()) {
			tftpdProcess.destroy();
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("TftpdManager [");
		if (tftpdProcess != null) {
			builder.append("tftpdProcess=");
			builder.append(tftpdProcess);
		}
		builder.append("]");
		return builder.toString();
	}

}
