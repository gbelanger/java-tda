import java.util.Date;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister64;
import hep.aida.IFunction;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.ITree;
import hep.aida.IAnalysisFactory;
import hep.aida.IFitData;
import hep.aida.IFitter;
import hep.aida.IFitFactory;
import hep.aida.IFitResult;
//import gb.codetda.io.AsciiDataFileWriter;

public class TestFitting {

    public static void main(String[] args) throws Exception {

	// Define simple sine function
	IFunction function = new SineFunction("sine");
	String[] parNames = new String[] {"yOffset", "period", "xOffset", "amplitude"};
	double yOffset = 0;
	function.setParameter(parNames[0], yOffset);
	double period = 1;
	function.setParameter(parNames[1], period);
	double xOffset = 0;
	function.setParameter(parNames[2], xOffset);
	double amplitude = 1;
	function.setParameter(parNames[3], amplitude);
	
	//  Generate data scattered around simple sine function
	IAnalysisFactory af = IAnalysisFactory.create();
	ITree tree = af.createTreeFactory().create();
	IDataPointSetFactory dpsf = af.createDataPointSetFactory(tree);
 	IDataPointSet dps = dpsf.create("dps", "Sine", 2);
	int n = 100;
	double step = period/n;
	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
	double stdDev = 0.4;
	Normal scatter = new Normal(0, stdDev, engine);
	double[] xValues = new double[n];
	double[] yValues = new double[n];
	double[] funcValues = new double[n];
	for ( int i = 0; i < n; i++ ) {
	    dps.addPoint();
	    double x = xOffset + i*step;
	    double func = function.value(new double[] {x});
	    double y = func + scatter.nextDouble();
	    dps.point(i).coordinate(0).setValue(x);
	    dps.point(i).coordinate(1).setValue(y);
	    xValues[i] = x;
	    yValues[i] = y;
	    funcValues[i] = func;
	}
	
	//  Fit the data
  	IFitFactory fitF = af.createFitFactory();
	IFitter fitter = fitF.createFitter("leastsquares", "jminuit");
 	IFitData data = fitF.createFitData();
	int xCoordIndex = 0;
	int yCoordIndex = 1;
 	data.create1DConnection(dps, xCoordIndex, yCoordIndex);

	//  Constrain the fit
	// fitter.fitParameterSettings("yOffset").setBounds(-0.5, 0.5);
	// fitter.fitParameterSettings("period").setBounds(0.1, 1.9);
	// fitter.fitParameterSettings("xOffset").setBounds(-1, 1);
	// fitter.fitParameterSettings("amplitude").setBounds(0.1, 1.9);

	// Do the fit and print out the results
	IFitResult fitResult = fitter.fit(data, function);
	IFunction fittedFunction = fitResult.fittedFunction();
	double[] fittedParValues = fitResult.fittedParameters();
	System.out.println("\nFit result:");
	System.out.println("  Valid: "+fitResult.isValid());
	System.out.println("  Status: "+fitResult.fitStatus());
	System.out.println("  Quality: "+fitResult.quality());
	System.out.println("\nFitted values:");	    
	for ( int i=0; i < fittedParValues.length; i++ ) {
	    System.out.println("  "+parNames[i]+" = "+fittedParValues[i]);
	}
	System.out.println();
	double[] fittedModel = new double[n];
	double[] initialModel = new double[n];
	for ( int i=0; i < n; i++ ) {
	    initialModel[i] = function.value(new double[] {xValues[i]});
	    fittedModel[i] = fittedFunction.value(new double[] {xValues[i]});
	}

	//  Plot the result for visual check
	AsciiDataFileWriter sinePlot = new AsciiDataFileWriter("testFitting.qdp");
	String[] header = sinePlot.makeHeader("X","Y with "+stdDev+" (rms) normal scatter");
	sinePlot.writeData(header, xValues, yValues, initialModel, fittedModel);

    }
    
}
