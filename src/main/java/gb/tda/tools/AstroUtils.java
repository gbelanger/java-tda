package gb.tda.tools;


/**
 *
 * @version  August 2010 (last modified)
 * @author   Guillaume Belanger (ESAC, Spain)
 *
 **/

public final class AstroUtils {

    // Constants
    static double c = 2.99792458e+10; // [ cm/s ] Speed of light
    static double G = 6.67259E-8;    // [ cm^3/(gram*s^2) ] Newton's constant
    static double k = 1.380658E-16;  // [ erg/K ] Boltzman's constant
    static double h_erg = 6.6260755E-27; // [ erg s ] Plank's constant
    static double h_keV = 4.13563E-18;; // [ keV s ] Plank's constant
    static double Msun = 1.99E33;    // [ gram ] Solar mass
    static double AU =  1.496E13;    // [ cm ] astronomical unit

    public static double getRlso(double mass, double spin) {

	double M = mass;
	double a = spin;
	
	double expo = 1.0/3.0;
	
	double z1 = 1 + Math.pow((1-a*a), expo)*( Math.pow((1+a), expo) + Math.pow((1-a), expo) );
	double z2 = Math.sqrt(3*a*a + z1*z1);
	double rs = 2*G*M/(c*c);
	double rlso = (rs/2) * ( 3 + z2 - Math.sqrt( (3-z1)*(3+z1+2*z2) ) );
	
	return rlso;
    }

}
