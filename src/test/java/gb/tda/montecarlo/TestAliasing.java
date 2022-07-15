package gb.esac.test;

import cern.colt.list.DoubleArrayList;
import cern.jet.random.ChiSquare;
import cern.jet.random.Poisson;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.stat.Probability;
import gb.esac.io.DataFileWriter;
import gb.esac.timing.ArrivalTimes;
import gb.esac.timing.AstroEventList;
import gb.esac.timing.FFTPeriodogram;
import gb.esac.timing.LightCurve;
import gb.esac.timing.LightCurveMaker;
import gb.esac.timing.ModifiedRayleighPeriodogram;
import gb.esac.timing.PeriodogramMaker;
import gb.esac.timing.TimingException;
import gb.esac.timing.TimmerKonig;
import gb.esac.tools.Analysis;
import gb.esac.tools.Binner;
import gb.esac.tools.BinningException;
import gb.esac.tools.Stats;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;



/**
 * Describe class <code>TestAliasing</code> here.
 *
 * @author <a href="mailto:guillaume.belanger@esa.int">Guillaume Belanger</a>
 * @version 1.0
 */

public class TestAliasing {

    static Logger logger  = Logger.getLogger(TestAliasing.class);

    /**
     * Describe <code>main</code> method here.
     *
     * @param args a <code>String</code> value
     * @exception TimingException if an error occurs
     * @exception IOException if an error occurs
     * @exception BinningException if an error occurs
     * @exception Exception if an error occurs
     */

    public static void main(String[] args) throws TimingException, IOException, BinningException, Exception   {

	//  logger
	PropertyConfigurator.configure("/Users/gbelanger/javaProgs/gb/esac/logger.config");
	

	// Factories
	LightCurveMaker lcMaker = new LightCurveMaker();
	PeriodogramMaker psdMaker = new PeriodogramMaker();


	double year = 31556952;
	double month = year/12;
	double week = year/52;
	double day = week/7;

	//  Red noise parameters
	double index = 0;
	double duration = 6*month;
	duration = 1e5;
	double mean = 10;
	
	//  Generate arrival times
  	logger.info("Generating event list 1 ...");
 	double dt = 1/(mean);
	//double dt = 1/(2*mean);
	double n = duration/dt;
	double nTK = Math.ceil(Math.log10(n)/Math.log10(2));
	int nTKBins = (int) Math.pow(2, Math.ceil(nTK));
 	double[] t = ArrivalTimes.generateRedArrivalTimes(mean, duration, index, nTKBins);
 	LightCurve lc = lcMaker.makeLightCurve(t, nTKBins);

	//LightCurve lc = TimmerKonig.getTimmerLightCurve(index, duration, nTKBins);
	//lc.scale(mean);


	//  Construct Histo of num of events per bin
// 	double[] rates = lc.getRates();
// 	double[] nEventsPerBin = new double[lc.nBins];
// 	for (int i=0; i < lc.nBins; i++) {
// 	    nEventsPerBin[i] = Math.round(rates[i]*dt);
// 	}
// 	int histoMin = (int) Stats.getMin(nEventsPerBin);
// 	int histoMax = (int)  Stats.getMax(nEventsPerBin);
// 	int nHistoBins = (int) histoMax - histoMin;
// 	double binWidth = (histoMax - histoMin)/nHistoBins;
// 	double binWidthOver2 = binWidth/2.0;
// 	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
// 	Poisson dist = new Poisson((int) mean*dt, engine);
// 	int[] x = new int[nHistoBins];
// 	double[] pdf = new double[nHistoBins];
// 	double sumPDF = 0;
// 	for (int i=0; i < nHistoBins; i++) {
// 	    x[i] = (int) (histoMin + i*binWidth);
// 	    pdf[i] = lc.nBins*binWidth*dist.pdf(x[i]);
// 	}
// 	double[] histo = Binner.binData(nEventsPerBin, histoMin, histoMax, nHistoBins);
// 	String[] header = new String[] {
// 	    "DEV /XS",
// 	    "READ 1 2 3",
// 	    "LW 3", "CS 1.3",
// 	    "LAB FILE", "TIME OFF",
// 	    "LINE STEP ON 2",
// 	    "LINE ON 3",
// 	    "!"
// 	};
// 	DataFileWriter out = new DataFileWriter("nEventsPerBin.qdp");
// 	out.writeData(header, x, histo, pdf);


	FFTPeriodogram psd = null;
	//FFTPeriodogram psdSum = null;
 	FFTPeriodogram psdSum = psdMaker.makeFFTPeriodogram(lc, "leahy");
 	double binTime = lc.getBinWidth();
	//ModifiedRayleighPeriodogram psdSum = psdMaker.makeModifiedRayleighPeriodogram(lc);


	double longDt = 100;
	int lessBins = (int) nTKBins/64;
	t = ArrivalTimes.generateRedArrivalTimes(mean, duration, index, lessBins);
 	LightCurve lc_longDt = lcMaker.makeLightCurve(t, lessBins);
 	double longBinTime = lc_longDt.getBinWidth();

	//LightCurve lc_longDt = TimmerKonig.getTimmerLightCurve(index, duration, (nTKBins/4));
	//lc_longDt.scale(mean);

	FFTPeriodogram psd_longDt = null;
	FFTPeriodogram psdSum_longDt = psdMaker.makeFFTPeriodogram(lc_longDt, "leahy");


// 	//  Introduce gaps
// 	double gapLength = week;
// 	double tLimit = obsTime;
// 	int k=0;
// 	double time = t[k];
// 	double tMax = t[t.length-1];
// 	DoubleArrayList newTimes = new DoubleArrayList();
// 	while (k < t.length && time < tMax) {

// 	    while (time < tLimit) {

// 		newTimes.add(time);
// 		k++;
// 		time = t[k];
// 	    }
// 	    tLimit += gapLength; 
// 	    tLimit = Math.min(tLimit, tMax);

// 	    while (time < tLimit) {

// 		k++;
// 		time = t[k];
// 	    }
// 	    tLimit += obsTime;
// 	    tLimit = Math.min(tLimit, tMax);
// 	}
// 	newTimes.trimToSize();

// 	AstroEventList evlistWithGaps = new AstroEventList(newTimes.elements());
// 	LightCurve lcGaps = lcMaker.makeLightCurve(evlistWithGaps, dt);
// 	FFTPeriodogram psd_gaps = null;
// 	FFTPeriodogram psdSum_gaps = psdMaker.makeFFTPeriodogram(lcGaps, "leahy");


	int i=1;
	int nEvlists = 50;
	while (i < nEvlists) {


	    t = ArrivalTimes.generateRedArrivalTimes(mean, duration, index, nTKBins);
	    lc = lcMaker.makeLightCurve(t, nTKBins);

// 	    lc = TimmerKonig.getTimmerLightCurve(index, duration, nTKBins);
// 	    lc.scale(mean);

	    psd = psdMaker.makeFFTPeriodogram(lc, "leahy");
	    psdSum.addPeriodogram(psd);

	    t = ArrivalTimes.generateRedArrivalTimes(mean, duration, index, lessBins);
	    lc_longDt = lcMaker.makeLightCurve(t, lessBins);


// 	    lc_longDt = TimmerKonig.getTimmerLightCurve(index, duration, (nTKBins/4));
// 	    lc_longDt.scale(mean);

	    psd_longDt = psdMaker.makeFFTPeriodogram(lc_longDt, "leahy");
	    psdSum_longDt.addPeriodogram(psd_longDt);


// 	    //  Introduce gaps of 1e3 s every 1e4 s by ignoring events
// 	    tLimit = obsTime;
// 	    k=0;
// 	    time = t[k];
// 	    tMax = t[t.length-1];
// 	    DoubleArrayList timesWithGaps = new DoubleArrayList();
// 	    while (k < t.length && time < tMax) {

// 		while (time < tLimit) {

// 		    timesWithGaps.add(time);
// 		    k++;
// 		    time = t[k];
// 		}
// 		tLimit += gapLength; 
// 		tLimit = Math.min(tLimit, tMax);

// 		while (time < tLimit) {

// 		    k++;
// 		    time = t[k];
// 		}
// 		tLimit += obsTime;
// 		tLimit = Math.min(tLimit, tMax);
// 	    }
// 	    timesWithGaps.trimToSize();
// 	    evlistWithGaps = new AstroEventList(timesWithGaps.elements());
// 	    lcGaps = lcMaker.makeLightCurve(evlistWithGaps, dt);
// 	    psd_gaps = psdMaker.makeFFTPeriodogram(lcGaps, "variance");
// 	    psdSum_gaps.addPeriodogram(psd_gaps);


	    i++;
	}
	psdSum.scale(1.0/nEvlists);
// 	psdSum_gaps.scale(1.0/nEvlists);
 	psdSum_longDt.scale(1.0/nEvlists);    


	psdSum.writeAsQDP("psd-alpha-"+index+"-mean-"+mean+"-dt-"+binTime+".qdp");
	psdSum_longDt.writeAsQDP("psd-alpha-"+index+"-mean-"+mean+"-dt-"+longDt+".qdp");
	

// 	psdSum_gaps.writeAsQDP("psd-alpha-"+index+"-mean-"+mean+"-binTime-"+binTime+"-gaps.qdp");


    }

}