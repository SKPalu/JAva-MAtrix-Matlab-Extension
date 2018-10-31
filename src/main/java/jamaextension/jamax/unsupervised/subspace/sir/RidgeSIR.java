/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.sir;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.SortMat;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public class RidgeSIR
{

    // y, x, nslice, lambda
    private Matrix response;
    private Matrix predictor;
    private int numberOfSlices;
    private double regularizationParam;

    // V directions. p-by-p matrix.
    // D eigenvalues. 1-by-p vector.
    // sigmaeta esitmate of cov(E(x|y)).
    private Matrix directions;
    private Matrix eigenvalues;
    private double estimatedCovariance;
    private boolean built = false;

    public RidgeSIR()
    {
    }

    public RidgeSIR(Matrix y, Matrix x, int nslice, double lambda)
    {
        if (y == null)
        {
            throw new IllegalArgumentException(" RidgeSIR : The response parameter 'y' must be non-null.");
        }
        if (!y.isColVector())
        {
            throw new IllegalArgumentException(" RidgeSIR : The response parameter 'y' must be a column vector.");
        }
        this.response = y;
        if (x == null)
        {
            throw new IllegalArgumentException(" RidgeSIR : The predictor parameter 'x' must be non-null.");
        }
        if (x.getRowDimension() != y.length())
        {
            throw new IllegalArgumentException(
                    " RidgeSIR : The length of response parameter \"y\" and the number of rows in predictor paramter \"x\" must be the same.");
        }
        this.predictor = x;

        build();
    }

    public void build()
    {
        if (response == null)
        {
            throw new IllegalArgumentException(" build : The response parameter 'response' must be non-null.");
        }
        if (!response.isColVector())
        {
            throw new IllegalArgumentException(" build : The response parameter response' must be a column vector.");
        }

        if (predictor == null)
        {
            throw new IllegalArgumentException(" build : The predictor parameter 'x' must be non-null.");
        }
        if (predictor.getRowDimension() != response.length())
        {
            throw new IllegalArgumentException(
                    " build : The length of \"response\" and the number of rows in \"predictor\" must be the same.");
        }

        int n = predictor.getRowDimension();
        int p = predictor.getColumnDimension();

        SortMat sort = new SortMat(response);
        Matrix a = (Matrix) sort.getSortedObject();
        Indices index = sort.getIndices();
        Matrix xmean = JDatafun.mean(this.predictor);

        Matrix modMat = Matrix.linIncrement(1.0, (double) n, 1.0);
        Matrix mod = JElfun.mod(modMat, (double) numberOfSlices);
        Matrix group = (Matrix) (new SortMat(mod).getSortedObject());

        Matrix sigmaeta = Matrix.zeros(p, p);
        Matrix diffmean = Matrix.zeros(1, p);

        for (int i = 0; i <= (numberOfSlices - 1); i++)
        {
            // diffmean = mean( x(index(group==i), :) ) - xmean;
            Indices I = group.EQ((double) i).find();
            if (I != null)
            {
            }
            else
            {
            }
            // sigmaeta = sigmaeta + mean(group == i) * diffmean' * diffmean;
        }

        /*
         * [a, index] = sort(y); xmean = mean(x); group = sort(mod(1:n,
         * nslice));
         */

        built = true;
    }

    /**
     * @return the response
     */
    public Matrix getResponse()
    {
        return response;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse(Matrix response)
    {
        this.response = response;
    }

    /**
     * @return the predictor
     */
    public Matrix getPredictor()
    {
        return predictor;
    }

    /**
     * @param predictor
     *            the predictor to set
     */
    public void setPredictor(Matrix predictor)
    {
        this.predictor = predictor;
    }

    /**
     * @return the numberOfSlices
     */
    public int getNumberOfSlices()
    {
        return numberOfSlices;
    }

    /**
     * @param numberOfSlices
     *            the numberOfSlices to set
     */
    public void setNumberOfSlices(int numberOfSlices)
    {
        this.numberOfSlices = numberOfSlices;
    }

    /**
     * @return the regularizationParam
     */
    public double getRegularizationParam()
    {
        return regularizationParam;
    }

    /**
     * @param regularizationParam
     *            the regularizationParam to set
     */
    public void setRegularizationParam(double regularizationParam)
    {
        this.regularizationParam = regularizationParam;
    }

    /**
     * @return the directions
     */
    public Matrix getDirections()
    {
        if (!isBuilt())
        {
            throw new IllegalArgumentException(" getDirections : The method 'build' must called first.");
        }
        return directions;
    }

    /**
     * @return the eigenvalues
     */
    public Matrix getEigenvalues()
    {
        if (!isBuilt())
        {
            throw new IllegalArgumentException(" getEigenvalues : The method 'build' must called first.");
        }
        return eigenvalues;
    }

    /**
     * @return the estimatedCovariance
     */
    public double getEstimatedCovariance()
    {
        if (!isBuilt())
        {
            throw new IllegalArgumentException(" getEstimatedCovariance : The method 'build' must called first.");
        }
        return estimatedCovariance;
    }

    /**
     * @return the built
     */
    public boolean isBuilt()
    {
        return built;
    }
}
