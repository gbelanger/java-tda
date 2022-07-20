package gb.tda.montecarlo;

import cern.colt.list.DoubleArrayList;
import org.apache.log4j.Logger;

class Utils {

    private static Logger logger = Logger.getLogger(Utils.class);

    static double[] getFourierFrequencies(double nuMin, double nuMax, double df) {
        int nFreqs = (int) Math.round((nuMax-nuMin)/df);
        double[] frequencies = new double[nFreqs];
        for (int i=0; i < nFreqs; i++) {
            frequencies[i] = nuMin + i*df;
        }
        return frequencies;
    }

    static double[] getBinEdges(double xmin, double xmax, int nbins) throws IllegalArgumentException {
        double binWidth = (xmax-xmin)/nbins;
        return getBinEdges(xmin, xmax, binWidth);
    }

    static double[] getBinEdges(double binWidth, int nbins) throws IllegalArgumentException {
        double xmin = 0;
        double xmax = binWidth*nbins;
        return getBinEdges(xmin, xmax, binWidth);
    }

    static double[] getBinEdges(double xmin, double xmax, double binWidth) throws IllegalArgumentException {
        logger.info("Constructing bin edges from xmin, xmax, binWidth");
        if (xmin >= xmax) {
            throw new IllegalArgumentException("Cannot construct bin edges: xmax >= xmin");
        }
        double range = xmax - xmin;
        if (range < binWidth) {
            throw new IllegalArgumentException("Cannot constuct bin edges: (xmax-xmin) < binWidth.");
        }
        //  Construct bin edges
        DoubleArrayList edges = new DoubleArrayList();
        double leftEdge = xmin - 0.5*Math.ulp(xmin);  //  move back the first left edge by a ULP
        double rightEdge = leftEdge + binWidth;
        while (rightEdge <= (xmax+binWidth/2)) {
            edges.add(leftEdge);
            edges.add(rightEdge);
            leftEdge = rightEdge;
            rightEdge = leftEdge + binWidth;
        }
        edges.trimToSize();
        double lastEdge = edges.get(edges.size()-1);
        lastEdge += 0.5*Math.ulp(lastEdge);  //  Push forward the last right edge by a ULP
        edges.set(edges.size()-1, lastEdge);
        return edges.elements();
    }

    static double[] getBinEdges(double xmin, double[] binWidths) throws IllegalArgumentException {
        logger.info("Constructing bin edges from xmin and binWidths[]");
        DoubleArrayList edges = new DoubleArrayList();
        double leftEdge = xmin - 0.5*Math.ulp(xmin);  //  move back the first left edge by 0.5 ULP
        double rightEdge = leftEdge + binWidths[0];
        for (int i=1; i < binWidths.length; i++) {
            edges.add(leftEdge);
            edges.add(rightEdge);
            leftEdge = rightEdge;
            rightEdge = leftEdge + binWidths[i];
        }
        edges.trimToSize();
        double lastEdge = edges.get(edges.size()-1);
        lastEdge += 0.5*Math.ulp(lastEdge);  //  Push forward the last right edge by 0.5 ULP
        edges.set(edges.size()-1, lastEdge);
        return edges.elements();
    }

}