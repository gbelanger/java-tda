import java.util.Random;
import hep.aida.IAnalysisFactory;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.IHistogramFactory;
import hep.aida.IFitFactory;
import hep.aida.IFitResult;
import hep.aida.IFitter;
import hep.aida.IFunction;
import hep.aida.IFunctionFactory;
import hep.aida.IPlotter;
import hep.aida.ITree;
import hep.aida.IHistogram1D;
import cern.jet.random.ChiSquare;
import cern.jet.random.engine.MersenneTwister64;

public class TestChiSquareFunction {
    
    public static void main(String[] args) throws Exception {
	
	// Create AIDA tree, factories and IFitter
	IAnalysisFactory af     = IAnalysisFactory.create();
	ITree tree = af.createTreeFactory().create("FunctionTree.xml", "xml", false, true, "compress=no");
	IDataPointSetFactory dpsf   = af.createDataPointSetFactory(tree);
	IHistogramFactory hf = af.createHistogramFactory(tree);
	IFunctionFactory funcF  = af.createFunctionFactory(tree);
	IFitFactory fitF   = af.createFitFactory();
	//IFitter fitter = fitF.createFitter("Chi2","jminuit", "noClone=true");
	IFitter fitter = fitF.createFitter("Chi2","jminuit", "");
	
	// Add UserFunction to the IFunctionCatalog
	IFunction f0 = new ChiSquareFunction("Chi Square Function");
	//funcF.catalog().add("ChiSquareFunction", f0);
	//System.out.println("ChiSquareFunction Codelet: "+f0.codeletString());
	
	// Print all the functions in the IFunctionCatalog
	//String[] list = funcF.catalog().list();
	//for (int i=0; i<list.length; i++)
	//    System.out.println(i+"\t "+list[i]);
	       

	//  Create and fill histo
	int nbins = 50;
	double min =  0;
	double max = 20;
	IHistogram1D histo = hf.createHistogram1D("Histo", 50, 0, 20);
	MersenneTwister64 eng = new MersenneTwister64();
	double dof = 2;
	ChiSquare chi2 = new ChiSquare(dof, eng);
	int n = 10000;
	for (int i=0; i < n; i++) {
	    histo.fill(chi2.nextDouble());
	}
	
	// Now start using IFunctionCatalog
	//IFunction function = funcF.createFunctionByName("User Function", "ChiSquareFunction");
	double norm = n*(max-min)/nbins;
	f0.setParameter("dof", dof);
	f0.setParameter("norm", norm);
	
	
	// Do fit
	System.out.println(f0.variableName(0));
	IFitResult result = fitter.fit(histo, f0);
	System.out.println("Chi2="+result.quality());
	
	// Optional plot
	IPlotter plotter = af.createPlotterFactory().create("TestChiSquareFunction.java plot");
	plotter.region(0).style().statisticsBoxStyle().setVisible(true);
// 	plotter.region(0).plot(dataPointSet);
	plotter.region(0).plot(histo);
	plotter.region(0).plot(result.fittedFunction());
	plotter.show();
	
	}
    }

