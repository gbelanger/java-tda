package gb.tda.tools;

/*
 * $Id: MyFFT.java,v 1.3 2005/05/23 12:51:48 rene Exp $
 *
 */

// History
//  January-2003 : B. Ali, First version.  The code is based on the FFT code in Numerical Recipes
//                 
// 25-sep-2003  : JV, moved from herschel.sky to herschel.pacs.share.math

////package herschel.pacs.share.math;

public class MyFFT {

    /*
     * Methods
     */

    /*
     * Test if number is a power of 2
     * @param number
     */

    public static boolean isPowOf2(double num) {
	boolean ans = false;
	double hval  = num;
	double eps   = 1.0e-6;
	while (hval > 2.0) {
	    hval /= 2.0 ;
	}
	if (Math.abs(hval-2.0) <= eps) {
	    ans = true;
	}
	return ans;
    }

/*
     * Test if number is a power of 2
     * @param number
     */

    public static boolean isPowOf2(int num) {
	boolean ans = false;
	double hval  = (double)num;
	double eps   = 1.0e-6;
	while (hval > 2.0) {
	    hval /= 2.0 ;
	}
	if (Math.abs(hval-2.0) <= eps) {
	    ans = true;
	}
	return ans;
    }

    /*
     * Perfrom the FFT.  See more detailed comments below.
     * @param vector of complex number of length equal to a power of 2
     * @param number of data points (length/2, see ComplexNumbers.java)
     * @param direction of transform
     * @return Fourier transform
     */
    
    public static double[] fft(double[] data_in, int nn, int isign) {

	/**
	 ** Computes the fast fourier transform.
	 **
	 ** NOTE:  nn  must be a power of 2 or this WILL NOT WORK!
	 **
	 **
	 ** Inputs:
	 ** 
	 ** data_in -- Floating point vector.  This is a (real,imaginary)
	 **            pair of complex numbers represented as a floating
	 **            point vector.  The data is organized as follows:
	 **            data_in[0] = real part of the first number
	 **            data_in[1] = imaginary part of the first number
	 **            data_in[2] = real part of the 2nd number
	 **            data_in[3] = imaginary part of the 2nd number
	 **            ... and so on.
	 **
	 **            It is recommended that if you have a vector
	 **            with real data, use the FFT.myComplex()
	 **            --part of this class-- to reorganize your data
	 **            into the pairing needed for FFT.fft
	 **
	 ** nn      -- The total number of complex points.
	 **            This is (data_in.length / 2)
	 **
	 ** isign   -- +1 for forward transform
	 **            -1 for reverse (inverse) transform
	 **
	 ** Output  --
	 **/


	// first must test to make sure we have a power of 2

	if (isPowOf2(nn) == false) {
	    System.out.println("FFT.java ERROR: The length of the input data vector must be a power of 2");
	    double[] err_vals = new double[2];
	    err_vals[0] = 0.0F;
	    err_vals[1] = 0.0F;
	    return err_vals;
	}

	int n, mmax, m, j, istep, i;
	double wtemp, wr, wpr, wpi, wi, theta;
	double temp, tempr, tempi;

	n = nn << 1;

	double[] data = new double[n+1];

	for (i=1; i<=n; i++){
	    data[i] = data_in[i-1];
	}

	j = 1;

	for (i=1; i<n; i+=2) {
	    if (j > i) {
		temp = data[j];
		data[j] = data[i];
		data[i] = temp;
		temp = data[j+1];
		data[j+1] = data[i+1];
		data[i+1] = temp;
	    }
	    m = n >> 1;
	    while(m >= 2 && j > m) {
		j -= m;
		m >>= 1;
	    }
	    j += m;
	}

	mmax = 2;
	while (n > mmax) {
	    istep = mmax << 1;
	    theta = isign * (6.28318530717959/mmax);
	    wtemp = Math.sin(0.5 * theta);
	    wpr = -2.0 * wtemp * wtemp;
	    wpi = Math.sin(theta);
	    wr = 1.0;
	    wi = 0.0;
	    for (m = 1; m<mmax; m+=2) {
		for (i=m; i<=n; i+=istep) {
		    j = i + mmax;
		    tempr = wr*data[j]-wi*data[j+1];
		    tempi = wr*data[j+1]+wi*data[j];
		    data[j] = data[i] - tempr;
		    data[j+1] = data[i+1] - tempi;
		    data[i] += tempr;
		    data[i+1] += tempi;
		}
		wr = (wtemp=wr) * wpr - wi * wpi + wr;
		wi = wi * wpr + wtemp * wpi + wi;
	    }
	    mmax = istep;
	}

	double oon = 1.0F;
	if (isign > 0) {
	    oon = nn;
	}

	double[] rdata = new double[n];
	for (i=1; i<=n; i++){
	    rdata[i-1] = data[i] / oon;
	}
	return rdata;
    }

}
