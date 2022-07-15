package gb.tda.eventlist.src.main.java;


import java.awt.geom.Point2D;
import java.io.IOException;

import gb.tda.eventlist.src.main.java.*;
import gb.esac.timeseries.TimeSeries;
import gb.esac.timeseries.TimeSeriesMaker;

import org.apache.log4j.Logger;


public class EventListTest {

    private static Logger logger  = Logger.getLogger(TestEventList.class.getName());

    public static void main(String[] args) throws Exception {

 	String filename = "flare.lc";
//  	filename = "GNRL-SCWG-GRP-IDX.fits.gz";
//  	filename="empty.dat";
// 	filename="evlist_H_0614+091.fits";
// 	filename = "simEvlist.fits";
 	AstroEventList evlist = new AstroEventList(filename);

	int nbins = 100;
	TimeSeries lc = TimeSeriesMaker.makeTimeSeries(evlist, nbins);
	lc.writeCountsAsQDP("lc.qdp");

// 	logger.info("");
//  	filename = "timeAndEnergy.dat";
//  	evlist = new AstroEventList(filename);

// 	logger.info("");
//  	filename = "timeEnergyXandY.dat";
//  	evlist = new AstroEventList(filename);


// 	double[] t = evlist.getArrivalTimes();
// 	double[] dt = evlist.getInterArrivalTimes();
// 	double[] tInDt = evlist.getArrivalTimesFromTo(evlist.tStart(), evlist.tStop());
// 	double[] tInDE = evlist.getArrivalTimesInEnergyRange(2000, 8000);

// 	logger.info("Arrival times, 1st, 2nd, 3rd and last: "+t[0]+"\t"+t[1]+"\t"+t[2]+"\t"+t[evlist.nEvents()-1]);
// 	logger.info("Inter-arrival times, 1st, 2nd, 3rd and last: "+dt[0]+"\t"+dt[1]+"\t"+dt[2]+"\t"+dt[evlist.nEvents()-2]);
// 	logger.info("Arrival times in time range, 1st, 2nd, 3rd and last: "+tInDt[0]+"\t"+tInDt[1]+"\t"+tInDt[2]+"\t"+tInDt[evlist.nEvents()-1]);
// 	logger.info("Arrival times in energy range, 1st, 2nd, 3rd and last: "+tInDE[0]+"\t"+tInDE[1]+"\t"+tInDE[2]+"\t"+tInDE[tInDE.length-1]);

// 	double[] e = evlist.getEnergies();
// 	logger.info("Energies, 1st, 2nd, 3rd and last: "+e[0]+"\t"+e[1]+"\t"+e[2]+"\t"+e[evlist.nEvents()-1]);

// 	int[] x = evlist.getXCoords();
// 	logger.info("X-Coords, 1st, 2nd, 3rd and last: "+x[0]+"\t"+x[1]+"\t"+x[2]+"\t"+x[evlist.nEvents()-1]);
// 	int[] y = evlist.getYCoords();
// 	logger.info("Y-Coords, 1st, 2nd, 3rd and last: "+y[0]+"\t"+y[1]+"\t"+y[2]+"\t"+y[evlist.nEvents()-1]);
// 	Point2D.Double[] coords = evlist.getCoords();
// 	logger.info("Coords, 1st, 2nd, 3rd and last: \n"+coords[0].getX()+"\t"+coords[0].getY()+
// 		    "\n"+coords[1].getX()+"\t"+coords[1].getY()+
// 		    "\n"+coords[2].getX()+"\t"+coords[2].getY() +
// 		    "\n"+coords[evlist.nEvents()-1].getX()+"\t"+coords[evlist.nEvents()-1].getY());

	
// 	evlist.writeTimesAsQDP("evlist-times.qdp");
// 	evlist.writeEnergiesVsTimeAsQDP("evlist-energies.qdp");
// 	evlist.writeXYCoordsAsQDP("evlist-coords.qdp");

// 	AstroEventList evlist2 = new AstroEventList(evlist.getArrivalTimes());

// 	AstroEventList evlist3 = new AstroEventList(evlist.getArrivalTimes(), evlist.getEnergies());

// 	AstroEventList evlist4 = new AstroEventList(evlist.getArrivalTimes(), evlist.getEnergies(), evlist.getXCoords(), evlist.getYCoords());


    }

}
