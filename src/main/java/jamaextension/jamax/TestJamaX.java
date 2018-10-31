/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.netlib.lapack.DGESVD;
import org.netlib.lapack.Dgesvd;

import jamaextension.jamax.funfun.FminSearch;
import jamaextension.jamax.funfun.FunFuncAbstract;

/**
 * 
 * @author Feynman Perceptrons
 */
public class TestJamaX
{

    static void testStringFormat()
    {

        /*
         * double[][] xx = {{0.2017, 0.6650, 0.1512, 1.0083, 1.1092, 0.7562,
         * 1.0083, 1.4117, 0.4638, 0.5042, 0.3025, 0.5546}}; Matrix xr = new
         * Matrix(xx).transpose(); xr.printInLabel("xr");
         * 
         * System.out.println("");
         * 
         * Matrix x = xr.reshape(3, 4); x.printInLabel("x");
         */

        int itercount = 54;
        int func_evals = 78;
        double fv = Math.sqrt(3.0);
        String how = "simplex";

        String formatted = String.format(" %5.0f        %5.0f     %12.6g         %s", itercount, func_evals, fv, how);
        System.out.println("" + formatted);

        // String str = "000410";
        // String stripPreZeros = stripPrecedingZeros( str);
        // System.out.println(" stripPreZeros = " + stripPreZeros);

        // testRefelctFunc();

        testLinpackSVD();

        /*
         * double val = 0.001035210292393; Matrix a = new Matrix(1,1,val);
         * CholeskyDecomposition chol = new CholeskyDecomposition(a);
         * 
         * Matrix b = chol.getLR();
         * 
         * System.out.println("--------------- b ---------------" ); b.print(8,
         * 8); System.out.println(" Positive Definite = "+ chol.isSPD() );
         */

    }

    private static void testRefelctFunc()
    {
        Class matClass = Matrix.class;
        Method[] meth = matClass.getDeclaredMethods();
        Field[] fields = matClass.getFields();
        int lenMet = fields.length;
        for (int i = 0; i < lenMet; i++)
        {
            String str = fields[i].getName();
            System.out.println("" + str);
        }
        // meth[0].invoke(meth, meth);

        Matrix A = Matrix.random(4, 5).arrayTimes(10.0).round();
        Matrix B = Matrix.random(5, 3).arrayTimes(10.0).round();
        Matrix C = A.times(B);
        // C.printInLabel("C1", 0);
        Object[] sortR = A.sortRows();
        Matrix sortA = (Matrix) sortR[0];
        sortA.printInLabel("sortA", 0);

        Method method = null;// = meth[0].
        try
        {
            method = matClass.getMethod("sortRows");// , (Class[]) null);

        }
        catch (NoSuchMethodException nsme)
        {
        }

        System.out.println(" method : " + method.toString());

        if (true)
        {
            // return;
        }

        if (method != null)
        {

            Object Cobj = null;
            try
            {
                Cobj = method.invoke(A);
            }
            catch (IllegalAccessException iae)
            {
            }
            catch (InvocationTargetException ite)
            {
            }

            Object[] objArr = (Object[]) Cobj;
            C = (Matrix) objArr[0];
            C.printInLabel("sortA", 0);

        }

    }

    private static void testLinpackSVD()
    {
        /*
         * double[][] m = { {18.91, -1.59, -1.59}, {14.91, -1.59, 1.59}, {-6.15,
         * -2.25, 0}, {-18.15, -1.59, 1.59}, {27.5, -2.25, 0}};
         */

        double[][] m =
        {
                {
                        10, 3, 1, 1, 3, 11
                },
                {
                        11, 11, 12, 7, 7, 4
                },
                {
                        11, 0, 10, 6, 7, 10
                },
                {
                        6, 4, 5, 11, 10, 10
                },
                {
                        10, 1, 14, 12, 11, 2
                }
        };

        Matrix mat = new Matrix(m).transpose();
        m = mat.getArray();

        int M = m.length;
        int N = m[0].length;
        double[] s = new double[m.length];
        double[][] u = new double[M][M];
        double[][] vt = new double[N][N];
        double[] work = new double[Math.max(3 * Math.min(M, N) + Math.max(M, N), 5 * Math.min(M, N))];
        org.netlib.util.intW info = new org.netlib.util.intW(2);

        // The following line is commented out as the original static class
        // DGESVD has less number of arguments in it's static DGESVD method.
        //DGESVD.DGESVD("A", "A", M, N, m, s, u, vt, work, work.length, info);

        System.out.println("info = " + info.val);

        Matrix U = new Matrix(u);
        int minMN = Math.min(M, N);
        if (minMN < M)
        {
            int[] arr = Indices.linspace(0, minMN - 1).getRowPackedCopy();
            U = U.getColumns(arr);
        }
        U.printInLabel("U");

        Matrix S = new Matrix(s).diag();
        S.printInLabel("S");

        Matrix V = new Matrix(vt);
        V.transpose().printInLabel("V");

        // Matrix W = new Matrix(work);
        // W.printInLabel("W");

    }

    /**
     * This method strips strings which start have zeros character preceding
     * non-zero's. eg: 000543 -> 543 , 087 -> 87 , 0900 -> 900 , 654 -> 654
     * 
     * @param str
     * @return
     */
    public static String stripPrecedingZeros(String str)
    {
        if (str == null)
        {
            return null;
        }
        if ("".equals(str))
        {
            return "";
        }
        if (str.length() == 1)
        {
            return (str + "");
        }

        // String rev = new StringBuffer(str+"").reverse().toString();

        char[] ch = str.toCharArray();
        int len = ch.length;
        int count = 0;

        for (int i = 0; i < len; i++)
        {
            /*
             * if(i==(len-1)){ strBuf.append(ch[i]); } else{ if(ch[i]!='0'){ } }
             */
            if (ch[i] != '0')
            {
                count = i;
                break;
            }
        }

        StringBuffer strBuf = new StringBuffer();
        for (int i = count; i < len; i++)
        {
            strBuf.append(ch[i]);
        }

        return strBuf.toString();
    }

    static void testFminSearch()
    {
        FunFuncAbstract funfcn = new FunFuncAbstract()
        {

            public void funfunc(int numOut, Object xxx, Object... varagin)
            {
                Matrix x = (Matrix) xxx;
                if (x == null)
                {
                    throw new ConditionalException("funfunc : Matrix parameter \"x\" must be non-null.");
                }
                if (!x.isVector())
                {
                    throw new ConditionalException("funfunc : Matrix parameter \"x\" must be and not a matrix.");
                }
                if (x.length() > 2)
                {
                    throw new ConditionalException("funfunc : Matrix parameter \"x\" must be a vector with 2 elements.");
                }

                double a = 1.5;
                if (varagin != null && varagin.length > 0)
                {
                    a = ((Double) varagin[0]).doubleValue();
                }

                double val = 0.0; // Math.pow(x.start(), 2.0) + a *
                                  // Math.pow(x.getElementAt(1),
                                  // 2.0);//a*x(2)^2;
                double val2 = 0.0;

                double x1 = x.start();
                double x2 = x.end();

                val = x2 - x1 * x1;
                val = 100.0 * val * val;

                a = Math.sqrt(2.0);

                val2 = a - x1;
                val2 = val2 * val2;

                val = val + val2;

                this.output = new Object[]
                {
                    new Matrix(1, 1, val)
                };
            }
        };
        double[] xarr =
        {
                -1.2, 1
        };
        Matrix x = new Matrix(xarr);

        FminSearch fmins = new FminSearch(funfcn, x);
    }

    static void testLinsolve()
    {
        double[][] arr =
        {
                {
                        0.1225, 0.6141, 0.9540, 0.2590, 0.8979, 0.4519, 0.2096
                },
                {
                        0.9734, 0.6076, 0.3569, 0.2515, 0.2446, 0.2636, 0.1137
                },
                {
                        0.1883, 0.1259, 0.8381, 0.4243, 0.7606, 0.6655, 0.5470
                },
                {
                        0.3513, 0.5529, 0.9743, 0.8130, 0.6949, 0.1166, 0.0061
                }
        };

        Matrix A = new Matrix(arr);
        A.printInLabel("A");
        System.out.println(" ");

        double[] xarr =
        {
                0.1760, 0.5994, 0.4319, 0.0825, 0.6874, 0.3753, 0.8362
        };
        Matrix x = new Matrix(xarr).transpose();

        x.printInLabel("x");
        System.out.println(" ");
        Matrix b = A.times(x);

        b.printInLabel("b");
        System.out.println(" ");

        Matrix Y = A.solve(b);

        Y.printInLabel("Y");
    }

    static void conditionEx()
    {
        double[][] x =
        {
                {
                        0.6557, 0.7431, 0.2769, 0.9502, 0.1869, 0.2760
                },
                {
                        0.0357, 0.3922, 0.0462, 0.0344, 0.4898, 0.6797
                },
                {
                        0.8491, 0.6555, 0.0971, 0.4387, 0.4456, 0.6551
                },
                {
                        0.9340, 0.1712, 0.8235, 0.3816, 0.6463, 0.1626
                },
                {
                        0.6787, 0.7060, 0.6948, 0.7655, 0.7094, 0.1190
                },
                {
                        0.7577, 0.0318, 0.3171, 0.7952, 0.7547, 0.4984
                }
        };

        Matrix xmat = new Matrix(x);
        double cond = xmat.cond(1);
        System.out.println("cond = " + cond);
        System.out.println("rcond = " + (1.0 / cond));
    }

    static void testWrite()
    {
        double[][] x =
        {
                {
                        0.6557, 0.7431, 0.2769, 0.9502, 0.1869, 0.2760
                },
                {
                        0.0357, 0.3922, 0.0462, 0.0344, 0.4898, 0.6797
                },
                {
                        0.8491, 0.6555, 0.0971, 0.4387, 0.4456, 0.6551
                },
                {
                        0.9340, 0.1712, 0.8235, 0.3816, 0.6463, 0.1626
                },
                {
                        0.6787, 0.7060, 0.6948, 0.7655, 0.7094, 0.1190
                },
                {
                        0.7577, 0.0318, 0.3171, 0.7952, 0.7547, 0.4984
                }
        };

        Matrix xmat = new Matrix(x);
        String filePath = "C:/Feynmance/NZFunds/sioneArray.txt";
        try
        {
            Matrix.write(xmat.toColVector(), filePath);
        }
        catch (IOException ex)
        {
            Logger.getLogger(TestJamaX.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            // Read
            xmat = Matrix.read(filePath);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(TestJamaX.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(TestJamaX.class.getName()).log(Level.SEVERE, null, ex);
        }
        xmat.printInLabel("xmat");
    }

    static void testReadColMat()
    {
        String strMat = "[-3 4 6; 7 -2 9; 12 1 5; -6 -10 8]";
        /*
         * int first = strMat.indexOf('['); int second = strMat.indexOf(']'); if
         * (first != -1) { if (second != -1) { strMat = strMat.substring(first +
         * 1, second); } else { strMat = strMat.substring(first + 1); } } else {
         * if (second != -1) { strMat = strMat.substring(0, second); } else {
         * strMat = strMat + ""; } } System.out.println("first = " + first);
         * System.out.println("second = " + second);
         */

        Matrix str2mat = Matrix.readSemiColMat(strMat);
        str2mat.printInLabel("str2mat", 0);
    }

    static void testLongHamming()
    {
        long A = 25412563587L;
        long B = 31254752120L;

        long dist = 0;
        long val = A ^ B;
        System.out.println("val = " + val);

        // Count the number of set bits
        while (val != 0)
        {
            ++dist;
            // val &= val - 1;
            val = val & (val - 1);
        }

        int D = (int) dist;
        System.out.println("D = " + D);

    }

    static void testZeroingInfData()
    {
        double[] val =
        {
                Double.NaN, Double.NaN, 4
        };
        Matrix A = new Matrix(val);
        A.printInLabel("Anan");

        A.setInfTo(0.0);
        A.printInLabel("ANoNan");
    }

    static void testDiagReflection()
    {
        
        Matrix randMat = Matrix.rand(5, 6).scale(10).round();
        randMat.printInLabel("X",0);
        Matrix B = randMat.reflectOnMainDiag();
        B.printInLabel("B",0);
    }

    public static void main(String[] args)
    {

        Matrix test = TestData.testMat1();
        test.sizeIndices().printInLabel("test-size");
        test.printInLabel("test");
        
        Matrix test2 = test.toRowVector();
        test2.printInLabel("test2");
        
        //testDiagReflection();
        //testZeroingInfData();
        // testLongHamming();

        // testReadColMat();

        /*
         * double[][] xr = {{4, 2, -3, 0}, {16, -1, -9, 5}, {6, 15, -1, -4},
         * {-24, -2, -11, -3}, {1, 2, -12, -4}};
         * 
         * Matrix x = new Matrix(xr); x.printInLabel("x", 0);
         * 
         * Matrix3D xs = x.reshape(2, 5, 1); xs.print3dArray("xs", 6, 0);
         * 
         * 
         * double[][] a = {{8, 1, 6}, {3, 5, 7}, {4, 9, 2}}; Matrix A = new
         * Matrix(a); A.printInLabel("A", 6, 0);
         * 
         * double[][] b = {{1, 1, 1}, {1, 2, 3}, {1, 3, 6}}; Matrix B = new
         * Matrix(b); B.printInLabel("B", 6, 0); Matrix B1 = B.getRows(new
         * int[]{0, 1}); B1.printInLabel("B1", 6, 0);
         * 
         * int[][] d = {{1, 1, 1}, {1, 2, 3}, {1, 3, 6}}; Indices D = new
         * Indices(d);
         * 
         * Cell C = new Cell(2, 2); C.setElementAt(0, A); C.setElementAt(1, new
         * Matrix()); C.setElementAt(3, B1.transpose());
         * 
         * Matrix cat = C.horzcat(); cat.printInLabel("cat", 6, 0);
         */

        // Matrix3D cat = (Matrix3D) C.cat(Dimension.PAGE);
        // cat.printInLabel("cat", 0);
        // cat.print3dArray("cat", 6, 0);

        // Object obj = opt.optimget("Display", defOpt) ;

        /*
         * testRefelctFunc();
         * 
         * OptimOpt opt = new OptimOpt(); Class optimClass = opt.getClass();
         * Field[] fields = optimClass.getFields(); int len = fields.length;
         * System.out.println("len = " + len); for (int i = 0; i < len; i++) {
         * System.out.println("field[" + i + "] = " + fields[i].getName()); }
         */

        /*
         * Matrix A = new Matrix(2, 3, 5.0); A.printInLabel("A", 0); Matrix B =
         * new Matrix(4, 2, -3); B.printInLabel("B", 0); Matrix C = new
         * Matrix(3, 1, 2.0); C.printInLabel("C", 0);
         * 
         * Matrix BLK = A.blkdiag(B, C); BLK.printInLabel("BLK", 0);
         * 
         * 
         * double[][] aa = {{16, 13, 19, 19, 8}, {18, 2, 19, 10, 18}, {3, 6, 3,
         * 16, 16}, {18, 11, 19, 3, 19}};
         * 
         * Matrix A = new Matrix(aa); A.printInLabel("A", 0);
         * 
         * Matrix L = new Matrix(new double[]{10.0, 3.0});
         * 
         * Indices AeqL = A.EQ(L); AeqL.printInLabel("AeqL");
         * 
         * Indices LeqA = L.EQ(A); LeqA.printInLabel("LeqA");
         * 
         * int n = 15; int m = 8; int k = n / m; System.out.println("k = " + k);
         */
        // testWrite();
        // conditionEx() ;
        // testLinsolve();
        // String name = "SionePalu";
        // String sub = name.substring(0, 1);
        // System.out.println("sub = " + sub);
    }
}
