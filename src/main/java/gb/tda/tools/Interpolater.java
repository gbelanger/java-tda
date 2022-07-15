package gb.tda.tools;

import javax.media.jai.InterpolationBicubic2;
import nom.tam.util.ArrayFuncs;


public class Interpolater {

	public static float[][] interpolate(float[][] data, int x1, int y1, int x2, int y2) {
		/** 
		   This program performs a bicubic interpolation using a 4 x 4 grid. 
		   The interpolation is done at 4 points on each pixel and so returns a 2D-array 
		   that has 4 times the number of pixels as in the original array. 
		   The position of these 4 points is chosen such that the result is equivalent 
		   to subdividing the original pixel in 4.
		 
		   @param data double[][] 
		   @param xsize int specifying the x-size of sub-image 
		   @param ysize int specifying the y-size of sub-image 
		   @param raref double specifying the reference position in sub-image [CRPIX1]
		   @param decref double specifying the reference postion in sub-image [CRPIX2]
		 
		   @author Guillaume Belanger (SAp, CEA-Saclay)
		   @version 23 Mar 2005
		 
		 **/

		//  Determine the size of the data double array
		int[] dataDims = ArrayFuncs.getDimensions(data);
		int xsize = x2 - x1;  //dataDims[0];
		int ysize = y2 - y1;  //dataDims[1];

		//  Define and initialise interpValues
		float[][] interpValues = new float[2*xsize][2*ysize];
		for (int n=0; n < ysize; n++) 
		    for (int m=0; m < xsize; m++) interpValues[m][n] = 0;

		//  Interpolate data[][] and construct interpValue[][]
		InterpolationBicubic2 interpolator = new InterpolationBicubic2(8);
		int i = 0, j = 0;
		int xfirst = x1, xlast = x2;
		int yfirst = y1, ylast = y2;
		for (int y = yfirst; y < ylast; y++) {
		    while (j < 2*ysize) {
				for (int x = xfirst; x < xlast; x++) {
				    interpValues[i][j] = interpolator.interpolate
					(data[y-1][x-1], data[y][x-1], data[y+1][x-1], data[y+2][x-1],
					 data[y-1][x], data[y][x], data[y+1][x], data[y+2][x], 
					 data[y-1][x+1], data[y][x+1], data[y+1][x+1], data[y+2][x+1],
					 data[y-1][x+2], data[y][x+2], data[y+1][x+2], data[y+2][x+2], 
					 0.25F, 0.25F);
				    interpValues[i+1][j] = interpolator.interpolate
					(data[y-1][x-1], data[y][x-1], data[y+1][x-1], data[y+2][x-1],
					 data[y-1][x], data[y][x], data[y+1][x], data[y+2][x], 
					 data[y-1][x+1], data[y][x+1], data[y+1][x+1], data[y+2][x+1],
					 data[y-1][x+2], data[y][x+2], data[y+1][x+2], data[y+2][x+2], 
					 0.75F, 0.25F);
				    interpValues[i][j+1] = interpolator.interpolate
					(data[y-1][x-1], data[y][x-1], data[y+1][x-1], data[y+2][x-1],
					 data[y-1][x], data[y][x], data[y+1][x], data[y+2][x], 
					 data[y-1][x+1], data[y][x+1], data[y+1][x+1], data[y+2][x+1],
					 data[y-1][x+2], data[y][x+2], data[y+1][x+2], data[y+2][x+2], 
					 0.25F, 0.75F);
				    interpValues[i+1][j+1] = interpolator.interpolate
					(data[y-1][x-1], data[y][x-1], data[y+1][x-1], data[y+2][x-1],
					 data[y-1][x], data[y][x], data[y+1][x], data[y+2][x], 
					 data[y-1][x+1], data[y][x+1], data[y+1][x+1], data[y+2][x+1],
					 data[y-1][x+2], data[y][x+2], data[y+1][x+2], data[y+2][x+2], 
					 0.75F, 0.75F);
				    j += 2;
				}
				i += 2;
		    }
		    j = 0;
		}
		return interpValues;
	}
}
