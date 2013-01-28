package com.datasynapse.fabric.container.apache;

import com.datasynapse.fabric.domain.featureinfo.ApplicationLoggingInfo;

public class ApacheApplicationLoggingInfo extends ApplicationLoggingInfo {	
	private static final long serialVersionUID = -2044305724271399489L;
	public static final String[] DEFAULT_PATTERNS = { "ap/logs/.*\\.log" };
	
    protected String[] getDefaultPatterns() {
        return DEFAULT_PATTERNS;
    }
}
