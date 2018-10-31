package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.elfun.JElfun;

public class NormalizeFea {

	private Matrix feaNorm;
	// private Matrix normalized;
	// private boolean ignoreNaN = false;

	public NormalizeFea(Matrix fearaw) {
		this(fearaw, null);
	}

	public NormalizeFea(Matrix fearaw, Dimension rowcol) {
		this(fearaw, rowcol, null);
	}

	public NormalizeFea(Matrix fearaw, Dimension rowcol, Boolean method1) {
		boolean meth1 = false;
		boolean issparse = false;
		Matrix fea = fearaw.copy();

		if (method1 == null) {
			meth1 = true;
		} else {
			meth1 = method1;
		}

		Dimension row = null;
		if (rowcol == null) {
			row = Dimension.ROW;
		} else {
			row = rowcol;
		}

		int nSmp = 0;
		int mFea = 0;
		Matrix temp = null;
		// Matrix feaNorm = null;
		Matrix spd = null;
		Matrix denom = null;

		if (meth1) {
			if (row == Dimension.ROW) {
				nSmp = fea.getRowDimension();// size(fea,1);
				temp = JDatafun.sum(JElfun.pow(fea, 2.0), Dimension.COL);// feaNorm = max(1e-14,full(sum(fea.^2,2)));
				temp.printInLabel("temp");
				this.feaNorm = JDatafun.max(1e-14, temp);// spd = spdiags(feaNorm.^-.5,0,nSmp,nSmp);
				spd = JElfun.pow(this.feaNorm, -0.5).diag();
				spd.printInLabel("spd");
				this.feaNorm = spd.times(fea);// fea = spd*fea;
				this.feaNorm.printInLabel("feaNorm");
			} else {
				nSmp = fea.getColumnDimension();// size(fea,2);
				temp = JDatafun.sum(JElfun.pow(fea, 2.0), Dimension.ROW).transpose();// feaNorm =
																						// max(1e-14,full(sum(fea.^2,1))');
				this.feaNorm = JDatafun.max(1e-14, temp);
				spd = JElfun.pow(this.feaNorm, -0.5).diag();
				this.feaNorm = fea.times(spd);// fea = fea*spdiags(feaNorm.^-.5,0,nSmp,nSmp);
			}
		} else {
			if (row == Dimension.ROW) {
				// [nSmp, mFea] = size(fea);
				nSmp = fea.getRowDimension();
				mFea = fea.getColumnDimension();
				if (issparse) {
					// fea2 = fea';
					// feaNorm = mynorm(fea2,1);
					// for i = 1:nSmp
					// fea2(:,i) = fea2(:,i) ./ max(1e-10,feaNorm(i));
					// end
					// fea = fea2';
				} else {
					temp = JDatafun.sum(JElfun.pow(fea, 2.0), Dimension.COL);
					this.feaNorm = JElfun.pow(temp, 0.5);// feaNorm = sum(fea.^2,2).^.5;
					denom = fea.getColumns(Indices.ones(1, mFea).minus(1).getRowPackedCopy());
					this.feaNorm = fea.arrayRightDivide(denom);// fea = fea./feaNorm(:,ones(1,mFea));
				}
			} else {
				// [mFea, nSmp] = size(fea);
				mFea = fea.getRowDimension();
				nSmp = fea.getColumnDimension();
				if (issparse) {
					// feaNorm = mynorm(fea,1);
					// for i = 1:nSmp
					// fea(:,i) = fea(:,i) ./ max(1e-10,feaNorm(i));
					// end
				} else {
					temp = JDatafun.sum(JElfun.pow(fea, 2.0), Dimension.ROW);// feaNorm = sum(fea.^2,1).^.5;
					this.feaNorm = JElfun.pow(temp, 0.5);
					denom = fea.getRows(Indices.ones(1, mFea).minus(1).getRowPackedCopy());
					this.feaNorm = fea.arrayRightDivide(denom);// fea = fea./feaNorm(ones(1,mFea),:);
				}
			}
		}
	}

	public Matrix getFeaNorm() {
		return feaNorm;
	}

	static void test1() {
		double[][] xarr = { { 7, 2, 7, 1, 8, 12, 6, 2, 1, 6 }, { 14, 2, 5, 8, 2, 8, 4, 13, 14, 1 },
				{ 5, 4, 12, 12, 9, 15, 12, 9, 14, 2 }, { 9, 13, 9, 14, 4, 1, 6, 5, 7, 14 },
				{ 3, 4, 8, 2, 10, 7, 14, 8, 7, 14 }, { 11, 12, 14, 9, 10, 2, 3, 6, 5, 9 },
				{ 4, 4, 4, 7, 11, 14, 4, 1, 14, 1 }, { 8, 14, 11, 0, 7, 0, 2, 4, 6, 4 },
				{ 10, 5, 11, 5, 1, 12, 2, 2, 2, 5 }, { 13, 3, 6, 2, 3, 12, 13, 3, 12, 12 },
				{ 14, 4, 9, 12, 14, 13, 9, 4, 6, 0 }, { 8, 9, 1, 5, 2, 1, 8, 6, 4, 1 } };
		Matrix X = new Matrix(xarr);
		
		new NormalizeFea(X, Dimension.ROW).getFeaNorm().printInLabel("Xnorm");
	}

	public static void main(String[] args) {

		test1();

	}

}
