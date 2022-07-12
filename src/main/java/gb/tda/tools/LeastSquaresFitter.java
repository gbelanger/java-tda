package gb.tda.tools;

import cern.colt.list.DoubleArrayList;
import org.apache.log4j.Logger;


/**
 * Class <code>LeastSquaresFitter</code> 
 *
 * @author <a href="mailto: guilaume.belanger@esa.int">Guillaume Belanger</a>, ESA/ESAC
 * @version 1.0  (2010 August)
 * @version 1.1  (2019 April)
 *
 */

public final class LeastSquaresFitter {

    private static Logger logger  = Logger.getLogger(LeastSquaresFitter.class);

    public static double[] fitLine(double[] _x, double[] _y) {

		// Check lengths are equal
		if ( _x.length != _y.length ) {
			logger.error("x.length != y.length");
			System.exit(-1);
		}

		logger.info("Calculating least squares best fit line");

		// Remove NaNs
		DoubleArrayList[] xyLists = removeNaNs(_x,_y);
		double[] x = xyLists[0].elements();
		double[] y = xyLists[1].elements();

		// Compute LS fit
		double sumOfX = 0;
		double sumOfY = 0;
		double sumOfXSqrd = 0;
		double sumOfYSqrd = 0;
		double sumOfXY = 0;
		double sumOfLnX = 0;
		double sumOfLnXSqrd = 0;
		double sumOfLnY = 0;
		double sumOfLnXLnY = 0;
						
		int npoints = x.length;
		for ( int i = 0; i < npoints; i++ ) {
			sumOfX += x[i];
			sumOfY += y[i];
			sumOfXSqrd += x[i]*x[i];
			sumOfYSqrd += y[i]*y[i];
			sumOfXY += x[i]*y[i];
		}
		double xbar = sumOfX/npoints;
		double ybar = sumOfY/npoints;
		double ss_xx = sumOfXSqrd - npoints*xbar*xbar;
		double ss_yy = sumOfYSqrd - npoints*ybar*ybar;
		double ss_xy = sumOfXY - npoints*xbar*ybar;
		double s = Math.sqrt( (ss_yy - ss_xy*ss_xy/ss_xx) / (npoints-2) );
			
		double b = (sumOfY*sumOfXSqrd - sumOfX*sumOfXY) / (npoints*sumOfXSqrd - sumOfX*sumOfX);
		double m = (npoints*sumOfXY - sumOfX*sumOfY) / (npoints*sumOfXSqrd - sumOfX*sumOfX);
			
		double err_b = s * Math.sqrt(1/npoints + xbar*xbar/ss_xx);
		double err_m = s / Math.sqrt(ss_xx);

		logger.info("Fitted function is: f(x) = m * x + b");
		logger.info("Best fit parameters are:");
		// logger.info("  m = "+(float)m+" +/- "+(float)err_m);
		// logger.info("  b = "+(float)b+" +/- "+(float)err_b);
		logger.info("  m = "+m+" +/- "+err_m);
		logger.info("  b = "+b+" +/- "+err_b);
		return new double[] {m, err_m, b, err_b};
    }
	
	
    public static double[] fitPowerLaw(double[] _x, double[] _y) {

		// Check lengths are equal
		if ( _x.length != _y.length ) {
			logger.error("x.length != y.length");
			System.exit(-1);
		}

		// Remove NaNs
		DoubleArrayList[] xyLists = removeNaNs(_x,_y);
		double[] x = xyLists[0].elements();
		double[] y = xyLists[1].elements();

		// Compute LS fit
		int npoints = x.length;
		double sumOfX = 0;
		double sumOfY = 0;
		double sumOfXSqrd = 0;
		double sumOfYSqrd = 0;
		double sumOfXY = 0;
		double sumOfLnX = 0;
		double sumOfLnXSqrd = 0;
		double sumOfLnY = 0;
		double sumOfLnXLnY = 0;
		for ( int i = 0; i < npoints; i++ ) {
			sumOfLnX += Math.log(x[i]);
			sumOfLnY += Math.log(y[i]);
			sumOfLnXLnY += Math.log(x[i])*Math.log(y[i]);
			sumOfLnXSqrd += Math.pow(Math.log(x[i]) , 2);
			//  For the fit of the straight line in log space: a + bx
			sumOfX += x[i];
			sumOfY += y[i];
			sumOfXSqrd += x[i]*x[i];
			sumOfYSqrd += y[i]*y[i];
			sumOfXY += x[i]*y[i];
		}
		double xbar = sumOfX/npoints;
		double ybar = sumOfY/npoints;
		double ss_xx = sumOfXSqrd - npoints*xbar*xbar;
		double ss_yy = sumOfYSqrd - npoints*ybar*ybar;
		double ss_xy = sumOfXY - npoints*xbar*ybar;
		double s = Math.sqrt( (ss_yy - ss_xy*ss_xy/ss_xx) / (npoints-2) );
		double err_a = s * Math.sqrt(1/npoints + xbar*xbar/ss_xx);
		double err_b = s / Math.sqrt(ss_xx);
		
		double index = (npoints*sumOfLnXLnY - sumOfLnX*sumOfLnY)/(npoints*sumOfLnXSqrd - Math.pow(sumOfLnX,2));
		double al = (sumOfLnY - index*sumOfLnX)/npoints;
		double norm = Math.exp(al);
		//  logger.info("Function is: norm * x^index");
		return new double[] {index, norm};
    }

	private static DoubleArrayList[] removeNaNs(double[] x, double[] y) {
		DoubleArrayList xList = new DoubleArrayList();
		DoubleArrayList yList = new DoubleArrayList();
		for (int i = 0; i < x.length; i++) {
			if (!Double.isNaN(x[i]) && !Double.isNaN(y[i])) {
				xList.add(x[i]);
				yList.add(y[i]);
			}
		}
		xList.trimToSize();
		yList.trimToSize();
		return new DoubleArrayList[]{xList,yList};
	}


}
