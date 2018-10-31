/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.sparfun.SparseDiagonals;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Spline
{

    private PiecewisePolynomial PP;

    public Spline(Matrix XX, Matrix YY)
    {
        // Check that data are acceptable
        CheckXyRange CXR = new CheckXyRange(XX, YY);
        Matrix x = CXR.getX();
        Matrix y = CXR.getY();
        Matrix endslopes = CXR.getEndslopes();
        int sizey = CXR.getSizeY();

        /*
         * System.out.println("---------------- X ----------------"); x.print(8,
         * 4); System.out.println("---------------- Y ----------------");
         * y.print(8, 4);
         */

        int n = x.length();
        int yd = CXR.getSizeY();
        int[] arr = null;
        int[] arr2 = null;
        double val = 0.0;
        double val2 = 0.0;
        double val3 = 0.0;

        // Generate the cubic spline interpolant in ppform
        // Matrix dd = Matrix.ones(yd,1);
        Matrix dx = JDatafun.diff(x);
        Matrix dy = JDatafun.diff(y);
        Matrix divdif = dy.arrayRightDivide(dx);// ./dx(dd,:);

        if (n == 2)
        {
            if (endslopes == null)
            {// the interpolant is a straight line
                PP = new PiecewisePolynomial(x, divdif.mergeH(y.getAsMatrix(0, 0)), new Indices(1, 1, sizey));// pp=mkpp(x,[divdif
                                                                                                              // y(:,1)],sizey);
                // PP.print(8, 4);
            }
            else
            {// the interpolant is the cubic Hermite polynomial
             // x,y,endslopes,dx,divdif
                PiecewiseC3Hermite PC3H = new PiecewiseC3Hermite(x, y, endslopes, dx, divdif);
                PP = PC3H.getPP();
                PP.setDim(new Indices(1, 1, sizey));
                // PP.print(8, 4);
            }
        }
        else if (n == 3 && endslopes == null)
        {
            arr = new int[]
            {
                    1, 2
            };
            y.setElements(arr, divdif);// y(:,2:3)=divdif;
            val = JDatafun.diff(divdif).get(0, 0) / (x.get(0, 2) - x.get(0, 0));// y(:,3)=diff(divdif')'/(x(3)-x(1));
            y.setElementAt(2, val);
            val = y.getElementAt(1) - y.getElementAt(2) * dx.get(0, 0);// y(:,2)=y(:,2)-y(:,3)*dx(1);
            y.setElementAt(1, val);
            // pp = mkpp(x([1,3]),y(:,[3 2 1]),sizey);
            arr = new int[]
            {
                    0, 2
            };
            x = x.getElements(arr);
            arr = new int[]
            {
                    2, 1, 0
            };
            y = y.getElements(arr);
            PP = new PiecewisePolynomial(x, y, new Indices(1, 1, sizey));
            // PP.print(8, 4);
        }
        else
        {// set up the sparse, tridiagonal, linear system b = ?*c for the slopes
            Matrix b = Matrix.zeros(yd, n);
            arr = Indices.intLinspaceIncrement(0, n - 3).getRowPackedCopy(); // 1:n-2
            arr2 = Indices.intLinspaceIncrement(1, n - 2).getRowPackedCopy(); // 2:n-1
            // b(:,2:n-1)=3*(dx(dd,2:n-1).*divdif(:,1:n-2) +
            // dx(dd,1:n-2).*divdif(:,2:n-1));
            Matrix temp1 = dx.getElements(arr2).arrayTimes(divdif.getElements(arr));
            Matrix temp2 = dx.getElements(arr).arrayTimes(divdif.getElements(arr2));
            Matrix temp = temp1.plus(temp2).arrayTimes(3.0);
            b.setElements(arr2, temp);

            // System.out.println("---------------- b1 ----------------");
            // b.print(8, 4);

            double x31 = 0.0;
            double xn = 0.0;

            if (endslopes == null)
            {
                // System.out.println(" Spline : Block #5 - Passed");
                x31 = x.getElementAt(2) - x.getElementAt(0);// x(3)-x(1);
                xn = x.getElementAt(n - 1) - x.getElementAt(n - 3);// x(n)-x(n-2);

                val = (dx.get(0, 0) + 2.0 * x31) * dx.getElementAt(1) * divdif.get(0, 0);// (dx(1)+2*x31)*dx(2)*divdif(:,1)
                val2 = dx.get(0, 0) * dx.get(0, 0) * divdif.getElementAt(1);// dx(1)^2*divdif(:,2)
                val3 = (val + val2) / x31;
                b.set(0, 0, val3);

                // b(:,n)=
                // (dx(n-1)^2*divdif(:,n-2)+(2*xn+dx(n-1))*dx(n-2)*divdif(:,n-1))/xn;
                val = dx.getElementAt(n - 2) * dx.getElementAt(n - 2) * divdif.getElementAt(n - 3);// dx(n-1)^2*divdif(:,n-2)
                val2 = (2.0 * xn + dx.getElementAt(n - 2)) * dx.getElementAt(n - 3) * divdif.getElementAt(n - 2); // (2*xn+dx(n-1))*dx(n-2)*divdif(:,n-1)
                val3 = (val + val2) / xn;
                b.setEnd(val3);
            }
            else
            {
                // System.out.println(" Spline : Block #6 - Passed");
                arr = new int[]
                {
                        0, n - 1
                };// [1 n]
                arr2 = new int[]
                {
                        1, n - 3
                };// [2 n-2]
                  // b(:,[1 n]) = dx(dd,[2 n-2]).*endslopes;
                temp = dx.getElements(arr2).arrayTimes(endslopes.toColVector()); // transpose
                                                                                 // call
                                                                                 // since
                                                                                 // the
                                                                                 // method
                                                                                 // 'getElements'
                                                                                 // returns
                                                                                 // a
                                                                                 // column
                                                                                 // vector.
                b.setElements(arr, temp);
            }

            Matrix dxt = dx.toColVector();// dx(:);
            // diagmat = [ [x31;dxt(1:n-2);0]
            // [dxt(2);2*[dxt(2:n-1)+dxt(1:n-2)];dxt(n-2)] [0;dxt(2:n-1);xn] ]
            arr = Indices.intLinspaceIncrement(0, n - 3).getRowIndicesAt(0); // 1:n-2
            arr2 = Indices.intLinspaceIncrement(1, n - 2).getRowIndicesAt(0); // 2:n-1
            // [ [x31;dxt(1:n-2);0]
            temp1 = (new Matrix(1, 1, x31)).mergeV(dxt.getElements(arr)).mergeV(Matrix.zeros(1));
            // [dxt(2);2*[dxt(2:n-1)+dxt(1:n-2)];dxt(n-2)]
            temp2 = dxt.getElements(arr2).plus(dxt.getElements(arr)).arrayTimes(2.0);// dxt.getAsMatrix(1,0);
            temp2 = dxt.getAsMatrix(1, 0).mergeV(temp2).mergeV(dxt.getAsMatrix(n - 3, 0));
            // [0;dxt(2:n-1);xn]
            Matrix temp3 = Matrix.zeros(1).mergeV(dxt.getElements(arr2)).mergeV(new Matrix(1, 1, xn));
            temp = temp1.mergeH(temp2).mergeH(temp3);

            Indices d = new Indices(new int[][]
            {
                {
                        -1, 0, 1
                }
            });
            SparseDiagonals SpD = new SparseDiagonals(temp, d, n, n);
            Matrix C = SpD.getFirstOutput();

            Matrix s = b.times(C.inverse());

            /*
             * System.out.println("---------------- x ----------------");
             * x.print(8, 0);
             * 
             * System.out.println("---------------- y ----------------");
             * y.print(8, 0);
             * 
             * System.out.println("---------------- s ----------------");
             * s.print(8, 4);
             * 
             * System.out.println("---------------- dx ----------------");
             * dx.print(8, 0);
             * 
             * System.out.println("---------------- divdif ----------------");
             * divdif.print(8, 0);
             */

            // construct piecewise cubic Hermite interpolant
            // to values and computed slopes
            // pp = pwch(x,y,s,dx,divdif); pp.dim = sizey;
            PiecewiseC3Hermite PC3H = new PiecewiseC3Hermite(x, y, s, dx, divdif);
            PP = PC3H.getPP();
            PP.setDim(new Indices(1, 1, sizey));

            /*
             * Matrix breaks = PP.getBreaks();
             * System.out.println("---------------- breaks ----------------");
             * breaks.print(8, 0); Matrix coefs = PP.getCoefs();
             * System.out.println("---------------- coefs ----------------");
             * coefs.print(8, 4);
             * System.out.println(" Pieces = "+PP.getPieces());
             * System.out.println(" Order = "+PP.getOrder());
             */

        }// end else

    }

    public PiecewisePolynomial getPP()
    {
        return PP;
    }

    public double evaluate(double xx)
    {
        if (PP == null)
        {
            throw new IllegalArgumentException("evaluate : Object \"PP\" is null.");
        }
        return PP.evaluate(xx);
    }

    public Matrix evaluate(Matrix A)
    {
        if (PP == null)
        {
            throw new IllegalArgumentException("evaluate : Object \"PP\" is null.");
        }
        return PP.evaluate(A);
    }

    public static void main(String[] args)
    {
        double[] a =
        {
                1, 2, 3, 4, 5, 6, 7, 8
        };
        Matrix A = new Matrix(new double[]
        {
                1, 2, 3
        });
        A = new Matrix(a);
        // double[] b = {14, 3, Double.NaN, 7, Double.NaN, 11, 7, 0/*, 6, 10*/};
        double[] b =
        {
                14, 3, 9, 7, 8, 11, 7, 0, 6, 10
        };
        Matrix B = new Matrix(new double[]
        {
                14, 3, 5
        });
        B = new Matrix(b);

        double[] val =
        {
                5, 4
        };
        Matrix xx = new Matrix(val);

        /*
         * System.out.println("---------------- A ----------------"); A.print(8,
         * 0); System.out.println("---------------- B ----------------");
         * B.print(8, 0);
         */

        Spline SP = new Spline(A, B);
        // Matrix V = SP.evaluate(xx);
        double dval = SP.evaluate(-1.50);
        System.out.println(" v = " + dval);

        // System.out.println("---------------- V ----------------");
        // V.print(8, 4);

    }
}// ------------------------ End Class Definition
// -------------------------------

