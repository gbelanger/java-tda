package gb.codetda.aida.test;

import java.util.Date;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister64;
import hep.aida.IAnalysisFactory;
import hep.aida.IAxis;
import hep.aida.IFitData;
import hep.aida.IFitFactory;
import hep.aida.IFitResult;
import hep.aida.IFitter;
import hep.aida.IFunctionFactory;
import hep.aida.IHistogramFactory;
import hep.aida.IPlotter;
import hep.aida.ITree;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.histogram.Histogram2D;
import hep.aida.ref.histogram.VariableAxis;


public class HistoTest
{
   public static void main(String[] argv)
   {
      IAnalysisFactory af = IAnalysisFactory.create();
      ITree tree = af.createTreeFactory().create();
      IHistogramFactory hf = af.createHistogramFactory(tree);
      IFunctionFactory funcf = af.createFunctionFactory(tree);
      IFitFactory fitf = af.createFitFactory();
     
//       IHistogram1D h1d = hf.createHistogram1D("test 1d",50,-4,4);
//       IHistogram2D h2d = hf.createHistogram2D("test 2d",50,-4,4,50,-4,4);

      double[] edges = new double[101];
      for (int i=0; i < 101; i++) edges[i] = -50 + i;
      //FixedAxis xaxis = new FixedAxis(50, -4, 4);
      //FixedAxis yaxis = new FixedAxis(50, -4, 4);
      VariableAxis xaxis = new VariableAxis(edges);
      VariableAxis yaxis = new VariableAxis(edges);
      Histogram1D h1d = new Histogram1D("histo1d", "Histo 1D", xaxis);
      Histogram2D h2d = new Histogram2D("histo2d", "Histo 2D", xaxis, yaxis);

      MersenneTwister64 r = new MersenneTwister64(new java.util.Date());
      Normal rGauss1 = new Normal(0, 10, r);
      Normal rGauss2 = new Normal(-20, 10, r);
      Normal rGauss3 = new Normal(20, 10, r);
      for (int i=0; i < 1000; i++) 
      {
         h1d.fill(rGauss1.nextDouble());
         h2d.fill(rGauss2.nextDouble(),rGauss2.nextDouble());
         h2d.fill(rGauss3.nextDouble(),rGauss3.nextDouble());
      }

      
      Histogram1D h1d_copy = new Histogram1D("histo1d_copy", "Histo 1D - copy", xaxis);
      int nbins = xaxis.bins() + 2;
      double[] heights = new double[nbins];
      double[]  errors = new double[nbins];
      int[]       entries = new int[nbins];
      double[]  means = new double[nbins];
      double[]     rmss = new double[nbins];
      for (int i=IAxis.UNDERFLOW_BIN; i < xaxis.bins()-2; i++) {
	  heights[i+2] = h1d.binHeight(i+1);
	  errors[i+2]  = h1d.binError(i+1);
	  entries[i+2] = h1d.binEntries(i+1);
	  means[i+2]  = h1d.binMean(i+1);
	  rmss[i+2]     = h1d.binRms(i+1);
      }
      //  Fill underflow and overflow bins
      //  external representation (from -2 to nBins-1 where -2 is the overflow and -1 is the underflow)
      //  internal one (from 0 to nBins+1 where 0 is the underflow and nBins+1 if the overflow bin)
      heights[0] = h1d.binHeight(-1);
      heights[xaxis.bins()+1] = h1d.binHeight(-2);
      errors[0] = h1d.binError(-1);
      errors[xaxis.bins()+1] = h1d.binError(-2);

      //  Set contens of histogram copy
      //  Method setContents cannot be used on a histogram constructed with a FixedAxis
      h1d_copy.setContents(heights, errors, null, null, null); // entries, means, rmss);
      

      IFitter fitter = fitf.createFitter("chi2");
      IFitData fitData = fitf.createFitData();
      fitData.create2DConnection(h2d);
      fitData.range(0).excludeAll();
      fitData.range(0).include(-45, 10);
      fitData.range(1).include(-50, 10);
      IFitResult fitResult2 = fitter.fit(fitData,"g2");

      IFitResult fitResult1 = fitter.fit(h1d_copy, "g");
      ///IFitResult fitResult2 = fitter.fit(h2d, "g2");
      String[] paramNames = fitResult2.fittedParameterNames();
      double[] paramValues = fitResult2.fittedParameters();

      System.out.println("Fit status = "+fitResult2.fitStatus());
      for (int i=0; i < paramNames.length; i++) 
	  System.out.println(paramNames[i]+" = "+paramValues[i]);
      System.out.println("Quality = "+fitResult2.quality());

      IPlotter plotter = af.createPlotterFactory().create("Plot");
      plotter.createRegions(1,2,0);
      plotter.region(0).plot(h1d_copy);
      plotter.region(0).plot(fitResult1.fittedFunction());
      plotter.region(1).plot(h2d);
      plotter.show();
   }
}
