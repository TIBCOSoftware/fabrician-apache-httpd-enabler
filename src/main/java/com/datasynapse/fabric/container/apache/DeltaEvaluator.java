/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 * 
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code. 
 * In most instances, the license terms are contained in a file named license.txt.
 */

package com.datasynapse.fabric.container.apache;

import java.util.logging.Logger;

import com.datasynapse.commons.beans.AbstractBean;
import com.datasynapse.fabric.stats.Statistic;
import com.datasynapse.fabric.stats.evaluator.Evaluator;

public class DeltaEvaluator extends AbstractBean implements Evaluator {

    private static final long serialVersionUID = 4595295990880109284L;
    private double previousValue = 0;
    
    public Statistic evaluate(Statistic statistic) throws Exception {
        double currentValue = statistic.getValue();
        double valueDifference = currentValue - previousValue;
        
        try {            
            if (valueDifference < 0) {
                Logger.getLogger(getClass().getSimpleName()).warning("Statistic value was reset, so aborting delta calculation");
                throw new Exception("Statistic value was reset, so aborting delta calculation");
            } 
            statistic.setValue(valueDifference);
            return statistic;
        } finally {
            previousValue = currentValue;
        }        
    }
}
