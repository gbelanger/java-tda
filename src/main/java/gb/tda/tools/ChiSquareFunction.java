package gb.tda.tools;

import cern.jet.stat.Gamma;
import cern.jet.random.ChiSquare;
import cern.jet.random.engine.MersenneTwister64;
import hep.aida.ref.function.AbstractIFunction;



/**
 *
 * @version  August 2010 (last modified)
 * @author   Guillaume Belanger (ESAC, Spain)
 *
 **/

public class ChiSquareFunction extends AbstractIFunction {
    
    public ChiSquareFunction() {
        this("");
    }
    
    public ChiSquareFunction(String title) {
        super(title, 1, 1);
    }
    
    public ChiSquareFunction(String[] variableNames, String[] parameterNames) {
        super(variableNames, parameterNames);
    }
    
    public double value(double[] v) {
	
 	MersenneTwister64 randEngine = new MersenneTwister64(new java.util.Date());
	double dof = p[0];
	ChiSquare chiSquare = new ChiSquare(dof, randEngine);
	double chi2_cern = chiSquare.pdf(v[0]);
	
	double dofOverTwo = dof/2;
	double gamma = Gamma.gamma(dofOverTwo);
	double chi2 = Math.pow(0.5, dofOverTwo)/gamma * Math.pow(v[0], dofOverTwo -1) * Math.exp(-v[0]/2);
	//System.out.println("chi2_cern = "+chi2_cern+"	 chi2 = "+chi2);

        return chi2;
    }
    
    // Here change the parameter names
    protected void init(String title) {
	for (int i=0; i<parameterNames.length; i++) { 
	    parameterNames[i] = "chi2Param"+i;
	}
	super.init(title);
    }
}
