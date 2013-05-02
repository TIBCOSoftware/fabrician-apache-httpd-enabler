/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 * 
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code. 
 * In most instances, the license terms are contained in a file named license.txt.
 */

package com.datasynapse.fabric.container.apache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.datasynapse.fabric.common.RuntimeContext;
import com.datasynapse.fabric.container.Container;
import com.datasynapse.fabric.container.ProcessWrapper;
import com.datasynapse.fabric.domain.Domain;
import com.datasynapse.fabric.stats.DefaultStatistic;
import com.datasynapse.fabric.stats.Statistic;
import com.datasynapse.fabric.stats.StatisticsMetadata;
import com.datasynapse.fabric.stats.provider.AbstractStatisticsProvider;

public class ApacheStatisticsProvider extends AbstractStatisticsProvider {
    
    private static final String TOTAL_KBYTES = "Total kBytes";
    private static final String TOTAL_BYTES = "Total Bytes";    
    private static final String BUSY_WORKERS = "BusyWorkers";
    private static final String IDLE_WORKERS = "IdleWorkers";    
    private static final String SCOREBOARD = "Scoreboard";
    private static final String IDLE_PERCENT = "IdleWorkersPercentage";
    private static final String BUSY_PERCENT = "BusyWorkersPercentage";
    
    private Map<String, Double> stats;
    private URL url;
    private Logger logger;
    
    public Statistic getStatistic(StatisticsMetadata statistic) {
        
        String name = statistic.getName();
        String internalName = ((ApacheStatisticsMetadata) statistic).getInternalName();
        Double value = (Double)stats.get(internalName);

        if (value == null)
            return null;
        else {
            double v = value.doubleValue();
            return new DefaultStatistic(name, v);
        }
    }

    public void init(Container container, Domain domain, ProcessWrapper process, 
            RuntimeContext context) {
        if (logger == null)
            logger = this.getLogger();
        
        url = ((ApacheContainer)container).getStatUrl();
    }

    public Statistic[] getStatistics(Object key) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            String[] values;
            stats = new HashMap<String, Double>(this.getSupportedStatisticCount()+2);

            while ((line = in.readLine()) != null) {
                if (line.indexOf(SCOREBOARD) == -1) { // skip scoreboard line
                    values = line.split(":");                
                    if (values.length == 2)
                        stats.put(values[0].trim(), new Double(values[1].trim()));
                }
            }
            // add additional calculated values
            int busyworkers = ((Double)stats.get(BUSY_WORKERS)).intValue();
            int idleworkers = ((Double)stats.get(IDLE_WORKERS)).intValue();
            double kbytes = ((Double)stats.get(TOTAL_KBYTES)).doubleValue();
            int totalworkers = busyworkers + idleworkers;
            stats.put(IDLE_PERCENT, new Double(idleworkers*100/totalworkers));
            stats.put(BUSY_PERCENT, new Double(busyworkers*100/totalworkers));
            stats.put(TOTAL_BYTES, new Double(kbytes * 1024));
        } catch (IOException e) {
            logger.warning("Failed to read server status");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.getStatistics(key);
    }
}
