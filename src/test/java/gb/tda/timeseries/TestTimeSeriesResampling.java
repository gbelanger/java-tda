public class TestTimeSeriesResampling {

    public static void main(String[] args) throws Exception {

        String filename = "ts_isgri_255.706_-48.790_20-35keV.fits";
        CodedMaskTimeSeries ts = (CodedMaskTimeSeries) TimeSeriesMaker.makeTimeSeries(filename);

    }

}