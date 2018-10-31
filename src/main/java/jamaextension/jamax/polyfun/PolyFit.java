/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import java.io.FileNotFoundException;
import java.io.IOException;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.QrJLapack;
import jamaextension.jamax.QrJLapack.Economy;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;

/**
 * 
 * @author Sione
 */
public class PolyFit
{

    // oututs
    private double mean = 0.0;
    private double std = 1.0;
    private Matrix coeffs;
    private Matrix R;
    private int DOF = 2; // degree of freedom
    private double normR = 0.5; // norm residual
    private Matrix resid; // residuals
    // Inputs
    private int order = 1;
    private Matrix x;
    private Matrix y;
    // setter Inputs
    private boolean sorted = false;
    private boolean computeAll = false;
    // internal use
    private boolean built = false;
    private static String filePath = "C:/Users/Sione/Documents/MATLAB/datafiles/multifractaltoolbox/";

    public PolyFit(Matrix X, Matrix Y, int order)
    {
        if (X == null)
        {
            throw new IllegalArgumentException("Polyfit : Parameter \"X\" must be non-null.");
        }
        if (Y == null)
        {
            throw new IllegalArgumentException("Polyfit : Parameter \"Y\" must be non-null.");
        }
        if (!X.isVector())
        {
            throw new IllegalArgumentException("Polyfit : Parameter \"X\" must be a vector and not a matrix.");
        }
        if (!Y.isVector())
        {
            throw new IllegalArgumentException("Polyfit : Parameter \"Y\" must be a vector and not a matrix.");
        }
        if (order < 0)
        {
            throw new IllegalArgumentException("Polyfit : Parameter \"order\" must be at least zero.");
        }
        this.order = order;

        if (X.length() != Y.length())
        {
            throw new IllegalArgumentException("Polyfit : Length of vector \"X\" ( = " + X.length()
                    + ") must equal the length of vector \"Y\" ( = " + Y.length() + ").");
        }
        if (X.isRowVector())
        {
            this.x = X.toColVector();
        }
        else
        {
            this.x = X;
        }

        if (Y.isRowVector())
        {
            this.y = Y.toColVector();
        }
        else
        {
            this.y = Y;
        }

        int lenX = X.length();
        if (order >= lenX)
        {
            throw new IllegalArgumentException("Polyfit : The polynomial order specified ( = " + order
                    + ") must be less than the length of vector \"X\" ( = " + X.length() + ").");
        }
    }

    public void build()
    {

        if (!this.sorted)
        {
            // QuickSortMat(Matrix A, Dimension dim, boolean computeSortedIndex)
            QuickSort sort = new QuickSortMat(this.x, null, true);
            this.x = (Matrix) sort.getSortedObject();
            int[] intArr = sort.getIndices().getRowPackedCopy();
            this.y = this.y.getEls(intArr);
        }

        if (this.computeAll)
        {
            this.mean = JDatafun.mean(this.x).start();
            this.std = JDatafun.std(this.x).start();
            this.x = this.x.minus(this.mean).arrayRightDivide(this.std);
        }

        int n = this.order;
        int lenX = this.x.length();
        Matrix V = new Matrix(lenX, this.order + 1);
        Matrix ones = Matrix.ones(lenX, 1);
        V.setColumnAt(n, ones);

        for (int j = (n - 1); j >= 0; j--)
        {
            Matrix temp = this.x.arrayTimes(V.getColumnAt(j + 1));
            // temp.printInLabel("temp " + (j));
            V.setColumnAt(j, temp);// V(:,j) = x.*V(:,j+1);
        }

        // V.printInLabel("V",0);

        QrJLapack qr = new QrJLapack(V, Economy.ZERO);

        // QRDecomposition qr = V.qr();
        Matrix Q = qr.getQ();
        // Q.printInLabel("Q", 8);
        Matrix RR = qr.getR();

        Matrix Qty = Q.transpose().times(this.y);
        this.coeffs = RR.solve(Qty);// RR.inverse().times(Qty);
        // this.coeffs.printInLabel("coefficients 1", 8);

        if (RR.getColumnDimension() > RR.getRowDimension())
        {
            System.out.println("Polyfit.build() : Polynomial is not unique; degree >= number of data points.");
        }

        Matrix r = this.y.minus(V.times(this.coeffs));// - V*p;
        this.coeffs = this.coeffs.transpose();// p = p.'; // Polynomial
                                              // coefficients are row vectors by
                                              // convention.

        if (this.computeAll)
        {
            this.R = RR;
            this.DOF = Math.max(0, this.y.length() - (n + 1));
            this.resid = r;
            this.normR = r.norm();
        }
        this.built = true;
    }

    /**
     * @return the mean
     */
    public double getMean()
    {
        if (!this.computeAll)
        {
            throw new ConditionalException(
                    "getMean : This parameter is not computed. Set the method \"setComputeAll(boolean computeAll)\" to true. ");
        }
        return mean;
    }

    /**
     * @return the std
     */
    public double getStd()
    {
        if (!this.built)
        {
            throw new ConditionalException("getStd : Must call the method \"build\" first. ");
        }
        if (!this.computeAll)
        {
            throw new ConditionalException(
                    "getStd : This parameter is not computed. Set the method \"setComputeAll(boolean computeAll)\" to true. ");
        }
        return std;
    }

    /**
     * @return the coeffs
     */
    public Matrix getCoeffs()
    {
        if (!this.built)
        {
            throw new ConditionalException("getCoeffs : Must call the method \"build\" first. ");
        }
        return coeffs;
    }

    /**
     * @return the R
     */
    public Matrix getR()
    {
        if (!this.built)
        {
            throw new ConditionalException("getR : Must call the method \"build\" first. ");
        }
        if (!this.computeAll)
        {
            throw new ConditionalException(
                    "getR : This parameter is not computed. Set the method \"setComputeAll(boolean computeAll)\" to true. ");
        }
        return R;
    }

    /**
     * @return the DOF
     */
    public int getDOF()
    {
        if (!this.built)
        {
            throw new ConditionalException("getDOF : Must call the method \"build\" first. ");
        }
        if (!this.computeAll)
        {
            throw new ConditionalException(
                    "getDOF : This parameter is not computed. Set the method \"setComputeAll(boolean computeAll)\" to true. ");
        }
        return DOF;
    }

    /**
     * @return the normR
     */
    public double getNormR()
    {
        if (!this.built)
        {
            throw new ConditionalException("getNormR : Must call the method \"build\" first. ");
        }
        if (!this.computeAll)
        {
            throw new ConditionalException(
                    "getNormR : This parameter is not computed. Set the method \"setComputeAll(boolean computeAll)\" to true. ");
        }
        return normR;
    }

    /**
     * @return the resid
     */
    public Matrix getResid()
    {
        if (!this.built)
        {
            throw new ConditionalException("getResid : Must call the method \"build\" first. ");
        }
        if (!this.computeAll)
        {
            throw new ConditionalException(
                    "getResid : This parameter is not computed. Set the method \"setComputeAll(boolean computeAll)\" to true. ");
        }
        return resid;
    }

    /**
     * @param sorted
     *            the sorted to set
     */
    public void setSorted(boolean sorted)
    {
        this.sorted = sorted;
    }

    /**
     * @param computeAll
     *            the computeAll to set
     */
    public void setComputeAll(boolean computeAll)
    {
        this.computeAll = computeAll;
    }

    static Matrix[] getTestMatrices()
    {
        Matrix[] tmpRead = new Matrix[2];
        try
        {
            // String fileNameInd = SvmUtil.getSvmDataFolder() +"ind.txt";
            String FL = filePath + "SegNumb.txt";// fileNameInd +
                                                 // fileNumStr[fileCount++];
            // System.out.println("FL =>> " + FL);
            tmpRead[0] = Matrix.read(FL);
            // ind = tmpRead.toIndices().minus(1);
            // ind.printInLabel("ind");
            FL = filePath + "Seg.txt";// fileNameInd + fileNumStr[fileCount++];
            // System.out.println("FL =>> " + FL);
            tmpRead[1] = Matrix.read(FL);

        }
        catch (FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        return tmpRead;
    }

    public Matrix eval(Matrix val)
    {
        double evaluated = 1.0;
        return null;
    }

    public double eval(double val)
    {
        double evaluated = 1.0;
        return evaluated;
    }

    public static void main(String[] args)
    {
        /*
         * Matrix x = new Matrix(new double[][]{{2, 1, 3, 4, 5}}); Matrix y =
         * new Matrix(new double[][]{{9, 15, 0, 16, 9}});
         */

        Matrix[] SG = getTestMatrices();

        Matrix Seg = SG[1];
        Matrix SegNumb = SG[0];

        SegNumb.printInLabel("SegNumb", 0);

        int m = 1;

        PolyFit PF = new PolyFit(SegNumb, Seg, m);
        PF.setSorted(true);
        PF.build();
        Matrix CF = PF.getCoeffs();
        CF.printInLabel("Coeff");
    }
}
