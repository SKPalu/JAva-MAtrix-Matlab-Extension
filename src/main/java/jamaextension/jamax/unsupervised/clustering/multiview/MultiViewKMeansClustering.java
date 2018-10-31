package jamaextension.jamax.unsupervised.clustering.multiview;

import java.util.*;

import jamaextension.jamax.*;
import jamaextension.jamax.elfun.JElfun;

public class MultiViewKMeansClustering {

	private List<Matrix> inXCell;
	private PropValPairList inPara;
	private Matrix inG0;
	private HashMap propValPairs = new LinkedHashMap<String, Object>();
	private int nViews;
	private int n;

	private Matrix outG0;
	private List<Matrix> outFCell;
	private Matrix outAlpha;
	private Matrix outAlpha_r;
	private Matrix outObj;
	private int outNumIter;

	public MultiViewKMeansClustering(List<Matrix> inXCell, PropValPairList inPara, Matrix inG0) {
		// initialize parameters
		propValPairs.putIfAbsent("maxIter", new Integer(100));
		propValPairs.putIfAbsent("thresh", new Double(0.5));
		propValPairs.putIfAbsent("numCluster", new Integer(5));
		propValPairs.putIfAbsent("r", new Double(0.3));
		// inPara.maxIter: max number of iterator
		// % inPara.thresh: the convergence threshold
		// % inPara.numCluster: the number cluster
		// % inPara.r:

		if (inXCell == null) {
			throw new ConditionalRuleException("Multi-view data \"inXCell\" must be non-null.");
		}

		this.nViews = inXCell.size();

		// check each views to make sure they're all non-empty
		for (int i = 0; i < this.nViews; i++) {
			Matrix viewMat = inXCell.get(i);
			if (viewMat.isNull()) {
				throw new ConditionalRuleException("Data in view " + (i + 1) + " must be non-null.");
			}
		}

		this.n = inXCell.get(0).getColumnDimension();

		if (this.nViews > 1) {
			for (int i = 1; i < this.nViews; i++) {
				Matrix viewMat = inXCell.get(i);
				int viewCol = viewMat.getColumnDimension();
				if (this.n != viewCol) {
					throw new ConditionalRuleException(
							"Columns of Data in each view (" + this.n + " != " + viewCol + ") must be consistent");
				}
			}
		}

		if (inPara == null) {
			throw new ConditionalRuleException("Second input argument \"inPara\" must be non-null.");
		}
		if (inG0 == null) {
			throw new ConditionalRuleException("Third input argument \"inG0\" must be non-null.");
		}
		this.inXCell = inXCell;
		this.inPara = inPara;
		this.inG0 = inG0;
	}

	public void build() {

		/*
		 * function [ outG0, outFCell, outAlpha, outAlpha_r, outObj, outNumIter
		 * ] = weighted_robust_multi_kmeans( inXCell, inPara, inG0 ) % solve the
		 * following problem % min_{F^(v), G0, alpha^{v}} sum_v
		 * {(alpha^{v})^r*||X^(v) - F^(v)G0^T)^T||_2,1} % s.t. G0 is a cluster
		 * indicator, sum_v{alpha^{v}) = 1, alpha^{v} >= 0 % % input: % inXcell:
		 * v by 1 cell, and the size of each cell is d_v by n % inPara:
		 * parameter cell % inPara.maxIter: max number of iterator %
		 * inPara.thresh: the convergence threshold % inPara.numCluster: the
		 * number cluster % inPara.r: the parameter to control the distribution
		 * of the % weights for each view % inG0: init common cluster indicator
		 * % output: % outG0: the output cluster indicator (n by c) % outFcell:
		 * the cluster centroid for each view (d by c by v) % outObj: obj value
		 * % outNumIter: number of iterator
		 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 * %%%%% % Ref: % Xiao Cai, Feiping Nie, Heng Huang. % Multi-View
		 * K-Means Clustering on Big Data. % The 23rd International Joint
		 * Conference on Artificial Intelligence (IJCAI), 2013.
		 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 * %%%%%
		 */

		// parameter settings

		int maxIter = (Integer) this.propValPairs.get("maxIter");
		if (this.inPara.containsProperty("maxIter")) {
			maxIter = (Integer) inPara.getValue("maxIter");
		}

		// inPara.maxIter;
		double thresh = (Double) this.propValPairs.get("thresh");
		if (this.inPara.containsProperty("thresh")) {
			thresh = (Double) inPara.getValue("thresh");
		}

		int c = (Integer) this.propValPairs.get("numCluster");// inPara.numCluster;
		if (this.inPara.containsProperty("numCluster")) {
			c = (Integer) inPara.getValue("numCluster");
		}

		// r = inPara.r;
		double r = (Double) this.propValPairs.get("r");// inPara.numCluster;
		if (this.inPara.containsProperty("r")) {
			r = (Double) inPara.getValue("r");
		}

		// n = size(inXCell{1}, 2);
		int numView = this.nViews;// length(inXCell);
		// inti alpha
		Matrix alpha = new Matrix(this.nViews, 1, 1.0 / this.nViews);/// numView;
		// inti common indicator D3
		Matrix G0 = inG0;
		Matrix G0t = G0.transpose();

		List<Matrix> D4 = new ArrayList<Matrix>(numView);

		for (int v = 0; v < numView; v++) {
			// D4{v} = sparse(diag(ones(n, 1))* alpha(v)^r);
		} // end

		// % % Fix D3{v}, G0, alpha, update F{v}
		// % for v = 1: numView
		// % M = G0'*D4{v}*G0;
		// % N = inXCell{v}*D4{v}*G0;
		// % F{v} = N/M;
		// % end
		// % clear M N;

		double tmp = 1.0 / (1 - r);
		Matrix obj = Matrix.zeros(maxIter, 1);
		// loop
		// Cell F = new Cell(1, numView);
		List<Matrix> F = new ArrayList<Matrix>(numView);
		int t = 0;
		for (t = 0; t < maxIter; t++) {
			// fprintf('processing iteration %d...\n', t);
			System.out.println(" processing iteration ..." + t);

			// Fix D3{v}, G0, alpha, update F{v}
			for (int v = 0; v < numView; v++) {
				// M = (G0'*D4{v}*G0);
				Matrix M = G0t.times(D4.get(v)).times(G0);
				// N = inXCell{v}*D4{v}*G0;
				Matrix N = inXCell.get(v).times(D4.get(v)).times(G0);
				// F{v} = N/M;
			} // end

			// Fix D3{v}, F{v}, update G0
			for (int i = 0; i < n; i++) {
				Matrix dVec = Matrix.zeros(numView, 1);
				for (int v = 0; v < numView; v++) {
					// xVec{v} = inXCell{v}(:,i);
					// tt = diag(D4{v});
					// dVec(v, 1) = tt(i);
				} // end
					// G0(i,:) = searchBestIndicator(dVec, xVec, F);
			} // end

			// Fix F{v}, G0, D4{v}, update alpha
			Matrix h = Matrix.zeros(numView, 1);
			for (int v = 0; v < numView; v++) {
				// E{v} = (inXCell{v} - F{v}*G0')';
				// Ei2{v} = sqrt(sum(E{v}.*E{v}, 2));
				// h(v) = sum(Ei2{v});
			} // end

			// alpha = ((r*h).^tmp)/(sum(((r*h).^tmp)));

			// Fix F{v}, G0, update D4{v}
			for (int v = 0; v < numView; v++) {
				// E{v} = (inXCell{v} - F{v}*G0')';
				// Ei2{v} = sqrt(sum(E{v}.*E{v}, 2) + eps);
				// D4{v} = sparse(diag(0.5./Ei2{v}*(alpha(v)^r)));
			} // end

			// calculate the obj
			obj.setElementAt(t, 0);// (t) = 0;
			for (int v = 0; v < numView; v++) {
				// double val = obj(t) + (alpha(v)^r)*sum(Ei2{v});
				// obj.setElementAt(t, val);
			} // end
			if (t > 0) {
				double diff = obj.getElementAt(t - 1) - obj.getElementAt(t);
				if (diff < thresh) {
					break;
				} // end
			}
		} // end
			// debug
			// figure, plot(1: length(obj), obj);

		this.outObj = obj;
		this.outNumIter = t;
		this.outFCell = F;
		this.outG0 = G0;
		this.outAlpha = alpha;
		this.outAlpha_r = JElfun.pow(alpha, r);// alpha.^r;

	}// end

	// function searchBestIndicator
	private Matrix searchBestIndicator(Matrix dVec, Cell xCell, Cell F) {
		// solve the following problem,
		int numView = this.nViews;// length(F);
		int c = this.n;// size(F{1}, 2);
		Matrix tmp = Matrix.eye(c);
		Matrix obj = Matrix.zeros(c, 1);
		for (int j = 0; j < c; j++) {
			for (int v = 0; v < numView; v++) {
				// obj(j,1) = obj(j,1) + dVec(v) * (norm(xCell{v} -
				// F{v}(:,j))^2);
			} // end
		} // end
			// [min_val, min_idx] = min(obj);
			// outVec = tmp(:, min_idx);
		Matrix outVec = null;
		return outVec;
	}// end

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
