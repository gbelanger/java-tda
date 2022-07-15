package gb.tda.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import cern.colt.list.DoubleArrayList;
import org.apache.log4j.Logger;

public class JSONDataFileWriter {

    // Class variables
    private static Logger logger  = Logger.getLogger(JSONDataFileWriter.class);
    private PrintWriter writer;

    //  Constructor
    public JSONDataFileWriter(String filename) throws IOException {
	int bufferSize = 256000;
  	writer = new PrintWriter(new BufferedWriter(new FileWriter(filename), bufferSize));
    }

    public void write(DoubleArrayList xValues, DoubleArrayList yValues, DoubleArrayList zValues) throws JSONDataFileWriterException {
	xValues.trimToSize();
	yValues.trimToSize();
	zValues.trimToSize();
	double[] x = xValues.elements();
	double[] y = yValues.elements();
	double[] z = zValues.elements();
	if (x.length != y.length || x.length != z.length || y.length != z.length) {
	    throw new JSONDataFileWriterException("Number of elements in data arrays is not equal");
	}
	writer.println("{");
	int n = x.length;
	int iPlusOne = 0;
	int i=0;
	while (i < n-1) {
	    iPlusOne = i+1;
	    writer.println("\""+iPlusOne+"\":["+x[i]+","+y[i]+","+z[i]+"],");
	    i++;
	}
	iPlusOne = i+1;
	writer.println("\""+iPlusOne+"\":["+x[i]+","+y[i]+","+z[i]+"]");
	writer.println("}");
	writer.close();
    }

}
