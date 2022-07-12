//package gb.codetda.transients;

//import gb.codetda.eventlist.EventList;
//import gb.codetda.montecarlo.WhiteNoiseGenerator;
//import gb.codetda.montecarlo.RedNoiseGenerator;

public final class EventListTransientDetectorTester {

    public static void main(String[] args) throws Exception {
		double mean = 1;
		double duration = 10000;
		//double[] white = WhiteNoiseGenerator.generateArrivalTimes(mean, duration);
		//EventList evlist = new EventList(white);
		//EventListTransientDetector.detectTransient(evlist);

		double alpha = 2;
		double[] red = RedNoiseGenerator.generateArrivalTimes(mean, duration, alpha);
		EventList evlist2 = new EventList(red);
		EventListTransientDetector.detectTransient(evlist2);
		//EventListTransientDetectorWithGrouping.detectTransient(evlist2);	
    }

}
