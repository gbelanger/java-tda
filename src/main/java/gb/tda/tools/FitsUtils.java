package gb.tda.tools;


import java.awt.geom.Point2D; 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jsky.coords.WCSTransform;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.BinaryTableHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.ImageHDU;
import nom.tam.util.BufferedDataInputStream;
import org.apache.log4j.Logger;


/**
 *
 *  @author G. Belanger, ESA
 *
 **/

public final class FitsUtils { 

    private static Logger logger = Logger.getLogger(FitsUtils.class);

    public static Fits openFits(File file) throws IOException, FitsException {
		Fits fitsFile = new Fits(file);
		return fitsFile;
    }

    public static Fits openFits(String filename) throws IOException, FitsException {
		return openFits(new File(filename));
    }

    public static BinaryTableHDU getHDU(String fitsFileName, String hduName) throws IOException, FitsException {
		Fits fitsFile = openFits(fitsFileName); 
		int numOfHDUs = (fitsFile.read()).length;
		BasicHDU hdu = fitsFile.getHDU(1);
		boolean foundHDU = true;
		String extName = hdu.getHeader().getStringValue("EXTNAME");
		if (!extName.startsWith(hduName)) foundHDU = false; 
		int extNum = 1;
		while (foundHDU == false && extNum < numOfHDUs) {
		    extNum++;
		    hdu = fitsFile.getHDU(extNum);
		    extName = hdu.getHeader().getStringValue("EXTNAME");
		    if (extName.equals(hduName))  foundHDU = true;
		}
		BinaryTableHDU tableHDU = (BinaryTableHDU) hdu;
		return tableHDU;
    }


    public static BinaryTableHDU getMatrixHDU(String filename) throws Exception {
		String hduName = "MATRIX";
		return getHDU(filename, hduName);
    }


    public static BinaryTableHDU getEventsHDU(String filename) throws Exception {
		String hduName = "EVENTS";
		return getHDU(filename, hduName);
    }

    public static BinaryTableHDU getRateHDU(String filename) throws Exception {
		String hduName = "RATE";
		return getHDU(filename, hduName);
    }


    public static BinaryTableHDU getSpectrumHDU(String filename) throws Exception {
		String hduName = "SPECTRUM";
		return getHDU(filename, hduName);
    }

    public static Point2D.Double[] getWCSCenters(String[] imagePaths, int hduExten) throws Exception {
		//  Returns the WCS centres of each image in Ra Dec as a Point2D.Double
		int nimages = imagePaths.length;
		Point2D.Double[] wcsCenters = new Point2D.Double[nimages];
		Fits ima;
		MyHeader head;
		WCSTransform wcsTransform;
		for (int i=0; i < nimages; i++) {
		    ima = openFits(imagePaths[i]);
		    head = new MyHeader(ima.getHDU(hduExten).getHeader());
		    wcsTransform = new WCSTransform(head);
		    wcsCenters[i] = wcsTransform.getWCSCenter();
		    (ima.getStream()).close();
		}
		return wcsCenters;
    }


    public static float getPixelValue(Point2D.Double skyCoords, File fitsImageFile, int extnum) throws Exception {
		Fits fitsImage = openFits(fitsImageFile);
		ImageHDU imaHDU = (ImageHDU) fitsImage.getHDU(extnum);
		return (float) getPixelValue(skyCoords, imaHDU);
    }

    public static double getPixelValue(Point2D.Double skyCoords, ImageHDU imaHDU) throws Exception {
		double[][] data = null;
		try { 
		    float[][] dataFlt = (float[][]) imaHDU.getKernel(); 
		    data = Converter.float2double(dataFlt);
		}
		catch (ClassCastException e) {
		    try { data = (double[][]) imaHDU.getKernel(); }
		    catch (ClassCastException e2) {
			logger.error("Image data is not float nor double. Cannot read imageHDU");
			System.exit(-1);
		    }
		}
		Point2D.Double ccdXY = null;
		double value = Double.NaN;
		try { 
		    ccdXY = getXY(skyCoords, imaHDU, false);
		    int x = (new Double(Math.rint(ccdXY.getX()))).intValue();
		    int y = (new Double(Math.rint(ccdXY.getY()))).intValue();
		    try {
			value = data[y-1][x-1];
		    }
		    catch (ArrayIndexOutOfBoundsException e1) {
			value = Double.NaN;
		    }
		}
		catch (NullPointerException e) {
		    value = Double.NaN;
		}
		return value;
    }


    
    /**
     * Method <code>image2physical</code> 
     *
     * @param ccdXY a <code>Point2D.Double</code> value
     * @param header a <code>Header</code> value
     * @return a <code>Point2D.Double</code> value
     */
    public static Point2D.Double image2physical(Point2D.Double ccdXY, Header header) {
		return image2physical(ccdXY.getX(), ccdXY.getY(), header);
    }

    public static Point2D.Double image2physical(double ccdX, double ccdY, Header header) { 
		//  Get keywords necessary to convert imaCoords to physCoords
		double ltm1 = header.getDoubleValue("LTM1_1");
		double ltv1 = header.getDoubleValue("LTV1");
		if (ltm1 == 0) ltm1 = 1;
		return image2physical(ccdX, ccdY, ltm1, ltv1);
    }

    public static Point2D.Double image2physical(double ccdX, double ccdY, double ltm1, double ltv1) { 
		return new Point2D.Double((ccdX-ltv1)/ltm1, (ccdY-ltv1)/ltm1);
    }

    
    /**
     * Method <code>image2skyCoords</code> 
     *
     * @param ccdXY a <code>Point2D.Double</code> value
     * @param header a <code>Header</code> value
     * @return a <code>Point2D.Double</code> value
     */
    public static Point2D.Double image2skyCoords(Point2D.Double ccdXY, Header header) {
		return image2skyCoords(ccdXY.getX(), ccdXY.getY(), header);
    }

    public static Point2D.Double image2skyCoords(double ccdX, double ccdY, Header header) {
		WCSTransform wcsTransform = new WCSTransform(new MyHeader(header));
		return wcsTransform.pix2wcs(ccdY, ccdX);
    }

    
    /**
     * Method <code>skyCoords2image</code> 
     *
     * @param skyCoords a <code>Point2D.Double</code> value
     * @param header a <code>Header</code> value
     * @return a <code>Point2D.Double</code> value
     */
    public static Point2D.Double skyCoords2image(Point2D.Double skyCoords, Header header) {
		WCSTransform wcsTransform = new WCSTransform(new MyHeader(header));
		return wcsTransform.wcs2pix(skyCoords.getX(), skyCoords.getY());
    }


    
    /**
     * Method <code>skyCoords2physical</code> 
     *
     * @param skyCoords a <code>Point2D.Double</code> value
     * @param header a <code>Header</code> value
     * @return a <code>Point2D.Double</code> value
     */
    public static Point2D.Double skyCoords2physical(Point2D.Double skyCoords, Header header) {
		WCSTransform wcsTransform = new WCSTransform(new MyHeader(header));
		Point2D.Double imageCoords = wcsTransform.wcs2pix(skyCoords.getX(), skyCoords.getY());
		return image2physical(imageCoords, header);
    }
    



    public static Point2D.Double getXY(Point2D.Double skyCoords, ImageHDU _imageHDU, boolean physical) {
		Point2D.Double ccdXY = null;
		Point2D.Double physXY = null;
		
		// Get image header
		Header imaHead  = _imageHDU.getHeader();

		//  Get keywords necessary to make imaCoords to physCoords
		double ltm1 = imaHead.getDoubleValue("LTM1_1");
		double ltv1 = imaHead.getDoubleValue("LTV1");
		if (ltm1 == 0) ltm1 = 1;

		//  Transform
		MyHeader myHeader = new MyHeader(imaHead);
		WCSTransform wcsTransform = new WCSTransform(myHeader);
		ccdXY = wcsTransform.wcs2pix(skyCoords.getX(), skyCoords.getY());
		physXY = new Point2D.Double((ccdXY.getX()-ltv1)/ltm1, (ccdXY.getY()-ltv1)/ltm1);

		if (physical)  return physXY;
		else  return ccdXY; 
    }


    public static Point2D.Double getXY(Point2D.Double skyCoords, File fitsImageFile, int extnum, boolean physCoords) throws Exception {
		Point2D.Double ccdXY = null;
		Point2D.Double physXY = null;
		// Create Fits Object and get image header
		Fits fitsImage  = openFits(fitsImageFile);
		ImageHDU imaHDU = (ImageHDU) fitsImage.getHDU(extnum);
		Header imaHead  = imaHDU.getHeader();
		//  Get keywords necessary to make imaCoords to physCoords
		double ltm1 = imaHead.getDoubleValue("LTM1_1");
		double ltv1 = imaHead.getDoubleValue("LTV1");
		if (ltm1 == 0) ltm1 = 1;
		//  Transform
		MyHeader myHeader = new MyHeader(imaHead);
		WCSTransform wcsTransform = new WCSTransform(myHeader);
		ccdXY = wcsTransform.wcs2pix(skyCoords.getX(), skyCoords.getY());
	 	physXY = new Point2D.Double((ccdXY.getX()-ltv1)/ltm1, (ccdXY.getY()-ltv1)/ltm1);
		if (physCoords)  return physXY;
		else  return ccdXY; 
    }				

    public static Point2D.Double getXY(Point2D.Double radec, Header fitsHeader) throws Exception {

	Point2D.Double ccdXY = null;
	Point2D.Double physXY = null;


	//  Get keywords necessary to make imaCoords to physCoords
	double ltm1 = fitsHeader.getDoubleValue("LTM1_1");
	double ltv1 = fitsHeader.getDoubleValue("LTV1");
	if (ltm1 == 0) ltm1 = 1;


	//  Define variables
	MyHeader myHeader = new MyHeader(fitsHeader);
	WCSTransform wcsTransform = null;
	double cra, cdec, xsecpix, ysecpix, xrpix, yrpix;
	int nxpix, nypix;
	double rotate;
	int equinox; 
	double epoch; 
	String[] projection;
	String xProj, yProj;
	String proj="TAN";


	//  Try to transform given image header if not construct using keywords
	try {
	    wcsTransform = new WCSTransform(myHeader);
	    ccdXY = wcsTransform.wcs2pix(radec.getX(), radec.getY());
	}
	catch (IllegalArgumentException e) {

	    ////  Get reference pixel RA and DEC values
	    Point2D.Double refRadec = getRefRadec(fitsHeader);
	    cra = refRadec.getX();
	    cdec = refRadec.getY();

	    ////  Get pixel size in degrees
	    Point2D.Double pixSize = getPixSize(fitsHeader);
	    xsecpix = 3600*pixSize.getX(); // convert to arcsec
	    ysecpix = 3600*pixSize.getY();

	    ////  Get reference pixel value
	    Point2D.Double refXY = getRefXY(fitsHeader);
	    xrpix = refXY.getX();
	    yrpix = refXY.getY();

	    ////  Get number of pixels = maximum image size
	    Point2D.Double nPix = getNPix(fitsHeader);
	    nxpix = (new Double(nPix.getX())).intValue();
	    nypix = (new Double(nPix.getY())).intValue();

	    ////  Get projection
	    projection = getProj(fitsHeader);
	    xProj = projection[0];
	    yProj = projection[1];
	    if (xProj.endsWith("TAN") && yProj.endsWith("TAN"))
		proj = "TAN";
	    else {
		logger.error("Projection is not TAN");
		System.exit(-1);
	    }

	    ////  Set rotation to zero and get equinox and epoch
	    rotate = 0.0;
	    equinox = (new Double(fitsHeader.getDoubleValue("EQUINOX"))).intValue();
	    epoch = 2000.0;

	    ////  Construct WCSTransform
	    wcsTransform = new WCSTransform(cra, cdec, xsecpix, ysecpix, xrpix, yrpix, nxpix, nypix, rotate, equinox, epoch, proj);

	    ////  Get xy value
	    ccdXY = wcsTransform.wcs2pix(radec.getX(), radec.getY());
	}

	physXY = new Point2D.Double((ccdXY.getX()-ltv1)/ltm1, (ccdXY.getY()-ltv1)/ltm1);
	return ccdXY;
    }


    public static Point2D.Double getRefXY(Header _header) throws Exception {
		String refxcrpx = _header.findKey("REFXCRPX");	
		double refX=0, refY=0;
		if (! (refxcrpx.valueOf(refxcrpx)).equals("null")) {
		    logger.info("Using REFXCRPX and REFYCRPX as reference X Y");
		    refX = _header.getDoubleValue("REFXCRPX");
		    refY = _header.getDoubleValue("REFYCRPX");
		}
		else {
		    logger.info("Using TCRPX6 and TCRPX7 as reference X Y");
		    refX = _header.getDoubleValue("TCRPX6");
		    refY = _header.getDoubleValue("TCRPX7");
		}
		Point2D.Double refXY = new Point2D.Double(refX, refY);
		return refXY;
    }

    public static Point2D.Double getRefRadec(Header _header) throws Exception {
		String refxcrvl = _header.findKey("REFXCRVL");	
		double refRa=0, refDec=0;
		if (! (refxcrvl.valueOf(refxcrvl)).equals("null")) {
		    logger.info("Using REFXCRVL and REFYCRVL as reference RA DEC");
		    refRa = _header.getDoubleValue("REFXCRVL");
		    refDec = _header.getDoubleValue("REFYCRVL");
		}
		else {
		    logger.info("Using TCRVL6 and TCRVL7 as reference RA Dec");
		    refRa = _header.getDoubleValue("TCRVL6");
		    refDec = _header.getDoubleValue("TCRVL7");
		}
		Point2D.Double refRadec = new Point2D.Double(refRa, refDec);
		return refRadec;
    }


    public static Point2D.Double getPixSize(Header _header) throws Exception {

	double xPixSize=0, yPixSize=0;
	String refxcdlt = _header.findKey("REFXCDLT");	
	if (! (refxcdlt.valueOf(refxcdlt)).equals("null")) {
	    logger.info("Using REFXCDLT and REFYCDLT as pixel size");
	    xPixSize = _header.getDoubleValue("REFXCDLT");
	    yPixSize = _header.getDoubleValue("REFYCDLT");
	}
	else {
	    logger.info("Using TCDLT6 and TCDLT7 as pixel size");
	    xPixSize = _header.getDoubleValue("TCDLT6");
	    yPixSize = _header.getDoubleValue("TCDLT7");
	}
	Point2D.Double pixSize = new Point2D.Double(xPixSize, yPixSize);

	return pixSize;
    }


    public static Point2D.Double getNPix(Header _header) throws Exception {

	double nXPix=0, nYPix=0;
	String refxlmax = _header.findKey("REFXLMAX");	
	if (! (refxlmax.valueOf(refxlmax)).equals("null")) {
	    logger.info("Using REFXLMAX and REFYLAMX as pixel size");
	    nXPix = _header.getDoubleValue("REFXLMAX");
	    nYPix = _header.getDoubleValue("REFYLMAX");
	}
	else {
	    logger.info("Using TLMAX6 and TLMAX7 as number of pixels");
	    nXPix = _header.getDoubleValue("TLMAX6");
	    nYPix = _header.getDoubleValue("TLMAX7");
	}
	Point2D.Double nPix = new Point2D.Double(nXPix, nYPix);

	return nPix;
    }

    public static String[] getProj(Header _header) throws Exception {

	String xProj = null;
	String yProj = null;
	String refxctyp = _header.findKey("REFXCTYP");	
	if (! (refxctyp.valueOf(refxctyp)).equals("null")) {
	    logger.info("Using REFXCTYP and REFYCTYP as projection type");
	    xProj = _header.getStringValue("REFXCTYP");
	    yProj = _header.getStringValue("REFYCTYP");
	}
	else {
	    logger.info("Using TLCTYP6 and TCTYP7 as number of pixels");
	    xProj = _header.getStringValue("TCTYP6");
	    yProj = _header.getStringValue("TCTYP7");
	}

	String[] proj = new String[] {xProj, yProj};
	return proj;
    }


    public static double getLTM1_1(File fitsImageFile, int extNum) throws Exception{

	double ltm1_1 = 0;
	    
	// Create Fits Object and get image header
	Fits fitsIma = openFits(fitsImageFile);
	ImageHDU imaHDU = (ImageHDU) fitsIma.getHDU(extNum);
	Header imaHead  = imaHDU.getHeader();

	//  Get and return LTM1_1
	ltm1_1 = imaHead.getDoubleValue("LTM1_1");
	if (ltm1_1 == 0) ltm1_1 = 1;
	return ltm1_1;
	
    }

    public static double[] getREFCDLT(File fitsImaFile, int extNum) throws Exception {

	// get degrees per pixel at reference pixel
	double[] refcdlt = new double[2];

	// Create Fits Object and get image header
	Fits fitsIma = openFits(fitsImaFile);
	ImageHDU imaHDU = (ImageHDU) fitsIma.getHDU(extNum);
	Header imaHead  = imaHDU.getHeader();

	//  Get REF*CDLT
	refcdlt[0] = imaHead.getDoubleValue("REFXCDLT");
	refcdlt[1] = imaHead.getDoubleValue("REFYCDLT");

	return refcdlt;
    }


    public static Point2D.Double xy2Radec(double physX, double physY, Header fitsHeader) throws Exception {

	Point2D.Double radec = null;

	//  Define variables
	MyHeader myHeader = new MyHeader(fitsHeader);
	WCSTransform wcsTransform = null;
	double cra, cdec, xsecpix, ysecpix, xrpix, yrpix;
	int nxpix, nypix;
	double rotate;
	int equinox; 
	double epoch; 
	String[] projection;
	String xProj, yProj;
	String proj="TAN";
	
	//  Try to transform given image header if not construct using keywords
	try {
	    wcsTransform = new WCSTransform(myHeader);
	    radec = wcsTransform.pix2wcs(physX, physY);
	}
	catch (IllegalArgumentException e) {

	    ////  Get reference pixel RA and DEC values
	    Point2D.Double refRadec = getRefRadec(fitsHeader);
	    cra = refRadec.getX();
	    cdec = refRadec.getY();

	    ////  Get pixel size in degrees
	    Point2D.Double pixSize = getPixSize(fitsHeader);
	    xsecpix = 3600*pixSize.getX(); // convert to arcsec
	    ysecpix = 3600*pixSize.getY();

	    ////  Get reference pixel value
	    Point2D.Double refXY = getRefXY(fitsHeader);
	    xrpix = refXY.getX();
	    yrpix = refXY.getY();

	    ////  Get number of pixels = maximum image size
	    Point2D.Double nPix = getNPix(fitsHeader);
	    nxpix = (new Double(nPix.getX())).intValue();
	    nypix = (new Double(nPix.getY())).intValue();

	    ////  Get projection
	    projection = getProj(fitsHeader);
	    xProj = projection[0];
	    yProj = projection[1];
	    if (xProj.endsWith("TAN") && yProj.endsWith("TAN"))
		proj = "TAN";
	    else {
		logger.error("Projection is not TAN");
		System.exit(-1);
	    }

	    ////  Set rotation to zero and get equinox and epoch
	    rotate = 0.0;
	    equinox = (new Double(fitsHeader.getDoubleValue("EQUINOX"))).intValue();
	    epoch = 2000.0;

	    ////  Construct WCSTransform
	    wcsTransform = new WCSTransform
		(cra, cdec, xsecpix, ysecpix, xrpix, yrpix, nxpix, nypix, rotate, equinox, epoch, proj);
	    radec = wcsTransform.pix2wcs(physX, physY);
	}
	
	return radec;
    }


    public static Point2D.Double[] xy2radec(double[] physX, double[] physY, Header fitsHeader) throws Exception {

	Point2D.Double[] radec = new Point2D.Double[physX.length];

	//  Define variables
	MyHeader myHeader = new MyHeader(fitsHeader);
	WCSTransform wcsTransform = null;
	double cra, cdec, xsecpix, ysecpix, xrpix, yrpix;
	int nxpix, nypix;
	double rotate;
	int equinox; 
	double epoch; 
	String[] projection;
	String xProj, yProj;
	String proj="TAN";

	//  Try to transform given image header if not construct using keywords
	try {
	    wcsTransform = new WCSTransform(myHeader);
	    for (int i=0; i < physX.length; i++) 
		radec[i] = wcsTransform.pix2wcs(physX[i], physY[i]);
	}
	catch (IllegalArgumentException e) {

	    ////  Get reference pixel RA and DEC values
	    Point2D.Double refRadec = getRefRadec(fitsHeader);
	    cra = refRadec.getX();
	    cdec = refRadec.getY();

	    ////  Get pixel size in degrees
	    Point2D.Double pixSize = getPixSize(fitsHeader);
	    xsecpix = 3600*pixSize.getX(); // convert to arcsec
	    ysecpix = 3600*pixSize.getY();

	    ////  Get reference pixel value
	    Point2D.Double refXY = getRefXY(fitsHeader);
	    xrpix = refXY.getX();
	    yrpix = refXY.getY();

	    ////  Get number of pixels = maximum image size
	    Point2D.Double nPix = getNPix(fitsHeader);
	    nxpix = (new Double(nPix.getX())).intValue();
	    nypix = (new Double(nPix.getY())).intValue();

	    ////  Get projection
	    projection = getProj(fitsHeader);
	    xProj = projection[0];
	    yProj = projection[1];
	    if (xProj.endsWith("TAN") && yProj.endsWith("TAN"))
		proj = "TAN";
	    else {
		logger.error("Projection is not TAN");
		System.exit(-1);
	    }

	    ////  Set rotation to zero and get epoch and equinox
	    rotate = 0.0;
	    equinox = (new Double(fitsHeader.getDoubleValue("EQUINOX"))).intValue();
	    epoch = 2000.0;

	    ////  Construct WCSTransform
	    wcsTransform = new WCSTransform
		(cra, cdec, xsecpix, ysecpix, xrpix, yrpix, nxpix, nypix, rotate, equinox, epoch, proj);
	    for (int i=0; i < physX.length; i++) 
		radec[i] = wcsTransform.pix2wcs(physX[i], physY[i]);
	}
	
	return radec;
    }

    public static Point2D.Double xy2Radec(double physX, double physY, ImageHDU imageHDU) throws Exception {

	MyHeader myHeader = new MyHeader(imageHDU.getHeader());
	WCSTransform wcsTransform = new WCSTransform(myHeader);
	return wcsTransform.pix2wcs(physX, physY);
    }

    public static Point2D.Double xy2Radec(double physX, double physY, File fitsImageFile, int extNum) throws Exception {

	Fits fitsImage  = openFits(fitsImageFile);
	ImageHDU imageHDU = (ImageHDU) fitsImage.getHDU(extNum);
	return xy2Radec(physX, physY, imageHDU);
    }

    public static Point2D.Double[] xy2radec(double[] physX, double[] physY, File fitsImageFile, int extNum) throws Exception {

	Point2D.Double[] radec = new Point2D.Double[physX.length];
	Fits fitsImage  = openFits(fitsImageFile);
	ImageHDU imaHDU = (ImageHDU) fitsImage.getHDU(extNum);
	Header imaHead  = imaHDU.getHeader();
	MyHeader myHeader = new MyHeader(imaHead);
	WCSTransform wcsTransform = new WCSTransform(myHeader);
	for (int i=0; i < physX.length; i++) {
	    radec[i] = wcsTransform.pix2wcs(physX[i], physY[i]);
	}
	return radec;
    }


    public static Point2D.Double radec2xy(double ra, double dec, Header fitsHeader) throws Exception {

	Point2D.Double xy = null;

	//  Define variables
	MyHeader myHeader = new MyHeader(fitsHeader);
	WCSTransform wcsTransform = null;
	double cra, cdec, xsecpix, ysecpix, xrpix, yrpix;
	int nxpix, nypix;
	double rotate;
	int equinox; 
	double epoch; 
	String[] projection;
	String xProj, yProj;
	String proj="TAN";

	//  Try to transform given image header, if not construct using keywords
	try {
	    wcsTransform = new WCSTransform(myHeader);
	    xy = wcsTransform.wcs2pix(ra, dec);
	}
	catch (IllegalArgumentException e) { //  Enters here when not FITS image

	    ////  Get reference pixel RA and DEC values
	    Point2D.Double refRadec = getRefRadec(fitsHeader);
	    cra = refRadec.getX();
	    cdec = refRadec.getY();

	    ////  Get pixel size in degrees
	    Point2D.Double pixSize = getPixSize(fitsHeader);
	    xsecpix = 3600*pixSize.getX(); // convert to arcsec
	    ysecpix = 3600*pixSize.getY();

	    ////  Get reference pixel value
	    Point2D.Double refXY = getRefXY(fitsHeader);
	    xrpix = refXY.getX();
	    yrpix = refXY.getY();

	    ////  Get number of pixels = maximum image size
	    Point2D.Double nPix = getNPix(fitsHeader);
	    nxpix = (new Double(nPix.getX())).intValue();
	    nypix = (new Double(nPix.getY())).intValue();

	    ////  Get projection
	    projection = getProj(fitsHeader);
	    xProj = projection[0];
	    yProj = projection[1];
	    if (xProj.endsWith("TAN") && yProj.endsWith("TAN"))
		proj = "TAN";
	    else {
		logger.error("Projection is not TAN");
		System.exit(-1);
	    }

	    ////  Set rotation to zero and get Epoch and Projection
	    rotate = 0.0;
	    equinox = (new Double(fitsHeader.getDoubleValue("EQUINOX"))).intValue();
	    epoch = 2000.0;

	    ////  Construct WCSTransform
	    wcsTransform = new WCSTransform
		(cra, cdec, xsecpix, ysecpix, xrpix, yrpix, nxpix, nypix, rotate, equinox, epoch, proj);
	    xy = wcsTransform.wcs2pix(ra, dec);
	}
	
	return xy;
    }
    
    public static Point2D.Double[] radec2xy(double[] ra, double[] dec, Header fitsHeader) throws Exception {

	Point2D.Double[] xy = new Point2D.Double[ra.length];

	//  Define variables
	MyHeader myHeader = new MyHeader(fitsHeader);
	WCSTransform wcsTransform = null;
	double cra, cdec, xsecpix, ysecpix, xrpix, yrpix;
	int nxpix, nypix;
	double rotate;
	int equinox; 
	double epoch; 
	String[] projection;
	String xProj, yProj;
	String proj="TAN";

	//  Try to transform given image header, if not construct using keywords
	try {
	    wcsTransform = new WCSTransform(myHeader);
	    for (int i=0; i < ra.length; i++) 
		xy[i] = wcsTransform.wcs2pix(ra[i], dec[i]);
	}
	catch (IllegalArgumentException e) {

	    ////  Get reference pixel RA and DEC values
	    Point2D.Double refRadec = getRefRadec(fitsHeader);
	    cra = refRadec.getX();
	    cdec = refRadec.getY();

	    ////  Get pixel size in degrees
	    Point2D.Double pixSize = getPixSize(fitsHeader);
	    xsecpix = 3600*pixSize.getX(); // convert to arcsec
	    ysecpix = 3600*pixSize.getY();

	    ////  Get reference pixel value
	    Point2D.Double refXY = getRefXY(fitsHeader);
	    xrpix = refXY.getX();
	    yrpix = refXY.getY();

	    ////  Get number of pixels = maximum image size
	    Point2D.Double nPix = getNPix(fitsHeader);
	    nxpix = (new Double(nPix.getX())).intValue();
	    nypix = (new Double(nPix.getY())).intValue();

	    ////  Get projection
	    projection = getProj(fitsHeader);
	    xProj = projection[0];
	    yProj = projection[1];
	    if (xProj.endsWith("TAN") && yProj.endsWith("TAN"))
		proj = "TAN";
	    else {
		logger.error("Projection is not TAN");
		System.exit(-1);
	    }

	    ////  Set rotation to zero and get Epoch and Projection
	    rotate = 0.0;
	    equinox = (new Double(fitsHeader.getDoubleValue("EQUINOX"))).intValue();
	    epoch = 2000.0;

	    ////  Construct WCSTransform
	    wcsTransform = new WCSTransform(cra, cdec, xsecpix, ysecpix, xrpix, yrpix, nxpix, nypix, rotate, equinox, epoch, proj);
	    for (int i=0; i < ra.length; i++)  xy[i] = wcsTransform.wcs2pix(ra[i], dec[i]);
	}
	
	return xy;
    }
	
}
