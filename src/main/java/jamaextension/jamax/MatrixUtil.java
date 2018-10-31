/*
 * MatrixUtil.java
 *
 * Created on 26 November 2007, 17:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Feynman Perceptrons
 */
public final class MatrixUtil
{

    /** Creates a new instance of MatrixUtil */
    private MatrixUtil()
    {
    }

    public static boolean linSpacedVector(Matrix vector)
    {
        if (vector == null)
        {
            throw new IllegalArgumentException(
                    "linSpacedVector : Parameter \"vector\" can't be allowed to have null value.");
        }
        if (!vector.isVector())
        {
            throw new IllegalArgumentException(
                    "linSpacedVector : Parameter \"vector\" must be a vector and not a matirx.");
        }
        if (vector.isfinite().NOT().anyBoolean())
        {
            return false;
        }

        if (vector.length() == 2)
        {
            return true;
        }

        boolean equallySpaced = false;
        Matrix h = JDatafun.diff(vector);
        // eqsp = (norm(diff(h),Inf) <= eps(norm(x,Inf)));
        double temp = JDatafun.diff(h).normInf();
        double temp2 = vector.normInf() * MathUtil.EPS;
        equallySpaced = temp <= temp2;

        return equallySpaced;
    }

    public static Indices findMultiCollinear(Matrix X)
    {
        if (X == null)
        {
            throw new IllegalArgumentException(
                    "findMultiCollinearBasis : Parameter \"X\" (predictors or independent variables) must be non-null.");
        }
        if (X.isVector())
        {
            throw new IllegalArgumentException(
                    "findMultiCollinearBasis : Parameter \"X\" (predictors or independent variables) must be a matrix and not a vector.");
        }

        int n = X.getRowDimension();
        int k = X.getColumnDimension();

        Indices basis = new Indices(k, 1, false);
        basis.set(0, 0, true);

        for (int i = 1; i < k; i++)
        {
            int[] arr = basis.find().getColumnAt(0).getColumnPackedCopy();
            Matrix Z = X.getColumns(arr);// X(:,basis);
            Matrix temp = Z.transpose().times(Z).inverse();
            Matrix temp2 = Z.times(temp).times(Z.transpose());
            // orthogonal projector onto orthogonal complement of Col(Z)
            Matrix Q = Matrix.identity(n).minus(temp2);// Q = eye(n) -
                                                       // Z*inv(Z'*Z)*(Z');
            // this is a crude test criterion
            temp = X.getColumnAt(i);
            double val = Q.times(temp).norm2() / temp.norm2();
            boolean chk = (val >= 1.0E-10);// chk = norm(Q*X(:,i))/norm(X(:,i))
                                           // >= 1e-10;
            basis.set(i, 0, chk); // exclude any column that is (numerically)
                                  // linearly dependent on Z
        }// end for

        return basis;
    }

    public static Matrix scaledRandom(int[] rowColSize, double[] lowerUpperPercents, double scale)
    {
        if (rowColSize == null)
        {
            throw new IllegalArgumentException(
                    "scaledRandom : Parameter \"rowColSize\" must be non-null. It should be a 2-element array.");
        }
        if (rowColSize.length != 2)
        {
            throw new IllegalArgumentException(
                    "scaledRandom : Parameter \"rowColSize\" must be a 2-element array. First element is for row size and second is for column size of the random matrix.");
        }

        Indices Ind = new Indices(rowColSize);
        Ind = Ind.LTEQ(0).find();
        if (Ind != null)
        {
            throw new IllegalArgumentException(
                    "scaledRandom : Elements for array parameter \"rowColSize\" must be a positive numbers.");
        }

        if (lowerUpperPercents == null)
        {
            throw new IllegalArgumentException(
                    "scaledRandom : Parameter \"lowerUpperPercents\" must be non-null. It should be a 2-element array.");
        }
        if (lowerUpperPercents.length != 2)
        {
            throw new IllegalArgumentException(
                    "scaledRandom : Parameter \"lowerUpperPercents\" must be a 2-element array. First element is for % lower-bound and second is for upper-bound % of scaled-value of the random matrix.");
        }

        Matrix lub = new Matrix(lowerUpperPercents);
        Ind = lub.LT(0.0).OR(lub.GT(100.0)).find();
        if (Ind != null)
        {
            throw new IllegalArgumentException(
                    "scaledRandom : Elements for array parameter \"lowerUpperPercents\" must be a percentage number in the closed interval [0, 100].");
        }
        if (lowerUpperPercents[0] >= lowerUpperPercents[1])
        {
            throw new IllegalArgumentException(
                    "scaledRandom : The first element for array parameter \"lowerUpperPercents\" must be than the second element.");
        }

        if (scale < 0.0)
        {
            throw new IllegalArgumentException("scaledRandom : Parameter \"scale\" must be a positive number.");
        }

        Matrix mat = new Matrix(rowColSize[0], rowColSize[1]);
        double low = lowerUpperPercents[0] / 100.0;
        double upper = lowerUpperPercents[1] / 100.0;
        double val = 0.0;
        // double val

        for (int i = 0; i < rowColSize[0]; i++)
        {
            for (int j = 0; j < rowColSize[1]; j++)
            {
                val = Math.random();
                while (val < low || val > upper)
                {
                    val = Math.random();
                }
                mat.set(i, j, val * scale);
            }
        }
        return mat;
    }

    public static Matrix scaledRandom(int row, int col, double scale)
    {
        return scaledRandom(row, col, 0.0, scale);
    }

    public static Matrix scaledRandom(int row, int col, double center, double scale)
    {
        if (scale <= 0.0)
        {
            throw new IllegalArgumentException("scaledRandomN : Parameter \"scale\" must be a positive number.");
        }
        Matrix randMat = new Matrix(row, col);
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                double val = center + scale * Math.random();
                randMat.set(i, j, val);
            }
        }
        return randMat;
    }

    public static Matrix scaledRandomN(int row, int col, double scale)
    {
        return scaledRandomN(row, col, 0.0, scale);
    }

    public static Matrix scaledRandomN(int row, int col, double center, double scale)
    {
        if (scale <= 0.0)
        {
            throw new IllegalArgumentException("scaledRandomN : Parameter \"scale\" must be a positive number.");
        }
        Matrix randMat = new Matrix(row, col);
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                double val = center + scale * MathUtil.randn();
                randMat.set(i, j, val);
            }
        }
        return randMat;
    }

    public static void checkMatrix(double[][] A)
    {
        int m = A.length;
        int n = A[0].length;
        for (int i = 0; i < m; i++)
        {
            if (A[i].length != n)
            {
                throw new IllegalArgumentException("checkMatrix : All rows must have the same length.");
            }
        }
    }

    public static void printMatrixArrays(Matrix[] array)
    {
        printMatrixArrays(array, 8, 4);
    }

    public static void printMatrixArrays(Matrix[] array, int i, int j)
    {
        if (array == null)
        {
            System.out.println(" Null array.");
            return;
        }

        System.out.println("\n");
        int len = array.length;
        for (int k = 0; k < len; k++)
        {
            Matrix mat = array[k];
            if (mat != null)
            {
                System.out.println("---------------- array[" + k + "] ----------------");
                mat.print(i, j);
            }
            else
            {
                System.out.println("---------------- array[" + k + "] : NULL");
            }
            System.out.println("\n");
        }

    }

    

    /**
     * A static method for testing only. It must be removed when it migrates to
     * J#.
     * 
     * @return Data that has been read from Excel test file.
     */
   
    public static Matrix rem(Matrix X, Matrix Y)
    {
        int xR = X.getRowDimension();
        int xC = X.getColumnDimension();

        int yR = Y.getRowDimension();
        int yC = Y.getColumnDimension();

        if (xR != yR || xC != yC)
        {
            throw new IllegalArgumentException("rem : Parameters \"X\" and \"Y\" must have equall dimensions.");
        }

        Matrix REM = new Matrix(xR, xC);
        double val = 0.0;
        double xx = 0.0;
        double yy = 0.0;

        for (int i = 0; i < xR; i++)
        {
            for (int j = 0; j < xC; j++)
            {
                xx = X.get(i, j);
                yy = Y.get(i, j);
                val = MathUtil.rem(xx, yy);
                REM.set(i, j, val);
            }
        }

        return REM;
    }

    public static Matrix rem(Matrix X, double Y)
    {
        int xR = X.getRowDimension();
        int xC = X.getColumnDimension();

        Matrix REM = new Matrix(xR, xC);
        double val = 0.0;
        double xx = 0.0;
        double yy = 0.0;

        for (int i = 0; i < xR; i++)
        {
            for (int j = 0; j < xC; j++)
            {
                xx = X.get(i, j);
                val = MathUtil.rem(xx, Y);
                REM.set(i, j, val);
            }
        }

        return REM;
    }

    public static Map<? extends Number, ? extends Object> sortMapWithNumberAsKey(
            Map<? extends Number, ? extends Object> map)
    {
        Set<? extends Number> numSet = map.keySet();
        List<Number> numList = new ArrayList<Number>(numSet);
        List<Double> dList = Matrix.numberArrayList2Double(numList);
        Map<Number, Object> mapSort = new LinkedHashMap<Number, Object>();
        int len = dList.size();

        for (int i = 0; i < len; i++)
        {
            Number num = dList.get(i);
            Object obj = map.get(num);
            mapSort.put(num, obj);
        }

        return mapSort;
    }

    public static List<String> asciiOnly(List<String> list)
    {
        int siz = list.size();
        List<String> asciiList = new ArrayList<String>();

        for (int i = 0; i < siz; i++)
        {
            String LI = list.get(i);
            boolean tf = isPureAscii(LI);
            if (tf)
            {
                asciiList.add(LI);
            }
        }

        return asciiList;
    }

    public static boolean isPureAscii(String v)
    {
        byte bytearray[] = v.getBytes();
        CharsetDecoder d = Charset.forName("US-ASCII").newDecoder();
        try
        {
            CharBuffer r = d.decode(ByteBuffer.wrap(bytearray));
            r.toString();
        }
        catch (CharacterCodingException e)
        {
            return false;
        }
        return true;
    }

    public static List<?> getSubList(List<?> list, int[] ind)
    {
        List listRet = new ArrayList();

        int siz = list.size();
        for (int i = 0; i < siz; i++)
        {
            Object obj = list.get(ind[i]);
            listRet.add(obj);
        }

        return listRet;
    }

    public static List<? extends Number> stringListToNumList(List<String> list)
    {
        return stringListToNumList(list, true);
    }

    public static List<? extends Number> stringListToNumList(List<String> list, boolean integer)
    {
        List<Number> numList = new ArrayList<Number>();
        int siz = list.size();

        for (int i = 0; i < siz; i++)
        {
            String strI = list.get(i);
            Number numI = null;
            if (integer)
            {
                numI = Integer.parseInt(strI);
            }
            else
            {
                numI = Double.parseDouble(strI);
            }
            numList.add(numI);
        }

        return numList;
    }

    public static List<String> numListToStringList(List<? extends Number> num)
    {
        List<String> listStr = new ArrayList<String>();
        int siz = num.size();
        for (int i = 0; i < siz; i++)
        {
            String DF = num.get(i).toString();
            listStr.add(DF);
        }
        return listStr;
    }

    public static Map<? extends Number, ? extends Object> toNumberAsKeyMap(List<? extends Number> num,
            List<? extends Object> val)
    {
        if (num == null || num.isEmpty())
        {
            throw new ConditionalRuleException("First collection input argument must be non-null or non-empty.");
        }
        int siz = num.size();
        boolean tf = (val == null || val.isEmpty());

        Map<Number, Object> map = new LinkedHashMap<Number, Object>();

        if (tf)
        {
            for (int i = 0; i < siz; i++)
            {
                Number nI = num.get(i);
                map.put(nI, null);
            }
            return map;
        }

        int siz2 = val.size();
        if (siz != siz2)
        {
            throw new ConditionalRuleException("The sizes of the input arguments must be the same.");
        }

        for (int i = 0; i < siz; i++)
        {
            Number nI = num.get(i);
            Object obj = val.get(i);
            map.put(nI, obj);
        }

        return map;
    }

    public static List<Object> toObjectList(List<?> list)
    {
        int siz = list.size();
        List<Object> objList = new ArrayList<Object>();

        for (int i = 0; i < siz; i++)
        {
            objList.add(list.get(i));
        }

        return objList;
    }

    public static void main(String[] args)
    {
        Matrix R1 = scaledRandom(new int[]
        {
                1, 6
        }, new double[]
        {
                2.0, 8.0
        }, 1.0);
        System.out.println("-------------- R1 --------------");
        R1.print(4, 4);

        /*
         * int row = 5; int col = 4; double center = 100.0; double scale = 10.0;
         * 
         * Matrix R1 = scaledRandom( row, col, center, scale); Matrix R2 =
         * scaledRandomN( row, col, center, scale);
         * 
         * System.out.println("-------------- R1 --------------");
         * R1.print(4,0);
         * 
         * System.out.println("-------------- R2 --------------");
         * R2.print(4,0);
         * 
         * double[][] xx = { {14, 7, 14, 6}, { 3, 0, 11, 13}, { 9, 12, 3, 1}, {
         * 7, 7, 6, 5}, {13, 9, 14, 12},
         * 
         * {11, 12, 14, 0} };
         * 
         * Matrix X = new Matrix(xx); Indices basis =
         * MatrixUtil.findMultiCollinear(X);
         * System.out.println("-------------- basis --------------");
         * basis.print(4);
         * 
         * 
         * boolean eqSpaced = false; Matrix X =
         * Matrix.linIncrement(0.0,10.0,1.0); if(!eqSpaced){ X.set(0,1,1.5); }
         * 
         * eqSpaced = linSpacedVector(X);
         * 
         * System.out.println("eqSpaced = "+eqSpaced);
         */

    }
}
