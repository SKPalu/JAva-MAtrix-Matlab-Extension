package jamaextension.jamax.unsupervised.subspace.features;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;

public class LaplacianScore {

	private Matrix Y;
	private Matrix D;
	private Matrix DPfull;
	private Matrix LPfull;
	
	public LaplacianScore(Matrix X, Matrix W) {
		// [nSmp,nFea] = size(X);
		int nSmp = X.getRowDimension();
		int nFea = X.getColumnDimension();
		int sizW1 = W.getRowDimension();

		if (sizW1 != nSmp) {
			// error('W is error');
			throw new ConditionalRuleException("The number of rows in\"X\" and \"W\" are not equal.");
		}

		Matrix sumW2 = JDatafun.sum(W, Dimension.COL);
		this.D = sumW2;// full(sum(W,2));
		Matrix L = W.copy();

		Indices allone = Indices.ones(nSmp, 1);

		Matrix tmp1 = this.D.transpose().arrayTimes(X);// D'*X;

		// D = sparse(1:nSmp,1:nSmp,D,nSmp,nSmp);
		// Dfull = full(D);

		Matrix sqrTmp1 = tmp1.arrayTimes(tmp1);
		Matrix sumDiag = JDatafun.sum(this.D.diag());
		Matrix mat2 = sqrTmp1.arrayRightDivide(sumDiag);

		Matrix xtDx = X.transpose().times(this.D).transpose().arrayTimes(X);
		xtDx = xtDx.minus(mat2);
		Matrix DPrime = JDatafun.sum(xtDx);

		Matrix xtLx = X.transpose().times(L).transpose().arrayTimes(X);
		xtLx = xtLx.minus(mat2);
		Matrix LPrime = JDatafun.sum(xtLx);

		// DPrime = sum((X'*D)'.*X)-tmp1.*tmp1/sum(diag(D));
		// LPrime = sum((X'*L)'.*X)-tmp1.*tmp1/sum(diag(D));

		// DPrime(find(DPrime < 1e-12)) = 10000;
		FindInd find = DPrime.LT(1e-12).findIJ();
		if (!find.isNull()) {
			int[] arr = find.getIndex();
			DPrime.setElements(arr, 10000.0);
		}

		this.DPfull = DPrime;
		this.LPfull = LPrime;

		this.Y = LPrime.arrayRightDivide(DPrime);
		this.Y = this.Y.transpose();		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
