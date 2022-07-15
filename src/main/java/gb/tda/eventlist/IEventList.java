package gb.tda.eventlist;

import java.io.IOException;

public interface IEventList {

    int nEvents();
    double tStart();
    double tStop();
    double duration();
    double meanRate();
    double minEventSpacing();
    double maxEventSpacing();
    double meanEventSpacing();
    double[] getArrivalTimes();
    double[] getInterArrivalTimes();
    void writeAsQDP(String filename) throws IOException;

}