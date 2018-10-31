package jamaextension.jamax.supervised.kernel.mlssvr;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

public class GridMlsSvr {

	private Matrix trnX;
	private Matrix trnY;
	private int fold;

	private double gamma_best;
	private double lambda_best;
	private double p_best;

	public GridMlsSvr(Matrix trnX, Matrix trnY) {
		this(trnX, trnY, 10);
	}

	public GridMlsSvr(Matrix trnX, Matrix trnY, int fold) {
		if (trnX == null || trnX.isNull()) {
			throw new ConditionalRuleException("Predictor training data \"trnX\" is null.");
		}
		if (trnY == null || trnY.isNull()) {
			throw new ConditionalRuleException("Target training data \"trnY\" is null.");
		}

		if (trnX.getRowDimension() != trnY.getRowDimension()) {
			throw new ConditionalRuleException(
					"Number of rows in the Predictor & Target training data (\"trnX\" & \"trnY\") must be the same.");
		}
		this.trnX = trnX;
		this.trnY = trnY;
		if (fold < 10) {
			this.fold = 10;
		} else {
			this.fold = fold;
		}
	}

	// function [train_inst, train_lbl, test_inst, test_lbl] = folding(svm_inst,
	// svm_lbl, fold, k)
	private Matrix[] folding(Matrix svm_inst, Matrix svm_lbl, int fold, int k) {

		if (svm_inst == null || svm_inst.isNull()) {
			throw new IllegalArgumentException("Matrix input argument \"svm_inst\" must be non-null or non-empty.");
		}
		if (svm_lbl == null || svm_lbl.isNull()) {
			throw new IllegalArgumentException("Matrix input argument \"svm_lbl\" must be non-null or non-empty.");
		}

		int n = svm_inst.getRowDimension();// size(svm_inst, 1);

		// folding instances
		double val = (k - 1.0) * n / fold;
		int start_index = (int) Math.round(val);
		val = k * n / fold;
		int end_index = (int) Math.round(val) - 1;
		if (end_index == n-1) {
			throw new IllegalArgumentException("Reached End");
		}

		int[] test_index = Indices.linspace(start_index, end_index).getRowPackedCopy();// start_index:
																						// end_index;

		// extract test instances and corresponding labels
		Matrix test_inst = svm_inst.getRows(test_index);// svm_inst(test_index,
														// :);
		Matrix test_lbl = svm_lbl.getRows(test_index);// svm_lbl(test_index, :);

		// extract train instances and corresponding labels
		Matrix train_inst = svm_inst.copy();
		train_inst = train_inst.removeRowsAt(test_index);// train_inst(test_index,
															// :) = [];
		Matrix train_lbl = svm_lbl.copy();
		train_lbl = train_lbl.removeRowsAt(test_index);// train_lbl(test_index,
														// :) = [];

		Matrix[] svmArrMat = { train_inst, train_lbl, test_inst, test_lbl };

		return svmArrMat;

	}

	// function [svm_inst, svm_lbl] =
	private Object[] random_perm(Matrix svm_inst, Matrix svm_lbl) {
		if (svm_inst == null || svm_inst.isNull()) {
			throw new IllegalArgumentException("Matrix input argument \"svm_inst\" must be non-null or non-empty.");
		}
		if (svm_lbl == null || svm_lbl.isNull()) {
			throw new IllegalArgumentException("Matrix input argument \"svm_lbl\" must be non-null or non-empty.");
		}
		int n = svm_inst.getRowDimension();// size(svm_inst, 1);

		Matrix svm_inst2 = svm_inst.copy();
		Matrix svm_lbl2 = svm_lbl.copy();
		Indices kVec = new Indices(n, 1);

		// rand('state', 0);
		for (int i = 0; i < n; i++) {
			int I = i + 1;
			// k = round(i + (n - i)*rand()); % [i, n]
			double rn = I + (n - I) * Math.random();
			int k = (int) Math.round(rn) - 1;

			System.out.println(i + ") k = " + k);

			// svm_inst([k, i], :) = svm_inst([i, k], :);
			Matrix tmp = svm_inst.getRows(new int[] { i, k });
			svm_inst2.setRows(new int[] { k, i }, tmp);

			// svm_lbl([k, i], :) = svm_lbl([i, k], :);
			Matrix tmp2 = svm_lbl.getRows(new int[] { i, k });
			svm_lbl2.setRows(new int[] { k, i }, tmp2);

			kVec.setElementAt(i, k);
		}

		Matrix[] svmMat = { svm_inst2, svm_lbl2 };

		Object[] objArr = { svmMat, kVec };
		return objArr;

	}

	public void searchBestParams() {

		Matrix gamma = Matrix.indicesToMatrix(Indices.linspace(-5, 15, 2));// Matrix.linspace(-5,
																			// rightBound,
																			// nPoints);//2.^(-5:
																			// 2:
		gamma = JElfun.pow(2.0, gamma); // 15);
		gamma.printInLabel("gamma");

		Matrix lambda = Matrix.indicesToMatrix(Indices.linspace(-10, 10, 2));// 2.^(-10:
																				// 2:
																				// 10);
		lambda = JElfun.pow(2.0, lambda);
		lambda.printInLabel("lambda");

		Matrix p = Matrix.indicesToMatrix(Indices.linspace(-15, 3, 2));
		p = JElfun.pow(2.0, p);
		p.printInLabel("p");
		// 2.^(-15: 2: 3);

		int m = trnY.getColumnDimension();// size(trnY, 2);

		// m = size(trnY, 2);

		// random permutation
		Object[] trnXYkVec = random_perm(this.trnX, this.trnY);

		Matrix[] trnXY = (Matrix[]) trnXYkVec[0];
		Matrix trnX = trnXY[0];
		Matrix trnY = trnXY[1];

		Indices kVec = (Indices) trnXYkVec[1];
		// kVec.printInLabel("kVec");
		Object[] trnXYkVec2 = random_perm(this.trnX, this.trnY);
		Indices kVec2 = (Indices) trnXYkVec2[1];
		kVec.mergeH(kVec2).printInLabel("k1k2");

		double MSE_best = Double.POSITIVE_INFINITY;// inf;
		Matrix MSE = Matrix.zeros(fold, m);
		Matrix curR2 = Matrix.zeros(1, m);
		Matrix R2 = Matrix.zeros(1, m);

		for (int i = 0; i < gamma.length(); i++) {
			for (int j = 0; j < lambda.length(); j++) {
				for (int k = 0; k < p.length(); k++) {
					Matrix predictY = null;// [];

					for (int v = 0; v < fold; v++) {
						int vv = v + 1;
						System.out.println("i = " + i + " ; j = " + j + " ; k = " + k + " ; vv = " + vv);
						if (vv == 10) {
							System.out.println(" dbstop ");
						}
						Matrix[] foldArr = folding(trnX, trnY, fold, vv);
						Matrix train_inst = foldArr[0];
						Matrix train_lbl = foldArr[1];
						Matrix test_inst = foldArr[2];
						Matrix test_lbl = foldArr[3];

						// [alpha, b] = MLSSVRTrain(train_inst, train_lbl,
						// gamma.getElementAt(i), lambda.getElementAt(j),
						// p.getElementAt(k));

						MultiTargetLsSVR alphaBSVR = new MultiTargetLsSVR(train_inst, train_lbl, gamma.getElementAt(i),
								lambda.getElementAt(j), p.getElementAt(k));
						alphaBSVR.train();
						Matrix alpha = alphaBSVR.getAlpha();
						Matrix bVec = alphaBSVR.getB();
						// MSE.setRowAt(v, msev);

						// [tmpY, MSE(v, :)] = MLSSVRPredict(test_inst,
						// test_lbl, train_inst, alpha, b, lambda(j), p(k));
						Matrix[] predArr = alphaBSVR.predict(test_inst, test_lbl, alpha, bVec, lambda.getElementAt(j),
								p.getElementAt(k));
						Matrix tmpY = predArr[0];// alphaBSVR.getPredictY();
						Matrix msev = predArr[1];// alphaBSVR.getTSE();
						MSE.setRowAt(v, msev);

						if (predictY == null) {
							predictY = tmpY;
						} else {
							predictY = predictY.mergeV(tmpY);
						}
						// predictY = [predictY; tmpY];
					}

					double curMSE = JDatafun.sum(JDatafun.sum(MSE)).start() / trnY.numel();

					if (MSE_best > curMSE) {
						gamma_best = gamma.getElementAt(i);
						lambda_best = lambda.getElementAt(j);
						p_best = p.getElementAt(k);
						MSE_best = curMSE;
					}

					// fprintf('gamma = %g, lambda = %g, p = %g, mean_MSE = %g
					// (%g, %g, %g, %g)\n', ...
					// log2(gamma(i)), log2(lambda(j)), log2(p(k)),
					// sum(sum(MSE))/numel(trnY), ...
					// log2(gamma_best), log2(lambda_best), log2(p_best),
					// MSE_best);
					Object[] objArr = JElfun.log2(gamma.getElementAt(i));
					// System.out.format("The square root of %d is %f.%n", i,
					// r);
					double log2gamma = ((Double) objArr[0]).doubleValue();// Math.log2(gamma.getElementAt(i));

					objArr = JElfun.log2(lambda.getElementAt(j));
					double log2lambda = ((Double) objArr[0]).doubleValue();

					objArr = JElfun.log2(p.getElementAt(k));
					double log2p = ((Double) objArr[0]).doubleValue();

					double sumsummse = JDatafun.sum(JDatafun.sum(MSE)).start() / trnY.numel();

					objArr = JElfun.log2(gamma_best);
					double log2gammaBest = ((Double) objArr[0]).doubleValue();

					objArr = JElfun.log2(lambda_best);
					double log2lambdaBest = ((Double) objArr[0]).doubleValue();

					objArr = JElfun.log2(p_best);
					double log2pBest = ((Double) objArr[0]).doubleValue();

					// System.out.format("gamma = %g, lambda = %g, p = %g,
					// mean_MSE = %g (%g, %g, %g, %g)\n", log2(gamma(i)),
					// log2(lambda(j)), log2(p(k)), sum(sum(MSE))/numel(trnY),
					// log2(gamma_best), log2(lambda_best), log2(p_best),
					// MSE_best);

					System.out.format("gamma = %g, lambda = %g, p = %g, mean_MSE = %g (%g, %g, %g, %g)\n", log2gamma,
							log2lambda, log2p, sumsummse, log2gammaBest, log2lambdaBest, log2pBest, MSE_best);
				}
			}
		}
	}

	public double getGamma_best() {
		return gamma_best;
	}

	public double getLambda_best() {
		return lambda_best;
	}

	public double getP_best() {
		return p_best;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
