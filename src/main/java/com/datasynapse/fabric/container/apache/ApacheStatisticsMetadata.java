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
