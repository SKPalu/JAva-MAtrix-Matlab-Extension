/*
 * Interpolation1D.java
 *
 * Created on 18 October 2007, 22:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.MatrixUtil;
import jamaextension.jamax.constants.InterpMethod;
import jamaextension.jamax.datafun.HistogramCount;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.elfun.JElfun;

/**
 * <p>
 * Interpolation1D is a 1D interpolation (table lookup):
 * </p>
 * <CODE>
 *    
 *    public static void main(String[] args){
 *    
 *       boolean equallySpaced = false;
 *       Matrix X  = Matrix.linIncrement(0.0,10.0,1.0);
 *       if(!equallySpaced){
 *         X.set(0,1,1.5);
 *        }
 *       
 *       Matrix Y  = JElfun.sin(X);
 *       Matrix XI = Matrix.linIncrement(0.0, 10.0,0.5);
 *       
 *       Interpolation1D  Itp1D = new Interpolation1D(X, Y, XI);
 *       Itp1D.setEquallySpaced(equallySpaced);
 *       Itp1D.build();
 *       Matrix YI = Itp1D.getInterpValues();
 *              
 *       Matrix R = XI.transpose().mergeH(YI);
 *       
 *       System.out.println("----- R -----");
 *       R.print(4,4);     
 *     }     
 * </CODE>
 * 
 * <p>
 * Interpolation1D class interpolates to find <B>YI</B>, the values of the
 * underlying function <B>Y</B> at the points in the vector, or array <B>XI</B>.
 * Parameter <B>X</B> must be a vector of length N, and so length of <B>Y</B>
 * must be also N. This class can only interpolate a set of data-pairs (<B>X</B>
 * , <B>Y</B>) , where values in <B>X</B> are equally spaced.
 * </p>
 * 
 * @author Feynman Perceptrons
 * @version 1.0 * @deprecated Use class <B>Interp1</B> instead.
 */
public class Interpolation1D
{

    private InterpMethod method = InterpMethod.LINEAR;
    private Matrix X;
    private Matrix Y;
    private Matrix XI;
    private Matrix YI;
    private boolean eqsp;
    private Indices k;
    private Matrix h;
    /*----- addded on 17/09/2009 ------
     * The method is used for extrapolation
     */
    private Object extrapMethod = InterpFlag.Extrap;

    // default value for 'spline' and 'pchip' methods
    private boolean hasBuilt = false;

    /**
     * 
     * @param Y
     * @param XI
     */
    public Interpolation1D(Matrix Y, Matrix XI)
    {
        this(null, Y, XI);
    }

    public Interpolation1D(Matrix X, Matrix Y, double xi)
    {
        this(X, Y, new Matrix(1, 1, xi));
    }

    /**
     * Creates a new instance of Interpolation1D
     * 
     * @param X
     * @param Y
     * @param XI
     */
    public Interpolation1D(Matrix X, Matrix Y, Matrix XI)
    {
        if (Y == null)
        {
            throw new IllegalArgumentException("Interpolation1D : Parameter \"Y\" must be non-null.");
        }
        if (!Y.isVector())
        {
            throw new IllegalArgumentException("Interpolation1D : Parameter \"Y\" must be a vector and not a matrix.");
        }
        if (Y.isRowVector())
        {
            this.Y = Y.toColVector();
        }
        else
        {
            this.Y = Y;
        }

        if (XI == null)
        {
            throw new IllegalArgumentException("Interpolation1D : Parameter \"XI\" must be non-null.");
        }
        if (!XI.isVector())
        {
            throw new IllegalArgumentException("Interpolation1D : Parameter \"XI\" must be a vector and not a matrix.");
        }
        if (XI.isRowVector())
        {
            this.XI = XI.toColVector();
        }
        else
        {
            this.XI = XI;
        }

        if (X == null)
        {
            this.X = Matrix.linspace(1.0, (double) Y.length(), Y.length() + 1).toColVector();
        }
        else
        {
            if (!X.isVector())
            {
                throw new IllegalArgumentException(
                        "Interpolation1D : Parameter \"X\" must be a vector and not a matrix.");
            }
            if (X.isRowVector())
            {
                this.X = X.toColVector();
            }
            else
            {
                this.X = X;
            }
        }

        if (X.length() != Y.length())
        {
            throw new IllegalArgumentException("Interpolation1D : Parameter \"X\" and \"Y\" must have the same length.");
        }

        if (X.length() < 2)
        {
            throw new IllegalArgumentException("Interpolation1D : There must be at least 2 data points.");
        }

        Indices nan = X.isnan().find();
        if (nan != null)
        {
            throw new IllegalArgumentException("Interpolation1D : Parameter \"X\" contains NaNs.");
        }

        QuickSort St = new QuickSortMat(this.X, true, false);
        this.X = (Matrix) St.getSortedObject();
        int[] arr = St.getIndices().getRowPackedCopy();
        this.Y = this.Y.getElements(arr);

        Matrix h = JDatafun.diff(this.X);
        if (h.EQ(0.0).anyBoolean())
        {// any(h == 0)
            throw new IllegalArgumentException(
                    "Interpolation1D : The elements of array parameter \"X\" must be distinct."); // The
                                                                                                  // values
                                                                                                  // of
                                                                                                  // X
                                                                                                  // should
                                                                                                  // be
                                                                                                  // distinct.');
        }
    }

    /**
     * 
     * @return
     */
    public Matrix getInterpValues()
    {
        return this.YI;
    }

    /**
     * 
     * @param method
     */
    public void setInterpMethod(InterpMethod method)
    {
        if (method == null)
        {
            return;
        }
        this.method = method;
    }

    /**
     * 
     * @param es
     */
    // public void setEquallySpaced(boolean es){ equallySpaced = es;}
    public void build()
    {
        Matrix x = X;
        Matrix y = Y;
        Matrix u = XI;

        int m = y.length();
        int n = 1; // only 1, since y is a column vector.

        // boolean ix = true;//1;
        eqsp = MatrixUtil.linSpacedVector(x);
        // System.out.println("eqsp =  "+eqsp);

        double val = 0.0;

        // if (ix){
        if (!eqsp)
        {
            h = JDatafun.diff(x);
            // eqsp = (norm(diff(h),Inf) <= eps(norm(x,Inf)));
            /*
             * temp = JDatafun.diff(h).normInf(); temp2 =
             * x.normInf()*MathUtil.EPS; eqsp = temp<=temp2;
             */
            if (x.isfinite().NOT().anyBoolean())
            {// any(~isfinite(x))
                eqsp = false; // if an INF in x, x is not equally spaced
            }
        }

        if (eqsp)
        {
            val = m - 1;
            val = (x.end() - x.get(0, 0)) / val;
            h = new Matrix(1, 1, val);// h = (x(m)-x(1))/(m-1);
        }

        // System.out.println("----- h -----");
        // h.print(4,4);

        // }
        // else{
        // x = Matrix.linIncrement(1.0, (double)m, 1.0);//(1:m)';
        // h = Matrix.ones(1,1);//h = 1;
        // eqsp = true;
        // }

        // System.out.println("Execute #1");
        Indices p = null;

        if (h.LT(0.0).anyBoolean())
        {// any(h < 0)
         // [x,p] = sort(x);
         // y = y(p,:);
         // x = JDatafun.pivotSort(x,y);
            QuickSort sort = new QuickSortMat(x, true, false);
            x = (Matrix) sort.getSortedObject();
            p = sort.getIndices();
            y = y.getMatrix(p.getRowPackedCopy(), 0);

            // System.out.println("----- x- y -----");
            // x.mergeH(y).print(4,4);

            if (eqsp)
            {
                h = h.uminus();
            }
            else
            {
                h = JDatafun.diff(x);
            }
        }

        /*--------------- block added on 17/09/2009 ----------------*/
        switch (method)
        {
        case SPLINE:
        case CUBIC:
        case PCHIP:
        {
            this.extrapMethod = InterpFlag.Extrap;
            break;
        }
        default:
        {
            this.extrapMethod = new Double(Double.NaN);
        }
        }
        /*-----------------------------------------------------------*/

        // Reassign here
        X = x;
        Y = y;

        Matrix v = null;
        Matrix mat = null;
        Matrix mat2 = null;
        Indices Ind1 = null;

        // NEAREST ,LINEAR , SPLINE , PCHIP , CUBIC , V5CUBIC
        switch (method)
        {
        case SPLINE:
        { // not tested
            Spline sp = new Spline(x, y);
            v = sp.evaluate(u);// JPolyfun.spline(x, y, u);
            // throw new
            // IllegalArgumentException("build : Method not yet implemented.");
            break;
        }
        case CUBIC:
        case PCHIP:
        { // Pchip is yet to be implemented
            v = JPolyfun.pchip(x, y, u);
            throw new IllegalArgumentException("build : Method not yet implemented.");
            // break;
        }
        default:
        {
            v = Matrix.zeros(u.getRowDimension(), u.getColumnDimension());
            int q = u.length();
            if (!eqsp && (q != 1 && JDatafun.diff(u).LT(0.0).anyBoolean()))
            {// ~eqsp && any(diff(u) < 0)
             // [u,p] = sort(u);
                QuickSort St = new QuickSortMat(u, true, false);
                u = (Matrix) St.getSortedObject();
                p = St.getIndices();
            }
            else
            {
                p = Indices.linspace(0, q - 1, 1);// 1:q;
            }

            // Find indices of subintervals, x(k) <= u < x(k+1),
            // or u < x(1) or u >= x(m-1).

            // Indices k = null;

            // if(u==null){
            // k = u;
            // }
            // else
            if (eqsp)
            {
                // 1+floor((u-x(1))/h);
                // System.out.println("if (eqsp) : ----- h -----");
                // h.print(4,4);

                mat = JElfun.floor(u.minus(x.get(0, 0))).arrayRightDivide(h.get(0, 0)).plus(1.0);
                mat = JDatafun.max(mat, 1.0);
                val = m - 1;
                mat = JDatafun.min(mat, val);
                // k = min(max(1+floor((u-x(1))/h),1),m-1);
                k = mat.toIndices().minus(1);
                // System.out.println("  eqsp = "+eqsp);
                // k.print(4,0);
            }
            else
            { //
                /*
                 * System.out.println("----- u -----"); u.transpose().print(4,4
                 * ); System.out.println("----- x -----");
                 * x.transpose().print(4,4 );
                 */
                // [ignore,k] = histc(u,x);
                HistogramCount histc = new HistogramCount(u, x);
                k = histc.getBinIndices();
                /*
                 * System.out.println("----- k -----");
                 * k.transpose().plus(1).print(4 );
                 */

                Indices ux = u.LT(u.get(0, 0)).OR(u.isfinite().NOT()).find();
                // k(u<x(1) | ~isfinite(u)) = 1;
                if (ux != null)
                {
                    // k.findIndicesSetValueAt(ux,1);
                    k.setFromFind(ux, 1);
                }

                // k(u>=x(m)) = m-1;
                ux = u.GTEQ(x.getElementAt(m - 1)).find();
                if (ux != null)
                {
                    k.setFromFind(ux, m - 2);
                }
            }

            /*
             * System.out.println("--------- p ---------"); // ----- else (not
             * eqsp) ----- p.plus(1).toRowVector().print(4,0);
             * System.out.println("--------- k ---------"); // ----- else (not
             * eqsp) ----- k.plus(1).toRowVector().print(4,0);
             */

            int[] kArr = k.getRowPackedCopy();
            int[] kArrP1 = k.plus(1).getRowPackedCopy();
            Indices Itemp = null;
            // NEAREST ,LINEAR , SPLINE , PCHIP , CUBIC , V5CUBIC
            switch (method)
            {
            case NEAREST:
            {
                mat = x.getMatrix(kArr, 0, 0).plus(x.getMatrix(kArrP1, 0, 0));
                Itemp = u.GTEQ(mat.arrayRightDivide(2.0));
                Indices i = Itemp.find();// find(u >= (x(k)+x(k+1))/2);
                // k(i) = k(i)+1;
                if (i != null)
                {
                    Ind1 = k.getFromFind(i).plus(1);
                    k.setFromFind(i, Ind1);
                }
                v = y.getMatrix(kArr, 0);
                // v(p,:) = y(k,:);
                break;
            }
            case LINEAR:
            {
                Matrix s = null;
                if (eqsp)
                { // System.out.println(" ----- if (eqsp) -----");
                  // s = (u - x(k))/h;
                    s = u.minus(x.getMatrix(kArr, 0)).arrayRightDivide(h.get(0, 0));
                }
                else
                { // System.out.println(" ----- else (not eqsp) ----- ");
                  // s = (u - x(k))./h(k);
                    s = u.minus(x.getMatrix(kArr, 0)).arrayRightDivide(h.getMatrix(kArr, 0));
                }

                // System.out.println(" case LINEAR : ----- s -----"); // -----
                // else (not eqsp) -----
                // s.toRowVector().print(4,4);
                // System.out.println("----- y -----");
                // y.print(4,4);
                // for(int j=0; j<n; j++){// j = 1:n
                // v(p,j) = y(k,j) + s.*(y(k+1,j)-y(k,j));
                mat = y.getMatrix(kArr, 0);
                mat2 = y.getMatrix(kArrP1, 0);
                Matrix mat3 = mat2.minus(mat);
                Matrix mat4 = mat.plus(s.arrayTimes(mat3));
                v.setElements(p.getRowPackedCopy(), mat4);
                // }
                // System.out.println("----- case LINEAR -----");
                // System.out.println(" case LINEAR : ----- v -----");
                // v.print(4,4);
                break;
            }
            case V5CUBIC:
            {
                break;
            }
            }// //// end inner switch ///////

        }// -------- end default ---------
         // ------------------------------

        }// ///// end switch ///////

        YI = v;

        // overide extrapolation
        /*----------- this new block on 17/09/2009, replaced the old one below --------------------
         *-----------------------------------------------------------------------------------------*/
        if (extrapMethod instanceof Double)
        {
            if (p == null)
            {
                p = Indices.linspace(0, XI.length() - 1);// 1 : numelXi;
            }
            // outOfBounds = xiCol<xCol(1) | xiCol>xCol(n);
            Indices outOfBounds = XI.LT(X.start()).OR(XI.GT(X.end())).find();
            if (outOfBounds != null)
            {
                int[] arr = outOfBounds.getColIndicesAt(0);
                arr = p.getElements(arr).getRowPackedCopy();
                // yiMat(p(outOfBounds),:) = extrapval;
                YI.setElements(arr, Double.NaN);
            }
        }

        /*--------------- old block retired on 17/09/2009 : replaced by the block above ------------
        boolean cond = (YI.numel() == 1) && (XI.get(0, 0) < X.get(0, 0) || XI.get(0, 0) > X.end());
        if (cond) {
            YI = new Matrix(1, 1, Double.NaN);
        }
         *-------------------------------------------------------------------------------------------*/

        hasBuilt = true;
    }// -----------------------------------------------------------

    private Object[] ppinterp(Matrix x, Matrix y, InterpMethod method)
    {
        return null;
    }

    private Matrix mkpp(Matrix breaks, Matrix coefs, Object d)
    {
        return null;
    }

    /*
     * Don't use this method.
     */
    public double evaluate(double u)
    {
        if (!hasBuilt)
        {
            throw new RuntimeException("evaluate : Method \"build\" must be called first.");
        }
        double val = 1.0;
        switch (method)
        {
        case NEAREST:
        {
            // break;
            throw new RuntimeException("evaluate : Interpolation method \"" + method.toString()
                    + "\" isn't implemented yet.");
        }
        case LINEAR:
        {
            val = evalLinear(u);
            break;
        }
        case SPLINE:
        {
            // break;
            throw new RuntimeException("evaluate : Interpolation method \"" + method.toString()
                    + "\" isn't implemented yet.");
        }
        case PCHIP:
        {
            // break;
            throw new RuntimeException("evaluate : Interpolation method \"" + method.toString()
                    + "\" isn't implemented yet.");
        }
        case CUBIC:
        {
            // break;
            throw new RuntimeException("evaluate : Interpolation method \"" + method.toString()
                    + "\" isn't implemented yet.");
        }
        case V5CUBIC:
        {
            // break;
            throw new RuntimeException("evaluate : Interpolation method \"" + method.toString()
                    + "\" isn't implemented yet.");
        }
        default:
        {
            throw new IllegalArgumentException("evaluate : Unknown interpolation method.");
        }

        }
        return val;
    }

    private double evalLinear(double u)
    {
        double s = 1.0;
        Matrix x = X;
        Matrix y = Y;
        int kArr = k.get(0, 0);
        int kArrP1 = kArr + 1;
        if (eqsp)
        {
            System.out.println(" ----- if (eqsp) -----");
            // s = (u - x(k))/h;
            s = (u - x.get(kArr, 0)) / h.get(0, 0);
        }
        else
        {
            System.out.println(" ----- else (not eqsp) ----- ");
            // s = (u - x(k))./h(k);
            s = (u - x.get(kArr, 0)) / h.get(kArr, 0);
            // System.out.println("s = "+s );
        }

        // System.out.println(" case LINEAR : ----- s -----"); // ----- else
        // (not eqsp) -----
        // s.toRowVector().print(4,4);
        // System.out.println("----- y -----");
        // y.print(4,4);
        // for(int j=0; j<n; j++){// j = 1:n
        // v(p,j) = y(k,j) + s.*(y(k+1,j)-y(k,j));
        double val = y.get(kArr, 0);
        double val2 = y.get(kArrP1, 0);
        double val3 = val2 - val;
        double val4 = val + s * val3;

        // overide extrapolation
        boolean cond = (u < X.get(0, 0)) || (u > X.end());
        if (cond)
        {
            val4 = Double.NaN;
        }

        return val4;
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args)
    {

        /*
         * X = 0:10; xi = 0:0.5:10; yi = interp1(X,sin(X),xi); R = [xi' yi']
         */

        double[] xi =
        {
                3.1288, 3.1644, 3.1589, 3.0630, 3.0219, 3.3315, 3.3260, 3.2301, 3.1890, 3.5014, 3.4932, 3.3973, 3.3562,
                3.6603, 3.5671, 3.5260, 3.8274, 3.7342, 3.6932, 3.9973, 3.9014, 3.8603, 4.0658, 4.0247, 4.2329, 4.1918,
                4.4000, 4.3589, 4.5699, 4.5288, 4.6959, 4.8630, 5.0219, 5.1890
        };
        double[] xi2 =
        {
            3.1589
        };
        Matrix CFYears = new Matrix(xi2);

        double[] cYears =
        {
                0.0000, 3.0384, 3.1288, 3.5014, 3.9973, 4.5699, 5.1890
        };
        Matrix CurveYears = new Matrix(cYears);

        double[] cZeros =
        {
                0.0700, 0.0700, 0.1700, 0.0800, 0.0500, 0.1000, 0.1300
        };
        Matrix CurveZeros = new Matrix(cZeros);

        Interpolation1D Itp1D = new Interpolation1D(CurveYears, CurveZeros, CFYears);
        Itp1D.build();

        double val = Itp1D.evaluate(3.1589);
        System.out.println(" val = " + val);

        // Matrix CFZeros = Itp1D.getInterpValues();
        // System.out.println("----- CFZeros -----");
        // CFZeros.toRowVector().print(4,4);

        /*
         * boolean equallySpaced = false; Matrix X =
         * Matrix.linIncrement(0.0,10.0,1.0); if(!equallySpaced){
         * X.set(0,1,2.8); //equallySpaced = false; }
         * 
         * Matrix Y = JElfun.sin(X); Matrix XY =
         * X.transpose().mergeH(Y.transpose());
         * 
         * System.out.println("----- XY -----"); XY.print(4,4);
         * 
         * Matrix XI = Matrix.linIncrement(0.0, 10.0,0.5);
         * 
         * Interpolation1D Itp1D = new Interpolation1D(X, Y, XI);
         * 
         * Itp1D.build(); Matrix YI = Itp1D.getInterpValues();
         * 
         * Matrix R = XI.transpose().mergeH(YI);
         * 
         * System.out.println("----- R -----"); R.print(4,4);
         */

    }

    /**
     * @param extrapMethod
     *            the extrapMethod to set
     */
    /*
     * public void setExtrapMethod(Object extrapolationMethod) { if
     * (extrapolationMethod == null) { return; } if (!(extrapolationMethod
     * instanceof Double) && !(extrapolationMethod instanceof InterpFlag)) {
     * throw new IllegalArgumentException(
     * "setExtrapMethod : Object parameter \"extrapolationMethod\" must be an instanceof \"Double\" or \"InterpFlag\"."
     * ); } this.extrapMethod = extrapolationMethod; }
     */
}
