/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.stats;

import java.util.ArrayList;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.HistogramCount;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public class RandSample {

	private ArrayList<Object> rsample;
	private boolean matrixObj = false;
	private boolean indicesObj = false;
	private boolean stringObj = false;

	public RandSample(Object n, int k) {
		this(n, k, null);
	}

	public RandSample(Object n, int k, Boolean replace) {
		this(n, k, replace, null);
	}

	public RandSample(Object n, int k, Boolean replace, Matrix w) {
		String msg = "";
		if (n == null) {
			msg = "Object parameter \"n\" must be non-null.";
			throw new ConditionalRuleException("RandSample", msg);
		}
		boolean cond = !(n instanceof ArrayList) && !(n instanceof Matrix) && !(n instanceof Indices)
				&& !(n instanceof Integer) && !(n instanceof String);
		if (cond) {
			msg = "Object parameter \"n\" must be an instance of \"ArrayList<Object>\", \"Matrix\", \"Indices\", \"String\" or \"Integer\".";
			throw new ConditionalRuleException("RandSample", msg);
		}

		if (n instanceof Matrix) {
			Matrix mat = (Matrix) n;
			if (mat.isNull()) {
				msg = "Matrix parameter \"n\" must be non-null/non-empty.";
				throw new ConditionalRuleException("RandSample", msg);
			}
			if (!mat.isVector()) {
				msg = "Matrix parameter \"n\" must be a vector and not a matrix.";
				throw new ConditionalRuleException("RandSample", msg);
			}
		} else if (n instanceof Indices) {
			Indices ind = (Indices) n;
			if (ind.isNull()) {
				msg = "Indices parameter \"n\" must be non-null/non-empty.";
				throw new ConditionalRuleException("RandSample", msg);
			}
			if (!ind.isVector()) {
				msg = "Indices parameter \"n\" must be a vector and not a matrix.";
				throw new ConditionalRuleException("RandSample", msg);
			}
		} else if (n instanceof Integer) {
			indicesObj = true;
		}

		Object population = null;
		boolean ele1 = false;
		if (n instanceof Matrix) {
			Matrix mat = (Matrix) n;
			ele1 = mat.numel() == 1;
		} else if (n instanceof Indices) {
			Indices ind = (Indices) n;
			ele1 = ind.numel() == 1;
		} else if (n instanceof ArrayList) {
			ArrayList list = (ArrayList) n;
			ele1 = list.size() == 1;
		} else if (n instanceof String) {
			String str = (String) n;
			ele1 = str.length() == 1;
		} else {
			ele1 = true;
		}

		if (ele1) {
			population = null;
		} else {
			population = n;
			// Re-assign 'n' here.
			n = numEle(population); // n = numel(population);
		}

		if (replace == null) {
			replace = false;
		}

		Matrix p = null;
		double val = 0.0;

		if (w != null && !w.isNull()) {
			if (!w.isVector()) {
				msg = "Matrix parameter \"w\" must be a vector and not a matrix.";
				throw new ConditionalRuleException("RandSample", msg);
			}

			if (w.length() != ((Integer) n).intValue()) {
				if (population == null) {
					// error('stats:randsample:InputSizeMismatch',...
					// 'W must have length equal to N.');
					msg = "Matrix parameter \"w\" must have length equal to N (= " + ((Integer) n).intValue() + ").";
				} else {
					// error('stats:randsample:InputSizeMismatch',...
					// 'W must have the same length as the population.');
					msg = "Matrix parameter \"w\" must have the same length as the population.";
				} // end
				throw new ConditionalRuleException("RandSample", msg);
			} else {
				val = JDatafun.sum(w).start();
				p = w.toColVector().transpose().arrayRightDivide(val);// p =
																		// w(:)' /
																		// sum(w);
			} // end

		}

		Matrix tmp = null;
		Indices y = null;
		int[] arr = null;

		if (replace) {// Sample with replacement
			if (w == null || w.isNull()) {
				val = (Integer) n;
				tmp = Matrix.random(k, 1).arrayTimes(val);
				// Use floor rather than ceil to round down since Java's index
				// is less 1 than matlab
				tmp = JElfun.floor(tmp);// JElfun.ceil(tmp);
				y = tmp.toIndices();// y = ceil(n .* rand(k,1));
			} else {
				tmp = Matrix.random(k, 1);
				Matrix tmp2 = JDatafun.cumsum(p);
				tmp2 = Matrix.zeros(1).mergeH(tmp2);
				// [dum, y] = histc(rand(k,1),[0 cumsum(p)]);
				HistogramCount histc = new HistogramCount(tmp, tmp2);
				y = histc.getCount();
			} // end
		} else {// Sample without replacement
			if (k > (Integer) n) {
				if (population == null) {
					// error('stats:randsample:SampleTooLarge',...
					// 'K must be less than or equal to N for sampling without
					// replacement.');
					msg = "\"K\" must be less than or equal to \"N\" for sampling without replacement.";
				} else {
					// error('stats:randsample:SampleTooLarge',...
					// 'K must be less than or equal to the population size.');
					msg = "\"K\" must be less than or equal to \"population\" size.";
				} // end
				throw new ConditionalRuleException("RandSample", msg);
			} // end

			if (w == null || w.isNull()) {
				// If the sample is a sizeable fraction of the population,
				// just randomize the whole population (which involves a full
				// sort of n random values), and take the first k.
				if (4 * k > (Integer) n) {
					Indices rp = JDatafun.randperm((Integer) n);// rp =
																// randperm(n);
					y = rp.getColumns(0, k - 1);// y = rp(1:k);
					// If the sample is a small fraction of the population, a
					// full sort
					// is wasteful. Repeatedly sample with replacement until
					// there are
					// k unique values.
				} else {
					int intN = (Integer) n;
					Indices x = new Indices(1, intN); // flags
					int sumx = 0;
					int ksumx = 0;
					// Indices whileInd = null;
					while (sumx < k) {
						ksumx = k - sumx;
						tmp = Matrix.random(1, ksumx).arrayTimes((double) intN);
						tmp = JElfun.ceil(tmp).minus(1); // Subtract
						arr = tmp.toIndices().getRowPackedCopy();
						x.setElements(arr, 1);// x(ceil(n * rand(1,k-sumx))) =
												// 1; // sample w/replacement
						sumx = JDatafun.sum(x).start(); // sumx = sum(x); //
														// count how many unique
														// elements so far
					} // end
					FindInd find = x.GT(0).findIJ();// y = find(x > 0);
					y = find.getIndexInd();
					x = JDatafun.randperm(k);
					arr = x.getRowPackedCopy();
					y = y.getEls(arr);// y = y(randperm(k));
				} // end
			} else {
				// error('stats:randsample:NoWeighting',...
				// 'Weighted sampling without replacement is not supported.');
				msg = "Weighted sampling without replacement is not supported.";
				throw new ConditionalRuleException("RandSample", msg);
			} // end

		} // -------------------- end sample without replacement
			// -----------------

		if (population != null) {
			arr = y.getRowPackedCopy();
			rsample = getObjInd(population, arr);// y = population(y);
		} else {
			rsample = getObjInd(y);// y = y(:);
		} // end

	}

	private int numEle(Object populObj) {
		int total = 1;
		if (populObj instanceof Matrix) {
			Matrix mat = (Matrix) populObj;
			total = mat.numel();
		} else if (populObj instanceof Indices) {
			Indices ind = (Indices) populObj;
			total = ind.numel();
		} else if (populObj instanceof ArrayList) {
			ArrayList list = (ArrayList) populObj;
			total = list.size();
		} else if (populObj instanceof String) {
			String str = (String) populObj;
			total = str.length();
		}
		return total;
	}

	private ArrayList<Object> getObjInd(Object populObj) {
		return getObjInd(populObj, null);
	}

	private ArrayList<Object> getObjInd(Object populObj, int[] ind) {
		String msg = "";
		int total = 0;
		ArrayList<Object> collect = new ArrayList<Object>();
		if (ind == null || ind.length == 0) {
			if (populObj != null && populObj instanceof Indices) {
				Indices ind2 = (Indices) populObj;
				total = ind2.numel();
				for (int i = 0; i < total; i++) {
					collect.add(ind2.getElementAt(i));
				}
				indicesObj = true;
			} else {
				if (populObj == null) {
					msg = "Parameter \"populObj\" must be non-null.";
					throw new ConditionalRuleException("getObjInd", msg);
				} else {
					msg = "This block is intended only for when parameter \"populObj\" is an instance of \"Indices\".";
					throw new ConditionalRuleException("getObjInd", msg);
				}
			}
		} else {
			total = ind.length;
			if (populObj instanceof Matrix) {
				Matrix mat = (Matrix) populObj;
				for (int i = 0; i < total; i++) {
					collect.add(mat.getElementAt(ind[i]));
				}
				matrixObj = true;
			} else if (populObj instanceof Indices) {
				Indices ind2 = (Indices) populObj;
				for (int i = 0; i < total; i++) {
					collect.add(ind2.getElementAt(ind[i]));
				}
				indicesObj = true;
			} else if (populObj instanceof ArrayList) {
				ArrayList list = (ArrayList) populObj;
				for (int i = 0; i < total; i++) {
					collect.add(list.get(ind[i]));
				}
			} else if (populObj instanceof String) {
				String str = (String) populObj;
				for (int i = 0; i < total; i++) {
					collect.add(str.charAt(ind[i]));
				}
				stringObj = true;
			} else {
				msg = "Object parameter \"populObj\" must be an instance of \"ArrayList<Object>\", \"Matrix\", \"Indices\", \"String\" or \"Integer\".";
				throw new ConditionalRuleException("getObjInd", msg);
			}
		}
		return collect;
	}

	/**
	 * @return the rsample
	 */
	public ArrayList<Object> getRsample() {
		return rsample;
	}

	/**
	 * @return the matrixObj
	 */
	public boolean isMatrixObj() {
		return matrixObj;
	}

	/**
	 * @return the indicesObj
	 */
	public boolean isIndicesObj() {
		return indicesObj;
	}

	/**
	 * @return the stringObj
	 */
	public boolean isStringObj() {
		return stringObj;
	}

	public Object getNumericSample() {
		Object mat = null;
		int siz = rsample.size();
		if (matrixObj) {
			Matrix newmat = new Matrix(siz, 1);
			for (int i = 0; i < siz; i++) {
				double valD = (Double) rsample.get(i);
				newmat.set(i, 0, valD);
			}
			mat = newmat;
		} else if (indicesObj) {
			Indices newind = new Indices(siz, 1);
			for (int i = 0; i < siz; i++) {
				int valI = (Integer) rsample.get(i);
				newind.set(i, 0, valI);
			}
			mat = newind;
		} else {
			String msg = "Only list collection of data-type \"double\" or \"integer\" ing \"rsample\" where this method call is valid.";
			throw new ConditionalRuleException("getNumericSample", msg);
		}
		return mat;
	}

	public boolean isNumericSample() {
		return this.matrixObj || this.indicesObj;
	}

	static void example1() {
		Matrix matSam = Matrix.linspace(1.0, 10.0, 10);
		RandSample rs = new RandSample(matSam, 8, true);
		boolean isMat = rs.isMatrixObj();
		boolean isNum = rs.isNumericSample();
		Object obj = rs.getNumericSample();
		if (isNum) {
			if (isMat) {
				Matrix mx = (Matrix) obj;
				mx.printInLabel("mx", 0);
			} else {
				Indices ix = (Indices) obj;
				ix.printInLabel("ix");
			}
		}
	}
	
	static void example2() {
		//Matrix matSam = Matrix.linspace(1.0, 10.0, 10);
		RandSample rs = new RandSample(15,10);
		boolean isMat = rs.isMatrixObj();
		boolean isNum = rs.isNumericSample();
		Object obj = rs.getNumericSample();
		if (isNum) {
			if (isMat) {
				Matrix mx = (Matrix) obj;
				mx.printInLabel("mx", 0);
			} else {
				Indices ix = (Indices) obj;
				ix.printInLabel("ix");
			}
		}
	}

	public static void main(String[] args) {
		//example1();
		example2();
	}

}
