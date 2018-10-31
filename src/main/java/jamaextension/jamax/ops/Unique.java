/*
 * Unique.java
 *
 * Created on 29 November 2007, 03:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.ops;

import java.util.ArrayList;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Unique
{

    private Matrix uniqueData;
    private Indices I;

    /** Creates a new instance of Unique */
    public Unique(Matrix matrix)
    {
        uniqueEval(matrix);
        uniqueData.setSorted(true);
    }

    public Matrix getUniqueData()
    {
        return uniqueData;
    }

    public Indices getI()
    {
        return I;
    }

    private void uniqueEval(Matrix A)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("uniqueEval : Parameter \"A\" , must be non-null.");
        }
        Object[] obj = null;
        if (A.isnanBoolean())
        {
            obj = uniqueWithNaNs(A);
        }
        else
        {
            obj = uniqueWithFinites(A);
        }
        uniqueData = (Matrix) obj[0];
        I = (Indices) obj[1];
    }

    private static Object[] uniqueWithFinites(Matrix matrix)
    {
        // Important :
        // #1) This method works only if there is no NaNs in the array.
        // #2) This method is correct, same as matlab output for 'I' parameter
        // except that matlab add the last duplicate element instead of the
        // first duplicate
        // #3) Should extend this method to include the indices of elements

        /*
         * ====================== Java Test ===========================
         * double[][] mat = {{ 1, 2, Double.NaN, 5}, { Double.NaN, 5, 5,
         * Double.NaN}, { 5, 1, 4, 6}};
         * 
         * Matrix rn = new
         * Matrix(mat);//Matrix.random(3,4).arrayTimes(10.0).round();
         * 
         * System.out.println("\n------- rn --------"); rn.print(4,0);
         * 
         * Object[] obj = rn.uniqueInfNaN(); Matrix R = (Matrix)obj[0]; Indices
         * I = (Indices)obj[1]; Matrix Imat = Matrix.indicesToMatrix(I);
         * 
         * System.out.println("\n------- R --------"); R.print(4,0);
         * 
         * System.out.println("\n------- I --------"); Imat.print(4,0);
         * ==========================================================
         */

        int m = matrix.getRowDimension();
        int n = matrix.getColumnDimension();
        double[][] A = matrix.getArray();

        ArrayList<Double> finiteValueList = new ArrayList<Double>();
        ArrayList<Integer> finiteIndexList = new ArrayList<Integer>();
        Matrix finiteData = null;
        Indices finiteIndices = null;

        // double[][] UA = null; // uniqueEval array
        double[][] FA = null; // finite array

        double holdTemp = 0.0;
        double value = 0.0;

        int colCount = 0;
        int xI = 0;
        int xI2 = 0;
        int totalUnique = 0;
        int ind = 0;
        int len = 0;

        // //////////////////////////////////////////////////////////////////////////
        // ////////////////// Data is a matrix or column vector
        // /////////////////////
        // //////////////////////////////////////////////////////////////////////////
        if (matrix.isVector() == false || matrix.isColVector())
        {
            // collection of elements
            colCount = 0;
            for (int j = 0; j < n; j++)
            {
                for (int i = 0; i < m; i++)
                {
                    Double dNumber = new Double(A[i][j]);
                    Integer iNumber = null;
                    if (!finiteValueList.contains(dNumber))
                    {
                        iNumber = new Integer(colCount);
                        finiteValueList.add(dNumber);
                        finiteIndexList.add(iNumber);
                    }
                    colCount++;
                }// end inner for loop
            }// end outer for loop

            totalUnique = finiteValueList.size();
            if (totalUnique == 1)
            {
                return new Object[]
                {
                        new Matrix(new double[][]
                        {
                            {
                                A[0][0]
                            }
                        }), new Indices(1, 1)
                };
            }

            // / CHECK HERE IS THE FINITE SET HAS ONLY ONE ELEMENT
            finiteData = new Matrix(finiteValueList.size(), 1);
            FA = finiteData.getArray();
            finiteIndices = new Indices(finiteValueList.size(), 1);
            // Populate array
            for (int k = 0; k < finiteValueList.size(); k++)
            {
                value = finiteValueList.get(k).doubleValue();
                FA[k][0] = value;
                ind = finiteIndexList.get(k).intValue();
                finiteIndices.set(k, 0, ind);
            }
            // Sort array
            if (finiteValueList.size() > 1)
            {
                for (int pass = 1; pass < finiteData.length(); pass++)
                {
                    for (int i = 0; i < (finiteData.length() - 1); i++)
                    {
                        if (FA[i][0] > FA[i + 1][0])
                        { // System.out.print("block 3\n");
                            holdTemp = FA[i][0];
                            FA[i][0] = FA[i + 1][0];
                            FA[i + 1][0] = holdTemp;

                            xI = finiteIndices.get(i, 0);
                            xI2 = finiteIndices.get(i + 1, 0);
                            finiteIndices.set(i, 0, xI2);
                            finiteIndices.set(i + 1, 0, xI);
                        }
                    }// end for
                }// end for
            }// end finiteValueList.size()>1

        }// ////////////////////////////////////////////////////////////////////////
        else
        {// ////////////////////// Data is a row vector
         // /////////////////////////
         // /////////////////////////////////////////////////////////////////////
         // collection of elements
            colCount = 0;
            for (int j = 0; j < n; j++)
            {
                // for(int i=0; i<m; i++){
                Double dNumber = new Double(A[0][j]);
                Integer iNumber = null;
                if (!finiteValueList.contains(dNumber))
                {
                    iNumber = new Integer(colCount);
                    finiteValueList.add(dNumber);
                    finiteIndexList.add(iNumber);
                }
                colCount++;
                // }// end inner for loop
            }// end outer for loop

            totalUnique = finiteValueList.size();
            if (totalUnique == 1)
            {
                return new Object[]
                {
                        new Matrix(new double[][]
                        {
                            {
                                A[0][0]
                            }
                        }), new Indices(1, 1)
                };
            }

            // / CHECK HERE IS THE FINITE SET HAS ONLY ONE ELEMENT
            finiteData = new Matrix(1, finiteValueList.size());
            FA = finiteData.getArray();
            finiteIndices = new Indices(1, finiteValueList.size());
            // Populate array
            for (int k = 0; k < finiteValueList.size(); k++)
            {
                value = finiteValueList.get(k).doubleValue();
                FA[0][k] = value;
                ind = finiteIndexList.get(k).intValue();
                finiteIndices.set(0, k, ind);
            }
            // Sort array
            if (finiteValueList.size() > 1)
            {
                for (int pass = 1; pass < finiteData.length(); pass++)
                {
                    for (int i = 0; i < (finiteData.length() - 1); i++)
                    {
                        if (FA[0][i] > FA[0][i + 1])
                        { // System.out.print("block 3\n");
                            holdTemp = FA[0][i];
                            FA[0][i] = FA[0][i + 1];
                            FA[0][i + 1] = holdTemp;

                            xI = finiteIndices.get(0, i);
                            xI2 = finiteIndices.get(0, i + 1);
                            finiteIndices.set(0, i, xI2);
                            finiteIndices.set(0, i + 1, xI);
                        }
                    }// end for
                }// end for
            }// end finiteValueList.size()>1

        }// ////////////////////////////////////////////////////////////////////////

        return new Object[]
        {
                finiteData, finiteIndices
        };
    }

    private static Object[] uniqueWithNaNs(Matrix matrix)
    {
        // Important :
        // #1) This method works even if there is NaNs or Infinitys in the
        // array.
        // #2) This method is correct, same as matlab output for 'I' parameter
        // except that matlab add the last duplicate element instead of the
        // first duplicate
        // #3) Should extend this method to include the indices of elements

        /*
         * ====================== Java Test ===========================
         * double[][] mat = {{ 1, 2, Double.NaN, 5}, { Double.NaN, 5, 5,
         * Double.NaN}, { 5, 1, 4, 6}};
         * 
         * Matrix rn = new
         * Matrix(mat);//Matrix.random(3,4).arrayTimes(10.0).round();
         * 
         * System.out.println("\n------- rn --------"); rn.print(4,0);
         * 
         * Object[] obj = rn.uniqueInfNaN(); Matrix R = (Matrix)obj[0]; Indices
         * I = (Indices)obj[1]; Matrix Imat = Matrix.indicesToMatrix(I);
         * 
         * System.out.println("\n------- R --------"); R.print(4,0);
         * 
         * System.out.println("\n------- I --------"); Imat.print(4,0);
         * ==========================================================
         */

        int m = matrix.getRowDimension();
        int n = matrix.getColumnDimension();
        double[][] A = matrix.getArray();

        ArrayList<Double> finiteValueList = new ArrayList<Double>();
        ArrayList<Integer> finiteIndexList = new ArrayList<Integer>();

        ArrayList<Double> nanValueList = new ArrayList<Double>();
        ArrayList<Integer> nanIndexList = new ArrayList<Integer>();

        Matrix uniqueData = null;
        Indices uniqueIndices = null;
        Matrix finiteData = null;
        Indices finiteIndices = null;

        double[][] UA = null; // uniqueEval array
        double[][] FA = null; // finite array

        double holdTemp = 0.0;
        double value = 0.0;

        int colCount = 0;
        int xI = 0;
        int xI2 = 0;
        int totalUnique = 0;
        int ind = 0;
        int len = 0;

        // //////////////////////////////////////////////////////////////////////////
        // ////////////////// Data is a matrix or column vector
        // /////////////////////
        // //////////////////////////////////////////////////////////////////////////
        if (matrix.isVector() == false || matrix.isColVector())
        {
            // collection of elements
            colCount = 0;
            for (int j = 0; j < n; j++)
            {
                for (int i = 0; i < m; i++)
                {
                    Double dNumber = new Double(A[i][j]);
                    Integer iNumber = null;
                    if (Double.isNaN(A[i][j]))
                    {
                        iNumber = new Integer(colCount);
                        nanValueList.add(new Double(Double.NaN));
                        nanIndexList.add(iNumber);
                    }
                    else if (!finiteValueList.contains(dNumber))
                    {
                        iNumber = new Integer(colCount);
                        finiteValueList.add(dNumber);
                        finiteIndexList.add(iNumber);
                    }
                    colCount++;
                }// end inner for loop
            }// end outer for loop

            totalUnique = finiteValueList.size() + nanValueList.size();
            if (totalUnique == 1)
            {
                return new Object[]
                {
                        new Matrix(new double[][]
                        {
                            {
                                A[0][0]
                            }
                        }), new Indices(1, 1)
                };
            }

            uniqueData = new Matrix(totalUnique, 1, Double.NaN);
            uniqueIndices = new Indices(totalUnique, 1);
            UA = uniqueData.getArray();

            if (finiteValueList.isEmpty())
            {
                for (int i = 0; i < totalUnique; i++)
                {
                    ind = nanIndexList.get(i).intValue();
                    uniqueIndices.set(i, 0, ind);
                }
            }
            else
            {//
             // / CHECK HERE IS THE FINITE SET HAS ONLY ONE ELEMENT
                finiteData = new Matrix(finiteValueList.size(), 1);
                FA = finiteData.getArray();
                finiteIndices = new Indices(finiteValueList.size(), 1);
                // Populate array
                for (int k = 0; k < finiteValueList.size(); k++)
                {
                    value = finiteValueList.get(k).doubleValue();
                    FA[k][0] = value;
                    ind = finiteIndexList.get(k).intValue();
                    finiteIndices.set(k, 0, ind);
                }
                // Sort array
                if (finiteValueList.size() > 1)
                {
                    for (int pass = 1; pass < finiteData.length(); pass++)
                    {
                        for (int i = 0; i < (finiteData.length() - 1); i++)
                        {
                            if (FA[i][0] > FA[i + 1][0])
                            { // System.out.print("block 3\n");
                                holdTemp = FA[i][0];
                                FA[i][0] = FA[i + 1][0];
                                FA[i + 1][0] = holdTemp;

                                xI = finiteIndices.get(i, 0);
                                xI2 = finiteIndices.get(i + 1, 0);
                                finiteIndices.set(i, 0, xI2);
                                finiteIndices.set(i + 1, 0, xI);
                            }
                        }// end for
                    }// end for
                }// end finiteValueList.size()>1

                if (nanValueList.isEmpty())
                { // System.out.println(" #Matrix or Column Nan Zero");
                    uniqueData = finiteData;
                    uniqueIndices = finiteIndices;
                }
                else
                { // System.out.println(" #Matrix or Column  Nan Non-Zero");
                    for (int k = 0; k < finiteData.length(); k++)
                    { // fill-in the finite collection
                        value = finiteData.get(k, 0);
                        uniqueData.set(k, 0, value);
                        ind = finiteIndices.get(k, 0);
                        uniqueIndices.set(k, 0, ind);
                    }
                    for (int k = finiteData.length(); k < totalUnique; k++)
                    { // fill-in the fNaN collection
                        ind = nanIndexList.get(k - finiteData.length()).intValue();
                        uniqueIndices.set(k, 0, ind);
                    }
                }
            }
        }// ////////////////////////////////////////////////////////////////////////
        else
        {// ////////////////////// Data is a row vector
         // /////////////////////////
         // /////////////////////////////////////////////////////////////////////
         // collection of elements
            colCount = 0;
            for (int j = 0; j < n; j++)
            {
                // for(int i=0; i<m; i++){
                Double dNumber = new Double(A[0][j]);
                Integer iNumber = new Integer(colCount);
                if (Double.isNaN(A[0][j]))
                {
                    nanValueList.add(new Double(Double.NaN));
                    nanIndexList.add(iNumber);
                }
                else if (!finiteValueList.contains(dNumber))
                {
                    finiteValueList.add(dNumber);
                    finiteIndexList.add(iNumber);
                }
                colCount++;
                // }// end inner for loop
            }// end outer for loop

            totalUnique = finiteValueList.size() + nanValueList.size();
            if (totalUnique == 1)
            {
                return new Object[]
                {
                        new Matrix(new double[][]
                        {
                            {
                                A[0][0]
                            }
                        }), new Indices(1, 1)
                };
            }

            uniqueData = new Matrix(1, totalUnique, Double.NaN);
            uniqueIndices = new Indices(1, totalUnique);
            UA = uniqueData.getArray();

            if (finiteValueList.isEmpty())
            {
                for (int i = 0; i < totalUnique; i++)
                {
                    ind = nanIndexList.get(i).intValue();
                    uniqueIndices.set(0, i, ind);
                }
            }
            else
            {//
             // / CHECK HERE IS THE FINITE SET HAS ONLY ONE ELEMENT
                finiteData = new Matrix(1, finiteValueList.size());
                FA = finiteData.getArray();
                finiteIndices = new Indices(1, finiteValueList.size());
                // Populate array
                for (int k = 0; k < finiteValueList.size(); k++)
                {
                    value = finiteValueList.get(k).doubleValue();
                    FA[0][k] = value;
                    ind = finiteIndexList.get(k).intValue();
                    finiteIndices.set(0, k, ind);
                }
                // Sort array
                if (finiteValueList.size() > 1)
                {
                    for (int pass = 1; pass < finiteData.length(); pass++)
                    {
                        for (int i = 0; i < (finiteData.length() - 1); i++)
                        {
                            if (FA[0][i] > FA[0][i + 1])
                            { // System.out.print("block 3\n");
                                holdTemp = FA[0][i];
                                FA[0][i] = FA[0][i + 1];
                                FA[0][i + 1] = holdTemp;

                                xI = finiteIndices.get(0, i);
                                xI2 = finiteIndices.get(0, i + 1);
                                finiteIndices.set(0, i, xI2);
                                finiteIndices.set(0, i + 1, xI);
                            }
                        }// end for
                    }// end for
                }// end finiteValueList.size()>1

                if (nanValueList.isEmpty())
                { // System.out.println(" #Row Nan Zero");
                    uniqueData = finiteData;
                    uniqueIndices = finiteIndices;
                }
                else
                { // System.out.println(" #Row Nan Non-Zero");
                    for (int k = 0; k < finiteData.length(); k++)
                    { // fill-in the finite collection
                        value = finiteData.get(0, k);
                        uniqueData.set(0, k, value);
                        ind = finiteIndices.get(0, k);
                        uniqueIndices.set(0, k, ind);
                    }
                    for (int k = finiteData.length(); k < totalUnique; k++)
                    { // fill-in the fNaN collection
                        ind = nanIndexList.get(k - finiteData.length()).intValue();
                        uniqueIndices.set(0, k, ind);
                    }

                    /*
                     * System.out.println("----- 2) uniqueData -----");
                     * uniqueData.print(8,0);
                     * System.out.println("\n----- uniqueIndices -----");
                     * Matrix.indicesToMatrix(uniqueIndices).print(8,0);
                     */

                }
            }

        }// ////////////////////////////////////////////////////////////////////////

        return new Object[]
        {
                uniqueData, uniqueIndices
        };
    }
}
