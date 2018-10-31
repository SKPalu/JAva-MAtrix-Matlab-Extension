package jamaextension.jamax.supervised.kernel.mlssvr;

import jamaextension.jamax.Cell;
import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.FileReadWriteUtil;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

public class MultiTargetLsSVR {

	private KernelType kernel = KernelType.rbf;
	private Matrix trnX;
	private Matrix trnY;
	private double gamma;
	private double lambda;
	private double p;
	
	private boolean trained;
	private boolean predicted;
	//private Matrix predictY;
	//private Matrix TSE;
	//private Matrix R2;
	
	private Matrix alpha;
	private Matrix b;

	public MultiTargetLsSVR(Matrix trnX, Matrix trnY, double gamma, double lambda, double p) {
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
		this.gamma = gamma;
		this.lambda = lambda;
		this.p = p;
	}
	
	public void train() {
		train(null);
	}

	public void train(KernelType kernel) {
		KernelType kern = null;
		if (kernel != null) {
			kern = kernel;
		} else {
			kern = this.kernel;
		}

		// [l, m] = size(trnY);
		int L = trnY.getRowDimension();
		int m = trnY.getColumnDimension();

		Kerfun KF = new Kerfun(kern, trnX, trnX, p, 0);
		Matrix K = KF.getKernelFunction();// Kerfun('rbf', trnX, trnX, p, 0);
		Matrix H = K.repmat(m, m);// repmat(K, m, m) + eye(m * l) / gamma;
		Matrix temp = Matrix.eye(m * L).arrayRightDivide(gamma);
		H = H.plus(temp);
		double ml = m/lambda;
		Matrix Kml = K.arrayTimes(ml);
		

		Matrix P = Matrix.zeros(m * L, m);
		for (int t = 0; t < m; t++) {
			 int idx1 = L*t;//l * (t - 1) + 1;
			 int idx2 = L * (t+1) - 1;
			 int[] arr = Indices.intLinspaceIncrement(idx1, idx2).getRowPackedCopy();
             temp = H.getMatrix(arr, arr);
             
             temp = temp.plus(Kml);
			// H(idx1: idx2, idx1: idx2) = H(idx1: idx2, idx1: idx2) +  K*(m/lambda);
             H.setMatrix(arr, arr, temp);

             Matrix onesL =  Matrix.ones(L, 1);
			// P(idx1: idx2, t) = ones(l, 1);
            P.setMatrix(arr, t, t, onesL);
		}
		
		P.getRows(0,14).printInLabel("P");
		H.getMatrix(0, 14,0 , 3).printInLabel("H");
		
		Matrix Hinv = H.inverse();
		Matrix eta = Hinv.times(P);//H \ P; 
		Matrix nu = Hinv.times(trnY.toColVector());//H \ trnY(:); 
		Matrix S = P.transpose().times(eta);//P'*eta; 
		this.b = S.inverse().times(eta.transpose()).times(trnY.toColVector());//inv(S)*eta'*trnY(:); 
		//b.printInLabel("b");
		this.alpha = nu.minus(eta.times(b));//nu - eta*b;
		
		this.alpha = this.alpha.reshape(L, m);
		
		//this.alpha.getRows(0, 14).printInLabel("alpha(0,14)");
		this.trained = true;
		
		System.out.println(" dbstop ");

	}
	
	public Matrix[] predict(Matrix tstX, Matrix tstY, Matrix alpha, Matrix b, double lambda, double p){
		
		
		if ( (tstY == null || b == null) ||  (tstY.getColumnDimension() != b.numel())) {
		    //display('The number of column in tstY and b must be equal.'); 
			throw new IllegalArgumentException("The number of columns in \"tstY\" and number of elements in vector \"b\" must be equal.");
		    //return null; 
		}

		int m = tstY.getColumnDimension();//size(tstY, 2); 
		int l = trnX.getRowDimension();//size(trnX, 1); 

		if ( alpha.getRowDimension() != l ||  alpha.getColumnDimension() != m) {
		    //display('The size of alpha should be ' + l + '*' + m); 
			throw new IllegalArgumentException("The size of \"alpha\" should be [" + l + " x " + m+"]");
		    //return; 
		}

		int tstN = tstX.getRowDimension();//size(tstX, 1); 
		b = b.toColVector();//b(:); 
		    
		Matrix K = new Kerfun(tstX, trnX, p, 0).getKernelFunction();
		//K = Kerfun('rbf', tstX, trnX, p, 0); 
		Matrix temp = JDatafun.sum(K.times(alpha),Dimension.COL);
		temp = temp.repmat(1, m);		
		Matrix temp2 = K.times(alpha).arrayTimes(m/lambda);
		Matrix temp3 = b.transpose().repmat(tstN, 1);//K.times(alpha).arrayTimes(m/lambda);
		
		Matrix predictY = temp.plus(temp2).plus(temp3);//repmat(sum(K*alpha, 2), 1, m) + K*alpha*(m/lambda) + repmat(b', tstN, 1); 

		//calculate Total Squared Error and squared correlation coefficient
		Matrix TSE = Matrix.zeros(1, m); 
		Matrix R2 = Matrix.zeros(1, m); 
		
		for (int t = 0; t<m; t++){
			double val2 = 0.;
			temp = predictY.getColumnAt(t);
			temp = temp.minus(tstY.getColumnAt(t));
			temp = JElfun.pow(temp, 2.0);
			val2 = JDatafun.sum(temp).start();
		    //TSE(t) = sum((predictY(:, t) - tstY(:, t)).^2); 
			TSE.setElementAt(t, val2);
						
		    Matrix R = JDatafun.corrcoef(predictY.getColumnAt(t), tstY.getColumnAt(t));//corrcoef(predictY(:, t), tstY(:, t)); 
		    if(R.getRowDimension()>1){ //size(R, 1) >  1
		        //R2(t) = R(1, 2)^2; 
		    	val2 = Math.pow(R.get(0, 1), 2.0);
		    	R2.setElementAt(t, val2);
		    }
		}
		
		//predictY, TSE, R2
		this.predicted = true;
		
		return new Matrix[]{predictY, TSE, R2};
	}
		
	public Matrix getAlpha() {
		return alpha;
	}

	public Matrix getB() {
		return b;
	}

	
	
 

	static void example1() {
		String srcdir = "C:\\Users\\admin\\Google Drive\\Technical\\Data Science\\MatlabScripts\\Downloads\\SVM\\MLSSVR\\MLSSVR-master\\";
		String fname = srcdir + "polymertab.csv";
		Cell csvfn = FileReadWriteUtil.readAppacheCommonCsvIntoCell(fname);
		int rows = csvfn.getRowDimension();
		int cols = csvfn.getColumnDimension();
		int nXcol = 10;
		int nYcol = cols - nXcol;

		Cell headers = csvfn.getRowAt(0);
		Matrix data = csvfn.getRows(1, rows - 1).toMatrix();
		rows = data.getRowDimension();

		int ntrn = 41;
		int ntst = rows - ntrn;

		Matrix trainData = data.getRows(0, ntrn - 1);
		Matrix trainX = trainData.getColumns(0, nXcol - 1);
		Matrix trainY = trainData.getColumns(nXcol, cols - 1);

		Matrix testData = data.getRows(ntrn, rows - 1);
		Matrix testX = testData.getColumns(0, nXcol - 1);
		Matrix testY = testData.getColumns(nXcol, cols - 1);

		// Gamma = 2^{-5,-3,...15}
		// Lambda = 2^{-10, -8,...,10}
		// p = 2^{-15, -13,...,3}

		//double gamma = Math.pow(2, -5);
		//double lambda = Math.pow(2, -10);
		//double p = Math.pow(2, -13);
		
		double gamma_best = 0.1250;
		double lambda_best = 0.0010;
		double p_best = 0.1250;
		double MSE_best = 0.0066;

		MultiTargetLsSVR mlsSvr = new MultiTargetLsSVR(trainX, trainY, gamma_best, lambda_best, p_best);
		
		mlsSvr.train();
		Matrix alpha = mlsSvr.getAlpha();
		Matrix b = mlsSvr.getB();
		
		alpha.printInLabel("alpha");
		b.printInLabel("b");
		
		Matrix[] resPred = mlsSvr.predict(testX, testY, alpha, b, lambda_best, p_best);
		
		Matrix predY = resPred[0];//mlsSvr.getPredictY();
		predY.printInLabel("predY");
		
		resPred[1].printInLabel("TSE");
		resPred[2].printInLabel("R2");

		System.out.println(" dbstop ");
	}

	
	public static void main(String[] args) {
		example1();
	}

	/*
	 * function [alpha, b] = MLSSVRTrain(trnX, trnY, gamma, lambda, p) % %
	 * [alpha, b] = MLSSVMTrain(trnX, trnY, gamma, lambda, p); % % author: XU,
	 * Shuo (pzczxs@gmail.com) % date: 2011-12-25 % if (size(trnX, 1) ~=
	 * size(trnY, 1)) display('The number of rows in trnX and trnY must be
	 * equal.'); return; end
	 * 
	 * [l, m] = size(trnY);
	 * 
	 * K = Kerfun('rbf', trnX, trnX, p, 0); H = repmat(K, m, m) + eye(m * l) /
	 * gamma;
	 * 
	 * P = zeros(m*l, m); for t = 1: m idx1 = l * (t - 1) + 1; idx2 = l * t;
	 * 
	 * H(idx1: idx2, idx1: idx2) = H(idx1: idx2, idx1: idx2) + K*(m/lambda);
	 * 
	 * P(idx1: idx2, t) = ones(l, 1); end
	 * 
	 * eta = H \ P; nu = H \ trnY(:); S = P'*eta; b = inv(S)*eta'*trnY(:); alpha
	 * = nu - eta*b;
	 * 
	 * alpha = reshape(alpha, l, m);
	 */

}
