package gb.tda.timeseries;

import org.apache.log4j.Logger;

import java.awt.geom.Point2D;

public class CodedMaskTimeSeriesFactory {
    private static final Logger logger  = Logger.getLogger(CodedMaskTimeSeriesFactory.class);

    public static CodedMaskTimeSeries create(CodedMaskTimeSeries ts) {
        return new CodedMaskTimeSeries(ts);
    }

    // All CodedMaskTimeSeries fields and attributes
    public static CodedMaskTimeSeries create(
            double targetRA, double targetDec,
            double emin, double emax,
            String telescope, String instrument, double maxDistForFullCoding,
            double[] binEdges, double[] effectivePointingDurations,
            double[] rates, double[] errors,
            double[] rasOfPointings, double[] decsOfPointings, double[] exposuresOnTarget
    ) throws BinningException {

        logger.info("Making CodedMaskTimeSeries");
        int[] arrayLengths = new int[] {effectivePointingDurations.length, rates.length, errors.length, rasOfPointings.length, decsOfPointings.length, exposuresOnTarget.length};
        double minLength = arrayLengths[0];
        for (int i=0; i < arrayLengths.length; i++) {
            if (arrayLengths[i] != arrayLengths[0]) {
                logger.warn("Different array length detected in input "+i);
                minLength = Math.min(minLength, arrayLengths[i]);
            }
        }
        if (binEdges.length != 2*rates.length) {
            throw new BinningException("Incompatible array lengths: binEdges.length != 2*rates.length");
        }
        Point2D.Double targetRaDec = new Point2D.Double(targetRA, targetDec);
        Point2D.Double energyMinMax = new Point2D.Double(emin, emax);
        Point2D.Double[] raDecsOfPointings = new Point2D.Double[rasOfPointings.length];
        for (int i=0; i < rasOfPointings.length; i++) {
            raDecsOfPointings[i] = new Point2D.Double(rasOfPointings[i], decsOfPointings[i]);
        }
        double tStart = binEdges[0];
        if (tStart < 0) { tStart = 0; }
        double[] zeroedBinEdges = Utils.resetToZero(binEdges);
        return new CodedMaskTimeSeries(targetRaDec, energyMinMax, telescope, instrument, maxDistForFullCoding, tStart, zeroedBinEdges,
                effectivePointingDurations, rates, errors, raDecsOfPointings, exposuresOnTarget);
    }


    // Adding optional targetName
    public static CodedMaskTimeSeries create(
            String targetName,
            double targetRA, double targetDec,
            double emin, double emax,
            String telescope, String instrument, double maxDistForFullCoding,
            double[] binEdges, double[] effectivePointingDurations,
            double[] rates, double[] errors,
            double[] rasOfPointings, double[] decsOfPointings, double[] exposuresOnTarget
    ) throws BinningException {

        CodedMaskTimeSeries ts = create(targetRA, targetDec, emin, emax, telescope, instrument, maxDistForFullCoding,
                binEdges, effectivePointingDurations, rates, errors, rasOfPointings, decsOfPointings, exposuresOnTarget);
        ts.setTargetName(targetName);
        return ts;
    }

    // Using leftBinEdges and rightBinEdges
    public static CodedMaskTimeSeries create(
            double targetRA, double targetDec,
            double emin, double emax,
            String telescope, String instrument, double maxDistForFullCoding,
            double[] leftBinEdges, double[] rightBinEdges, double[] effectivePointingDurations,
            double[] rates, double[] errors,
            double[] rasOfPointings, double[] decsOfPointings, double[] exposuresOnTarget
    ) throws BinningException {

        double[] binEdges = BinningUtils.getBinEdges(leftBinEdges, rightBinEdges);
        return create(targetRA, targetDec, emin, emax, telescope, instrument, maxDistForFullCoding,
                binEdges, effectivePointingDurations, rates, errors, rasOfPointings, decsOfPointings, exposuresOnTarget);
    }
    // With optional targetName
    public static CodedMaskTimeSeries create(
            String targetName, double targetRA, double targetDec,
            double emin, double emax,
            String telescope, String instrument, double maxDistForFullCoding,
            double[] leftBinEdges, double[] rightBinEdges, double[] effectivePointingDurations,
            double[] rates, double[] errors,
            double[] rasOfPointings, double[] decsOfPointings, double[] exposuresOnTarget
    ) throws BinningException {

        double[] binEdges = BinningUtils.getBinEdges(leftBinEdges, rightBinEdges);
        CodedMaskTimeSeries ts = create(targetRA, targetDec, emin, emax, telescope, instrument, maxDistForFullCoding,
                binEdges, effectivePointingDurations, rates, errors, rasOfPointings, decsOfPointings, exposuresOnTarget);
        ts.setTargetName(targetName);
        return ts;
    }


    // Using distToPointingAxis only
    public static CodedMaskTimeSeries create(
            double targetRA, double targetDec,
            double emin, double emax,
            String telescope, String instrument, double maxDistForFullCoding,
            double[] binEdges, double[] effectivePointingDurations,
            double[] rates, double[] errors,
            double[] distToPointingAxis
    ) throws BinningException {

        logger.info("Making simple CodedMaskTimeSeries");
        int[] arrayLengths = new int[] {effectivePointingDurations.length, rates.length, errors.length, distToPointingAxis.length};
        for (int i=0; i < arrayLengths.length; i++) {
            if (arrayLengths[i] != arrayLengths[0]) {
                throw new BinningException("Incompatible input array lengths");
            }
        }
        if (binEdges.length != 2*rates.length) {
            throw new BinningException("Incompatible bin edges with input data");
        }
        Point2D.Double targetRaDec = new Point2D.Double(targetRA, targetDec);
        Point2D.Double energyMinMax = new Point2D.Double(emin, emax);
        double tStart = binEdges[0];
        if (tStart < 0) { tStart = 0; }
        double[] zeroedBinEdges = Utils.resetToZero(binEdges);
        return new CodedMaskTimeSeries(targetRaDec, energyMinMax, telescope, instrument, maxDistForFullCoding, tStart, zeroedBinEdges,
                effectivePointingDurations, rates, errors, distToPointingAxis);
    }

    // With optional targetName
    public static CodedMaskTimeSeries create(
            String targetName, double targetRA, double targetDec,
            double emin, double emax,
            String telescope, String instrument, double maxDistForFullCoding,
            double[] binEdges, double[] effectivePointingDurations,
            double[] rates, double[] errors,
            double[] distToPointingAxis
    ) throws BinningException {

        CodedMaskTimeSeries ts = create(targetRA, targetDec, emin, emax, telescope, instrument, maxDistForFullCoding,
                binEdges, effectivePointingDurations, rates, errors, distToPointingAxis);
        ts.setTargetName(targetName);
        return ts;
    }
    
}
