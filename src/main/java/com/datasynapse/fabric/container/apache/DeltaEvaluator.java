package com.datasynapse.fabric.container.apache;

import com.datasynapse.commons.beans.AbstractBean;
import com.datasynapse.fabric.common.plugins.FabricLogRepository;
import com.datasynapse.fabric.stats.Statistic;
import com.datasynapse.fabric.stats.evaluator.Evaluator;

public class DeltaEvaluator extends AbstractBean implements Evaluator {
    private static final long serialVersionUID = 4595295990880109284L;
    private double previousValue = 0;
    
    /* (non-Javadoc)
     * @see com.datasynapse.fabric.stats.evaluator.Evaluator#evaluate(com.datasynapse.fabric.stats.Statistic)
     */
    public Statistic evaluate(Statistic statistic) throws Exception {
        double currentValue = statistic.getValue();
        double valueDifference = currentValue - previousValue;
        
        try {            
            if (valueDifference < 0) {
                FabricLogRepository.STATISTICS.warning("Statistic value was reset, so aborting delta calculation");
                throw new Exception("Statistic value was reset, so aborting delta calculation");
            } 
            statistic.setValue(valueDifference);
            return statistic;
        } finally {
            previousValue = currentValue;
        }        
    }
}
