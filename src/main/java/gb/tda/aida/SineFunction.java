package gb.tda.aida;

import hep.aida.ref.function.AbstractIFunction;

public class SineFunction extends AbstractIFunction {

    public static int dims = 1;
    public static int nPars = 4;
    
    public SineFunction() {
        this("");
    }
    
    public SineFunction(String title) {
        super(title, dims, nPars);
    }
    
    public SineFunction(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
    	double period = (2*Math.PI)/p[0];
    	double phase = p[1];
    	double amplitude = p[2];
    	double slope = p[3];
        double intercept = p[4];
    	double sine = amplitude * Math.sin(period*(v[0] - phase)) + slope*v[0] + intercept;
    	return sine;
    }
    
    // Define parameter names
    protected void init(String title) {
    	parameterNames[0] = "period";
    	parameterNames[1] = "phase";
    	parameterNames[2] = "amplitude";
    	parameterNames[3] = "slope";
        parameterNames[4] = "intercept";
    	super.init(title);
    }
}
