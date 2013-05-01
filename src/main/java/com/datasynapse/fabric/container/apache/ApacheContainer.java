/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
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

import java.util.logging.Logger;

import com.datasynapse.fabric.common.ActivationInfo;
import com.datasynapse.fabric.container.Container;
import com.datasynapse.fabric.container.ExecContainer;
import com.datasynapse.fabric.container.Feature;
import com.datasynapse.fabric.container.ProcessWrapper;
import com.datasynapse.fabric.domain.Domain;
import com.datasynapse.fabric.domain.featureinfo.FeatureInfo;
import com.datasynapse.fabric.domain.featureinfo.HttpFeatureInfo;

/**
 * FabricServer apache httpd enabler implementation.
 */
public class ApacheContainer extends ExecContainer {
	private static final long serialVersionUID = -4671237224306339437L;
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
    	//File dir = new File(getStringVariableValue("DISTRIBUTION_GRIDLIB_DIR"));
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
	
    public static Feature getFeature(String featureName, Container container) {
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
    
    public static FeatureInfo getFeatureInfo(Feature feature, Domain domain) {
        String infoClassName = feature.getInfoClass();
        for (int i = 0; i < domain.getFeatureInfoCount(); i++) {
            FeatureInfo info = domain.getFeatureInfo(i);
            if (info.getClass().getName().equals(infoClassName)) {
                return info;
            }
        }
        return null;
    }
    
    public static boolean isWindows() {
        return System.getProperty("os.name").indexOf("Window") != -1;        
    }


}
