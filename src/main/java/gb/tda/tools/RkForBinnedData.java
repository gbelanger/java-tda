package gb.tda.tools;

import gb.tda.eventlist.EventList;
import gb.tda.periodogram.PeriodogramMaker;
import gb.tda.periodogram.ModifiedRayleighPeriodogram;

public class RkForBinnedData {

    public static void main(String[] args) throws Exception {
		String filename = "oversamplingArtefacts_evlist.fits";
		EventList evlist = new EventList(filename);
		double nuMin = 1e-4;
		double nuMax = 1e-2;
		int sampling = 21;
		int harmonic = 1;
		ModifiedRayleighPeriodogram r2_1 = PeriodogramMaker.makeModifiedRayleighPeriodogram(evlist, nuMin, nuMax, sampling, harmonic);
		r2_1.writeAsQDP("r2_1.qdp");
    }

}
