package gb.tda.io;

import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.ref.histogram.Histogram1D;
import java.util.StringTokenizer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import org.apache.log4j.Logger;
import gb.tda.utils.BasicStats;
import gb.tda.utils.MinMax;

/**
* The class <code>AsciiDataFileWriter</code> is used to write data as ASCI files in QDP format.
*
* @author <a href="mailto: guilaume.belanger@esa.int">G. Belanger, ESA, ESAC</a>
* @version April 2021 (last update)
*
*/
public class AsciiDataFileWriter {

  //  Class variables
  private static Logger logger  = Logger.getLogger(AsciiDataFileWriter.class);
  private File file;
  private String filename;
  private PrintWriter printWriter;
  private static DecimalFormat stats = new DecimalFormat("0.00E00");
  private static DecimalFormat label = new DecimalFormat("0.000");
  private static String ENTRIES_PER_BIN = "Entries per bin";

  //  Constructor
  public AsciiDataFileWriter(final String filename) throws IOException {
    int bufferSize = 256000;
    this.file = new File(filename);
    this.filename = this.file.getPath();
    printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filename), bufferSize));
  }

  // All Methods
 
  public File getFile() {
    return new File(this.file.getPath());
  }

  public String getFilename() {
    return new String(this.filename);
  }

  //  Methods writeHisto for interface IHistogram1D

  public void writeHisto(final IHistogram1D iHisto, final String xLabel) {
    writeHisto((Histogram1D) iHisto, xLabel);
  }

  public void writeHisto(final IHistogram1D iHisto, final String xLabel, final boolean showStats) {
    writeHisto((Histogram1D) iHisto, xLabel, showStats);
  }

  public void writeHisto(final IHistogram1D iHisto, final String xLabel, final String yLabel) {
    writeHisto((Histogram1D) iHisto, xLabel, yLabel);
  }

  public void writeHisto(final IHistogram1D iHisto, final String xLabel, final String yLabel, final boolean showStats) {
    writeHisto((Histogram1D) iHisto, xLabel, yLabel, showStats);
  }

  public void writeHisto(final IHistogram1D iHisto, final String xLabel, final String yLabel, final String plotLabel) {
    writeHisto((Histogram1D) iHisto, xLabel, yLabel, plotLabel);
  }

  public void writeHisto(final IHistogram1D iHisto, final String xLabel, final String yLabel, final String plotLabel, final boolean showStats) {
    writeHisto((Histogram1D) iHisto, xLabel, yLabel, plotLabel, showStats);
  }

  public void writeHisto(final IHistogram1D iHisto, final double yMin, final double yMax, final String xLabel) {
    writeHisto((Histogram1D) iHisto, yMin, yMax, xLabel);
  }

  public void writeHisto(final IHistogram1D iHisto, final double yMin, final double yMax, final String xLabel, final boolean showStats) {
    writeHisto((Histogram1D) iHisto, yMin, yMax, xLabel, showStats);
  }

  public void writeHisto(final IHistogram1D iHisto, final double[] function, final String xLabel) {
    writeHisto((Histogram1D) iHisto, function, xLabel);
  }

  public void writeHisto(final IHistogram1D iHisto, final double[] function, final String xLabel, final boolean showStats) {
    writeHisto((Histogram1D) iHisto, function, xLabel, showStats);
  }

  public void writeHisto(final IHistogram1D iHisto, final double[] function, final String xLabel, final String yLabel, final boolean showStats) {
    writeHisto((Histogram1D) iHisto, function, xLabel, yLabel, showStats);
  }

  public void writeHisto(final IHistogram1D iHisto, final double[] function, final String xLabel, final String yLabel, final String plotLabel) {
    writeHisto((Histogram1D) iHisto, function, xLabel, yLabel, plotLabel);
  }

  public void writeHisto(final IHistogram1D iHisto, final double[] function, final String xLabel, final String yLabel, final String plotLabel, final boolean showStats) {
    writeHisto((Histogram1D) iHisto, function, xLabel, yLabel, plotLabel, showStats);
  }


  //  Methods writeHisto for class Histogram1D

  public void writeHisto(final Histogram1D histo, final String xLabel) {
    boolean showStats = true;
    writeHisto(histo, xLabel, showStats);
  }

  public void writeHisto(final Histogram1D histo, final String xLabel, final boolean showStats) {
    String[] header = makeHistoHeader(histo, xLabel, showStats);
    double[][] data = getData(histo);
    printToFile(header, data[0], data[1]);
  }

  public void writeHisto(final Histogram1D histo, final String xLabel, final String yLabel) {
    boolean showStats = true;
    writeHisto(histo, xLabel, yLabel, showStats);
  }

  public void writeHisto(final Histogram1D histo, final String xLabel, final String yLabel, final boolean showStats) {
    String[] header = makeHistoHeader(histo, xLabel, yLabel, showStats);
    double[][] data = getData(histo);
    printToFile(header, data[0], data[1]);
  }

  public void writeHisto(final Histogram1D histo, final String xLabel, final String yLabel, final String plotLabel) {
    boolean showStats = true;
    writeHisto(histo, xLabel, yLabel, plotLabel, showStats);
  }

  public void writeHisto(final Histogram1D histo, final String xLabel, final String yLabel, final String plotLabel, final boolean showStats) {
    String[] header = makeHistoHeader(histo, xLabel, yLabel, plotLabel, showStats);
    double[][] data = getData(histo);
    printToFile(header, data[0], data[1]);
  }

  public void writeHisto(final Histogram1D histo, final double yMin, final double yMax, final String xLabel) {
    boolean showStats = true;
    writeHisto(histo, yMin, yMax, xLabel, showStats);
  }

  public void writeHisto(final Histogram1D histo, final double yMin, final double yMax, final String xLabel, final boolean showStats) {
    String[] header = makeHistoHeader(histo, yMin, yMax, xLabel, showStats);
    double[][] data = getData(histo);
    printToFile(header, data[0], data[1]);
  }

  public void writeHisto(final Histogram1D histo, final double[] function, final String xLabel) {
    boolean showStats = true;
    writeHisto(histo, function, xLabel, showStats);
  }

  public void writeHisto(final Histogram1D histo, final double[] function, final String xLabel, final boolean showStats) {
    String yLabel = ENTRIES_PER_BIN;
    writeHisto(histo, function, xLabel, yLabel, showStats);
  }

  public void writeHisto(final Histogram1D histo, final double[] function, final String xLabel, final String yLabel) {
    boolean showStats = true;
    writeHisto(histo, function, xLabel, yLabel, showStats);
  }

  public void writeHisto(final Histogram1D histo, final double[] function, final String xLabel, final String yLabel, final boolean showStats) {
    String[] header = makeHistoHeader(histo, xLabel, yLabel, showStats);
    double[][] data = getData(histo);
    printToFile(header, data[0], data[1], function);
  }

  public void writeHisto(final Histogram1D histo, final double[] function, final String xLabel, final String yLabel, final String plotLabel) {
    boolean showStats = true;
    writeHisto(histo, function, xLabel, yLabel, plotLabel, showStats);
  }

  public void writeHisto(final Histogram1D histo, final double[] function, final String xLabel, final String yLabel, final String plotLabel, final boolean showStats) {
    String[] header = makeHistoHeader(histo, xLabel, yLabel, plotLabel, showStats);
    double[][] data = getData(histo);
    printToFile(header, data[0], data[1], function);
  }

  public void writeCorrPlot(final double[] x, final double[] y, final String xLabel, final String yLabel) {
    double[] correlationCoeff = BasicStats.getCorrelationCoefficient(x, y);
    double r = correlationCoeff[0];
    double rSigma = correlationCoeff[1];
    String[] header = makeCorrPlotHeader(xLabel, yLabel, r, rSigma);
    printToFile(header, x, y);
  }

  // public void writeCorrPlot(final double[] x, final double[] y, final double[] yErr, final String xLabel, final String yLabel) {
  // 	double[] correlationCoeff = BasicStats.getCorrelationCoefficient(x, y);
  // 	double r = correlationCoeff[0];
  // 	double rSigma = correlationCoeff[1];
  // 	String[] header = makeCorrPlotHeader(xLabel, yLabel, r, rSigma);
  // 	printToFile(header, x, y);
  // }

  public void writeCorrPlot(final double[] x, final double[] y, final String xLabel, final String yLabel, final double[] xRange, final double[] yRange, final boolean logX, final boolean logY) {
    double[] correlationCoeff = BasicStats.getCorrelationCoefficient(x, y);
    double r = correlationCoeff[0];
    double rSigma = correlationCoeff[1];
    String[] header = makeCorrPlotHeader(xLabel, yLabel, r, rSigma, xRange, yRange, logX, logY);
    printToFile(header, x, y);
  }


  //  Utility methods

  public static double[][] getData(final Histogram1D histo) {
    IAxis axis = histo.axis();
    int nBins = axis.bins();
    double[] binHeights = new double[nBins];
    double[] binCentres = new double[nBins];
    for (int i=0; i < nBins; i++) {
      binHeights[i] = histo.binHeight(i);
      binCentres[i] = axis.binCenter(i);
    }
    return new double[][] {binCentres, binHeights};
  }

  public static double[][] getData(final IHistogram1D iHisto) {
    return getData((Histogram1D) iHisto);
  }

  private double[] calculateYMinYMax(final Histogram1D histo) {
    // 	double maxBinHeight = histo.maxBinHeight();
    // 	double minBinHeight = histo.minBinHeight();
    // 	double max = Math.ceil(maxBinHeight/5d)*5d;
    // 	double margin = Math.ceil(0.05*max);
    // 	margin = Math.ceil(margin/5)*5;
    // 	double yMin = -margin;
    // 	double yMax = max + margin;
    // 	return new double[] { yMin, yMax };
    double maxBinHeight = histo.maxBinHeight();
    double minBinHeight = histo.minBinHeight();
    double margin = 0.05*maxBinHeight;
    double max = maxBinHeight + margin;
    double yMin = minBinHeight - margin;
    yMin = Math.max(0, yMin-margin);
    double yMax = max;
    return new double[] { yMin, yMax };
  }

  //  Make header methods

  public static String[] makeHeaderWithYErrors(final String xLabel, final String yLabel) {
    String[] header = new String[] {
      "DEV /XS",
      "READ SERR 2",
      "LAB T", "LAB F",
      "TIME OFF",
      "LINE STEP",
      "MA 17 ON", "MA SIZE 1",
      "LW 4", "CS 1.1",
      "LAB X "+xLabel,
      "LAB Y "+yLabel,
      "VIEW 0.1 0.2 0.9 0.8",
      "SKIP SINGLE",
      "!"
    };
    return header;
  }

  public static String[] makeHeaderWithYErrors(final String xLabel, final String yLabel, final String plotLabel, final double[] xRange, final double[] yRange) {
    String[] header = new String[] {
      "DEV /XS",
      "READ SERR 2",
      "LAB T", "LAB F",
      "TIME OFF",
      "LINE STEP",
      "LINE ON 2",
      "MA OFF 2",
      "MA 17 ON 1", 
      "MA SIZE 1",
      "LW 4", "CS 1.1",
      "LAB X "+xLabel,
      "LAB Y "+yLabel,
      "LAB 1 \""+plotLabel+"\" CS 1.3",
      "LAB 1 VPOS 0.2 0.7q JUST LEFT",      
      "VIEW 0.1 0.2 0.9 0.8",
      "SKIP SINGLE",
      "R X "+xRange[0]+" "+xRange[1],
      "R Y "+yRange[0]+" "+yRange[1],
      "!"
    };
    return header;
  }

  public static String[] makeHeaderWithXYErrors(final String xLabel, final String yLabel) {
    String[] header = new String[] {
      "DEV /XS",
      "READ SERR 1 2",
      "LAB T", "LAB F",
      "TIME OFF",
      "LINE STEP",
      "MA 17 ON", "MA SIZE 1",
      "LW 4", "CS 1.1",
      "LAB X "+xLabel,
      "LAB Y "+yLabel,
      "VIEW 0.1 0.2 0.9 0.8",
      "SKIP SINGLE",
      "!"
    };
    return header;
  }

  public static String[] makeHeaderWithXYErrors(final String xLabel, final String yLabel, final double[] xRange, final double[] yRange) {
    String[] header = new String[] {
      "DEV /XS",
      "READ SERR 1 2",
      "LAB T", "LAB F",
      "TIME OFF",
      "LINE STEP",
      "MA 17 ON", "MA SIZE 1",
      "LW 4", "CS 1.1",
      "LAB X "+xLabel,
      "LAB Y "+yLabel,
      "VIEW 0.1 0.2 0.9 0.8",
      "SKIP SINGLE",
      "R X "+xRange[0]+" "+xRange[1],
      "R Y "+yRange[0]+" "+yRange[1],
      "!"
    };
    return header;
  }


  public static String[] makeHeader(final String xLabel, final String yLabel, final String plotLabel) {
    String[] header = new String[] {
      "DEV /XS",
      "LAB T", "LAB F",
      "TIME OFF",
      "LINE OFF",
      "MA 2 ON", "MA SIZE 1",
      "LW 4", "CS 1.5",
      "LAB X "+xLabel,
      "LAB Y "+yLabel,
      "LAB 1 \""+plotLabel+"\" CS 1.3",
      "LAB 1 VPOS 0.27 0.8 JUST LEFT",
      "VIEW 0.1 0.3 0.9 0.7",
      "SKIP SINGLE",
      "!"
    };
    return header;
  }

  public static String[] makeHeader(final String xLabel, final String yLabel) {
    String[] header = new String[] {
      "DEV /XS",
      "LAB T", "LAB F",
      "TIME OFF",
      "LINE OFF",
      "MA 2 ON", "MA SIZE 1",
      "LW 4", "CS 1.5",
      "LAB X "+xLabel,
      "LAB Y "+yLabel,
      "VIEW 0.1 0.3 0.9 0.7",
      "SKIP SINGLE",
      "!"
    };
    return header;
  }

  public static String[] makeCorrPlotHeader(final String xLabel, final String yLabel, final double rho, final double rhoErr) {
    String[] header = new String[] {
      "DEV /XS",
      "LAB T", "LAB F",
      "TIME OFF",
      "LINE OFF",
      "MA 17 ON",
      "MA SIZE 1.1",
      "LW 4", "CS 1.3",
      "VIEW 0.2 0.1 0.8 0.9",
      "LAB X "+xLabel,
      "LAB Y "+yLabel,
      "LAB 100 VPOS 0.25 0.83 \"Correlation Coefficient:\"",
      "LAB 100 JUST LEFT",
      "LAB 1 VPOS 0.3 0.78 \\gr = "+label.format(rho)+" +/- "+label.format(rhoErr)+"\"",
      "LAB 1 JUST LEFT",
      "!"
    };
    return header;
  }

  public static String[] makeCorrPlotHeader(final String xLabel, final String yLabel, final double rho, final double rhoErr, final double[] xRange, final double[] yRange, final boolean logX, final boolean logY) {
    String logXValue = "OFF";
    String logYValue = "OFF";
    if (logX) logXValue="ON";
    if (logY) logYValue="ON";
    String[] header = new String[] {
      "DEV /XS",
      "LAB T", "LAB F",
      "TIME OFF",
      "LINE OFF",
      "MA 17 ON",
      "MA SIZE 1.1",
      "LW 4", "CS 1.3",
      "VIEW 0.2 0.1 0.8 0.9",
      "LAB X "+xLabel,
      "LAB Y "+yLabel,
      "LAB 100 VPOS 0.25 0.83 \"Correlation Coefficient:\"",
      "LAB 100 JUST LEFT",
      "LAB 1 VPOS 0.3 0.78 \\gr = "+label.format(rho)+" +/- "+label.format(rhoErr)+"\"",
      "LAB 1 JUST LEFT",
      "LOG X "+logXValue,
      "LOG Y "+logYValue,
      "R X "+xRange[0]+" "+xRange[1],
      "R Y "+yRange[0]+" "+yRange[1],
      "!"
    };
    return header;
  }

  // PRIVATE methods

  private String[] makeHistoHeader(final Histogram1D histo, final String xLabel) {
    boolean showStats = true;
    return makeHistoHeader(histo, xLabel, showStats);
  }

  private String[] makeHistoHeader(final Histogram1D histo, final String xLabel, final boolean showStats) {
    String yLabel = ENTRIES_PER_BIN;
    return makeHistoHeader(histo, xLabel, yLabel, showStats);
  }

  private String[] makeHistoHeader(final Histogram1D histo, final String xLabel, final String yLabel) {
    boolean showStats = true;
    return makeHistoHeader(histo, xLabel, yLabel, showStats);
  }

  private String[] makeHistoHeader(final Histogram1D histo, final String xLabel, final String yLabel, final boolean showStats) {
    double[] yMinMax = calculateYMinYMax(histo);
    double yMin = yMinMax[0];
    double yMax = yMinMax[1];
    return makeHistoHeader(histo, yMin, yMax, xLabel, yLabel, showStats);
  }

  private String[] makeHistoHeader(final Histogram1D histo, final String xLabel, final String yLabel, final String plotLabel) {
    boolean showStats = true;
    return makeHistoHeader(histo, xLabel, yLabel, plotLabel, showStats);
  }

  private String[] makeHistoHeader(final Histogram1D histo, final String xLabel, final String yLabel, final String plotLabel, final boolean showStats) {
    double[] yMinMax = calculateYMinYMax(histo);
    double yMin = yMinMax[0];
    double yMax = yMinMax[1];
    return makeHistoHeader(histo, yMin, yMax, xLabel, yLabel, plotLabel, showStats);
  }

  private String[] makeHistoHeader(final Histogram1D histo, final double yMin, final double yMax, final String xLabel, final boolean showStats) {
    String yLabel = ENTRIES_PER_BIN;
    return makeHistoHeader(histo, yMin, yMax, xLabel, yLabel, showStats);
  }

  private String[] makeHistoHeader(final Histogram1D histo, final double yMin, final double yMax, final String xLabel, final String yLabel, final boolean showStats) {
    IAxis axis = histo.axis();
    int nBins = axis.bins();
    double binWidth = axis.binWidth(0);
    double lowerEdge = axis.binLowerEdge(0);
    double upperEdge = axis.binUpperEdge(nBins-1);
    double xRange = upperEdge - lowerEdge;
    double nMajorDivs = xRange/(2*binWidth);
    String xMinStr = stats.format(lowerEdge);
    String xMaxStr = stats.format(upperEdge);
    String yMinStr = stats.format(yMin);
    String yMaxStr = stats.format(yMax);
    String[] header = null;
    if (showStats == true) {
      int entries = histo.entries();
      double mean = histo.mean();
      double rms = histo.rms();
      String meanStr = stats.format(mean);
      String rmsStr = rmsStr = stats.format(rms);
      header = new String[] {
        "DEV /XS",
        "READ 1",
        "LAB T", "LAB F",
        "TIME OFF",
        "LINE STEP",
        "LINE ON 3",
        "LW 4", "CS 1.5",
        "LAB 1 VPOS 0.76 0.8 \"Entries = "+entries+"\" JUST RIGHT CS 1",
        "LAB 2 VPOS 0.76 0.77 \"Mean = "+meanStr+"\" JUST RIGHT CS 1",
        "LAB 3 VPOS 0.76 0.74 \"RMS = "+rmsStr+"\" JUST RIGHT CS 1",
        "LAB X "+xLabel,
        "LAB Y "+yLabel,
        "VIEW 0.2 0.1 0.8 0.9",
        "R X "+xMinStr+" "+xMaxStr,
        "R Y "+yMinStr+" "+yMaxStr,
        //"GRID X "+nMajorDivs+",2",
        "!"
      };
    }
    else {
      header = new String[] {
        "DEV /XS",
        "READ 1",
        "LAB T", "LAB F",
        "TIME OFF",
        "LINE STEP",
        "LINE ON 3",
        "LW 4", "CS 1.5",
        "LAB X "+xLabel,
        "LAB Y "+yLabel,
        "VIEW 0.2 0.1 0.8 0.9",
        "R X "+xMinStr+" "+xMaxStr,
        "R Y "+yMinStr+" "+yMaxStr,
        //"GRID X "+nMajorDivs+",2",
        "!"
      };
    }
    return header;
  }

  private String[] makeHistoHeader(final Histogram1D histo, final double yMin, final double yMax, final String xLabel, final String yLabel, final String plotLabel, final boolean showStats) {
    IAxis axis = histo.axis();
    int nBins = axis.bins();
    double binWidth = axis.binWidth(0);
    double lowerEdge = axis.binLowerEdge(0);
    double upperEdge = axis.binUpperEdge(nBins-1);
    double xRange = upperEdge - lowerEdge;
    double nMajorDivs = xRange/(2*binWidth);
    String xMinStr = stats.format(lowerEdge);
    String xMaxStr = stats.format(upperEdge);
    String yMinStr = stats.format(yMin);
    String yMaxStr = stats.format(yMax);
    String[] header = null;
    if (showStats == true) {
      int entries = histo.entries();
      double mean = histo.mean();
      double rms = histo.rms();
      String meanStr = null;
      String rmsStr = null;
      if (Math.abs(mean) < 0.01 || Math.abs(mean) > 10) meanStr = stats.format(mean);
      else meanStr = stats.format(mean);
      if (Math.abs(rms) < 0.01 || Math.abs(mean) > 10) rmsStr = stats.format(rms);
      else rmsStr = stats.format(rms);
      header = new String[] {
        "DEV /XS",
        "READ 1",
        "LAB T", "LAB F",
        "TIME OFF",
        "LINE STEP",
        "LINE ON 3",
        "LW 4", "CS 1.5",
        "LAB 1 VPOS 0.76 0.8 \"Entries = "+entries+"\" JUST RIGHT CS 1",
        "LAB 2 VPOS 0.76 0.77 \"Mean = "+meanStr+"\" JUST RIGHT CS 1",
        "LAB 3 VPOS 0.76 0.74 \"RMS = "+rmsStr+"\" JUST RIGHT CS 1",
        "LAB 4 \""+plotLabel+"\" CS 1",
        "LAB 4 VPOS 0.27 0.8 JUST LEFT",
        "LAB X "+xLabel,
        "LAB Y "+yLabel,
        "VIEW 0.2 0.1 0.8 0.9",
        "R X "+xMinStr+" "+xMaxStr,
        "R Y "+yMinStr+" "+yMaxStr,
        //"GRID X "+nMajorDivs+",2",
        "!"
      };
    }
    else {
      header = new String[] {
        "DEV /XS",
        "READ 1",
        "LAB T", "LAB F",
        "TIME OFF",
        "LINE STEP",
        "LINE ON 3",
        "LW 4", "CS 1.5",
        "LAB X "+xLabel,
        "LAB Y "+yLabel,
        "LAB 4 \""+plotLabel+"\" CS 1",
        "LAB 4 VPOS 0.27 0.8 JUST LEFT",
        "VIEW 0.2 0.1 0.8 0.9",
        "R X "+xMinStr+" "+xMaxStr,
        "R Y "+yMinStr+" "+yMaxStr,
        //"GRID X "+nMajorDivs+",2",
        "!"
      };
    }
    return header;
  }

  private void printToFile(final String[] header, final double[] binCentres, final double[] binHeights) {
    for (int i=0; i < header.length; i++) {
      printWriter.println(header[i]);
    }
    for (int i=0; i < binCentres.length; i++) {
      printWriter.println((binCentres[i]) +"	"+ (binHeights[i]));
    }
    printWriter.close();
  }

  private void printToFile(final String[] header, final double[] binCentres, final double[] binHeights, DecimalFormat numberFormat) {
    for (int i=0; i < header.length; i++) {
      printWriter.println(header[i]);
    }
    for (int i=0; i < binCentres.length; i++) {
      printWriter.println(numberFormat.format(binCentres[i]) +"	"+ numberFormat.format(binHeights[i]));
    }
    printWriter.close();
  }

  private void printToFile(final String[] header, final double[] binCentres, final double[] binHeights, final double[] function) {
    for (int i=0; i < header.length; i++) {
      printWriter.println(header[i]);
    }
    for (int i=0; i < binCentres.length; i++) {
      printWriter.println((binCentres[i]) +"	"+ (binHeights[i]) +"	"+ function[i]);
    }
    printWriter.close();
  }


  // Methods writeData

  public void writeData(final double[] data) throws IOException {
    int n = data.length;
    for (int i=0; i < n; i++) {
      printWriter.println(data[i]);
    }
    printWriter.close();
  }

  public void writeData(final double[] col1, final double[] col2) throws IOException {
    int n = (int) Math.min(col1.length, col2.length);
    for (int i=0; i < n; i++) {
      printWriter.println(col1[i]+"	"+col2[i]);
    }
    printWriter.close();
  }

  public void writeData(final String[] header, final double[] x, final double[] y) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int n = (int) Math.min(x.length, y.length);
    for (int i=0; i < n; i++) {
      printWriter.println((x[i]) +"	"+ (y[i]));
    }
    String name = (new StringTokenizer(this.getFile().getName(),".")).nextToken();
    printWriter.println("HARD "+name+".ps/cps");
    printWriter.println("$ file="+name+" ; epstopdf ${file}.ps");
    printWriter.close();
  }

  public void writeData(final String[] header, final double[] x, final double[] y, int startIndex) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int nbins = (int) Math.min(x.length, y.length);
    for (int i=startIndex; i < nbins; i++) {
      printWriter.println((x[i]) +"	"+ (y[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, int[] x, final double[] y) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int n = (int) Math.min(x.length, y.length);
    for (int i=0; i < n; i++) {
      printWriter.println((x[i]) +"	"+ (y[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, int[] x, int[] y) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int n = (int) Math.min(x.length, y.length);
    for (int i=0; i < n; i++) {
      printWriter.println(x[i] +"	"+ y[i]);
    }
    printWriter.close();
  }

  public void writeData(final String[] header, int[] x, final double[] y, final double[] y2) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {x.length, y.length, y2.length};
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((x[i]) +"	"+ (y[i]) +"	"+ (y2[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, int[] x, final double[] y, final double[] y2, final String[] footer) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {x.length, y.length, y2.length};
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) printWriter.println((x[i]) +"	"+ (y[i]) +"	"+ (y2[i]));
    for (int i=0; i < footer.length; i++) printWriter.println(footer[i]);
    printWriter.close();
  }

  public void writeData(final String[] header, int[] x, final double[] y, final double[] y2, final double[] y3) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {x.length, y.length, y2.length, y3.length};
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((x[i]) +"	"+ (y[i]) +"	"+ (y2[i]) +"	"+ (y3[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, int[] x, final double[] y, final double[] y2, final double[] y3, final double[] y4) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {x.length, y.length, y2.length, y3.length, y4.length};
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((x[i]) +"	"+ (y[i]) +"	"+ (y2[i]) +"	"+ (y3[i]) +"	" +(y4[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, int[] x, final double[] y, final double[] y2, final double[] y3, final double[] y4, final double[] y5) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {x.length, y.length, y2.length, y3.length, y4.length, y5.length};
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((x[i]) +"	"+ (y[i]) +"	"+ (y2[i]) +"	"+ (y3[i]) +"	" +(y4[i]) +"	" +(y5[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, int[] col1, int[] col2, final double[] col3) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {col1.length, col2.length, col3.length};
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((col1[i]) +"	"+ (col2[i]) +"	"+ (col3[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, String[] col1, int[] col2, final double[] y) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    for (int i=0; i < col1.length; i++) {
      printWriter.println(col1[i] +"	"+ (col2[i]) +"	"+ (y[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, final double[] c1, final double[] c2, final double[] c3) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {c1.length, c2.length, c3.length};
    double var = BasicStats.getVariance(lengths);
    if (var != 0) {
      logger.warn("input column data of different lengths. Using min.");
    }
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((c1[i]) +"	"+ (c2[i]) +"	"+ (c3[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, final double[] c1, final double[] c2, final double[] c3, final double[] c4) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {c1.length, c2.length, c3.length, c4.length};
    double var = BasicStats.getVariance(lengths);
    if (var != 0) {
      logger.warn("input column data of different lengths. Using min.");
    }
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((c1[i]) +"	"+ (c2[i]) +"	"+
      (c3[i]) +"	"+ (c4[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, final double[] c1, final double[] c2, final double[] c3, final double[] c4, final double[] c5) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {c1.length, c2.length, c3.length, c4.length, c5.length};
    double var = BasicStats.getVariance(lengths);
    if (var != 0) {
      logger.warn("input column data of different lengths. Using min.");
    }
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((c1[i]) +"	"+ (c2[i]) +"	"+
      (c3[i]) +"	"+ (c4[i]) +"	"+
      (c5[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, int[] c1, final double[] c2, final double[] c3, final double[] c4, int[] c5) 	throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {c1.length, c2.length, c3.length, c4.length, c5.length};
    double var = BasicStats.getVariance(lengths);
    if (var != 0) {
      logger.warn("input column data of different lengths. Using min.");
    }
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((c1[i]) +"	"+ (c2[i]) +"	"+
      (c3[i]) +"	"+ (c4[i]) +"	"+
      (c5[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, final double[] c1, final double[] c2, final double[] c3, final double[] c4, final double[] c5, final double[] c6) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {c1.length, c2.length, c3.length, c4.length, c5.length, c6.length};
    double var = BasicStats.getVariance(lengths);
    if (var != 0) {
      logger.warn("input column data of different lengths. Using min.");
    }
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((c1[i]) +"	"+ (c2[i]) +"	"+
      (c3[i]) +"	"+ (c4[i]) +"	"+
      (c5[i]) +"	"+ (c6[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, final double[] c1, final double[] c2, final double[] c3, final double[] c4, final double[] c5, final double[] c6, final double[] c7) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {c1.length, c2.length, c3.length, c4.length, c5.length, c6.length, c7.length};
    double var = BasicStats.getVariance(lengths);
    if (var != 0) {
      logger.warn("input column data of different lengths. Using min.");
    }
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((c1[i]) +"	"+ (c2[i]) +"	"+
      (c3[i]) +"	"+ (c4[i]) +"	"+
      (c5[i]) +"	"+ (c6[i]) +"	"+
      (c7[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, final double[] c1, final double[] c2, final double[] c3, final double[] c4, final double[] c5, final double[] c6, final double[] c7, final double[] c8) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {c1.length, c2.length, c3.length, c4.length, c5.length, c6.length, c7.length, c8.length};
    double var = BasicStats.getVariance(lengths);
    if (var != 0) {
      logger.warn("input column data of different lengths. Using min.");
    }
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((c1[i]) +"	"+ (c2[i]) +"	"+
      (c3[i]) +"	"+ (c4[i]) +"	"+
      (c5[i]) +"	"+ (c6[i]) +"	"+
      (c7[i]) +"	"+ (c8[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, final double[] c1, final double[] c2, final double[] c3, final double[] c4, final double[] c5, final double[] c6, final double[] c7, final double[] c8, final double[] c9) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {c1.length, c2.length, c3.length, c4.length, c5.length, c6.length, c7.length, c8.length, c9.length};
    double var = BasicStats.getVariance(lengths);
    if (var != 0) {
      logger.warn("input column data of different lengths. Using min.");
    }
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((c1[i]) +"	"+ (c2[i]) +"	"+
      (c3[i]) +"	"+ (c4[i]) +"	"+
      (c5[i]) +"	"+ (c6[i]) +"	"+
      (c7[i]) +"	"+ (c8[i]) +"	"+
      (c9[i]));
    }
    printWriter.close();
  }
  public void writeData(final String[] header, final double[] c1, final double[] c2, final double[] c3, final double[] c4, final double[] c5, final double[] c6, final double[] c7, final double[] c8, final double[] c9, final double[] c10) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {c1.length, c2.length, c3.length, c4.length, c5.length, c6.length, c7.length, c8.length, c9.length, c10.length};
    double var = BasicStats.getVariance(lengths);
    if (var != 0) {
      logger.warn("input column data of different lengths. Using min.");
    }
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((c1[i]) +"	"+ (c2[i]) +"	"+
      (c3[i]) +"	"+ (c4[i]) +"	"+
      (c5[i]) +"	"+ (c6[i]) +"	"+
      (c7[i]) +"	"+ (c8[i]) +"	"+
      (c9[i]) +"	"+ (c10[i]));
    }
    printWriter.close();
  }

  public void writeData(final String[] header, final double[] c1, final double[] c2, final double[] c3, final double[] c4, final double[] c5, final double[] c6, final double[] c7, final double[] c8, final double[] c9, final double[] c10, final double[] c11) throws IOException {
    for (int i=0; i < header.length; i++)  printWriter.println(header[i]);
    int lengths[] = new int[] {c1.length, c2.length, c3.length, c4.length, c5.length, c6.length, c7.length, c8.length, c9.length, c10.length, c11.length};
    double var = BasicStats.getVariance(lengths);
    if (var != 0) {
      logger.warn("input column data of different lengths. Using min.");
    }
    int nbins = (int) MinMax.getMin(lengths);
    for (int i=0; i < nbins; i++) {
      printWriter.println((c1[i]) +"	"+ (c2[i]) +"	"+
      (c3[i]) +"	"+ (c4[i]) +"	"+
      (c5[i]) +"	"+ (c6[i]) +"	"+
      (c7[i]) +"	"+ (c8[i]) +"	"+
      (c9[i]) +"	"+ (c10[i]) +"	"+
      (c11[i]));
    }
    printWriter.close();
  }

}
