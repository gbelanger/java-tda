package gb.tda.binner;

import hep.aida.IAnalysisFactory;
import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.ITree;
import java.util.Arrays;
import org.apache.log4j.Logger;

import gb.tda.tools.MinMax;

/**

The final class <code>Binner</code> defines the methods to bin data
and make histograms.

@author <a href="mailto: guilaume.belanger@esa.int">Guillaume Belanger</a>, ESA/ESAC.
@created 2010 July
@version 2021 April

Last modified:
  2021 April: Included getMin and getMax methods to remove dependency on gb.codetda.tools
  2017 January : Minor updates

**/

public final class Binner {

  private static Logger logger = Logger.getLogger(Binner.class);

  public static double[][] binData(double[] data, int nBins) {
    double[] copyOfData = Arrays.copyOf(data, data.length);
    Arrays.sort(copyOfData);
    double xMin = copyOfData[0] - 1e-13;
    double xMax = copyOfData[copyOfData.length-1] + 1e-13;
    return binData(data, xMin, xMax, nBins);
  }

  public static double[][] binData(double[] data, double xmin, double xmax, int nBins) {
    IHistogram1D histo = makeHisto(data, xmin, xmax, nBins);
    IAxis axis = histo.axis();
    double[] entriesPerBin = new double[nBins];
    double[] binEdges = new double[2*nBins];
    for (int i=0; i < nBins; i++) {
      entriesPerBin[i] = histo.binHeight(i);
      binEdges[2*i] = axis.binLowerEdge(i);
      binEdges[2*i+1] = axis.binUpperEdge(i);
    }
    return new double[][]{entriesPerBin, binEdges};
  }

  public static double[][] binPhases(double[] phases, int nPhaseBins) {
    return binData(phases, 0, 1, nPhaseBins);
  }

  public static double[] binData(double[] data, double[] binEdges) {
    double[] copyOfData = Arrays.copyOf(data, data.length);
    Arrays.sort(copyOfData);
    int n = copyOfData.length;
    int k=0;
    int nBins = binEdges.length/2;
    double[] entriesPerBin = new double[nBins];
    for (int i=0; i < nBins; i++) {
      int counts = 0;
      double leftEdge = binEdges[2*i];
      double rightEdge = binEdges[2*i+1];
      if (copyOfData[k] < leftEdge) {
        while (k < n && copyOfData[k] < leftEdge) k++;
      }
      while (k < n && copyOfData[k] <= (rightEdge+1e-13)) {
        counts++;
        k++;
      }
      entriesPerBin[i] = counts;
    }
    return entriesPerBin;
  }

  public static double[][] binSNRData(double[] dataSNRs, double[] dataTimes,  double[] binEdges) {
    int n = dataSNRs.length;
    int k=0;
    int nBins = binEdges.length/2;
    double[] entriesPerBin = new double[nBins];
    double[] combSNRs = new double[nBins];
    for (int i=0; i < nBins; i++) {
      int entries = 0;
      double quadSum = 0;
      double leftEdge = binEdges[2*i];
      double rightEdge = binEdges[2*i+1];
      //logger.info("leftEdge="+leftEdge+"  rightEdge="+rightEdge);
      if (k < n && dataTimes[k] < leftEdge) {
        while (k < n && dataTimes[k] < leftEdge) {
          //logger.info("Skipping: k="+k+", dataTimes[k]="+dataTimes[k]+" < leftEdge="+leftEdge);
          k++;
        }
      }
      while (k < n && dataTimes[k] <= rightEdge) {
        quadSum += Math.pow(dataSNRs[k],2);
        entries++;
        //logger.info("Adding: k="+k+", dataTimes[k]="+dataTimes[k]+" < rightEdge="+rightEdge);
        //logger.info("quadSum = "+quadSum+" (entries = "+entries+")");
        k++;
      }
      entriesPerBin[i] = entries;
      combSNRs[i] = Math.sqrt(quadSum);
      //System.out.println(((leftEdge+rightEdge)/2)+"	"+combSNRs[i]);
    }
    return new double[][] { combSNRs, entriesPerBin };
  }

  public static IHistogram1D makeHisto(double[] data, double xmin, double xmax, int nBins) {
    IAnalysisFactory af = IAnalysisFactory.create();
    ITree tree = af.createTreeFactory().create();
    IHistogramFactory hf = af.createHistogramFactory(tree);
    IHistogram1D histo = hf.createHistogram1D("histo", nBins, xmin, xmax);
    double weight = 0;
    for (int i=0; i < data.length; i++) {
      if (Double.isNaN(data[i]) || data[i] == 0.0) {
        weight = 0;
      }
      else {
        weight = 1;
      }
      histo.fill(data[i], weight);
    }
    return histo;
  }

  public static IHistogram1D makeHisto(float[] data, double xmin, double xmax, int nBins) {
    IAnalysisFactory af = IAnalysisFactory.create();
    ITree tree = af.createTreeFactory().create();
    IHistogramFactory hf = af.createHistogramFactory(tree);
    IHistogram1D histo = hf.createHistogram1D("histo", nBins, xmin, xmax);
    double weight = 0;
    for (int i=0; i < data.length; i++) {
      if (Double.isNaN(data[i]) || data[i] == 0.0) {
        weight = 0;
      }
      else {
        weight = 1;
      }
      histo.fill(data[i], weight);
    }
    return histo;
  }

  public static IHistogram1D makeHisto(int[] data, double xmin, double xmax, int nBins) {
    IAnalysisFactory af = IAnalysisFactory.create();
    ITree tree = af.createTreeFactory().create();
    IHistogramFactory hf = af.createHistogramFactory(tree);
    IHistogram1D histo = hf.createHistogram1D("histo", nBins, xmin, xmax);
    double weight = 0;
    for (int i=0; i < data.length; i++) {
      if (Double.isNaN(data[i]) || data[i] == 0.0) {
        weight = 0;
      }
      else {
        weight = 1;
      }
      histo.fill(data[i], weight);
    }
    return histo;
  }

  public static IHistogram1D makeHisto(double[] data) {
    double min = MinMax.getMin(data);
    double max = MinMax.getMax(data);
    int nBins = data.length;
    return makeHisto(data, min, max, nBins);
  }
  public static IHistogram1D makeHisto(float[] data) {
    double min = MinMax.getMin(data);
    double max = MinMax.getMax(data);
    int nBins = data.length;
    return makeHisto(data, min, max, nBins);
  }

  public static IHistogram1D makeHisto(double[] data, int nBins) {
    double min = MinMax.getMin(data);
    double max = MinMax.getMax(data);
    return makeHisto(data, min, max, nBins);
  }
  public static IHistogram1D makeHisto(float[] data, int nBins) {
    double min = MinMax.getMin(data);
    double max = MinMax.getMax(data);
    return makeHisto(data, min, max, nBins);
  }

  public static IHistogram1D makePDF(double[] data, int nBins) {
    double min = MinMax.getMin(data);
    double max = MinMax.getMax(data);
    IHistogram1D pdf = makeHisto(data, min, max, nBins);
    double sum = pdf.sumBinHeights() * pdf.axis().binWidth(0);
    pdf.scale(1d/sum);
    return pdf;
  }

  public static IHistogram1D makePDF(double[] data, double xmin, double xmax, int nBins) {
    IHistogram1D pdf = makeHisto(data, xmin, xmax, nBins);
    double sum = pdf.sumBinHeights() * pdf.axis().binWidth(0);
    pdf.scale(1d/sum);
    return pdf;
  }

  public static double getMax(double[] data) {
    double max = -Double.MAX_VALUE;
    for (int i=0; i < data.length; i++) {
        if (!Double.isNaN(data[i])) max = Math.max(max, data[i]);
    }
    if (max == -Double.MAX_VALUE) max = Double.NaN;
    return max;
  }
    
  public static float getMax(float[] data) {
    float max = -Float.MAX_VALUE;
    for (int i=0; i < data.length; i++) {
        if (!Float.isNaN(data[i])) max = Math.max(max, data[i]);
    }
    if (max == -Float.MAX_VALUE) max = Float.NaN;
    return max;
  }

  public static double getMin(double[] data) {
    double min = Double.MAX_VALUE;
    for (int i=0; i < data.length; i++) {
        if (!Double.isNaN(data[i])) {
          min = Math.min(min, data[i]);
        }
    }
    return min;
  }
        
  public static float getMin(float[] data) {
    float min = Float.MAX_VALUE;
    for (int i=0; i < data.length; i++)
      if (!Float.isNaN(data[i])) min = Math.min(min, data[i]);
    return min;
  }
      

}

// Algorithm for unordered data into adjacent bins

// 	double binwidth = (max - min)/(new Double(nbins)).doubleValue();
// 	double[] binnedData = new double[nbins];
// 	double valueOverBinwidth = 0;
// 	int binNum = 0;
// 	for (int i=0; i < data.length; i++) {
// 	    if (data[i] >= min && data[i] <= max) {
// 		valueOverBinwidth = (data[i] - min)/binwidth;
// 		binNum = (int) Math.floor(valueOverBinwidth);
// 		if (binNum == nbins)  binNum--;
// 		binnedData[binNum]++;
// 	    }
// 	}
