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
//	extended status returned by Apache, this is for reference
//	private static final String TOTAL_ACCESSES = "Total Accesses";
	private static final String TOTAL_KBYTES = "Total kBytes";
    private static final String TOTAL_BYTES = "Total Bytes";
//	private static final String UPTIME = "Uptime";
//	private static final String REQ_PER_SEC = "ReqPerSec";
//	private static final String BYTES_PER_SEC = "BytesPerSec";
//	private static final String BYTES_PER_REQ = "BytesPerReq";
	
	private static final String BUSY_WORKERS = "BusyWorkers";
	private static final String IDLE_WORKERS = "IdleWorkers";	
	private static final String SCOREBOARD = "Scoreboard";
	// based on BUSY_WORKERS and IDEL_WORKERS
	private static final String IDLE_PERCENT = "IdleWorkersPercentage";
	private static final String BUSY_PERCENT = "BusyWorkersPercentage";
	
	private Map stats;
	private URL url;
	private Logger logger;
	
	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.stats.provider.AbstractStatisticsProvider#getStatistic(com.datasynapse.fabric.stats.StatisticsMetadata)
	 */
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

	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.stats.provider.AbstractStatisticsProvider#init(com.datasynapse.fabric.container.Container, com.datasynapse.fabric.domain.Domain, com.datasynapse.fabric.container.ProcessWrapper, com.datasynapse.fabric.common.RuntimeContext)
	 */
	public void init(Container container, Domain domain, ProcessWrapper process, 
			RuntimeContext context) {
		if (logger == null)
			logger = this.getLogger();
		
		url = ((ApacheContainer)container).getStatUrl();
	}

	/* (non-Javadoc)
	 * @see com.datasynapse.fabric.stats.provider.AbstractStatisticsProvider#getStatistics(java.lang.Object)
	 */
	public Statistic[] getStatistics(Object key) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));

			String line;
			String[] values;
			stats = new HashMap(this.getSupportedStatisticCount()+2);

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
