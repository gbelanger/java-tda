package gb.tda.aida;

import hep.aida.ref.function.AbstractIFunction;

public class RadiationBeltExitAltitudeFunction extends AbstractIFunction {

    public static int dims = 1;
    // three sine waves 
    //public static int nPars = 10;
    // line and two sine waves
    public static int nPars = 8;

    public RadiationBeltExitAltitudeFunction(String title) {
	super(title, dims, nPars);
    }

    public RadiationBeltExitAltitudeFunction(String[] variableNames, String[] parameterNames) {
	super(variableNames, parameterNames);
    }

    public RadiationBeltExitAltitudeFunction(String title, String[] variableNames, String[] parameterNames) {
	super(title, variableNames, parameterNames);
    }

    public double value(double[] var) {
    	//  Define variables from parameters
    	//// Global yOffset and slope
    	double global_yOffset = p[0];
	double global_slope = p[1];
    	//// short term sine wave
    	double shortTerm_period = p[2];
    	double shortTerm_xOffset = p[3];
    	double shortTerm_amplitude = p[4];
    	//// mid term since wave
    	double midTerm_period = p[5];
    	double midTerm_xOffset = p[6];  // in [0,1]
    	double midTerm_amplitude = p[7];
    	//// Calculate the function value
    	double twoPI = 2*Math.PI;
	double globalTrend = global_yOffset + global_slope*var[0];
    	double shortTerm = shortTerm_amplitude * Math.sin(twoPI*(var[0] - shortTerm_xOffset)/shortTerm_period);
    	double midTerm = midTerm_amplitude * Math.sin(twoPI*(var[0] - midTerm_xOffset)/midTerm_period);
    	double sum = globalTrend + shortTerm + midTerm;
	//System.out.println(var[0]+"	"+sum);
    	return sum;
    }

    //  Version with line and two sine waves
    protected void init(String title) {
	String[] parNames = new String[] {
	    "global_yOffset",
	    "global_slope",
	    "shortTerm_period",
	    "shortTerm_xOffset",
	    "shortTerm_amplitude",
	    "midTerm_period",
	    "midTerm_xOffset",
	    "midTerm_amplitude",
	};
	for (int i=0; i < nPars; i++) {
	    parameterNames[i] = parNames[i];
	}
	super.init(title);
 	double[] defaultParValues = new double[] {
	    41500,
	    2.38e-5,
	    3.e7, 6.13e7, -3814, 
	    9.16e7, 1.75e7, -1151
	};
 	setParameters(defaultParValues);
    }

    //// Version with three sine waves
    //
    // public double value(double[] var) {
    // 	//  Define variables from parameters
    // 	//// Global y-scale
    // 	double global_yOffset = p[0];
    // 	//// long term
    // 	double shortTerm_period = p[1];
    // 	double shortTerm_xOffset = p[2];
    // 	double shortTerm_amplitude = p[3];
    // 	//// mid term
    // 	double longTerm_period = p[4];
    // 	double longTerm_xOffset = p[5];
    // 	double longTerm_amplitude = p[6]; 
    // 	//// short term
    // 	double midTerm_period = p[7];
    // 	double midTerm_xOffset = p[8];  // in [0,1]
    // 	double midTerm_amplitude = p[9];
    // 	//// Calculate the function value
    // 	double twoPI = 2*Math.PI;
    // 	double shortTerm = shortTerm_amplitude * Math.sin(twoPI*(var[0] - shortTerm_xOffset)/shortTerm_period);
    // 	double longTerm = longTerm_amplitude * Math.sin(twoPI*(var[0] - longTerm_xOffset)/longTerm_period);
    // 	double midTerm = midTerm_amplitude * Math.sin(twoPI*(var[0] - midTerm_xOffset)/midTerm_period);
    // 	double sum = global_yOffset + shortTerm + longTerm + midTerm;
    // 	//System.out.println(var[0]+"	"+(global_yOffset+shortTerm)+"	"+(global_yOffset+longTerm)+"	"+(global_yOffset+midTerm)+"	"+sum);
    // 	return sum;
    // }

    ////  Version with three sine waves
    //
    // protected void init(String title) {
    // 	String[] parNames = new String[] {
    // 	    "global_yOffset",
    // 	    "shortTerm_period",
    // 	    "shortTerm_xOffset",
    // 	    "shortTerm_amplitude",
    // 	    "longTerm_period",
    // 	    "longTerm_xOffset",
    // 	    "longTerm_amplitude",
    // 	    "midTerm_period",
    // 	    "midTerm_xOffset",
    // 	    "midTerm_amplitude",
    // 	};
    // 	for (int i=0; i < nPars; i++) {
    // 	    parameterNames[i] = parNames[i];
    // 	}
    // 	super.init(title);
    // 	double[] defaultParValues = new double[] {
    // 	    48288,
    // 	    3.e7, 6.13e7, -3814, 
    // 	    4.01e9, -3.73e9, 14667,
    // 	    9.16e7, 1.75e7, -1151
    // 	};
    // 	setParameters(defaultParValues);
    // }

}
