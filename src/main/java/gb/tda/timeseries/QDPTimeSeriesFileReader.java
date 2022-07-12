package gb.tda.timeseries;

import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import gb.tda.tools.StringUtils;
import gb.tda.binner.BinningException;
import gb.tda.binner.BinningUtils;
import gb.tda.eventlist.AsciiEventFileException;
import gb.tda.eventlist.AsciiEventFileReader;
import gb.tda.eventlist.EventList;
import gb.tda.eventlist.EventListException;
import gb.tda.io.AsciiDataFileFormatException;
import gb.tda.io.AsciiDataFileReader;

/**
 * Class <code>QDPTimeSeriesFileReader</code> reads a times series or Coded Mask Time Series file in ASCII-QDP format.
 * The Time Series reader is very simple an only additionally reads the tStart header in the label.
 * The Coded Mask Reader requires 5 columns to read the file, setting the columns to the arrays binCentres, dtOver2, rates, errorOnRates and distToPointingAxis.
 *
 * The TimeSeries will have bins defined by the binCentres and corresponding half-widths.
 *
 * @author G. Belanger
 */

public class QDPTimeSeriesFileReader extends AsciiTimeSeriesFileReader {
    
    private static final Logger logger = Logger.getLogger(QDPTimeSeriesFileReader.class);

	// Time series attributes
    private String targetName;
    private double targetRA;
    private double targetDec;
    private String instrument;
    private String telescope;
    private double maxDistForFullCoding;
    private double energyRangeMin;
    private double energyRangeMax;
    private double exposureOnTarget;
    
    public ITimeSeries readTimeSeriesFile(String filename) throws TimeSeriesFileException, TimeSeriesException, BinningException, IOException  {
        AsciiDataFileReader dataFile;
		try {
			dataFile = new AsciiDataFileReader(filename);
		}
		catch ( AsciiDataFileFormatException e ) {
			throw new AsciiTimeSeriesFileException("Problem reading ASCII data file", e);
		}
		int ncols = dataFile.getNDataCols();
		if ( ncols >= 5 ) {  //   Assume it is a CodedMaskTimeSeries
			parseHeader(dataFile.getHeader());
			//  These columns must be defined
			double[] binCentres = dataFile.getDblCol(0);
			double[] dtOver2 = dataFile.getDblCol(1);
			double[] rates = dataFile.getDblCol(2);
			double[] errors = dataFile.getDblCol(3);
			double[] distToPointingAxis = dataFile.getDblCol(4);    // Constructed from rasOfPointing and decsOfPointing
			//  construct the bin edges
			double[] binEdges = BinningUtils.getBinEdgesFromBinCentresAndHalfWidths(binCentres, dtOver2);
			try {
				double[] rasOfPointings = dataFile.getDblCol(5);
				double[] decsOfPointings = dataFile.getDblCol(6);
				double[] exposuresOnTarget = dataFile.getDblCol(7);
				double[] effectivePointingDurations = dataFile.getDblCol(8);
				CodedMaskTimeSeries ts = TimeSeriesFactory.makeCodedMaskTimeSeries(targetName, targetRA, targetDec, energyRangeMin, energyRangeMax, telescope, instrument, maxDistForFullCoding, binEdges, effectivePointingDurations, rates, errors, rasOfPointings, decsOfPointings, exposuresOnTarget);
				return ts;
			}
			catch ( ArrayIndexOutOfBoundsException e ) {
				double[] effectivePointingDurations = new double[dtOver2.length];
				for ( int i=0; i < dtOver2.length; i++ ) {
					effectivePointingDurations[i] = 2*dtOver2[i];
				}
				CodedMaskTimeSeries ts = TimeSeriesFactory.makeCodedMaskTimeSeries(targetName, targetRA, targetDec, energyRangeMin, energyRangeMax, telescope, instrument, maxDistForFullCoding, binEdges, effectivePointingDurations, rates, errors, distToPointingAxis);
				ts.setExposureOnTarget(this.exposureOnTarget);
				return ts;
			}
		}
		else {
            throw new AsciiTimeSeriesFileException("Not an ASCII CodedMaskTimeSeries file CodedMaskTimeSeries requires at least 5 columns: binCentres, halfBinWidths, rates, errors, distToPointingAxis");
        }
    }

    private void parseHeader(String[] header) {
		//  Define the comment block and the header block
		ArrayList<String> commentLines = new ArrayList<String>();
		ArrayList<String> qdpHeaderLines = new ArrayList<String>();
		for ( int i=0; i < header.length; i++ ) {
			if ( header[i].startsWith("!") ) {
				commentLines.add(header[i]);
			}
			else {
				qdpHeaderLines.add(header[i]);
			}
		}
		commentLines.trimToSize();
		qdpHeaderLines.trimToSize();

		//  Define the values of variables from the contents of the comment block
		// Internal variables
		String[] commentBlock = new String[commentLines.size()];
		String[] qdpHeaderBlock = new String[qdpHeaderLines.size()];
		for (int i = 0; i < commentBlock.length; i++ ) {
			commentBlock[i] = commentLines.get(i);
		}
		for (int i = 0; i < qdpHeaderBlock.length; i++ ) {
			qdpHeaderBlock[i] = qdpHeaderLines.get(i);
		}
		String[] stringsToFind = new String[] {"Target Name", "Target RA", "Target Dec", "Telescope", "Instrument", "Max distance for full coding", "Energy range min", "Energy range max", "Exposure on target"};
		int[] indexes = new int[stringsToFind.length];
		for ( int i=0; i < stringsToFind.length; i++ ) {
			indexes[i] = StringUtils.findStringIndex(stringsToFind[i], commentBlock);
		}
		int k=0;
		int index = indexes[k];
		if ( index != -1 ) {
			int from = commentBlock[index].indexOf(": ") + 2;
			this.targetName = commentBlock[index].substring(from);
		}
		k++;
		index = indexes[k];
		if ( index != -1 ) {
			int from = commentBlock[index].indexOf(": ") + 2;
			this.targetRA = Double.parseDouble(commentBlock[index].substring(from));
		}
		k++;
		index = indexes[k];
		if ( index != -1 ) {
			int from = commentBlock[index].indexOf(": ") + 2;
			this.targetDec = Double.parseDouble(commentBlock[index].substring(from));
		}
		k++;
		index = indexes[k];
		if ( index != -1 ) {
			int from = commentBlock[index].indexOf(": ") + 2;
			this.telescope = commentBlock[index].substring(from);
		}
		k++;
		index = indexes[k];
		if ( index != -1 ) {
			int from = commentBlock[index].indexOf(": ") + 2;
			this.instrument = commentBlock[index].substring(from);
		}
		k++;
		index = indexes[k];
		if ( index != -1 ) {
			int from = commentBlock[index].indexOf(": ") + 2;
			this.maxDistForFullCoding = Double.parseDouble(commentBlock[index].substring(from));
		}
		k++;
		index = indexes[k];
		if ( index != -1 ) {
			int from = commentBlock[index].indexOf(": ") + 2;
			this.energyRangeMin = Double.parseDouble(commentBlock[index].substring(from));
		}
		k++;
		index = indexes[k];
		if ( index != -1 ) {
			int from = commentBlock[index].indexOf(": ") + 2;
			this.energyRangeMax = Double.parseDouble(commentBlock[index].substring(from));
		}
		k++;
		index = indexes[k];
		if ( index != -1 ) {
			int from = commentBlock[index].indexOf(": ") + 2;
			int to = commentBlock[index].indexOf(" s");
			this.exposureOnTarget = Double.parseDouble(commentBlock[index].substring(from, to));
		}
    }	

}
