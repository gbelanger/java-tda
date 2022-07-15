package gb.tda.aida;

import hep.aida.ref.function.AbstractIFunction;

public class BeltEntryAltitudeFunction extends AbstractIFunction {

    public static int dims = 1;
    public static int nPars = 7;

    public BeltEntryAltitudeFunction() {
        this("");
    }

    public BeltEntryAltitudeFunction(String title) {
	super(title, dims, nPars);
    }

    public BeltEntryAltitudeFunction(String title, double[] defaultParValues) {
	super(title, dims, nPars);
 	this.setParameters(defaultParValues);
    }

    public BeltEntryAltitudeFunction(String[] variableNames, String[] parameterNames) {
	super(variableNames, parameterNames);
    }

    public BeltEntryAltitudeFunction(String title, String[] variableNames, String[] parameterNames) {
	super(title, variableNames, parameterNames);
    }

    public double value(double[] var) {
    	double yOffset = p[0];
    	double period = p[1];
    	double xOffset = p[2];
    	double amplitude = p[3];
	double period2 = p[4];
	double xOffset2 = p[5];
	double amplitude2 = p[6];
	double longTermTrend = yOffset + amplitude * Math.sin(2*Math.PI*(var[0] - xOffset)/period);
	double shortTermTrend = amplitude2 * Math.sin(2*Math.PI*(var[0] - xOffset2)/period2);;
	return longTermTrend + shortTermTrend;
    }

    protected void init(String title) {
	String[] parNames = new String[] {
	    "yOffset",
	    "period",
	    "xOffset",
	    "amplitude",
	    "period2",
	    "xOffset2",
	    "amplitude2"
	};
	for (int i=0; i < nPars; i++) {
	    parameterNames[i] = parNames[i];
	}
	super.init(title);
    }

}
