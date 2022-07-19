package gb.tda.tools;

/**
 * $id$
 *


 // History
 // January-2003 : B. Ali, first version
 // 25-sep-2003  : JV, moved from herschel.sky to herschel.pacs.share.math

**/

////package herschel.pacs.share.math;


public class ComplexNumbers {

    /*
     * Methods
     */

    /*
     * Arrange the data into complex form.  See more detailed
     * comments below.
     * @param vector of data values of length n
     * @return rearranged data into complex number (length 2*n)
     */

    public static double[] myComplex(double[] data_in) {

	/**
	 ** Rearranges a vector of real values into a complex
	 ** number arrangement that is needed for input into
	 ** FFT.fft (see below).
	 **
	 ** The arrangement is done as described in the comments
	 ** for FFT.fft
	 **
	 **  Input is a floating point vector of length N
	 ** Output is a floating point vector of length 2*N
	 **
	 **/

	int i;
	int j;
	int n;

	n = 2 * data_in.length;
	double[] data = new double[n];
	int len = data_in.length;
	for (i=0; i<len; i++){
	    j = i*2;
	    data[j] = data_in[i];
	    data[j+1] = 0.0;
	}

	return data;
    }


    /* Arrange vector into complex number.  See more detailed 
     * comments below.
     * @param data vector of real values (length = n)
     * @param data vector of imaginary values (length = n)
     * @return data vector of complex numbers (length=2*n)
     */

    public static double[] myComplex(double[] real_in, double[] imaginary_in) {

	/**
	 ** Rearranges two vectors (one with real values, the
	 ** 2nd with imaginary values) into a complex
	 ** number arrangement that is needed for input into
	 ** FFT.fft (see below).
	 **
	 ** The arrangement is done as described in the comments
	 ** for FFT.fft
	 **
	 **  Input is two floating point vectors of length N
	 ** Output is a floating point vector of length 2*N
	 **
	 **/

	int i;
	int j;
	int n;

	n = 2 * real_in.length;
	double[] data = new double[n];
	for (i=0; i<real_in.length; i++){
	    j = i*2;
	    data[j] = real_in[i];
	    data[j+1] = imaginary_in[i];
	}

	return data;
    }


    // Added 
    public static double[] getPower(double[] complex_in){

	/**  Returns the "sum squared amplitude"  **/

	int n = complex_in.length;
	double[] power = new double[n/2];
	for (int i=0; i < n/2; i++){
	    power[i] = complex_in[i*2]*complex_in[i*2] + complex_in[i*2+1]*complex_in[i*2+1];
	}
	return power;
    }


    /*
     * Get the real part of the complex number
     * @param vector of complex numbers
     * @return vector of real values
     */

    public static double[] getReal(double[] complex_in){

	/**
	 ** Extract the "real" part of a complex number 
	 ** generated via myComplex
	 **
	 **/

	int n = complex_in.length;
	double[] r = new double[ n/2 ];
	for (int i=0; i<n/2; i++){
	    r[i] = complex_in[i*2];
	}
	return r;
    }


    /*
     * Get the imaginary part of the complex number
     * @param vector of complex numbers
     * @return vector of imaginary values
     */

    public static double[] getImaginary(double[] complex_in){

	/**
	 ** Extract the "imaginary" part of a complex number
	 ** generated via myComplex
	 **
	 **/

	int n = complex_in.length;
	double[] i = new double[ n/2 ];
	for (int j=0; j<n/2; j++){
	    i[j] = complex_in[j*2+1];
	}
	return i;
    }


    /*
     * Multiply two complex numbers together
     * @param complex vector a
     * @param complex vector b
     * @return complex vector a*b
     */

    public static double[] multiply(double[] a, double[] b){

	/**
	 ** multiply two complex numbers together 
	 **
	 **/

	double[] c = new double[a.length];

	for (int i=0; i<a.length; i+=2) {
	    c[i]   = a[i]   * b[i] - a[i+1] * b[i+1];
	    c[i+1] = a[i+1] * b[i] + a[i]   * b[i+1];
	}

	return c;
    }


}
