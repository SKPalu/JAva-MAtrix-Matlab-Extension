/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.nnmf.dtu;

import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.NumericConditionalException;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.unsupervised.subspace.BuildFeatures;

/**
 * 
 * @author Sione
 */
public abstract class DtuBaseNnmf implements BuildFeatures
{

    protected double tolerance = MathUtil.EPS;
    protected Matrix dataSample;// X (N,M) : N (dimensionallity) x M (samples)
                                // non negative input matrix
    protected int numberOfComponents = 2;// : Number of components
    protected int maxIteration = 50;// : Maximum number of iterations to run
    protected boolean printInternal = true;// speak : prints iteration count and
                                           // changes in connectivity matrix
    // % elements unless speak is 0
    protected Matrix W;
    protected Matrix H;
    protected boolean nonNegative = true;
    protected boolean buildFeatures = false;
    
    protected boolean svdInitialized = false;
    protected boolean optimalSvdInitialized = false;
    

    public DtuBaseNnmf()
    {
    }

    public DtuBaseNnmf(Matrix data)
    {
        if (data == null || data.isNull())
        {
            throw new NumericConditionalException("DtuBaseNnmf : Data must be non-null.");
        }

        if (data.LT(0.0).anyBoolean())
        {// (!this.nonNegative) {
            nonNegative = false;
            throw new NumericConditionalException("DtuBaseNnmf : Data elements must all be non-negative.");
        }
        this.dataSample = data;
    }

    protected boolean isDataSampleNull()
    {
        boolean tf = dataSample == null;
        if (!tf)
        {
            int row = dataSample.getRowDimension();
            int col = dataSample.getColumnDimension();

            outerloop: for (int i = 0; i < row; i++)
            {
                for (int j = 0; j < col; j++)
                {
                    if (dataSample.get(i, j) < 0.0)
                    {
                        nonNegative = false;
                        break outerloop;
                    }
                }
            }
        }
        return tf;
    }

    /**
     * @return the tolerance
     */
    public double getTolerance()
    {
        return tolerance;
    }

    /**
     * @param tolerance
     *            the tolerance to set
     */
    public void setTolerance(double tolerance)
    {
        this.tolerance = tolerance;
    }

    /**
     * @return the dataSample
     */
    public Matrix getDataSample()
    {
        return dataSample;
    }

    /**
     * @param dataSample
     *            the dataSample to set
     */
    public void setDataSample(Matrix dataSample)
    {
        this.dataSample = dataSample;
    }

    /**
     * @return the numberOfComponents
     */
    public int getNumberOfComponents()
    {
        return numberOfComponents;
    }

    /**
     * @param numberOfComponents
     *            the numberOfComponents to set
     */
    public void setNumberOfComponents(int numberOfComponents)
    {
        this.numberOfComponents = numberOfComponents;
    }

    /**
     * @return the maxIteration
     */
    public int getMaxIteration()
    {
        return maxIteration;
    }

    /**
     * @param maxIteration
     *            the maxIteration to set
     */
    public void setMaxIteration(int maxIteration)
    {
        this.maxIteration = maxIteration;
    }

    /**
     * @return the printInternal
     */
    public boolean isPrintInternal()
    {
        return printInternal;
    }

    /**
     * @param printInternal
     *            the printInternal to set
     */
    public void setPrintInternal(boolean printInternal)
    {
        this.printInternal = printInternal;
    }

    /**
     * @return the W
     */
    public Matrix getW()
    {
        if (isBuildFeatures() == false)
        {
            throw new NumericConditionalException("getW : Must call the method \"extractFeatures\" first.");
        }
        return W;
    }

    /**
     * @return the H
     */
    public Matrix getH()
    {
        if (isBuildFeatures() == false)
        {
            throw new NumericConditionalException("getH : Must call the method \"extractFeatures\" first.");
        }
        return H;
    }

    /**
     * @return the nonNegative
     */
    public boolean isNonNegative()
    {
        return nonNegative;
    }

    /**
     * @return the buildFeatures
     */
    public boolean isBuildFeatures()
    {
        return buildFeatures;
    }

    
    
    public boolean isSvdInitialized()
    {
        return svdInitialized;
    }

    public void setSvdInitialized(boolean svdInitialized)
    {
        this.svdInitialized = svdInitialized;
    }

    public boolean isOptimalSvdInitialized()
    {
        return optimalSvdInitialized;
    }

    public void setOptimalSvdInitialized(boolean optimalSvdInitialized)
    {
        this.optimalSvdInitialized = optimalSvdInitialized;
    }

    public void orderComponents()
    {
        if (isBuildFeatures() == false)
        {
            throw new NumericConditionalException("orderComponents : Must call the method \"extractFeatures\" first.");
        }

        // Order components according to "energy"

        int D = W.getRowDimension();
        int K = W.getColumnDimension();// size(W);
        // [K,N]=size(H);
        int N = H.getColumnDimension();

        Matrix nrgy = Matrix.zeros(K, 1);
        Matrix wsum = JDatafun.sum(W, Dimension.ROW);
        Matrix hsum = JDatafun.sum(H, Dimension.COL).transpose();
        nrgy = wsum.arrayTimes(hsum);
        // [nrgy,index]=sort(-nrgy);
        QuickSort sort = new QuickSortMat(nrgy.uminus(), true, true);
        nrgy = (Matrix) sort.getSortedObject();
        Indices index = sort.getIndices();
        int[] ind = index.getRowPackedCopy();
        nrgy = nrgy.uminus();
        W = W.getColumns(ind);// W(:,index);
        H = H.getRows(ind);// H(index,:);

    }
}
