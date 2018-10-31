/*
 * JOps.java
 *
 * Created on 5 March 2007, 17:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.ops;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortInd;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.elmat.Meshgrid;

/**
 * 
 * @author Feynman Perceptrons
 */
public final class JOps
{

    /** Creates a new instance of JOps */
    private JOps()
    {
    }

    public static Matrix intersectVec(Matrix matA, Indices matB)
    {
        return intersectVec(matA, Matrix.indicesToMatrix(matB));
    }

    public static Matrix intersectVec(Indices matA, Matrix matB)
    {
        return intersectVec(Matrix.indicesToMatrix(matA), matB);
    }

    /*
     * This method can only deal with vectors, and not matrices.
     */
    public static Matrix intersectVec(Matrix A, Matrix B)
    {
        if (A == null || B == null)
        {
            return null;
        }

        if (!A.isVector() || !B.isVector())
        {
            throw new IllegalArgumentException("intersectVec : Parameters \"A\"  and  \"B\" must  be both vectors.");
        }

        int lenA = A.length();
        int lenB = B.length();

        Matrix matA = null;
        if (A.isRowVector())
        {
            matA = A.toColVector();
        }
        else
        {
            matA = A;
        }

        Matrix matB = B;
        if (B.isRowVector())
        {
            matB = B.toColVector();
        }
        else
        {
            matB = B;
        }

        TreeSet<Double> set = new TreeSet<Double>();
        TreeSet<Double> setA = new TreeSet<Double>();
        TreeSet<Double> setB = new TreeSet<Double>();

        for (int i = 0; i < lenA; i++)
        {
            Double val = null;
            Double val2 = null;
            if (matA.isRowVector())
            {
                val = new Double(matA.get(0, i));
                val2 = new Double(matA.get(0, i));
            }
            else
            {
                val = new Double(matA.get(i, 0));
                val2 = new Double(matA.get(i, 0));
            }
            set.add(val);
            if (!setA.contains(val2))
            {
                setA.add(val2);
            }
        }

        for (int j = 0; j < lenB; j++)
        {
            Double val = null;
            Double val2 = null;
            if (matB.isRowVector())
            {
                val = new Double(matB.get(0, j));
                val2 = new Double(matB.get(0, j));
            }
            else
            {
                val = new Double(matB.get(j, 0));
                val2 = new Double(matB.get(j, 0));
            }
            set.add(val);
            if (!setB.contains(val2))
            {
                setB.add(val2);
            }
        }

        int size = set.size();
        if (size == 0)
        {
            return null;
        }
        double t = 0.0;

        ArrayList<Double> intersect = new ArrayList<Double>();
        Object[] objSet = set.toArray();
        for (int k = 0; k < objSet.length; k++)
        {
            Double intNum = (Double) objSet[k];
            if (setA.contains(intNum) && setB.contains(intNum))
            {
                intersect.add(intNum);
            }
        }

        size = intersect.size();
        Matrix isect = new Matrix(1, size);
        for (int v = 0; v < size; v++)
        {
            t = intersect.get(v).doubleValue();
            isect.set(0, v, t);
        }

        isect = JDatafun.sort(isect);

        return isect;
    }

    /*
     * This method can only deal with vectors, and not matrices.
     */
    public static Indices intersectVec(Indices matA, Indices matB)
    {
        if (matA == null || matB == null)
        {
            return null;
        }

        if (!matA.isVector() || !matB.isVector())
        {
            throw new IllegalArgumentException(
                    "intersectVec : Parameters \"matA\"  and  \"matB\" must  be both vectors.");
        }

        int lenA = matA.length();
        int lenB = matB.length();

        TreeSet<Integer> set = new TreeSet<Integer>();
        TreeSet<Integer> setA = new TreeSet<Integer>();
        TreeSet<Integer> setB = new TreeSet<Integer>();

        for (int i = 0; i < lenA; i++)
        {
            Integer val = null;
            Integer val2 = null;
            if (matA.isRowVector())
            {
                val = new Integer(matA.get(0, i));
                val2 = new Integer(matA.get(0, i));
            }
            else
            {
                val = new Integer(matA.get(i, 0));
                val2 = new Integer(matA.get(i, 0));
            }
            set.add(val);
            if (!setA.contains(val2))
            {
                setA.add(val2);
            }
        }

        for (int j = 0; j < lenB; j++)
        {
            Integer val = null;
            Integer val2 = null;
            if (matB.isRowVector())
            {
                val = new Integer(matB.get(0, j));
                val2 = new Integer(matB.get(0, j));
            }
            else
            {
                val = new Integer(matB.get(j, 0));
                val2 = new Integer(matB.get(j, 0));
            }
            set.add(val);
            if (!setB.contains(val2))
            {
                setB.add(val2);
            }
        }

        int size = set.size();
        if (size == 0)
        {
            return null;
        }
        int t = 0;

        ArrayList<Integer> intersect = new ArrayList<Integer>();
        Object[] objSet = set.toArray(); // set.iterator().
        for (int k = 0; k < objSet.length; k++)
        {
            Integer intNum = (Integer) objSet[k];
            if (setA.contains(intNum) && setB.contains(intNum))
            {
                intersect.add(intNum);
            }
        }

        size = intersect.size();
        // System.out.println(" size = "+size);

        if (size == 0)
        {
            return null;
        }

        Indices isect = new Indices(1, size);
        for (int v = 0; v < size; v++)
        {
            t = intersect.get(v).intValue();
            isect.set(0, v, t);
        }

        isect = JDatafun.sort(isect);

        return isect;
    }

    public static Object[] uniqueMat(Matrix a)
    {
        return uniqueMat(a, false);
    }

    public static Object[] uniqueMat(Matrix a, boolean hasSorted)
    {
        int rows = 0;
        int cols = 0;
        String msg = "";
        if (a != null)
        {
            rows = a.getRowDimension();
            cols = a.getColumnDimension();
        }
        else
        {
            msg = "Parameter \"a\" must be non-null.";
            throw new ConditionalRuleException("findDiff", msg);
        }

        boolean rowvec = ((rows == 1) && (cols > 1));
        Matrix A = null;
        Matrix b = null;
        Matrix temp = null;
        Matrix temp2 = null;

        Indices ndx = null;
        Indices pos = null;
        Object[] obj = null;
        int[] index = null;

        String order = "last";

        int numelA = (a == null ? 0 : a.numel());
        if (numelA == 0)
        {
            return new Object[3];
        }
        if (numelA == 1)
        {
            return new Object[]
            {
                    a.copy(), new Indices(1, 1, 0), new Indices(1, 1, 0)
            };
        }
        else
        {// ----------------------------------------------------------------------
            if (a.isVector())
            {
                if (a.isRowVector())
                {
                    A = a.toColVector();
                }
                else
                {
                    A = a;
                }
            }
            else
            {
                A = a.toColVector();
            }
            // Sort if unsorted. Only check this for long lists.
            int checksortcut = 1000;

            // -------- Old Block of codes which has been replaced by the
            // previous line ------
            // obj = JDatafun.sortInd(A);
            // b = (Matrix)obj[0];
            // ndx = (Indices)obj[1];
            if (numelA <= checksortcut || !hasSorted)
            {
                QuickSort sortA = new QuickSortMat(A, true, true);
                b = (Matrix) sortA.getSortedObject();
                ndx = sortA.getIndices();
            }
            else
            {
                b = A;
                ndx = Indices.linspace(0, numelA - 1).toColVector();// A.generateIndices();
            }
            // --------------------------------------------------------------------------------//

            // d indicates the location of non-matching entries.
            Matrix db = JDatafun.diff(b);

            // Since DIFF returns NaN in both Inf and NaN cases,
            // use slower method of detection if NaN's detected in DIFF(b).
            // After sort, Infs or NaNs will be at ends of list only.
            Indices d = null;
            if (Double.isNaN(db.get(0, 0)) || Double.isNaN(db.end()))
            {
                index = Indices.linspace(0, numelA - 2, 1).getColIndicesAt(0);
                temp = b.getRows(index);
                index = Indices.linspace(1, numelA - 1, 1).getColIndicesAt(0);
                temp2 = b.getRows(index);
                // d = b(1:numelA-1) ~= b(2:numelA);
                d = temp.NEQ(temp2);
                // System.out.println("EXECUTE-1");
            }
            else
            {
                // d = db ~= 0;
                d = db.NEQ(0);
                // System.out.println("EXECUTE-2");
            }
            // d.printInLabel("d",0);
            // if(true) { return null; }

            if (order.charAt(0) == 'l')
            {// Final element is always a member of unique list.
                d = d.mergeV(new Indices(1, 1, Boolean.TRUE));// .set(numelA -
                                                              // 1, 0,
                                                              // Boolean.TRUE);
            }
            else
            { // First element is always a member of unique list.
                d = new Indices(1, 1, Boolean.TRUE).mergeV(d);
            }

            // System.out.println("\n------- d --------");
            // d.transpose().print(4,0);

            Indices findD = d.find();

            if (findD != null)
            {
                // Create unique list by indexing into sorted list.
                boolean bsort = b.isSorted();
                b = b.getFromFind(findD);// b = b(d);
                b.setSorted(bsort);
            }
            // System.out.println("\n------- b --------");
            // b.transpose().print(4,0);

            if (order.charAt(0) == 'l')
            {
                Indices appendD = (new Indices(1, 1, Boolean.TRUE)).mergeV(d);
                pos = JDatafun.cumsum(appendD); // Lists position, starting at
                                                // 1.
                // System.out.println("\n posLen1 = "+pos.length());
                pos = pos.getIndices(0, numelA - 1, 0, 0);// pos(numelA+1) = [];
                                                          // // Remove extra
                                                          // element introduced
                                                          // by d.
                // System.out.println("\n EXECUTE " );
            }
            else
            {
                pos = JDatafun.cumsum(d);
            }
            pos = pos.minus(1); // subtract one since matlab indexes starts at
                                // 1, while Java starts at 0;

            // System.out.println("\n posLen = "+pos.length());
            // System.out.println("\n ndxLen = "+ndx.length());
            // if(true){ return null;}

            // pos(ndx) = pos;
            pos.setElements(ndx.getRowPackedCopy(), pos);

            /*
             * System.out.println("\n------- pos --------");
             * pos.plus(1).transpose().print(4,0);
             * 
             * System.out.println("\n------- ndx --------");
             * ndx.plus(1).transpose().print(4,0);
             */

            // System.out.println("\n------- pos_ndx --------");
            // pos_ndx.plus(1).transpose().print(4,0);

            // ndxInd = pos.getRowPackedCopy();
            // pos = pos_ndx.getRows(ndxInd);

            // System.out.println("\n------- pos --------");
            // pos.plus(1).transpose().print(4,0);

            if (findD != null)
            {
                // ndxInd = findD.getRowPackedCopy();
                // ndx = ndx.getIndices(ndxInd,0,0);//ndx(d);
                ndx = ndx.getFromFind(findD);// getIndices(ndxInd,0,0);
                // System.out.println("\n------- NDX --------");
            }
            // If row vector, return as row vector.
            if (rowvec)
            {
                b = b.transpose();
                ndx = ndx.transpose();
                pos = pos.transpose();
            }

        }// -------------------------------------------------------------------------

        return new Object[]
        {
                b, ndx, pos
        };
    }

    // //////////////////////////////////////////////////////////////////////////
    public static Object[] uniqueInd(Indices a)
    {
        return uniqueInd(a, false);
    }

    public static Object[] uniqueInd(Indices a, boolean hasSorted)
    {
        int rows = 0;
        int cols = 0;
        String msg = "";
        if (a != null)
        {
            rows = a.getRowDimension();
            cols = a.getColumnDimension();
        }
        else
        {
            msg = "Parameter \"a\" must be non-null.";
            throw new ConditionalRuleException("findDiff", msg);
        }

        boolean rowvec = ((rows == 1) && (cols > 1));
        Indices A = null;
        Indices b = null;
        Indices temp = null;
        Indices temp2 = null;

        Indices ndx = null;
        Indices pos = null;
        Object[] obj = null;
        int[] index = null;

        String order = "last";

        int numelA = (a == null ? 0 : a.numel());
        if (numelA == 0)
        {
            return new Object[3];
        }
        if (numelA == 1)
        {
            return new Object[]
            {
                    a.copy(), new Indices(1, 1, 0), new Indices(1, 1, 0)
            };
        }
        else
        {// ----------------------------------------------------------------------
            if (a.isVector())
            {
                if (a.isRowVector())
                {
                    A = a.toColVector();
                }
                else
                {
                    A = a;
                }
            }
            else
            {
                A = a.toColVector();
            }
            // Sort if unsorted. Only check this for long lists.
            int checksortcut = 1000;

            // -------- Old Block of codes which has been replaced by the
            // previous line ------
            // obj = JDatafun.sortInd(A);
            // b = (Matrix)obj[0];
            // ndx = (Indices)obj[1];
            if (numelA <= checksortcut || !hasSorted)
            {
                QuickSort sortA = new QuickSortInd(A, true, true);
                b = (Indices) sortA.getSortedObject();
                ndx = sortA.getIndices();
            }
            else
            {
                b = A;
                ndx = Indices.linspace(0, numelA - 1).toColVector();// A.generateIndices();
            }
            // --------------------------------------------------------------------------------//

            // d indicates the location of non-matching entries.
            Indices db = JDatafun.diff(b);

            // Since DIFF returns NaN in both Inf and NaN cases,
            // use slower method of detection if NaN's detected in DIFF(b).
            // After sort, Infs or NaNs will be at ends of list only.
            Indices d = null;
            if (Double.isNaN(db.get(0, 0)) || Double.isNaN(db.end()))
            {
                index = Indices.linspace(0, numelA - 2, 1).getColIndicesAt(0);
                temp = b.getRows(index);
                index = Indices.linspace(1, numelA - 1, 1).getColIndicesAt(0);
                temp2 = b.getRows(index);
                // d = b(1:numelA-1) ~= b(2:numelA);
                d = temp.NEQ(temp2);
                // System.out.println("EXECUTE-1");
            }
            else
            {
                // d = db ~= 0;
                d = db.NEQ(0);
                // System.out.println("EXECUTE-2");
            }
            // d.printInLabel("d",0);
            // if(true) { return null; }

            if (order.charAt(0) == 'l')
            {// Final element is always a member of unique list.
                d = d.mergeV(new Indices(1, 1, Boolean.TRUE));// .set(numelA -
                                                              // 1, 0,
                                                              // Boolean.TRUE);
            }
            else
            { // First element is always a member of unique list.
                d = new Indices(1, 1, Boolean.TRUE).mergeV(d);
            }

            // System.out.println("\n------- d --------");
            // d.transpose().print(4,0);

            Indices findD = d.find();

            if (findD != null)
            {
                // Create unique list by indexing into sorted list.
                boolean bsort = b.isSorted();
                b = b.getFromFind(findD);// b = b(d);
                b.setSorted(bsort);
            }
            // System.out.println("\n------- b --------");
            // b.transpose().print(4,0);

            if (order.charAt(0) == 'l')
            {
                Indices appendD = (new Indices(1, 1, Boolean.TRUE)).mergeV(d);
                pos = JDatafun.cumsum(appendD); // Lists position, starting at
                                                // 1.
                // System.out.println("\n posLen1 = "+pos.length());
                pos = pos.getIndices(0, numelA - 1, 0, 0);// pos(numelA+1) = [];
                                                          // // Remove extra
                                                          // element introduced
                                                          // by d.
                // System.out.println("\n EXECUTE " );
            }
            else
            {
                pos = JDatafun.cumsum(d);
            }
            pos = pos.minus(1); // subtract one since matlab indexes starts at
                                // 1, while Java starts at 0;

            // System.out.println("\n posLen = "+pos.length());
            // System.out.println("\n ndxLen = "+ndx.length());
            // if(true){ return null;}

            // pos(ndx) = pos;
            pos.setElements(ndx.getRowPackedCopy(), pos);

            /*
             * System.out.println("\n------- pos --------");
             * pos.plus(1).transpose().print(4,0);
             * 
             * System.out.println("\n------- ndx --------");
             * ndx.plus(1).transpose().print(4,0);
             */

            // System.out.println("\n------- pos_ndx --------");
            // pos_ndx.plus(1).transpose().print(4,0);

            // ndxInd = pos.getRowPackedCopy();
            // pos = pos_ndx.getRows(ndxInd);

            // System.out.println("\n------- pos --------");
            // pos.plus(1).transpose().print(4,0);

            if (findD != null)
            {
                // ndxInd = findD.getRowPackedCopy();
                // ndx = ndx.getIndices(ndxInd,0,0);//ndx(d);
                ndx = ndx.getFromFind(findD);// getIndices(ndxInd,0,0);
                // System.out.println("\n------- NDX --------");
            }
            // If row vector, return as row vector.
            if (rowvec)
            {
                b = b.transpose();
                ndx = ndx.transpose();
                pos = pos.transpose();
            }

        }// -------------------------------------------------------------------------

        return new Object[]
        {
                b, ndx, pos
        };
    }

    public static Matrix kron(Matrix A, Matrix B)
    {
        double ma = A.getRowDimension();
        double na = A.getColumnDimension();
        double mb = B.getRowDimension();
        double nb = B.getColumnDimension();

        Matrix meshmA = Matrix.linspace(0.0, ma - 1.0, (int) ma);
        Matrix meshmB = Matrix.linspace(0.0, mb - 1.0, (int) mb);
        Meshgrid meshA = new Meshgrid(meshmA, meshmB);
        meshmA = meshA.getXX();
        // meshmA.printInLabel("ia", 0);
        int[] ia = meshmA.toIndices().toColVector().getRowPackedCopy();
        meshmB = meshA.getYY();
        // meshmB.printInLabel("ib", 0);
        int[] ib = meshmB.toIndices().toColVector().getRowPackedCopy();

        Matrix meshnA = Matrix.linspace(0.0, na - 1.0, (int) na);
        Matrix meshnB = Matrix.linspace(0.0, nb - 1.0, (int) nb);
        Meshgrid meshB = new Meshgrid(meshnA, meshnB);
        meshnA = meshB.getXX();
        // meshnA.printInLabel("ja", 0);
        int[] ja = meshnA.toIndices().toColVector().getRowPackedCopy();
        meshnB = meshB.getYY();
        // meshnB.printInLabel("jb", 0);
        int[] jb = meshnB.toIndices().toColVector().getRowPackedCopy();

        meshmA = A.getMatrix(ia, ja);
        // meshmA.printInLabel("Aija",0);

        meshmB = B.getMatrix(ib, jb);
        // meshmB.printInLabel("Bijb",0);

        meshnA = meshmA.arrayTimes(meshmB);

        return meshnA;
    }

    public static int getIndex(Set<? extends Object> set, Object value)
    {
        int result = 0;
        for (Object entry : set)
        {
            if (entry.equals(value))
            {
                return result;
            }
            result++;
        }
        return -1;
    }

    public static Double getValueD(Set<? extends Double> set, int valueInd)
    {
        if ((valueInd > set.size()) || (valueInd < 0))
        {
            throw new ConditionalRuleException(
                    "getValueD : The integer input argument valueInd is an integer out of bound from the \"set\" elements' index.");
        }
        Double valueNum = null;
        int setInd = 0;
        for (Double entry : set)
        {
            if (setInd == valueInd)
            {
                valueNum = entry;
                break;
            }
            setInd++;
        }

        return valueNum;
    }

    public static Integer getValueI(Set<? extends Integer> set, int valueInd)
    {
        if ((valueInd > set.size()) || (valueInd < 0))
        {
            throw new ConditionalRuleException(
                    "getValueI : The integer input argument valueInd is an integer out of bound from the \"set\" elements' index.");
        }
        Integer valueNum = null;
        int setInd = 0;
        for (Integer entry : set)
        {
            if (setInd == valueInd)
            {
                valueNum = entry;
                break;
            }
            setInd++;
        }

        return valueNum;
    }

    private static void matExample()
    {

        double[] mat =
        {
                14, 3, Double.NaN, 7, Double.NaN, 11, 7, 0
        };
        Matrix A = new Matrix(mat);

        Object[] obj = uniqueMat(A);

        Matrix B = (Matrix) obj[0];
        Indices I = (Indices) obj[1];
        Indices J = (Indices) obj[2];

        System.out.println("\n------- B --------");
        B.print(4, 0);

        System.out.println("\n------- I --------");
        I.plus(1).print(4, 0);

        System.out.println("\n------- J --------");
        J.plus(1).print(4, 0);

        /*
         * double[][] mat = {{2, 10, 7, 10}, {6, 7, 3, 7}, {3, 4, 4, 2}}; Matrix
         * matA = new Matrix(mat);
         * 
         * double[][] mat2 = {{728621, 728621, 728623, 728623, 728623, 728623,
         * 728623, 728623, 728623, 728623, 728623, 728623, 728623, 728623,
         * 728623, 728623, 728623, 728623, 728623, 728623, 728623, 728623,
         * 728623, 728623, 728623, 728623, 728623, 728623, 728623, 728623,
         * 728623, 729519, 729519}}; Matrix matB = new Matrix(mat2); matB =
         * matB.flipLR();
         * 
         * 
         * Object[] obj = uniqueMat(matB);
         * 
         * Matrix B = (Matrix)obj[0]; Indices I = (Indices)obj[1]; Indices J =
         * (Indices)obj[2];
         * 
         * System.out.println("\n------- B --------"); B.print(4,0);
         * 
         * System.out.println("\n------- I --------"); I.plus(1).print(4,0);
         * 
         * System.out.println("\n------- J --------"); J.plus(1).print(4,0);
         * 
         * 
         * double[][] mat = {{ 1, 2, Double.NaN, 5}, { Double.NaN, 5, 5,
         * Double.NaN}, { 5, 1, 4, 6}};
         * 
         * Matrix rn = new
         * Matrix(mat);//Matrix.random(3,4).arrayTimes(10.0).round();
         * 
         * System.out.println("\n------- rn --------"); rn.print(4,0);
         * 
         * Object[] obj = unique(rn); Matrix R = (Matrix)obj[0]; Indices I =
         * (Indices)obj[1]; Matrix Imat = Matrix.indicesToMatrix(I);
         * 
         * System.out.println("\n------- R --------"); R.transpose().
         * print(4,0);
         * 
         * System.out.println("\n------- I --------");
         * I.plus(1).transpose().print(4 );
         */

        /*
         * Matrix a = new Matrix(new double[][]{{7, 4, 9, 5, 4, 8}}); Matrix b =
         * new Matrix(new double[][]{{9, 9, 4, 9, 1, 4, 8, 0, 1}});
         * 
         * Matrix c = intersectVec(a,b);
         * System.out.println("\n------- c --------"); c.print(4,0);
         */

    }

    public static void main(String[] args)
    {
        // matExample();

        double[][] a =
        {
                {
                        5, -9
                },
                {
                        3, 33
                },
                {
                        -3, -2
                },
                {
                        11, 2
                }
        };
        Matrix A = new Matrix(a);

        double[][] b =
        {
                {
                        14, 12, 6
                },
                {
                        7, 2, 14
                }
        };
        Matrix B = new Matrix(b);

        // Meshgrid mesh = new Meshgrid();

        Matrix C = kron(A, B);
        C.printInLabel("C", 0);

        /*
         * int[][] matArr = { {24, 16, 9, 26, 27, 10}, {5, 31, 4, 23, 28, 14},
         * {4, 2, 42, 2, 42, 41}, {22, 1, 39, 4, 48, 46}, {9, 32, 37, 31, 13, 1}
         * };
         * 
         * Indices matInd = new Indices(matArr);//.random(5,
         * 6).arrayTimes(50.0).round().toIndices();
         * matInd.printInLabel("matInd", 0);
         */

        /*
         * Object[] obj = matInd.sortRows(Dimension.ROW, true); Indices sorted =
         * (Indices) obj[0]; Indices ind = (Indices) obj[1];
         * 
         * sorted.printInLabel("sorted", 0); ind.printInLabel("ind", 0);
         */

        /*
         * Object[] obj = uniqueInd(matInd); Indices uniqueMatInd = (Indices)
         * obj[0]; Indices I = (Indices) obj[1]; Indices J = (Indices) obj[2];
         * 
         * uniqueMatInd.printInLabel("uniqueMatInd", 0);
         * I.plus(1).printInLabel("I", 0); J.plus(1).printInLabel("J", 0);
         */

    }
    /*
     * This method can only deal with vectors, and not matrices.
     */
    /*
     * public static Indices[] intersectVec(Indices[] matA, Indices[] matB){
     * if(matA==null || matB==null){ return null; }
     * 
     * int aNum = matA.length; int bNum = matB.length;
     * 
     * if(!matA.isVector() || !matB.isVector()){ throw new
     * IllegalArgumentException
     * ("intersectVec : Parameters \"matA\"  and  \"matB\" must  be both vectors."
     * ); }
     * 
     * int lenA = matA.length(); int lenB = matB.length();
     * 
     * TreeSet set = new TreeSet(); TreeSet setA = new TreeSet(); TreeSet setB =
     * new TreeSet();
     * 
     * for(int i=0; i<lenA; i++){ Integer val = null; Integer val2 = null;
     * if(matA.isRowVector()){ val = new Integer(matA.get(0,i)); val2 = new
     * Integer(matA.get(0,i)); } else{ val = new Integer(matA.get(i,0)); val2 =
     * new Integer(matA.get(i,0)); } set.add(val); if(!setA.contains(val2)) {
     * setA.add(val2); } }
     * 
     * for(int j=0; j<lenB; j++){ Integer val = null; Integer val2 = null;
     * if(matA.isRowVector()){ val = new Integer(matB.get(0,j)); val2 = new
     * Integer(matB.get(0,j)); } else{ val = new Integer(matB.get(j,0)); val2 =
     * new Integer(matB.get(j,0)); } set.add(val); if(!setB.contains(val2)) {
     * setB.add(val2); } }
     * 
     * int size = set.size(); if(size==0){ return null; } int t = 0;
     * 
     * ArrayList intersect = new ArrayList(); Object[] objSet = set.toArray();
     * for(int k=0; k<objSet.length; k++){ Integer intNum = (Integer)objSet[k];
     * if(setA.contains(intNum) && setB.contains(intNum)){
     * intersect.add(intNum); } }
     * 
     * size = intersect.size(); Indices isect = new Indices(1,size); for(int
     * v=0; v<size; v++){ t = ((Integer)intersect.get(v)).intValue();
     * isect.set(0,v,t); }
     * 
     * isect = JDatafun.sort(isect);
     * 
     * return isect; }
     */
    /*
     * private static Object[] uniqueMatrixFiniteValueListZero(Matrix
     * uniqueData, Indices uniqueIndices, ArrayList infiniteValueList, ArrayList
     * infiniteIndexList, ArrayList nanIndexList){
     * 
     * double value = 0.0; int ind = 0;
     * 
     * if(infiniteValueList.size()==0){//-------------------------------------
     * for(int i=0; i< nanIndexList.size(); i++){ ind =
     * ((Integer)nanIndexList.get(i)).intValue(); uniqueIndices.set(i,0,ind); }
     * } else if(infiniteValueList.size()==1){ value =
     * ((Double)infiniteValueList.get(0)).doubleValue(); ind =
     * ((Integer)infiniteIndexList.get(0)).intValue();
     * uniqueIndices.set(0,0,ind); for(int i=1; i< nanIndexList.size(); i++){
     * ind = ((Integer)nanIndexList.get(i)).intValue();
     * uniqueIndices.set(i,0,ind); } uniqueData.set(0,0,value); } else
     * if(infiniteValueList.size()==2){
     * uniqueData.set(0,0,Double.NEGATIVE_INFINITY);
     * uniqueData.set(1,0,Double.POSITIVE_INFINITY); value =
     * ((Double)infiniteValueList.get(0)).doubleValue(); if(value<0.0){//
     * -Infinity for first entry ind =
     * ((Integer)infiniteIndexList.get(0)).intValue();
     * uniqueIndices.set(0,0,ind); ind =
     * ((Integer)infiniteIndexList.get(1)).intValue();
     * uniqueIndices.set(1,0,ind); } else{// +Infinity for first entry ind =
     * ((Integer)infiniteIndexList.get(0)).intValue();
     * uniqueIndices.set(1,0,ind); ind =
     * ((Integer)infiniteIndexList.get(1)).intValue();
     * uniqueIndices.set(0,0,ind); } for(int i=1; i<nanIndexList.size(); i++){
     * ind = ((Integer)nanIndexList.get(i)).intValue();
     * uniqueIndices.set(i,0,ind); } }
     * 
     * return new Object[]{uniqueData, uniqueIndices}; }
     * 
     * private static Object[] uniqueMatrixInfiniteValueListZero(double[][] FA,
     * Matrix uniqueData, Matrix finiteData, Indices uniqueIndices, Indices
     * finiteIndices, ArrayList finiteValueList, ArrayList nanValueList,
     * ArrayList nanIndexList){
     * 
     * if(nanValueList.size()==0){ uniqueData = finiteData; uniqueIndices =
     * finiteIndices; } else{ int len = nanValueList.size(); int ind = 0;
     * for(int v=0; v<(finiteValueList.size()+nanValueList.size()); v++){
     * if(v<=(finiteValueList.size()-1)){ uniqueData.set(v,0,FA[v][0]); ind =
     * finiteIndices.get(v,0); uniqueIndices.set(v,0,ind); } else{
     * uniqueData.set(v,0, Double.NaN); ind =
     * ((Integer)nanIndexList.get(v)).intValue(); uniqueIndices.set(v,0,ind); }
     * } } return new Object[]{uniqueData, uniqueIndices}; }
     * 
     * public static Object[] uniqueMatrixInfiniteValueListOne(int totalUnique,
     * double[][] FA, Matrix uniqueData, Matrix finiteData, Indices
     * uniqueIndices, Indices finiteIndices, ArrayList finiteValueList ,
     * ArrayList finiteIndexList, ArrayList infiniteValueList, ArrayList
     * infiniteIndexList, ArrayList nanValueList, ArrayList nanIndexList){
     * double value = ((Double)infiniteValueList.get(0)).doubleValue(); int ind
     * = ((Integer)infiniteIndexList.get(0)).intValue();
     * 
     * if(nanValueList.size()==0){ if(value<0.0){
     * uniqueData.set(0,0,Double.NEGATIVE_INFINITY); uniqueIndices.set(0,0,ind);
     * for(int v=1; v<=finiteValueList.size(); v++){
     * uniqueData.set(v,0,FA[v-1][0]); ind =
     * ((Integer)finiteIndexList.get(v-1)).intValue();
     * uniqueIndices.set(v,0,ind); } } else{
     * uniqueData.set(totalUnique-1,0,Double.POSITIVE_INFINITY);
     * uniqueIndices.set(totalUnique-1,0,ind); for(int v=0;
     * v<finiteValueList.size(); v++){ uniqueData.set(v,0,FA[v][0]); ind =
     * ((Integer)finiteIndexList.get(v)).intValue(); uniqueIndices.set(v,0,ind);
     * } } }
     * else{//-------------------------------------------------------------
     * if(value<0.0){ uniqueData.set(0,0,Double.NEGATIVE_INFINITY);
     * uniqueIndices.set(0,0,ind); for(int v=1; v<=finiteValueList.size(); v++){
     * uniqueData.set(v,0,FA[v-1][0]); ind =
     * ((Integer)finiteIndexList.get(v-1)).intValue();
     * uniqueIndices.set(v,0,ind); } for(int v = (finiteValueList.size()+1);
     * v<(finiteValueList.size()+nanValueList.size()); v++){
     * uniqueData.set(v,0,Double.NaN); ind =
     * ((Integer)nanIndexList.get(v-finiteValueList.size())).intValue();
     * uniqueIndices.set(v,0,ind); } } else{
     * uniqueData.set(finiteValueList.size(),0,Double.POSITIVE_INFINITY);
     * uniqueIndices.set(finiteValueList.size(),0,ind); for(int v=0;
     * v<finiteValueList.size(); v++){ uniqueData.set(v,0,FA[v][0]); ind =
     * ((Integer)finiteIndexList.get(v)).intValue(); uniqueIndices.set(v,0,ind);
     * } for(int v = (finiteValueList.size()+1);
     * v<(finiteValueList.size()+nanValueList.size()); v++){
     * uniqueData.set(v,0,Double.NaN); ind =
     * ((Integer)nanIndexList.get(v-finiteValueList.size())).intValue();
     * uniqueIndices.set(v,0,ind); }
     * 
     * }
     * 
     * }
     * 
     * return new Object[]{uniqueData, uniqueIndices}; }
     */
}// ------------------------- End Class Definition
// ------------------------------

