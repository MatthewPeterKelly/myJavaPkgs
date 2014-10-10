package mpk_dsc;

import javax.vecmath.Vector2d;

public class MyMath {

	/** 
	 * Solves a system of two linear equations for x:
	 * [b0; b1] = [a00, a01;  a10, a11]*[x0;x1]   (Matlab conventions)
	 * b = Ax
	 * x = A\b
	 */
	public static double[] LinSolve(double a00, double a01, double a10, double a11,
			double b0, double b1){
		double det = a00*a11 - a01*a10;
		if (det==0) {
			System.out.println("WARNING -- Divide by Zero in MyMath!");
			return null;
		}
		double x0 = -(a01*b1 - a11*b0)/det;
		double x1 = (a00*b1 - a10*b0)/det;
		return new double[] {x0, x1};
	}

	/**
	 * Solves a system of two linear equations for bounded x:
	 * [b0; b1] = [a00, a01;  a10, a11]*[x0;x1]   (Matlab conventions)
	 * b = Ax
	 * x = A\b
	 * xLow < x < xUpp
	 */
	public static double[] LinSolveBnd(double[] a0, double[] a1, double[] b,
			double[] xLow, double[] xUpp){
		double [] x  = LinSolve(a0[0], a1[0], a0[1], a1[1], b[0], b[1]);
		for (int i=0; i<2; i++){  // Saturate vector before returning
			if (x[i]<xLow[i]) x[i] = xLow[i];
			if (x[i]>xUpp[i]) x[i] = xUpp[i];
		}
		return x;
	}

	/** Quadratic root solve 
	 * 0 = a*x*x + b*x + c*/
	public static double[] QuadraticSolve(double a, double b, double c){
		double d = b*b-4*a*c; // determinant

		if (d==0){
			double x = -b/2*a;
			return new double[] {x, x};
		} else if (d < 0) {
			return null;  // don't support imaginary roots
		} else {  // d>0
			double z = -0.5*(b+Math.signum(b)*Math.sqrt(d));
			double x1 = z/a; double x2 = c/z;
			return new double[] {x1, x2};
		}
	}

	/** Circle Intersection 
	 * @param x1 - circle 1, center coordinate, x-axis
	 * @param y1 - circle 1, center coordinate, y-axis
	 * @param r1 - circle 1, radius
	 * @param x2 - circle 2, center coordinate, x-axis
	 * @param y2 - circle 2, center coordinate, y-axis
	 * @param r2 - circle 2, radius
	 * @return the two solutions: {Pt1, Pt2} or null if no solution.
	 */
	public static double[][] CircleIntersection(double x1, double y1, double r1,
			double x2, double y2, double r2){

		double X = x1 - x2;
		double Y = y1 - y2;

		double d = Math.sqrt(X*X+Y*Y);
		double th = -Math.atan2(Y,X);

		/* Solve the problem in the reduced coordinates:
		 *		
		 * u*u + v*v = r1*r1
		 * (u-d)*(u-d) + v*v = r2*r2
		 * 
		 * uStar = (d*d + r1*r1 - r2*r2)/(2*d);
		 * vStar = (+-)Math.sqrt(r1*r1-uStar*uStar);
		 */

		double u = (d*d + r1*r1 - r2*r2)/(2*d);
		double vv = r1*r1-u*u;

		if (vv<0){  // The roots are imaginary... 
			return null;   
		} else {

			double v1 = Math.sqrt(vv);
			double v2 = -Math.sqrt(vv);

			/// Transform solution to original coordinate system:
			double sin = Math.sin(th);
			double cos = Math.cos(th);
			double xStar1 = x2 + u*cos + v1*sin;
			double yStar1 = y2 - u*sin + v1*cos;
			double xStar2 = x2 + u*cos + v2*sin;
			double yStar2 = y2 - u*sin + v2*cos;

			double[] Pt1 = new double[] {xStar1, yStar1};
			double[] Pt2 = new double[] {xStar2, yStar2};
			return new double[][] {Pt1, Pt2};
		}

	}
	
	
	/**
	 * Evaluate a Bezier curve in 2D
	 * @param p - an array of points to define the curve
	 * @param t - parametric distance along curve
	 */
	public static Vector2d BezierEval(Vector2d[] p, double t){
		return BezierKernel(p,t,0,p.length-1);
	}

	/**
	 * Recursive evaluation of a bezier curve
	 * @param p - an array of points to define the curve
	 * @param t - parametric distance along curve
	 * @param iLow - low index (used for recursion)
	 * @param iUpp - upp index (used for recursion)
	 * @return the value of the bezier curve at t
	 */
	private static Vector2d BezierKernel(Vector2d[] p, double t, int iLow, int iUpp) {
		if (iLow==iUpp){ // end recursion
			return new Vector2d(p[iLow]);
		} else {
			Vector2d b1 = BezierKernel(p,t,iLow,iUpp-1); b1.scale(1-t);
			Vector2d b2 = BezierKernel(p,t,iLow+1,iUpp); b2.scale(t);
			b1.add(b2);
			return b1;
		}
	}
	
	/** Evaluate the derivative of a Bezier Curve 
	 * @param p - an array of points to define the curve
	 * @param t - parametric distance along curve
	 */
	public static Vector2d BezierDeriv(Vector2d[] p, double t){
		return BezierDerivKernel(p,t,0,p.length-1);
	}
	
	/**
	 * Recursive evaluation of a bezier curve
	 * @param p - an array of points to define the curve
	 * @param t - parametric distance along curve
	 * @param iLow - low index (used for recursion)
	 * @param iUpp - upp index (used for recursion)
	 * @return the value of the bezier curve at t
	 */
	private static Vector2d BezierDerivKernel(Vector2d[] p, double t, int iLow, int iUpp) {
		if (iLow==iUpp){ // end recursion
			return new Vector2d(0,0);
		} else {
			Vector2d b1 = BezierDerivKernel(p,t,iLow,iUpp-1); b1.scale(1-t);
			b1.sub(BezierKernel(p,t,iLow,iUpp-1));
			
			Vector2d b2 = BezierDerivKernel(p,t,iLow+1,iUpp); b2.scale(t);
			b1.add(BezierKernel(p,t,iLow+1,iUpp));
			
			b1.add(b2);
			return b1;
		}
	}

	/** Evaluates the cubic polynomial of the form:
	 * y = a*x^3 + b*x^2 + c*x + d	 */
	public static double cubicEval(double x, 
			double a, double b, double c, double d){
		return d + x*(c + x*(b + x*a));
	}

	/** Solves for the roots of a cubic polynomial on the given interval using 
	 * Ridder's method. If no root is found then the returns null. If the
	 * function does not change sign between the bounds of the interval then
	 * null is returned. Algorithm taken from Numerical Recipes in C. If the
	 * maximum allowable iterations are reached, it returns the best solution
	 * and does not issue a warning.
	 * y = a*x^3 + b*x^2 + c*x + d */
	public static Double cubicRoot(double xLow, double xUpp,
			double a, double b, double c, double d){

		/// Initialization
		double fLow = cubicEval(xLow,a,b,c,d);
		double fUpp = cubicEval(xUpp,a,b,c,d);
		double xMid, fMid, xNew, fNew;
		double s;
		Double soln = null;

		/// Check that the root is bounded
		if ( (fLow > 0.0 && fUpp < 0.0) || (fLow < 0.0 && fUpp > 0.0) ){
			for (int iter=0; iter < maxIter; iter++){

				/// Compute two intermediate points
				xMid = 0.5*(xLow + xUpp);
				fMid = cubicEval(xMid,a,b,c,d);
				s = Math.sqrt(fMid*fMid - fLow*fUpp);
				if (s==0.0) return soln;
				xNew=xMid+(xMid-xLow)*((fLow >= fUpp ? 1.0 : -1.0)*fMid/s);
				if (iter>0) if (Math.abs(xNew-soln) <= rootTol) return soln;
				soln = xNew;
				fNew = cubicEval(soln,a,b,c,d);
				if (fNew == 0.0) return soln;

				/// Update bracketing of root
				if (Math.signum(fMid) != Math.signum(fNew)){
					xLow = xMid;
					fLow = fMid;
					xUpp = soln;
					fUpp = fNew;
				} else if (Math.signum(fLow) != Math.signum(fNew)){
					xUpp = soln;
					fUpp = fNew;
				} else if (Math.signum(fUpp) != Math.signum(fNew)){
					xLow = soln;
					fLow = fNew;
				} else	return null;  // NEVER GET HERE	

				/// Check convergence
				if (Math.abs(xUpp-xLow) <= rootTol) return soln;

			}
		} else { /// Root was not bounded, check edge cases
			if (fLow == 0.0){
				soln = xLow;
			} else if (fUpp == 0.0){
				soln = xUpp;
			} else {
				soln = null;
			}
		}

		return soln;  // WARNING - only get here if maxIter is exceeded
	}
	


}
