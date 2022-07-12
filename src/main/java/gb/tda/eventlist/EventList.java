package gb.tda.eventlist;

import java.awt.geom.Point2D; 
import java.io.IOException;
import java.util.Arrays;

import gb.tda.io.AsciiDataFileFormatException;
import gb.tda.tools.BasicStats;
import gb.tda.tools.MinMax;

import cern.colt.list.DoubleArrayList;
import org.apache.log4j.Logger;


public class EventList {

    private static Logger logger  = Logger.getLogger(EventList.class.getName());

    private int nEvents;
    private double tStart;
    private double tStop;
    private double duration;
    private double meanRate;
    private double minEventSpacing;
    private double maxEventSpacing;
    private double meanEventSpacing;

    private double[] arrivalTimes;
    private double[] interArrivalTimes;
    private double[] energies;
    private double eMin;
    private double eMax;
    private double eMean;

    private int[] xCoords = null;
    private int[] yCoords = null;
    private Point2D.Double[] coords = null;
    private int xMin;
    private int xMax;
    private double xMean;
    private int yMin;
    private int yMax;
    private double yMean;

    private int[] flags = null;
    private int[] patterns = null;

    private boolean energiesAreSet = false;
    private boolean coordsAreSet = false;
    private boolean flagsAreSet = false;
    private boolean patternsAreSet = false;


    //  Constructors

    private EventList() {}

    public EventList(String filename) throws AsciiDataFileFormatException, EventFileException, EventListException, IOException {
		EventList evlist = EventFileReader.readEventFile(filename);
		setArrivalTimes(evlist.getArrivalTimes());
		if ( evlist.energiesAreSet() ) {
		    setEnergies(evlist.getEnergies());
		}
		if ( evlist.coordsAreSet() ) {
		    setCoords(evlist.getXCoords(), evlist.getYCoords());
		}
		if ( evlist.flagsAreSet() ) {
		    setFlags(evlist.getFlags());
		}
		if ( evlist.patternsAreSet() ) {
		    setPatterns(evlist.getPatterns());
		}
    }

    public EventList(double[] times) throws EventListException {
		setArrivalTimes(times);
		printSummary();
    }

    public EventList(double[] times, double[] energies) throws EventListException {
		if ( times.length != energies.length ) {
		    throw new EventListException("Cannot create EventList(times, energies): Input array lengths are different");
		}
		else {
		    setArrivalTimes(times);
		    setEnergies(energies);
		}
		printSummary();
    }

    public EventList(double[] times, double[] energies, int[] xCoords, int[] yCoords) throws EventListException {
		if ( times.length != energies.length || times.length != xCoords.length || times.length != yCoords.length ) {
		    throw new EventListException("Cannot create EventList(times, energies, xCoords, yCoords): Input array lengths are different");
		}
		else {
		    setArrivalTimes(times);
		    setEnergies(energies);
		    setCoords(xCoords, yCoords);
		}
		printSummary();
    }

    public EventList(double[] times, double[] energies, int[] xCoords, int[] yCoords, int[] flags) throws EventListException {
		if ( times.length != energies.length || times.length != xCoords.length || times.length != yCoords.length || times.length != flags.length ) {
		    throw new EventListException("Cannot create EventList(times, energies, xCoords, yCoords, flags): Input array lengths are different");
		}
		else {
		    setArrivalTimes(times);
		    setEnergies(energies);
		    setCoords(xCoords, yCoords);
		    setFlags(flags);
		}
		printSummary();
    }

    public EventList(double[] times, double[] energies, int[] xCoords, int[] yCoords, int[] flags, int[] patterns) throws EventListException {
		if ( times.length != energies.length || times.length != xCoords.length || times.length != yCoords.length || times.length != flags.length || times.length != patterns.length ) {
		    throw new EventListException("Cannot create EventList(times, energies, xCoords, yCoords, flags, patterns): Input array lengths are different");
		}
		else {
		    setArrivalTimes(times);
		    setEnergies(energies);
		    setCoords(xCoords, yCoords);
		    setFlags(flags);
		    setPatterns(patterns);
		}
		printSummary();
    }
    
    private void setArrivalTimes(double[] times) throws EventListException {
		this.arrivalTimes = new double[times.length];
		this.interArrivalTimes = new double[times.length-1];
		for ( int i=0; i < times.length-1; i++ ) {
		    this.arrivalTimes[i] = times[i];
		    this.interArrivalTimes[i] = times[i+1] - times[i];
	 	    if ( interArrivalTimes[i] < 0 ) {
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

    private void setEnergies(double[] energies) {
		this.energies = new double[energies.length];
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		double sum = 0;
		for ( int i=0; i < energies.length; i++ ) {
		    this.energies[i] = energies[i];
		    min = Math.min(min, this.energies[i]);
		    max = Math.max(max, this.energies[i]);
		    sum += this.energies[i];
		}
		this.eMin = min;
		this.eMax = max;
		this.eMean = sum/this.energies.length;
		this.energiesAreSet = true;
    }

    private void setCoords(int[] xCoords, int[] yCoords) throws EventListException  {
		if ( xCoords.length != yCoords.length ) {
		    throw new EventListException("X and Y coord arrays do not have the same number of elements");
		}
		this.xCoords = new int[xCoords.length];
		this.yCoords = new int[yCoords.length];
		this.coords = new Point2D.Double[xCoords.length];
		int xMin = Integer.MAX_VALUE;
		int xMax = -Integer.MAX_VALUE;
		double sumX = 0;
		int yMin = Integer.MAX_VALUE;
		int yMax = -Integer.MAX_VALUE;
		double sumY = 0;
		for ( int i=0; i < xCoords.length; i++ ) {
		    this.xCoords[i] = xCoords[i];
		    this.yCoords[i] = yCoords[i];
		    this.coords[i] = new Point2D.Double(xCoords[i], yCoords[i]);
		    xMin = Math.min(xMin, this.xCoords[i]);
		    xMax = Math.max(xMax, this.xCoords[i]);
		    sumX += this.xCoords[i];
		    yMin = Math.min(yMin, this.yCoords[i]);
		    yMax = Math.max(yMax, this.yCoords[i]);
		    sumY += this.yCoords[i];
		}
		this.xMin = xMin;
		this.xMax = xMax;
		this.xMean = sumX/this.xCoords.length;
		this.yMin = yMin;
		this.yMax = yMax;
		this.yMean = sumY/this.yCoords.length;
		this.coordsAreSet = true;	
    }

    private void setFlags(int[] flags) {
		this.flags = new int[flags.length];
		for ( int i=0; i < flags.length; i++ ) {
		    this.flags[i] = flags[i];
		}
		this.flagsAreSet = true;
    }

    private void setPatterns(int[] patterns) {
		this.patterns = new int[patterns.length];
		for ( int i=0; i < patterns.length; i++ ) {
		    this.patterns[i] = patterns[i];
		}
		this.patternsAreSet = true;
    }

    private void printSummary() {
		logger.info("Arrival times are set:");
		logger.info("  nEvents = "+this.nEvents);
		logger.info("  tStart = "+this.tStart);
		logger.info("  tStop = "+this.tStop);
		logger.info("  duration = "+this.duration);
		logger.info("  mean rate = "+meanRate);
		logger.info("  minEventSpacing = "+this.minEventSpacing);
		if ( this.minEventSpacing <= 0 ) {
		    logger.warn("  Min event spacing is <= 0. Arrival times might be unsorted.");
		}
		logger.info("  maxEventSpacing = "+this.maxEventSpacing);
		logger.info("  meanEventSpacing = "+this.meanEventSpacing);
		if ( this.energiesAreSet ) {
		    logger.info("Energies are set:");
		    logger.info("  eMin = "+this.eMin);
		    logger.info("  eMax = "+this.eMax);
		    logger.info("  eMean = "+this.eMean);
		}
		if ( this.coordsAreSet ) {
		    logger.info("Coordinates are set:");
		    logger.info("  xMin = "+this.xMin);
		    logger.info("  xMax = "+this.xMax);
		    logger.info("  xMean = "+this.xMean);
		    logger.info("  yMin = "+this.yMin);
		    logger.info("  yMax = "+this.yMax);
		    logger.info("  yMean = "+this.yMean);
		}
		if ( this.flagsAreSet ) {
		    logger.info("Flags are set");
		}
		if ( this.patternsAreSet ) {
		    logger.info("Patterns are set");
		}
		logger.info("EventList is ready");
    }


    //  Public "get" methods

    public int nEvents() { return this.nEvents; }
    public double tStart() { return this.tStart; }
    public double tStop() { return this.tStop; }
    public double duration() { return this.duration; }
    public double meanRate() { return this.meanRate; }
    public double minEventSpacing() { return this.minEventSpacing; }
    public double maxEventSpacing() { return this.maxEventSpacing; }
    public double meanEventSpacing() { return this.meanEventSpacing; }

    public double eMax() throws EventListException {
		if ( energiesAreSet )
		    return this.eMax;
		else
		    throw new EventListException("Energies are not defined");
    }

    public double eMin() throws EventListException {
		if ( energiesAreSet )
		    return this.eMin;
		else
		    throw new EventListException("Energies are not defined");
    }

    public double eMean() throws EventListException {
		if ( energiesAreSet )
		    return this.eMean;
		else
		    throw new EventListException("Energies are not defined");
    }

    public double[] getArrivalTimes() { return Arrays.copyOf(this.arrivalTimes, this.arrivalTimes.length); }
    public double[] getInterArrivalTimes() { return Arrays.copyOf(this.interArrivalTimes, this.interArrivalTimes.length); }

    public double[] getEnergies() throws EventListException {
		if ( energiesAreSet )
		    return Arrays.copyOf(this.energies, this.energies.length);
		else
		    throw new EventListException("Energies are not defined");
    }

    public int[] getXCoords() throws EventListException {
		if ( coordsAreSet )
		    return Arrays.copyOf(this.xCoords, this.xCoords.length);
		else
		    throw new EventListException("Coordinates are not defined");
    }

    public int[] getYCoords() throws EventListException {
		if ( coordsAreSet )
		    return Arrays.copyOf(this.yCoords, this.yCoords.length);
		else
		    throw new EventListException("Coordinates are not defined");
    }

    public Point2D.Double[] getCoords() throws EventListException {
		if ( coordsAreSet )
		    return Arrays.copyOf(this.coords, this.coords.length);
		else
		    throw new EventListException("Coordinates are not defined");
    }

    public int[] getFlags() throws EventListException {
		if ( flagsAreSet ) 
		    return Arrays.copyOf(this.flags, this.flags.length);
		else
		    throw new EventListException("Flags are not defined");
    }

    public int[] getPatterns() throws EventListException {
		if ( patternsAreSet ) 
		    return Arrays.copyOf(this.patterns, this.patterns.length);
		else
		    throw new EventListException("Patterns are not defined");
    }


    //  Boolean checkers

    public boolean energiesAreSet() {
		return this.energiesAreSet;
    }

    public boolean coordsAreSet() {
		return this.coordsAreSet;
    }

    public boolean flagsAreSet() {
		return this.flagsAreSet;
    }

    public boolean patternsAreSet() {
		return this.patternsAreSet;
    }


    //  Write methods

    public void writeTimesAsQDP(String filename) throws IOException {
		EventListWriter.writeTimesAsQDP(this, filename);
    }

    public void writeEnergiesVsTimeAsQDP(String filename) throws IOException, EventListException  {
		EventListWriter.writeEnergiesVsTimeAsQDP(this, filename);
    }

    public void writeXYCoordsAsQDP(String filename) throws IOException, EventListException {
		EventListWriter.writeXYCoordsAsQDP(this,filename);
    }

    
}
