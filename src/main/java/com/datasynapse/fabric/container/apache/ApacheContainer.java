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
import java.util.Properties;
import java.util.logging.Logger;

import com.datasynapse.commons.util.FileUtils;
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
public class ApacheContainer extends ExecContainer implements ArchiveManagement, ArchiveProvider {
    private static final long serialVersionUID = -4671237224306339437L;

    private static final String DEFAULT_ARCHIVE_INSTALL_DIR = "FILE_ARCHIVE_DEPLOY_DIRECTORY";

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
        updatePermissions(dir);
        
        // create save the url object to be used by conditions and stat providers
        int port = Integer.parseInt(getStringVariableValue("LISTEN_PORT"));
        String protocal = getStringVariableValue("SERVER_STATUS_PROTOCAL");
        String path = getStringVariableValue("SERVER_STATUS_PATH");
        String query = getStringVariableValue("SERVER_STATUS_QUERY");
        URI uri = new URI(protocal, null, "localhost", port, path, query, null);;
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
            getEngineLogger().warning("Failed to open statistics url. Server probably is not running.");
        }
        return rc;
    }
    
    protected URL getStatUrl() {
        return _staturl;
    }
    
    protected void doInstall(ActivationInfo info) throws Exception {
        Feature feature = getFeature(Feature.HTTP_FEATURE_NAME, this);
        _httpFeatureInfo = (HttpFeatureInfo) getFeatureInfo(feature, getCurrentDomain());
        if (_httpFeatureInfo.isHttpEnabled()) {
            info.setProperty(ActivationInfo.HTTP_PORT, getStringVariableValue("LISTEN_PORT"));
        }
        if (_httpFeatureInfo.isHttpsEnabled()) {
            info.setProperty(ActivationInfo.HTTPS_PORT, getStringVariableValue("LISTEN_PORT_SSL"));
        }
        super.doInstall(info);
    }
    
    protected void doUninstall() throws Exception {
        super.doUninstall();
    }
    
    protected void doShutdown() throws Exception {
        super.doShutdown();
        String delete = getStringVariableValue("DELETETARGETDIR", "");
        if (delete.compareToIgnoreCase("true") == 0 ) {
            File todelete = new File (getStringVariableValue("SERVER_RUNTIME_DIR"));             
            if (!todelete.exists()) {
                return;
            } else {
                todelete.delete();
            }
        }                
    }

    private void updatePermissions(File rootDir) throws IOException, InterruptedException {
        if (!isWindows()) {
            ProcessWrapper perm = new ProcessWrapper(getRuntime(), "chmod -R u+x " + rootDir.getAbsolutePath(), null, rootDir);
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

    private File getStagedDeployDir() {
        try {
            return new File(getStringVariableValue("ENGINE_WORK_DIR"), getArchiveManagementFeatureInfo().getArchiveDirectory());
        } catch (Exception e) {
            return null;
        }
    }
    
    private File getDeployDir() {
        try {
            return new File(getStringVariableValue(DEFAULT_ARCHIVE_INSTALL_DIR));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void archiveDeploy(String archiveName,
            List<ArchiveLocator> archiveLocators, Properties properties)
            throws Exception {
        File archive = ContainerUtils.retrieveAndConfigureArchiveFile(this, archiveName, archiveLocators, properties);
        if (!archive.exists()) {
            throw new Exception("Failed to deploy " + archiveName);
        }
    }

    @Override
    public ArchiveActivationInfo archiveStart(String archiveName, Properties properties) throws Exception {
        File deploymentRoot = getDeployDir();
        File archive = new File(getStagedDeployDir(), archiveName);
        if (!archive.exists()) {
            throw new Exception("Cannot start archive " + archiveName + ", not deployed");
        }
        deploymentRoot.mkdirs();
        // deploy only if not already present in captured Component.
        File deployedFile = new File(deploymentRoot, archiveName);
        if (!deployedFile.exists()) {
            ContainerUtils.copyFile(archive, deployedFile);
        } else if (isActivating()) {
            getEngineLogger().warning("Cannot start archive " + archiveName + " during activation, archive is already running");
        } else {
            throw new Exception("Cannot start archive " + archiveName + ", archive is already running");
        }
            
        return new ArchiveActivationInfo(archiveName, "1");
    }

    @Override
    public ArchiveActivationInfo archiveScaleUp(String archiveName,
            List<ArchiveLocator> archiveLocators) throws Exception {
        getEngineLogger().severe("Archive "+archiveName+" scale-up called which is not supported on this enabler.");
        throw new Exception("Not Implemented");
    }

    @Override
    public void archiveScaleDown(String archiveName, String archiveId)
            throws Exception {
        getEngineLogger().severe("Archive "+archiveName+" scale-down called which is not supported on this enabler.");
        throw new Exception("Not Implemented");
    }

    @Override
    public void archiveStop(String archiveName, String archiveId, Properties properties) throws Exception {
        String nameId = ArchiveUtils.getArchiveNameIdString(archiveName, archiveId);
        
        File file = new File(getDeployDir(), archiveName);
        if (!file.exists()) {
            throw new Exception("Cannot stop archive " + nameId + " because it is not running.");
        }
        FileUtils.delete(file);
        if (file.exists()) {
            throw new Exception("Failed to stop archive " + nameId);
        }        
    }

    @Override
    public void archiveUndeploy(String archiveName, Properties properties)
            throws Exception {

        String forcePropertyName = "FORCE_UNDEPLOY";
        String forceProperty = null;
        if (properties != null) {
            forceProperty = properties.getProperty(forcePropertyName);
        }
        File deployedFile = new File(getDeployDir(), archiveName);
        if (deployedFile.exists()) {
            if (forceProperty == null || !forceProperty.equalsIgnoreCase("true")) {
                throw new Exception("Cannot undeploy archive " + archiveName + " because it is running (use property " + forcePropertyName + "=true to override).");
            } else {
                FileUtils.delete(deployedFile);
            }
        }
        File file = new File(getStagedDeployDir(), archiveName);
        FileUtils.delete(file);
        if (file.exists()) {
            throw new Exception("Failed to undeploy archive " + archiveName);
        }
    }

    @Override
    public ArchiveDetail[] archiveDetect() throws Exception {
        getEngineLogger().severe("Archive detect called which is not supported on this enabler.");
        throw new Exception("Not Implemented");
    }

    @Override
    public String[] urlDetect() throws Exception {
        // TODO Auto-generated method stub
        return new String[] {"apache"};
    }

    @Override
    public File getArchive(String archiveName) throws Exception {
        File archive = new File(getDeployDir(), archiveName);
        if (!archive.exists()) {
            throw new Exception("Unable to provide archive " + archiveName + ", not found in deploy directory");
        }
        return archive;
    }
}
