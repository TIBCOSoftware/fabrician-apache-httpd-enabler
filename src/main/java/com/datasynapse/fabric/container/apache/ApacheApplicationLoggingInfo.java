/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 * 
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code. 
 * In most instances, the license terms are contained in a file named license.txt.
 */

package com.datasynapse.fabric.container.apache;

import com.datasynapse.fabric.domain.featureinfo.ApplicationLoggingInfo;

public class ApacheApplicationLoggingInfo extends ApplicationLoggingInfo {    
    
    private static final long serialVersionUID = -2044305724271399489L;
    
    private static final String[] DEFAULT_PATTERNS = { "ap/logs/.*\\.log" };
    
    protected String[] getDefaultPatterns() {
        return DEFAULT_PATTERNS;
    }
}
