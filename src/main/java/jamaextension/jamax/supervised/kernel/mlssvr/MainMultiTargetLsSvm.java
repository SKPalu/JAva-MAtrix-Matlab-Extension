package jamaextension.jamax.supervised.kernel.mlssvr;

import jamaextension.jamax.Cell;
import jamaextension.jamax.FileReadWriteUtil;
import jamaextension.jamax.Matrix;

public class MainMultiTargetLsSvm {

	static void exampleMain1() {
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
		
		trainX.printInLabel("trainX");
		trainY.printInLabel("trainY");

		Matrix testData = data.getRows(ntrn, rows - 1);
		Matrix testX = testData.getColumns(0, nXcol - 1);
		Matrix testY = testData.getColumns(nXcol, cols - 1);

		System.out.println("Start Grid Search");

		// [gamma_best, lambda_best, p_best, MSE_best] = GridMLSSVR(trnX, trnY,
		// fold);
		int fold = 10;

		GridMlsSvr gridSearch = new GridMlsSvr(trainX, trainY, fold);
		gridSearch.searchBestParams();

		// disp('done grid search');
		System.out.println("Done Grid Search");

		double gamma_best = gridSearch.getGamma_best();
		double lambda_best = gridSearch.getLambda_best();
		double p_best = gridSearch.getP_best();

		System.out.println("\n\ngamma_best = " + gamma_best + " ; lambda_best = " + lambda_best + " ; p_best = " + p_best);

	}

	public static void main(String[] args) {
		exampleMain1();

	}

}
