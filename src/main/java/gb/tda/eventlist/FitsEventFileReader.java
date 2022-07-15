package gb.tda.eventlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.BinaryTableHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.TruncatedFileException;
import nom.tam.util.ArrayFuncs;
import nom.tam.util.BufferedDataInputStream;
import org.apache.log4j.Logger;

import gb.tda.tools.Converter;


public class FitsEventFileReader implements IEventFileReader {

    private static Logger logger  = Logger.getLogger(FitsEventFileReader.class.getName());

    public EventList read(String filename) throws EventFileException, EventListException, IOException {

		//  Open the FITS file and retrieve all the HDUs
		BasicHDU<?>[] hdus = getAllHDUs(filename);

		//  Get the EVENTS HDU
		BinaryTableHDU hdu = findEventsHDU(hdus);

		//  Get the arrival times and convert from days to seconds if necessary
		int timeColIndex = hdu.findColumn("TIME");
		double[] times;
		if ( timeColIndex == -1 ) {
		    timeColIndex = hdu.findColumn("Time");
		}
		if ( timeColIndex == -1 ) {
		    throw new FitsEventFileException("Cannot find 'TIME' or 'Time' column");
		}
		times = getDoubleDataCol(hdu, timeColIndex);
		int timeColNumber = timeColIndex + 1;
		String timeColUnits = hdu.getHeader().getStringValue("TUNIT"+timeColNumber);
		if ( timeColUnits.equalsIgnoreCase("d") ) {
		    logger.info("Time units are days: Converting to seconds");
		    for ( int i=0; i < times.length; i++ ) {
			times[i] *= 86400;
		    }
		}

		//  Energy
		double[] energies = null;
		boolean energyCol = false;
		try {
		    short[] energ = (short[]) hdu.getColumn("PI");
		    energies = Converter.short2double(energ);
		    energyCol = true;
		}
		catch ( FitsException e ) { 
		    try {
			short[] energ = (short[]) hdu.getColumn("PHA");
			energies = Converter.short2double(energ);
			energyCol = true;
		    }
		    catch ( FitsException e2) {
			logger.info("There is no PI or PHA column"); 
		    }
		}
		
		//  Coordinates
		int[] xCoords = null;
		int[] yCoords = null;
		boolean coordsCol = false;
		try { 
		    xCoords = (int[]) hdu.getColumn("X"); 
		    yCoords = (int[]) hdu.getColumn("Y"); 
		    coordsCol = true;
		}
		catch ( FitsException e )  { logger.info("There is no X or Y column");}

		// Flag
		int[] flags = null;
		boolean flagCol = false;
		try {
		    flags = (int[]) hdu.getColumn("FLAG");
		    flagCol = true;
		}
		catch ( FitsException e )  { logger.info("There is no FLAG column");}

		// Flag
		int[] patterns = null;
		boolean patternCol = false;
		try {
		    byte[] p = (byte[]) hdu.getColumn("PATTERN");
		    patterns = new int[p.length];
	 	    for ( int i=0; i < p.length; i++ ) {
	 		patterns[i] = (new Byte(p[i])).intValue();
	 	    }
		    patternCol = true;
		}
		catch ( FitsException e )  { logger.info("There is no PATTERN column");}

		//  Construct and return the EventList
		if ( energyCol && coordsCol && flagCol && patternCol ) {
		    return new EventList(times, energies, xCoords, yCoords, flags, patterns);
		}
		if ( energyCol && coordsCol ) {
		    return new EventList(times, energies, xCoords, yCoords);
		}
		else if ( energyCol && !coordsCol ) {
		    return new EventList(times, energies);
		}
		else {
		    return new EventList(times);
		}

    }


    private BasicHDU<?>[] getAllHDUs(String filename) throws IOException, EventFileException {
		Fits fitsFile = null;
		try {
		    fitsFile = new Fits(filename);
			logger.info("File format is FITS. Reading HDUs.");
		    BasicHDU<?>[] hdus = fitsFile.read();
		    return hdus;
		}
		catch ( TruncatedFileException e ) {
		    throw new EventFileException("File is either empty or corrupted", e);
		}
		catch ( FitsException e ) {
		    if ( e.getMessage().contains("Not FITS format") ) {
				throw new FitsEventFileFormatException("File format is not FITS.");
		    }
		    else {
				throw new FitsEventFileException("File format is FITS but there was a problem reading it.", e);
		    }
		}
    }


    private BinaryTableHDU findEventsHDU(BasicHDU<?>[] hdus) throws FitsEventFileException {
		int k=1;
		try {
		    while ( true ) {
			
			try {
			    BinaryTableHDU hdu = (BinaryTableHDU) hdus[k];
			    String extname = hdu.getHeader().getStringValue("EXTNAME");
			    if ( extname.equals("EVENTS") ) {
				return hdu;
			    }
			    String hduclas1 = hdu.getHeader().getStringValue("HDUCLAS1");
			    if ( hduclas1 != null && hduclas1.equals("EVENTS") ) {
				return hdu;
			    }
			}
			catch ( ClassCastException e ) {}  //  the HDU is not a BinaryTableHDU
			k++;  //  Check the next HDU
		    }
		}
		catch ( ArrayIndexOutOfBoundsException e ) {
		    throw new FitsEventFileException("Not a FITS event file. There is no HDU named EVENTS or whose HDUCLAS1 keyword is EVENTS");
		}
    }

    private BinaryTableHDU getBinaryTableHDU(BasicHDU<?>[] hdus, String hduName) throws NullPointerException {
		for ( BasicHDU<?> h : hdus ) {

		    try { 
			BinaryTableHDU hdu = (BinaryTableHDU) h;
			String extname = h.getHeader().getStringValue("EXTNAME");
			if ( extname.equals(hduName) ) {
			    return hdu;
			}
		    }
		    catch ( ClassCastException e ) {}  //  The HDU is not a BinaryTableHDU
		}
		throw new NullPointerException("BasicHDU<?> array does not contain a BinaryTableHDU named "+hduName);
    }

    private double[] getDoubleDataCol(BinaryTableHDU hdu, int colNumber) throws FitsEventFileException {
		try {
		    double[] col = null;
		    try {
			col = (double[]) hdu.getColumn(colNumber); 
			return col;
		    }
		    catch ( ClassCastException e ) {
			try{
			    float[] flt = (float[]) hdu.getColumn(colNumber);
			    col = Converter.float2double(flt);
			    return col;
			}
			catch ( ClassCastException e2 ) {
			    throw new FitsEventFileException("Column number "+colNumber+" format is not double[] nor float[]. "+e2);
			}
		    }
		    
		}
		catch ( FitsException e ) {
		    throw new FitsEventFileException("Cannot get column "+colNumber+" in HDU", e);
		}
    }    

    private double[] getDoubleDataCol(BinaryTableHDU hdu, String colName) throws FitsEventFileException {
		try {
		    double[] col = null;
		    try {
			col = (double[]) hdu.getColumn(colName); 
			return col;
		    }
		    catch ( ClassCastException e ) {
			try{
			    float[] flt = (float[]) hdu.getColumn(colName);
			    col = Converter.float2double(flt);
			    return col;
			}
			catch ( ClassCastException e2 ) {
			    throw new FitsEventFileException(colName+" column format not double[] nor float[]. "+e2);
			}
		    }
		    
		}
		catch ( FitsException e ) {
		    throw new FitsEventFileException("Cannot get column "+colName+" in HDU", e);
		}
    }

    
}
