package jamaextension.jamax.unsupervised.subspace.lowrankcompletion;

import java.util.List;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.PropValPairList;
import jamaextension.jamax.PropertyValuePair;
import jamaextension.jamax.datafun.JDatafun;

public class NmfCompletion {
	/*
	 * solver for matrix completion with nonnegative factors
	 *
	 * min ||P_omega(XY - M)||_F^2, st. X,Y >= 0
	 *
	 * Output: X --- m x k matrix Y --- k x n matrix Out --- output information
	 * Input: A --- given partial matrix M(Omega) k --- given estimate rank opts
	 * --- option structure
	 */

	private Matrix X;
	private Matrix Y;
	private int numiter;
	private Matrix Lam1;
	private Matrix Lam2;
	private Matrix beta;// = [beta1 beta2];

	public NmfCompletion(Matrix A0, Indices Omega, int k, int m, int n, PropValPairList opts) {

		if (A0.LT(0.0).anyBoolean()) {
			throw new ConditionalRuleException("All entries of \"A0\" must be nonnegative.");
		}

		Matrix A = A0.copy();

		int L = A.length();
		boolean Zfull = (L / (m * n) > 0.2) || (k > 0.02 * Math.min(m, n)) || (m * n < 5e5);

		// parameters;
		double tol = 5e-6;
		int maxit = 500;
		int iprint = 1;
		Matrix Y0 = Matrix.rand(k, n);
		double gamma = 1.618;

		if (opts != null && !opts.isEmpty()) {

			List<PropertyValuePair> propValList = opts.getList();
			int nopts = propValList.size();

			for (int i = 0; i < nopts; i++) {
				PropertyValuePair propVal = propValList.get(i);
				
				// if isfield(opts,'tol'); tol = opts.tol; end
				if ("tol".equals(propVal.getProperty())) {
					tol = (Double)propVal.getValue();
				}

				// if isfield(opts,'maxit'); maxit = opts.maxit; end
				if ("maxit".equals(propVal.getProperty())) {
					maxit = (Integer)propVal.getValue();
				}
				
				// if isfield(opts,'print'); iprint = opts.print; end
				if ("print".equals(propVal.getProperty())) {
					iprint = (Integer)propVal.getValue();
				}
				
				
				// if isfield(opts,'Y0'); Y0 = opts.Y0; end
				if ("Y0".equals(propVal.getProperty())) {
					Y0 = (Matrix)propVal.getValue();
				}
				
				// if isfield(opts,'Zfull'); Zfull = opts.Zfull; end
				if ("Zfull".equals(propVal.getProperty())) {
					Zfull = (Boolean)propVal.getValue();
				}

			}
		}
		// linopts.SYM = true; linopts.POSDEF = true;

		double relres = 1;
		int nstall = 0;
		Matrix I = Matrix.eye(k);

		// scale the problem, different value of Mnrm can be used
		double Mnrm = 2.5e+5;
		double scal = Mnrm / A.norm();
		A = A.scale(scal);// scal*A;
		double r1 = Math.max(m, n) / Math.min(m, n);
		double r2 = k / Math.min(m, n);

		// set penalty parameters
		double beta1 = Mnrm * (r1 / r2) * 2e-4;
		double beta2 = n * beta1 / m;// (n/m)*beta1;

		if (iprint == 2) {
			// fprintf('Initial beta: %6.3e%6.3e\n',beta1, beta2);
			System.out.format("Initial beta: %6.3e%6.3e\n", beta1, beta2);
		}

		if (iprint == 1) {
			System.out.println("Iteration: ");
		}
		// initialize
		this.X = Matrix.zeros(m, k);
		this.Y = Y0;
		this.Y = this.Y.scale(Math.sqrt(Mnrm) / Y.normF());// Y / norm(Y,'fro')
															// *
		// sqrt(Mnrm);
		Matrix U = Matrix.zeros(m, k);
		this.Lam1 = Matrix.zeros(m, k);
		Matrix V = Matrix.zeros(k, n);
		this.Lam2 = Matrix.zeros(k, n);

		Matrix Z = null;
		Matrix S = null;

		if (Zfull) { // Z is full
			Z = Matrix.zeros(m, n);
			Z.setElements(Omega.getRowPackedCopy(), A); // Z(Omega) = A;
		} else {
			// Z = S + XY, initialize the storage of S
			// A(A==0) = eps;
			// if isnumeric(Omega);
			// [Ik,Jk] = ind2sub([m n],Omega);
			// elseif isstruct(Omega)
			// Ik = Omega.Ik;
			// Jk = Omega.Jk;
			// end
			// make sure the order of Ik, Jk and data are correctly as
			// in a sparse matrix
			// S = sparse(Ik, Jk, A, m, n);
			// [Ik, Jk, A] = find(S);
			// A = A.transpose();//A';
		}

		int iter = 0;
		for (iter = 0; iter < maxit; iter++) {

			// updating variables X, U and Lam1
			// gX = beta1*U - Lam1;
			Matrix gX = U.arrayTimes(beta1).minus(this.Lam1);
			Matrix Yt = this.Y.transpose();
			Matrix Xt = null;
			Matrix tmp = null;
			if (Zfull) {
				tmp = Z.times(Yt);
				Xt = gX.plus(tmp).transpose();// Xt = (gX + Z*Y')';
			} else {
				tmp = this.Y.times(Yt);
				tmp = this.X.times(tmp);
				tmp = S.times(Yt).plus(tmp);
				Xt = gX.plus(tmp).transpose();// Xt = (gX + S*Yt + X*(Y*Yt))';
			}
			Matrix B = this.Y.times(Yt).plus(I.arrayTimes(beta1));// B = Y*Y' +
																	// beta1*I;
			Xt = B.solve(Xt);// Xt = linsolve(B, Xt, linopts);
			this.X = Xt.transpose();// X = Xt';
			Matrix XtZ = null;

			if (Zfull) {
				XtZ = Xt.times(Z);
			} else {
				tmp = Xt.times(X).times(Y);
				XtZ = Xt.times(S).plus(tmp);// XtZ = Xt*S+(Xt*X)*Y;
			}

			tmp = this.Lam1.arrayRightDivide(beta1);
			U = JDatafun.max(0.0, tmp);// U = max(0, X + Lam1/beta1);
			tmp = this.X.minus(U).arrayTimes(gamma * beta1);
			this.Lam1 = this.Lam1.plus(tmp);// Lam1 = Lam1 + gamma*beta1*(X-U);

			// updating variables Y, V and Lam2
			Matrix gY = V.arrayTimes(beta2).minus(this.Lam2);// gY = beta2*V -
																// Lam2;
			tmp = Xt.times(X);
			B = tmp.plus(I.arrayTimes(beta2));// B = Xt*X + beta2*I;
			tmp = gY.plus(XtZ);
			this.Y = B.solve(tmp);// Y = linsolve(B, gY+XtZ, linopts);
			tmp = this.Lam2.arrayRightDivide(beta2);
			V = JDatafun.max(0.0, this.Y.plus(tmp));// V = max(0, Y +
													// Lam2/beta2);
			tmp = this.Y.minus(V).arrayTimes(gamma * beta2);
			this.Lam2 = this.Lam2.plus(tmp);// Lam2 = Lam2 + gamma*beta2*(Y-V);

			// updating variable Z
			Matrix Res = null;
			if (Zfull) {
				Z = this.X.times(this.Y);// Z = X*Y;
				Res = A.minus(Z.getEls(Omega.getRowPackedCopy()));// Res =
																	// A-Z(Omega);
				Z.setElements(Omega.getRowPackedCopy(), A);// Z(Omega) = A;
			} else {
				// Res = A-partXY(Xt, Y, Ik, Jk, L);
				// updateSval(S, Res, L);
			}

			double relres0 = relres;
			if (Zfull) {
				relres = Res.norm() / Mnrm;
			} else {
				relres = Res.norm() / Mnrm;
			}

			// printout
			if (iprint == 1) {
				// fprintf('\b\b\b\b\b%5i',iter);
				System.out.format("\b\b\b\b\b%5i", iter);
			}
			if (iprint == 2) {
				// fprintf('iter %5i: relres %6.3e \n', iter,relres);
				System.out.format("iter %5i: relres %6.3e \n", iter, relres);
			}

			// check stopping
			boolean crit1 = Math.abs(relres0 - relres) < (tol * Math.max(1.0, relres0));
			boolean crit2 = relres < tol;

			if (crit1) {
				nstall = nstall + 1;
			} else {
				nstall = 0;
			}
			// make crit1 satisfy in three successive times

			if ((crit1 || crit2) && (nstall > 3)) {
				if (iprint == 2) {
					// fprintf('crits = [%i %i]\n',...
					// crit1,crit2);
					System.out.format("crits = [%i %i]\n", crit1, crit2);
				}
				break;
			}

		} // for iter

		double sqscal = Math.sqrt(scal);
		this.X = JDatafun.max(0.0, this.X).arrayRightDivide(sqscal);// max(0,X)/sqrt(scal);
		this.Y = JDatafun.max(0.0, this.Y).arrayRightDivide(sqscal);// max(0,Y)/sqrt(scal);

		if (iprint == 1) {
			System.out.println("\n");
		}

		this.numiter = iter;
		// Out.iter = iter;
		// Out.Lam1 = Lam1;
		// Out.Lam2 = Lam2;
		// Out.beta = [beta1 beta2];
		this.beta = new Matrix(new double[] { beta1, beta2 });

		// end%main
	}

	public Matrix getX() {
		return this.X;
	}

	public Matrix getY() {
		return this.Y;
	}

	public int getNumiter() {
		return this.numiter;
	}

	public Matrix getLam1() {
		return this.Lam1;
	}

	public Matrix getLam2() {
		return this.Lam2;
	}

	public Matrix getBeta() {
		return this.beta;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
