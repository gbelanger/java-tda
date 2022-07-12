package gb.tda.montecarlo;

import gb.tda.binner.BinningUtils;
import gb.tda.io.AsciiDataFileWriter;
import gb.tda.periodogram.FFTPeriodogram;
import gb.tda.periodogram.PeriodogramMaker;
import gb.tda.periodogram.PeriodogramUtils;

public class TestAutoRegressionGenerator {

    public static void main(String[] args) throws Exception{

	double tau = 2;
	double alpha = 1;
	if ( args.length == 2 ) {
	    tau = (Double.valueOf(args[0])).doubleValue();
	    alpha = (Double.valueOf(args[1])).doubleValue();
	}
	double duration = 1e5;
	int nTimeBins = 16384;
	double binTime = duration/nTimeBins;
	double[] times = BinningUtils.getBinCentres(0, duration, nTimeBins);
	double[] ar = AutoRegressionGenerator.generateAR1Process(times, tau);
// 	double[] counts = new double[nTimeBins];
// 	for ( int i=0; i < nTimeBins; i++ ) {
// 	    counts[i] = Math.round(ar[i]*binTime);
// 	}
// 	double[] binEdges = BinninUtils.getBinEdges(0, duration, nTimeBins);
// 	TimeSeries ar_ts = TimeSeriesMaker.makeTimeSeries(binEdges, counts);
	AsciiDataFileWriter out = new AsciiDataFileWriter("ar1.qdp");
	String[] head = new String[] {
	    "DEV /XS"	    
	};
	out.writeData(head, times, ar);
	FFTPeriodogram ar_fft = PeriodogramMaker.makeUnnormalizedWindowedFFTPeriodogram(ar, duration, "rectangular", 1);
	ar_fft.writeAsQDP("ar_fft.qdp");
	double[] fitRes = PeriodogramUtils.fitPowerLawInLogSpace(ar_fft);
	System.out.println(fitRes[0]+"	"+fitRes[2]);

	double mean = 1; 
	double[] tk = TimmerKonig.getRates(mean, duration, alpha, nTimeBins);
	out = new AsciiDataFileWriter("tk.qdp");
	out.writeData(head, times, tk);
	FFTPeriodogram tk_fft = PeriodogramMaker.makeUnnormalizedWindowedFFTPeriodogram(tk, duration, "rectangular", 1);
	tk_fft.writeAsQDP("tk_fft.qdp");
	for ( int i=0; i < tk.length; i++ ) {
	    tk[i] *= 10;
	}
	tk_fft = PeriodogramMaker.makeUnnormalizedWindowedFFTPeriodogram(tk, duration, "rectangular", 1);
	tk_fft.writeAsQDP("tk2_fft.qdp");


    }


}
