package gb.tda.likelihood;

public class GetLikelihood {

    public static void main(String[] args) throws Exception {

	ChiSquareLikelihood chi2 = new ChiSquareLikelihood();
	// double[] x = chi2.getXValues(0.01, 10);
	// double[] func = chi2.getLikelihoodFunction(x, 0.1);
	// chi2.drawFunction(x, func, "chi2.qdp");

	PoissonLikelihood p = new PoissonLikelihood();
	// double l = p.getNegLogLikelihood(70, 70);
	//System.out.println(l);
	// func = p.getLikelihoodFunction(x, 70);
	// p.drawFunction(x, func, "p.qdp");

	ExponentialLikelihood e = new ExponentialLikelihood();
	// func = e.getLikelihoodFunction(x, 0.1);
	// e.drawFunction(x, func, "e.qdp");
 	// System.out.println(e.getNegLogLikelihood(20.4, 20.4));
 	// System.out.println(e.getNegLogLikelihood(14, 14));
 	// System.out.println(e.getNegLogLikelihood(4, 4));
	//System.out.println(e.getLikelihood(1, 2));
	//System.out.println(e.getLikelihood(2, (2+Math.sqrt(2))));
	System.out.println(e.getLikelihood(2, new double[]{2}));


	InverseExponentialLikelihood ie = new InverseExponentialLikelihood();
	// x = ie.getXValues(0.01, 10);
	// func = ie.getLikelihoodFunction(x, 2);
	// ie.drawFunction(x, func, "ie.qdp");

 	NormalLikelihood n = new NormalLikelihood();
 	//func = n.getLikelihoodFunction(x, 0.1);
 	//n.drawFunction(x, func, "n.qdp");
	//double pdfValAtZero = n.pdfValue(0, 1, 0);
	//System.out.println("Zero: "+pdfValAtZero);
	// System.out.println("One: "+n.pdfValue(0, 1, 1)/pdfValAtZero);
	// System.out.println("Two: "+n.pdfValue(0, 1, 2)/pdfValAtZero);
	// System.out.println("TwoAndAHalf: "+n.pdfValue(0, 1, 2.5)/pdfValAtZero);
	// System.out.println("Three: "+n.pdfValue(0, 1, 3)/pdfValAtZero);
	// System.out.println("ThreeAndHalf: "+n.pdfValue(0, 1, 3.5)/pdfValAtZero);
	// System.out.println("Four: "+n.pdfValue(0, 1, 4)/pdfValAtZero);
	// System.out.println();
	// pdfValAtZero = n.pdfValue(0, 2, 0);
	// System.out.println("One: "+n.pdfValue(0, 2, 2)/pdfValAtZero);
	// System.out.println("Two: "+n.pdfValue(0, 2, 4)/pdfValAtZero);
	// System.out.println("TwoAndAHalf: "+n.pdfValue(0, 2, 5)/pdfValAtZero);
	// System.out.println("Three: "+n.pdfValue(0, 2, 6)/pdfValAtZero);
	// System.out.println("ThreeAndHalf: "+n.pdfValue(0, 2, 7)/pdfValAtZero);
	// System.out.println("Four: "+n.pdfValue(0, 2, 8)/pdfValAtZero);



	//System.out.println(n.getNegLogLikelihood(20.4, Math.sqrt(20.4), 20.4));
	//System.out.println(n.getNegLogLikelihood(14, Math.sqrt(14), 14));
	//System.out.println(n.getNegLogLikelihood(4, Math.sqrt(4), 4));
	

    }

}
