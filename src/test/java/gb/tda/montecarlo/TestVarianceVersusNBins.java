package gb.esac.test;

import java.util.Arrays;
import java.util.Vector;

import gb.esac.timing.LightCurve;
import gb.esac.timing.FFTPeriodogram;
import gb.esac.timing.TimingException;
import gb.esac.tools.IllegalArgumentException;
import gb.esac.tools.Stats;
import gb.esac.io.DataFileWriter;
import gb.esac.timing.AstroEventList;
import gb.esac.timing.ArrivalTimes;
import gb.esac.timing.LightCurveMaker;
import gb.esac.timing.PeriodogramMaker;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import cern.colt.list.DoubleArrayList;


public class TestVarianceVersusNBins {

    static Logger logger = Logger.getLogger(TestVarianceVersusNBins.class);

    public static void main(String[] args) throws IOException, TimingException, IllegalArgumentException  {


	PropertyConfigurator.configure("/Users/gbelanger/javaProgs/gb/esac/logger.config");

	int nTests = 14;
	double dt = 2048.0;
	double[] dts = new double[nTests];
	int[] nBins = new int[nTests];
	double[] vars = new double[nTests];
	double[] fracVars = new double[nTests];

	double meanRate = 100;
	double duration = 1e5;
	double alpha = 2;
	double[] t = ArrivalTimes.generateRedArrivalTimes(meanRate, duration, alpha);
	AstroEventList evlist = new AstroEventList(t);
	LightCurveMaker lcMaker = new LightCurveMaker();
	LightCurve lc = null;

	for (int i=0; i < nTests; i++) {
	    
	    lc = lcMaker.makeLightCurve(evlist, dt);
	    dts[i] = dt;
	    nBins[i] = lc.getNBins();
	    vars[i] = lc.getVariance();
	    fracVars[i] = vars[i]/lc.getMean();
	    dt /= 2.0;

	}

	
	DataFileWriter out = new DataFileWriter("varVsBinTime.qdp");
	String[] header = new String[] {
		"DEV /XS",
		"READ 1 2 3",
		"LAB T", "LAB F",
		"TIME OFF",
		"LINE OFF",
		"MA 16 ON",
		"MA SIZE 1.1",
		"LW 3", "CS 1.3",
		"VIEW 0.1 0.1 0.9 0.9",
		"LAB X Number of Bins",
		"LAB Y Variance",
		//"LAB 1 VPOS 0.2 0.8 \"\\gr = "+number.format(correl[0])+" +/- "+number.format(correl[1])+"\"",
		//"LAB 1 JUST LEFT",
		"!"
	};
 	out.writeData(header, nBins, fracVars);


    }


}