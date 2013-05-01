/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 * 
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code. 
 * In most instances, the license terms are contained in a file named license.txt.
 */

package com.datasynapse.fabric.container.apache;

import com.datasynapse.fabric.common.RunningCondition;
import com.datasynapse.fabric.common.RuntimeContext;
import com.datasynapse.fabric.container.Container;
import com.datasynapse.fabric.container.ProcessWrapper;
import com.datasynapse.fabric.domain.Domain;

public class ApacheRunningCondition implements RunningCondition {
    
	private String _errormsg;
    private long _pollPeriod;
    private ApacheContainer _container;
	
	public String getErrorMessage() {
		return _errormsg;
	}

	public long getPollPeriod() {
		return _pollPeriod;		
	}

	public void init(Container container, Domain domain,
			ProcessWrapper process, RuntimeContext runtimeContext) {
		_container = (ApacheContainer)container;
	}

	public boolean isRunning() {
		return _container.checkCondition();
	}

	public void setPollPeriod(long pollPeriod) {
		_pollPeriod = pollPeriod;
	}

}
