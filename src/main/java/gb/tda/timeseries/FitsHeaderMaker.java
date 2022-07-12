package gb.tda.timeseries;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import nom.tam.fits.BinaryTableHDU;
import nom.tam.fits.FitsException;
import nom.tam.fits.BinaryTable;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.Header;


/**
 * Class <code>FitsHeaderMaker</code> constructs headers for <code>FitsTimeSeriesFileWriter</code>
 * @author G. Belanger
 */


final class FitsHeaderMaker {
    
    private static final Logger logger = Logger.getLogger(FitsHeaderMaker.class);
    private static final String classname = (FitsHeaderMaker.class).getName();
    private static final String sdf_format = "yyyy-MM-dd'T'hh:mm:ss";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(sdf_format);
    
    static Header getCountsHeader(IAstroTimeSeries ts, BinaryTable binTable, String producedBy) throws IOException, FitsException {
        logger.info("Making COUNTS extension header");
        Header hdr = BinaryTableHDU.manufactureHeader(binTable);
        hdr.addValue("TFORM1", "1D", "data format of field: 8-byte DOUBLE");
        hdr.addValue("TUNIT1", "s", "physical unit of the field");
        hdr.addValue("TTYPE1", "TIME", "midpoint of the time bin");
        hdr.addValue("TFORM2", "1D", "data format of field: 8-byte DOUBLE");
        hdr.addValue("TUNIT2", "s", "physical unit of the field");
        hdr.addValue("TTYPE2", "TIMEDEL", "size of time bin");
    	hdr.addValue("TFORM3", "1D", "data format of field: 8-byte DOUBLE");
    	hdr.addValue("TUNIT3", "counts per bin", "physical unit of the field");
    	hdr.addValue("TTYPE3", "COUNTS", "intensity in counts");
    	if ( ts instanceof CodedMaskTimeSeries ) {	
            hdr.addValue("TFORM4", "1D", "data format of field: 8-byte DOUBLE");
            hdr.addValue("TUNIT4", "degree", "physical unit of the field");
            hdr.addValue("TTYPE5", "ANGDIST", "angular distance between source and pointing axis direction");  // Decide Name
    	}	
        String extName = "COUNTS";
        hdr.addValue("EXTNAME", extName, "name of this binary extension table");
    	hdr = addCommonHeaderInfo(ts, hdr);
        hdr.addValue("AUTHOR", producedBy, "Program name that produced this file");
        return hdr;
    }

    static Header getRatesHeader(IAstroTimeSeries ts, BinaryTable binTable, String producedBy) throws IOException, FitsException {
    	logger.info("Making RATES extenstion header");
        Header hdr = BinaryTableHDU.manufactureHeader(binTable);        
        hdr.addValue("TFORM1", "1D", "data format of field: 8-byte DOUBLE");
        hdr.addValue("TUNIT1", "s", "physical unit of the field");
        hdr.addValue("TTYPE1", "TIME", "midpoint of the time bin");
        hdr.addValue("TFORM2", "1D", "data format of field: 8-byte DOUBLE");
        hdr.addValue("TUNIT2", "s", "physical unit of the field");
        hdr.addValue("TTYPE2", "TIMEDEL", "size of time bin");
    	hdr.addValue("TFORM3", "1D", "data format of field: 8-byte DOUBLE");
    	hdr.addValue("TUNIT3", "counts/s", "physical unit of the field");
    	hdr.addValue("TTYPE3", "RATES", "intensity in counts per second");
    	hdr.addValue("TFORM4", "1D", "data format of field: 8-byte DOUBLE");
    	hdr.addValue("TUNIT4", "counts/s", "physical unit of the field");
    	hdr.addValue("TTYPE4", "ERRORS", "uncertainty on intensity (RATES)");
    	if ( ts instanceof CodedMaskTimeSeries ) {	
            hdr.addValue("TFORM5", "1D", "data format of field: 8-byte DOUBLE");
            hdr.addValue("TUNIT5", "degree", "physical unit of the field");
            hdr.addValue("TTYPE5", "ANGDIST", "angular distance between source and pointing axis direction");  // Decide Name
        }
        String extName = "RATES";
        hdr.addValue("EXTNAME", extName, "name of this binary extension table");
    	hdr = addCommonHeaderInfo(ts, hdr);
        hdr.addValue("AUTHOR", producedBy, "Program name that produced this file");
        return hdr;
    }

    static Header getSamplingHeader(IAstroTimeSeries ts, BinaryTable samplingTable, String producedBy) throws IOException, FitsException {
        logger.info("Making SAMPLING extension header");
        Header hdr = BinaryTableHDU.manufactureHeader(samplingTable);
        hdr.addValue("TFORM1", "1D", "data format of field: 8-byte DOUBLE");
        hdr.addValue("TUNIT1", "s", "physical unit of the field");
        hdr.addValue("TTYPE1", "TIME", "midpoint of the time bin");
        hdr.addValue("TFORM2", "1D", "data format of field: 8-byte DOUBLE");
        hdr.addValue("TUNIT2", "s", "physical unit of the field");
        hdr.addValue("TTYPE2", "TIMEDEL", "size of time bin");
        hdr.addValue("TFORM3", "1D", "data format of field: 8-byte DOUBLE");
    	hdr.addValue("TUNIT3", "n/a", "physical unit of the field");
        hdr.addValue("TTYPE3", "FUNCTION", "sampling function: 0 - off or 1 - on)");
    	String extName = "SAMPLING";
        hdr.addValue("EXTNAME", extName, "name of this binary extension table");
    	hdr = addCommonHeaderInfo(ts, hdr);
        hdr.addValue("AUTHOR", producedBy, "Program name that produced this file");
        return hdr;
    }

    static Header getPointingsHeader(CodedMaskTimeSeries ts, BinaryTable pointingsTable, String producedBy) throws IOException, FitsException {
        logger.info("Making POINTINGS extension header");
        Header hdr = BinaryTableHDU.manufactureHeader(pointingsTable);
        hdr.addValue("FCFOV", ts.maxDistForFullCoding(), "Max distance (deg) for full coding");
        hdr.addValue("PCFOV", ts.maxDistForPartialCoding(), "Max distance (deg) for partial coding");
        hdr.addValue("FCFRAC", ts.fullyCodedFraction(), "Fraction of fully coded data points");            
        hdr.addValue("TFORM1", "1D", "data format of field: 8-byte DOUBLE");
        hdr.addValue("TUNIT1", "degrees", "physical unit of the field");
    	hdr.addValue("TTYPE1", "RA", "RA of pointing direction"); // rasOfPointings
        hdr.addValue("TFORM2", "1D", "data format of field: 8-byte DOUBLE");
        hdr.addValue("TUNIT2", "degrees", "physical unit of the field");
        hdr.addValue("TTYPE2", "DEC", "DEC of pointing direction"); // decsOfPointings
        hdr.addValue("TFORM3", "1D", "data format of field: 8-byte DOUBLE");
        hdr.addValue("TUNIT3", "s", "physical unit of the field");
        hdr.addValue("TTYPE3", "EXPOSURE", "effective exposure on target"); // exposureOnTarget
        String extName = "POINTINGS";        
        hdr.addValue("EXTNAME", extName, "name of this binary extension table");
    	hdr = addCommonHeaderInfo(ts, hdr);
        hdr.addValue("AUTHOR", producedBy, "Program name that produced this file");
        return hdr;
    }
    
    static Header getGTIHeader(CodedMaskTimeSeries ts, BinaryTable gtiTable, String producedBy) throws IOException, FitsException {
    	logger.info("Making GTI extension header");
    	Header hdr = BinaryTableHDU.manufactureHeader(gtiTable);
        hdr.addValue("FCFOV", ts.maxDistForFullCoding(), "Max distance (deg) for full coding");
        hdr.addValue("PCFOV", ts.maxDistForPartialCoding(), "Max distance (deg) for partial coding");
        hdr.addValue("FCFRAC", ts.fullyCodedFraction(), "Fraction of fully coded data points");            
    	hdr.addValue("TFORM1", "1D", "data format of field: 8-byte DOUBLE");
    	hdr.addValue("TUNIT1", "s", "physical unit of the field");
    	hdr.addValue("TTYPE1", "ONTIME", "time collecting data"); // binWidths
    	hdr.addValue("TFORM2", "1D", "data format of field: 8-byte DOUBLE");
    	hdr.addValue("TUNIT2", "n/a", "physical unit of the field");
    	hdr.addValue("TTYPE2", "ONTIMEFRAC", "ratio of ONTIME to LIVETIME"); // liveTimeFractions
    	hdr.addValue("TFORM3", "1D", "data format of field: 8-byte DOUBLE");
    	hdr.addValue("TUNIT3", "s", "physical unit of the field");
    	hdr.addValue("TTYPE3", "LIVETIME", "time with instrument on"); // effectivePointingsDurations
    	hdr.addValue("TFORM4", "1D", "data format of field: 8-byte DOUBLE");
    	hdr.addValue("TUNIT4", "n/a", "physical unit of the field");
    	hdr.addValue("TTYPE4", "DEADTFRAC", "ratio of DEADTIME to LIVETIME"); // deadTimeFractions	
    	hdr.addValue("TFORM5", "1D", "data format of field: 8-byte DOUBLE");
    	hdr.addValue("TUNIT5", "s", "physical unit of the field");
    	hdr.addValue("TTYPE5", "DEADTIME", "dead time"); // deadTimeDurations	
    	String extName = "GTI";
    	hdr.addValue("EXTNAME", extName, "name of this binary extension table");
    	hdr = addCommonHeaderInfo(ts, hdr);
        hdr.addValue("AUTHOR", producedBy, "Program name that produced this file");
        return hdr;
    }

    
    //  Private 
    private static Header addCommonHeaderInfo(IAstroTimeSeries ts, Header hdr) throws HeaderCardException {
        hdr.addValue("TIMVERS", "OGIP/93-003", "OGIP memo number for file format");
        hdr.addValue("CONTENT", "Time Series", "file contains time series data");
        hdr.addValue("ORIGIN", "ESA, ESAC", "origin of the file");
        hdr.addValue("DATE", sdf.format(new Date()), "file creation date ("+sdf_format+")");
        hdr.addValue("TELESCOP", ts.telescope(), "telescope (mission) name");
        hdr.addValue("INSTRUME", ts.instrument(), "instrument used for observation");
        hdr.addValue("E_MIN", ts.energyRangeMin(), "low energy for channel keV");
        hdr.addValue("E_MAX", ts.energyRangeMax(), "high energy for channel keV");
        hdr.addValue("EUNIT", "keV", "energy unit");
        hdr.addValue("MJDREF", "", "MJD for reference file");
        hdr.addValue("TIMESYS", "MJD", "The time system is MJD");
        hdr.addValue("TIMEUNIT", ts.timeUnit(), "unit for TSTARTI/F and TSTOPI?F");
        hdr.addValue("EQUINOX", "2000.0", "equinox of celestial coord. system");
        hdr.addValue("RADECSYS", "FK5", "FK5 coordinate system used");
        hdr.addValue("OBJECT", ts.targetName(),"common object name ");
        hdr.addValue("RA", ts.targetRA(), "target RA in degrees");
        hdr.addValue("DEC", ts.targetDec(), "target Dec in degrees");
        hdr.addValue("DATE-OBS", ts.dateStart(), "date of first obsvn (yyyy-MM-dd)");
        hdr.addValue("TIME-OBS", ts.timeStart(), "time of first obsvn (hh:mm:ss)");
        hdr.addValue("DATE-END", ts.dateEnd(), "date of last obsvn (yyyy-MM-dd)");
        hdr.addValue("TIME-END", ts.timeStop(), "date of first obsvn (hh:mm:ss)");
        hdr.addValue("TSTART", ts.tStart(), "obeservation start time");
        hdr.addValue("TSTOP", ts.tStop(), "observation stop time");
        hdr.addValue("TIMEZERO", ts.tStart(), "zerotime to calculate t(n) event or bin");
        try {
            hdr.addValue("TIERRELA", ts.relTimeError(), "relative time error");
        }
        catch (java.lang.NumberFormatException e) {
            logger.warn("Cannot add TIERRELA (relative time error) to header");
        }
        try {
            hdr.addValue("TIERABSO", ts.absTimeError(), "absolute time error");
        }
        catch (java.lang.NumberFormatException e) {
            logger.warn("Cannot add TIERRABSO (absolute time error) to header");
        }
        hdr.addValue("CLOCKCOR", "NO", "if time corrected to UT");
        hdr.addValue("TIMEREF", "F", "barycentric correction applied to times");
        hdr.addValue("TASSIGN", "F", "time is assigned");
    	hdr.addValue("ONTIME", ts.sumOfBinWidths(), "sum of pointing durations");
    	hdr.addValue("LIVETIME", ts.livetime(), "deadtime-corrected sum of pointing durations");
    	hdr.addValue("EXPOSURE", ts.exposureOnTarget(), "effective exposure on target");
        hdr.addValue("BACKAPP", "F", "background subtracted");
        hdr.addValue("DEADAPP", "F", "deadtime applied");
        hdr.addValue("VIGNAPP", "F", "vignetting or collimator applied");
        return hdr;
    }

}
