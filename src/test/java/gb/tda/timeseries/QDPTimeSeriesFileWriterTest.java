import org.apache.log4j.Logger;

import java.awt.geom.Point2D;

public class QDPTimeSeriesFileWriterTest {

    private static final Logger logger = Logger.getLogger(QDPTimeSeriesFileWriterTest.class);

    public static void main(String[] args) throws Exception {
        
        // Data
        double[] times = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] intensities = new double[] {5, 3, 4, 6, 7, 4, 6, 4, 4, 5};
        double[] uncertainties = new double[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        double tstart = 5;
        String filename;
        double[] binCentres = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] binEdges = BinningUtils.getBinEdgesFromBinCentres(binCentres);

        // BasicTimeSeries
        BasicTimeSeries ts;
        logger.info("Testing BasicTimeSeries(times, intensities)");
        ts = new BasicTimeSeries(times, intensities);
        filename = "basic-time-series1.qdp";
        QDPTimeSeriesFileWriter.writeToFile(ts, filename);

        logger.info("Testing BasicTimeSeries(tstart, times, intensities)");
        ts = new BasicTimeSeries(tstart, times, intensities);
        filename = "basic-time-series2.qdp";
        QDPTimeSeriesFileWriter.writeToFile(ts, filename);

        logger.info("Testing BasicTimeSeries(times, intensities, uncertainties)");
        ts = new BasicTimeSeries(times, intensities, uncertainties);
        filename = "basic-time-series3.qdp";
        QDPTimeSeriesFileWriter.writeToFile(ts, filename);

        logger.info("Testing BasicTimeSeries(tstart, times, intensities, uncertainties)");
        ts = new BasicTimeSeries(tstart, times, intensities, uncertainties);
        filename = "basic-time-series4.qdp";
        QDPTimeSeriesFileWriter.writeToFile(ts, filename);

        // BinnedTimeSeries
        BinnedTimeSeries bts;
        logger.info("Testing BinnedTimeSeries(binEdges, intensities)");
        bts = new BinnedTimeSeries(binEdges, intensities);
        filename = "binned-time-series1.qdp";
        QDPTimeSeriesFileWriter.writeToFile(bts, filename);
        filename = "binned-time-series1-with-sampling.qdp";
        QDPTimeSeriesFileWriter.writeToFileWithSampling(bts, filename);

        logger.info("Testing BinnedTimeSeries(tstart, binEdges, intensities)");
        bts = new BinnedTimeSeries(tstart, binEdges, intensities);
        filename = "binned-time-series2.qdp";
        QDPTimeSeriesFileWriter.writeToFile(bts, filename);
        filename = "binned-time-series2-with-sampling.qdp";
        QDPTimeSeriesFileWriter.writeToFileWithSampling(bts, filename);

        logger.info("Testing BinnedTimeSeries(binEdges, intensities, uncertainties)");
        bts = new BinnedTimeSeries(binEdges, intensities, uncertainties);
        filename = "binned-time-series3.qdp";
        QDPTimeSeriesFileWriter.writeToFile(bts, filename);
        filename = "binned-time-series3-with-sampling.qdp";
        QDPTimeSeriesFileWriter.writeToFileWithSampling(bts, filename);

        logger.info("Testing BinnedTimeSeries(tstart, binEdges, intensities, uncertainties)");
        bts = new BinnedTimeSeries(tstart, binEdges, intensities, uncertainties);
        filename = "binned-time-series4.qdp";
        QDPTimeSeriesFileWriter.writeToFile(bts, filename);
        filename = "binned-time-series4-with-sampling.qdp";
        QDPTimeSeriesFileWriter.writeToFileWithSampling(bts, filename);

        // CountsTimeSeries
        CountsTimeSeries cts;
        logger.info("Testing CountsTimeSeries(binEdges, intensities)");
        cts = new CountsTimeSeries(binEdges, intensities);
        filename = "counts-time-series1.qdp";
        QDPTimeSeriesFileWriter.writeToFile(cts, filename);

        logger.info("Testing CountsTimeSeries(tstart, binEdges, intensities)");
        cts = new CountsTimeSeries(tstart, binEdges, intensities);
        filename = "counts-time-series2.qdp";
        QDPTimeSeriesFileWriter.writeToFile(cts, filename);

        logger.info("Testing CountsTimeSeries(binEdges, intensities, uncertainties)");
        cts = new CountsTimeSeries(binEdges, intensities, uncertainties);
        filename = "counts-time-series3.qdp";
        QDPTimeSeriesFileWriter.writeToFile(cts, filename);

        logger.info("Testing CountsTimeSeries(tstart, binEdges, intensities, uncertainties)");
        cts = new CountsTimeSeries(tstart, binEdges, intensities, uncertainties);
        filename = "counts-time-series4.qdp";
        QDPTimeSeriesFileWriter.writeToFile(cts, filename);


        // RatesTimeSeries
        RatesTimeSeries rts;
        logger.info("Testing RatesTimeSeries(binEdges, intensities)");
        rts = new RatesTimeSeries(binEdges, intensities);
        filename = "rates-time-series1.qdp";
        QDPTimeSeriesFileWriter.writeToFile(rts, filename);

        logger.info("Testing RatesTimeSeries(tstart, binEdges, intensities)");
        rts = new RatesTimeSeries(tstart, binEdges, intensities);
        filename = "rates-time-series2.qdp";
        QDPTimeSeriesFileWriter.writeToFile(rts, filename);

        logger.info("Testing RatesTimeSeries(binEdges, intensities, uncertainties)");
        rts = new RatesTimeSeries(binEdges, intensities, uncertainties);
        filename = "rates-time-series3.qdp";
        QDPTimeSeriesFileWriter.writeToFile(rts, filename);

        logger.info("Testing RatesTimeSeries(tstart, binEdges, intensities, uncertainties)");
        rts = new RatesTimeSeries(tstart, binEdges, intensities, uncertainties);
        filename = "rates-time-series4.qdp";
        QDPTimeSeriesFileWriter.writeToFile(rts, filename);

        // AstroTimeSeries
        AstroTimeSeries ats = new AstroTimeSeries(rts);
        String telescope = "TELESCOPE";
        ats.setTelescope(telescope);
        String instrument = "INSTRUMENT";
        ats.setInstrument(instrument);
        double mjdref = 52000;
        ats.setMJDREF(mjdref);
        String targetName = "TARGET_NAME";
        ats.setTargetName(targetName);
        double ra = 266.40;
        double dec = -29.01;
        ats.setTargetRaDec(ra, dec);
        double emin = 20;
        double emax = 40;
        ats.setEnergyRange(emin, emax);
        double relTimeError = 0.006;
        ats.setRelTimeError(relTimeError);
        double absTimeError = 0.012;
        ats.setAbsTimeError(absTimeError);
        double ontime = 54321;
        ats.setOntime(ontime);
        double livetime = 12345;
        ats.setLivetime(livetime);
        double exposureOnTarget = 1234;
        ats.setExposureOnTarget(exposureOnTarget);
        filename = "astro-time-series1.qdp";
        QDPTimeSeriesFileWriter.writeToFile(ats, filename);

        // CodedMaskTimeSeries
        double maxDistForFullCoding = 8.33;
        double[] effectivePointingDurations = new double[] {0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
        double[] distToPointingAxis = new double[] {5, 7, 3, 2, 6, 8, 12, 3, 2, 3};
        CodedMaskTimeSeries cmts = new CodedMaskTimeSeries(ats, maxDistForFullCoding, effectivePointingDurations, distToPointingAxis);
        filename = "codedmask-time-series1.qdp";
        QDPTimeSeriesFileWriter.writeToFile(cmts, filename);

    }
}