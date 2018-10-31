/*
 * Histogram.java
 *
 * Created on 10 November 2007, 14:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Histogram
{

    private Matrix dataY;
    private Matrix bins;

    private Indices binElementCounts;
    private Matrix binElements;

    private boolean dataIsRowVector = false;

    public Histogram(Matrix Y)
    {
        this(Y, null);
    }

    public Histogram(Matrix Y, int M)
    {
        this(Y, new Matrix(1, 1, (double) M));
    }

    public Histogram(Matrix Y, Matrix X)
    {
        if (Y == null)
        {
            throw new IllegalArgumentException("Histogram : Parameter \"Y\" can't be allowed to have null value.");
        }
        if (!Y.isVector())
        {
            throw new IllegalArgumentException("Histogram : Parameter \"Y\" must be a vector and not matrix.");
        }
        if (Y.length() < 2)
        {
            throw new IllegalArgumentException("Histogram : Length of \"Y\" must be at least two.");
        }

        if (Y.isColVector())
        {
            this.dataY = Y.toRowVector();
            dataIsRowVector = false;
        }
        else
        {
            this.dataY = Y;
            dataIsRowVector = true;
        }

        if (X == null)
        {
            bins = new Matrix(1, 1, 10.0);
        }
        else
        {
            if (!X.isVector())
            {
                throw new IllegalArgumentException("Histogram : Parameter \"X\" must be a vector and not a matrix.");
            }
            if (X.isRowVector())
            {
                bins = X;
            }
            else
            {
                bins = X.toRowVector();
            }
        }

        Indices find = bins.LT(1.0).find();
        if (find != null)
        {
            if (bins.length() == 1)
            {
                throw new IllegalArgumentException(
                        "Histogram : Parameter \"M\" must be a positive number (ie, greater than zero).");
            }
            else
            {
                throw new IllegalArgumentException(
                        "Histogram : Elements of parameter \"X\" must be all positive numbers (ie, greater than zero).");
            }
        }

        build();
    }

    private void build()
    {
        Matrix x = bins;
        Matrix y = dataY;
        // if(dataY.isRowVector()){ y = dataY; }
        // else{y = dataY.toRowVector(); }
        Matrix xx = null;
        Matrix temp = null;
        double val = 0.0;

        double miny = JDatafun.min(y).get(0, 0);
        double maxy = JDatafun.max(y).get(0, 0);

        if (x.length() == 1)
        {
            if (miny == maxy)
            {
                miny = miny - Math.floor((double) x.get(0, 0) / 2.0) - 0.5;
                maxy = maxy + Math.ceil((double) x.get(0, 0) / 2.0) - 0.5;
            }
            double binwidth = (maxy - miny) / (double) x.get(0, 0);
            // System.out.println(" binwidth = "+binwidth);
            xx = Matrix.linspace(0.0, x.get(0, 0), (int) x.get(0, 0) + 1).arrayTimes(binwidth).plus(miny);// miny
                                                                                                          // +
                                                                                                          // binwidth*(0:x);
            xx.set(0, xx.length() - 1, maxy);// xx(length(xx)) = maxy;
            // System.out.println("----- xx -----");
            // xx.print(4,4);
            // x = xx(1:length(xx)-1) + binwidth/2;
            int[] indArr = Indices.intLinspaceIncrement(0, xx.length() - 2).getRowPackedCopy();
            x = xx.getMatrix(0, indArr).plus(binwidth / 2.0);
            // System.out.println("----- (x.length()==1) -----");
        }
        else
        {
            xx = bins;
            Matrix binwidth = JDatafun.diff(xx);
            binwidth = binwidth.mergeH(new Matrix(1, 1));
            val = xx.get(0, 0) - binwidth.get(0, 0) / 2.0;
            temp = xx.plus(binwidth.arrayRightDivide(2.0));
            xx = (new Matrix(1, 1, val)).mergeH(temp);
            xx.set(0, 0, Math.min(xx.get(0, 0), miny));
            // val = xx.get(xx.getRowDimension()-1,0);
            val = xx.get(0, xx.length() - 1);
            xx.set(0, xx.length() - 1, Math.max(val, maxy));
        }

        // int nbin = xx.length();
        Matrix BIN = xx.plus(xx.arrayTimes(MathUtil.EPS));// + eps(xx);

        // System.out.println("----- bins -----");
        // bins.print(4,4);

        temp = new Matrix(1, 1, Double.NEGATIVE_INFINITY);
        temp = temp.mergeH(BIN);

        /*
         * System.out.println("----- y -----"); y.print(4,0);
         * 
         * System.out.println("----- bins -----"); temp.print(4,4);
         */

        // Object[] nn = JDatafun.histc(y,temp);
        HistogramCount histc = new HistogramCount(y, temp, Dimension.ROW);

        Indices nn = histc.getCount();
        // System.out.println("----- N -----");
        // nn.print(4);

        // Indices BINS = histc.getBinIndices();
        // System.out.println("----- BINS -----");
        // BINS.print(4);

        // Combine first bin with 2nd bin and last bin with next to last bin
        Indices join = nn.getRowAt(1).plus(nn.getRowAt(0));
        nn.setRowAt(1, join);

        int intRow = nn.getRowDimension();
        int end = intRow - 1; // index is one less
        // nn(end-1,:) = nn(end-1,:)+nn(end,:);
        join = nn.getRowAt(end - 1).plus(nn.getRowAt(end));
        // nn = nn(2:end-1,:);
        int[] twoEndMinOne = Indices.intLinspaceIncrement(1, end - 1).getColumnPackedCopy();
        nn = nn.getRows(twoEndMinOne);

        if (dataIsRowVector)
        { // Return row vectors if possible.
          // no = nn';
            binElementCounts = nn.transpose();
            // xo = x;
            binElements = x;
        }
        else
        {
            // no = nn;
            binElementCounts = nn;
            // xo = x';
            binElements = x.transpose();
        }

    }

    public Indices getBinElementCounts()
    {
        return binElementCounts;
    }

    public Matrix getBinElements()
    {
        return binElements;
    }

    public static void main(String[] args)
    {
        double[][] y =
        {
            {
                    19, 5, 12, 10, 18, 15, 9, 0, 16, 9, 12, 16, 18, 15, 4
            }
        };

        Matrix Y = new Matrix(y);
        Matrix bins = Matrix.linspace(1.0, 6.0, 6);
        Histogram hist = new Histogram(Y, 7);// ,bins);

        System.out.println("----- N -----");
        hist.binElementCounts.print(4);
        System.out.println("----- X -----");
        hist.binElements.print(4, 4);
    }

}// //////////////////////////// End Class Definition
// ///////////////////////////
