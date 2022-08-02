package gb.tda.timeseries;

public class FitsTimeSeriesFileReaderTest {

    public static void main(String[] args) throws Exception {
        String filename1 = "saxj1808_lc1.fits";
        BinnedTimeSeries ts1 = (BinnedTimeSeries) BinnedTimeSeriesFactory.create(filename1);

        String filename2 = "flare.fits";
        BinnedTimeSeries ts2 = (BinnedTimeSeries) BinnedTimeSeriesFactory.create(filename2);

        String filename3 = "ts_isgri_255.706_-48.790_20-35keV.fits";
        CodedMaskTimeSeries ts3 = (CodedMaskTimeSeries) BinnedTimeSeriesFactory.create(filename3);

    }
}