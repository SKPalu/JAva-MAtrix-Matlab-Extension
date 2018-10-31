/*
 * JDatafun.java
 *
 * Created on 3/11/2007, 13:33:36
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
import java.util.ArrayList;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.Matrix3D;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.elfun.JElfun;

/**
 * A static class for <B>Data analysis</B> functions.
 */
public final class JDatafun
{
    public static boolean doDeb = false;
    public static double varTol = 1e-16;

    private JDatafun()
    {
    }

    /**
     * FILTER One-dimensional digital filter. Y = FILTER(B,A,X) filters the data
     * in vector X with the filter described by vectors A and B to create the
     * filtered data Y. The filter is a "Direct Form II Transposed"
     * implementation of the standard difference equation: a(1)*y(n) = b(1)*x(n)
     * + b(2)*x(n-1) + ... + b(nb+1)*x(n-nb) - a(2)*y(n-1) - ... -
     * a(na+1)*y(n-na) If a(1) is not equal to 1, FILTER normalizes the filter
     * coefficients by a(1). When X is a matrix, FILTER operates on the columns
     * of X. When X is an N-D array, FILTER operates along the first
     * non-singleton dimension. [Y,Zf] = FILTER(B,A,X,Zi) gives access to
     * initial and final conditions, Zi and Zf, of the delays. Zi is a vector of
     * length MAX(LENGTH(A),LENGTH(B))-1 or an array of such vectors, one for
     * each column of X. FILTER(B,A,X,[],DIM) or FILTER(B,A,X,Zi,DIM) operates
     * along the dimension DIM.
     * 
     * @param vectorB
     * @param vectorA
     * @param vectorData
     * @return
     */
    public static Matrix filter(Matrix vectorB, Matrix vectorA, Matrix vectorData)
    {
        if (vectorB.isVector() == false)
        {
            throw new IllegalArgumentException("filter : Parameter \"vectorB\" , must be a column or row vector.");
        }
        if (vectorA.isVector() == false)
        {
            throw new IllegalArgumentException("filter : Parameter \"vectorA\" , must be a column or row vector.");
        }
        if (vectorData.isVector() == false)
        {
            throw new IllegalArgumentException("filter : Parameter \"vectorData\" , must be a column or row vector.");
        }

        // System.out.println("---- 1) vectorA----");
        // vectorA.print(4,4);
        /*
         * //------------------- New code block ---------------- Matrix
         * negVectorA = vectorA.arrayTimes(-1.0);
         * negVectorA.setElementAt(0,-negVectorA.getElementAt(0)); vectorA =
         * negVectorA; //---------------------------------------------------
         */

        // System.out.println("---- 1) vectorA----");
        // vectorA.print(4,4);

        double aOne = vectorA.get(0, 0);
        Matrix normalizedVectorA = null;
        if (aOne != 1.0)
        {
            normalizedVectorA = vectorA.arrayRightDivide(aOne);
        }
        else
        {
            normalizedVectorA = vectorA;
        }

        Matrix B = vectorB.toRowVector().arrayRightDivide(aOne); // vectorB is
                                                                 // normalized
        Matrix A = normalizedVectorA.toRowVector();
        Matrix X = vectorData.toRowVector();

        int lenB = B.length();
        int lenA = A.length();
        int lenX = X.length();

        double valB = 0.0;
        double valA = 0.0;
        double coeffB = 0.0;
        double coeffA = 0.0;

        int ind = 0;

        Matrix Y = new Matrix(1, lenX);
        double[][] yArr = Y.getArray();
        double[][] xArr = X.getArray();

        for (int i = 0; i < lenX; i++)
        {
            // re-initialize
            valB = 0.0;
            for (int b = 0; b < lenB; b++)
            {
                coeffB = B.get(0, b);
                ind = i - b;
                // if(ind<0){ valB += 0.0; }
                // else{ valB += coeffB*xArr[0][ind]; }
                if (ind >= 0)
                {
                    valB += coeffB * xArr[0][ind];
                }
            }// end for b

            valA = valB; // System.out.println("valB = "+valB);
            for (int a = 1; a < lenA; a++)
            {
                coeffA = A.get(0, a);
                ind = i - a;
                // if(ind<0){ valA -= 0.0; }
                // else{ valA -= coeffA*yArr[0][ind]; }
                if (ind >= 0)
                {
                    valA -= coeffA * yArr[0][ind];
                }
            }// end for a

            yArr[0][i] = valA;
        }// end for i

        return Y;
    }// end method

    /**
     * FILTER One-dimensional digital filter. Y = FILTER(B,A,X) filters the data
     * in vector X with the filter described by vectors A and B to create the
     * filtered data Y. The filter is a "Direct Form II Transposed"
     * implementation of the standard difference equation: a(1)*y(n) = b(1)*x(n)
     * + b(2)*x(n-1) + ... + b(nb+1)*x(n-nb) - a(2)*y(n-1) - ... -
     * a(na+1)*y(n-na) If a(1) is not equal to 1, FILTER normalizes the filter
     * coefficients by a(1). When X is a matrix, FILTER operates on the columns
     * of X. When X is an N-D array, FILTER operates along the first
     * non-singleton dimension. [Y,Zf] = FILTER(B,A,X,Zi) gives access to
     * initial and final conditions, Zi and Zf, of the delays. Zi is a vector of
     * length MAX(LENGTH(A),LENGTH(B))-1 or an array of such vectors, one for
     * each column of X. FILTER(B,A,X,[],DIM) or FILTER(B,A,X,Zi,DIM) operates
     * along the dimension DIM.
     * 
     * @param vectorB
     * @param vectorA
     * @param data
     * @return
     */
    public static double[] filter(double[] vectorB, double[] vectorA, double[] data)
    {

        double aOne = vectorA[0];
        double[] normalizedVectorA = null;
        double[] normalizedVectorB = null;

        if (aOne != 1.0)
        {
            normalizedVectorA = new double[vectorA.length];
            for (int i = 0; i < vectorA.length; i++)
            {
                normalizedVectorA[i] = vectorA[i] / aOne;
            }
            normalizedVectorB = new double[vectorB.length];
            for (int i = 0; i < vectorB.length; i++)
            {
                normalizedVectorB[i] = vectorB[i] / aOne;
            }
        }
        else
        {
            normalizedVectorA = vectorA;
            normalizedVectorB = vectorB;
        }

        int lenB = normalizedVectorB.length;
        int lenA = normalizedVectorA.length;
        int lenX = data.length;

        double valB = 0.0, valA = 0.0;
        double coeffB = 0.0, coeffA = 0.0;
        int ind = 0;

        double[] yArr = new double[lenX];
        // double[] xArr = new double[lenX];

        for (int i = 0; i < lenX; i++)
        {
            // re-initialize
            valB = 0.0;
            for (int b = 0; b < lenB; b++)
            {
                coeffB = normalizedVectorB[b];
                ind = i - b;
                if (ind < 0)
                {
                    valB += 0.0;
                }
                else
                {
                    valB += coeffB * data[ind];
                }
            }// end for b

            valA = valB;
            for (int a = 1; a < lenA; a++)
            {
                coeffA = normalizedVectorA[a];
                ind = i - a;
                if (ind < 0)
                {
                    valA -= 0.0;
                }
                else
                {
                    valA -= coeffA * yArr[ind];
                }
            }// end for a

            yArr[i] = valA;
        }// end for i

        return yArr;
    }// end method

    /**
     * CONV Convolution and polynomial multiplication. C = CONV(A, B) convolves
     * vectors A and B. The resulting vector is length LENGTH(A)+LENGTH(B)-1. If
     * A and B are vectors of polynomial coefficients, convolving them is
     * equivalent to multiplying the two polynomials.
     * 
     * @param A
     * @param B
     * @return
     */
    public static Matrix conv(Matrix A, Matrix B)
    {
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException("conv : Parameter \"A\" , must be a column or row vector.");
        }
        if (B.isVector() == false)
        {
            throw new IllegalArgumentException("conv : Parameter \"B\" , must be a column or row vector.");
        }

        Matrix matA = null;
        if (A.isColVector())
        {
            matA = A.toRowVector();
        }
        else
        {
            matA = A.copy();
        }

        Matrix matB = null;
        if (B.isColVector())
        {
            matB = B.toRowVector();
        }
        else
        {
            matB = B.copy();
        }

        int na = matA.length();
        int nb = matB.length();

        Matrix C = null;

        if (na > nb)
        {
            if (nb > 1)
            {
                matA = matA.mergeH(new Matrix(1, nb - 1, 0.0));
            }// a(na+nb-1) = 0;
            C = filter(matB, new Matrix(1, 1, 1.0), matA);
        }
        else
        {
            if (na > 1)
            {
                matB = matB.mergeH(new Matrix(1, na - 1, 0.0));
            }// b(na+nb-1) = 0;
            C = filter(matA, new Matrix(1, 1, 1.0), matB);
        }

        return C;
    }// end method

    /**
     * DECONV Deconvolution and polynomial division. [Q,R] = DECONV(B,A)
     * deconvolves vector A out of vector B. The result is returned in vector Q
     * and the remainder in vector R such that B = conv(A,Q) + R.
     * 
     * If A and B are vectors of polynomial coefficients, deconvolution is
     * equivalent to polynomial division. The result of dividing B by A is
     * quotient Q and remainder R.
     * 
     * @param B
     * @param A
     * @return
     */
    public static Matrix[] deconv(Matrix B, Matrix A)
    {
        Matrix Q = null, R = null;
        if (A.isVector() == false)
        {
            throw new IllegalArgumentException("deconv : Parameter \"A\" , must be a column or row vector.");
        }
        if (B.isVector() == false)
        {
            throw new IllegalArgumentException("deconv : Parameter \"B\" , must be a column or row vector.");
        }
        if (A.get(0, 0) == 0.0)
        {
            throw new IllegalArgumentException("deconv : First coefficient of \"A\" must be non-zero.");
        }

        int mb = B.getRowDimension();
        int nb = B.getColumnDimension();
        // [mb,nb] = size(b);
        nb = Math.max(mb, nb);
        int na = A.length();

        if (na > nb)
        {
            Q = new Matrix(1, 1);
            R = B.copy();
            return new Matrix[]
            {
                    Q, R
            };
        }

        // Deconvolution and polynomial division are the same operations
        // as a digital filter's impulse response B(z)/A(z):
        Q = filter(B, A, (new Matrix(1, 1, 1.0)).mergeH(Matrix.zeros(1, nb - na)));// q
                                                                                   // =
                                                                                   // filter(b,
                                                                                   // a,
                                                                                   // [1
                                                                                   // zeros(1,nb-na)]);

        if (mb != 1)
        {
            Q = Q.toColVector();
        } // q = q(:);

        // r = b - conv(a,q);
        R = B.minus(conv(A, Q));

        return new Matrix[]
        {
                Q, R
        };
    }// end method

    public static Matrix tfidf(Matrix X)
    {

        Matrix Y = null;// new Matrix();

        // get term frequencies
        Y = tf(X);

        // get inverse document frequencies
        Matrix I = idf(X);

        // apply weights for each document
        int numdoc = X.getColumnDimension();
        // for j=1:size(X, 2)
        // X(:, j) = X(:, j)*I(j);
        // end
        for (int j = 0; j < numdoc; j++)
        {
            Matrix YJ = Y.getColumnAt(j);
            double val = I.getElementAt(j);
            YJ = YJ.arrayTimes(val);
            Y.setColumnAt(j, YJ);
        }

        return Y;
    }

    /*
     * 
     * computes TF-IDF weighted word histograms. Y = tfidf( X ); % % INPUT : X -
     * document-term matrix (documents in columns) OUTPUT : Y - TF-IDF weighted
     * document-term matrix %
     */
    public static Matrix tf(Matrix X)
    {
        Matrix Y = X.copy();
        int row = X.getRowDimension();
        // for every word
        for (int i = 0; i < row; i++)// i=1:size(X, 1)
        {
            // get word i counts for all documents
            Matrix x = Y.getRowAt(i);// X(i, :);

            // sum all word i occurences in the whole collection
            double sumX = JDatafun.sum(x).start();

            // compute frequency of the word i in the whole collection
            if (sumX != 0)
            {
                // X(i, :) = x / sum(x);
                x = x.arrayRightDivide(sumX);
                Y.setRowAt(i, x);
            }
            else
            {
                // avoiding NaNs : set zero to never appearing words
                // (i, :) = 0;
                Y.setRowAt(i, 0.0);
            }// end

        }// end

        return Y;
    }

    public static Matrix idf(Matrix X)
    {
        // Matrix Y = X.copy();
        // m - number of terms or words
        // n - number of documents
        // [m, n]=size(X);
        double m = X.getRowDimension();
        int n = X.getColumnDimension();

        // allocate space for document idf's
        Matrix I = Matrix.zeros(n, 1);

        // for every document
        for (int j = 0; j < n; j++)// j=1:n
        {
            // count non-zero frequency words
            Matrix XJ = X.getColumnAt(j);
            double nz = XJ.nnz();// nnz( X(:, j) );

            // if not zero, assign a weight:
            if (nz != 0)
            {
                double val = Math.log(m / nz);
                I.set(j, 0, val);// I(j) = log( m / nz );
            }// end

        }// end
        return I;
    }

    /**
     * This method over-loading is the same as: Matrix.mean(data,Dimension.ROW)
     * If data = (0 1 2 4) (3 4 5 8)
     * 
     * Matrix.mean(data) = (1.5, 2.5, 3.5, 6) , returns the mean along the
     * column datas.
     * 
     * @param data
     *            Matrix matrix
     * @return Matrix Matrix matrix
     */
    public static Matrix mean(Matrix data)
    {
        /*
         * //Determine which dimension SUM will use Indices dim =
         * data.size().NEQ(1.0).find(); if(dim!=null){ dim = dim.plus(1); }
         * else{}
         * 
         * Indices dim = min(find(size(x)~=1)); if isempty(dim), dim = 1; end
         * 
         * y = sum(x)/size(x,dim);
         */

        // return JDatafun.mean(data,Dimension.ROW);
        return (new Mean(data)).getMean();
    }

    /**
     * If data = (0 1 2 4) (3 4 5 8)
     * 
     * Matrix.mean(data,Dimension.ROW) = (1.5, 2.5, 3.5, 6) , returns the mean
     * along the column datas.
     * 
     * Matrix.mean(data,Dimension.COL) = ( 1.75 ) , returns the mean along the
     * row datas. ( 10 )
     * 
     * @param data
     *            Matrix matrix
     * @param Dim
     *            integer constant specifying the dimension to find the mean.
     * @return Matrix Matrix matrix
     */
    public static Matrix mean(Matrix data, Dimension Dim)
    {
        return (new Mean(data, Dim)).getMean();
    }// end method

    /**
     * The sum of the elements of matrix, along columns of every rows.
     * 
     * If A = (148 131 175) , then JDatafun.sum(A) , returns a single-element
     * matrix of 454
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * JDatafun.sum(A) = (36, 43, 25, 37)
     * 
     * @param matrix
     * @return Matrix The Matrix summed matrix of <tt>data</tt>.
     */
    public static Matrix sum(Matrix matrix)
    {
        // ---- new block added on 19/02/2011 ----
        SumMat sumMat = new SumMat(matrix);
        Matrix sum = (Matrix) sumMat.getSumObject();
        return sum;

        // ---- old line commented on 19/02/2011 ----
        // return (Matrix) (new SumMat(matrix)).getSumObject();
    }

    /**
     * The sum of the elements of matrix, along the specified dimension , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.sum(A,Dimension.ROW) , returns a
     * single-element matrix of 454
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * JDatafun.sum(A,Dimension.ROW) = (36, 43, 25, 37)
     * 
     * ( 47 ) JDatafun.sum(A,Dimension.COL) = | 35 | ( 59 )
     * 
     * @param Matrix
     *            The input data matrix set
     * @param Dim
     *            Integer for the dimension to be summed.
     * @return Matrix The Matrix summed matrix of <tt>data</tt>.
     * 
     */
    public static Matrix sum(Matrix matrix, Dimension Dim)
    {
        return (Matrix) (new SumMat(matrix, Dim)).getSumObject();
    }

    /**
     * The sum of the elements of matrix, along columns of every rows.
     * 
     * If A = (148 131 175) , then JDatafun.sum(A) , returns a single-element
     * matrix of 454
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * JDatafun.sum(A) = (36, 43, 25, 37)
     * 
     * @param matrix
     * @return Matrix The Matrix summed matrix of <tt>data</tt>.
     * @deprecated Use the method <B>sum(Matrix A)</B> instead.
     */
    public static Matrix sumvec(Matrix matrix)
    {
        return sumvec(matrix, Dimension.ROW);
    }

    /**
     * The sum of the elements of matrix, along the specified dimension , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.sum(A,Dimension.ROW) , returns a
     * single-element matrix of 454
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * JDatafun.sum(A,Dimension.ROW) = (36, 43, 25, 37)
     * 
     * ( 47 ) JDatafun.sum(A,Dimension.COL) = | 35 | ( 59 )
     * 
     * @param matrix
     * @param Dim
     *            Integer for the dimension to be summed.
     * @return Matrix The Matrix summed matrix of <tt>data</tt>.
     * @deprecated Use the method <B>sum(Matrix A, Dimension dim)</B> instead.
     */
    public static Matrix sumvec(Matrix matrix, Dimension Dim)
    {

        double[][] internal = matrix.getArrayCopy();
        double[][] summing = null;
        double temp = 0.0;

        int row = matrix.getRowDimension();
        int col = matrix.getColumnDimension();
        Matrix singleElement = new Matrix(1, 1);

        if (matrix.isVector() == false)
        {
            throw new IllegalArgumentException("sumvec : Data must be a vector and not a matrix. ");
        }

        if (Dim == Dimension.ROW)
        {
            summing = new double[1][col];
            for (int j = 0; j < col; j++)
            {
                for (int i = 0; i < row; i++)
                {
                    temp += internal[i][j];
                }
                summing[0][j] = temp;
                temp = 0.0;
            }
        }
        else if (Dim == Dimension.COL)
        {
            summing = new double[row][1];
            for (int i = 0; i < row; i++)
            {
                for (int j = 0; j < col; j++)
                {
                    temp += internal[i][j];
                }
                summing[i][0] = temp;
                temp = 0.0;
            }
        }
        else
        {
            throw new IllegalArgumentException("sumvec : Dimension  " + Dim.toString() + " , not supported.");
        }
        return new Matrix(summing);
    }

    /**
     * The sum of the elements of matrix, along columns of every rows.
     * 
     * If A = (148 131 175) , then JDatafun.sum(A) , returns a single-element
     * matrix of 454
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * JDatafun.sum(A) = (36, 43, 25, 37)
     * 
     * @param matrix
     * @return Matrix The Matrix summed matrix of <tt>data</tt>.
     * @deprecated Use the method <B>sum(Indices A)</B> instead.
     */
    public static Indices sumvec(Indices matrix)
    {
        return sumvec(matrix, Dimension.ROW);
    }

    /**
     * The sum of the elements of matrix, along the specified dimension , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.sum(A,Dimension.ROW) , returns a
     * single-element matrix of 454
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * JDatafun.sum(A,Dimension.ROW) = (36, 43, 25, 37)
     * 
     * ( 47 ) JDatafun.sum(A,Dimension.COL) = | 35 | ( 59 )
     * 
     * @param matrix
     * @param Dim
     *            Integer for the dimension to be summed.
     * @return Matrix The Matrix summed matrix of <tt>data</tt>.
     * @deprecated Use the method <B>sum(Indices A, Dimension dim)</B> instead.
     */
    public static Indices sumvec(Indices matrix, Dimension Dim)
    {

        int[][] internal = matrix.getArray();
        int[][] summing = null;
        int temp = 0;

        int row = matrix.getRowDimension();
        int col = matrix.getColumnDimension();
        // Indices singleElement = new Indices(1,1);

        if (matrix.isVector() == false)
        {
            throw new IllegalArgumentException("sumvec : Data must be a vector and not a matrix. ");
        }

        if (Dim == Dimension.ROW)
        {
            summing = new int[1][col];
            for (int j = 0; j < col; j++)
            {
                for (int i = 0; i < row; i++)
                {
                    temp += internal[i][j];
                }
                summing[0][j] = temp;
                temp = 0;
            }
        }
        else if (Dim == Dimension.COL)
        {
            summing = new int[row][1];
            for (int i = 0; i < row; i++)
            {
                for (int j = 0; j < col; j++)
                {
                    temp += internal[i][j];
                }
                summing[i][0] = temp;
                temp = 0;
            }
        }
        else
        {
            throw new IllegalArgumentException("sumvec : Dimension  " + Dim.toString() + " , not supported.");
        }

        return new Indices(summing);
    }

    // ///////////////////////////////////////////////////////////////////////////
    /**
     * 
     * @param data
     * @return
     */
    public static Matrix cumsum(Matrix data)
    {
        return (new CumsumMat(data)).getCumsum();
    }

    /**
     * The cumulative sum of the elements of matrix, along the spefied dimension
     * , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.cumsum(A,Dimension.ROW) , returns (
     * 148, 279, 454 )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * ( 19, 10, 9, 9 ) JDatafun.cumsum(A,Dimension.ROW) = | 24, 28, 9, 21 | (
     * 36, 43, 25, 37 )
     * 
     * ( 19, 29, 38, 47 ) JDatafun.cumsum(A,Dimension.COL) = | 5, 23, 23, 35 | (
     * 12, 27, 43, 59 )
     * 
     * @param Matrix
     *            The input data matrix set
     * @param Dim
     *            Integer for the dimension for cumulated summing.
     * @return Matrix The Matrix cumulated summed matrix of <tt>data</tt>.
     */
    public static Matrix cumsum(Matrix data, Dimension Dim)
    {
        return (new CumsumMat(data, Dim)).getCumsum();
    }

    /**
     * This method is overloaded to do cumulative sums along the columns of each
     * rows. The cumulative sum of the elements of matrix, along the spefied
     * dimension , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.cumsum(A) , returns ( 148, 279, 454
     * )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * ( 19, 10, 9, 9 ) JDatafun.cumsum(A) = | 24, 28, 9, 21 | ( 36, 43, 25, 37
     * )
     * 
     * @param Matrix
     *            The input data matrix set
     * @return Matrix The Matrix cumulated summed matrix of <tt>data</tt>.
     */
    public static Matrix cumprod(Matrix data)
    {
        return new CumprodMat(data).getCumprod();
    }

    /**
     * The cumulative sum of the elements of matrix, along the spefied dimension
     * , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.cumsum(A,Dimension.ROW) , returns (
     * 148, 279, 454 )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * ( 19, 10, 9, 9 ) JDatafun.cumsum(A,Dimension.ROW) = | 24, 28, 9, 21 | (
     * 36, 43, 25, 37 )
     * 
     * ( 19, 29, 38, 47 ) JDatafun.cumsum(A,Dimension.COL) = | 5, 23, 23, 35 | (
     * 12, 27, 43, 59 )
     * 
     * @param Matrix
     *            The input data matrix set
     * @param Dim
     *            Integer for the dimension for cumulated summing.
     * @return Matrix The Matrix cumulated summed matrix of <tt>data</tt>.
     */
    public static Matrix cumprod(Matrix data, Dimension Dim)
    {
        return new CumprodMat(data, Dim).getCumprod();
    }

    /**
     * This method is overloaded to do cumulative sums along the columns of each
     * rows. The cumulative sum of the elements of matrix, along the spefied
     * dimension , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.cumsum(A) , returns ( 148, 279, 454
     * )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * ( 19, 10, 9, 9 ) JDatafun.cumsum(A) = | 24, 28, 9, 21 | ( 36, 43, 25, 37
     * )
     * 
     * @param Matrix
     *            The input data matrix set
     * @return Matrix The Matrix cumulated summed matrix of <tt>data</tt>.
     */
    public static Indices cumprod(Indices data)
    {
        return new CumprodInd(data).getCumprod();
    }

    /**
     * The cumulative sum of the elements of matrix, along the spefied dimension
     * , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.cumsum(A,Dimension.ROW) , returns (
     * 148, 279, 454 )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * ( 19, 10, 9, 9 ) JDatafun.cumsum(A,Dimension.ROW) = | 24, 28, 9, 21 | (
     * 36, 43, 25, 37 )
     * 
     * ( 19, 29, 38, 47 ) JDatafun.cumsum(A,Dimension.COL) = | 5, 23, 23, 35 | (
     * 12, 27, 43, 59 )
     * 
     * @param Matrix
     *            The input data matrix set
     * @param Dim
     *            Integer for the dimension for cumulated summing.
     * @return Matrix The Matrix cumulated summed matrix of <tt>data</tt>.
     */
    public static Indices cumprod(Indices data, Dimension Dim)
    {
        return new CumprodInd(data, Dim).getCumprod();
    }

    /*
     * public static Matrix cumprod(Matrix data, Dimension Dim){ double temp =
     * 0.0; int transFlag = -1; int rows = data.getRowDimension(); int cols =
     * data.getColumnDimension(); Matrix result = null;
     * 
     * //System.out.println(" cumprod "); if(data.isVector()){
     * //System.out.println(" data.isVector() "); Matrix copy = data.copy();
     * if(copy.isColVector()){ copy = copy.toRowVector(); //transform into a row
     * vector. transFlag = 1; } int c = copy.length(); if(c==1){//if only one
     * element , return this element. return new Matrix(1,1,copy.get(0,0)); }
     * result = new Matrix(1,c); result.set(0,0,copy.get(0,0)); for(int i=1; i<c
     * ; i++){ temp = copy.get(0,i) ; temp = temp*result.get(0,i-1);
     * //System.out.println("temp_"+i+" = "+temp); result.set(0,i,temp); }
     * if(transFlag != -1){//if it has been transposed ,then transposed it back.
     * return result.toColVector(); } return result; }
     * 
     * result = new Matrix(rows,cols); switch(Dim){ case ROW: for(int i=0 ;
     * i<cols ; i++){ result.set(0,i,data.get(0,i)); for(int i=0 ; i<rows ;
     * i++){ //temp += data.get(i,i); //result.set(i,i,temp); temp =
     * data.get(i,i) ; temp = temp*result.get(i-1,i); result.set(i,i,temp); } }
     * break; case COL: for(int i=0 ; i<rows ; i++){
     * result.set(i,0,data.get(i,0)); for(int i=0; i<cols ; i++){ //temp +=
     * data.get(i,i); //result.set(i,i,temp); temp = data.get(i,i) ; temp =
     * temp*result.get(i,i-1); result.set(i,i,temp); } } break; default: throw
     * new IllegalArgumentException("cumprod : Dimension  "+Dim.toString()+
     * " , not supported."); }//end switch return result; }
     */
    /**
     * This method is overloaded to do cumulative sums along the columns of each
     * rows. The cumulative sum of the elements of matrix, along the spefied
     * dimension , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.cumsum(A) , returns ( 148, 279, 454
     * )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * ( 19, 10, 9, 9 ) JDatafun.cumsum(A) = | 24, 28, 9, 21 | ( 36, 43, 25, 37
     * )
     * 
     * @return Matrix The Matrix cumulated summed matrix of <tt>data</tt>.
     * @param data
     * @deprecated Use <B>cumsum(Matrix A)</B> instead.
     */
    public static Matrix cumsumvec(Matrix data)
    {
        return cumsumvec(data, Dimension.ROW);
    }

    /**
     * The cumulative sum of the elements of matrix, along the spefied dimension
     * , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.cumsum(A,Dimension.ROW) , returns (
     * 148, 279, 454 )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * ( 19, 10, 9, 9 ) JDatafun.cumsum(A,Dimension.ROW) = | 24, 28, 9, 21 | (
     * 36, 43, 25, 37 )
     * 
     * ( 19, 29, 38, 47 ) JDatafun.cumsum(A,Dimension.COL) = | 5, 23, 23, 35 | (
     * 12, 27, 43, 59 )
     * 
     * @return Matrix The Matrix cumulated summed matrix of <tt>data</tt>.
     * @param data
     * @param Dim
     *            Integer for the dimension for cumulated summing.
     * @deprecated Use <B>cumsum(Matrix A, Dimension dim)</B> instead.
     */
    public static Matrix cumsumvec(Matrix data, Dimension Dim)
    {
        double temp = 0.0;
        int transFlag = -1;
        int rows = data.getRowDimension();
        int cols = data.getColumnDimension();
        Matrix result = null;

        if (data.isVector() == false)
        {
            throw new IllegalArgumentException("cumsumvec : Data must be a vector and not a matrix.");
        }

        result = new Matrix(rows, cols);
        switch (Dim)
        {
        case ROW:
            for (int j = 0; j < cols; j++)
            {
                temp = 0.;
                for (int i = 0; i < rows; i++)
                {
                    temp += data.get(i, j);
                    result.set(i, j, temp);
                }
            }
            break;
        case COL:
            for (int i = 0; i < rows; i++)
            {
                temp = 0.;
                for (int j = 0; j < cols; j++)
                {
                    temp += data.get(i, j);
                    result.set(i, j, temp);
                }
            }
            break;
        default:
            throw new IllegalArgumentException("cumsumvec : Dimension  " + Dim.toString() + " , not supported.");
        }// end switch
        return result;
    }

    /**
     * 
     * @param data
     * @return
     */
    public static Indices cumsum(Indices data)
    {
        return (new CumsumInd(data)).getCumsum();
    }

    /**
     * The cumulative sum of the elements of matrix, along the spefied dimension
     * , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.cumsum(A,Dimension.ROW) , returns (
     * 148, 279, 454 )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * ( 19, 10, 9, 9 ) JDatafun.cumsum(A,Dimension.ROW) = | 24, 28, 9, 21 | (
     * 36, 43, 25, 37 )
     * 
     * ( 19, 29, 38, 47 ) JDatafun.cumsum(A,Dimension.COL) = | 5, 23, 23, 35 | (
     * 12, 27, 43, 59 )
     * 
     * 
     * @return Matrix The Matrix cumulated summed matrix of <tt>data</tt>.
     * @param data
     * @param Dim
     *            Integer for the dimension for cumulated summing.
     */
    public static Indices cumsum(Indices data, Dimension Dim)
    {
        return (new CumsumInd(data, Dim)).getCumsum();
    }

    /**
     * This method is overloaded to do cumulative sums along the columns of each
     * rows. The cumulative sum of the elements of matrix, along the spefied
     * dimension , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.cumsum(A) , returns ( 148, 279, 454
     * )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * ( 19, 10, 9, 9 ) JDatafun.cumsum(A) = | 24, 28, 9, 21 | ( 36, 43, 25, 37
     * )
     * 
     * @return Matrix The Matrix cumulated summed matrix of <tt>data</tt>.
     * @param data
     * @deprecated Use <B>cumsum(Indices A)</B> instead.
     */
    public static Indices cumsumvec(Indices data)
    {
        return cumsumvec(data, Dimension.ROW);
    }

    /**
     * The cumulative sum of the elements of matrix, along the spefied dimension
     * , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.cumsum(A,Dimension.ROW) , returns (
     * 148, 279, 454 )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * ( 19, 10, 9, 9 ) JDatafun.cumsum(A,Dimension.ROW) = | 24, 28, 9, 21 | (
     * 36, 43, 25, 37 )
     * 
     * ( 19, 29, 38, 47 ) JDatafun.cumsum(A,Dimension.COL) = | 5, 23, 23, 35 | (
     * 12, 27, 43, 59 )
     * 
     * @param data
     * @param Dim
     *            Integer for the dimension for cumulated summing.
     * @return Matrix The Matrix cumulated summed matrix of <tt>data</tt>.
     * @deprecated Use <B>cumsum(Indices A, Dimension dim)</B> instead.
     */
    public static Indices cumsumvec(Indices data, Dimension Dim)
    {
        int temp = 0;
        int transFlag = -1;
        int rows = data.getRowDimension();
        int cols = data.getColumnDimension();
        Indices result = null;

        if (data.isVector() == false)
        {
            throw new IllegalArgumentException("cumsumvec : Data must be a vector and not a matrix.");
        }

        result = new Indices(rows, cols);
        switch (Dim)
        {
        case ROW:
            for (int j = 0; j < cols; j++)
            {
                temp = 0;
                for (int i = 0; i < rows; i++)
                {
                    temp += data.get(i, j);
                    result.set(i, j, temp);
                }
            }
            break;
        case COL:
            for (int i = 0; i < rows; i++)
            {
                temp = 0;
                for (int j = 0; j < cols; j++)
                {
                    temp += data.get(i, j);
                    result.set(i, j, temp);
                }
            }
            break;
        default:
            throw new IllegalArgumentException("cumsumvec : Dimension  " + Dim.toString() + " , not supported.");
        }// end switch
        return result;
    }

    /**
     * This method over-loading is the same as : Matrix.max(X,1)
     * 
     * @param X
     *            Matrix matrix
     * @return Matrix Matrix matrix
     */
    public static Matrix max(Matrix X)
    {
        return Maximum.max(X);
    }

    /**
     * For vectors, Matrix.max(X,1) is the smallest element in X, eg:
     * 
     * If X = (9,-8,3,-7,4,10) , JDatafun.max(X) returns a Matrix matrix with 10
     * as the only element.
     * 
     * For matrices, eg:
     * 
     * (9 , -7, 1, 6) If X = |10, 9, 5, 3| (0 , 6, 8, 2)
     * 
     * JDatafun.max(X,Dimension.ROW) = (10, 9, 8, 6) , maximum of each column.
     * 
     * ( 9 ) JDatafun.max(X,Dimension.COL) = | 10 | ,returns a Matrix column
     * vector matrix with a maximum of each row. ( 8 )
     * 
     * @param X
     *            Matrix matrix
     * @param Dim
     *            integer constant specifying the dimension to operate.
     * @return Matrix Matrix matrix
     */
    public static Matrix max(Matrix X, Dimension Dim)
    {
        return Maximum.max(X, Dim);
    }

    /**
     * For vectors, Matrix.max(X,Y) is the biggest element in each corresponding
     * entries of X and Y eg:
     * 
     * If X = (9,-8,3,-7,4,10) , Y = (3, -4,8,-10,5,9) ,
     * 
     * Matrix.max(X,Y) = (9 , -4, 8, -7, 5, 10), returns a Matrix matrix same
     * dimensions as X and Y.
     * 
     * For matrices, eg:
     * 
     * (9 , -7, 1, 6) ( 2 , -5, 2, 9 ) If X = |10, 9, 5, 3| , Y = | 4 , 6, 13,
     * -1 | (0 , 6, 8, 2) ( 2 , 11, 20, -16)
     * 
     * ( 9 , -5, 2, 9 ) Matrix.max(X,Y) = | 10 , 9, 13, 3 | ( 2 , 11, 20, 2 )
     * 
     * @param X
     *            Matrix matrix
     * @param Y
     *            Matrix matrix
     * @return Matrix Matrix matrix
     */
    public static Matrix max(Matrix X, Matrix Y)
    {
        int x_rows = X.getRowDimension();
        int x_cols = X.getColumnDimension();
        int y_rows = Y.getRowDimension();
        int y_cols = Y.getColumnDimension();
        Matrix result = null;
        if (x_rows != y_rows || x_cols != y_cols)
        {
            throw new IllegalArgumentException("max : Incompatible matrix dimensions.");
        }
        double val = 0.0;
        result = new Matrix(x_rows, x_cols);
        for (int i = 0; i < x_rows; i++)
        {
            for (int j = 0; j < x_cols; j++)
            {
                val = X.get(i, j) > Y.get(i, j) ? X.get(i, j) : Y.get(i, j);
                result.set(i, j, val);
            }
        }
        return result;
    }

    /**
     * For vectors, Matrix.max(X,1.5) is the smallest element in comparison of
     * each entries of X to 2.4 eg:
     * 
     * If X = (9,-8,3,-7,4, 1) , then Matrix.max(X,1.5) = (9 , 1.5, 3, 1.5, 4,
     * 1.5)
     * 
     * For matrices, eg:
     * 
     * (9 , -7, 1, 6) If X = |10, 9, -5, 3| , (0 , -6, 8, 2)
     * 
     * ( 9 , 1.5, 1.5, 6 ) Matrix.max(X,1.5) = | 10 , 9, 1.5, 3 | ( 1.5, 1.5, 8,
     * 2 )
     * 
     * @param X
     *            Matrix matrix
     * @param val
     *            double value
     * @return Matrix B
     */
    public static Matrix max(Matrix B, double val)
    {
        int x_rows = B.getRowDimension(), x_cols = B.getColumnDimension();
        Matrix X = new Matrix(x_rows, x_cols);
        double[][] C = X.getArray();
        double[][] M = B.getArray();
        for (int i = 0; i < x_rows; i++)
        {
            for (int j = 0; j < x_cols; j++)
            {
                C[i][j] = M[i][j] > val ? M[i][j] : val;
            }
        }
        return X;
    }

    /**
     * overload
     * 
     * @param X
     *            Matrix matrix
     * @param val
     *            double value
     * @return Matrix B
     */
    public static Matrix max(double val, Matrix B)
    {
        return max(B, val);
    }

    /**
     * This method over-load is exactly the same as,
     * JDatafun.min(X,Dimension.ROW)
     * 
     * @param X
     *            Matrix matrix
     * @return Matrix Matrix matrix
     */
    public static Matrix min(Matrix X)
    {
        return Minimum.min(X);
    }

    /**
     * For vectors, Matrix.min(X,1) is the smallest element in X, eg:
     * 
     * If X = (9,-8,3,-7,4,10) , JDatafun.min(X) returns a Matrix matrix with -8
     * as the only element.
     * 
     * For matrices, eg:
     * 
     * (9 , -7, 1, 6) If X = |10, 9, 5, 3| , then JDatafun.min(X,Dimension.ROW)
     * = (0, -7, 1, 2) , minimum of each column. (0 , 6, 8, 2)
     * 
     * @param X
     *            Matrix matrix
     * @param Dim
     *            integer constant specifying the dimension to operate.
     * @return Matrix Matrix matrix
     */
    public static Matrix min(Matrix X, Dimension Dim)
    {
        return Minimum.min(X, Dim);
    }

    public static Matrix min(Matrix X, Matrix Y)
    {
        int x_rows = X.getRowDimension(), x_cols = X.getColumnDimension(), y_rows = Y.getRowDimension(), y_cols = Y
                .getColumnDimension();
        Matrix result = null;
        if (x_rows != y_rows || x_cols != y_cols)
        {
            throw new IllegalArgumentException("min : Incompatible matrix dimensions.");
        }
        result = new Matrix(x_rows, x_cols);
        double val = 0.0;
        for (int i = 0; i < x_rows; i++)
        {
            for (int j = 0; j < x_cols; j++)
            {
                val = X.get(i, j) < Y.get(i, j) ? X.get(i, j) : Y.get(i, j);
                result.set(i, j, val);
            }
        }
        return result;
    }

    /**
     * For vectors, Matrix.min(X,2.4) is the smallest element in comparison of
     * each entries of X to 2.4 eg:
     * 
     * If X = (9,-8,3,-7,4, 1) , then Matrix.min(X,2.4) = (2.4 , -8, 2.4, -7,
     * 2.4, 1)
     * 
     * For matrices, eg:
     * 
     * (9 , -7, 1, 6) If X = |10, 9, -5, 3| , (0 , -6, 8, 2)
     * 
     * ( 2.4 , -7 , 1, 2.4 ) Matrix.min(X,2.4) = | 2.4 , 2.4, -5, 2.4 | ( 0 ,
     * -6, 2.4, 2 )
     * 
     * @param X
     *            Matrix matrix
     * @param val
     *            double value
     * @return Matrix Matrix matrix
     */
    public static Matrix min(Matrix X, double val)
    {
        int x_rows = X.getRowDimension(), x_cols = X.getColumnDimension();
        Matrix result = new Matrix(x_rows, x_cols);
        double t = 0.0;
        for (int i = 0; i < x_rows; i++)
        {
            for (int j = 0; j < x_cols; j++)
            {
                t = X.get(i, j) < val ? X.get(i, j) : val;
                result.set(i, j, t);
            }
        }
        return result;
    }

    /**
     * This method calculates standard deviation of the data . This method
     * normalizes by "n" , "n" is the sequence length.
     * 
     * @param Matrix
     *            The input data matrix set
     * @return Matrix The Matrix standard deviation matrix of <tt>data</tt>.
     */
    public static Matrix std(Matrix data)
    {
        return std(data, false, Dimension.ROW);
    }

    public static Matrix std(Matrix data, boolean flag)
    {
        return std(data, flag, Dimension.ROW);
    }

    /**
     * This method calculates standard deviation of the data set along the
     * dimension <i>Dim</i>. This method normalizes by "n-1" ,if parameter
     * flag==true , or normalizes by "n" , if parameter flag==false, "n" is the
     * sequence length.
     * 
     * @param Matrix
     *            The input data matrix set
     * @param flag
     *            A boolean to determine whether to normalize as "n" or "n-1"
     * @param Dim
     *            Integer for the dimension to normalize
     * @return Matrix The Matrix standard deviation matrix of <tt>data</tt>.
     */
    public static Matrix std(Matrix data, boolean flag, Dimension Dim)
    {
        Matrix stdDev = null;

        try
        {
            Matrix varData = JDatafun.var(data, flag, Dim);
            /*
             * if (doDeb) { FindInd find = varData.LT(0.0).findIJ(); if
             * (!find.isNull()) { Matrix neg = varData.getEls(find.getIndex());
             * neg.printInLabel("neg"); Indices IndI = find.getI();
             * IndI.printInLabel("IndI");
             * data.getRows(IndI.getRowPackedCopy()).printInLabel("Row");
             * Indices IndJ = find.getJ(); IndJ.printInLabel("IndJ");
             * 
             * System.out.println("Stop"); } }
             */

            FindInd find = varData.LTEQ(varTol).findIJ();
            if (!find.isNull())
            {
                int[] findInd = find.getIndex();
                varData.setElements(findInd, 0.0);
            }

            stdDev = JElfun.sqrt(varData);
        }
        catch (IllegalArgumentException iae)
        {
            throw iae;
        }
        return stdDev;
    }

    /**
     * This method calculates variance of a data set along the columns of each
     * rows. This method normalizes by "n" where "n" is the sequence length.
     * 
     * @param Matrix
     *            The input data matrix set
     * @return Matrix The Matrix variance matrix of <tt>data</tt>.
     */
    public static Matrix var(Matrix data)
    {
        return var(data, false, Dimension.ROW);
    }

    /**
     * This method calculates variance of a data set along the dimension
     * <i>Dim</i>. This method normalizes by "n-1" where if parameter
     * flag==true, or normalizes by "n" , if parameter flag==false, "n" is the
     * sequence length.
     * 
     * @param Matrix
     *            The input data matrix set
     * @param flag
     *            A boolean to determine whether to normalize as "n" or "n-1"
     * @param Dim
     *            Integer for the dimension to normalize
     * @return Matrix The Matrix variance matrix of <tt>data</tt>.
     */
    public static Matrix var(Matrix data, boolean flag, Dimension Dim)
    {
        int rows = data.getRowDimension();
        int cols = data.getColumnDimension();

        int Flag = flag ? 1 : 0;
        double sum = 0., sum2 = 0., v = 0., r1 = 0., r2 = 0.;

        Matrix result = null;
        if (data.isVector())
        {
            Matrix temp = data.toRowVector();
            int c = temp.getColumnDimension();
            r1 = c * c;
            r2 = c * (c - 1);
            for (int j = 0; j < c; j++)
            {
                sum += temp.get(0, j);
                sum2 += temp.get(0, j) * temp.get(0, j);
            }
            if (Flag == 1)
            {
                v = (c * sum2 - sum * sum) / r1;
            }
            else
            {
                if (r2 == 0.)
                {
                    v = 0.;
                }
                else
                {
                    v = (c * sum2 - sum * sum) / r2;
                }
            }
            if (doDeb)
            {

                if (v < 0)
                {
                    System.out.println(" v =" + v);
                    data.printInLabel("data", 15);
                    System.out.println("Stop");
                }
            }
            return new Matrix(1, 1, v);
        }

        switch (Dim)
        {
        case ROW:
            result = new Matrix(1, cols);
            r1 = rows * rows;
            r2 = rows * (rows - 1);
            for (int j = 0; j < cols; j++)
            {
                sum = 0.;
                sum2 = 0.;
                for (int i = 0; i < rows; i++)
                {
                    sum += data.get(i, j);
                    sum2 += data.get(i, j) * data.get(i, j);
                }
                if (Flag == 1)
                {
                    v = (rows * sum2 - sum * sum) / r1;
                }
                else
                {
                    if (r2 == 0.)
                    {
                        v = 0.;
                    }
                    else
                    {
                        v = (rows * sum2 - sum * sum) / r2;
                    }
                }
                result.set(0, j, v);
            }
            break;
        case COL:
            result = new Matrix(rows, 1);
            r1 = cols * cols;
            r2 = cols * (cols - 1);
            for (int i = 0; i < rows; i++)
            {
                sum = 0.;
                sum2 = 0.;
                for (int j = 0; j < cols; j++)
                {
                    sum += data.get(i, j);
                    sum2 += data.get(i, j) * data.get(i, j);
                }
                if (Flag == 1)
                {
                    v = (cols * sum2 - sum * sum) / r1;
                }
                else
                {
                    if (r2 == 0.)
                    {
                        v = 0.;
                    }
                    else
                    {
                        v = (cols * sum2 - sum * sum) / r2;
                    }
                }
                result.set(i, 0, v);
            }
            break;
        default:
            throw new IllegalArgumentException("var : Dimension  " + Dim.toString() + " , not supported.");
        }// end switch
        return result;
    }// end method.

    /**
     * categorize data into interval-quantities plus frequency of each
     * interval-quantity.
     */
    public static Matrix[] categoriseData(Matrix data, int bins)
    {
        Matrix[] rMat = null;
        if (bins < 1)
        {
            throw new IllegalArgumentException(" categoriseData : Input parameter, bins = " + bins
                    + " must be at least one.");
        }
        Matrix Data = data.toRowVector();
        ;
        Data = JDatafun.sort(Data);
        int siz = Data.length();
        double min = Data.get(0, 0);
        double max = Data.end();
        double val = 0.0;
        int count = 0;

        if (min == max)
        {
            return new Matrix[]
            {
                    new Matrix(1, 1, min), new Matrix(1, 1, (double) siz)
            };
        }

        /*
         * if(bins==2){ val = (min+max)/2.0; for(int i=0; i<siz; i++){
         * //System.out
         * .println(val+"<="+Data.get(0,i)+" : "+(val<=Data.get(0,i)));
         * if(Data.get(0,i)<=val) { count++; } else { break; } }//end for Matrix
         * frq = new Matrix(1,2,Double.NaN); frq.set(0,0,(double)count);
         * frq.set(0,1,(double)(siz-count)); return new Matrix[]{ new
         * Matrix(1,2,val), frq }; }
         */

        Matrix intervals = Matrix.linspace(min, max, bins);
        val = intervals.get(0, 1) - intervals.get(0, 0);
        val = val / 2.0;
        intervals = intervals.plus(val);
        // intervals = intervals.remainingColumns(bins);

        // System.out.println("min = "+min+" :   max = "+max);
        // System.out.println("*** intervals ***"); intervals.print(2,2);

        int len = intervals.length();
        Matrix freq = new Matrix(1, len);
        count = 0;

        for (int i = 0; i < len; i++)
        {
            count = 0; // re-initialize
            if (i == 0)
            {
                for (int j = 0; j < siz; j++)
                {
                    if (Data.get(0, j) <= intervals.get(0, i))
                    {
                        count++;
                    }
                }// end for
            }
            else if (i == (len - 1))
            {
                for (int j = 0; j < siz; j++)
                {
                    if (Data.get(0, j) > intervals.get(0, i - 1))
                    {
                        count++;
                    }
                }// end for
            }
            else
            {
                for (int j = 0; j < siz; j++)
                {
                    if ((Data.get(0, j) <= intervals.get(0, i)) && (Data.get(0, j) > intervals.get(0, i - 1)))
                    {
                        count++;
                    }
                }// end for
            }// end else
            freq.set(0, i, (double) count);
        }// for main interval loop

        rMat = new Matrix[]
        {
                intervals, freq
        };

        return rMat;
    }// end method

    public static Matrix[] categoriseData(Matrix data)
    {
        return categoriseData(data, false);
    }

    /**
     * categorize data into quantities plus frequency of each quantity.
     */
    public static Matrix[] categoriseData(Matrix data, boolean hasSorted)
    {
        if (data.isVector() == false)
        {
            throw new IllegalArgumentException("categoriseData - Parameter 'data' must be a row or column vector.");
        }
        Matrix Data = null;
        if (data.isColVector())
        {
            Data = data.toRowVector();
        }
        else
        {
            Data = data.copy();
        }
        int len = Data.length();

        if (hasSorted == false)
        {
            Data = JDatafun.sort(Data);
        }

        int count = 0;

        ArrayList<Double> qty = new ArrayList<Double>();
        ArrayList<Double> freq = new ArrayList<Double>();

        double currentVal = Data.get(0, 0);
        for (int i = 0; i < len; i++)
        {
            if (currentVal == Data.get(0, i))
            {
                count++;
                if (i == (len - 1))
                {
                    qty.add(new Double(Data.get(0, i)));
                    freq.add(new Double(count));
                }
            }
            else
            {
                qty.add(new Double(Data.get(0, i - 1)));
                freq.add(new Double(count));
                currentVal = Data.get(0, i);
                count = 1;
                if (i == (len - 1))
                {
                    qty.add(new Double(Data.get(0, i)));
                    freq.add(new Double(count));
                }
            }
        }// end for

        len = qty.size(); // System.out.println(" qty.size() = "+qty.size()+"   :    freq.size() = "+freq.size());
        Matrix QTY = new Matrix(1, len);
        Matrix FRQ = new Matrix(1, len);
        double val = 0.0;

        for (int i = 0; i < len; i++)
        {
            val = qty.get(i).doubleValue();
            QTY.set(0, i, val);
            val = freq.get(i).doubleValue();
            FRQ.set(0, i, val);
        }// end for

        return new Matrix[]
        {
                QTY, FRQ
        };
    }// end method

    /**
     * Sort data's in ascending order. This overloaded method is the same as:
     * JDatafun.sort(matrix,Dimension.ROW);
     * 
     * @return Matrix The Matrix sort matrix of the <tt>data</tt>.
     * @param matrix
     */
    public static Matrix sort(Matrix matrix)
    {
        // return Sorting.sort(matrix);//sort(matrix, Dimension.ROW);
        QuickSort sort = new QuickSortMat(matrix, false, true);
        return (Matrix) sort.getSortedObject();
    }

    /**
     * Sort data's in ascending order. For vectors, If A = (7, 0, 4, 2, 5),
     * JDatafun.sort(A,Dimension.ROW) = ( 0, 2, 4, 5, 7 )
     * 
     * Sorting data's in descending order, do this:
     * JDatafun.sort(A,Dimension.ROW).flipLR() = ( 7, 5, 4, 2, 0 )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * //sort in ascending order, along all columns of each rows ( 5, 10, 0, 9 )
     * JDatafun.sort(A,Dimension.ROW) = | 12, 15, 9, 12 | ( 19, 18, 16, 16 )
     * //sort in descending order, along all columns of each rows ( 19, 18, 16,
     * 16 ) JDatafun.sort(A,Dimension.ROW).flipUD() = | 12, 15, 9, 12 | ( 5, 10,
     * 0, 9 )
     * 
     * //sort in ascending order, along all rows of each columns ( 9, 9, 10, 19
     * ) JDatafun.sort(A,Dimension.COL) = | 0, 5, 12, 18 | ( 12, 15, 16, 16 )
     * //sort in descending order, along all rows of each columns ( 19, 10, 9, 9
     * ) JDatafun.sort(A,Dimension.COL).flipLR() = | 18, 12, 5, 0 | ( 16, 16,
     * 15, 12 )
     * 
     * 
     * @return Matrix The Matrix sort matrix of the <tt>data</tt>.
     * @param AbsDim
     * @param data
     *            The Matrix input matrix data set.
     */
    public static Matrix sort(Matrix data, Dimension AbsDim)
    {
        // return Sorting.sort(data, AbsDim); //Indices A, Dimension dim,
        // boolean computeSortedIndex, boolean sortedCopy
        QuickSort sort = new QuickSortMat(data, AbsDim, false, true);
        return (Matrix) sort.getSortedObject();
    }// end sort

    /**
     * 
     * @param matrix
     * @return
     */
    public static Indices sort(Indices matrix)
    {
        // return Sorting.sort(matrix);
        QuickSort sort = new QuickSortInd(matrix, false, true);
        return (Indices) sort.getSortedObject();
    }

    /**
     * Sort data's in ascending order. For vectors, If A = (7, 0, 4, 2, 5),
     * JDatafun.sort(A,Dimension.ROW) = ( 0, 2, 4, 5, 7 )
     * 
     * Sorting data's in descending order, do this:
     * JDatafun.sort(A,Dimension.ROW).flipLR() = ( 7, 5, 4, 2, 0 )
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * //sort in ascending order, along all columns of each rows ( 5, 10, 0, 9 )
     * JDatafun.sort(A,Dimension.ROW) = | 12, 15, 9, 12 | ( 19, 18, 16, 16 )
     * //sort in descending order, along all columns of each rows ( 19, 18, 16,
     * 16 ) JDatafun.sort(A,Dimension.ROW).flipUD() = | 12, 15, 9, 12 | ( 5, 10,
     * 0, 9 )
     * 
     * //sort in ascending order, along all rows of each columns ( 9, 9, 10, 19
     * ) JDatafun.sort(A,Dimension.COL) = | 0, 5, 12, 18 | ( 12, 15, 16, 16 )
     * //sort in descending order, along all rows of each columns ( 19, 10, 9, 9
     * ) JDatafun.sort(A,Dimension.COL).flipLR() = | 18, 12, 5, 0 | ( 16, 16,
     * 15, 12 )
     * 
     * 
     * @return Matrix The Matrix sort matrix of the <tt>data</tt>.
     * @param AbsDim
     * @param data
     *            The Matrix input matrix data set.
     */
    public static Indices sort(Indices data, Dimension AbsDim)
    {
        // return Sorting.sort(data, AbsDim);
        QuickSort sort = new QuickSortInd(data, AbsDim, false, true);
        return (Indices) sort.getSortedObject();
    }// end sort

    /**
     * Sort data's in ascending order. This overloaded method is the same as:
     * JDatafun.sort(matrix,Dimension.ROW);
     * 
     * @return Matrix The Matrix sort matrix of the <tt>data</tt>.
     * @deprecated Must use the Class Sort from package datafun.
     * @param matrix
     * @deprecated Use class <B>SortMat(Matrix A)</B> instead.
     */
    public static Object[] sortInd(Matrix matrix)
    {
        return JDatafun.sortInd(matrix, Dimension.ROW);
    }

    /**
     * 
     * @param A
     * @param Dim
     * @return
     * @deprecated Use class <B>SortMat(Matrix A, Dimension dim)</B> instead.
     */
    public static Object[] sortInd(Matrix A, Dimension Dim)
    {

        Indices indices = null;
        int[][] I = null;
        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        Matrix sort = A.copy();
        double[][] S = sort.getArray();

        double hold = 0.0;
        int holdInt = 0;

        if (A.isVector())
        {

            // indices = A.generateIndices();
            int c = A.length();
            if (c == 1)
            {// if only one element , return this element.
                Matrix singleElement = new Matrix(1, 1);
                singleElement.set(0, 0, A.get(0, 0));
                indices = new Indices(1, 1);
                I[0][0] = 0;
                return new Object[]
                {
                        singleElement, indices
                };
            }

            if (A.isRowVector())
            {
                indices = A.generateIndices(false);
                I = indices.getIndexValues();
                for (int pass = 1; pass < c; pass++)
                {
                    for (int j = 0; j < (c - 1); j++)
                    {
                        if (S[0][j] > S[0][j + 1])
                        {
                            hold = S[0][j];
                            S[0][j] = S[0][j + 1];
                            S[0][j + 1] = hold;
                            // reorder 'B'
                            holdInt = I[0][j];
                            I[0][j] = I[0][j + 1];
                            I[0][j + 1] = holdInt;
                        }// end if
                    }// end for
                }// end for
            }
            else
            {
                indices = A.generateIndices();
                I = indices.getIndexValues();
                for (int pass = 1; pass < c; pass++)
                {
                    for (int i = 0; i < (c - 1); i++)
                    {
                        if (S[i][0] > S[i + 1][0])
                        {
                            hold = S[i][0];
                            S[i][0] = S[i + 1][0];
                            S[i + 1][0] = hold;
                            // reorder 'I'
                            holdInt = I[i][0];
                            I[i][0] = I[i + 1][0];
                            I[i + 1][0] = holdInt;
                        }// end if
                    }// end for
                }// end for
            }
            return new Object[]
            {
                    sort, indices
            };
        }

        if (Dim == Dimension.ROW)
        {
            indices = A.generateIndices();
            I = indices.getIndexValues();
            for (int j = 0; j < n; j++)
            {
                for (int pass = 1; pass < m; pass++)
                {
                    for (int i = 0; i < (m - 1); i++)
                    {
                        if (S[i][j] > S[i + 1][j])
                        {
                            hold = S[i][j];
                            S[i][j] = S[i + 1][j];
                            S[i + 1][j] = hold;
                            // reorder 'B'
                            holdInt = I[i][j];
                            I[i][j] = I[i + 1][j];
                            I[i + 1][j] = holdInt;
                        }// end if
                    }// end for
                }// end for
            }// end for
        }// end if
        else if (Dim == Dimension.COL)
        {
            indices = A.generateIndices(false);
            I = indices.getIndexValues();
            for (int i = 0; i < m; i++)
            {
                for (int pass = 1; pass < n; pass++)
                {
                    for (int j = 0; j < (n - 1); j++)
                    {
                        if (S[i][j] > S[i][j + 1])
                        {
                            hold = S[i][j];
                            S[i][j] = S[i][j + 1];
                            S[i][j + 1] = hold;
                            // reorder 'B'
                            holdInt = I[i][j];
                            I[i][j] = I[i][j + 1];
                            I[i][j + 1] = holdInt;
                        }
                    }// end for
                }// end for
            }// end for
        }// end else if
        else
        {
            throw new IllegalArgumentException("sortInd : Dimension  " + Dim.toString() + " , not supported.");
        }
        return new Object[]
        {
                sort, indices
        };
    }

    /**
     * 
     * @param A
     * @return
     * @deprecated Use class <B>SortMat(Indices A)</B> instead.
     */
    public static Object[] sortInd(Indices A)
    {
        return sortInd(A, Dimension.ROW);
    }

    /**
     * 
     * @param A
     * @param Dim
     * @return
     * @deprecated Use class <B>SortMat(Indices A, Dimension dim)</B> instead.
     */
    public static Object[] sortInd(Indices A, Dimension Dim)
    {

        Indices indices = null;
        int[][] I = null;
        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        Indices sort = A.copy(); //
        // System.out.println("------- sort ---------");
        // sort.print(4);
        // System.out.println();
        int[][] S = sort.getArray();

        int hold = 0;
        int holdInt = 0;

        if (A.isVector())
        {

            // indices = A.generateIndices();
            int c = A.length();
            if (c == 1)
            {// if only one element , return this element.
                Indices singleElement = new Indices(1, 1);
                singleElement.set(0, 0, A.get(0, 0));
                indices = new Indices(1, 1);
                I[0][0] = 0;
                return new Object[]
                {
                        singleElement, indices
                };
            }

            if (A.isRowVector())
            {
                indices = A.generateIndices(false);
                I = indices.getIndexValues();
                // System.out.println("------- I 1 ---------");
                // indices.print(4);
                for (int pass = 1; pass < c; pass++)
                {
                    for (int j = 0; j < (c - 1); j++)
                    {
                        if (S[0][j] > S[0][j + 1])
                        {
                            hold = S[0][j];
                            S[0][j] = S[0][j + 1];
                            S[0][j + 1] = hold;
                            // reorder 'B'
                            holdInt = I[0][j];
                            I[0][j] = I[0][j + 1];
                            I[0][j + 1] = holdInt;
                        }// end if
                    }// end for
                }// end for
                 // System.out.println("Dimension.ROW");
                 // System.out.println("------- I 2 ---------");
                 // indices.print(4);
            }
            else
            {
                indices = A.generateIndices();
                I = indices.getIndexValues();
                for (int pass = 1; pass < c; pass++)
                {
                    for (int i = 0; i < (c - 1); i++)
                    {
                        if (S[i][0] > S[i + 1][0])
                        {
                            hold = S[i][0];
                            S[i][0] = S[i + 1][0];
                            S[i + 1][0] = hold;
                            // reorder 'I'
                            holdInt = I[i][0];
                            I[i][0] = I[i + 1][0];
                            I[i + 1][0] = holdInt;
                        }// end if
                    }// end for
                }// end for
            }
            return new Object[]
            {
                    sort, indices
            };
        }

        if (Dim == Dimension.ROW)
        {
            indices = A.generateIndices();
            I = indices.getIndexValues();
            for (int j = 0; j < n; j++)
            {
                for (int pass = 1; pass < m; pass++)
                {
                    for (int i = 0; i < (m - 1); i++)
                    {
                        if (S[i][j] > S[i + 1][j])
                        {
                            hold = S[i][j];
                            S[i][j] = S[i + 1][j];
                            S[i + 1][j] = hold;
                            // reorder 'B'
                            holdInt = I[i][j];
                            I[i][j] = I[i + 1][j];
                            I[i + 1][j] = holdInt;
                        }// end if
                    }// end for
                }// end for
            }// end for
             // System.out.println("Dimension.ROW");
        }// end if
        else if (Dim == Dimension.COL)
        {
            indices = A.generateIndices(false);
            I = indices.getIndexValues();
            for (int i = 0; i < m; i++)
            {
                for (int pass = 1; pass < n; pass++)
                {
                    for (int j = 0; j < (n - 1); j++)
                    {
                        if (S[i][j] > S[i][j + 1])
                        {
                            hold = S[i][j];
                            S[i][j] = S[i][j + 1];
                            S[i][j + 1] = hold;
                            // reorder 'B'
                            holdInt = I[i][j];
                            I[i][j] = I[i][j + 1];
                            I[i][j + 1] = holdInt;
                        }
                    }// end for
                }// end for
            }// end for
        }// end else if

        return new Object[]
        {
                sort, indices
        };
    }

    /**
     * 
     * @param matA
     * @param matB
     * @return
     */
    public static Matrix pivotSort(Matrix matA, Matrix matB)
    {
        return pivotSort(matA, matB, Dimension.ROW);// JDatafun.sort(matrix,Dimension.ROW);
    }

    /**
     * This method sorts 'A' and return a new Matrix object and also 'B' is
     * re-arranged to the sorting order of 'A'
     */
    public static Matrix pivotSort(Matrix A, Matrix B, Dimension AbsDim)
    {
        int row = A.getRowDimension();
        int col = A.getColumnDimension();

        if ((row != B.getRowDimension()) && (col != B.getColumnDimension()))
        {
            throw new IllegalArgumentException("pivotSort:  Matrix A and B have incompatible sizes.");
        }

        int flag = -1;

        Matrix singleElement = new Matrix(1, 1);
        double hold = 0.;
        double[][] a = A.getArrayCopy();
        double[][] b_reorder = B.getArray();

        if (A.isVector())
        {
            Matrix temp = new Matrix(A.getArrayCopy());// make local copy
            if (!temp.isRowVector())
            {
                temp = A.transpose(); // transform into a row vector.
                b_reorder = B.transpose().getArray();
                flag = 1;
            }
            int c = temp.getColumnDimension();
            if (c == 1)
            {// if only one element , return this element.
                singleElement.set(0, 0, temp.get(0, 0));
                return singleElement;
            }

            double[][] b = temp.getArray();
            for (int pass = 1; pass < c; pass++)
            {
                for (int j = 0; j < (c - 1); j++)
                {
                    if (b[0][j] > b[0][j + 1])
                    {
                        hold = b[0][j];
                        b[0][j] = b[0][j + 1];
                        b[0][j + 1] = hold;
                        // reorder 'B'
                        hold = b_reorder[0][j];
                        b_reorder[0][j] = b_reorder[0][j + 1];
                        b_reorder[0][j + 1] = hold;
                    }// end if
                }// end for
            }// end for

            if (flag == -1)
            {// if it has not been tranposed.
             // System.out.println("--flag_m1--");
                return new Matrix(b);
            }
            else
            { // if it has been tranposed.
              // System.out.println("--flag_not_m1--");
                B = (new Matrix(b_reorder)).transpose();
                return (new Matrix(b)).transpose();
            }
        }

        if (AbsDim == Dimension.ROW)
        {
            for (int j = 0; j < col; j++)
            {
                for (int pass = 1; pass < row; pass++)
                {
                    for (int i = 0; i < (row - 1); i++)
                    {
                        if (a[i][j] > a[i + 1][j])
                        {
                            hold = a[i][j];
                            a[i][j] = a[i + 1][j];
                            a[i + 1][j] = hold;
                            // reorder 'B'
                            hold = b_reorder[i][j];
                            b_reorder[i][j] = b_reorder[i + 1][j];
                            b_reorder[i + 1][j] = hold;
                        }// end if
                    }// end for
                }// end for
            }// end for
        }// end if
        else if (AbsDim == Dimension.COL)
        {
            for (int i = 0; i < row; i++)
            {
                for (int pass = 1; pass < col; pass++)
                {
                    for (int j = 0; j < (col - 1); j++)
                    {
                        if (a[i][j] > a[i][j + 1])
                        {
                            hold = a[i][j];
                            a[i][j] = a[i][j + 1];
                            a[i][j + 1] = hold;
                            // reorder 'B'
                            hold = b_reorder[i][j];
                            b_reorder[i][j] = b_reorder[i][j + 1];
                            b_reorder[i][j + 1] = hold;
                        }
                    }// end for
                }// end for
            }// end for
        }// end else if
        else
        {
            throw new IllegalArgumentException("pivotSort : Dimension  " + AbsDim.toString() + " , not supported.");
        }
        return new Matrix(a);

    }// end sort

    /**
     * 
     * @param A
     * @param B
     * @return
     * @deprecated Use class <B>SortMat</B> instead.
     */
    public static Object[] syncSort(Matrix A, Indices B)
    {
        return syncSort(A, B, Dimension.ROW);
    }

    /**
     * This method sorts 'A' and return a new Matrix object and also 'B' is
     * re-arranged to the sorting order of 'A'
     * 
     * @deprecated Use class <B>SortMat</B> instead.
     */
    public static Object[] syncSort(Matrix A, Indices B, Dimension AbsDim)
    {
        int row = A.getRowDimension();
        int col = A.getColumnDimension();

        if ((row != B.getRowDimension()) && (col != B.getColumnDimension()))
        {
            throw new IllegalArgumentException("syncSort:  Matrix A and Indices B have incompatible sizes.");
        }

        int flag = -1;

        double hold = 0.;
        int holdInt = 0;
        double[][] a = A.getArrayCopy();
        int[][] b_reorder = B.copy().getArray();

        if (A.isVector())
        {
            Matrix temp = A.copy();// make local copy
            if (!temp.isRowVector())
            {
                temp = A.transpose(); // transform into a row vector.
                b_reorder = B.transpose().getArray();
                flag = 1;
            }
            int c = temp.getColumnDimension();
            if (c == 1)
            {// if only one element , return this element.
             // singleElement.set(0,0,temp.get(0,0));
                return new Object[]
                {
                        temp, B.copy()
                };// singleElement;
            }

            double[][] b = temp.getArray();
            for (int pass = 1; pass < c; pass++)
            {
                for (int j = 0; j < (c - 1); j++)
                {
                    if (b[0][j] > b[0][j + 1])
                    {
                        hold = b[0][j];
                        b[0][j] = b[0][j + 1];
                        b[0][j + 1] = hold;
                        // reorder 'B'
                        holdInt = b_reorder[0][j];
                        b_reorder[0][j] = b_reorder[0][j + 1];
                        b_reorder[0][j + 1] = holdInt;
                    }// end if
                }// end for
            }// end for

            if (flag == -1)
            {// if it has not been tranposed.
             // System.out.println("--flag_m1--");
                return new Object[]
                {
                        new Matrix(b), new Indices(b_reorder)
                };
            }
            else
            { // if it has been tranposed.
              // System.out.println("--flag_not_m1--");
              // B = (new Matrix(b_reorder)).transpose();
                return new Object[]
                {
                        (new Matrix(b)).transpose(), (new Indices(b_reorder)).transpose()
                };
            }
        }

        if (AbsDim == Dimension.ROW)
        {
            for (int j = 0; j < col; j++)
            {
                for (int pass = 1; pass < row; pass++)
                {
                    for (int i = 0; i < (row - 1); i++)
                    {
                        if (a[i][j] > a[i + 1][j])
                        {
                            hold = a[i][j];
                            a[i][j] = a[i + 1][j];
                            a[i + 1][j] = hold;
                            // reorder 'B'
                            holdInt = b_reorder[i][j];
                            b_reorder[i][j] = b_reorder[i + 1][j];
                            b_reorder[i + 1][j] = holdInt;
                        }// end if
                    }// end for
                }// end for
            }// end for
        }// end if
        else if (AbsDim == Dimension.COL)
        {
            for (int i = 0; i < row; i++)
            {
                for (int pass = 1; pass < col; pass++)
                {
                    for (int j = 0; j < (col - 1); j++)
                    {
                        if (a[i][j] > a[i][j + 1])
                        {
                            hold = a[i][j];
                            a[i][j] = a[i][j + 1];
                            a[i][j + 1] = hold;
                            // reorder 'B'
                            holdInt = b_reorder[i][j];
                            b_reorder[i][j] = b_reorder[i][j + 1];
                            b_reorder[i][j + 1] = holdInt;
                        }
                    }// end for
                }// end for
            }// end for
        }// end else if
        else
        {
            throw new IllegalArgumentException("syncSort : Dimension  " + AbsDim.toString() + " , not supported.");
        }
        return new Object[]
        {
                new Matrix(a), new Indices(b_reorder)
        };

    }// end syncSort

    /**
     * 
     * @param A
     * @param B
     * @return
     * 
     * @deprecated Use class <B>SortInd</B> instead.
     */
    public static Object[] syncSort(Indices A, Indices B)
    {
        return syncSort(A, B, Dimension.ROW);
    }

    /**
     * This method sorts 'A' and return a new Matrix object and also 'B' is
     * re-arranged to the sorting order of 'A'
     * 
     * @deprecated Use class <B>SortInd</B> instead.
     */
    public static Object[] syncSort(Indices A, Indices B, Dimension AbsDim)
    {
        int row = A.getRowDimension();
        int col = A.getColumnDimension();

        if ((row != B.getRowDimension()) && (col != B.getColumnDimension()))
        {
            throw new IllegalArgumentException("syncSort:  Indices A and Indices B have incompatible sizes.");
        }

        int flag = -1;

        int hold = 0;
        int holdInt = 0;
        int[][] a = A.copy().getArray();
        int[][] b_reorder = B.copy().getArray();

        if (A.isVector())
        {
            Indices temp = A.copy();// make local copy
            if (!temp.isRowVector())
            {
                temp = A.transpose(); // transform into a row vector.
                b_reorder = B.transpose().getArray();
                flag = 1;
            }
            int c = temp.getColumnDimension();
            if (c == 1)
            {// if only one element , return this element.
             // singleElement.set(0,0,temp.get(0,0));
                return new Object[]
                {
                        temp, B.copy()
                };// singleElement;
            }

            int[][] b = temp.getArray();
            for (int pass = 1; pass < c; pass++)
            {
                for (int j = 0; j < (c - 1); j++)
                {
                    if (b[0][j] > b[0][j + 1])
                    {
                        hold = b[0][j];
                        b[0][j] = b[0][j + 1];
                        b[0][j + 1] = hold;
                        // reorder 'B'
                        holdInt = b_reorder[0][j];
                        b_reorder[0][j] = b_reorder[0][j + 1];
                        b_reorder[0][j + 1] = holdInt;
                    }// end if
                }// end for
            }// end for

            if (flag == -1)
            {// if it has not been tranposed.
             // System.out.println("--flag_m1--");
                return new Object[]
                {
                        new Indices(b), new Indices(b_reorder)
                };
            }
            else
            { // if it has been tranposed.
              // System.out.println("--flag_not_m1--");
              // B = (new Matrix(b_reorder)).transpose();
                return new Object[]
                {
                        (new Indices(b)).transpose(), (new Indices(b_reorder)).transpose()
                };
            }
        }

        if (AbsDim == Dimension.ROW)
        {
            for (int j = 0; j < col; j++)
            {
                for (int pass = 1; pass < row; pass++)
                {
                    for (int i = 0; i < (row - 1); i++)
                    {
                        if (a[i][j] > a[i + 1][j])
                        {
                            hold = a[i][j];
                            a[i][j] = a[i + 1][j];
                            a[i + 1][j] = hold;
                            // reorder 'B'
                            holdInt = b_reorder[i][j];
                            b_reorder[i][j] = b_reorder[i + 1][j];
                            b_reorder[i + 1][j] = holdInt;
                        }// end if
                    }// end for
                }// end for
            }// end for
        }// end if
        else if (AbsDim == Dimension.COL)
        {
            for (int i = 0; i < row; i++)
            {
                for (int pass = 1; pass < col; pass++)
                {
                    for (int j = 0; j < (col - 1); j++)
                    {
                        if (a[i][j] > a[i][j + 1])
                        {
                            hold = a[i][j];
                            a[i][j] = a[i][j + 1];
                            a[i][j + 1] = hold;
                            // reorder 'B'
                            holdInt = b_reorder[i][j];
                            b_reorder[i][j] = b_reorder[i][j + 1];
                            b_reorder[i][j + 1] = holdInt;
                        }
                    }// end for
                }// end for
            }// end for
        }// end else if
        else
        {
            throw new IllegalArgumentException("syncSort : Dimension  " + AbsDim.toString() + " , not supported.");
        }
        return new Object[]
        {
                new Indices(a), new Indices(b_reorder)
        };

    }// end syncSort

    /**
     * Signum function. For each element of X, SIGN(X) returns 1 if the element
     * is greater than zero, 0 if it equals zero and -1 if it is less than zero
     */
    public static Matrix sign(Matrix mat, double Tol)
    {
        // if(tol<0.0){
        // throw new
        // IllegalArgumentException("sign : Double floating parameter \"tol\" ( = "+tol+") , must be greater than zero.");
        // }
        double tol = Math.abs(Tol);
        double[][] C = mat.getArray();
        int rows = mat.getRowDimension();
        int cols = mat.getColumnDimension();
        Matrix ret = new Matrix(rows, cols);
        double[][] R = ret.getArray();

        /*
         * if(tol!=0.0){ for(int i=0; i<rows; i++){ for(int i=0; i<cols; i++){
         * if(Math.abs(C[i][i])>tol) { if(C[i][i]>0.0){ R[i][i] = 1.0; } else
         * if(C[i][i]<0.0){ R[i][i] = -1.0; } } }//end for }//end for return
         * ret; }
         */
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (C[i][j] > 0.0)
                {
                    R[i][j] = 1.0;
                }
                else if (C[i][j] < 0.0)
                {
                    R[i][j] = -1.0;
                }
            }// end for
        }// end for

        return ret;
    }// end method

    /**
     * 
     * @param mat
     *            Matrix
     * @return Matrix
     */
    public static Matrix sign(Matrix mat)
    {
        return sign(mat, 0.0);
    }

    public static Matrix cov(Matrix X, Matrix Y, boolean flag)
    {
        Matrix R = null;
        Matrix x = X.toColVector();
        Matrix y = Y.toColVector();
        int len = x.length();
        if (len != y.length())
        {
            throw new IllegalArgumentException(" cov : Matrix parameters \"X\" and \"Y\" must have the same length .");
        }
        x = x.mergeH(y);

        int m = x.getRowDimension();
        int n = x.getColumnDimension();

        if (m == 1)
        {
            return new Matrix(1, 1);
        }
        else
        {
            Matrix xc = sum(x).arrayRightDivide((double) m).repmat(m, 1).uminus().plus(x);// x
                                                                                          // -
                                                                                          // repmat(sum(x)/m,m,1);
                                                                                          // //
                                                                                          // Remove
                                                                                          // mean
            if (flag)
            {
                R = xc.transpose().times(xc).arrayRightDivide((double) m);
            }
            else
            {
                R = xc.transpose().times(xc).arrayRightDivide((double) m - 1.0);
            }
        }

        return R;
    }// end method

    /**
     * COV Covariance matrix. COV(X), if X is a vector, returns the variance.
     * For matrices, where each row is an observation, and each column a
     * variable, COV(X) is the covariance matrix. DIAG(COV(X)) is a vector of
     * variances for each column, and SQRT(DIAG(COV(X))) is a vector of standard
     * deviations. COV(X,Y), where X and Y are vectors of equal length, is
     * equivalent to COV([X(:) Y(:)]).
     * 
     * COV(X) or COV(X,Y) normalizes by (N-1) where N is the number of
     * observations. This makes COV(X) the best unbiased estimate of the
     * covariance matrix if the observations are from a normal distribution.
     * 
     * COV(X,1) or COV(X,Y,1) normalizes by N and produces the second moment
     * matrix of the observations about their mean. COV(X,Y,0) is the same as
     * COV(X,Y) and COV(X,0) is the same as COV(X).
     * 
     * The mean is removed from each column before calculating the result.
     * 
     * @deprecated Use <B>cov(Matrix X, Matrix Y, boolean flag)</B> instead.
     */
    public static Matrix cov(Matrix X, Matrix Y, int flag)
    {
        Matrix R = null;
        Matrix x = X.toColVector();
        Matrix y = Y.toColVector();
        int len = x.length();
        if (len != y.length())
        {
            throw new IllegalArgumentException(" cov : Matrix parameters \"X\" and \"Y\" must have the same length .");
        }
        x = x.mergeH(y);

        int m = x.getRowDimension();
        int n = x.getColumnDimension();

        if (m == 1)
        {
            return new Matrix(1, 1);
        }
        else
        {
            Matrix xc = sum(x).arrayRightDivide((double) m).repmat(m, 1).uminus().plus(x);// x
                                                                                          // -
                                                                                          // repmat(sum(x)/m,m,1);
                                                                                          // //
                                                                                          // Remove
                                                                                          // mean
            if (flag == 1)
            {
                R = xc.transpose().times(xc).arrayRightDivide((double) m);
            }
            else
            {
                R = xc.transpose().times(xc).arrayRightDivide((double) m - 1.0);
            }
        }

        return R;
    }// end method

    /**
     * 
     * @param X
     * @param Y
     * @return
     */
    public static Matrix cov(Matrix X, Matrix Y)
    {
        return cov(X, Y, false);
    }

    public static Matrix cov(Matrix X, boolean flag)
    {
        Matrix R = null;
        Matrix x = null;

        if (X.isVector())
        {
            x = X.toColVector();
        }
        else
        {
            x = X.copy();
        }

        int m = x.getRowDimension();
        int n = x.getColumnDimension();

        if (m == 1)
        {
            return new Matrix(1, 1);
        }
        else
        {
            Matrix xc = sum(x).arrayRightDivide((double) m).repmat(m, 1).uminus().plus(x);// x
                                                                                          // -
                                                                                          // repmat(sum(x)/m,m,1);
                                                                                          // //
                                                                                          // Remove
                                                                                          // mean
            if (flag)
            {
                R = xc.transpose().times(xc).arrayRightDivide((double) m);
            }
            else
            {
                R = xc.transpose().times(xc).arrayRightDivide((double) m - 1.0);
            }
        }

        return R;
    }// end method

    /**
     * COV Covariance matrix. COV(X), if X is a vector, returns the variance.
     * For matrices, where each row is an observation, and each column a
     * variable, COV(X) is the covariance matrix. DIAG(COV(X)) is a vector of
     * variances for each column, and SQRT(DIAG(COV(X))) is a vector of standard
     * deviations. COV(X,Y), where X and Y are vectors of equal length, is
     * equivalent to COV([X(:) Y(:)]).
     * 
     * COV(X) or COV(X,Y) normalizes by (N-1) where N is the number of
     * observations. This makes COV(X) the best unbiased estimate of the
     * covariance matrix if the observations are from a normal distribution.
     * 
     * COV(X,1) or COV(X,Y,1) normalizes by N and produces the second moment
     * matrix of the observations about their mean. COV(X,Y,0) is the same as
     * COV(X,Y) and COV(X,0) is the same as COV(X).
     * 
     * The mean is removed from each column before calculating the result.
     * 
     * @deprecated Use <B>cov(Matrix A, boolean flag)</B> instead.
     */
    public static Matrix cov(Matrix X, int flag)
    {
        Matrix R = null;
        Matrix x = null;

        if (X.isVector())
        {
            x = X.toColVector();
        }
        else
        {
            x = X.copy();
        }

        int m = x.getRowDimension();
        int n = x.getColumnDimension();

        if (m == 1)
        {
            return new Matrix(1, 1);
        }
        else
        {
            Matrix xc = sum(x).arrayRightDivide((double) m).repmat(m, 1).uminus().plus(x);// x
                                                                                          // -
                                                                                          // repmat(sum(x)/m,m,1);
                                                                                          // //
                                                                                          // Remove
                                                                                          // mean
            if (flag == 1)
            {
                R = xc.transpose().times(xc).arrayRightDivide((double) m);
            }
            else
            {
                R = xc.transpose().times(xc).arrayRightDivide((double) m - 1.0);
            }
        }

        return R;
    }// end method

    /**
     * 
     * @param X
     * @return
     */
    public static Matrix cov(Matrix X)
    {
        return cov(X, false);
    }

    /**
     * CORRCOEF Correlation coefficients. CORRCOEF(X) is a matrix of correlation
     * coefficients formed from array X whose each row is an observation, and
     * each column is a variable. CORRCOEF(X,Y), where X and Y are column
     * vectors is the same as CORRCOEF([X Y]).
     * 
     * If C is the covariance matrix, C = COV(X), then CORRCOEF(X) is the matrix
     * whose (i,i)'th element is
     * 
     * C(i,i)/SQRT(C(i,i)*C(i,i)).
     */
    public static Matrix corrcoef(Matrix x, Matrix y)
    {
        Matrix c = cov(x, y); // System.out.println("*** c ***"); c.print(3,4);
        Matrix d = c.diag();
        d = d.times(d.transpose());
        Matrix xy = c.arrayRightDivide(JElfun.sqrt(d));
        return xy;
    }

    /**
     * 
     * @param x
     * @return
     */
    public static Matrix corrcoef(Matrix x)
    {
        Matrix c = cov(x); // System.out.println("*** c ***"); c.print(3,4);
        Matrix d = c.diag();
        d = d.times(d.transpose());
        Matrix xy = c.arrayRightDivide(JElfun.sqrt(d));// c/sqrt(d);
        return xy;
    }

    /**
     * 
     * @param A
     * @return
     */
    public static Matrix prod(Matrix A)
    {
        return prod(A, Dimension.ROW);
    }

    /**
     * 
     * @param A
     * @param Dim
     * @return
     */
    public static Matrix prod(Matrix A, Dimension Dim)
    {

        Matrix R = null;
        double val = 1.0;

        if (A.isVector())
        {
            if (A.isRowVector())
            {
                for (int j = 0; j < A.length(); j++)
                {
                    val = val * A.get(0, j);
                    if (val == 0.0)
                    {
                        return new Matrix(1, 1);
                    }
                }
            }
            else
            {
                for (int i = 0; i < A.length(); i++)
                {
                    val = val * A.get(i, 0);
                    if (val == 0.0)
                    {
                        return new Matrix(1, 1);
                    }
                }
            }
            return new Matrix(1, 1, val);
        }

        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        if (Dim == Dimension.ROW)
        {
            // int col = A.getColumnDimension();
            R = new Matrix(1, n);
            for (int j = 0; j < n; j++)
            {
                val = 1.0;
                for (int i = 0; i < m; i++)
                {
                    val = val * A.get(i, j);
                }
                R.set(0, j, val);
            }
        }
        else if (Dim == Dimension.COL)
        {
            // int row = A.getRowDimension();
            R = new Matrix(m, 1);
            for (int i = 0; i < m; i++)
            {
                val = 1.0;
                for (int j = 0; j < n; j++)
                {
                    val = val * A.get(i, j);
                }
                R.set(i, 0, val);
            }
        }
        else
        {
            throw new IllegalArgumentException("prod : Dimension  " + Dim.toString() + " , not supported.");
        }
        return R;
    }

    /**
     * 
     * @param matrix
     * @return
     */
    public static Matrix diff(Matrix A)
    {
        DifferenceMat diff = new DifferenceMat(A);
        return (Matrix) diff.getDifferenceObject();
    }

    /**
     * Difference and approximate derivative. This overloading method is the
     * approximate difference along the column-dimension. eg:
     * JDatafun.diff(A,1,Maths.DIM_COL)
     * 
     * @param matrix
     *            The Matrix input matrix data set.
     * @param order
     *            Integer for the derivative order.
     * @return Matrix The Matrix diff matrix of the <tt>data</tt>.
     */
    public static Matrix diff(Matrix A, int order)
    {
        DifferenceMat diff = new DifferenceMat(A, order);
        return (Matrix) diff.getDifferenceObject();
    }

    /**
     * Difference and approximate derivative. JDatafun.diff(A), for a vector A,
     * is [A(2)-A(1), A(3)-A(2), ... ,A(n)-A(n-1)], eg:
     * 
     * For vectors, If A = ( 36, 43, 25, 37 ) , JDatafun.diff(A,1,Maths.DIM_COL)
     * = null, returns null;
     * 
     * //First order JDatafun.diff(A,1,Maths.DIM_ROW) = ( 7, -18, 12 ), //Second
     * order JDatafun.diff(A,2,Maths.DIM_ROW) = ( -25, 30 ), //Third order
     * JDatafun.diff(A,3,Maths.DIM_ROW) = ( 55 ), //Forth order
     * JDatafun.diff(A,4,Maths.DIM_ROW) = null, returns NULL.
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * //--- Along the row dimension --- //First order
     * JDatafun.diff(A,1,Maths.DIM_ROW) = ( -14, 8, -9, 3 ) | 7, -3, 16, 4 |
     * //Second order JDatafun.diff(A,2,Maths.DIM_ROW) = ( 21, -11, 25, 1 )
     * 
     * * //Third order JDatafun.diff(A,2,Maths.DIM_ROW) = null , returns a null
     * ;
     * 
     * //--- Along the column dimension --- //First order ( -9, -1, 0 )
     * JDatafun.diff(A,1,Maths.DIM_COL) = | 13, -18, 12 | ( 3, 1, 0 )
     * 
     * //Second order ( 8, 1 ) JDatafun.diff(A,2,Maths.DIM_COL) = | -31, 30 | (
     * -2, -1 )
     * 
     * //Third order ( -7 ) JDatafun.diff(A,2,Maths.DIM_COL) = | 61 | ( 1 )
     * //Fourth order returns null.
     * 
     * @param matrix
     *            The Matrix input matrix data set.
     * @param order
     *            Integer for the derivative order.
     * @param Dim
     *            Integer for the dimension to operate.
     * @return Matrix The Matrix diff matrix of the <tt>data</tt>.
     */
    public static Matrix diff(Matrix A, int order, Dimension Dim)
    {
        DifferenceMat diff = new DifferenceMat(A, order, Dim);
        return (Matrix) diff.getDifferenceObject();
    }

    /**
     * 
     * @param matrix
     * @return
     */
    public static Indices diff(Indices A)
    {
        DifferenceInd diff = new DifferenceInd(A);
        return (Indices) diff.getDifferenceObject();
    }

    /**
     * Difference and approximate derivative. This overloading method is the
     * approximate difference along the column-dimension. eg:
     * JDatafun.diff(A,1,Maths.DIM_COL)
     * 
     * @param matrix
     *            The Matrix input matrix data set.
     * @param order
     *            Integer for the derivative order.
     * @return Matrix The Matrix diff matrix of the <tt>data</tt>.
     */
    public static Indices diff(Indices A, int order)
    {
        DifferenceInd diff = new DifferenceInd(A, order);
        return (Indices) diff.getDifferenceObject();
    }

    /**
     * Difference and approximate derivative. JDatafun.diff(A), for a vector A,
     * is [A(2)-A(1), A(3)-A(2), ... ,A(n)-A(n-1)], eg:
     * 
     * For vectors, If A = ( 36, 43, 25, 37 ) , JDatafun.diff(A,1,Maths.DIM_COL)
     * = null, returns null;
     * 
     * //First order JDatafun.diff(A,1,Maths.DIM_ROW) = ( 7, -18, 12 ), //Second
     * order JDatafun.diff(A,2,Maths.DIM_ROW) = ( -25, 30 ), //Third order
     * JDatafun.diff(A,3,Maths.DIM_ROW) = ( 55 ), //Forth order
     * JDatafun.diff(A,4,Maths.DIM_ROW) = null, returns NULL.
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * //--- Along the row dimension --- //First order
     * JDatafun.diff(A,1,Maths.DIM_ROW) = ( -14, 8, -9, 3 ) | 7, -3, 16, 4 |
     * //Second order JDatafun.diff(A,2,Maths.DIM_ROW) = ( 21, -11, 25, 1 )
     * 
     * * //Third order JDatafun.diff(A,2,Maths.DIM_ROW) = null , returns a null
     * ;
     * 
     * //--- Along the column dimension --- //First order ( -9, -1, 0 )
     * JDatafun.diff(A,1,Maths.DIM_COL) = | 13, -18, 12 | ( 3, 1, 0 )
     * 
     * //Second order ( 8, 1 ) JDatafun.diff(A,2,Maths.DIM_COL) = | -31, 30 | (
     * -2, -1 )
     * 
     * //Third order ( -7 ) JDatafun.diff(A,2,Maths.DIM_COL) = | 61 | ( 1 )
     * //Fourth order returns null.
     * 
     * @param matrix
     *            The Matrix input matrix data set.
     * @param order
     *            Integer for the derivative order.
     * @param Dim
     *            Integer for the dimension to operate.
     * @return Matrix The Matrix diff matrix of the <tt>data</tt>.
     */
    public static Indices diff(Indices A, int order, Dimension Dim)
    {
        DifferenceInd diff = new DifferenceInd(A, order, Dim);
        return (Indices) diff.getDifferenceObject();
    }

    /**
     * 
     * @param n
     * @return
     */
    public static Indices randperm(int n)
    {
        if (n < 1)
        {
            throw new IllegalArgumentException("randperm - Parameter 'n' must be at least one.");
        }
        Matrix R = Matrix.random(1, n);
        // Object[] obj = JDatafun.sortInd(R); //==> original line (commented
        // out on 15/01/11)

        QuickSort sort = new QuickSortMat(R, null, true);
        return sort.getIndices();
    }

    /**
     * 
     * @param m
     * @param n
     * @param p
     * @return
     */
    public static Matrix3D randn(int m, int n, int p)
    {
        Matrix3D randMat = new Matrix3D(m, n, p);
        double val = 0.0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                for (int k = 0; k < p; k++)
                {
                    val = MathUtil.randn();
                    randMat.set(i, j, k, val);
                }
            }
        }
        return randMat;
    }

    /**
     * 
     * @param m
     * @param n
     * @return
     */
    public static Matrix randn(int m, int n)
    {
        Matrix randMat = new Matrix(m, n);
        double val = 0.0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                val = MathUtil.randn();
                randMat.set(i, j, val);
            }
        }
        return randMat;
    }

    /**
     * 
     * @param X
     * @param Edges
     * @return
     */
    public static Object[] histc(Matrix X, Matrix Edges)
    {
        return histc(X, Edges, Dimension.ROW);
    }

    /**
     * 
     * @param X
     * @param Edges
     * @param dim
     * @return
     */
    public static Object[] histc(Matrix X, Matrix Edges, Dimension dim)
    {

        return null;
    }

    /**
     * 
     * @param y
     * @param x
     * @return
     */
    public static Object[] hist(Matrix y, Matrix x)
    {
        // Method is yet to be tested.

        // x must also be checked if it is unique
        if (x.isVector() == false)
        {
            throw new IllegalArgumentException("hist - Parameter 'x' must be a vector.");
        }

        double miny = 0.0;
        double maxy = 0.0;
        double val = 0.0;
        Matrix R = null;
        int[] linsp = null;

        Matrix xx = x.toColVector().transpose();// x(:)';
        miny = min(min(y)).get(0, 0);
        maxy = max(max(y)).get(0, 0);
        Matrix binwidth = diff(xx).mergeH(new Matrix(1, 1));
        val = xx.get(0, 0) - binwidth.get(0, 0) / 2.0;
        xx = (new Matrix(1, 1, val)).mergeH(xx.plus(binwidth.arrayRightDivide(2.0)));// xx
                                                                                     // =
                                                                                     // [xx(1)-binwidth(1)/2
                                                                                     // xx+binwidth/2];
        val = Math.min(xx.get(0, 0), miny);
        xx.set(0, 0, val);
        val = Math.max(xx.end(), maxy);
        xx.set(0, xx.length() - 1, val);

        Matrix bins = xx.plus(MathUtil.EPS);
        Matrix temp = (new Matrix(1, 1, Double.NEGATIVE_INFINITY)).mergeH(bins);

        Matrix nn = (Matrix) histc(y, temp)[0];
        int lenN = nn.length();

        // Combine first bin with 2nd bin and last bin with next to last bin
        temp = nn.getRowAt(1).plus(nn.getRowAt(0));// nn(2,:) = nn(2,:)+nn(1,:);
        nn.setRowAt(1, temp);
        temp = nn.getRowAt(lenN - 2).plus(nn.getRowAt(lenN));// nn(end-1,:) =
                                                             // nn(end-1,:)+nn(end,:);
        nn.setRowAt(lenN - 2, temp);
        linsp = Matrix.intLinspace(1, lenN - 2);
        nn = nn.getRows(linsp);// nn = nn(2:end-1,:);

        Matrix no = null;
        Matrix xo = null;

        if (y.isVector())
        { // Return row vectors if possible.
            no = nn.transpose();// nn';
            xo = x;
        }
        else
        {
            no = nn;
            xo = x.transpose();
        }

        return new Object[]
        {
                no, xo
        };
    }

    /**
     * 
     * @param y
     * @return
     */
    public static Object[] hist(Matrix y)
    {
        return hist(y, 10);
    }

    /**
     * 
     * @param y
     * @param n
     * @return
     */
    public static Object[] hist(Matrix y, int n)
    {
        Matrix R = null;
        double miny = 0.0;
        double maxy = 0.0;
        double binNum = (double) n;
        Matrix x = null;

        if (y.isVector())
        { // y is a vector
            R = new Matrix(1, n);
            miny = min(y).get(0, 0);
            maxy = max(y).get(0, 0);
            if (miny == maxy)
            {
                miny = miny - Math.floor(binNum / 2.0) - 0.5;
                maxy = maxy + Math.ceil(binNum / 2.0) - 0.5;
            }
            double binwidth = (maxy - miny) / 10.0;
            Matrix xx = Matrix.linspace(0.0, (double) n, n + 1).arrayTimes(binwidth).plus(miny);// xx
                                                                                                // =
                                                                                                // miny
                                                                                                // +
                                                                                                // binwidth*(0:x);
            xx.set(0, xx.length() - 1, maxy);// xx(length(xx)) = maxy;
            int[] linsp = Matrix.intLinspace(0, xx.length() - 2);
            x = xx.getMatrix(0, 0, linsp).plus(binwidth / 2.0);// x =
                                                               // xx(1:length(xx)-1)
                                                               // + binwidth/2;
        }
        else
        { // y is a matrix
            R = new Matrix(n, y.getColumnDimension());
        }

        return new Object[]
        {
            R
        };
    }

    /**
     * This method over-loading is the same as : Matrix.max(X,1)
     * 
     * @param X
     *            Matrix matrix
     * @return Matrix Matrix matrix
     */
    public static Indices max(Indices X)
    {
        return Maximum.max(X);
    }

    /**
     * For vectors, Matrix.max(X,1) is the smallest element in X, eg:
     * 
     * If X = (9,-8,3,-7,4,10) , JDatafun.max(X) returns a Matrix matrix with 10
     * as the only element.
     * 
     * For matrices, eg:
     * 
     * (9 , -7, 1, 6) If X = |10, 9, 5, 3| (0 , 6, 8, 2)
     * 
     * JDatafun.max(X,Dimension.ROW) = (10, 9, 8, 6) , maximum of each column.
     * 
     * ( 9 ) JDatafun.max(X,Dimension.COL) = | 10 | ,returns a Matrix column
     * vector matrix with a maximum of each row. ( 8 )
     * 
     * @param X
     *            Matrix matrix
     * @param Dim
     *            integer constant specifying the dimension to operate.
     * @return Matrix Matrix matrix
     */
    public static Indices max(Indices X, Dimension Dim)
    {
        return Maximum.max(X, Dim);
    }

    // ////////////////////////////////////////////////////////////
    public static Indices max(Indices X, Indices Y)
    {
        int x_rows = X.getRowDimension();
        int x_cols = X.getColumnDimension();
        int y_rows = Y.getRowDimension();
        int y_cols = Y.getColumnDimension();
        Indices result = null;
        if (x_rows != y_rows || x_cols != y_cols)
        {
            throw new IllegalArgumentException("max : Incompatible indices dimensions.");
        }
        int val = 0;
        result = new Indices(x_rows, x_cols);
        for (int i = 0; i < x_rows; i++)
        {
            for (int j = 0; j < x_cols; j++)
            {
                val = X.get(i, j) > Y.get(i, j) ? X.get(i, j) : Y.get(i, j);
                result.set(i, j, val);
            }
        }
        return result;
    }

    /**
     * For vectors, Matrix.max(X,1.5) is the smallest element in comparison of
     * each entries of X to 2.4 eg:
     * 
     * If X = (9,-8,3,-7,4, 1) , then Matrix.max(X,1.5) = (9 , 1.5, 3, 1.5, 4,
     * 1.5)
     * 
     * For matrices, eg:
     * 
     * (9 , -7, 1, 6) If X = |10, 9, -5, 3| , (0 , -6, 8, 2)
     * 
     * ( 9 , 1.5, 1.5, 6 ) Matrix.max(X,1.5) = | 10 , 9, 1.5, 3 | ( 1.5, 1.5, 8,
     * 2 )
     * 
     * @param X
     *            Matrix matrix
     * @param val
     *            double value
     * @return Matrix B
     */
    public static Indices max(Indices B, int val)
    {
        int x_rows = B.getRowDimension();
        int x_cols = B.getColumnDimension();
        Indices X = new Indices(x_rows, x_cols);
        int[][] C = X.getArray();
        int[][] M = B.getArray();
        for (int i = 0; i < x_rows; i++)
        {
            for (int j = 0; j < x_cols; j++)
            {
                C[i][j] = M[i][j] > val ? M[i][j] : val;
            }
        }
        return X;
    }

    /**
     * overload
     * 
     * @param X
     *            Matrix matrix
     * @param val
     *            double value
     * @return Matrix B
     */
    public static Indices max(int val, Indices B)
    {
        return max(B, val);
    }

    // ////////////////////////////////////////////////////////////
    /**
     * The sum of the elements of matrix, along columns of every rows.
     * 
     * If A = (148 131 175) , then JDatafun.sum(A) , returns a single-element
     * matrix of 454
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * JDatafun.sum(A) = (36, 43, 25, 37)
     * 
     * 
     * @return Matrix The Matrix summed matrix of <tt>data</tt>.
     * @param indices
     */
    public static Indices sum(Indices indices)
    {
        return (Indices) (new SumInd(indices)).getSumObject();// JDatafun.sum(indices,
                                                              // Dimension.ROW);
    }

    /**
     * The sum of the elements of matrix, along the specified dimension , Dim.
     * 
     * For vectors, eg:
     * 
     * If A = (148 131 175) , then JDatafun.sum(A,Dimension.ROW) , returns a
     * single-element matrix of 454
     * 
     * For matrices, ( 19, 10, 9, 9 ) If A = | 5, 18, 0, 12 | ( 12, 15, 16, 16 )
     * 
     * JDatafun.sum(A,Dimension.ROW) = (36, 43, 25, 37)
     * 
     * ( 47 ) JDatafun.sum(A,Dimension.COL) = | 35 | ( 59 )
     * 
     * 
     * @return Matrix The Matrix summed matrix of <tt>data</tt>.
     * @param matrix
     * @param Dim
     *            Integer for the dimension to be summed.
     */
    public static Indices sum(Indices matrix, Dimension Dim)
    {
        return (Indices) (new SumInd(matrix, Dim)).getSumObject();
    }

    /**
     * This method over-load is exactly the same as,
     * JDatafun.min(X,Dimension.ROW)
     * 
     * @param X
     *            Matrix matrix
     * @return Matrix Matrix matrix
     */
    public static Indices min(Indices X)
    {
        return Minimum.min(X);
    }

    /**
     * For vectors, Matrix.min(X,1) is the smallest element in X, eg:
     * 
     * If X = (9,-8,3,-7,4,10) , JDatafun.min(X) returns a Matrix matrix with -8
     * as the only element.
     * 
     * For matrices, eg:
     * 
     * (9 , -7, 1, 6) If X = |10, 9, 5, 3| , then JDatafun.min(X,Dimension.ROW)
     * = (0, -7, 1, 2) , minimum of each column. (0 , 6, 8, 2)
     * 
     * @param X
     *            Matrix matrix
     * @param Dim
     *            integer constant specifying the dimension to operate.
     * @return Matrix Matrix matrix
     */
    public static Indices min(Indices X, Dimension Dim)
    {
        return Minimum.min(X, Dim);
    }

    /**
     * 
     * @param X
     * @param Y
     * @return
     */
    public static Indices min(Indices X, Indices Y)
    {
        int x_rows = X.getRowDimension();
        int x_cols = X.getColumnDimension();
        int y_rows = Y.getRowDimension();
        int y_cols = Y.getColumnDimension();
        Indices result = null;
        if (x_rows != y_rows || x_cols != y_cols)
        {
            throw new IllegalArgumentException("min : Incompatible matrix dimensions.");
        }
        result = new Indices(x_rows, x_cols);
        int val = 0;
        for (int i = 0; i < x_rows; i++)
        {
            for (int j = 0; j < x_cols; j++)
            {
                val = X.get(i, j) < Y.get(i, j) ? X.get(i, j) : Y.get(i, j);
                result.set(i, j, val);
            }
        }
        return result;
    }

    /**
     * For vectors, Matrix.min(X,2.4) is the smallest element in comparison of
     * each entries of X to 2.4 eg:
     * 
     * If X = (9,-8,3,-7,4, 1) , then Matrix.min(X,2.4) = (2.4 , -8, 2.4, -7,
     * 2.4, 1)
     * 
     * For matrices, eg:
     * 
     * (9 , -7, 1, 6) If X = |10, 9, -5, 3| , (0 , -6, 8, 2)
     * 
     * ( 2.4 , -7 , 1, 2.4 ) Matrix.min(X,2.4) = | 2.4 , 2.4, -5, 2.4 | ( 0 ,
     * -6, 2.4, 2 )
     * 
     * @param X
     *            Matrix matrix
     * @param val
     *            double value
     * @return Matrix Matrix matrix
     */
    public static Indices min(Indices X, Integer val)
    {
        int x_rows = X.getRowDimension();
        int x_cols = X.getColumnDimension();
        Indices result = null;
        result = new Indices(x_rows, x_cols);
        int t = 0;
        int value = val.intValue();
        for (int i = 0; i < x_rows; i++)
        {
            for (int j = 0; j < x_cols; j++)
            {
                t = X.get(i, j) < value ? X.get(i, j) : value;
                result.set(i, j, t);
            }
        }
        return result;
    }

    /**
     * 
     * @param X
     * @return
     */
    public static Matrix center(Matrix X)
    {
        // [n p] = size(X);
        int n = X.getRowDimension();
        int p = X.getColumnDimension();
        Matrix centX = X.minus(Matrix.ones(n, 1).times(X));// X -
                                                           // ones(n,1)*mean(X);
        return centX;
    }

    /**
     * 
     * @param X
     * @return
     */
    public static Matrix center(Indices X)
    {
        // [n p] = size(X);
        int n = X.getRowDimension();
        int p = X.getColumnDimension();
        Matrix matX = Matrix.indicesToMatrix(X);
        Matrix centX = matX.minus(Matrix.ones(n, 1).times(X));// X -
                                                              // ones(n,1)*mean(X);
        return centX;
    }

    /**
     * 
     * @param n
     * @return
     */
    public static Matrix magic(int n)
    {

        double[][] M = new double[n][n];

        // Odd order

        if ((n % 2) == 1)
        {

            int a = (n + 1) / 2;

            int b = (n + 1);

            for (int j = 0; j < n; j++)
            {

                for (int i = 0; i < n; i++)
                {

                    M[i][j] = n * ((i + j + a) % n) + ((i + 2 * j + b) % n) + 1;

                }

            }

            // Doubly Even Order

        }
        else if ((n % 4) == 0)
        {

            for (int j = 0; j < n; j++)
            {

                for (int i = 0; i < n; i++)
                {

                    if (((i + 1) / 2) % 2 == ((j + 1) / 2) % 2)
                    {

                        M[i][j] = n * n - n * i - j;

                    }
                    else
                    {

                        M[i][j] = n * i + j + 1;

                    }

                }

            }

            // Singly Even Order

        }
        else
        {

            int p = n / 2;

            int k = (n - 2) / 4;

            Matrix A = magic(p);

            for (int j = 0; j < p; j++)
            {

                for (int i = 0; i < p; i++)
                {

                    double aij = A.get(i, j);

                    M[i][j] = aij;

                    M[i][j + p] = aij + 2 * p * p;

                    M[i + p][j] = aij + 3 * p * p;

                    M[i + p][j + p] = aij + p * p;

                }

            }

            for (int i = 0; i < p; i++)
            {

                for (int j = 0; j < k; j++)
                {

                    double t = M[i][j];
                    M[i][j] = M[i + p][j];
                    M[i + p][j] = t;

                }

                for (int j = n - k + 1; j < n; j++)
                {

                    double t = M[i][j];
                    M[i][j] = M[i + p][j];
                    M[i + p][j] = t;

                }

            }

            double t = M[k][0];
            M[k][0] = M[k + p][0];
            M[k + p][0] = t;

            t = M[k][k];
            M[k][k] = M[k + p][k];
            M[k + p][k] = t;

        }

        return new Matrix(M);

    }

    public static Matrix wnanmean(Matrix X, Matrix W)
    {
        /*
         * WNANMEAN Weighted mean values ignoring NaN's. M=WNANMEAN(X,W) returns
         * mean for matrix X along 1st dimension weighted by vector W.
         */

        Indices isnanW = W.isnan();
        FindInd find = isnanW.findIJ();
        int[] ind = null;
        if (!find.isNull())
        {
            // W(isnan(W)) = 0;
            ind = find.getIndex();
            W.setElements(ind, 0.0);
        }

        // W = W(:);
        W = W.toColVector();

        int n = X.getColumnDimension();
        Matrix xw = W.repmat(1, n);

        // X = bsxfun(@times,X,W);
        X = X.arrayTimes(xw);
        Indices tfnan = X.isnan();
        find = tfnan.findIJ();
        if (!find.isNull())
        {
            // X(tfnan) = 0;
            ind = find.getIndex();
            X.setElements(ind, 0.0);
        }
        Matrix tfnanNot = Matrix.indicesToMatrix(tfnan.NOT());

        Matrix Wcol = tfnanNot.arrayTimes(xw);// Wcol =
                                              // sum(bsxfun(@times,~tfnan,W),1);
        Wcol = JDatafun.sum(Wcol, Dimension.ROW);
        Matrix M = JDatafun.sum(X, Dimension.ROW).arrayRightDivide(Wcol);

        return M;
    }

    public static Object[] removenan(Object... varargin)
    {
        int badin = 0;
        Indices wasnan = null;
        int n = -1;

        // Find NaN, check length, and store outputs temporarily
        // varargout = cell(nargout,1);
        int nargin = varargin.length;
        Object[] varargout = new Object[nargin];

        Object[] output = new Object[2 + nargin];

        for (int j = 0; j < nargin; j++)
        {
            Matrix y = (Matrix) varargin[j];
            if (y.getRowDimension() == 1 && (n != 1))
            {
                y = y.transpose();
            }

            int ny = y.getRowDimension();
            if (n == -1)
            {
                n = ny;
            }
            else if (n != ny && ny != 0)
            {
                if (badin == 0)
                {
                    badin = j + 1;
                }
            }

            varargout[j] = y;

            if (badin == 0 && ny > 0)
            {
                // wasnan = wasnan | any(isnan(y),2);
                Indices isnanYany = y.isnan().ANY(Dimension.COL);
                if (wasnan == null)
                {
                    wasnan = isnanYany;
                }
                else
                {
                    wasnan = wasnan.OR(isnanYany);
                }
            }
        }

        if (badin > 0)
        {
            // badin,wasnan,varargout
            output[0] = badin;
            output[1] = wasnan;
            for (int k = 2; k < (2 + nargin); k++)
            {
                output[k] = varargout[k - 2];
            }
            return output;
        }

        // Fix outputs
        if (wasnan.anyBoolean())
        {
            Indices t = wasnan.NOT();
            for (int j = 0; j < nargin; j++)
            {
                Matrix y = (Matrix) varargout[j];
                if (y.length() > 0)
                {
                    FindInd find = t.findIJ();
                    if (!find.isNull())
                    {
                        Matrix yt = y.getRows(find.getIndex());
                        varargout[j] = yt;// varargout{j} = y(t,:);
                    }
                }
            }
        }

        output[0] = badin;
        output[1] = wasnan;
        for (int k = 2; k < (2 + nargin); k++)
        {
            output[k] = varargout[k - 2];
        }

        return output;
    }

    public static double sumNanVec(Matrix vec)
    {
        if (vec == null || vec.isNull())
        {
            throw new ConditionalException("sumNanVec : Input matrix vector must be non-null or non-empty.");
        }
        if (!vec.isVector())
        {
            throw new ConditionalException("sumNanVec : Input argument must be a vector and not a matrix.");
        }

        int len = vec.length();
        Indices fin = vec.isfinite();
        FindInd find = fin.findIJ();
        if (find.isNull())
        {
            return Double.NaN;
        }

        Matrix finiteMat = null;

        if (find.getIndex().length == len)
        {
            finiteMat = vec;
        }
        else
        {
            int[] ind = find.getIndex();
            finiteMat = vec.getEls(ind);
        }

        len = finiteMat.length();
        double sum = 0.0;
        if (finiteMat.isColVector())
        {
            for (int i = 0; i < len; i++)
            {
                sum += finiteMat.get(i, 0);
            }
        }
        else
        {
            for (int i = 0; i < len; i++)
            {
                sum += finiteMat.get(0, i);
            }
        }

        // Indices inf = vec.isinf();

        return sum;
    }

    static Matrix tfidfTest()
    {
        double[][] xx =
        {
                {
                        0, 10, 1, 8, 2, 4, 5, 9, 1, 3, 6, 9
                },
                {
                        7, 1, 5, 6, 3, 9, 7, 1, 4, 5, 6, 5
                },
                {
                        6, 7, 6, 1, 1, 6, 5, 9, 2, 8, 10, 6
                },
                {
                        3, 7, 8, 0, 4, 4, 0, 6, 1, 8, 9, 4
                },
                {
                        6, 2, 1, 5, 5, 4, 1, 6, 2, 7, 7, 3
                },
                {
                        1, 6, 4, 9, 9, 4, 4, 9, 6, 6, 3, 2
                },
                {
                        4, 7, 1, 5, 7, 9, 3, 8, 2, 9, 3, 2
                },
                {
                        1, 7, 9, 5, 8, 9, 3, 0, 3, 5, 9, 1
                },
                {
                        6, 3, 10, 9, 4, 5, 5, 3, 4, 1, 3, 7
                },
                {
                        3, 6, 5, 9, 1, 8, 8, 9, 4, 8, 2, 0
                }
        };

        return new Matrix(xx);
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args)
    {

        Matrix X = tfidfTest();

        X = tfidf(X);
        X.printInLabel("X");

        /*
         * double[] aa = { Double.NaN, 3, -5, 4, Double.NaN, 9, Double.NaN };
         * 
         * double[] a = { Double.NaN, Double.NaN, Double.NaN };
         * 
         * Matrix A = new Matrix(a); double sum = sumNanVec(A.transpose());
         * System.out.println("\n sum = " + sum);
         */

        /*
         * int numRows = 5; Matrix A = Matrix.random(numRows, numRows); Matrix B
         * = Matrix.random(3, numRows);
         * 
         * Matrix max = max(A, B);
         * 
         * 
         * double[][] a = { {14, 13, 12, Double.NaN, 14}, {3, Double.NaN, 7, 11,
         * 14}, {9, 7, 9, 3, 6}, {7, 0, 12, 6, 13} };
         * 
         * Matrix A = new Matrix(a); System.out.println("\n------- A --------");
         * A.print(4,0);
         * 
         * Matrix sum = JDatafun.sum(A);
         * System.out.println("\n------- sumA --------"); sum.print(4,0);
         * 
         * 
         * 
         * Matrix x = new Matrix( new double[][]{{2, 5, 3, 6, 4, 5}} );
         * System.out.println("\n------- x --------"); x.print(4,0);
         * 
         * 
         * Matrix xcump = cumprod(x);
         * System.out.println("\n------- xcump --------"); xcump.print(4,0);
         * 
         * double[][] mat = {{ 1, 2, Double.NaN, 5}, { Double.NEGATIVE_INFINITY,
         * 5, 5, Double.NEGATIVE_INFINITY}, { 5, 1, 4, 6}};
         * 
         * Matrix rn = new Matrix(mat);
         * 
         * 
         * System.out.println("\n------- rn --------"); rn.print(4,0);
         * 
         * 
         * Object[] obj = JDatafun.sortInd(rn); Matrix R = (Matrix)obj[0];
         * Indices I = (Indices)obj[1];
         * 
         * System.out.println("\n------- R --------"); R.print(4,0);
         * 
         * System.out.println("\n------- I --------");
         * Matrix.indicesToMatrix(I).print(4,0);
         */

    }
}// ------------------------- End Class Definition -------------------

