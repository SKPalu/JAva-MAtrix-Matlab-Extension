/*
 * HistogramCount.java
 *
 * Created on 11 November 2007, 17:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Feynman Perceptrons
 */
public class HistogramCount
{

    private Matrix data;
    private Matrix edges;
    private Indices count = null;
    private Indices binIndex = null;
    private Dimension dimension = Dimension.ROW;
    private boolean isRowVector = false;

    public HistogramCount(Matrix x, Matrix edges)
    {
        this(x, edges, null);
    }

    /**
     * Creates a new instance of HistogramCount
     */
    public HistogramCount(Matrix x, Matrix edges, Dimension dimension)
    {
        if (!edges.isVector())
        {
            throw new IllegalArgumentException(
                    " HistogramCount : Parameter \"edges\" must be a vector and not a matrix.");
        }
        if (edges.isRowVector())
        {
            this.edges = edges;
        }
        else
        {
            this.edges = edges.toRowVector();
        }

        if (!x.isVector())
        {
            throw new IllegalArgumentException(" HistogramCount : Parameter \"x\" must be a vector and not a matrix.");
        }
        if (x.isRowVector())
        {
            this.data = x;
            isRowVector = true;
        }
        else
        {
            this.data = x.toRowVector();
            isRowVector = false;
        }

        int nbins = this.edges.length();

        /* Make sure the edges vector is monotonically non-decreasing */

        for (int i = 0; i < (nbins - 1); i++)
        {
            if (this.edges.get(0, i) > this.edges.get(0, i + 1))
            {
                throw new IllegalArgumentException(
                        " HistogramCount : Parameter \"edges\" must be monotonically non-decreasing.");
            }
        }
        if (dimension != null)
        {
            this.dimension = dimension;
        }
        Matrix siz = this.data.size();
        int ndims = 2;
        int dim = this.dimension == Dimension.ROW ? 0 : 1;
        int stride = 1;
        int min = Math.min(ndims, dim);

        /* Compute stride along active dimension */
        for (int i = 0; i < min; i++)
        {
            stride *= (int) siz.get(0, i);
        }

        /* Number of elements along active dimension */
        int m = (dim < ndims ? (int) siz.get(0, dim) : 1);
        /*
         * System.out.println("----- siz ----- "); siz.print(4,0);
         * System.out.println(" dim = "+dim ); System.out.println(" m = "+m );
         */

        for (int i = ndims; i < dim; i++)
        {
            siz.set(0, i, 1.0);
        }

        siz.set(0, dim, (double) nbins);

        count = new Indices((int) siz.get(0, 0), (int) siz.get(0, 1));
        binIndex = new Indices(this.data.getRowDimension(), this.data.getColumnDimension());

        histLoop(stride, m, nbins, isRowVector);

        /*
         * System.out.println("----- N -----"); count.print(4,0);
         * 
         * System.out.println("----- bin_output -----"); binIndex.print(4);
         */
    }

    public Indices getCount()
    {
        return count;
    }

    public Indices getBinIndices()
    {
        return this.binIndex;
    }

    private void histLoop(int stride, int m, int nbins, boolean isRowVector)
    {

        // System.out.println("----- X -----");
        // X.print(4,4);

        double xr = 0.0;

        int mx = m;
        int my = nbins;
        int bin = 0;
        int n2 = data.numel() / mx;

        int num = 0;

        /* real loop */
        // if(isRowVector){//##################################
        for (int j = 0; j < n2; j++)
        {
            for (int k = 0; k < mx; k++)
            {// ---------------
                xr = data.get(k, j);
                // bin = findBin(xr,edges,my);
                bin = findBin(xr, edges, my);
                // System.out.println("xr = "+xr+"  :  bin = "+bin);
                if (bin != -1)
                {
                    binIndex.set(k, j, bin);
                    // if(isRowVector) {
                    count.set(bin, j, 1);
                    // }
                    // else{}
                }
            }// inner for
        }// outer for
         // }
        count = JDatafun.sum(count, Dimension.COL);

        // System.out.println("----- count -----");
        // count.print(4);

        if (isRowVector)
        {
            count = count.transpose();
            if (binIndex.isColVector())
            {
                binIndex = binIndex.transpose();
            }
        }
        else
        {
            if (binIndex.isRowVector())
            {
                binIndex = binIndex.transpose();
            }
        }

    }

    private int findBin(double x, Matrix bin_edges, /* Bin edges */int nbins /*
                                                                              * Number
                                                                              * of
                                                                              * edges
                                                                              */)
    {
        int k = -1;
        // Check for NaN and empty bin_edges
        if (!Double.isNaN(x) && bin_edges != null)
        { // Use a binary search
          // Matrix doubleBin_edges = Matrix.indicesToMatrix(bin_edges);
            int k0 = 0;
            int k1 = nbins - 1;

            if (x >= (double) bin_edges.get(0, 0) && x < (double) bin_edges.get(0, nbins - 1))
            {
                k = (k0 + k1) / 2;
                while (k0 < (k1 - 1))
                {
                    if (x >= (double) bin_edges.get(0, k))
                    {
                        k0 = k;
                    }
                    else
                    {
                        k1 = k;
                    }
                    k = (k0 + k1) / 2;
                }
                k = k0;
            }

            // Check for special case
            if (x == bin_edges.get(0, nbins - 1))
            {
                k = nbins - 1;
            }
        }
        return k;
    }

    private int findBinColVector(double x, Matrix bin_edges, /* Bin edges */int nbins /*
                                                                                       * Number
                                                                                       * of
                                                                                       * edges
                                                                                       */)
    {
        int k = -1;
        // Check for NaN and empty bin_edges
        if (!Double.isNaN(x) && bin_edges != null)
        { // Use a binary search
          // Matrix doubleBin_edges = Matrix.indicesToMatrix(bin_edges);
            int k0 = 0;
            int k1 = nbins - 1;

            if (x >= (double) bin_edges.get(0, 0) && x < (double) bin_edges.get(0, nbins - 1))
            {
                k = (k0 + k1) / 2;
                while (k0 < (k1 - 1))
                {
                    if (x >= (double) bin_edges.get(0, k))
                    {
                        k0 = k;
                    }
                    else
                    {
                        k1 = k;
                    }
                    k = (k0 + k1) / 2;
                }
                k = k0;
                System.out.println("#1) k = " + k);
            }

            // Check for special case
            if (x == bin_edges.get(0, nbins - 1))
            {
                k = nbins - 1;
                System.out.println("#2) k = " + k);
            }
        }
        return k;
    }

    public static void main(String[] args)
    {
        /*
         * x= [19.0026, 17.8260, 16.4281, 18.4363, 18.7094; 4.6228, 15.2419,
         * 8.8941, 14.7641, 18.3381; 12.1369, 9.1294, 12.3086, 3.5253, 8.2054;
         * 9.7196, 0.3701, 15.8387, 8.1141, 17.8730]
         * 
         * double[][] x = { {19.0026, 17.8260, 16.4281, 18.4363, 18.7094}, {
         * 4.6228, 15.2419, 8.8941, 14.7641, 18.3381}, {12.1369, 9.1294,
         * 12.3086, 3.5253, 8.2054}, { 9.7196, 0.3701, 15.8387, 8.1141,
         * 17.8730}, };
         */
        double[][] x =
        {
            {
                    19, 5, 12, 10, 18, 15, 9, 0, 16, 9, 12, 16, 18, 15, 4
            }
        };
        Matrix A = new Matrix(x);

        double[][] bins =
        {
            {
                    Double.NEGATIVE_INFINITY, 0.0000, 2.7143, 5.4286, 8.1429, 10.8571, 13.5714, 16.2857, 19.0000
            }
        };

        Matrix BINS = new Matrix(bins);
        // Matrix x1 = A.getRowAt(0);

        // A = A.transpose();

        /*
         * System.out.println("----- y -----"); A.print(4,0); //Indices Ir =
         * M.getIndices(); System.out.println("----- bins -----");
         * BINS.print(4,4);
         */

        HistogramCount histc = new HistogramCount(A, BINS, Dimension.ROW);

        // int binIdx = mxGetPr(binIndex);
        // binIndex[binIdx] = bin + 1;
        // binIdx += stride;

        Indices C = histc.getCount();
        Indices B = histc.getBinIndices();

        System.out.println("----- N -----");
        C.print(4);

        System.out.println("----- BINS -----");
        B.plus(1).print(4);

    }
}// ////////////// End Class Definition ////////////////////////
