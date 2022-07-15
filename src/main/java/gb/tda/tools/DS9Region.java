package gb.tda.tools;

import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;


public class DS9Region {

    private static Logger logger = Logger.getLogger(DS9Region.class);

    private static DecimalFormat decimal = new DecimalFormat("0.00000");

    private BufferedReader br;
    private String coordinateSystem = null;
    private Point2D.Double[] coordinates = null;
    private String[] labels = null;
    private int nPoints = 0;
    private boolean coordinateSystemIsSet = false;
    private boolean coordinatesAreSet = false;
    private boolean labelsAreSet = false;

    private static String sep = File.separator;


    //  Constructors
    public DS9Region(String filename) throws IOException  {

	readRegionFile(filename);
    }

    public DS9Region(String coordinatesystem, Point2D.Double[] coordinates) {

	setCoordinateSystem(coordinatesystem);
	setCoordinates(coordinates);
	setLabels("");
    }

    public DS9Region(String coordinatesystem, Point2D.Double[] coordinates, String[] labels) {

	setCoordinateSystem(coordinatesystem);
	setCoordinates(coordinates);
	setLabels(labels);
    }



    //  Private methods
    private void setCoordinateSystem(String coordinateSystem) {

	this.coordinateSystem = coordinateSystem;
	this.coordinateSystemIsSet = true;
    }

    private void setCoordinates(Point2D.Double[] coordinates) {

	this.nPoints = coordinates.length;
	this.coordinates = new Point2D.Double[nPoints];
	for (int i=0; i < nPoints; i++) {
	    this.coordinates[i] = new Point2D.Double(coordinates[i].getX(), coordinates[i].getY());
	}
	this.coordinatesAreSet = true;
    }

    private void setLabels(String[] labels) {

	int nLabels = labels.length;
	if (nLabels != this.nPoints) {
	    logger.warn("Cannot set labels: The number of labels is not equal to the number of points");
	}
	else {
	    this.labels = new String[this.nPoints];
	    for (int i=0; i < nPoints; i++) {
		this.labels[i] = labels[i];
	    }
	}
	this.labelsAreSet = true;
    }

    private void setLabels(String label) {

	this.labels = new String[this.nPoints];
	for (int i=0; i < nPoints; i++) {
	    this.labels[i] = label;
	}
	this.labelsAreSet = true;
    }

    public void readRegionFile(String filename) throws IOException {

	Vector<Point2D.Double> coordVector = new Vector<>();
	Vector<String> labelVector = new Vector<>();

	br = new BufferedReader(new FileReader(filename));
	String line = br.readLine();
	while (line != null) {
	    
	    //  Skip comment lines
	    if (line.startsWith("#") || line.startsWith("global")) {
		while (line.startsWith("#") || line.startsWith("global")) {
		    line = br.readLine();
		}
	    }

	    //  Read the coordinate system
	    if (line.startsWith("fk5")) {
		setCoordinateSystem("fk5");
	    }
	    else if (line.startsWith("gal")) {
		setCoordinateSystem("galactic");
	    }
	    else {
		logger.error("Unrecognised coordinate system: Not fk5 nor gal");
		System.exit(-1);
	    }

	    //  Read the coordinates and the labels
	    StringTokenizer tokenizer = new StringTokenizer(line);
	    String token = tokenizer.nextToken("(");
	    double lon = (java.lang.Double.valueOf(tokenizer.nextToken(","))).doubleValue();
	    double lat = (java.lang.Double.valueOf(tokenizer.nextToken(")"))).doubleValue();
	    Point2D.Double point = new Point2D.Double(lon, lat);
	    coordVector.add(point);
	    token = tokenizer.nextToken("{");
	    String label = tokenizer.nextToken("}");
	    labelVector.add(label);

	    //  Read the next line
	    line = br.readLine();
	}

	int nPoints = coordVector.size();
	Point2D.Double[] coords = new Point2D.Double[nPoints];
	String[] labs = new String[nPoints];
	for (int i=0; i < nPoints; i++) {
	    coords[i] = coordVector.elementAt(i);
	    labs[i] = labelVector.elementAt(i);
	}

	setCoordinates(coords);
	setLabels(labs);
    }



    //  Public Methods
    public String getCoordinateSystem() throws NullPointerException {

	if (coordinateSystemIsSet) return this.coordinateSystem;
	else throw new NullPointerException("Coordinate system is not set");
    }

    public Point2D.Double[] getCoordinates() throws NullPointerException {

	if (coordinatesAreSet) return this.coordinates;
	else throw new NullPointerException("Coordinates are not set");
    }

    public String[] getLabels() throws NullPointerException {

	if (! this.labelsAreSet) {
	    logger.warn("Labels are not set. Returning empty strings");
	}
	return this.labels;
    }

    public void write(String filename) throws IOException {

	File file = new File(filename);
	PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	
	pw.println("# Region file format: DS9 version 6.2");
	pw.println(this.coordinateSystem);
	for (int i=0; i < this.nPoints; i++) {
	    String line="point("+decimal.format(this.coordinates[i].getX())+", "+decimal.format(this.coordinates[i].getY())+") # point=cross text={"+this.labels[i]+"}";
	    pw.println(line);
	}
	pw.flush();
	pw.close();
    }


}
