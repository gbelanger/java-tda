package gb.tda.timeseries;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.log4j.Logger;
import gb.tda.io.AsciiDataFileWriter;

/**
 * Class <code>QDPTimeSeriesFileWriter</code>
 */

final class QDPTimeSeriesFileWriter implements ITimeSeriesFileWriter {
    
    private static final Logger logger = Logger.getLogger(QDPTimeSeriesFileWriter.class);
    private static final String producedBy = (QDPTimeSeriesFileWriter.class).getCanonicalName();

	// ITimeSeries
	static void writeToFile(ITimeSeries ts, String filename) throws Exception {
		String[] header = QDPHeaderMaker.getHeader(ts, filename, producedBy);
		double[][] data = getData(ts);
		printToFile(filename, header, data);
		logger.info(ts.getClass().getCanonicalName()+" written to "+filename);
	}
	
	static void writeToFile(ITimeSeries ts, double[] function, String filename) throws Exception {
		String[] header = QDPHeaderMaker.getHeader(ts, filename, producedBy);
		double[][] data = getData(ts, function);
		printToFile(filename, header, data);
		logger.info(ts.getClass().getCanonicalName()+" written to "+filename);
	}

	// IBinnedTimeSeries
	public static void writeToFile(IBinnedTimeSeries ts, String filename) throws Exception {
		String[] header = QDPHeaderMaker.getHeader(ts, filename, producedBy);
		double[][] data = getData(ts);
		printToFile(filename, header, data);
		logger.info(ts.getClass().getCanonicalName()+" written to "+filename);
	}
	public static void writeToFile(IBinnedTimeSeries ts, double[] function, String filename) throws Exception {
		String[] header = QDPHeaderMaker.getHeader(ts, filename, producedBy);
		double[][] data = getData(ts, function);
		printToFile(filename, header, data);
		logger.info(ts.getClass().getCanonicalName()+" with function written to "+filename);
	}
	public static void writeToFileWithSampling(IBinnedTimeSeries ts, String filename) throws Exception {
		String[] header = QDPHeaderMaker.getHeader(ts, filename, producedBy);
		double[][] data = getData(ts);
		double[][] sampling = getSamplingFunction(ts);
		printToFile(filename, header, data, sampling);
		logger.info(ts.getClass().getCanonicalName()+" and sampling function written to "+filename);
	}

	public static void writeToFile(CodedMaskTimeSeries ts, String filename) throws Exception {
		String[] header = QDPHeaderMaker.getHeader((IBinnedTimeSeries) ts, filename, producedBy);
		double[][] data = getData(ts);
		printToFile(filename, header, data);
		logger.info(ts.getClass().getCanonicalName()+" in counts written to "+filename);
    }

	public static void writeToFile(CodedMaskTimeSeries ts, double[] function, String filename) throws Exception {
		String[] header = QDPHeaderMaker.getHeader((IBinnedTimeSeries) ts, filename, producedBy);
		double[][] data = getData(ts, function);
		printToFile(filename, header, data);
		logger.info(ts.getClass().getCanonicalName()+" in counts written to "+filename);
	}

	public static void writeToFileWithSampling(CodedMaskTimeSeries ts, double[] function, String filename) throws Exception {
		String[] header = QDPHeaderMaker.getHeader((IBinnedTimeSeries) ts, filename, producedBy);
		double[][] data = getData(ts, function);
		double[][] sampling = getSamplingFunction(ts);
		printToFile(filename, header, data, sampling);
		logger.info(ts.getClass().getCanonicalName()+" in counts written to "+filename);
	}

    public static void writeAllCodedMaskData(CodedMaskTimeSeries ts, String filename) throws Exception {
        String[] header = QDPHeaderMaker.getAllDataHeader(ts, filename, producedBy);
		double[][] data = getAllCodedMaskData(ts);
		printToFile(filename, header, data);
        logger.info(ts.getClass().getCanonicalName()+" (all data) written to "+filename);
    }

	public static void writeAllCodedMaskData(CodedMaskTimeSeries ts, double[] function, String filename) throws Exception {
		String[] header = QDPHeaderMaker.getAllDataHeader(ts, filename, producedBy);
		double[][] data = getAllCodedMaskData(ts, function);
		try {
			printToFile(filename, header, data);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new TimeSeriesFileFormatException("Error writing TimeSeriesFile: ts.nBins() != function.length");
		}
		logger.info(ts.getClass().getCanonicalName()+" (all data) with function written to "+filename);
	}

    ////
    //  Private methods
    ////
	private static double[][] getData(ITimeSeries ts) {
		double[] times = ts.getTimes();
		double[] intensities = ts.getIntensities();
		double[][] data = new double[][] {times, intensities};
		if (ts.uncertaintiesAreSet()) {
			double[] uncertainties = ts.getUncertainties();
			data = new double[][] {times, intensities, uncertainties};
		}
		return data;
	}
	private static double[][] getData(ITimeSeries ts, double[] function) {
		double[] times = ts.getTimes();
		double[] intensities = ts.getIntensities();
		double[][] data = new double[][] {times, intensities, function};
		if (ts.uncertaintiesAreSet()) {
			double[] uncertainties = ts.getUncertainties();
			data = new double[][] {times, intensities, uncertainties, function};
		}
		return data;
	}

	private static double[][] getData(IBinnedTimeSeries ts) {
		double[] times = ts.getTimes();
		double[] intensities = ts.getIntensities();
		double[] halfBinWidths = ts.getHalfBinWidths();
		double[][] data = new double[][] {times, halfBinWidths, intensities};
		if (ts.uncertaintiesAreSet()) {
			double[] uncertainties = ts.getUncertainties();
			data = new double[][] {times, halfBinWidths, intensities, uncertainties};
		}
		return data;
	}

	private static double[][] getData(IBinnedTimeSeries ts, double[] function) {
		double[] times = ts.getTimes();
		double[] intensities = ts.getIntensities();
		double[] halfBinWidths = ts.getHalfBinWidths();
		double[][] data = new double[][] {times, halfBinWidths, intensities, function};
		if (ts.uncertaintiesAreSet()) {
			double[] uncertainties = ts.getUncertainties();
			data = new double[][] {times, halfBinWidths, intensities, uncertainties, function};
		}
		return data;
	}

	private static double[][] getData(CodedMaskTimeSeries ts) {
		double[] times = ts.getTimes();
		double[] intensities = ts.getIntensities();
		double[] halfBinWidths = ts.getHalfBinWidths();
		double[] distToPointingAxis = ts.getDistToPointingAxis();
		double[][] data = new double[][] {times, halfBinWidths, intensities, distToPointingAxis};
		if (ts.uncertaintiesAreSet()) {
			double[] uncertainties = ts.getUncertainties();
			data = new double[][] {times, halfBinWidths, intensities, uncertainties, distToPointingAxis};
		}
		return data;
	}

	private static double[][] getData(CodedMaskTimeSeries ts, double[] function) {
		double[] times = ts.getTimes();
		double[] intensities = ts.getIntensities();
		double[] halfBinWidths = ts.getHalfBinWidths();
		double[] distToPointingAxis = ts.getDistToPointingAxis();
		double[][] data = new double[][] {times, halfBinWidths, intensities, distToPointingAxis, function};
		if (ts.uncertaintiesAreSet()) {
			double[] uncertainties = ts.getUncertainties();
			data = new double[][] {times, halfBinWidths, intensities, uncertainties, distToPointingAxis, function};
		}
		return data;
	}

	private static double[][] getAllCodedMaskData(CodedMaskTimeSeries ts) {
		double[] times = ts.getTimes();
		double[] halfBinWidths = ts.getHalfBinWidths();
		double[] intensities = ts.getIntensities();
		double[] uncertainties = ts.getUncertainties();
		double[] distToPointingAxis = ts.getDistToPointingAxis();
		double[] rasOfPointings = ts.getRasOfPointings();
		double[] decsOfPointings = ts.getDecsOfPointings();
		double[] exposuresOnTarget = ts.getExposuresOnTarget();
		double[] effectivePointingDurations = ts.getEffectivePointingDurations();
		return new double[][] {times, halfBinWidths, intensities,
				distToPointingAxis, rasOfPointings, decsOfPointings,
				exposuresOnTarget, effectivePointingDurations};
	}
	private static double[][] getAllCodedMaskData(CodedMaskTimeSeries ts, double[] function) {
		double[] times = ts.getTimes();
		double[] halfBinWidths = ts.getHalfBinWidths();
		double[] intensities = ts.getIntensities();
		double[] uncertainties = ts.getUncertainties();
		double[] distToPointingAxis = ts.getDistToPointingAxis();
		double[] rasOfPointings = ts.getRasOfPointings();
		double[] decsOfPointings = ts.getDecsOfPointings();
		double[] exposuresOnTarget = ts.getExposuresOnTarget();
		double[] effectivePointingDurations = ts.getEffectivePointingDurations();
		return new double[][] {times, halfBinWidths, intensities,
				distToPointingAxis, rasOfPointings, decsOfPointings,
				exposuresOnTarget, effectivePointingDurations, function};
	}


	private static double[][] getSamplingFunction(IBinnedTimeSeries ts) {
        double[] edges = ts.getSamplingFunctionBinEdges();
        double[] centres = BinningUtils.getBinCentresFromBinEdges(edges);
        double[] halfWidths = BinningUtils.getHalfBinWidthsFromBinEdges(edges);
        double[] sampling = ts.getSamplingFunctionValues();
		return new double[][] {centres, halfWidths, sampling};
    }

    private static void printToFile(String filename, String[] header, double[][] data) throws IOException, TimeSeriesFileFormatException {
		int bufferSize = 256000;
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename), bufferSize));
		//  Write the header
		for (String s : header) {
			pw.println(s);
		}
		//  Write the data
		try {
			for (int i=0; i < data[0].length; i++) {
				pw.print(data[0][i]);
				int k=1;
				while (k < data.length) {
					pw.print("	"+data[k][i]);
					k++;
				}
				pw.println();
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new TimeSeriesFileFormatException("Error writing TimeSeriesFile: ts.nBins() != function.length");
		}
        pw.close();
    }

	private static void printToFile(String filename, String[] header, double[][] data, double[][] sampling) throws IOException, TimeSeriesFileFormatException {
		int bufferSize = 256000;
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename), bufferSize));
		//  Write the header
		for (String s : header) {
			pw.println(s);
		}
		//  Write the time series data
		try {
			for (int i=0; i < data[0].length; i++) {
				pw.print(data[0][i]);
				int k=1;
				while (k < data.length) {
					pw.print("	"+data[k][i]);
					k++;
				}
				pw.println();
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new TimeSeriesFileFormatException("Error writing TimeSeriesFile: ts.nBins() != function.length");
		}
		pw.println("NO NO NO NO NO");
		//  Write the sampling function
		for (int i=0; i < sampling[0].length; i++) {
			pw.println(sampling[0][i] +"	"+ sampling[1][i] +"	"+ (sampling[2][i]) +"	 0.0 	" + (sampling[2][i]));
		}
		pw.close();
	}

}
