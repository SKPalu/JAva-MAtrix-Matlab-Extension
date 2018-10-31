package jamaextension.jamax.unsupervised.subspace.features;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

public class EuDist {

	private Matrix D;
	
	public EuDist(Matrix... AB) {
		this(true, AB);
	}

	public EuDist(boolean bSqrt, Matrix... AB) {
		if (AB == null || AB.length == 0) {
			throw new ConditionalRuleException("Matrix array \"AB\" must be non-null or non-empty.");
		}
		if (AB.length > 2) {
			throw new ConditionalRuleException("Length of matrix array \"AB\" must be 1 or 2.");
		}

		//Matrix D = null;
		int len = AB.length;

		Matrix fea_a = AB[0];

		if (len == 1) {
			Matrix aa = JDatafun.sum(fea_a.arrayTimes(fea_a), Dimension.COL);
			Matrix aa2 = aa.repmat(1, aa.length());// repmat(aa,1,length(aa))

			Matrix aat = aa.transpose();
			Matrix aat2 = aat.repmat(aat.length(), 1);

			Matrix aaaat = aa2.plus(aat2);

			Matrix two_ab = fea_a.times(fea_a.transpose()).arrayTimes(2.0);
			
			this.D = aaaat.minus(two_ab);
			FindInd find = this.D.LT(0.0).findIJ();
			if (!find.isNull()) {
				int[] arr = find.getIndex();
				this.D.setElements(arr, 0.0);
			}
			if (bSqrt) {
				this.D = JElfun.sqrt(this.D);
			}
			this.D = JDatafun.max(this.D, D.transpose());
		} else {
			throw new ConditionalRuleException("Two matrix inputs is not yet implemented.");
		}
	}
	
	
	public Matrix getD() {
		return D;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
