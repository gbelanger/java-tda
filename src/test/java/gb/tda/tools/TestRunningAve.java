package gb.esac.test;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.*;

import gb.esac.tools.Stats;

public class TestRunningAve {

    public static void main (String[] args) {

	int n = 0;
	if ( args.length != 1 ) {
	    System.out.println("Usage: java TestRunningAve nNumbers");
	    System.exit(-1);
	}
	else n = (Integer.valueOf(args[0])).intValue();

	MersenneTwister64 eng = new MersenneTwister64(new java.util.Date());
	Normal randGauss = new Normal(10, 2, eng);
	double[] numbers = new double[n];
	System.out.println("Log  : Generating numbers");
	for ( int i=0; i < n; i++ ) {
	    numbers[i] = randGauss.nextDouble();
	}

	System.out.println("Log  : Calculating 2-pass ave and var");
	double ave = Stats.getMean(numbers);
	double var = Stats.getVariance(numbers);
	System.out.println("Log  : ave = "+ave +"\t var = "+var);
	    
	System.out.println("Log  : Calculating 1-pass ave and var");
	double[] runAveAndVar = Stats.getRunningAveAndVar(numbers);
	double runAve = runAveAndVar[0];
	double runVar = runAveAndVar[1];
	System.out.println("Log  : runAve = "+runAve +"\t runVar = "+runVar);

    }
}