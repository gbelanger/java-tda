package gb.tda.likelihood;

public class LogNormalLikelihood extends TwoParameterLikelihood {

    public double pdfValue(double mu, double sigma, double x) {

	double z = (Math.log(x) - mu)/sigma;
	double norm = 1/(x*sigma*Math.sqrt(2*Math.PI));
	return norm*Math.exp(-Math.pow(z, 2)/2);
    }

    public double getLogLikelihood(double mu, double sigma, double x) {

	double z = (Math.log(x) - mu)/sigma;
	return -1*(Math.log(2*Math.PI)/2 + Math.log(x) + Math.log(sigma) + Math.pow(z, 2)/2);
    }
}
