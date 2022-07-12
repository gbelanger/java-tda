package gb.tda.tools;

//import gb.codetda.timeseries.TimeSeries;
//import gb.codetda.timeseries.TimeSeriesMaker;
//import gb.codetda.timeseries.TimeSeriesUtils;

public class TestDataUtils {


    public static void testFillGaps() throws Exception {

	TimeSeries ts = (TimeSeries) TimeSeriesMaker.makeTimeSeries("lc7.qdp");

	ts.writeCountsAsQDP("lc-gaps.qdp");
	ts = TimeSeriesUtils.fillGaps(ts);
	ts.writeCountsAsQDP("lc-noGaps.qdp");

    }

    public static void main(String[] args) throws Exception {

	testFillGaps();
    }

}
