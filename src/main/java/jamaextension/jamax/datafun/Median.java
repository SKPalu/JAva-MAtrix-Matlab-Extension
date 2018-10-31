/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.TestData;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Sione
 */
public class Median
{

    private Matrix median;

    public Median(Matrix x)
    {
        this(x, null);
    }

    /**
     * Creates a new instance of SumMat
     */
    public Median(Matrix x, Dimension dim)
    {
        computeMedian(x, dim);
    }

    /**
     * @return the median
     */
    public Matrix getMedian()
    {
        return median;
    }

    private void computeMedian(Matrix x, Dimension dim)
    {
        if (x == null || x.isNull())
        {
            throw new ConditionalException("computeMedian : Parameter \"x\" must be non-null.");
        }
        else if (x.isVector())
        {
            // If input is a vector, calculate single value of output.
            QuickSort sort = new QuickSortMat(x);
            x = (Matrix) sort.getSortedObject();
            int nCompare = x.numel();
            int half = (int) Math.floor((double) nCompare / 2.0);
            double y = x.getElementAt(half);
            if (2 * half == nCompare)
            { // Average if even number of elements
                y = meanof(x.getElementAt(half - 1), y);
                // System.out.println("BLOCK");
            }// end
            if (Double.isNaN(x.end()))
            {// isnan(x(nCompare)) // Check last index for NaN
                y = Double.NaN;// nan(class(x));
            }// end
             // System.out.println(" yMat = " + yMat);
            this.median = new Matrix(1, 1, y);
        }
        else
        {// --------------------------------------------------------------

            if (dim == null)
            { // Determine first nonsingleton dimension
              // dim = find(size(x)~=1,1);
                dim = Dimension.ROW;
            }// end
            int[] s = x.sizeIntArr();
            // s.printInLabel("s");

            // x.printInLabel("x-unordered", 0);

            // Sort along given dimension
            // x = sort(x,dim);
            QuickSort sort = new QuickSortMat(x, dim);
            x = (Matrix) sort.getSortedObject();
            // x.printInLabel("x-sorted", 0);

            int nCompare = (dim == Dimension.ROW ? s[0] : s[1]); // Number of
                                                                 // elements
                                                                 // used to
                                                                 // generate a
                                                                 // median
            int half = (int) Math.floor((double) nCompare / 2.0); // Midway
                                                                  // point, used
                                                                  // for median
                                                                  // calculation
            // System.out.println("nCompare = " + nCompare + " ; half = " +
            // half);
            Matrix yMat = null;
            Indices find = null;

            if (dim == Dimension.ROW)
            {// dim == 1
             // If calculating along columns, use vectorized method with
             // column
             // indexing. Reshape at end to appropriate dimension.
                yMat = x.getRowAt(half);
                // yMat.printInLabel("yMat", 0);
                if (2 * half == nCompare)
                {
                    yMat = meanof(x.getRowAt(half - 1), yMat);
                    // yMat.printInLabel("yMat2", 0);
                }// end

                // yMat(isnan(x(nCompare,:))) = NaN; // Check last index for NaN
                find = x.getRowAt(nCompare - 1).isnan().find();
                if (find != null)
                {
                    yMat.setFromFind(find, Double.NaN);
                }
            }
            else if (dim == Dimension.COL)
            {// dim == 2 && length(s) == 2
             // If calculating along rows, use vectorized method only when
             // possible.
             // This requires the input to be 2-dimensional. Reshape at end.
                yMat = x.getColumnAt(half);
                if (2 * half == nCompare)
                {
                    yMat = meanof(x.getColumnAt(half - 1), yMat);
                    // yMat.printInLabel("yMat3", 0);
                }// end

                // yMat(isnan(x(:,nCompare))) = NaN; // Check last index for NaN
                find = x.getColumnAt(nCompare - 1).isnan().find();
                if (find != null)
                {
                    yMat.setFromFind(find, Double.NaN);
                }
            }
            else
            {
                throw new ConditionalException(
                        "computeMedian : This block is not being implemented since only 2D \"Matrix\" data input is used.");
            }

            this.median = yMat;
        }// ---------------------------------------------------------------------
    }

    private static double meanof(double a, double b)
    {
        // MEANOF the mean of A and B with B > A
        // MEANOF calculates the mean of A and B. It uses different formula
        // in order to avoid overflow in floating point arithmetic.

        double c = a + (b - a) / 2.0;

        boolean k = (Math.signum(a) != Math.signum(b)) || !MathUtil.isfinite(a) || !MathUtil.isfinite(b);
        if (k)
        {
            c = (a + b) / 2.0;
        }
        return c;
    }

    private static Matrix meanof(Matrix a, Matrix b)
    {
        // MEANOF the mean of A and B with B > A
        // MEANOF calculates the mean of A and B. It uses different formula
        // in order to avoid overflow in floating point arithmetic.

        Matrix c = b.minus(a).arrayRightDivide(2.0).plus(a);// a + (b - a) /
                                                            // 2.0;
        int row = c.getRowDimension();
        int col = c.getColumnDimension();

        Indices k = a.sign().NEQ(b.sign()).OR(a.isinf()).OR(b.isinf()).find();// |
                                                                              // isinf(a)
                                                                              // |
                                                                              // isinf(b);;
        // (Math.signum(a) != Math.signum(b)) || !MathUtil.isfinite(a) ||
        // !MathUtil.isfinite(b);
        if (k != null)
        {
            // c = (a + b) / 2.0;
            Matrix tmp = a.getFromFind(k).plus(b.getFromFind(k)).arrayRightDivide(2.0);
            c.setFromFind(k, tmp);
            // System.out.println("meanof");
        }
        return c;
    }

    public static void main(String[] args)
    {

        Matrix vMat = TestData.testMat1();
        
        vMat.printInLabel("vMat", 0);
        //vMat = vMat.getRowAt(0);

        Dimension dim = Dimension.ROW;
        Median med = new Median(vMat, dim);
        Matrix medMat = med.getMedian();
        medMat.printInLabel("medMat");

    }
}
