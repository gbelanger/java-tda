package gb.tda.tools;

import cern.colt.list.DoubleArrayList;
//import gb.codetda.binner.BinningException;
//import gb.codetda.binner.BinningUtils;


/**
 *
 * @version  Jan 2016 (last modified)
 * @author   Guillaume Belanger (ESAC, Spain)
 *
 **/

public final class MathUtils {

    public static double[] crossCorrelate(double[] f, double[] g) {
	int nf = f.length;
	int ng = g.length;
	int nfg = ng + nf - 1;
	double[] fg = new double[nfg];
	// if (nf < ng) {
	//     fg = crossCorrelate(g, f);
	// }
	// else {
	    int k=0;
	    while (k < ng) {
		fg[k] = 0;
		int i=0;
		while (i <= k) {
		    fg[k] += f[i]*g[(ng-1)-k+i];
		    i++;
		}
		k++;
	    }
	    while (k < nfg) {
		fg[k] = 0;
		int nTermsInSum = Math.min(ng, nfg-k);
		int i=0;
		while (i < nTermsInSum) {
		    fg[k] += f[k-(ng-1)+i]*g[i];
		    i++;
		}
		k++;
	    }
	// }
	return fg;
    }

    public static double[] convolve(double[] f, double[] g) {
	double[] gFlipped = new double[g.length];
	for (int i=0; i < g.length; i++) {
	    gFlipped[i] = g[g.length-1-i];
	}
	double[] gf = crossCorrelate(f, gFlipped);
	return gf;
    }

    public static double[] autoCorrelate(double[] f) {
	return crossCorrelate(f, f);
    }

    public static double getDistBetweenPeaks(double[] f, double dx_f, double[] g, double dx_g) {
	double peakOfF = MinMax.getMax(f);
	int indexOfPeakOfF = DataUtils.getIndex(peakOfF, f);
	double locationOfPeakOfF = (indexOfPeakOfF+0.5)*dx_f;
	double peakOfG = MinMax.getMax(g);
	int indexOfPeakOfG = DataUtils.getIndex(peakOfG, g);
	double locationOfPeakOfG = (indexOfPeakOfG+0.5)*dx_g;
	return (locationOfPeakOfF - locationOfPeakOfG);
    }

    public static double getDistBetweenPeaks(double[] f, double[] g, double dx) {	
	return getDistBetweenPeaks(f, dx, g, dx);
    }

    public static double[] normaliseAreaToOne(double[] f, double dx) {
	double area = dx * BasicStats.getSum(f);
	double[] f_norm = new double[f.length];
	for (int i=0; i < f.length; i++) {
	    f_norm[i] = f[i]/area;
	}
	return f_norm;
    }

    public static double[] normaliseMaxToOne(double[] f) {
	double max = MinMax.getMax(f);
	double[] f_norm = new double[f.length];
	for (int i=0; i < f.length; i++) {
	    f_norm[i] = f[i]/max;
	}
	return f_norm;
    }

    public static double[] getXAxisOfConvolutionForSymmetricFunctions(double[] x_f, double[] x_g) throws BinningException {
	int nf = x_f.length;
	int ng = x_g.length;
	// f
	double xMin_f = x_f[0];
	double xMax_f = x_f[nf-1];
	double xSpan_f = xMax_f - xMin_f;
	double dx_f = xSpan_f/(nf-1);
	// g
	double xMin_g = x_g[0];
	double xMax_g = x_g[ng-1];
	double xSpan_g = xMax_g - xMin_g;
	double dx_g = xSpan_g/(ng-1);
	double diff = Math.abs(dx_f - dx_g);
	// if (diff > 1e-3*Math.max(dx_f, dx_g)) {
	//     throw new BinningException("Bin size for f (dx_f="+dx_f+") and g (dx_g="+dx_g+") is not equal");
	// }
	// fg
	double dx = Math.min(dx_f, dx_g);
	double deltaG = xSpan_g + dx;
	double shift = deltaG/2;
	double xMin_fg = xMin_f - shift;
	double xMax_fg = xMax_f + shift;
	double[] xAxis_fg = BinningUtils.getBinCentres(xMin_fg, xMax_fg, dx);
	return xAxis_fg;
    }

//     public static double[][] convolve(double[] f, double[] x_f, double[] g, double[] x_g) throws BinningException {

// 	if (f.length != x_f.length || g.length != x_g.length)
// 	    throw new BinningException("Number of elements in function and axis is not equal");
// 	double[] y = convolve(f, g);
// 	double[] x = getXAxisOfConvolution(x_f, x_g);
// 	double[][] xy = new double[2][y.length];
// 	for (int i=0; i < x.length; i++) {
// 	    xy[0][i] = x[i];
// 	    xy[1][i] = y[i];
// 	}
// 	return xy;
//     }


    public static double[] getFirstDerivative(double[] data, double dx) {
	int nBins = data.length;
	double[] dxs = new double[nBins];
	for (int i=0; i < nBins; i++) {
	    dxs[i] = dx;
	}
	return getFirstDerivative(data, dxs);
    }
    public static double[] getFirstDerivative(double[] data, double[] dxs) {
	int nBins = data.length;
	double[] firstDerivative = new double[nBins-1];
	for (int i=0; i < nBins-1; i++) {
	    if (!Double.isNaN(data[i]) && !Double.isNaN(data[i+1])) {
		double slope = (data[i] - data[i+1])/dxs[i];
		firstDerivative[i] = slope;
	    }
	    else {
		firstDerivative[i] = Double.NaN;
	    }
	}
	return firstDerivative;
    }

    public static double getIntersectionPoint(double[] f1, double[] x1, double[] f2, double[] x2, double dx) {
	double peak1 = MinMax.getMax(f1);
	int indexAtPeak1 = DataUtils.getIndex(peak1, f1);
	double x1AtPeak1 = x1[indexAtPeak1];
	double peak2 = MinMax.getMax(f2);
	int indexAtPeak2 = DataUtils.getIndex(peak2, f2);
	double x2AtPeak2 = x2[indexAtPeak2];
	double intersection = 0;
	double deltaXBetweenPeaks = Math.abs(x1AtPeak1 - x2AtPeak2);
	if (deltaXBetweenPeaks <= dx) {
	    //  If the peaks of the two functions are aligned, return the value of x1AtPeak1
	    intersection = x1AtPeak1;
	}
	else if (x1AtPeak1 > x2AtPeak2) {
	    //  If peak1 occurs at larger value of X than peak2, just flip the order of the functions
	    intersection = getIntersectionPoint(f2, x2, f1, x1, dx);
	}
	else {
	    // Start from xAtPeak1 and scan up to xAtPeak2 calculating the difference between f1 and f2
	    int idxAlongX1 = indexAtPeak1;
	    double xAlongX1= x1AtPeak1;
	    int idxAlongX2 = DataUtils.getClosestIndexInSortedData(xAlongX1, x2);
	    double diff = f1[idxAlongX1] - f2[idxAlongX2];
	    while (diff > 0) {
		xAlongX1 = x1[idxAlongX1++];
		idxAlongX2 = DataUtils.getClosestIndexInSortedData(xAlongX1, x2);
		diff = f1[idxAlongX1] - f2[idxAlongX2];
	    }
	    intersection = xAlongX1 - dx/2;
	}
	return intersection;
    }

    public static double[] calcPowerLawFlux(double norm, double index, double emin, double emax) {
	int nPoints = (int) Math.ceil(emax - emin);
	double[] energies = new double[nPoints];
	for (int i=0; i < nPoints; i++) {
	    energies[i] = emin + i;
	}
	return calcPowerLawFlux(norm, index, energies);
    }
    public static double[] calcPowerLawFlux(double norm, double index, double[] energies) {
	double[] flux = new double[energies.length];
	for (int i=0; i < energies.length; i++) {
	    flux[i] = norm*Math.pow(energies[i], index);
	}
	return flux;
    }

    public static double[] getRatios(double[] numerators, double[] denominators) {
	double[] ratios = new double[numerators.length];
	for (int i=0; i < numerators.length; i++) {
	    ratios[i] = numerators[i]/denominators[i];						  
	}
	return ratios;
    }
    
}
