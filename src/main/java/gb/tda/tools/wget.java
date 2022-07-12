package gb.tda.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class wget {

    // Here is an example of a complex query:
    // ra=266; dec=-29; startDate=01-01-2013; endDate=31-12-2013; duration=3600; java wget "http://integral.codetda.esa.int/isocweb/tvp.html?action=predict&ra=$ra&dec=$dec&dither=R&startDate=$startDate&endDate=$endDate&duration=$minDuration" | tail -14 | head -1 | awk '{print $1}'

    public static void main(String[] args) throws Exception {
	String s;
	BufferedReader r = new BufferedReader(new InputStreamReader(new URL(args[0]).openStream()));
	int nLines = 0;
	while ((s = r.readLine()) != null) {
	    System.out.println(s);
	}
    }
}
