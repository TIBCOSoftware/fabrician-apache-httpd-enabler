package com.datasynapse.fabric.container.apache;

import com.datasynapse.fabric.common.RuntimeContext;
import com.datasynapse.fabric.common.StartCondition;
import com.datasynapse.fabric.container.Container;
import com.datasynapse.fabric.container.ProcessWrapper;
import com.datasynapse.fabric.domain.Domain;

public class ApacheStartCondition implements StartCondition {
	private ApacheContainer _container;
	private long _pollPeriod;
	
	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.common.StartCondition#getPollPeriod()
	 */
	public long getPollPeriod() {
		return _pollPeriod;
	}

	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.common.StartCondition#hasStarted()
	 */
	public boolean hasStarted() throws Exception {
		return _container.checkCondition();
	}

	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.common.StartCondition#init(com.datasynapse.fabric.container.Container, com.datasynapse.fabric.domain.Domain, com.datasynapse.fabric.container.ProcessWrapper, com.datasynapse.fabric.common.RuntimeContext)
	 */
	public void init(Container container, Domain domain, ProcessWrapper process, RuntimeContext runtimeContext) {
		_container = (ApacheContainer)container;
	}

	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.common.StartCondition#setPollPeriod(long)
	 */
	public void setPollPeriod(long pollPeriod) {
		_pollPeriod = pollPeriod;
	}

}
