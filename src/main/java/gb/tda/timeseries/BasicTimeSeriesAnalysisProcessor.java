package gb.tda.timeseries;

import gb.tda.io.AsciiDataFileReader;

private String filename;

public final class BasicTimeSeriesAnalysisProcessor {

    public BasicTimeSeriesAnalysisProcessor(String filename) {
        this.filename = filename;
        processTimeseries(filename);
    }

    private void processTimeseries(String filename) {
        // Read input data file
        AsciiDataFileReader input = new AsciiDataFileReader(filename);
        double[] t = input.getDblCol(0); // assume time as double
        int ncols = input.getNDataCols();

        // Create array of times series: one for each column
        BasicTimeSeries[] tsArray = new BasicTimeSeries[ncols];
        for (int k = 0 ; k < ncols ; k++) {
            double[] values = intput.getDblCol(k+1); // skip first column of times
            List<Double> timesList = new ArrayList<>();
            List<Double> intensitiesList = new ArrayList<>();
            for (int i = 0 ; i < values.length ; i++) {
                if (!Double.isNaN(values[i]) && values[i] != 0.0) {
                    timesList.add(t[i]);
                    intensitiesList.add(values[i])
                }
                timesList.trimToSize();
                intensitiesList.trimToSize();
                double[] times = timesList.stream().mapToDouble(Double::doubleValue).toArray();
                double[] intensities = intensitiesList.stream().mapToDouble(Double::doubleValue).toArray();
                tsArray[k] = BasicTimeSeriesFactory.create(times, intensities);
            }
        }

        // Make histograms of values and intervals
        List<IHistogram1D> valuesHistoList = new ArrayList<>();
        List<IHistogram1D> intervalsHistoList = new ArrayList<>();
        for (BasicTimeSeries ts : tsArray) {
            double[] values = ts.getIntensities();
            double[] intervals = ts.getTimeIntervals();
            valuesHistoList.add(Binner.makeHisto(values));
            intervalsHistoList.add(Binner.makeHisto(intervals));
        }

        // Make Likelihood time series for all values
        NormalLikelihood normalLikelihood = new NormalLikelihood();
        PoissonLikelihood poissonLikelihood = new PoissonLikelihood();
        BasicTimeSeries[] likelihoodTimeseriesArray = new BasicTimeSeries[tsArray.length];
        int k = 0;
        int count = 0;
        for (BasicTimeSeries ts : tsArray) {
            double[] times = ts.getTimes();
            double[] values = ts.getIntensities();
            double mu = ts.meanIntensity();
            double sigma = Math.sqrt(ts.varianceInIntensities());
            double[] logLikelihoods = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                logLikelihoods[i] = normalLikelihood.getLogLikelihood(mu, sigma, values[i]);
                if (logLikelihoods[i] > 1.5) count++;
            }
            likelihoodTimeseriesArray[k++] = BasicTimeSeriesFactory.create(times, logLikelihoods);
        }

    }

}