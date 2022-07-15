package gb.tda.eventlist;

import java.awt.geom.Point2D; 
import java.io.IOException;
import java.util.Arrays;
import org.apache.log4j.Logger;
import gb.tda.io.AsciiDataFileFormatException;
import gb.tda.tools.BasicStats;
import gb.tda.tools.MinMax;

public class BasicEventList implements IEventList {

    private static Logger logger  = Logger.getLogger(BasicEventList.class.getName());

    int nEvents;
    double tStart;
    double tStop;
    double duration;
    double meanRate;
    double minEventSpacing;
    double maxEventSpacing;
    double meanEventSpacing;
    double[] arrivalTimes;
    double[] interArrivalTimes;

    //  Constructors

    private BasicEventList() {}

    public BasicEventList(String filename) throws AsciiDataFileFormatException, EventFileException, EventListException, IOException {
		IEventList evlist = EventFileReader.read(filename);
		setArrivalTimes(evlist.getArrivalTimes());
		printSummary();
    }

    public BasicEventList(double[] times) throws EventListException {
		setArrivalTimes(times);
		printSummary();
    }

	// Private setters

    private void setArrivalTimes(double[] times) throws EventListException {
		this.arrivalTimes = new double[times.length];
		this.interArrivalTimes = new double[times.length-1];
		for (int i=0; i < times.length-1; i++) {
		    this.arrivalTimes[i] = times[i];
		    this.interArrivalTimes[i] = times[i+1] - times[i];
	 	    if (interArrivalTimes[i] < 0) {
	 		logger.warn("Negative inter-arrival time i="+i+" times[i+1]="+times[i+1]+" times[i]="+times[i]+" dt="+interArrivalTimes[i]);
	 	    }
		}
		this.arrivalTimes[times.length-1] = times[times.length-1];
		this.nEvents = this.arrivalTimes.length;
		this.tStart = this.arrivalTimes[0];
		this.tStop = this.arrivalTimes[nEvents-1];
		this.duration = this.tStop - this.tStart;
		this.meanRate = this.nEvents/this.duration;
		this.minEventSpacing = MinMax.getNonZeroMin(this.interArrivalTimes);
		this.maxEventSpacing = MinMax.getMax(this.interArrivalTimes);
		this.meanEventSpacing = BasicStats.getMean(this.interArrivalTimes);
    }

    private void printSummary() {
		logger.info("Arrival times are set:");
		logger.info("  nEvents = "+this.nEvents);
		logger.info("  tStart = "+this.tStart);
		logger.info("  tStop = "+this.tStop);
		logger.info("  duration = "+this.duration);
		logger.info("  mean rate = "+meanRate);
		logger.info("  minEventSpacing = "+this.minEventSpacing);
		if (this.minEventSpacing <= 0) {
		    logger.warn("  Min event spacing is <= 0. Arrival times might be unsorted.");
		}
		logger.info("  maxEventSpacing = "+this.maxEventSpacing);
		logger.info("  meanEventSpacing = "+this.meanEventSpacing);
		logger.info("BasicEventList is ready");
    }


    //  Public getters

    public int nEvents() { return this.nEvents; }
    public double tStart() { return this.tStart; }
    public double tStop() { return this.tStop; }
    public double duration() { return this.duration; }
    public double meanRate() { return this.meanRate; }
    public double minEventSpacing() { return this.minEventSpacing; }
    public double maxEventSpacing() { return this.maxEventSpacing; }
    public double meanEventSpacing() { return this.meanEventSpacing; }
    public double[] getArrivalTimes() { return Arrays.copyOf(this.arrivalTimes, this.arrivalTimes.length); }
    public double[] getInterArrivalTimes() { return Arrays.copyOf(this.interArrivalTimes, this.interArrivalTimes.length); }

    //  Write methods

    public void writeAsQDP(String filename) throws IOException {
		EventListWriter.writeAsQDP(this, filename);
    }

}
