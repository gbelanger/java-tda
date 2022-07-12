package gb.tda.tools;

import java.util.Calendar;
import java.text.SimpleDateFormat;

public class DateUtils {
 
 public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
 public static final String TIME_FORMAT_NOW = "HH:mm:ss";

  public static String dateAndTime() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    return sdf.format(cal.getTime());

  }

  public static String time() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_NOW);
    return sdf.format(cal.getTime());

  }

  public static void  main(String arg[]) {
    System.out.println("Now : " + DateUtils.dateAndTime());
  }

}
