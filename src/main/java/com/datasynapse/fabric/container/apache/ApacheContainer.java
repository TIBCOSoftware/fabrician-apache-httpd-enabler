package com.datasynapse.fabric.container.apache;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import com.datasynapse.commons.util.FileUtils;
import com.datasynapse.commons.util.HostUtils;
import com.datasynapse.fabric.common.ActivationInfo;
import com.datasynapse.fabric.container.ExecContainer;
import com.datasynapse.fabric.container.Feature;
import com.datasynapse.fabric.container.ProcessWrapper;
import com.datasynapse.fabric.domain.featureinfo.HttpFeatureInfo;
import com.datasynapse.fabric.util.FeatureUtils;

/**
 * FabricServer container class implementation.
 */
public class ApacheContainer extends ExecContainer {
	private static final long serialVersionUID = -4671237224306339437L;
	private HttpFeatureInfo _httpFeatureInfo;
	private URL _staturl;

	/**
	 * Constructor.
	 */
	public ApacheContainer() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.container.ExecContainer#doInit(java.util.List)
	 */
	protected void doInit(List additionalVariables) throws Exception {
		super.doInit(additionalVariables);
		// TODO container-specific initialization
	}
	
    /* (non-Javadoc)
     * @see com.datasynapse.fabric.container.ExecContainer#doStart()
     */
    protected void doStart() throws Exception {
    	File dir = new File(getStringVariableValue("DISTRIBUTION_GRIDLIB_DIR"));
        updatePermissions(dir);
        
        // create save the url object to be used by conditions and stat providers
		int port = Integer.parseInt(getStringVariableValue("LISTEN_PORT"));
		String protocal = getStringVariableValue("SERVER_STATUS_PROTOCAL");
		String path = getStringVariableValue("SERVER_STATUS_PATH");
		String query = getStringVariableValue("SERVER_STATUS_QUERY");
		URI uri = new URI(protocal, null, "localhost", port, path, query, null);;
		_staturl = uri.toURL();
		
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
    
	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.container.ExecContainer#doInstall(com.datasynapse.fabric.common.ActivationInfo)
	 */
	protected void doInstall(ActivationInfo info) throws Exception {
		_httpFeatureInfo = (HttpFeatureInfo) FeatureUtils.getFeatureInfo(Feature.HTTP_FEATURE_NAME, this, getCurrentDomain());
    	if (_httpFeatureInfo.isHttpEnabled()) {
    		info.setProperty(ActivationInfo.HTTP_PORT, getStringVariableValue("LISTEN_PORT"));
    	}
    	if (_httpFeatureInfo.isHttpsEnabled()) {
    		info.setProperty(ActivationInfo.HTTPS_PORT, getStringVariableValue("LISTEN_PORT_SSL"));
    	}
        super.doInstall(info);
	}
	
	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.container.ExecContainer#doUninstall()
	 */
	protected void doUninstall() throws Exception {
		super.doUninstall();
		// TODO container-specific uninstallation code
	}
	
	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.container.ExecContainer#doShutdown()
	 */
	protected void doShutdown() throws Exception {
		super.doShutdown();
		String delete = getStringVariableValue("DELETETARGETDIR", "");
   		if (delete.compareToIgnoreCase("true") == 0 ) {
   			FileUtils.deleteDirectory(new File(getTargetConfigDir()));
    	}		    	
	}

    private void updatePermissions(File rootDir) throws IOException, InterruptedException {
        if (!HostUtils.isWindows()) {
            ProcessWrapper perm = new ProcessWrapper(getRuntime(), "chmod -R u+x " + rootDir.getAbsolutePath(), null, rootDir);
            perm.exec();
            perm.waitFor();
        }
    }
	
	// TODO: Depending on what your container's needs are, you might
	// want to override the following additional methods of the
	// ExecContainer superclass: doStart(), getProcess(), confirmShutdown(),
	// exec(), getEnvironment(), and shouldCopyConfigFile().
}
