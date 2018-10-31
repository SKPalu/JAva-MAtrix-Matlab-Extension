package jamaextension.jamax;

import java.util.List;

import jamaextension.jamax.datafun.JDatafun;

public class SparseDI {

	private int m;
	private int n;
	private int[] r;
	private int[] c;
	private int nnz;
	private List<? extends Number> entry;

	public SparseDI(int[] r, int[] c, List<? extends Number> entry) {
		this(r, c, entry, null, null);
	}

	public SparseDI(int[] r, int[] c, List<? extends Number> entry, Integer m, Integer n) {

		int nnzr = 0;
		if (r != null && r.length > 0) {
			nnzr = r.length;
			this.m = JDatafun.max(new Indices(r)).start() + 1;
		}
		if (m != null && this.m != m) {
			throw new ConditionalRuleException("Incompatible row number.");
		}
		this.r = r;

		int nnzc = 0;
		if (c != null && c.length > 0) {
			nnzc = c.length;
			this.n = JDatafun.max(new Indices(c)).start() + 1;
		}
		if (n != null && this.n != n) {
			throw new ConditionalRuleException("Incompatible column number.");
		}
		this.c = c;

		boolean nonmatched = nnzr != nnzc;
		if (nonmatched) {
			throw new ConditionalRuleException("Incompatible array lengths of rows & columns elements.");
		}

		if (entry != null && entry.size() > 0) {
			this.nnz = entry.size();
		}

		nonmatched = this.nnz != nnzc;
		if (nonmatched) {
			throw new ConditionalRuleException(
					"Incompatible array lengths of rows & columns & number of entry elements.");
		}
		this.entry = entry;
	}

	public boolean isSparseInt() {
		boolean tf = false;
		if (this.entry != null & this.entry.size() > 0) {
			Number num = this.entry.get(0);
			if (num instanceof Integer) {
				tf = true;
			}
		}
		return tf;
	}

	public boolean isSparseDbl() {
		boolean tf = false;
		if (this.entry != null & this.entry.size() > 0) {
			Number num = this.entry.get(0);
			if (num instanceof Double) {
				tf = true;
			}
		}
		return tf;
	}

	public Object full() {
		if (isSparseInt()) {
			Indices matI = new Indices(this.m, this.n);
			for (int u = 0; u < this.nnz; u++) {
				int i = r[u];
				int j = c[u];
				int val = (Integer) this.entry.get(u);
				matI.set(i, j, val);
			}
			return matI;
		} else if (isSparseDbl()) {
			Matrix matD = new Matrix(this.m, this.n);
			for (int u = 0; u < this.nnz; u++) {
				int i = r[u];
				int j = c[u];
				double val = (Double) this.entry.get(u);
				matD.set(i, j, val);
			}
			return matD;
		} else {
			throw new ConditionalRuleException("Not integer or double sparse found.");
		}
	}

	public static void main(String[] args) {
		int[][] DD = { { 7, 0, 0, 0, 3, 5 }, { 0, 0, 4, 0, 0, 10 }, { 0, 3, 8, 6, 7, 0 }, { 0, 10, 0, 7, 2, 6 },
				{ 1, 0, 2, 8, 1, 0 } };

		Indices isp = new Indices(DD);
		isp.printInLabel("isp");

		FindInd find = isp.findIJ();
		find.getI().mergeH(find.getJ()).plus(1).mergeH(isp.getEls(find.getIndexInd().getRowPackedCopy()).toColVector())
				.printInLabel("I-J-S");

		int[] II = find.getI().getRowPackedCopy();
		int[] JJ = find.getJ().getRowPackedCopy();
		List<Integer> listNum = isp.getEls(find.getIndexInd().getRowPackedCopy()).toArrayList();

		SparseDI sparse = new SparseDI(II, JJ, listNum, isp.getRowDimension(), isp.getColumnDimension());
		isp.printInLabel("isp2");
		if (sparse.isSparseInt()) {
			Indices iInd = (Indices)sparse.full();
			iInd.printInLabel("isp3");
		}

	}

}
