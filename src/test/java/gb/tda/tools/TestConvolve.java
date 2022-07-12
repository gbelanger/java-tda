package gb.tda.tools;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister64;
//import gb.codetda.likelihood.PoissonLikelihood;
//import gb.codetda.binner.BinningUtils;

public class TestConvolve {

    public static void main(String[] args) throws Exception {

	//  Test with a box function convolved with a triangle (Wikipedia example)
	double[] f = new double[] {1,1,1,1,1,1,1,1,1,1};
	double[] g = new double[f.length];
	double step = 1d/(int)g.length;
	for ( int i=0; i < g.length; i++ ) {
	    g[i] = 1 - i*step;
	}
	// f
	double min_xf = -0.5;
	double max_xf = 0.5;
	int nf = f.length;
	double dxf = (max_xf-min_xf)/(nf-1);
	double[] x_f = BinningUtils.getBinCentres(min_xf-dxf/2, max_xf+dxf/2, dxf);
	for ( int i=0; i < x_f.length; i++ ) System.out.println("x_f["+i+"]= "+x_f[i]);
	System.out.println();
	// g
	int ng = g.length;
	double min_xg = -0.5;
	double max_xg = 0.5;
	double dxg = (max_xg-min_xg)/(ng-1);
	double[] x_g = BinningUtils.getBinCentres(min_xg-dxg/2, max_xg+dxg/2, dxg);
	for ( int i=0; i < x_g.length; i++ ) System.out.println("x_g["+i+"]= "+x_g[i]);
	System.out.println();
	// fg
	double deltaG = max_xg - min_xg;
	double shift = deltaG/2;
	double min_xfg = min_xf - shift - dxg/2;
	double max_xfg = max_xf + shift + dxg/2;
	double dxfg = (max_xfg-min_xfg)/(ng-1);
	double dx = Math.min(dxf, dxg);
	double[] x_fg = MathUtils.getXAxisOfConvolutionForSymmetricFunctions(x_f, x_g);
	for ( int i=0; i < x_fg.length; i++ ) System.out.println("x_fg["+i+"]= "+x_fg[i]);
	System.out.println();
	// Perform Convolution, Cross-correlation and Auto-correltation
 	double[] fg = MathUtils.convolve(f, g);
	double[] gf = MathUtils.crossCorrelate(f, g);
	double[] gg = MathUtils.autoCorrelate(g);
	// Normalize for visualisation
	fg = MathUtils.normaliseMaxToOne(fg);
	gf = MathUtils.normaliseMaxToOne(gf);
	gg = MathUtils.normaliseMaxToOne(gg);
 	for ( int i=0; i < fg.length; i++ ) System.out.println("f*g["+i+"]= "+fg[i]);
	System.out.println();
 	PoissonLikelihood like = new PoissonLikelihood();
 	like.drawThreeFunctions(x_fg, fg, x_fg, gf, x_fg, gg, "convolution.qdp");

	//  Test with Normal distribution functions
	 dx = 0.5;
	double xMin = -10 -dx/2;
	double xMax = 10 + dx/2;
	 x_f = BinningUtils.getBinCentres(xMin, xMax, dx);
	 f = new double[x_f.length];
	 x_g = BinningUtils.getBinCentres(xMin, xMax, dx);
	 g = new double[x_g.length];
	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
	Normal n1 = new Normal(0,1,engine);
	Normal n2 = new Normal(0,1.5,engine);
	for ( int i=0; i < x_f.length; i++ ) {
	    f[i] = n1.pdf(x_f[i]);
	    g[i] = n2.pdf(x_g[i]);
	}
	 fg = MathUtils.convolve(f, g);
	 x_fg = MathUtils.getXAxisOfConvolutionForSymmetricFunctions(x_f, x_g);
	 fg = MathUtils.normaliseAreaToOne(fg, dx);
 	like.drawThreeFunctions(x_f, f, x_g, g, x_fg, fg, "g_convolution.qdp");

	//  Test with Poisson likelihood functions
	dx = like.getDeltaX();
	double xmin = 0 -dx/2;
	double xmax = 16 +dx/2;
	double span = xmax - xmin;
	x_f = BinningUtils.getBinCentres(xmin, xmax, dx);
	double nu1 = 5;
	f = like.getLikelihoodFunction(x_f, nu1);
	x_g = BinningUtils.getBinCentres(xmin, xmax, dx);
	g = like.getLikelihoodFunction(x_g, new double[]{5,5,5,5,5,5});

	fg = MathUtils.convolve(f,g);
	fg = MathUtils.normaliseMaxToOne(fg);
	double offset = MathUtils.getDistBetweenPeaks(f, fg, dx);
	System.out.println(offset);
	x_fg = BinningUtils.getBinCentres(xmin+offset, xmax+offset+span/2, dx);
	//x_fg = x_f;
	like.drawThreeFunctions(x_f, f, x_g, g, x_fg, fg, "p_convolution.qdp"); 

    }
}
