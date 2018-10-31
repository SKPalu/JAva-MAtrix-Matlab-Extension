/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.demos;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public class NumGrid
{

    private Object G;

    public NumGrid(char R, int n)
    {
        this(R, n, Matrix.class);
    }

    public NumGrid(char RR, int n, Class cls)
    {
        String clsName = cls.getSimpleName();
        String matCls = Matrix.class.getSimpleName();
        String indCls = Indices.class.getSimpleName();
        // System.out.println("clsName = " + clsName);
        // System.out.println("matCls = " + matCls);
        // System.out.println("indCls = " + indCls);

        boolean cond = !clsName.equals(matCls);// Matrix;
        cond = cond && !clsName.equals(indCls);
        if (cond)
        {
            throw new ConditionalException(
                    "NumGrid : Class type parameter \"cls\" must be a \"Matrix\" or \"Indices\".");
        }

        char R = ("" + RR).toUpperCase().charAt(0);

        if (R == 'N')
        {
            G = DemoUtil.nested(n);
        }
        else
        {
            if (n < 1)
            {
                throw new ConditionalException("NumGrid : Integer parameter \"n = " + n
                        + "\" must be a \"Matrix\" or \"Indices\".");
            }
            Indices ind1 = null;
            Indices ind2 = null;
            Indices ind3 = null;
            Matrix tmp = null;
            Matrix tmp2 = null;
            Matrix x = null;
            Matrix y = null;
            // x = ones(n,1)*[-1, (-(n-3):2:(n-3))/(n-1), 1];
            if (n == 1)
            {
                x = new Matrix(new double[][]
                {
                    {
                            -1, 1
                    }
                });
            }
            else if (n == 2)
            {
                x = new Matrix(new double[][]
                {
                        {
                                -1, 1
                        },
                        {
                                -1, 1
                        }
                });
            }
            else if (n == 3)
            {
                x = new Matrix(new double[][]
                {
                    {
                            -1, 0, 1
                    }
                });
                x = x.repmat(3, 1);
            }
            else
            {
                double lower = -(n - 3.0);
                double upper = (n - 3.0);
                double incr = 2.0;
                tmp = Matrix.linIncrement(lower, upper, incr);
                tmp = tmp.arrayRightDivide((double) n - 1.0);
                // tmp.printInLabel("tmp");

                int len = tmp.length();
                // int[] arr = Indices.linspace(1, len - 2).getRowPackedCopy();
                x = new Matrix(1, len + 2);
                x.set(0, 0, -1.0);
                len = x.length();
                x.set(0, len - 1, 1.0);
                int[] arr = Indices.linspace(1, len - 2).getRowPackedCopy();
                x.setElements(arr, tmp);

                x = x.repmat(n, 1);
            }

            // x.printInLabel("x");

            y = x.transpose().flipUD();// flipud(x');
            // y.printInLabel("y");

            if (R == 'S')
            { // G = (x > -1) & (x < 1) & (y > -1) & (y < 1);
                ind1 = x.GT(-1.0).AND(x.LT(1.0));
                ind2 = y.GT(-1.0).AND(y.LT(1.0));
                G = ind1.AND(ind2);
            }
            else if (R == 'L')
            { // G = (x > -1) & (x < 1) & (y > -1) & (y < 1) & ( (x > 0) | (y >
              // 0));
                ind1 = x.GT(-1.0).AND(x.LT(1.0));
                ind2 = y.GT(-1.0).AND(y.LT(1.0));
                ind3 = x.GT(0.0).OR(y.GT(0.0));
                G = ind1.AND(ind2).AND(ind3);
            }
            else if (R == 'C')
            { // G = (x > -1) & (x < 1) & (y > -1) & (y < 1) &
              // ((x+1).^2+(y+1).^2 > 1);
                ind1 = x.GT(-1.0).AND(x.LT(1.0));
                ind2 = y.GT(-1.0).AND(y.LT(1.0));
                tmp = x.plus(1.0);
                tmp = JElfun.pow(tmp, 2.0);
                tmp2 = y.plus(1.0);
                tmp2 = JElfun.pow(tmp2, 2.0);
                tmp = tmp.plus(tmp2);
                ind3 = tmp.GT(1.0);
                G = ind1.AND(ind2).AND(ind3);
            }
            else if (R == 'D')
            { // G = x.^2 + y.^2 < 1;
                tmp = JElfun.pow(x, 2.0);
                tmp2 = JElfun.pow(y, 2.0);
                tmp = tmp.plus(tmp2);
                G = tmp.LT(1.0);
            }
            else if (R == 'A')
            { // G = ( x.^2 + y.^2 < 1) & ( x.^2 + y.^2 > 1/3);
                tmp = JElfun.pow(x, 2.0);
                tmp2 = JElfun.pow(y, 2.0);
                tmp = tmp.plus(tmp2);
                ind1 = tmp.LT(1.0);
                ind2 = tmp.GT(1.0 / 3.0);
                G = ind1.AND(ind2);
            }
            else if (R == 'H')
            {// G = (x.^2+y.^2).*(x.^2+y.^2-SIGMA*y) < RHO*x.^2;
                double RHO = 0.75;
                double SIGMA = 0.75;
                tmp = JElfun.pow(x, 2.0);
                tmp2 = JElfun.pow(y, 2.0);
                tmp = tmp.plus(tmp2);

                tmp2 = y.arrayTimes(SIGMA);
                tmp2 = tmp.minus(tmp2);

                tmp = tmp.arrayTimes(tmp2);

                tmp2 = JElfun.pow(x, 2.0);
                tmp2 = tmp2.arrayTimes(RHO);

                G = tmp.LT(tmp2);
            }
            else if (R == 'B')
            {
                Matrix t = JElfun.atan2(y, x);// t = atan2(y,x);

                tmp = JElfun.pow(x, 2.0);
                tmp2 = JElfun.pow(y, 2.0);
                tmp = tmp.plus(tmp2);
                Matrix r = JElfun.sqrt(tmp);// r = sqrt(x.^2 + y.^2);

                tmp = t.arrayTimes(8.0);
                tmp = JElfun.sin(tmp);
                tmp = tmp.arrayTimes(0.2);
                tmp2 = t.arrayTimes(2.0);
                tmp2 = JElfun.sin(tmp2);
                ind1 = r.GTEQ(tmp2);
                tmp = tmp.plus(ind1);
                ind1 = tmp.logical();
                ind2 = x.GT(-1.0).AND(x.LT(1.0));
                ind3 = y.GT(-1.0).AND(y.LT(1.0));
                // G = (r >= sin(2*t) + .2*sin(8*t)) & ...
                // (x > -1) & (x < 1) & (y > -1) & (y < 1);
                G = ind1.AND(ind2).AND(ind3);
            }
            else
            {
                // error('MATLAB:numgrid:InvalidRegionType','Invalid region
                // type.');
                throw new ConditionalException("NumGrid : Region type \"R = " + R + "\" is invalid.");
            }// end

            // if (true) {
            // throw new ConditionalException("NumGrid : To Do #2.");
            // }

            FindInd findK = ((Indices) G).findIJ();// k = find(G);
            int[] siz = ((Indices) G).sizeIntArr();
            G = new Matrix(siz[0], siz[1]);// G = zeros(size(G)); // Convert
                                           // from logical to double matrix
            Matrix L = Matrix.linIncrement(1.0, (double) findK.numel(), 1.0);
            siz = findK.getIndex();
            ((Matrix) G).setElements(siz, L);// G(k) = (1:length(k))';
        }
    }

    /**
     * @return the G
     */
    public Object getG()
    {
        return G;
    }

    public static void main(String[] args)
    {
        NumGrid ng = new NumGrid('C', 15);
        Matrix G = (Matrix) ng.getG();
        G.printInLabel("G", 0);
    }
}
