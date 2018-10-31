/*
 * Quick1DLinearInterp.java
 *
 * Created on 23 October 2007, 11:42
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
public class Quick1DLinearInterp
{

    private Matrix X;
    private Matrix Y;
    private Matrix XI;
    private Matrix YI;

    public Quick1DLinearInterp(Matrix X, Matrix Y, double xxi)
    {
        this(X, Y, new Matrix(1, 1, xxi));
    }

    /**
     * Creates a new instance of Quick1DLinearInterp
     */
    public Quick1DLinearInterp(Matrix X, Matrix Y, Matrix XI)
    {
        if (X == null)
        {
            throw new IllegalArgumentException("Quick1DLinearInterp : Parameter \"X\" must be non-null.");
        }
        if (!X.isVector())
        {
            throw new IllegalArgumentException("Quick1DLinearInterp : Parameter \"X\" must be a vector.");
        }
        if (Y == null)
        {
            throw new IllegalArgumentException("Quick1DLinearInterp : Parameter \"Y\" must be non-null.");
        }
        if (!Y.isVector())
        {
            throw new IllegalArgumentException("Quick1DLinearInterp : Parameter \"Y\" must be a vector.");
        }
        if (XI == null)
        {
            throw new IllegalArgumentException("Quick1DLinearInterp : Parameter \"XI\" must be non-null.");
        }
        if (!XI.isVector())
        {
            throw new IllegalArgumentException("Quick1DLinearInterp : Parameter \"XI\" must be a vector.");
        }
        if (X.length() != Y.length())
        {
            throw new IllegalArgumentException(
                    "Quick1DLinearInterp : Parameter \"X\" and \"Y\" must have the same length.");
        }

        this.X = X;
        this.Y = Y;
        this.XI = XI;

        computeInterp();
    }

    private void computeInterp()
    {

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

        // y = new Matrix(new double[][]{{0.01206795580876, 0.01206795580876,
        // 0.01273093342448, 0.01936090206870, 0.03170452521425,
        // 0.04230608522451, 0.05498741534294}});
        // y = y.transpose();

        // System.out.println("------ x-----");
        // x.print(8,0);

        // System.out.println("------ y-----");
        // y.print(8,4);

        // System.out.println("------ xi-----");
        // xi.print(8,0);

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

        Object[] obj = null;

        if (xi.length() != 1)
        { // -------------------- vector --------------------------
          // obj = JDatafun.sortInd(xi);
            QuickSort sort = new QuickSortMat(xi, true, true);
            Matrix xxi = (Matrix) sort.getSortedObject(); // (Matrix)obj[0];
            // System.out.println("------ xxi -----");
            // xxi.print(8,0);

            Indices k = sort.getIndices();// (Indices)obj[1];
            // System.out.println("------ k -----");
            // k.plus(1).print(4);

            // [dum,j] = sort([x;xxi]);
            // obj = JDatafun.sortInd(x.mergeH(xxi));
            sort = new QuickSortMat(x.mergeH(xxi), true, false);

            Matrix dum = (Matrix) sort.getSortedObject();// (Matrix)obj[0];
            // System.out.println("------ dum -----");
            // dum.print(8,0);

            Indices j = sort.getIndices();// (Indices)obj[1];
            // System.out.println("------ j -----");
            // j.plus(1).print(4);

            // obj = JDatafun.sortInd(j);

            sort = new QuickSortInd(j, true, false);
            r = sort.getIndices();// (Indices)obj[1];
            // System.out.println("------ r 1 -----");
            // r.print(4);

            // r(j)=1:length(j);
            // r =
            // j.copy();//j.getColumns(Indices.intLinspaceIncrement(0,j.length()-1,1).getRowIndicesAt(0));
            // System.out.println("------ r -----");
            // r.plus(1).print(4);

            // r = r( length(x)+1:end) - ( 1:length(xxi) );

            // length(x)+1:end
            // Indices temp1 = Indices.linspace(x.length(), r.endInd());
            Indices temp1 = Indices.linspace(x.length(), r.length() - 1);
            // System.out.println("------ temp1-----");
            // temp1.plus(1).print(4);

            // r( length(x)+1:end )
            Indices temp = r.getColumns(temp1.getRowIndicesAt(0));
            // System.out.println("------ rleng-----");
            // temp.plus(1).print(4);

            // 1:length(xxi)
            Indices temp2 = Indices.linspace(0, xxi.length() - 1);
            // System.out.println("------ lenxxi-----");
            // temp2.plus(1).print(4);

            // //r = r( length(x)+1:end) - ( 1:length(xxi) );
            r = temp.minus(temp2);// .minus(1);
            // System.out.println("------ r 2 -----");
            // r.print(4);

            int[] kg = k.getRowIndicesAt(0);
            Indices rcopy = r.copy();
            // r(k)=r;
            r.setIndices(0, 0, kg, rcopy);
            // System.out.println("------ r 3 -----");
            // r.print(4);

            // r(xi==x(end)) = length(x)-1;
            temp = xi.EQ(x.end());
            // System.out.println("------ temp-----");
            // temp.print(4);

            temp = temp.find();
            if (temp != null)
            {
                // r.findIndicesSetValueAt(temp,x.length()-1); //---> origina
                // line
                r.setFromFind(temp, x.length() - 2);
            }
            // System.out.println("------ r 4 -----");
            // r.print(4);

            // ind=find((r>0) & (r<length(x)));
            // ind = ind(:);
            ind = r.GT(0).AND(r.LT(x.length()));
            // System.out.println("------ ind-----");
            // ind.print(4);

            ind = ind.find();

            // indArr = null;
            if (ind != null)
            {
                indArr = ind.getColIndicesAt(1);
                // System.out.println("indArr = ["+indArr[0]+" "+indArr[1]+"]");
                // System.out.println("------ ind-----");
                // JDatafun.diff(ind.getColumnAt(1)).toRowVector().print(4);
            }

            yi = new Matrix(1, xxi.length(), Double.NaN);// NaN(length(xxi),size(y,2),
                                                         // superiorfloat(x,y,xi));
            // System.out.println("------ yi-----");
            // yi.print(4,4);

            if (ind != null)
            {
                // System.out.println("------ r-----");
                // r.print(4);
                rind = r.getElements(indArr);// r(ind);
                // System.out.println("------ rind-----");
                // rind.print(4);

                arrInd = rind.minus(1).getRowPackedCopy();
                rind1 = rind;
                arrInd1 = rind1.getRowPackedCopy();

                Matrix xnumer1 = xi.getElements(indArr);
                // System.out.println("------ xnumer1-----");
                // xnumer1.print(4,4);

                Matrix xnumer2 = x.getElements(arrInd);
                // System.out.println("------ xnumer2-----");
                // xnumer2.print(4,4);

                // (xi(ind)-x(rind))
                numer = xnumer1.minus(xnumer2);
                // System.out.println("------ numer-----");
                // numer.print(4,4);

                // (x(rind+1)-x(rind))
                denom = x.getElements(arrInd1).minus(x.getElements(arrInd));
                // System.out.println("------ denom-----");
                // denom.print(4,4);

                // (xi(ind)-x(rind))./(x(rind+1)-x(rind));
                u = numer.arrayRightDivide(denom);
                // System.out.println("------ u-----");
                // u.print(4,4);

                Matrix yrind = y.getElements(arrInd);
                // System.out.println("------ yrind-----");
                // yrind.print(4,4);

                // y(rind+1,:)-y(rind,:)
                matYDiff = y.getElements(arrInd1).minus(yrind);
                // System.out.println("------ matYDiff-----");
                // matYDiff.print(4,15);

                // yi(ind,:) = y(rind,:) +
                // (y(rind+1,:)-y(rind,:)).*u(:,ones(1,size(y,2)));
                matY = y.getElements(arrInd).plus(matYDiff.arrayTimes(u));
                yi.setElements(indArr, matY);

                // System.out.println("------ yi-----");
                // yi.toColVector().print(4,4);
            }

        }
        else
        { // --------------- Special scalar xi case
          // -----------------------------
            ind = x.LTEQ(xi.start()).find();
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
                // uOnes = Indices.intLinspaceIncrement(0,y.length()-1,1);
                // matUOne = u.getMatrix(0,0,uOnes.getRowIndicesAt(0));

                matY = y.getColumns(arrInd).plus(matYDiff.arrayTimes(u));
                yi = matY;// .setMatrix(0,0,indArr,matY);
            }

        }

        YI = yi;
    }

    public Matrix getInterpValues()
    {
        return this.YI;
    }

    public static void main(String[] args)
    {
        Matrix X = new Matrix(new double[][]
        {
            {
                    731546, 731639, 731730, 732251, 733361, 735188, 741854
            }
        });
        Matrix Y = new Matrix(new double[][]
        {
            {
                    0.0121, 0.0121, 0.0127, 0.0194, 0.0317, 0.0423, 0.0550
            }
        });
        Matrix XI = new Matrix(new double[][]
        {
            {
                    731546, 731686, 731869, 732052, 732235, 732417, 732600, 732782, 732965
            }
        }).toColVector();
        Quick1DLinearInterp QI = new Quick1DLinearInterp(X, Y, XI);
        System.out.println("------ QI-----");
        QI.getInterpValues().print(8, 6);
    }
}// ------------------------------ End Class Definition
// -------------------------

