package gb.tda.aida;

import hep.aida.ref.function.AbstractIFunction;

import cern.jet.stat.Gamma;
import cern.jet.random.ChiSquare;
import cern.jet.random.engine.MersenneTwister64;

public class ChiSquareFunction extends AbstractIFunction {

    public static int dims = 1;
    public static int nPars = 2;
    
    public ChiSquareFunction() {
        this("");
    }
    
    public ChiSquareFunction(String title) {
        super(title, dims, nPars);
    }
    
    public ChiSquareFunction(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {

	double dof = p[0];
	double norm = p[1];
	double dofOverTwo = dof/2;
	double gamma = Gamma.gamma(dofOverTwo);
	double chi2 = norm*Math.pow(0.5, dofOverTwo)/gamma * 
	    Math.pow(v[0], dofOverTwo -1) * Math.exp(-v[0]/2);
	return chi2;
    }
    
    // Here change the parameter names
    protected void init(String title) {
	parameterNames[0] = "dof";
	parameterNames[1] = "norm";
	super.init(title);
    }
}
