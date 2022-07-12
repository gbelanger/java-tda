package gb.tda.tools;

import java.io.File;
import jsky.coords.WCSKeywordProvider;
import nom.tam.fits.Header;


public class MyHeader implements WCSKeywordProvider {

    private Header hdr;

    public MyHeader() {
    }

    public MyHeader(Header _hdr) {
        hdr = _hdr;
    }

    public void fill(Header _hdr) {
        hdr = _hdr;
    }

    /** Return true if the given keyword was found */
    public boolean findKey(String key) {
    String keyValue = hdr.findKey(key);
        if ( keyValue == null ) return false;
        else return true;   
    }

    /** Return the value of the given keyword as a String, or null if not found. */
    public String getStringValue(String key) {
        return hdr.getStringValue(key);
    }
    
    /** Return the value of the given keyword as a String, or null if not found. */
    public String getStringValue(String key, String defaultValue) {
        String keyValue = hdr.getStringValue(key);
        if ( keyValue == null ) return defaultValue;
        else return keyValue;
    }


    /** Return the value of the given keyword as a double, or 0.0 if not found. */
    public double getDoubleValue(String key) {
        return hdr.getDoubleValue(key);
    }

    /** Return the value of the given keyword as a double, or 0.0 if not found. */
    public double getDoubleValue(String key, double defaultValue) {
        return hdr.getDoubleValue(key, defaultValue);
    }


    /** Return the value of the given keyword as a double, or 0.0 if not found. */
    public float getFloatValue(String key) {
        return hdr.getFloatValue(key);
    }

    /** Return the value of the given keyword as a double, or 0.0 if not found. */
    public float getFloatValue(String key, float defaultValue) {
        return hdr.getFloatValue(key, defaultValue);
    }


    /** Return the value of the given keyword as an int, or 0 if not found. */
    public int getIntValue(String key) {
        return hdr.getIntValue(key);
    }

    /** Return the value of the given keyword as an int, or 0 if not found. */
    public int getIntValue(String key, int defaultValue) {
        return hdr.getIntValue(key, defaultValue);
    }
    
}
