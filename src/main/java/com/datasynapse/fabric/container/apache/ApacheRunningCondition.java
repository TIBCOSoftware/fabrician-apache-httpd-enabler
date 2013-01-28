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
	
	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.common.RunningCondition#getErrorMessage()
	 */
	public String getErrorMessage() {
		return _errormsg;
	}

	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.common.RunningCondition#getPollPeriod()
	 */
	public long getPollPeriod() {
		return _pollPeriod;		
	}

	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.common.RunningCondition#init(com.datasynapse.fabric.container.Container, com.datasynapse.fabric.domain.Domain, com.datasynapse.fabric.container.ProcessWrapper, com.datasynapse.fabric.common.RuntimeContext)
	 */
	public void init(Container container, Domain domain,
			ProcessWrapper process, RuntimeContext runtimeContext) {
		_container = (ApacheContainer)container;
	}

	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.common.RunningCondition#isRunning()
	 */
	public boolean isRunning() {
		return _container.checkCondition();
	}

	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.common.RunningCondition#setPollPeriod(long)
	 */
	public void setPollPeriod(long pollPeriod) {
		_pollPeriod = pollPeriod;
	}

}
