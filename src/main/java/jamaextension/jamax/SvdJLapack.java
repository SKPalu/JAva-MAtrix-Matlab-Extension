/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.netlib.lapack.Dgesvd;

/**
 * 
 * @author Feynman Perceptrons
 */
public class SvdJLapack {

	private Matrix U;
	private Matrix S;
	private Matrix V;
	private int m;
	private int n;
	private Economy type;

	public SvdJLapack(Matrix X, Economy econType) {
		if (X == null) {
			throw new IllegalArgumentException("LapackSVD : Parameter \"X\" must be non-null.");
		}
		double[][] A = X.getArrayCopy();
		if (econType != null) {

			this.type = econType;
		}
		computeSVD(A);
	}

	public SvdJLapack(Matrix X) {
		this(X, null);

	}

	private void computeSVD(double[][] A) {

		m = A.length;
		n = A[0].length;
		// double[] s = new double[A.length]; //==> old line [commented on
		// 17/03/2010]
		double[] s = new double[Math.min(m, n)]; // ==> new line [added on
													// 17/03/2010]
		double[][] u = new double[m][m];
		double[][] vt = new double[n][n];

		double[] work = new double[Math.max(3 * Math.min(m, n) + Math.max(m, n), 5 * Math.min(m, n))];

		org.netlib.util.intW info = new org.netlib.util.intW(2);

		// The following line is commented out since the static original class
		// DGESVD
		// has less number of arguments in it's DGESVD static method.

		// DGESVD.DGESVD("A", "A", m, n, A, s, u, vt, work, work.length, info);

		// System.out.println("info = " + info.val);

		U = new Matrix(u);

		/*
		 * ================== Block Commented on 17/03/2010
		 * ===================== int minMN = Math.min(m, n); if (minMN < m) {
		 * int[] arr = Indices.linspace(0, minMN - 1).getRowPackedCopy(); U =
		 * U.getColumns(arr); }
		 * ==================================================
		 * ====================
		 */
		// U.printInLabel("U");

		V = new Matrix(vt).transpose();

		// S = new Matrix(s).diagonal(); //==> old line [17/03/2010]
		S = new Matrix(s); // ==> new line [17/03/2010]

		/*
		 * //================== Block Commented on 17/03/2010
		 * ===================== int sRow = S.getRowDimension(); int sLength =
		 * S.getColumnDimension(); if (sLength < V.length()) { S = S.mergeH(new
		 * Matrix(sRow, V.length() - sLength)); }
		 */
		// ======================================================================*/

		/*
		 * S = new Matrix(m,n); for(int i=0; i<m; i++){ //for(int j=0; j<n;
		 * j++){ //if(i==j){ S.set(i, j, s); } //} S.set(i, i, s[i]); }
		 */
		// S.printInLabel("S");

		// V.printInLabel("V");
	}

	public Matrix getU() {
		Matrix uu = null;
		if (type == null) {
			uu = U;
			// System.out.println("Block #1 - U");
		} else {
			if (type == Economy.ZERO) {// instanceof Integer) {
				if (m > n) {
					int[] indArr = Indices.linspace(0, n - 1).getRowPackedCopy();
					uu = U.getColumns(indArr);
					// System.out.println("Block #2 - U");
				} else {
					uu = U;
					// System.out.println("Block #3 - U");
				}
			} else {
				if (m >= n) {
					int[] indArr = Indices.linspace(0, n - 1).getRowPackedCopy();
					uu = U.getColumns(indArr);
					// System.out.println("Block #4 - U");
				} else {
					uu = U;
					// System.out.println("Block #5 - U");
				}
			}
		}

		/*
		 * if (type != null) { int[] arr = null; if (type instanceof Integer) {
		 * if (m > n) { arr = Indices.linspace(0, n - 1).getRowPackedCopy(); uu
		 * = U.getColumns(arr); } } else { if (m >= n) { arr =
		 * Indices.linspace(0, n - 1).getRowPackedCopy(); uu =
		 * U.getColumns(arr); } }
		 * 
		 * }
		 */

		return uu;

	}

	public Matrix getS() {
		Matrix ss = null;
		double val = 0.0;
		if (type == null) {
			ss = new Matrix(m, n);
			for (int i = 0; i < S.length(); i++) {
				val = S.get(0, i);
				ss.set(i, i, val);
			}
		} else {
			if (type == Economy.ZERO) {// instanceof Integer) {
				if (m > n) {
					ss = new Matrix(n, n);
					for (int i = 0; i < n; i++) {
						val = S.get(0, i);
						ss.set(i, i, val);
					}
				} else {
					ss = new Matrix(m, n);
					for (int i = 0; i < S.length(); i++) {
						val = S.get(0, i);
						ss.set(i, i, val);
					}
				}
			} else {
				if (m >= n) {
					ss = new Matrix(n, n);
					for (int i = 0; i < n; i++) {
						val = S.get(0, i);
						ss.set(i, i, val);
					}
				} else {
					ss = new Matrix(m, m);
					for (int i = 0; i < m; i++) {
						val = S.get(0, i);
						ss.set(i, i, val);
					}
				}
			}
		}
		return ss;
	}

	public Matrix getV() {
		Matrix vv = null;
		if (type == null) {
			vv = V;
		} else if (type == Economy.ECON) {// instanceof String) {
			if (m < n) {
				int[] indArr = Indices.linspace(0, m - 1).getRowPackedCopy();
				vv = V.getColumns(indArr);
			} else {
				vv = V;
			}
		} else {
			vv = V;
		}

		return vv;
	}

	public static enum Economy {

		ZERO, ECON;
	}

	static void examp1() {
		double[][] aa = { { 38, 48, 42, 17, 18, 14 }, { 13, 27, 13, 10, 42, 38 }, { 25, 7, 41, 13, 29, 38 },
				{ 35, 7, 12, 31, 27, 19 }, { 45, 13, 46, 24, 46, 28 } };

		Matrix A = new Matrix(aa);
		Matrix B = A.transpose();
		Matrix C = A.getMatrix(0, 4, 0, 4);

		SvdJLapack svd = new SvdJLapack(C, Economy.ECON);

		Matrix u = svd.getU();
		Matrix s = svd.getS();
		Matrix v = svd.getV();

		u.printInLabel("U");
		s.printInLabel("S");
		v.printInLabel("V");
	}

	static void examp2() {
		double[][] d = { { -0.3964, -0.1993, -0.6569, 0.4359, 0.1691, -0.1901 },
				{ -0.4351, -0.0716, -0.0220, -0.3736, 0.0631, 0.4567 },
				{ -0.0580, 0.5520, -0.1403, 0.1008, 0.5230, 0.0089 },
				{ -0.4448, 0.1848, 0.5750, 0.2635, 0.1322, 0.1768 },
				{ -0.3094, -0.1926, -0.2344, -0.3263, 0.1346, 0.2585 },
				{ -0.0483, 0.5738, -0.2447, -0.0976, -0.4822, -0.1569 } };
		Matrix Q1 = new Matrix(d);

		Q1.printInLabel("Q1");

		SvdJLapack svd = new SvdJLapack(Q1);
		Matrix u = svd.getU();
		u.printInLabel("U");
	}

	static void examp3() {
		Matrix[] Q1R = fileReadExample3();
		Matrix Q11 = Q1R[0];
		Matrix Uu11 = Q1R[1];

		// QQ11.EQ(Q1, 1.0E-8).printInLabel("QQ11 == Q1", 0);

		SvdJLapack svd = new SvdJLapack(Q11);
		// Uu,Ss,Vv
		Matrix Uu = svd.getU();
		// Cc = svd.getS();
		// Zz = svd.getV();
		Uu11.EQ(Uu, 1.0E-8).printInLabel("Uu11 == Uu", 0);
	}

	static Matrix[] fileReadExample3() {
		BufferedReader Q1BufReader = null;
		BufferedReader UBufReader = null;

		String filePath = "C:/Users/Sione/Documents/MATLAB/datafiles/";

		System.out.println("### Begin File Reading ###");

		try {
			Q1BufReader = new BufferedReader(new FileReader(filePath + "Q1.txt"));
			UBufReader = new BufferedReader(new FileReader(filePath + "U.txt"));

		} catch (FileNotFoundException fne) {
			fne.printStackTrace();
		}

		System.out.println("### File Reading Successful ###");

		Matrix Q1 = null;
		Matrix U = null;

		try {
			Q1 = Matrix.read(Q1BufReader);
			U = Matrix.read(UBufReader);

		} catch (IOException io) {
			io.printStackTrace();
		}

		System.out.println("### Matrix Reading Successful ###");

		return new Matrix[] { Q1, U };
	}

	// ////////////////////////////////////////
	static void examp4() {
		Matrix[] Q1R = fileReadExample4();
		Matrix QQ11 = Q1R[0];
		Matrix Uu11 = Q1R[1];
		Matrix Ss11 = Q1R[2];
		Matrix Vv11 = Q1R[3];

		// QQ11.EQ(Q1, 1.0E-8).printInLabel("QQ11 == Q1", 0);

		SvdJLapack svd = new SvdJLapack(QQ11);
		// SingularValueDecomposition svd = new
		// SingularValueDecomposition(QQ11);
		// Uu,Ss,Vv
		Matrix U = svd.getU();
		Matrix S = svd.getS();
		Matrix V = svd.getV();

		Matrix USV = U.times(S).times(V.transpose());

		double tol = 1.0E-10;

		System.out.println("\n");
		// Uu11.EQ(U, tol).printInLabel("Uu11 == U", 0);
		if (Uu11.EQ(U, tol).allBoolean()) {
			System.out.println("Uu11 EQUALS U\n");
		} else {
			System.out.println("Uu11 NOT_EQUALS U\n");
		}
		// Ss11.EQ(S, tol).printInLabel("Ss11 == S", 0);
		if (Ss11.EQ(S, tol).allBoolean()) {
			System.out.println("Ss11 EQUALS S\n");
		} else {
			System.out.println("Ss11 NOT_EQUALS S\n");
		}
		// Vv11.EQ(V, tol).printInLabel("Vv11 == V", 0);
		if (Vv11.EQ(V, tol).allBoolean()) {
			System.out.println("Vv11 EQUALS V\n");
		} else {
			System.out.println("Vv11 NOT_EQUALS V\n");
		}

		if (USV.EQ(QQ11, tol).allBoolean()) {
			System.out.println("USV EQUALS QQ11\n");
		} else {
			System.out.println("USV NOT_EQUALS QQ11\n");
		}
	}

	static Matrix[] fileReadExample4() {
		BufferedReader Q1BufReader = null;
		BufferedReader UBufReader = null;
		BufferedReader SBufReader = null;
		BufferedReader VBufReader = null;

		String filePath = "C:/Users/Sione/Documents/MATLAB/datafiles/";

		System.out.println("### Begin File Reading ###");

		try {
			Q1BufReader = new BufferedReader(new FileReader(filePath + "QQ11.txt"));
			UBufReader = new BufferedReader(new FileReader(filePath + "U.txt"));
			SBufReader = new BufferedReader(new FileReader(filePath + "S.txt"));
			VBufReader = new BufferedReader(new FileReader(filePath + "V.txt"));

		} catch (FileNotFoundException fne) {
			fne.printStackTrace();
		}

		System.out.println("### File Reading Successful ###");

		Matrix Q1 = null;
		Matrix U = null;
		Matrix S = null;
		Matrix V = null;

		try {
			Q1 = Matrix.read(Q1BufReader);
			U = Matrix.read(UBufReader);
			S = Matrix.read(SBufReader);
			V = Matrix.read(VBufReader);

		} catch (IOException io) {
			io.printStackTrace();
		}

		System.out.println("### Matrix Reading Successful ###");

		return new Matrix[] { Q1, U, S, V };
	}

	public static void main(String[] args) {
		examp4();

	}
}
