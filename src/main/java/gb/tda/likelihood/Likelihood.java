package gb.tda.likelihood;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import gb.tda.io.AsciiDataFileWriter;
import gb.tda.tools.MinMax;

public abstract class Likelihood {

    public double deltaX = 1e-1;

    public double getDeltaX() { return this.deltaX; }

    public void setDeltaX(double newDeltaX)  { this.deltaX = newDeltaX; }

    public double[] getXValues(double xMin, double xMax) {
		int nValues = (int) Math.floor((xMax - xMin)/this.deltaX) + 1;
		double[] xValues = new double[nValues];
		for ( int i=0; i < nValues; i++ ) {
		    xValues[i] = xMin + i*this.deltaX;
		}
		return xValues;
    }

    /**  Draw the likelihood function  **/
    public void drawFunction(double[] parameterValues, double[] function, String filename) throws IOException {
		String yLabel = "Likelihood";
		drawFunction(parameterValues, function, yLabel, filename);
    }

    public void drawFunction(double[] parameterValues, double[] function, String yLabel, String filename) throws IOException {
		String[] header = getHeader(yLabel);
		AsciiDataFileWriter out = new AsciiDataFileWriter(filename);
		out.writeData(header, parameterValues, function);
    }

    public void drawFunction(double[] parameterValues, double[] function, String xLabel, String yLabel, String filename) throws IOException {
		String[] header = getHeader(xLabel, yLabel);
		AsciiDataFileWriter out = new AsciiDataFileWriter(filename);
		out.writeData(header, parameterValues, function);
    }

    public void drawFunction(double[] parameterValues, double[] function, String xLabel, String yLabel, String plotLabel, String filename) throws IOException {
		String[] header = getHeader(xLabel, yLabel, plotLabel);
		AsciiDataFileWriter out = new AsciiDataFileWriter(filename);
		out.writeData(header, parameterValues, function);
    }

    public void drawFunction(double[] parameterValues, double[] function, String xLabel, String yLabel, String[] plotLabels, String filename) throws IOException {
		String[] header = getHeader(xLabel, yLabel, plotLabels);
		AsciiDataFileWriter out = new AsciiDataFileWriter(filename);
		out.writeData(header, parameterValues, function);
    }


    public void drawTwoFunctions(double[] parameterValues, double[] function1, double[] function2, String filename) throws IOException {
		String[] header = getHeader("Likelihood");
		AsciiDataFileWriter out = new AsciiDataFileWriter(filename);
		out.writeData(header, parameterValues, function1, function2);
    }

    public void drawTwoFunctions(double[] parameterValues1, double[] function1, double[] parameterValues2, double[] function2, String filename) throws IOException {
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
		//  Get and print the header
		double xmin = Math.min(MinMax.getMin(parameterValues1), MinMax.getMin(parameterValues2));
		double xmax = Math.max(MinMax.getMax(parameterValues1), MinMax.getMax(parameterValues2));
		String[] header = getHeader("Likelihood", xmin, xmax);
		for ( int i=0; i < header.length; i++ ) {
		    printWriter.println(header[i]);
		}
		//  Print the first function
	 	for ( int i=0; i < parameterValues1.length; i++ ) {
		    printWriter.println(parameterValues1[i] +"	"+ function1[i] +"	");
		}
		printWriter.println("NO NO");
		//  Print the second function
	 	for ( int i=0; i < parameterValues2.length; i++ ) {
		    printWriter.println(parameterValues2[i] +"	"+ function2[i] +"	");
		}
		printWriter.close();
    }

    public void drawThreeFunctions(double[] x1, double[] f1, double[] x2, double[] f2, double[] x3, double[] f3, String filename) throws IOException {
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
		//  Get and print the header
		double xmin = Math.min(MinMax.getMin(x1), MinMax.getMin(x2));
		xmin = Math.min(xmin, MinMax.getMin(x3));
		double xmax = Math.max(MinMax.getMax(x1), MinMax.getMax(x2));
		xmax = Math.max(xmax, MinMax.getMax(x3));
		String[] header = getHeader("Likelihood", xmin, xmax);
		for ( int i=0; i < header.length; i++ ) {
		    printWriter.println(header[i]);
		}
		//  Print the first function
	 	for ( int i=0; i < x1.length; i++ ) {
		    printWriter.println(x1[i] +"	"+ f1[i] +"	");
		}
		printWriter.println("NO NO");
		//  Print the second function
	 	for ( int i=0; i < x2.length; i++ ) {
		    printWriter.println(x2[i] +"	"+ f2[i] +"	");
		}
		printWriter.println("NO NO");
		//  Print the third function
	 	for ( int i=0; i < x3.length; i++ ) {
		    printWriter.println(x3[i] +"	"+ f3[i] +"	");
		}
		printWriter.close();
    }

    public String[] getHeader(String yLabel) {
		String xLabel = "Parameter Value";
		String[] header = new String[] {
		    "DEV /XS",
		    "READ 1",
		    "LAB T", "LAB F",
		    "TIME OFF",
		    "LINE ON",
		    "LW 3", "CS 1.2",
		    "LAB X "+xLabel,
		    "LAB Y "+yLabel,
		    "VIEW 0.2 0.1 0.8 0.9",
		    "SKIP SINGLE",
		    "PLOT OVER",
		    "!"
		};
		return header;
    }

    public String[] getHeader(String xLabel, String yLabel) {
		String[] header = new String[] {
		    "DEV /XS",
		    "READ 1",
		    "LAB T", "LAB F",
		    "TIME OFF",
		    "LINE ON",
		    "LW 3", "CS 1.2",
		    "LAB X "+xLabel,
		    "LAB Y "+yLabel,
		    "VIEW 0.2 0.1 0.8 0.9",
		    "SKIP SINGLE",
		    "PLOT OVER",
		    "!"
		};
		return header;
    }

    public String[] getHeader(String xLabel, String yLabel, String plotLabel) {
		String[] header = new String[] {
		    "DEV /XS",
		    "READ 1",
		    "LAB T", "LAB F",
		    "TIME OFF",
		    "LINE ON",
		    "LW 3", "CS 1.3",
		    "LAB X "+xLabel,
		    "LAB Y "+yLabel,
		    plotLabel,
		    "VIEW 0.2 0.1 0.8 0.9",
		    "VIEW 0.1 0.2 0.9 0.8",		    
		    "SKIP SINGLE",
		    "PLOT OVER",
		    "!"
		};
		return header;
    }

    public String[] getHeader(String xLabel, String yLabel, String[] plotLabels) {
		String[] head = new String[] {
		    "DEV /XS",
		    "READ 1",
		    "LAB T", "LAB F",
		    "TIME OFF",
		    "LINE ON",
		    "LW 3", "CS 1.2",
		    "LAB X "+xLabel,
		    "LAB Y "+yLabel,
		    "VIEW 0.2 0.1 0.8 0.9",
		    "SKIP SINGLE",
		    "PLOT OVER",
		};
		String[] header = new String[head.length + plotLabels.length];
		System.arraycopy(head, 0, header, 0, head.length);
		System.arraycopy(plotLabels, 0, header, head.length, plotLabels.length);
		return header;
    }


    public String[] getHeader(String yLabel, double xmin, double xmax) {
		String xLabel = "Parameter Value";
		String[] header = new String[] {
		    "DEV /XS",
		    "READ 1",
		    "LAB T", "LAB F",
		    "TIME OFF",
		    "LINE ON",
		    "LW 3", "CS 1.2",
		    "LAB X "+xLabel,
		    "LAB Y "+yLabel,
		    "VIEW 0.2 0.1 0.8 0.9",
		    "R X "+xmin+" "+xmax,
		    "SKIP SINGLE",
		    "PLOT OVER",
		    "!"
		};
		return header;
    }

    public String[] getHeader(String xLabel, String yLabel, double xmin, double xmax) {
		String[] header = new String[] {
		    "DEV /XS",
		    "READ 1",
		    "LAB T", "LAB F",
		    "TIME OFF",
		    "LINE ON",
		    "LW 3", "CS 1.2",
		    "LAB X "+xLabel,
		    "LAB Y "+yLabel,
		    "VIEW 0.2 0.1 0.8 0.9",
		    "R X "+xmin+" "+xmax,
		    "SKIP SINGLE",
		    "PLOT OVER",
		    "!"
		};
		return header;
    }

    public String[] getHeader(String xLabel, String yLabel, String plotLabel, double xmin, double xmax) {
		String[] header = new String[] {
		    "DEV /XS",
		    "READ 1",
		    "LAB T", "LAB F",
		    "TIME OFF",
		    "LINE ON",
		    "LW 3", "CS 1.2",
		    "LAB X "+xLabel,
		    "LAB Y "+yLabel,
		    plotLabel,
		    "VIEW 0.2 0.1 0.8 0.9",
		    "R X "+xmin+" "+xmax,
		    "SKIP SINGLE",
		    "PLOT OVER",
		    "!"
		};
		return header;
    }


}
