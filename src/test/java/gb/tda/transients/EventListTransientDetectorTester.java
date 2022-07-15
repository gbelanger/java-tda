//package gb.codetda.transients;

//import gb.codetda.eventlist.AstroEventList;
//import gb.codetda.montecarlo.WhiteNoiseGenerator;
//import gb.codetda.montecarlo.RedNoiseGenerator;

public final class EventListTransientDetectorTester {

    public static void main(String[] args) throws Exception {
		double mean = 1;
		double duration = 10000;
		//double[] white = WhiteNoiseGenerator.generateArrivalTimes(mean, duration);
		//AstroEventList evlist = new AstroEventList(white);
		//EventListTransientDetector.detectTransient(evlist);

		double alpha = 2;
		double[] red = RedNoiseGenerator.generateArrivalTimes(mean, duration, alpha);
		AstroEventList evlist2 = new AstroEventList(red);
		EventListTransientDetector.detectTransient(evlist2);
		//EventListTransientDetectorWithGrouping.detectTransient(evlist2);	
    }

}
