package mpk_dsc;

import javax.vecmath.Vector2d;

/** A simple class for dealing with 2d matrices */
public class MyMatrix2d {

	public double m00 = 0;
	public double m01 = 0;
	public double m10 = 0;
	public double m11 = 0;

	/** Creates a new matrix: M(Row)(Col)
	 * M = [m00,  m01;
	 *      m10,  m11];
	 */
	public MyMatrix2d(double m00, double m01, double m10, double m11){
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
	}

	/** Create a matrix of zeros */
	public MyMatrix2d(){}
	
	/** In place matrix inversion */
	public void invert(){
		double det = m00*m11 - m01*m10;
		double a = m11;
		double b = -m01;
		double c = -m10;
		double d = m00;
		m00 = a/det;
		m01 = b/det;
		m10 = c/det;
		m11 = d/det;
	}
	
	/** Return a new matrix, equal to the inverse of this matrix*/
	public MyMatrix2d getInverse(){
		double det = m00*m11 - m01*m10;
		double a = m11;
		double b = -m01;
		double c = -m10;
		double d = m00;
		return new MyMatrix2d(a/det, b/det, c/det, d/det);
	}
	
	/** Computes the dot product with a vector 2d 
	 *  q = M*v  */
	public Vector2d dot(Vector2d v){
		Vector2d q = new Vector2d(); 
		q.x = m00*v.x + m01*v.y;
		q.y = m10*v.x + m11*v.y;
		return q;
	}
	
}
