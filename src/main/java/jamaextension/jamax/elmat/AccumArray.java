/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.elmat;

import java.util.ArrayList;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.Max;
import jamaextension.jamax.datafun.MaxInd;
import jamaextension.jamax.datafun.MaxMat;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortInd;
import jamaextension.jamax.datafun.Sum;
import jamaextension.jamax.datafun.SumInd;
import jamaextension.jamax.datafun.SumMat;
import jamaextension.jamax.funfun.FunFuncAbstract;
import jamaextension.jamax.funfun.FunctionFunctions;

/**
 * 
 * @author Sione
 */
public class AccumArray
{

    private Indices subs;
    private Object val;
    // output
    private Object A;
    private boolean valmatrix = false;
    private boolean valindices = false;
    private FunctionFunctions fun = createFunSum();
    private Indices sz;
    private Number fillVal = new Double(0.0);

    public AccumArray(Indices subs, Object val)
    {
        this(subs, val, (Object[]) null);
    }

    public AccumArray(Indices subs, Object val, Object... varagin)
    {
        String msg = "";
        int M = 1;
        int N = 1;

        if (varagin != null && varagin.length != 0)
        {
            if (varagin.length > 3)
            {
                msg = "Length of object array parameter \"varagin\" must be 3 or less.";
                throw new ConditionalRuleException("AccumArray", msg);
            }
            if (varagin.length > 0 && varagin[0] != null)
            {
                if (!(varagin[0] instanceof Indices))
                {
                    msg = "First element of object array parameter \"varagin\" must be instance of Indices.";
                    throw new ConditionalRuleException("AccumArray", msg);
                }
                Indices indSiz = (Indices) varagin[0];
                if (!indSiz.isNull())
                {
                    // if (!indSiz.isVector()) {
                    // msg = "Indices parameter \"indSiz\" must be a vector.";
                    // throw new ConditionalRuleException("AccumArray", msg);
                    // }
                    if (indSiz.isColVector())
                    {
                        indSiz = indSiz.toRowVector();
                    }
                    this.sz = indSiz;
                }
            }
            if (varagin.length > 1 && varagin[1] != null)
            {
                if (!(varagin[1] instanceof FunctionFunctions))
                {
                    msg = "Second element of object array parameter \"varagin\" must be instance of FunctionFunctions.";
                    throw new ConditionalRuleException("AccumArray", msg);
                }
                this.fun = (FunctionFunctions) varagin[1];
            }
            if (varagin.length > 2 && varagin[2] != null)
            {
                if (!(varagin[2] instanceof Number))
                {
                    msg = "Second element of object array parameter \"varagin\" must be instance of Number.";
                    throw new ConditionalRuleException("AccumArray", msg);
                }
                this.fillVal = (Number) varagin[2];
            }
        }

        if (subs != null && !subs.isNull())
        {
            if (subs.LT(0).anyBoolean())
            {
                msg = "All elements of \"subs\" must be non-negative.";
                throw new ConditionalRuleException("AccumArray", msg);
            }
            if (subs.isVector())
            {
                if (subs.isRowVector())
                {
                    subs = subs.toColVector();
                }
            }
            M = subs.getRowDimension();
            N = subs.getColumnDimension();

            if (this.sz != null)
            {
                // if (sz.length() != N) {
                // }
                if (N > 1)
                {
                    Indices maxSubs = JDatafun.max(subs, Dimension.ROW);
                    if (maxSubs.length() != sz.length())
                    {
                        msg = "Number of columns in \"subs\" must equal the length of \"sz\" vector.";
                        throw new ConditionalRuleException("AccumArray", msg);
                    }
                    boolean cond = sz.GTEQ(maxSubs).allBoolean();
                    if (!cond)
                    {
                        msg = "Input \"subs\" and \"sz\" must satisfy \"all(sz>=max(subs))\".";
                        throw new ConditionalRuleException("AccumArray", msg);
                    }
                    // ALL(SZ >= MAX(SUBS,[],1))
                    /*
                     * if (this.sz != null ) { if (M != this.sz.length())
                     * {//Check this block because msg =
                     * "Indices \"sz\" must be a row vector with one element for each column of \"subs\"."
                     * ; throw new ConditionalRuleException("AccumArray", msg);
                     * } }
                     */
                }
                else
                {
                    int fullLen = JDatafun.max(subs).start();
                    if (sz.length() != 2)
                    {
                        msg = "Input \"sz\" must be a two-element vector of size [N x 1].";
                        throw new ConditionalRuleException("AccumArray", msg);
                    }
                    if (sz.end() != 1)
                    {
                        msg = "Second element of Indices \"sz\" must be one, ie: [N x 1].";
                        throw new ConditionalRuleException("AccumArray", msg);
                    }
                    if (sz.start() < fullLen)
                    {
                        msg = "First element of Indices \"sz\" must equal or greater than \"max(subs)\" (= " + fullLen
                                + ").";
                        throw new ConditionalRuleException("AccumArray", msg);
                    }
                }
            }
            else
            {
                Indices subsPlus1 = subs.plus(1);
                if (N > 1)
                {
                    sz = JDatafun.max(subsPlus1, Dimension.ROW);
                    // sz.printInLabel("sz");
                }
                else
                {
                    int maxSubs = JDatafun.max(subsPlus1).start();
                    sz = new Indices(new int[]
                    {
                            maxSubs, 1
                    });
                }
            }
        }
        else
        {
            msg = "Indices \"subs\" must be non-null or non-empty.";
            throw new ConditionalRuleException("AccumArray", msg);
        }
        this.subs = subs;

        if (val == null)
        {
            msg = "Object parameter \"val\" must be non-null.";
            throw new ConditionalRuleException("AccumArray", msg);
        }

        boolean cond = !(val instanceof Matrix) && !(val instanceof Indices) && !(val instanceof Number);// &&
                                                                                                         // (((Indices)
                                                                                                         // obj).isLogical()))
                                                                                                         // ||
                                                                                                         // (obj
                                                                                                         // instanceof
                                                                                                         // Indices3D)
        // || (obj instanceof Number);;//!MathUtil.isNumeric(val);
        if (cond)
        {
            msg = "Parameter \"val\" must be an instance of \"Matrix\" or \"Indices\".";
            throw new ConditionalRuleException("AccumArray", msg);
        }

        if (val instanceof Matrix)
        {
            Matrix matVal = (Matrix) val;
            if (matVal.isNull())
            {
                msg = "Matrix parameter \"val\" must be non-empty.";
                throw new ConditionalRuleException("AccumArray", msg);
            }

            if (!matVal.isVector())
            {
                msg = "Matrix parameter \"val\" must be a vector and not a matrix.";
                throw new ConditionalRuleException("AccumArray", msg);
            }

            if (matVal.length() != 1)
            {
                if (matVal.length() != M)
                {
                    msg = "Total elements in Matrix parameter \"val\" must equal number of rows in \"subs\".";
                    throw new ConditionalRuleException("AccumArray", msg);
                }
            }
            else
            {
                matVal = new Matrix(1, M, matVal.start());
            }

            if (matVal.isRowVector())
            {
                this.val = matVal.toColVector();
            }
            else
            {
                this.val = matVal;
            }
            valmatrix = true;
        }
        else if (val instanceof Indices)
        {
            Indices indVal = (Indices) val;
            if (indVal.isNull())
            {
                msg = "Indices parameter \"val\" must be non-empty.";
                throw new ConditionalRuleException("AccumArray", msg);
            }
            if (!indVal.isVector())
            {
                msg = "Indices parameter \"val\" must be a vector and not a matrix.";
                throw new ConditionalRuleException("AccumArray", msg);
            }

            if (indVal.length() != 1)
            {
                if (indVal.length() != M)
                {
                    msg = "Total elements in Indices parameter \"val\" must equal number of rows in \"subs\".";
                    throw new ConditionalRuleException("AccumArray", msg);
                }
            }
            else
            {
                indVal = new Indices(1, M, indVal.start());
            }

            if (indVal.isRowVector())
            {
                this.val = indVal.toColVector();
            }
            else
            {
                this.val = indVal;
            }
            valindices = true;
        }
        else if (val instanceof Number)
        {
            if (val instanceof Integer)
            {
                this.val = new Indices(1, M, (Integer) val);
                valindices = true;
            }
            else if (val instanceof Double)
            {
                this.val = new Matrix(1, M, (Double) val);
                valmatrix = true;
            }
            else
            {
                msg = "Only \"Integer\" parameter is supported vor \"val\" and not \"Float\".";
                throw new ConditionalRuleException("AccumArray", msg);
            }
        }
        else
        {
            msg = "Parameter \"val\" must be an instance of \"Integer\", \"Matrix\" or \"Indices\" (currently supported).";
            throw new ConditionalRuleException("AccumArray", msg);
        }

        if (N == 1)
        {
            colSubsSingle();
        }
        else if (N == 2)
        {
            /*
             * if (true) { msg = "To do #1. Not implemented yet."; throw new
             * ConditionalRuleException("AccumArray", msg); }
             */
            colSubsDouble();
        }
        else if (N == 3)
        {
            if (true)
            {
                msg = "To do #2. Not implemented yet.";
                throw new ConditionalRuleException("AccumArray", msg);
            }
            colSubsTripple();
        }
        else
        {
            msg = "The number of columns for \"subs\" must be 3 or less.";
            throw new ConditionalRuleException("AccumArray", msg);
        }

    }

    private void colSubsTripple()
    {
        String msg = "To do #2. Not implemented yet.";
        throw new ConditionalRuleException("tripleSolSubs", msg);
    }

    private void printList(ArrayList<Integer> indList)
    {
        int siz = indList.size();
        for (int i = 0; i < siz; i++)
        {
            int I = indList.get(i);
            System.out.println("I = " + I);
        }
    }

    private void colSubsDouble()
    {
        Indices subsPlus1 = subs.plus(1);
        // Matrix matSubs = Matrix.indicesToMatrix(subsPlus1);
        // matSubs = JDatafun.prod(matSubs, Dimension.COL);
        // Indices prodSubs = matSubs.toIndices();
        // prodSubs.printInLabel("prodSubs");

        // Indices maxSubs = JDatafun.max(subsPlus1, Dimension.ROW);//add 1,
        // since Java indexing starts at zero.
        int maxRow = sz.start();// maxSubs.start();
        int maxCol = sz.end();// maxSubs.end();
        int len = subsPlus1.getRowDimension();// JDatafun.max(maxSubs).start();

        // int subsRow = subs.getRowDimension();
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < len; i++)
        {
            /*
             * Integer subsRowInd = prodSubs.getElementAt(i) - 1; //subtract 1
             * to transform index back into zero based Java if
             * (!list.contains(subsRowInd)) { list.add(subsRowInd); }
             */
            int curRow = subsPlus1.get(i, 0);
            int curCol = subsPlus1.get(i, 1);

            int indEl = indEntry2Ele(curRow, curCol, maxRow);
            // if (!list.contains(indEl)) {
            list.add(indEl);
            // }
        }

        Indices colMajorInd = new Indices(1, len);
        int objInd = 0;
        for (int i = 0; i < len; i++)
        {
            objInd = list.get(i);
            colMajorInd.set(0, i, objInd);
        }

        list = new ArrayList<Integer>();
        ArrayList<Indices> listSubs = new ArrayList<Indices>();

        for (int i = 0; i < len; i++)
        {
            objInd = colMajorInd.get(0, i);
            if (!list.contains(objInd))
            {
                list.add(objInd);
                listSubs.add(subs.getRowAt(i));
            }
        }

        int sizList = list.size();
        // printList(list);

        String msg = "";
        int[] arr = null;
        Matrix tmpMat = null;
        Indices tmpInd = null;
        Indices rowPos = null;
        int rowI = 0;
        int colJ = 0;

        // //////////////////////////////////////////////////////////////////////
        if (val instanceof Matrix)
        {
            double doubleVal = 0.0;
            Matrix matVal = (Matrix) val;
            if (len > matVal.length())
            {
                msg = "The largest element of \"subs\" column indices can't be greater than the length of \"val\" matrix vector.";
                throw new ConditionalRuleException("doubleSolSubs", msg);
            }
            // this.val = indVal.getEls(arr);
            Matrix accumMat = new Matrix(maxRow, maxCol, this.fillVal.doubleValue());
            for (int i = 0; i < sizList; i++)
            {
                int pos = list.get(i);
                arr = colMajorInd.EQ(pos).findIJ().getIndex();
                tmpMat = matVal.getEls(arr);
                fun.funfunc(1, tmpMat, (Object[]) null);
                doubleVal = (Double) fun.output()[0];// JDatafun.sum(tmpMat).start();
                rowPos = listSubs.get(i);// subs.getRowAt(pos - 1);
                rowI = rowPos.start();
                colJ = rowPos.end();
                accumMat.set(rowI, colJ, doubleVal);
                // accumMat.setElementAt(pos - 1, doubleVal);
            }
            A = accumMat;
        }
        else if (val instanceof Indices)
        {
            int integerVal = 0;
            Indices indVal = (Indices) val;
            if (len > indVal.length())
            {
                msg = "The largest element of \"subs\" column indices can't be greater than the length of \"val\" indices vector.";
                throw new ConditionalRuleException("doubleSolSubs", msg);
            }
            // this.val = indVal.getEls(arr);
            Indices accumInd = new Indices(maxRow, maxCol, this.fillVal.intValue());
            for (int i = 0; i < sizList; i++)
            {
                int pos = list.get(i);
                // System.out.println("pos = " + pos);
                arr = colMajorInd.EQ(pos).findIJ().getIndex();
                tmpInd = indVal.getEls(arr);
                fun.funfunc(1, tmpInd, (Object[]) null);
                integerVal = (Integer) fun.output()[0];// JDatafun.sum(tmpInd).start();
                rowPos = listSubs.get(i);// subs.getRowAt(pos - 1);
                rowI = rowPos.start();
                colJ = rowPos.end();
                accumInd.set(rowI, colJ, integerVal);
                // accumInd.setElementAt(pos - 1, integerVal);
            }
            A = accumInd;
        }
        // //////////////////////////////////////////////////////////////////////

        // printList(list);

        // msg = "To do #1. Not implemented yet.";
        // throw new ConditionalRuleException("colSubsDouble", msg);
    }

    private void colSubsSingle()
    {
        String msg = "";
        // sort first
        QuickSort sort = new QuickSortInd(subs, true, true); // QuickSortInd(Indices
                                                             // A, boolean
                                                             // computeSortedIndex,
                                                             // boolean
                                                             // sortedCopy)
        Indices sortedSubsObj = (Indices) sort.getSortedObject();
        // Indices sortedSubsInd = sort.getIndices();

        ArrayList<Integer> list = new ArrayList<Integer>();
        int numElements = sortedSubsObj.length();
        for (int k = 0; k < numElements; k++)
        {
            Integer vvl = sortedSubsObj.getElementAt(k);
            if (!list.contains(vvl))
            {
                list.add(vvl);
            }
        }

        int sizList = list.size();

        int[] arr = null;// sortedSubsInd.getRowPackedCopy();

        int max = sortedSubsObj.end();
        int len = max + 1;
        // int colLen = len;
        boolean cond = false;

        // Indices sizInternal = new Indices(new int[]{len, 1});
        int maxSz = 0;
        int minSz = 0;

        /*
         * if (this.sz != null) { if (sz.length() != 2) { msg =
         * "Indices \"sz\" must be a 2 element vector."; throw new
         * ConditionalRuleException("AccumArray", msg); } cond = (sz.start() <
         * len);//sizInternal.NEQ(sz).anyBoolean(); if (cond) { msg =
         * "Indices \"sz\" must be a 2 element vector of [ N >= " + len +
         * " x 1]."; throw new ConditionalRuleException("AccumArray", msg); }
         * maxSz = JDatafun.max(sz).start(); minSz = JDatafun.min(sz).start();
         * if (minSz != 1) { msg =
         * "Indices \"sz\" must be a 2 element vector of [ N >= " + len +
         * " x 1]."; throw new ConditionalRuleException("AccumArray", msg); } }
         * else { maxSz = len; }
         */

        // sz.printInLabel("sz");

        if (sz.start() > max)
        {
            maxSz = JDatafun.max(sz).start();
        }
        else
        {
            maxSz = len;
        }

        Matrix tmpMat = null;
        Indices tmpInd = null;

        if (val instanceof Matrix)
        {
            double doubleVal = 0.0;
            Matrix matVal = (Matrix) val;
            if (len > matVal.length())
            {
                // System.out.println(" Error message has been commented out.");
                msg = "The largest element of \"subs\" column indices can't be greater than the length of \"val\" matrix vector.";
                throw new ConditionalRuleException("AccumArray", msg);
            }
            // this.val = indVal.getEls(arr);
            Matrix accumMat = new Matrix(maxSz, 1, this.fillVal.doubleValue());

            for (int i = 0; i < sizList; i++)
            {
                int pos = list.get(i);
                arr = subs.EQ(pos).findIJ().getIndex();
                tmpMat = matVal.getEls(arr);
                fun.funfunc(1, tmpMat, (Object[]) null);
                doubleVal = (Double) fun.output()[0];// JDatafun.sum(tmpMat).start();
                accumMat.set(pos, 0, doubleVal);
            }
            A = accumMat;
        }
        else if (val instanceof Indices)
        {
            int integerVal = 0;
            Indices indVal = (Indices) val;
            if (len > indVal.length())
            {
                // System.out.println(" Error message has been commented out.");
                msg = "The largest element of \"subs\" column indices can't be greater than the length of \"val\" indices vector.";
                throw new ConditionalRuleException("AccumArray", msg);
            }
            // this.val = indVal.getEls(arr);
            Indices accumInd = new Indices(maxSz, 1, this.fillVal.intValue());

            for (int i = 0; i < sizList; i++)
            {
                int pos = list.get(i);
                arr = subs.EQ(pos).findIJ().getIndex();
                tmpInd = indVal.getEls(arr);
                fun.funfunc(1, tmpInd, (Object[]) null);
                integerVal = (Integer) fun.output()[0];// JDatafun.sum(tmpInd).start();
                accumInd.set(pos, 0, integerVal);
            }
            A = accumInd;
        }

    }

    /**
     * @return the A
     */
    public Object getA()
    {
        return A;
    }

    /**
     * @return the valmatrix
     */
    public boolean isValmatrix()
    {
        return valmatrix;
    }

    private static FunctionFunctions createFunSum()
    {
        FunctionFunctions sumFun = new FunFuncAbstract()
        {

            public void funfunc(int numOut, Object xxx, Object... inputArgs)
            {
                // System.out.println("Executing : snlogLfun");
                String msg = "";

                if (xxx == null)
                {
                    // throw new
                    // ConditionalException("funfunc : Parameter \"xx\" must be non-null.");
                    msg = " - funfunc : Parameter \"xxx\" must be non-null.";
                    throw new ConditionalRuleException("sumFun", msg);
                }
                boolean cond = !(xxx instanceof Matrix) && !(xxx instanceof Indices);
                if (cond)
                {
                    msg = " - funfunc : Parameter \"xxx\" must be an instance of \"Matrix\" or \"Indices\".";
                    throw new ConditionalRuleException("sumFun", msg);
                }

                Sum sumObj = null;
                if (xxx instanceof Matrix)
                {
                    Matrix xMat = (Matrix) xxx;
                    if (!xMat.isVector())
                    {
                        msg = " - funfunc : Matrix parameter \"xxx\" must be a vector.";
                        throw new ConditionalRuleException("sumFun", msg);
                    }
                    sumObj = new SumMat(xMat);
                    this.output = new Object[]
                    {
                        ((Matrix) sumObj.getSumObject()).start()
                    };
                }
                else
                {
                    Indices xInd = (Indices) xxx;
                    if (!xInd.isVector())
                    {
                        msg = " - funfunc : Indices parameter \"xxx\" must be a vector.";
                        throw new ConditionalRuleException("sumFun", msg);
                    }
                    sumObj = new SumInd(xInd);
                    this.output = new Object[]
                    {
                        ((Indices) sumObj.getSumObject()).start()
                    };
                }

                // //////////////////////////////////////////////////////////////

            }
        };
        sumFun.setName("sumFun");

        return sumFun;
    }

    public static FunctionFunctions createFunMax()
    {
        FunctionFunctions maxFun = new FunFuncAbstract()
        {

            public void funfunc(int numOut, Object xxx, Object... inputArgs)
            {
                // System.out.println("Executing : snlogLfun");
                String msg = "";

                if (xxx == null)
                {
                    // throw new
                    // ConditionalException("funfunc : Parameter \"xx\" must be non-null.");
                    msg = " - funfunc : Parameter \"xxx\" must be non-null.";
                    throw new ConditionalRuleException("maxFun", msg);
                }
                boolean cond = !(xxx instanceof Matrix) && !(xxx instanceof Indices);
                if (cond)
                {
                    msg = " - funfunc : Parameter \"xxx\" must be an instance of \"Matrix\" or \"Indices\".";
                    throw new ConditionalRuleException("maxFun", msg);
                }

                Max maxObj = null;
                if (xxx instanceof Matrix)
                {
                    Matrix xMat = (Matrix) xxx;
                    if (!xMat.isVector())
                    {
                        msg = " - funfunc : Matrix parameter \"xxx\" must be a vector.";
                        throw new ConditionalRuleException("maxFun", msg);
                    }
                    maxObj = new MaxMat(xMat);
                    this.output = new Object[]
                    {
                        ((Matrix) maxObj.getMaxObject()).start()
                    };
                }
                else
                {
                    Indices xInd = (Indices) xxx;
                    if (!xInd.isVector())
                    {
                        msg = " - funfunc : Indices parameter \"xxx\" must be a vector.";
                        throw new ConditionalRuleException("maxFun", msg);
                    }
                    maxObj = new MaxInd(xInd);
                    this.output = new Object[]
                    {
                        ((Indices) maxObj.getMaxObject()).start()
                    };
                }

                // //////////////////////////////////////////////////////////////

            }
        };
        maxFun.setName("maxFun");

        return maxFun;
    }

    private static int indEntry2Ele(int curRow, int curCol, int maxRow)
    {
        String msg = "";
        boolean cond = curRow <= 0 || curCol <= 0;
        if (cond)
        {
            msg = "Both integer parameters \"curRow\" (= " + curRow + ") and \"curCol\" (= " + curCol
                    + ") must be positive.\n" + "Try adding 1 to both integer parameters.";
            throw new ConditionalRuleException("indEntry2Ele", msg);
        }

        int colInd = 1;
        if (curCol == 1)
        {
            colInd = curRow;
        }
        else
        {
            colInd = (curCol - 1) * maxRow + curRow;
        }

        return colInd;
    }

    /**
     * @return the valindices
     */
    public boolean isValindices()
    {
        return valindices;
    }

    private static void Ex1()
    {
        int[] vv =
        {
                1, 2, 4, 2, 4
        };
        Indices subs = new Indices(vv);// .toColVector().minus(1);
        // subs = subs.toColVector();
        subs = subs.minus(1);
        subs.plus(1).printInLabel("subs");
        // double[] vv2 = {101, 102, 103, 104, 105};
        int[] vv2 =
        {
                101, 102, 103, 104, 105
        };
        Object val = new Indices(vv2).toColVector();// new
                                                    // Matrix(vv2).toColVector();
        ((Indices) val).printInLabel("val", 0);

        Indices sz2 = new Indices(new int[]
        {
                7, 1
        });

        AccumArray AC = new AccumArray(subs, val, sz2);

        Object A = AC.getA();
        ((Indices) A).printInLabel("A", 0);
    }

    private static void Ex2()
    {
        int[][] vv =
        {
                {
                        1, 1
                },
                {
                        2, 1
                },
                {
                        2, 3
                },
                {
                        2, 1
                },
                {
                        2, 3
                }
        };
        Indices subs = new Indices(vv);// .toColVector().minus(1);
        // subs = subs.toColVector();
        subs = subs.minus(1);
        subs.plus(1).printInLabel("subs");
        // double[] vv2 = {101, 102, 103, 104, 105};
        double[] vv2 =
        {
                101, 102, 103, 104, 105
        };
        Object val = new Matrix(vv2).toColVector();// new
                                                   // Matrix(vv2).toColVector();
        ((Matrix) val).printInLabel("val", 0);

        Indices sz2 = new Indices(new int[]
        {
                4, 5
        });

        AccumArray AC = new AccumArray(subs, val, sz2, AccumArray.createFunMax(), Double.NaN);

        Object A = AC.getA();
        ((Matrix) A).printInLabel("A", 0);
    }

    private static void Ex3()
    {
        int[] vv =
        {
                1, 2, 4, 2, 4
        };

        Indices subs = new Indices(vv);// .toColVector().minus(1);

        String filePathName = "C:/Users/Sione/Documents/MATLAB/datafiles/kmeansdata/";
        Matrix idx = Matrix.readMat(filePathName, "idx.txt");
        subs = idx.toIndices();

        // subs = subs.toColVector();
        subs = subs.minus(1);
        subs.plus(1).printInLabel("subs");
        // double[] vv2 = {101, 102, 103, 104, 105};
        int[] vv2 =
        {
            1
        };
        Object val = new Indices(vv2);// .toColVector();//new
                                      // Matrix(vv2).toColVector();
        ((Indices) val).printInLabel("val", 0);

        Indices sz2 = new Indices(new int[]
        {
                4, 1
        });

        AccumArray AC = new AccumArray(subs, 1, sz2);

        Object A = AC.getA();
        ((Indices) A).printInLabel("A", 0);
    }

    private static void Ex4()
    {
        int[] vv =
        {
                1, 2, 4, 2, 4
        };
        Indices subs = new Indices(vv);// .toColVector().minus(1);
        // subs = subs.toColVector();
        subs = subs.minus(1);
        subs.plus(1).printInLabel("subs");
        // double[] vv2 = {101, 102, 103, 104, 105};
        int[] vv2 =
        {
            101
        };// , 102, 103, 104, 105};
        Object val = new Indices(vv2);// .toColVector();//new
                                      // Matrix(vv2).toColVector();
        ((Indices) val).printInLabel("val", 0);

        Indices sz2 = new Indices(new int[]
        {
                7, 1
        });

        AccumArray AC = new AccumArray(subs, val, sz2);

        Object A = AC.getA();
        ((Indices) A).printInLabel("A", 0);
    }

    public static void main(String[] args)
    {
        Ex4();
    }
}
