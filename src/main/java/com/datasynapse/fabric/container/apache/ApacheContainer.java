/*
 * Copyright (c) 2013-2015 TIBCO Software Inc. All Rights Reserved.
 * 
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code. 
 * In most instances, the license terms are contained in a file named license.txt.
 */

package com.datasynapse.fabric.container.apache;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

import com.datasynapse.fabric.common.ActivationInfo;
import com.datasynapse.fabric.common.ArchiveActivationInfo;
import com.datasynapse.fabric.common.message.ArchiveLocator;
import com.datasynapse.fabric.container.ArchiveDetail;
import com.datasynapse.fabric.container.ArchiveManagement;
import com.datasynapse.fabric.container.ArchiveProvider;
import com.datasynapse.fabric.container.Container;
import com.datasynapse.fabric.container.ExecContainer;
import com.datasynapse.fabric.container.Feature;
import com.datasynapse.fabric.container.ProcessWrapper;
import com.datasynapse.fabric.domain.Domain;
import com.datasynapse.fabric.domain.featureinfo.FeatureInfo;
import com.datasynapse.fabric.domain.featureinfo.HttpFeatureInfo;
import com.datasynapse.fabric.util.ArchiveUtils;
import com.datasynapse.fabric.util.ContainerUtils;

/**
 * FabricServer apache httpd enabler implementation.
 */
public class ApacheContainer extends ExecContainer implements
		ArchiveManagement, ArchiveProvider {
	private static final long serialVersionUID = -4671237224306339437L;

	private static final String DEFAULT_ARCHIVE_INSTALL_DIR = "FILE_ARCHIVE_DEPLOY_DIRECTORY";

	private static final String FORCE_UNDEPLOY = "FORCE_UNDEPLOY";

	private HttpFeatureInfo _httpFeatureInfo;
	private URL _staturl;

	private transient java.util.logging.Logger engineLogger;

	public ApacheContainer() {
		super();
		engineLogger = Logger.getLogger(getClass().getSimpleName());
	}

	protected void doInit(List additionalVariables) throws Exception {
		super.doInit(additionalVariables);
	}

	protected void doStart() throws Exception {
		File dir = new File(getStringVariableValue("SERVER_RUNTIME_DIR"));
		// updatePermissions(dir);

		// create save the url object to be used by conditions and stat
		// providers
		int port = Integer.parseInt(getStringVariableValue("LISTEN_PORT"));
		String protocal = getStringVariableValue("SERVER_STATUS_PROTOCAL");
		String path = getStringVariableValue("SERVER_STATUS_PATH");
		String query = getStringVariableValue("SERVER_STATUS_QUERY");
		URI uri = new URI(protocal, null, "localhost", port, path, query, null);
		_staturl = uri.toURL();
		engineLogger.fine("Statistics URL: " + _staturl);

		super.doStart();
	}

	protected boolean checkCondition() {
		boolean rc = false;
		try {
			_staturl.openStream();
			rc = true;
		} catch (IOException e) {
			rc = false;
			getEngineLogger()
					.warning(
							"Failed to open statistics url. Server probably is not running.");
		}
		return rc;
	}

	protected URL getStatUrl() {
		return _staturl;
	}

	protected void doInstall(ActivationInfo info) throws Exception {
		Feature feature = getFeature(Feature.HTTP_FEATURE_NAME, this);
		_httpFeatureInfo = (HttpFeatureInfo) getFeatureInfo(feature,
				getCurrentDomain());

		if (_httpFeatureInfo.isHttpEnabled()) {
			info.setProperty(ActivationInfo.HTTP_PORT,
					getStringVariableValue("LISTEN_PORT"));
		}
		if (_httpFeatureInfo.isHttpsEnabled()) {
			info.setProperty(ActivationInfo.HTTPS_PORT,
					getStringVariableValue("LISTEN_PORT_SSL"));
		}

		super.doInstall(info);
	}

	protected void doUninstall() throws Exception {
		super.doUninstall();
	}

	protected void doShutdown() throws Exception {
		super.doShutdown();
		String delete = getStringVariableValue("DELETETARGETDIR", "");
		if (delete.compareToIgnoreCase("true") == 0) {
			File todelete = new File(
					getStringVariableValue("SERVER_RUNTIME_DIR"));
			if (!todelete.exists()) {
				return;
			} else {
				todelete.delete();
			}
		}
	}

	private void updatePermissions(File rootDir) throws IOException,
			InterruptedException {
		if (!isWindows()) {
			ProcessWrapper perm = new ProcessWrapper(getRuntime(),
					"chmod -R u+x " + rootDir.getAbsolutePath(), null, rootDir);
			perm.exec();
			perm.waitFor();
		}
	}

	private static Feature getFeature(String featureName, Container container) {
		if (container != null) {
			for (int i = 0; i < container.getSupportedFeatureCount(); i++) {
				Feature feature = container.getSupportedFeature(i);
				if (feature.getName().equalsIgnoreCase(featureName)) {
					return feature;
				}
			}
		}
		return null;
	}

	private static FeatureInfo getFeatureInfo(Feature feature, Domain domain) {
		String infoClassName = feature.getInfoClass();
		for (int i = 0; i < domain.getFeatureInfoCount(); i++) {
			FeatureInfo info = domain.getFeatureInfo(i);
			if (info.getClass().getName().equals(infoClassName)) {
				return info;
			}
		}
		return null;
	}

	private static boolean isWindows() {
		return System.getProperty("os.name").indexOf("Window") != -1;
	}

	private File getArchiveDeployDir() {
		try {
			return new File(getStringVariableValue("ENGINE_WORK_DIR"),
					getArchiveManagementFeatureInfo().getArchiveDirectory());
		} catch (Exception e) {
			return null;
		}
	}

	private File getArchiveInstallDir() {
		try {
			return new File(getStringVariableValue(DEFAULT_ARCHIVE_INSTALL_DIR));
		} catch (Exception e) {
			return null;
		}
	}

	private File getExpandedArchive(String archiveName) {
		return new File(getArchiveDeployDir(),
				getSimpleArchiveName(archiveName));
	}

	@Override
	public void archiveDeploy(String archiveName,
			List<ArchiveLocator> archiveLocators, Properties properties)
			throws Exception {

		engineLogger.info("Enter archiveDeploy:" + archiveName);

		// check if the archive is running
		File expandedArchive = getExpandedArchive(archiveName);

		if (expandedArchive.exists()) {
			archiveUndeploy(archiveName, properties);
		}

		File archive = ContainerUtils.retrieveAndConfigureArchiveFile(this,
				archiveName, archiveLocators, properties);
		if (!archive.exists()) {
			throw new Exception("Failed to deploy " + archiveName);
		}

		engineLogger.info("Exit archiveDeploy:" + archiveName);
	}

	@Override
	public ArchiveActivationInfo archiveStart(String archiveName,
			Properties properties) throws Exception {

		engineLogger.info("Enter archiveStart:" + archiveName);

		File expandedArchive = getExpandedArchive(archiveName);

		if (!expandedArchive.isDirectory()) {
			File archive = new File(getArchiveDeployDir(), archiveName);
			if (!archive.exists()) {
				throw new Exception("Cannot start archive " + archiveName
						+ ", not deployed");
			}

			expandedArchive.mkdirs();
			ArchiveUtils.extractFileTo(archive, expandedArchive);

			File archiveDir = getArchiveInstallDir();
			if (!archiveDir.exists()) {
				archiveDir.mkdirs();
			}
			ArchiveUtils.extractFileTo(archive, archiveDir);
		}
		ArchiveActivationInfo info = new ArchiveActivationInfo(archiveName, "1");

		engineLogger.info("Exit archiveStart:" + archiveName);

		return info;
	}

	@Override
	public ArchiveActivationInfo archiveScaleUp(String archiveName,
			List<ArchiveLocator> archiveLocators) throws Exception {
		getEngineLogger()
				.severe("Archive "
						+ archiveName
						+ " scale-up called which is not supported on this enabler.");
		throw new Exception("Not Implemented");
	}

	@Override
	public void archiveScaleDown(String archiveName, String archiveId)
			throws Exception {
		getEngineLogger()
				.severe("Archive "
						+ archiveName
						+ " scale-down called which is not supported on this enabler.");
		throw new Exception("Not Implemented");
	}

	private String getSimpleArchiveName(String archiveName) {
		String simpleArchiveName = archiveName;

		int index = archiveName.indexOf(".");
		if (index > 0)
			simpleArchiveName = archiveName.substring(0, index);
		return simpleArchiveName;
	}

	@Override
	public void archiveStop(String archiveName, String archiveId,
			Properties properties) throws Exception {

		engineLogger.info("Enter archiveStop:" + archiveName);
		File expandedArchive = getExpandedArchive(archiveName);

		if (expandedArchive.isDirectory()) {
			File[] archiveFiles = ContainerUtils.listFiles(expandedArchive, null);
			File installDir = getArchiveInstallDir();

			if (installDir.isDirectory()) {
				String expandedArchivePath = expandedArchive.getAbsolutePath();
				String installPath = installDir.getAbsolutePath();

				for (File file : archiveFiles) {
					String path = file.getAbsolutePath();
					path = path.replace(expandedArchivePath, installPath);
					File installedFile = new File(path);
					if (installedFile.isFile()) {
						ContainerUtils.deleteFile(installedFile);
					} else if (installedFile.isDirectory()) {
						ContainerUtils.deleteDirectory(installedFile);
					}
				}
			}

			ContainerUtils.deleteDirectory(expandedArchive);
		} else {
			engineLogger.warning("Nothing to stop; Archive is not running:"
					+ archiveName);
		}
		engineLogger.info("Exit archiveStop:" + archiveName);
	}

	@Override
	public void archiveUndeploy(String archiveName, Properties properties)
			throws Exception {

		engineLogger.info("Enter archiveUndeploy:" + archiveName);

		File expandedArchive = getExpandedArchive(archiveName);
		if (expandedArchive.exists()) {
			String forceUndeploy = null;
			if (properties != null) {
				forceUndeploy = properties.getProperty(FORCE_UNDEPLOY);
			}

			if (!Boolean.parseBoolean(forceUndeploy)) {
				throw new Exception("Cannot undeploy archive " + archiveName
						+ " because it is running (use property "
						+ FORCE_UNDEPLOY + "=true to override).");
			} else {
				engineLogger
						.warning("FORCE_UNDEPLOY=true, so stopping running archive:"
								+ archiveName);
				archiveStop(archiveName, "1", properties);
			}
		}

		File file = new File(getArchiveDeployDir(), archiveName);
		ContainerUtils.deleteFile(file);

		engineLogger.info("Exit archiveUndeploy:" + archiveName);
	}

	private ArrayList<File> getRunningArchives() {
		ArrayList<File> archiveList = new ArrayList<File>(2);

		File folder = getArchiveDeployDir();
		if (folder.isDirectory()) {
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles != null) {
				for (File file : listOfFiles) {
					if (file.isFile()) {
						File dir = new File(folder,
								getSimpleArchiveName(file.getName()));
						if (dir.isDirectory())
							archiveList.add(file);
					}
				}
			}
		}

		return archiveList;
	}

	@Override
	public ArchiveDetail[] archiveDetect() throws Exception {
		ArchiveDetail[] archiveDetail = null;

		ArrayList<File> archiveList = getRunningArchives();

		if (archiveList.size() > 0) {
			archiveDetail = new ArchiveDetail[archiveList.size()];
			for (int i = 0; i < archiveList.size(); i++) {
				archiveDetail[i] = new ArchiveDetail(archiveList.get(i)
						.getName(), true, false, "1");
			}
		}

		return archiveDetail;
	}

	@Override
	public String[] urlDetect() throws Exception {
		return new String[] {};
	}

	@Override
	public File getArchive(String archiveName) throws Exception {
		File archive = new File(getArchiveDeployDir(), archiveName);
		if (!archive.exists()) {
			throw new Exception("Unable to provide archive " + archiveName
					+ ", not found in deploy directory");
		}
		return archive;
	}
}
