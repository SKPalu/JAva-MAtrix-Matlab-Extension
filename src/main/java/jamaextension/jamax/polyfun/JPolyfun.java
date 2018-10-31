/*
 * JPolyfun.java
 *
 * Created on 15 December 2006, 20:08
 *
 * To change this template, choose Tools | Template Manager
 * AND open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortInd;
import jamaextension.jamax.datafun.QuickSortMat;

/**
 * 
 * @author Feynman Perceptrons
 */
public final class JPolyfun
{

    public final static String NEAREST_NEIGHBOR_INTERPOLATION = "NNI";
    public final static String LINEAR_INTERPOLATION = "LI";
    public final static String SPLINE_INTERPOLATION = "SI";
    public final static String CUBIC_INTERPOLATION = "CI";

    /**
     * Creates a new instance of JPolyfun
     */
    private JPolyfun()
    {
    }

    /*
     * %LINEAR Linear Interpolation of a 1-D function. % F=LINEAR(Y,XI) returns
     * the value of the 1-D function Y at the % points XI using linear
     * interpolation. length(F)=length(XI). XI is % an index into the vector Y.
     * Y is the value of the function % evaluated uniformly on a interval. If Y
     * is a matrix, then % the interpolation is performed for each column of Y
     * in which % case F is length(XI)-by-size(Y,2). % % If Y is of length N
     * then XI must contain values between 1 AND N. % The value NaN is returned
     * if this is not the case. % % F = LINEAR(X,Y,XI) uses the vector X to
     * specify the coordinates % of the underlying interval. X must be equally
     * spaced AND % monotonic. NaN's are returned for values of XI outside the %
     * coordinates in X.
     */
    public static Object linear(Matrix X, Matrix Y, Matrix U)
    {

        /*
         * //if nargin==2, //No X specified. //u = y; y = x; //Check for vector
         * problem. If so, make everything a column vector. //if
         * min(size(y))==1, y = y(:); end //[nrows,ncols] = size(y); Matrix x =
         * X; Matrix y = Y; Matrix u = U;
         * 
         * if(U==null) { return null; } //elseif nargin==3, //X specified.
         * //Check for vector problem. If so, make everything a column vector.
         * if (y.isVector()){ if(y.isRowVector()) { y = y.toColVector();} } if
         * (x.isVector()){ if(x.isRowVector()) { x = x.toColVector();} }
         * 
         * int nrows = y.getRowDimension(); int ncols = y.getColumnDimension();
         * //Scale AND shift u to be indices into Y. if (x.isVector()==false){
         * throw new IllegalArgumentException(
         * "linear : Parameter 'X' must be a vector AND not a matrix."); }
         * //[m,n] = size(x); int m = x.getRowDimension(); int n =
         * x.getColumnDimension(); if (m != nrows){ throw new
         * IllegalArgumentException
         * ("linear : The length of X must match the number of rows of Y."); }
         * double x1 = x.getElementAt(0); double xm = x.getElementAt(m-1);
         * Matrix temp = u.minus(x1); u =
         * u.minus(x1).arrayRightDivide(xm-x1).times
         * ((double)nrows-1.0).plus(1.0);//1 + (u-x(1))/(x(m)-x(1))*(nrows-1);
         * 
         * //else // error('Wrong number of input arguments.'); //end
         * 
         * //if isempty(u), F = []; return, end if (nrows<2){ //error('Y must
         * have at least 2 rows.'); throw new
         * IllegalArgumentException("linear : Y must have at least 2 rows."); }
         * 
         * Matrix siz = u.size(); u = u.toColVector();//u(:); //Make sure u is a
         * vector u = u(:,ones(1,ncols)); //Expand u //[m,n] = size(u); int m =
         * u.getRowDimension(); int n = u.getColumnDimension();
         * 
         * //Check for out of range values of u AND set to 1 //uout = find(u<1 |
         * u>nrows); Indices uout = u.LT(1.0).or(u.GT((double)nrows)).find(); if
         * ~isempty(uout), u(uout) = 1; end
         * 
         * //Interpolation parameters, check for boundary value. s = (u -
         * floor(u)); u = floor(u); if isempty(u), d = u; else d =
         * find(u==nrows); end if length(d)>0, u(d) = u(d)-1; s(d) = s(d)+1; end
         * 
         * //Now interpolate. v = (0:n-1)*nrows; ndx = u+v(ones(m,1),:); F = (
         * y(ndx).*(1-s) + y(ndx+1).*s );
         * 
         * //Now set out of range values to NaN. if ~isempty(uout), F(uout) =
         * NaN; end
         * 
         * if (min(size(F))==1) & (prod(siz)>1), F = reshape(F,siz); end
         */
        return null;
    }

    public static double interp1q(Matrix X, Matrix Y, double XI)
    {
        return interp1q(X, Y, new Matrix(new double[][]
        {
            {
                XI
            }
        })).get(0, 0);
    }

    public static Matrix interp1q(Matrix X, Matrix Y, Matrix XI)
    {
        if (!X.isVector())
        {
            throw new IllegalArgumentException("interp1q : Parameter \"x\" must be a vector.");
        }
        if (!Y.isVector())
        {
            throw new IllegalArgumentException("interp1q : Parameter \"y\" must be a vector.");
        }
        if (!XI.isVector())
        {
            throw new IllegalArgumentException("interp1q : Parameter \"xi\" must be a vector.");
        }
        if (X.length() != Y.length())
        {
            throw new IllegalArgumentException("interp1q : Parameter \"x\" and \"y\" must have the same length.");
        }

        Matrix x = null;
        if (X.isColVector())
        {
            x = X.toRowVector();
        }
        else
        {
            x = X;
        }

        Matrix y = null;
        if (Y.isColVector())
        {
            y = Y.toRowVector();
        }
        else
        {
            y = Y;
        }

        Matrix xi = null;
        if (XI.isColVector())
        {
            xi = XI.toRowVector();
        }
        else
        {
            xi = XI;
        }

        Matrix yi = null;
        Matrix numer = null;
        Matrix denom = null;
        Indices ind = null;
        Indices r = null;

        int[] indArr = null;
        int[] arrInd = null;
        int[] arrInd1 = null;

        Indices rind = null;
        Indices rind1 = null;

        Indices uOnes = null;

        Matrix matYDiff = null;
        Matrix matUOne = null;
        Matrix matY = null;
        Matrix u = null;
        // siz = size(xi);

        QuickSort obj = null;
        // SortMat objSort = new SortMat(x);

        if (xi.length() != 1)
        { // -------------------- vector --------------------------
          // obj = JDatafun.sortInd(xi);
            obj = new QuickSortMat(xi, true, true);
            Matrix xxi = (Matrix) obj.getSortedObject();
            // System.out.println("------ xxi -----");
            // xxi.print(4,4);

            Indices k = obj.getIndices();
            // System.out.println("------ k -----");
            // k.plus(1).print(4);

            // [dum,j] = sort([x;xxi]);
            // obj = JDatafun.sortInd(x.mergeV(xxi));
            obj = new QuickSortMat(x.mergeV(xxi), true, false);

            Matrix dum = (Matrix) obj.getSortedObject();
            // System.out.println("------ dum -----");
            // dum.print(4,2);

            Indices j = obj.getIndices();
            // System.out.println("------ j -----");
            // j.plus(1).print(4);

            // j = JDatafun.sort(j);
            // obj = JDatafun.sortInd(j);
            obj = new QuickSortInd(j, true, false);
            r = obj.getIndices();
            // System.out.println("------ r -----");
            // r.plus(1).print(4);

            // r(j)=1:length(j);
            // r =
            // j.copy();//j.getColumns(Indices.linspace(0,j.length()-1,1).getRowIndicesAt(0));
            // System.out.println("------ r -----");
            // r.plus(1).print(4);
            // r = r( length(x)+1:end) - ( 1:length(xxi) );
            // length(x)+1:end
            Indices temp1 = Indices.linspace(x.length(), r.endInd(), 1);
            // System.out.println("------ temp1-----");
            // temp1.plus(1).print(4);

            // r( length(x)+1:end)
            Indices temp = r.getColumns(temp1.getRowIndicesAt(0));
            // System.out.println("------ rleng-----");
            // temp.plus(1).print(4);

            // 1:length(xxi)
            Indices temp2 = Indices.linspace(0, xxi.length() - 1, 1);
            // System.out.println("------ lenxxi-----");
            // temp2.plus(1).print(4);

            // //r = r( length(x)+1:end) - ( 1:length(xxi) );
            r = temp.minus(temp2);

            /*
             * System.out.println("------ r-----"); r.print(4);
             * 
             * System.out.println("\n------ k-----"); k.plus(1).print(4);
             * 
             * int[] kr = k.getRowIndicesAt(0); for(int i=0; i<kr.length; i++){
             * System.out.println((i+1)+") "+(kr[i]+1)); }
             */
            int[] kg = k.getRowIndicesAt(0);
            Indices rcopy = r.copy();
            // r(k)=r;
            r.setIndices(0, 0, kg, rcopy);
            // System.out.println("------ r-----");
            // r.print(4);

            // r(xi==x(end)) = length(x)-1;
            temp = xi.EQ(x.end());
            // System.out.println("------ temp-----");
            // temp.print(4);

            temp = temp.find();
            if (temp != null)
            {
                r.findIndicesSetValueAt(temp, x.length() - 1);
            }

            // System.out.println("------ r-----");
            // r.print(4);
            // ind=find((r>0) & (r<length(x)));
            // ind = ind(:);
            ind = r.GT(0).AND(r.LT(x.length() - 1));
            ind = ind.find();

            // indArr = null;
            if (ind != null)
            {
                indArr = ind.getColIndicesAt(1);
                // System.out.println("indArr = ["+indArr[0]+" "+indArr[1]+"]");
            }

            yi = new Matrix(1, xxi.length(), Double.NaN);// NaN(length(xxi),size(y,2),
                                                         // superiorfloat(x,y,xi));

            if (ind != null)
            {
                // System.out.println("------ r-----");
                // r.print(4);
                rind = r.getColumns(indArr);// r(ind);
                // System.out.println("------ rind-----");
                // rind.print(4);

                arrInd = rind.minus(1).getRowIndicesAt(0);
                rind1 = rind;
                arrInd1 = rind1.getRowIndicesAt(0);

                Matrix xnumer1 = xi.getColumns(indArr);
                // System.out.println("------ xnumer1-----");
                // xnumer1.print(4,4);

                Matrix xnumer2 = x.getColumns(arrInd);
                // System.out.println("------ xnumer2-----");
                // xnumer2.print(4,4);

                // (xi(ind)-x(rind))
                numer = xnumer1.minus(xnumer2);
                // System.out.println("------ numer-----");
                // numer.print(4,4);

                // (x(rind+1)-x(rind))
                denom = x.getColumns(arrInd1).minus(x.getColumns(arrInd));
                // System.out.println("------ denom-----");
                // denom.print(4,4);

                // (xi(ind)-x(rind))./(x(rind+1)-x(rind));
                u = numer.arrayRightDivide(denom);
                // System.out.println("------ u-----");
                // u.print(4,4);

                // y(rind+1,:)-y(rind,:)
                matYDiff = y.getColumns(arrInd1).minus(y.getColumns(arrInd));

                // yi(ind,:) = y(rind,:) +
                // (y(rind+1,:)-y(rind,:)).*u(:,ones(1,size(y,2)));
                matY = y.getColumns(arrInd).plus(matYDiff.arrayTimes(u));
                yi.setMatrix(0, 0, indArr, matY);
            }

        }
        else
        { // --------------- Special scalar xi case
          // -----------------------------
            ind = x.LTEQ(xi.get(0, 0)).find();
            // r = null;
            Indices tempInd = null;
            // r = max(find(x <= xi));
            // r(xi==x(end)) = length(x)-1;
            if (ind != null)
            {
                ind = ind.getColumnAt(1).transpose();
                // System.out.println("------ ind-----");
                // ind.print(4);

                r = JDatafun.max(ind);
                // System.out.println("------ r----");
                // r.print(4);

                tempInd = xi.EQ(x.end()).find();
                r.findIndicesSetValueAt(tempInd, x.length() - 1);
                // System.out.println("------ r2----");
                // r.print(4);
            }

            if (r == null || r.LTEQ(0).trueAll() || r.GTEQ(x.length() - 1).trueAll())
            {
                // yi = new Matrix(1, y.length(), Double.NaN);
                yi = new Matrix(1, 1, Double.NaN);
            }
            else
            {
                // yi=y(r,:)+(y(r+1,:)-y(r,:)).*u(:,ones(1,size(y,2)));
                arrInd = r.getRowIndicesAt(0);
                rind1 = r.plus(1);

                arrInd1 = rind1.getRowIndicesAt(0);
                numer = new Matrix(new double[][]
                {
                    {
                        xi.get(0, 0) - x.get(0, arrInd[0])
                    }
                });// xi.minus(x.getColumns(arrInd));

                // System.out.println("------ numer-----");
                // numer.print(4,4);
                denom = new Matrix(new double[][]
                {
                    {
                        x.get(0, arrInd1[0]) - x.get(0, arrInd[0])
                    }
                });
                // System.out.println("------ denom-----");
                // denom.print(4,4);

                u = numer.arrayRightDivide(denom);// u =
                                                  // (xi-x(r))./(x(r+1)-x(r));

                matYDiff = new Matrix(new double[][]
                {
                    {
                        y.get(0, arrInd1[0]) - y.get(0, arrInd[0])
                    }
                });
                ;
                // uOnes = Indices.linspace(0,y.length()-1,1);
                // matUOne = u.getMatrix(0,0,uOnes.getRowIndicesAt(0));

                matY = y.getColumns(arrInd).plus(matYDiff.arrayTimes(u));
                yi = matY;// .setMatrix(0,0,indArr,matY);
            }

        }

        return yi;
    }

    /*
     * public static Matrix spline(Matrix X, Matrix Y, Matrix XI){ return null;
     * }
     */
    public static Matrix pchip(Matrix x, Matrix y, Matrix xx)
    {
        return null;
    }

    public static void main(String[] args)
    {
        Matrix x = new Matrix(new double[][]
        {
            {
                    1, 2, 3, 4, 5, 6, 7, 8
            }
        });
        Matrix y = new Matrix(new double[][]
        {
            {
                    0.1349, -1.3312, 3.2507, 4.5754, 2.7071, 8.3818, 9.3783, 7.9247
            }
        });
        Matrix xi = new Matrix(new double[][]
        {
            {
                    2.5, 4.2, 0.5, 8.4
            }
        });
        // 2.5 4.2 0.5 8.4
        // Matrix xi = new Matrix(new double[][]{{2.5}});
        // Matrix xi = new Matrix(new double[][]{{2.5}});

        Matrix yi = interp1q(x, y, xi);
        System.out.println("------- yi --------");
        yi.print(4, 4);

    }
}// ------------------------ End Class Definition
// -------------------------------

