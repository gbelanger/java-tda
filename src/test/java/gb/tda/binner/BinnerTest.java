package gb.tda.binner;

//import gb.codetda.eventlist.EventList;
//import gb.codetda.montecarlo.WhiteNoiseGenerator;
import java.io.IOException;
import org.apache.log4j.Logger;

/**

   The final class <code>BinnerTest</code> is written to test <code>Binner</code>.

   @author <a href="mailto: guilaume.belanger@esa.int">Guillaume Belanger</a>, ESA/ESAC.
   @created August 2010
   @version June 2015

**/

public class BinnerTest { 

    private static Logger logger = Logger.getLogger(BinnerTest.class);

    public static void main(String[] args) throws Exception  {
	double mean = 5;
	double duration = 40;
	double[] times = WhiteNoiseGenerator.generateArrivalTimes(mean, duration);
	EventList evlist = new EventList(times);
	evlist.writeTimesAsQDP("times.qdp");
	binDataMethodOneTest(times);
	binDataMethodTwoTest(times);
    }


    private static void binDataMethodOneTest(double[] data) throws IOException {
	
	// public static double[][] binData(double[] data, int nbins) {	
	
	int nBins = 5;
	double[][] binnedTimes = Binner.binData(data, nBins);
	double[] binEdges = binnedTimes[1];
	for ( int i=0; i < nBins; i++ ) {
	    double binCentre = (binEdges[2*i+1] + binEdges[2*i])/2;
	    System.out.println(binCentre+"	"+binnedTimes[0][i]);
	}
    }


    private static void binDataMethodTwoTest(double[] data) {

	// public static double[][] binData(double[] data, double[] binEdges) {
	
	int nBins = 5;
	double xmin = data[0];
	double[] binEdges = new double[] {0, 8, 8, 16, 16, 24, 24, 32, 32, 40};
	for ( int i=0; i < binEdges.length; i++ ) {
	    binEdges[i] += xmin;
	}
	double[] binnedTimes = Binner.binData(data, binEdges);
	for ( int i=0; i < nBins; i++ ) {
	    double binCentre = (binnedTimes[2*i] + binnedTimes[2*i+1])/2;
	    System.out.println(binCentre+"	"+binnedTimes[i]);
	}

    }

}
