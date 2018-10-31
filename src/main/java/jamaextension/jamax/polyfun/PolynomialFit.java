/*
 * PolynomialFit.java
 *
 * Created on 19 October 2007, 10:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.QRDecomposition;
import jamaextension.jamax.datafun.JDatafun;

/**
 * @deprecated Use Class <B>PolyFit</B> instead.
 * @author Feynman Perceptrons
 */
public class PolynomialFit
{
    // Outputs

    private double mean = 0.0;
    private double standardDeviation = 1.0;
    private Matrix coefficients;
    private Matrix R;
    private int degreeOfFreedom = 2;
    private double normResiduals = 0.5;
    private Matrix residuals;
    // Inputs
    private boolean sorted = false;
    private int order = 1;
    private Matrix XIndependent;
    private Matrix YDependent;
    private boolean computeAll = false;

    public PolynomialFit(Matrix X, Matrix Y, int order)
    {
        this(X, Y, order, false);
    }

    public PolynomialFit(Matrix X, Matrix Y, int order, boolean sorted)
    {
        this(X, Y, order, sorted, false);
    }

    /**
     * Creates a new instance of PolynomialFit
     * 
     * @param X
     * @param Y
     * @param order
     * @param sorted
     */
    public PolynomialFit(Matrix X, Matrix Y, int order, boolean sorted, boolean comAll)
    {
        if (X == null)
        {
            throw new IllegalArgumentException("PolynomialFit : Parameter \"X\" must be non-null.");
        }
        if (Y == null)
        {
            throw new IllegalArgumentException("PolynomialFit : Parameter \"Y\" must be non-null.");
        }
        if (!X.isVector())
        {
            throw new IllegalArgumentException("PolynomialFit : Parameter \"X\" must be a vector and not a matrix.");
        }
        if (!Y.isVector())
        {
            throw new IllegalArgumentException("PolynomialFit : Parameter \"Y\" must be a vector and not a matrix.");
        }
        if (order < 0)
        {
            throw new IllegalArgumentException("PolynomialFit : Parameter \"order\" must be at least zero.");
        }
        this.order = order;
        if (X.length() != Y.length())
        {
            throw new IllegalArgumentException("PolynomialFit : Length of vector \"X\" ( = " + X.length()
                    + ") must equal the length of vector \"Y\" ( = " + Y.length() + ").");
        }
        if (X.isRowVector())
        {
            XIndependent = X.toColVector();
        }
        else
        {
            XIndependent = X;
        }
        if (Y.isRowVector())
        {
            YDependent = Y.toColVector();
        }
        else
        {
            YDependent = Y;
        }

        this.sorted = sorted;
        this.computeAll = comAll;

        int lenX = XIndependent.length();
        if (order >= lenX)
        {
            throw new IllegalArgumentException("PolynomialFit : The polynomial order specified ( = " + order
                    + ") must be less than the length of vector \"X\" ( = " + X.length() + ").");
        }

        build();
    }

    private void build()
    {
        mean = JDatafun.mean(XIndependent).get(0, 0);
        standardDeviation = JDatafun.std(XIndependent).get(0, 0);
        Matrix x = null;
        Matrix y = null;
        if (!sorted)
        {
            y = YDependent.copy();
            x = JDatafun.pivotSort(XIndependent, y);
        }
        else
        {
            x = XIndependent;
            y = YDependent;
        }

        x = x.minus(mean).arrayRightDivide(standardDeviation);
        x.printInLabel("x");

        int lenX = x.length();
        Matrix V = new Matrix(lenX, order + 1);
        Matrix ones = Matrix.ones(lenX, 1);
        V.setColumnAt(order, ones);
        V.printInLabel("V1", 0);

        for (int j = (order - 1); j >= 0; j--)
        {
            Matrix temp = x.arrayTimes(V.getColumnAt(j + 1));
            // temp.printInLabel("temp " + (j));
            V.setColumnAt(j, temp);// V(:,j) = x.*V(:,j+1);
        }

        // System.out.println("----- V -----");
        // V.print(4,4);
        // V.printInLabel("V2", 0);

        // QrJLapack qr = new QrJLapack(V, Economy.ZERO);

        QRDecomposition qr = V.qr();
        Matrix Q = qr.getQ();
        // Q.printInLabel("Q", 8);
        R = qr.getR();
        R.printInLabel("R", 8);

        Matrix Qty = Q.transpose().times(y);
        coefficients = R.inverse().times(Qty);
        // coefficients.printInLabel("coefficients 1", 8);

        coefficients = R.solve(Qty);
        // coefficients.printInLabel("coefficients 2", 8);

        // System.out.println("----- coefficients -----");
        // coefficients.print(4,4);

        this.residuals = y.minus(V.times(this.getCoefficients()));// y - V*p;
        coefficients = getCoefficients().transpose();
        this.degreeOfFreedom = y.length() - (order + 1);
        this.normResiduals = this.getResiduals().norm2();

        // System.out.println("----- normResiduals = "+normResiduals+" -----");
        // System.out.println("----- degreeOfFreedom = "+degreeOfFreedom+" -----");
        // System.out.println("----- mean = "+mean+" -----");
        // System.out.println("----- standardDeviation = "+standardDeviation+" -----");
    }

    /**
     * @return the mean
     */
    public double getMean()
    {
        return mean;
    }

    /**
     * @return the standardDeviation
     */
    public double getStandardDeviation()
    {
        return standardDeviation;
    }

    /**
     * @return the coefficients
     */
    public Matrix getCoefficients()
    {
        return coefficients;
    }

    /**
     * @return the R
     */
    public Matrix getR()
    {
        return R;
    }

    /**
     * @return the degreeOfFreedom
     */
    public int getDegreeOfFreedom()
    {
        return degreeOfFreedom;
    }

    /**
     * @return the normResiduals
     */
    public double getNormResiduals()
    {
        return normResiduals;
    }

    /**
     * @return the residuals
     */
    public Matrix getResiduals()
    {
        return residuals;
    }

    public static void main(String[] args)
    {
        Matrix x = new Matrix(new double[][]
        {
            {
                    1, 2, 3, 4, 5
            }
        });
        Matrix y = new Matrix(new double[][]
        {
            {
                    15, 9, 0, 16, 9
            }
        });
        PolynomialFit PF = new PolynomialFit(x, y, 4);
        PF.coefficients.printInLabel("Coeff");

    }
}
