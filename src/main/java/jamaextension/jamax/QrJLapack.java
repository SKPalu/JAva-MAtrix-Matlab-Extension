/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

import org.netlib.lapack.Dgeqrf;

//import edu.emory.mathcs.jplasma.tdouble.corelapack.*;

/*import org.netlib.lapack.DGEQP3;
import org.netlib.lapack.DGEQRF;
import org.netlib.lapack.DORGQR;*/
import org.netlib.util.intW;

/**
 * 
 * @author Feynman Perceptrons
 */
public class QrJLapack {

	private Matrix Q;
	private Matrix R;
	private int m;
	private int n;
	private Economy type;

	public QrJLapack(Matrix A) {
		this(A, null);
	}

	public QrJLapack(Matrix A, Economy econType) {
		if (A == null) {
			throw new IllegalArgumentException("QrJLapack : Parameter \"A\" must be non-null.");
		}
		if (econType != null) {
			this.type = econType;
		}
		qrJLap(A);
	}// end constructor

	private void qrJLap(Matrix A) {
		double[][] bb = A.getArrayCopy();// {{-0.3714, 0.5774}, {-0.7428,
											// 0.5774}, {-0.5571, 0.5774}};
		int M = A.getRowDimension();// bb.length;
		this.m = M;
		int N = A.getColumnDimension();// bb[0].length;
		this.n = N;

		double[][] q = new double[M][M];
		double[] tau = new double[Math.min(M, N)];
		double[] work = new double[Math.max(1, Math.max(M, N))];
		org.netlib.util.intW info = new org.netlib.util.intW(2);
		int i, j;

		// original line below
		// DGEQRF.DGEQRF(M, N, bb, tau, work, work.length, info);

		// The following line is commented out since the static original class
		// DGEQRF
		// has less number of arguments in it's dgeqrf static method.
		// Dgeqrf.dgeqrf(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8,
		// arg9, arg10);

		if (info.val != 0) {
			throw new RuntimeException("qrJLap : Error occured during call to DGEQRF.");
		}

		// System.out.println("dgeqrf info = " + info.val);

		this.R = new Matrix(bb).triu();
		// R.printInLabel("R1");

		for (i = 1; i < M; i++) {
			for (j = 0; j < Math.min(M, N); j++) {// for(j=0;j<i;j++) {
				q[i][j] = bb[i][j];
			}
		}

		// The following line is commented out as the original static class
		// DORGQR has less
		// number of arguments in it's static DORGQR method.
		// DORGQR.DORGQR(M, M, Math.min(M, N), q, tau, work, work.length, info);

		// System.out.println("dorgqr info = " + info.val);
		if (info.val != 0) {
			throw new RuntimeException("qrJLap : Error occured during call to DORGQR.");
		}

		this.Q = new Matrix(q);
	}

	public Matrix getQ() {
		Matrix qMod = null;
		if (type == null) {
			qMod = this.Q;
			// System.out.println("PRINT-1 Q");
		} else {
			if (this.m > this.n) {
				int[] arrInd = Indices.linspace(0, this.n - 1).getRowPackedCopy();
				qMod = this.Q.getColumns(arrInd);
				// System.out.println("PRINT-2 Q");
			} else {
				qMod = this.Q;
				// System.out.println("PRINT-3 Q");
			}
		}
		return qMod;
	}

	public Matrix getR() {
		Matrix rMod = null;
		if (type == null) {
			rMod = this.R;
			// System.out.println("PRINT-1 R");
		} else {
			if (this.m > this.n) {
				int[] arrInd = Indices.linspace(0, this.n - 1).getRowPackedCopy();
				rMod = this.R.getRows(arrInd);
				// System.out.println("PRINT-2 R");
			} else {
				rMod = this.R;
				// System.out.println("PRINT-3 R");
			}
		}
		return rMod;
	}

	public static enum Economy {

		ZERO;
	}

	static void examp1() {
		double[][] aa = { { 38, 48, 42, 17, 18, 14 }, { 13, 27, 13, 10, 42, 38 }, { 25, 7, 41, 13, 29, 38 },
				{ 35, 7, 12, 31, 27, 19 }, { 45, 13, 46, 24, 46, 28 } };

		Matrix A = new Matrix(aa);
		Matrix a = A.getRowAt(0);
		Matrix B = A.transpose();
		Matrix b = a.transpose();
		Matrix C = A.getMatrix(0, 4, 0, 4);

		Matrix D = C;

		QrJLapack QRJ = new QrJLapack(A);

		System.out.println("-------------- Q --------------");
		QRJ.getQ().print(8, 4);

		System.out.println("-------------- R --------------");
		QRJ.getR().print(8, 4);
	}

	static void examp2() {
		double[][] aa = { { 38, 48, 42, 17, 18, 14 }, { 13, 27, 13, 10, 42, 38 }, { 25, 7, 41, 13, 29, 38 },
				{ 35, 7, 12, 31, 27, 19 }, { 45, 13, 46, 24, 46, 28 } };

		Matrix A = new Matrix(aa);
		int m = A.getRowDimension();
		int n = A.getColumnDimension();
		double[] a = A.toRowVector().getArray()[0];

		int _a_offset = 0;
		int lda = m;
		int[] jpvt = new int[n];
		int _jpvt_offset = 0;
		double[] tau = new double[Math.min(m, n)];
		int _tau_offset = 0;
		double[] work = new double[3 * n + 1];
		int _work_offset = 0;
		int lwork = 3 * n + 1;

		intW info = new intW(2);

		// The following line is commented out as the original static class
		// DGEQP3 has less
		// number of arguments in it's static DGEQP3 method.
		// DGEQP3.DGEQP3(m, n, aa, jpvt, work, work, n, info);

		/*
		 * Dgeqp3.dgeqp3(m, n, a, _a_offset, lda, jpvt, _jpvt_offset, tau,
		 * _tau_offset, work, _work_offset, lwork, info);
		 */

	}

	public static void main(String[] args) {
		// double[][] aa = {{14.2519, /*3.4671*/0, 9.1026, 7.2897, /*13.3695*/0,
		// 11.4315, 6.8470}};
		/*
		 * double[][] aa = {{-0.3714, 0.5774}, {-0.7428, 0.5774}, {-0.5571,
		 * 0.5774}}; Matrix A = new Matrix(aa);
		 * 
		 * Matrix B = A.transpose();
		 */
		examp1();

	}

	/*
	 * =========================================================================
	 * = ==
	 * ========================================================================
	 * ==
	 * ========================================================================
	 * ==
	 * ========================================================================
	 * ==
	 * ========================================================================
	 */
	/*
	 * Original method
	 */
	private void qrJLap_original(Matrix A) {
		double[][] bb = A.getArrayCopy();// {{-0.3714, 0.5774}, {-0.7428,
											// 0.5774}, {-0.5571, 0.5774}};
		int M = A.getRowDimension();// bb.length;
		this.m = M;
		int N = A.getColumnDimension();// bb[0].length;
		this.n = N;

		double[][] q = new double[Math.max(M, N)][Math.max(M, N)];
		double[] tau = new double[Math.min(M, N)];
		double[] work = new double[Math.max(1, Math.max(M, N))];
		org.netlib.util.intW info = new org.netlib.util.intW(2);
		int i, j;

		// The following line is commented out as the original static class
		// DGEQRF has less
		// number of arguments in it's static DGEQRF method.
		// DGEQRF.DGEQRF(M, N, bb, tau, work, work.length, info);

		if (info.val != 0) {
			throw new RuntimeException("QrJLapack : Error occured during call to DGEQRF.");
		}

		// System.out.println("dgeqrf info = " + info.val);
		// int[] arr = Indices.intLinspaceIncrement(0,
		// Math.min(M,N)).getRowPackedCopy();
		this.R = new Matrix(bb).triu();

		/*
		 * for(i=0;i<Math.min(M,N);i++) { for(j=0;j<i;j++)
		 * System.out.print("               0.0 "); for(j=i;j<N;j++) {
		 * System.out.print(bb[i][j] + " "); } System.out.println(); }
		 */

		// for(i=Math.min(M,N);i<Math.max(M,N);i++)
		// for(j=0;j<N;j++)
		// System.out.print(" 0.0 ");
		// System.out.println();

		/*
		 * for(i=1;i<M;i++) { for(j=0;j<i;j++) { q[i][j] = bb[i][j]; } }
		 */

		for (i = 1; i < M; i++) {
			for (j = 0; j < Math.min(M, N); j++) {// for(j=0;j<i;j++) {
				q[i][j] = bb[i][j];
			}
		}

		// The following line is commented out as the original static class
		// DORGQR has less number of arguments in it's static DORGQR method.
		// DORGQR.DORGQR(M, M, Math.min(M, N), q, tau, work, work.length, info);

		// System.out.println("dorgqr info = " + info.val);
		if (info.val != 0) {
			throw new RuntimeException("QrJLapack : Error occured during call to DORGQR.");
		}

		/*
		 * for(i=0;i<M;i++) { for(j=0;j<M;j++) { System.out.print(q[i][j] +
		 * " "); } System.out.println(); }
		 */

		// SEE the comment after the following method call.
		this.Q = new Matrix(q);

		this.Q = this.Q.getMatrix(0, M - 1, 0, M - 1);

	}
}// ---------------------------- End Class Definition
	// ---------------------------
