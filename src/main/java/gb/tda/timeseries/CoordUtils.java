package gb.tda.timeseries;

import java.awt.geom.Point2D;
import jsky.coords.WorldCoords;
import jsky.coords.wcscon;


/**
 *
 *  @version  December 2016 (last modified)
 *  @author   Guillaume Belanger (ESAC, Spain)
 *
 **/

public final class CoordUtils {

    public static double[] getAverageRaDec(double[] ras_deg, double[] decs_deg) {
	int n = ras_deg.length;
	// Convert to radians
	double[] ras = new double[n];
	double[] decs = new double[n];
	for (int i=0; i < n; i++) {
	    ras[i] = Math.toRadians(ras_deg[i]);
	    decs[i] = Math.toRadians(decs_deg[i]);
	}
	// Compute sum of vectors
	double x = 0;
	double y = 0;
	double z = 0;
	for (int i=0; i < n; i++) {
	    x += Math.cos(ras[i]) * Math.cos(decs[i]); // Note cos(dec); not sin(decs) as in spherical coords formula
	    y += Math.sin(ras[i]) * Math.cos(decs[i]); // Note cos(dec)
	    z += Math.sin(decs[i]); // Note sin(dec) instead of cos(dec)
	}
	// Average and normalise
	x /= n;
	y /= n;
	z /= n;
	double norm = Math.sqrt(x*x + y*y + z*z);
	x /= norm;
	y /= norm;
	z /= norm;
	// compute centroid
	double centreRa = Math.toDegrees(Math.atan2(y, x));
	centreRa = (centreRa % 360) + (centreRa < 0 ? 360 : 0);
	double centreDec = Math.toDegrees(Math.asin(z));
	return new double[] {centreRa, centreDec};
    }
    
    public static WorldCoords getAverageRaDec(WorldCoords[] coords) {
	int n = coords.length;
	Point2D.Double[] raDecs = new Point2D.Double[n];
	for (int i=0; i < n; i++) {
	    raDecs[i] = new Point2D.Double(coords[i].getRaDeg(), coords[i].getDecDeg());
	}
	Point2D.Double centre = getAverageRaDec(raDecs);
	return new WorldCoords(centre.getX(), centre.getY());
    }

    public static Point2D.Double getAverageRaDec(Point2D.Double[] raDecs) {
	int n = raDecs.length;
	double[] ras_deg = new double[n];
	double[] decs_deg = new double[n];
	for (int i=0; i < n; i++) {
	    ras_deg[i] = raDecs[i].getX();
	    decs_deg[i] = raDecs[i].getY();
	}
	double[] centreRaDec = getAverageRaDec(ras_deg, decs_deg);
	return new Point2D.Double(centreRaDec[0], centreRaDec[1]);
    }

    //  The following method also works, but will fail in problematic cases

    // public static double[] getCentre(double[] ras_deg, double[] decs_deg) {
    // 	// Convert to radians
    // 	double[] ras = new double[ras_deg.length];
    // 	double[] decs = new double[ras.length];
    // 	for (int i=0; i < ras.length; i++) {
    // 	    ras[i] = Math.toRadians(ras_deg[i]);
    // 	    decs[i] = Math.toRadians(decs_deg[i]);
    // 	}
    // 	// Translate DEC to be around 0
    // 	double dec_bar = BasicStats.getMean(decs);
    // 	double[] decs_translated = new double[decs.length];
    // 	for (int i=0; i < decs.length; i++) {
    // 	    decs_translated[i] = decs[i] - dec_bar;
    // 	}
    // 	// Translate RA to be around PI
    // 	double ra_bar = BasicStats.getMean(ras);
    // 	double[] ras_translated = new double[ras.length];
    // 	for (int i=0; i < ras.length; i++) {
    // 	    ras_translated[i] = ras[i] - ra_bar - Math.PI;
    // 	}
    // 	// Compute average and translate back to original position
    // 	double dec_avg = BasicStats.getMean(decs_translated) + dec_bar;
    // 	double ra_avg = BasicStats.getMean(ras_translated) + ra_bar + Math.PI;
    // 	// Convert to degrees and return
    // 	return new double[] {Math.toDegrees(ra_avg), Math.toDegrees(dec_avg)};
    // }    

    // public static WorldCoords getCentre(WorldCoords[] coords) {
    // 	int n = coords.length;
    // 	Point2D.Double[] raDecs = new Point2D.Double[n];
    // 	for (int i=0; i < n; i++) {
    // 	    raDecs[i] = new Point2D.Double(coords[i].getRaDeg(), coords[i].getDecDeg());
    // 	}
    // 	Point2D.Double centre = getCentre(raDecs);
    // 	return new WorldCoords(centre.getX(), centre.getY());
    // }

    // public static Point2D.Double getCentre(Point2D.Double[] raDecs) {
    // 	int n = raDecs.length;
    // 	double[] ras_deg = new double[n];
    // 	double[] decs_deg = new double[n];
    // 	for (int i=0; i < n; i++) {
    // 	    ras_deg[i] = raDecs[i].getX();
    // 	    decs_deg[i] = raDecs[i].getY();
    // 	}
    // 	double[] centreRaDec = getCentre(ras_deg, decs_deg);
    // 	return new Point2D.Double(centreRaDec[0], centreRaDec[1]);
    // }

    
    public static double getAngularDist(Point2D.Double radec1, Point2D.Double radec2) {
	WorldCoords coords1 = new WorldCoords(radec1);
	WorldCoords coords2 = new WorldCoords(radec2);
	double angDist = coords1.dist(coords2); // the 'dist' method returns arcminutes
	return angDist;
    }

    public static double[] getAngularDist(Point2D.Double[] radec, Point2D.Double refRadec) {
	WorldCoords refCoords = new WorldCoords(refRadec);
	WorldCoords coords = null;
	double[] angDist = new double[radec.length];
	for (int i=0; i < radec.length; i++) {
	    coords = new WorldCoords(radec[i]);
	    angDist[i] = refCoords.dist(coords); // return arcmins
	}
	return angDist;
    }

    public static double getXDist(Point2D.Double coord1, Point2D.Double coord2) {
	double xDist = Math.abs(coord1.getX() - coord2.getX());
	return xDist;
    }

    public static double[] getXDist(Point2D.Double[] coords, Point2D.Double refCoords) {
	double[] xDist = new double[coords.length];
	double refX = refCoords.getX();
	for (int i=0; i < coords.length; i++) {
	    try { xDist[i] = Math.abs(coords[i].getX() - refX); }
	    catch (NullPointerException e) {xDist[i]=Double.MAX_VALUE;}
	}
	return xDist;
    }

    public static double getYDist(Point2D.Double coord1, Point2D.Double coord2) {
	double yDist = Math.abs(coord1.getY() - coord2.getY());
	return yDist;
    }

    public static double[] getYDist(Point2D.Double[] coords, Point2D.Double refCoords) {
	double[] yDist = new double[coords.length];
	double refY = refCoords.getY();
	for (int i=0; i < coords.length ; i++) {
	    try { yDist[i] = Math.abs(coords[i].getY() - refY); }
	    catch (NullPointerException e) {yDist[i]=Double.MAX_VALUE;}
	}
	return yDist;
    }

    public static double getDist(Point2D.Double coord1, Point2D.Double coord2) {
	double dist = Math.sqrt(Math.pow(coord1.getX() - coord2.getX(), 2) + Math.pow(coord1.getY() - coord2.getY(), 2));
	return dist;
    }

    public static double[] getDist(Point2D.Double[] coords, Point2D.Double refCoords) {
	double[] dist = new double[coords.length];
	double refX = refCoords.getX();
	double refY = refCoords.getY();
	for (int i=0; i < coords.length ; i++) {
	    try { dist[i] = Math.sqrt(Math.pow(coords[i].getX() - refX, 2) +
		        Math.pow(coords[i].getY() - refY, 2)); }
	    catch (NullPointerException e) {dist[i]=Double.MAX_VALUE;}
	}
	return dist;
    }

    public static Point2D.Double[] constructPoint2DArray(double[] x, double[] y) {
	Point2D.Double[] xy = new Point2D.Double[x.length];
	for (int i=0; i < x.length; i++) {
	    xy[i] = new Point2D.Double(x[i], y[i]);
	}
	return xy;
    }


}
