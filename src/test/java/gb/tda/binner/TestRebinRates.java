package gb.esac.test;

import gb.esac.tools.Binner;



public class TestRebinRates {

    public static void main (String[] args) {


	double oldBinTime = Double.valueOf(args[0]).doubleValue();
	double newBinTime = Double.valueOf(args[1]).doubleValue();


	//  Construct the arrays
	int nbins = 100;
	double[][] rates = new double[nbins][2];
	for (int i=0; i < nbins; i++) {
	    rates[i][0] = 5;
	    rates[i][1] = 1;
	}

	//  Rebin
	double[][] rebRates = Binner.rebinRates(rates, oldBinTime, newBinTime);



    }

}
