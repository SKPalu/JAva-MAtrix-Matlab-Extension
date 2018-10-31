package jamaextension.jamax;

import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;

public final class JamaxTestMain
{

    public JamaxTestMain()
    {
    }

    public static void main(String[] args)
    {
        /*
         * String word = "beginning"; int len = word.length(); String wordSub =
         * word.substring(len-2,len);
         * System.out.println("\n wordSub = "+wordSub);
         * 
         * 
         * double[][] m = new double[][]{ {11, 4, 8, 1, 3, 8}, { 4, 10, 2, 10,
         * 4, 4} };
         * 
         * Matrix M = new Matrix(m); Matrix covm = M.transpose().times(M);
         * System.out.println("\n------- covm --------"); covm.print(4,0);
         * 
         * EigenvalueDecomposition eig = covm.eig();
         * 
         * Matrix V = eig.getV(); Matrix D = eig.getD();
         * 
         * System.out.println("\n------- V --------"); V.print(4,4);
         * 
         * System.out.println("\n------- D --------"); D.diag().print(4,4);
         * 
         * 
         * Matrix M = JDatafun.magic(5);
         * System.out.println("\n------- M --------"); M.print(4,0);
         * 
         * Matrix Mflip = M.flipAlongTopLToRightB();
         * System.out.println("\n------- Mflip --------"); Mflip.print(4,0);
         */

        double[][] A = new double[][]
        {
                {
                        9, 9, 1, 1, 3
                },
                {
                        7, 9, 4, 2, 2
                },
                {
                        2, 4, 8, 2, 0
                },
                {
                        4, 9, 0, 6, 7
                }
        };

        Matrix matA = new Matrix(A);

        System.out.println("\n------- matA --------");
        matA.print(4, 4);

        Matrix meanMatA = JDatafun.mean(matA, Dimension.COL);

        System.out.println("\n------- meanMatA --------");
        meanMatA.print(4, 4);

        /*
         * Indices ind = matA.lt(8.0).findColumnwiseIndices();
         * 
         * if(ind!=null){ System.out.println("\n------- ind --------");
         * ind.plus(1).print(4,0); }
         * 
         * QR qr = new QR(A);
         * 
         * double[][] Qd = qr.getQ(); double[][] Rd = qr.getR();
         * 
         * Matrix Q = new Matrix(Qd); Matrix R = new Matrix(Rd);
         * 
         * 
         * System.out.println("\n------- Q --------"); Q.print(4,4);
         * 
         * System.out.println("\n------- R --------"); R.print(4,4);
         * 
         * int[] pm = qr.getPermute();
         * 
         * Indices E = MathsUtil.permuteToIndices(pm);
         * 
         * System.out.println("\n------- E --------"); E.print(4,0);
         * 
         * 
         * 
         * 
         * double[] mm = {Double.NaN , 730760 , 730760 , 200, Double.NaN};
         * ArrayList list = new ArrayList(); list.add(new Double(mm[0]));
         * for(int i=1; i<mm.length; i++){ Double num = new Double(mm[i]);
         * if(!list.contains(num)){ list.add(num); } }
         * 
         * int siz = list.size(); for(int i=0; i<siz; i++){
         * System.out.println(i+") "+( ((Double)list.get(i)).doubleValue()
         * )+""); }
         * 
         * System.out.println(" Double.NaN > Double.NEGATIVE_INFINITY  = "+(0.0>
         * Double.NEGATIVE_INFINITY));
         * 
         * 
         * double[][] c = {{2, 0, 5, 3}}; Matrix M = new Matrix(c);
         * ComplexMatrix cm = M.complexRoots(); Matrix[] RI =
         * cm.getRealAndImaginaryMatrices(); Matrix mergeRI =
         * RI[0].mergeH(RI[1]);
         * System.out.println("\n------- mergeRI --------"); mergeRI.print(4,6);
         * 
         * double[][] a = new double[][]{ {16, 9, 9, 9}, { 5, 15, 2, 11}, { 11,
         * 13, 14, 14} };
         * 
         * 
         * double[][] a = new double[][]{{0.5000, 0.3750, 0.1250}, {0.3750,
         * 0.5000, 0.1250}, {0.1429, 0.4286, 0.4286}, {0.1111, 0.3333, 0.5556},
         * {0.1250, 0.1250, 0.7500} };
         * 
         * Matrix A = new Matrix(a);
         * 
         * QRDecomposition qr = A.qr();
         * 
         * Matrix Q = qr.getQ(); System.out.println("------- Q --------");
         * Q.print(4,4); System.out.println("\n");
         * 
         * Matrix R = qr.getR(); System.out.println("------- R --------");
         * R.print(4,4); System.out.println("\n");
         * 
         * System.out.println("--- rank(R) = "+R.rank());
         * 
         * Matrix H = qr.getH(); System.out.println("------- H --------");
         * H.print(4,4); System.out.println("\n");
         * 
         * int[] remove = new int[]{0,1,2};
         * 
         * Indices indices = new Indices(5,4); A.getMatrix(indices);
         * 
         * System.out.println("------- A --------"); A.print(4,0);
         * System.out.println("\n\n");
         * 
         * //Matrix R = A.removeColAt(remove); Matrix R = A.removeRowAt(remove);
         * 
         * System.out.println("------- R --------"); R.print(4,0);
         * System.out.println("\n");
         * 
         * 
         * double[][] a = new double[][]{{-9, 6 , 24, 3}, { 0, -23, -1, -4}, {
         * 3, 24 , 7, 15}}; Matrix A = new Matrix(a);
         * 
         * Object[] obj = A.toColVector().transpose().unique();
         * 
         * Matrix ua = (Matrix)obj[0];
         * System.out.println("------- ua --------");
         * ((Matrix)obj[(0)]).print(4,0); System.out.println("\n");
         * 
         * Matrix I = Matrix.indicesToMatrix((Indices)obj[1]);
         * System.out.println("------- I --------"); I.print(4,0);
         * System.out.println("\n");
         * 
         * 
         * double[][] v = new double[][]{ // ------------- Test Data
         * --------------- {17, 14, 3, 3, 6, 10, 11, 9}, {11, 12, 20, 0, 9, 7,
         * 13, 11}, {7, 16, 5, 18, 1, 9, 4, 16}, {14, 19, 5, 4, 20, 5, 8, 1},
         * {11, 10, 18, 6, 12, 12, 16, 12}, {9, 18, 15, 13, 8, 15, 14, 1}
         * 
         * 
         * };
         * 
         * Matrix mat = new Matrix(v); Object[] obj =
         * JDatafun.maxInd(mat,Matrix.DIM_COL);
         * System.out.println("------- Min --------");
         * ((Matrix)obj[(0)]).print(2,0); System.out.println("\n"); Indices ind
         * = (Indices)obj[(1)]; Matrix temp = Matrix.indicesToMatrix(ind);
         * System.out.println("--------- Ind ---------"); temp.print(2,0);
         * 
         * 
         * Matrix[] grids = JElfun.meshgrid(v);
         * System.out.println("---------- xx ----------"); grids[0].print(2,2);
         * System.out.println("\n");
         * System.out.println("---------- yy ----------"); grids[1].print(2,2);
         * System.out.println("\n");
         * 
         * 
         * 
         * int[][] indX = new int[][]{{9, 9, 1, 1, 3, 4}, {7, 9, 4, 2, 2, 9},
         * {2, 4, 8, 2, 0, 5}, {4, 9, 0, 6, 7, 4}}; Indices indices = new
         * Indices(4,6); indices.setIndexValues(indX); Indices ind2 =
         * indices.any(); Matrix indToMat = Matrix.indicesToMatrix(ind2);
         * System.out.println("------- indToMat -------"); indToMat.print(2,0);
         * 
         * 
         * double[][] x = new double[][]{{8, 2, 3}, {0, 2, 2}, {1, 6, 0}};
         * 
         * Matrix X = new Matrix(x);
         * 
         * Object[] obj = JDatafun.sortInd(X, Matrix.DIM_COL); Matrix maxX =
         * (Matrix)obj[0]; Indices indices = (Indices)obj[1]; Matrix indToMat =
         * Matrix.indicesToMatrix(indices);
         * System.out.println("--------- sortX ---------"); maxX.print(1,0);
         * System.out.println(" ");
         * System.out.println("--------- indToMat ---------");
         * indToMat.print(1,0);
         * 
         * 
         * double[] x = {.25,.50,.75,1.00,1.25,1.50,1.75,2.00}; int len =
         * x.length; double val = 0.0; for(int i=0; i<len; i++){ val =
         * JSpecfun.mpsi(x[i]); System.out.println(""+x[i]+"  -->  "+val);
         * System.out.println("--------------------- "); }
         */

    }

}// -------------------------- End Class Definition
// -----------------------------
