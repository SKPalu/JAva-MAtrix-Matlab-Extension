/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.MatrixUtil;

/**
 * 
 * @author Feynman Perceptrons
 */
public class InfiniteImpulseFilter
{

    private Matrix filteredData;

    public InfiniteImpulseFilter(double B, Matrix A, Matrix X)
    {
        filteredData = filter(new Matrix(1, 1, B), A, X);
    }

    public InfiniteImpulseFilter(Matrix B, Matrix A, Matrix X)
    {
        filteredData = filter(B, A, X);
    }

    private Matrix filter(Matrix vectorB, Matrix vectorA, Matrix vectorData)
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

        // vectorB is normalized
        Matrix B = null;
        if (vectorB.isColVector())
        {
            B = vectorB.toRowVector().arrayRightDivide(aOne);
        }
        else
        {
            B = vectorB.arrayRightDivide(aOne);
        }
        Matrix A = null;
        if (vectorA.isColVector())
        {
            A = normalizedVectorA.toRowVector();
        }
        else
        {
            A = normalizedVectorA;
        }
        boolean isCol = false;
        Matrix X = null;
        if (vectorData.isColVector())
        {
            X = vectorData.toRowVector();
            isCol = true;
        }
        else
        {
            X = vectorData;
        }

        int lenB = B.length();
        int lenA = A.length();
        int lenX = X.length();
        double valB = 0.0, valA = 0.0;
        double coeffB = 0.0, coeffA = 0.0;
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

        if (isCol)
        {
            Y = Y.toColVector();
        }

        return Y;
    }// end method

    public Matrix getFilteredData()
    {
        return filteredData;
    }

    public static void main(String[] args)
    {
        String filenameFullPath = "C:\\JavaProjects\\JNumeric\\TestData\\SysIdTestData\\";

        Matrix Ebar = null;//MatrixUtil.readXlsData(filenameFullPath + "Ebar1.xls");
        // matrixPSI.toColVector().mergeH(E.toColVector()).printInLabel("[ E1, E ]",15);
        // matrixI = matrixPSI.EQ( E, MathUtil.EPS);
        // matrixI.printSize("matrixI");
        // matrixI.printInLabel("E");
        Matrix C = new Matrix(new double[]
        {
                1.0000, -0.4464, -0.5535
        });

        InfiniteImpulseFilter AR = new InfiniteImpulseFilter(1.0, C, Ebar);

        Matrix E = AR.getFilteredData();
        E.printInLabel("E");
        /*
         * Matrix X = MatrixUtil.readXlsData( filenameFullPath+"filter.xls");
         * //matrixPSI
         * .toColVector().mergeH(E.toColVector()).printInLabel("[ E1, E ]",15);
         * //matrixI = matrixPSI.EQ( E, MathUtil.EPS); Matrix C = new Matrix(new
         * double[]{1.0000, 0.2, -0.25});
         * 
         * InfiniteImpulseFilter AR = new InfiniteImpulseFilter(1.0, C, X);
         * 
         * Matrix Y = AR.getFilteredData();
         * 
         * Y.printInLabel("Y");
         */

    }

}
