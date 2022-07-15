package gb.tda.aida;

import hep.aida.ref.function.AbstractIFunction;

public class SimpleRadiationBeltAltitudeFunction extends AbstractIFunction {

    public static int dims = 1;
    public static int nPars = 4;

    public SimpleRadiationBeltAltitudeFunction() {
        this("");
    }

    public SimpleRadiationBeltAltitudeFunction(String title) {
	super(title, dims, nPars);
    }

    public SimpleRadiationBeltAltitudeFunction(String title, double[] defaultParValues) {
	super(title, dims, nPars);
 	this.setParameters(defaultParValues);
    }

    public SimpleRadiationBeltAltitudeFunction(String[] variableNames, String[] parameterNames) {
	super(variableNames, parameterNames);
    }

    public SimpleRadiationBeltAltitudeFunction(String title, String[] variableNames, String[] parameterNames) {
	super(title, variableNames, parameterNames);
    }

    public double value(double[] var) {
    	double yOffset = p[0];
    	double period = p[1];
    	double xOffset = p[2];
    	double amplitude = p[3];
    	return amplitude * Math.sin(2*Math.PI*(var[0] - xOffset)/period) + yOffset;
    }

    protected void init(String title) {
	String[] parNames = new String[] {
	    "yOffset",
	    "period",
	    "xOffset",
	    "amplitude",
	};
	for (int i=0; i < nPars; i++) {
	    parameterNames[i] = parNames[i];
	}
	super.init(title);
    }

}
