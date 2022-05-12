package org.ar4k.bee.daemon;

import java.io.IOException;
import java.util.logging.Logger;

import org.ar4k.agent.Ar4kAgent;
import org.ar4k.bee.daemon.bind9.DnsManager;
import org.ar4k.bee.daemon.dhcpd.DhcpdManager;
import org.ar4k.bee.daemon.httpd.ApacheManager;
import org.ar4k.bee.daemon.tftp.TftpdManager;
import org.ar4k.bee.service.ConfigurationRefresh;
import org.ar4k.bee.utils.LogUtils;

public class DaemonManager implements ConfigurationRefresh {

	private static final DaemonManager INSTANCE = new DaemonManager();

	private static final Logger logger = Logger.getLogger(DaemonManager.class.getName());

	public static DaemonManager getInstance() {
		return INSTANCE;
	}

	private final DhcpdManager dhcp;

	private final DnsManager dns;

	private final ApacheManager httpd;

	private final TftpdManager tftp;

	private DaemonManager() {
		this.dns = new DnsManager();
		this.dhcp = new DhcpdManager();
		this.httpd = new ApacheManager();
		this.tftp = new TftpdManager();
		Ar4kAgent.getAppManager().registerService(this);
	}

	public DhcpdManager getDhcp() {
		return dhcp;
	}

	public DnsManager getDns() {
		return dns;
	}

	public ApacheManager getHttpd() {
		return httpd;
	}

	public TftpdManager getTftp() {
		return tftp;
	}

	private void reloadAllServices() {
		try {
			this.dns.reload();
		} catch (final IOException e) {
			logger.severe(LogUtils.stackTraceToString(e));
		}
		try {
			this.dhcp.reload();
		} catch (final IOException e) {
			logger.severe(LogUtils.stackTraceToString(e));
		}
		try {
			this.httpd.reload();
		} catch (final IOException e) {
			logger.severe(LogUtils.stackTraceToString(e));
		}
		try {
			this.tftp.reload();
		} catch (final IOException e) {
			logger.severe(LogUtils.stackTraceToString(e));
		}

	}

	@Override
	public void reloadConfiguration() {
		// TODO Auto-generated method stub
		reloadAllServices();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DaemonManager [");
		if (dhcp != null) {
			builder.append("dhcp=");
			builder.append(dhcp);
			builder.append(", ");
		}
		if (dns != null) {
			builder.append("dns=");
			builder.append(dns);
			builder.append(", ");
		}
		if (httpd != null) {
			builder.append("httpd=");
			builder.append(httpd);
			builder.append(", ");
		}
		if (tftp != null) {
			builder.append("tftp=");
			builder.append(tftp);
		}
		builder.append("]");
		return builder.toString();
	}

}
