package jamaextension.jamax.unsupervised.subspace.nnmf.dtu;

import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;

public class NMF1 {

	public static Matrix[] mynmf1(Matrix a, int k, double tol, int maxiter) {

		int n = a.getRowDimension();
		int m = a.getColumnDimension();
		Matrix w0 = Matrix.rand(n, k);
		Matrix h0 = Matrix.rand(k, m);
		
		for (int j = 1; j <= maxiter; j++) {
			// Multiplicative update formula
			Matrix numer = w0.transpose().times(a);
			Matrix h = h0.arrayTimes(numer.arrayRightDivide(w0.transpose().times(w0).times(h0).plus(MathUtil.EPS)));
			numer = a.times(h.transpose());
			Matrix w = w0.arrayTimes(numer.arrayRightDivide(w0.times(h.times(h.transpose())).plus(MathUtil.EPS)));
			w0 = w;
			h0 = h;
			if (a.minus(w.times(h)).norm() < tol) {
				break;
			}
		}
		return new Matrix[] { w0, h0 };
	}

	public static void main(String[] args) {
		int n = 100;
		int m = 80;
		int k = 30;
		int maxiter = 200;
		double tol = 1e-8;

		Matrix a = Matrix.rand(n, m).scale(10).round();
		Matrix[] WH = mynmf1(a, k, tol, maxiter);

		double re = a.minus(WH[0].times(WH[1])).norm();
		System.out.println("re = " + re);
	}

}
