package gb.tda.tools;

//import gb.codetda.io.AsciiDataFileReader;

 
public class TestCoordTools {

    public static void main(String[] args) throws Exception {
	double[] ras = new double[]{190.0, 187.5};
	double[] decs = new double[]{12.0, 10.0};
	// This works
	//ras = new double[] {0, 20, 40, 60, 80, 100, 120, 140, 160, 180, 200, 220, 240, 260, 280, 300, 320, 340, 360};
	//decs = new double[] {-90, -80, -70, -60, -50, -40, -30, -20, -10, 0, 10, 20, 30, 40, 50, 60, 70, 80, 90};
	// This works too
	AsciiDataFileReader in = new AsciiDataFileReader("raDecs.dat");
	ras = in.getDblCol(0);
	decs = in.getDblCol(1);
	// double[] centre1 = CoordUtils.getCentre(ras, decs);
	// System.out.println(centre1[0]+"	"+centre1[1]);
	// double[] centre2 = CoordUtils.getCentreUnitVectors(ras, decs);
	// System.out.println(centre2[0]+"	"+centre2[1]);


    }

}
