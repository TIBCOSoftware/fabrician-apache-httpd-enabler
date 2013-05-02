/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 * 
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code. 
 * In most instances, the license terms are contained in a file named license.txt.
 */

package com.datasynapse.fabric.container.apache;

import com.datasynapse.fabric.stats.BasicStatisticsMetadata;

public class ApacheStatisticsMetadata extends BasicStatisticsMetadata {
    
    private static final long serialVersionUID = -7756848721873821609L;
    private String internalName;
    
    public String getInternalName() {
        return internalName;
    }

    public void setInternalName( String internalName ) {
        this.internalName = internalName;
    }
    
}
