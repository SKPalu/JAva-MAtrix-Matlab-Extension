package jamaextension.jamax.ops;

import java.util.HashSet;
import java.util.LinkedHashSet;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortInd;

public class UniqueSetInd
{

    private Indices uniqueData;
    private Indices I;

    public UniqueSetInd(Indices matrix)
    {
        uniqueEval(matrix);
        uniqueData.setSorted(true);
    }

    private void uniqueEval(Indices A)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("uniqueEval : Parameter \"A\" , must be non-null.");
        }
        Object[] obj = null;
        // if (A.isnanBoolean())
        // {
        // obj = uniqueWithNaNs(A);
        // }
        // else
        // {
        obj = uniqueWithFinites(A);
        // }
        uniqueData = (Indices) obj[0];
        I = (Indices) obj[1];
    }

    private static Object[] uniqueWithFinites(Indices matrix)
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
        int[][] A = matrix.getArray();

        HashSet<Integer> finiteValueList = new LinkedHashSet<Integer>();
        HashSet<Integer> finiteIndexList = new LinkedHashSet<Integer>();
        Object[] finiteIndexObject = null;
        Indices finiteData = null;
        Indices finiteIndices = null;

        // double[][] UA = null; // uniqueEval array
        int[][] FA = null; // finite array

        // int holdTemp = 0;
        // double value = 0.0;

        int colCount = 0;
        // int xI = 0;
        // int xI2 = 0;
        int totalUnique = 0;
        int ind = 0;
        // int len = 0;

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
                    int dNumber = A[i][j];
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
                        new Indices(new int[][]
                        {
                            {
                                A[0][0]
                            }
                        }), new Indices(1, 1)
                };
            }

            // / CHECK HERE IS THE FINITE SET HAS ONLY ONE ELEMENT
            finiteData = new Indices(finiteValueList.size(), 1);
            FA = finiteData.getArray();
            finiteIndices = new Indices(finiteValueList.size(), 1);

            // for(String str : streams)

            // Populate array
            // for (int k = 0; k < finiteValueList.size(); k++)
            int k = 0;
            finiteIndexObject = finiteIndexList.toArray();
            for (int value : finiteValueList)
            {
                // value = finiteValueList.get(k).doubleValue();
                FA[k][0] = value;
                ind = (Integer) finiteIndexObject[k]; // JOps.getValueI(finiteIndexList,
                                                  // k);//
                                                  // finiteIndexList.get(k).intValue();
                finiteIndices.set(k, 0, ind);
                k++;
            }

            // Sort array
            if (finiteValueList.size() > 1)
            {
                /*
                 * for (int pass = 1; pass < finiteData.length(); pass++) { for
                 * (int i = 0; i < (finiteData.length() - 1); i++) { if
                 * (FA[i][0] > FA[i + 1][0]) { // System.out.print("block 3\n");
                 * holdTemp = FA[i][0]; FA[i][0] = FA[i + 1][0]; FA[i + 1][0] =
                 * holdTemp;
                 * 
                 * xI = finiteIndices.get(i, 0); xI2 = finiteIndices.get(i + 1,
                 * 0); finiteIndices.set(i, 0, xI2); finiteIndices.set(i + 1, 0,
                 * xI); } }// end for }// end for
                 */

                QuickSort sortI = new QuickSortInd(new Indices(FA), true, true);
                Indices FAind = (Indices) sortI.getSortedObject();
                FA = FAind.getArray();
                finiteData = FAind;
                finiteIndices = sortI.getIndices();

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
                int dNumber = A[0][j];
                Integer iNumber = j;
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
                        new Indices(new int[][]
                        {
                            {
                                A[0][0]
                            }
                        }), new Indices(1, 1)
                };
            }

            // / CHECK HERE IS THE FINITE SET HAS ONLY ONE ELEMENT
            finiteData = new Indices(1, finiteValueList.size());
            FA = finiteData.getArray();
            finiteIndices = new Indices(1, finiteValueList.size());

            // Populate array
            int k = 0;
            finiteIndexObject = finiteIndexList.toArray();
            for (int value : finiteValueList)
            // for (int k = 0; k < finiteValueList.size(); k++)
            {
                // value = finiteValueList.get(k).doubleValue();
                FA[0][k] = value;
                ind = (Integer) finiteIndexObject[k];// JOps.getValueI(finiteIndexList,
                                                 // k);//
                                                 // finiteIndexList.get(k).intValue();
                finiteIndices.set(0, k, ind);
                k++;
            }
            // Sort array
            if (finiteValueList.size() > 1)
            {

                // Take this block off
                /*
                 * for (int pass = 1; pass < finiteData.length(); pass++) { for
                 * (int i = 0; i < (finiteData.length() - 1); i++) { if
                 * (FA[0][i] > FA[0][i + 1]) { // System.out.print("block 3\n");
                 * holdTemp = FA[0][i]; FA[0][i] = FA[0][i + 1]; FA[0][i + 1] =
                 * holdTemp;
                 * 
                 * xI = finiteIndices.get(0, i); xI2 = finiteIndices.get(0, i +
                 * 1); finiteIndices.set(0, i, xI2); finiteIndices.set(0, i + 1,
                 * xI); } }// end for }// end for
                 */

                QuickSort sortI = new QuickSortInd(new Indices(FA), true, true);
                Indices FAind = (Indices) sortI.getSortedObject();
                FA = FAind.getArray();
                finiteData = FAind;
                finiteIndices = sortI.getIndices();

            }// end finiteValueList.size()>1

        }// ////////////////////////////////////////////////////////////////////////

        return new Object[]
        {
                finiteData, finiteIndices
        };
    }

    public Indices getUniqueData()
    {
        return uniqueData;
    }

    public Indices getI()
    {
        return I;
    }

    public static void main(String[] args)
    {
        Indices A = Matrix.rand(20, 10).scale(10).round().toIndices();

        /*
         * FindInd ALT3 = A.LTEQ(3).findIJ(); if (!ALT3.isNull()) { int[] ind =
         * ALT3.getIndex(); A.setElements(ind, Double.NaN); }
         */

        A.printInLabel("A", 0);
        UniqueSetInd set = new UniqueSetInd(A);
        Indices uniqueA = set.getUniqueData();
        uniqueA.printInLabel("uniqueA", 0);
        set.getI().printInLabel("I");

    }

}
