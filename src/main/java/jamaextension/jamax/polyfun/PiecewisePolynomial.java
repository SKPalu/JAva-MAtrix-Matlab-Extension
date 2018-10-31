/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSortMat;

/**
 * 
 * @author Feynman Perceptrons
 */
public class PiecewisePolynomial
{

    private Matrix breaks;// = reshape(breaks,1,L+1);
    private Matrix coefs;// = reshape(coefs,dl,k);
    private int pieces;// = L;
    private int order;// = k;
    private Indices dim;// = d;

    public PiecewisePolynomial(Matrix breaks, Matrix coefs)
    {
        this(breaks, coefs, new Indices(1, 1, 1));
    }

    public PiecewisePolynomial(Matrix breaks, Matrix coefs, Indices d)
    {
        if (breaks == null)
        {
            throw new IllegalArgumentException("PiecewisePolynomial : Parameter \"breaks\" must be non-null.");
        }
        if (!breaks.isVector())
        {
            throw new IllegalArgumentException(
                    "PiecewisePolynomial : Parameter \"breaks\" must be a vector and not a matrix.");
        }
        if (coefs == null)
        {
            throw new IllegalArgumentException("PiecewisePolynomial : Parameter \"coefs\" must be non-null.");
        }
        if (d == null)
        {
            throw new IllegalArgumentException("PiecewisePolynomial : Parameter \"d\" must be non-null.");
        }
        if (!d.isVector())
        {
            throw new IllegalArgumentException(
                    "PiecewisePolynomial : Parameter \"d\" must be a vector and not a matrix.");
        }

        if (d.isColVector())
        {
            d = d.toRowVector();
        }

        int dlk = coefs.numel();
        int l = breaks.length() - 1;

        int dl = (int) JDatafun.prod(Matrix.indicesToMatrix(d)).get(0, 0);
        dl = dl * l;
        double val = ((double) dlk / (double) dl) + 100.0 * MathUtil.EPS;
        int k = (int) MathUtil.fix(val);

        if ((k <= 0) || (dl * k != dlk))
        {
            throw new IllegalArgumentException("PiecewisePolynomial : The requested number of polynomial pieces = " + l
                    + ", is incompatible with the proposed size = " + d.get(0, 0));
        }

        this.breaks = breaks.reshape(1, l + 1);
        this.coefs = coefs.reshape(dl, k);
        this.pieces = l;
        this.order = k;
        this.dim = d;

    }// end method

    public String getForm()
    {
        return "pp";
    }

    public Matrix getBreaks()
    {
        return this.breaks;
    }

    public Matrix getCoefs()
    {
        return this.coefs;
    }

    public int getPieces()
    {
        return this.pieces;
    }

    public int getOrder()
    {
        return this.order;
    }

    public Indices getDim()
    {
        return this.dim;
    }

    public void setDim(Indices dim)
    {
        this.dim = dim;
    }

    public double evaluate(double x)
    {
        Matrix A = new Matrix(1, 1, x);
        return evaluate(A).get(0, 0);
    }

    public Matrix evaluate(Matrix xx)
    {
        if (xx == null)
        {
            throw new IllegalArgumentException("evaluate : Parameter \"xx\" must be non-null.");
        }
        if (!xx.isVector())
        {
            throw new IllegalArgumentException("evaluate : Parameter \"xx\" must be a vector and not a matrix.");
        }

        // obtain the row vector xs equivalent to XX
        Matrix sizexx = xx.size();
        int lx = xx.numel();
        Matrix xs = xx.reshape(1, lx);

        // if XX is row vector, suppress its first dimension
        // if sizexx.length()==2 && sizexx(1)==1, sizexx(1) = []; end
        if (xx.isRowVector())
        {
            sizexx = sizexx.getAsMatrix(0, 1);
        }

        QuickSortMat sort = null;
        Indices ix = null;
        // if necessary, sort xs
        if (lx != 1 && JDatafun.diff(xs).LT(0.0).anyBoolean())
        {
            // [xs,ix] = sort(xs);
            sort = new QuickSortMat(xs, true, true);
            xs = (Matrix) sort.getSortedObject();
            ix = sort.getIndices();
        }
        // b,c,L,k,dd
        // breaks,coefs,L,k,d
        // d = pp.dim; L = pp.pieces; breaks = pp.breaks; coefs = pp.coefs; k =
        // pp.order;

        Matrix b = breaks.copy();
        Matrix c = coefs.copy();
        int L = pieces;
        int k = order;
        Indices dd = dim.copy();

        int[] arr = null;

        // for each data point, compute its breakpoint interval
        arr = Indices.intLinspaceIncrement(0, L - 1).getRowPackedCopy();
        Matrix temp = b.getElements(arr).toRowVector().mergeH(xs);
        sort = new QuickSortMat(temp, true, false);

        // Matrix ignored = (Matrix)sort.getSortedObject();
        Indices index = sort.getIndices();
        // System.out.println("---------------- index ----------------");
        // index.print(8, 0);

        Indices find = index.GT(L - 1).find();// -(1:lx);

        Indices indLx = Indices.intLinspaceIncrement(0, lx - 1);

        /*
         * System.out.println("---------------- findI ----------------");
         * find.getColumnAt(1).toRowVector().print(8, 0);
         * System.out.println("---------------- fin2 ----------------");
         * indLx.print(8, 0);
         * System.out.println("---------------- top ----------------");
         * find.getColumnAt(1).toRowVector().minus(indLx).print(8, 0);
         */

        Indices tempInd = Indices.ones(1, lx);
        if (find != null)
        {
            tempInd = find.getColumnAt(1).toRowVector().minus(indLx).mergeV(tempInd);
            // System.out.println(" Number #1");
        }
        else
        {
            tempInd = indLx.uminus().mergeV(tempInd);
            // System.out.println(" Number #2");
        }

        // index = max( [find(index>L)-(1:lx);ones(1,lx)] );
        index = JDatafun.max(tempInd).minus(1); // subtract one here, since java
                                                // has less than one index to
                                                // matlab

        arr = index.getRowPackedCopy();

        /*
         * System.out.println("---------------- index ----------------");
         * index.print(8, 0);
         * System.out.println("---------------- b ----------------"); b.print(8,
         * 0); System.out.println("---------------- b2 ----------------");
         * b.getElements(arr).toRowVector().print(8, 0);
         */

        // now go to local coordinates ...
        xs = xs.minus(b.getElements(arr).toRowVector());// xs-b(index);

        int d = (int) JDatafun.prod(Matrix.indicesToMatrix(dd)).get(0, 0);

        if (d > 1)
        {
            temp = xs.repmat(d, 1);
            xs = temp.reshape(1, d * lx);
            index = index.arrayTimes(d);// d*index;
            Indices tp = Indices.intLinspaceIncrement(-d, -1).transpose();// temp
                                                                          // =
                                                                          // (-d:-1).';
            Indices rep = index.repmat(d, 1).plus(1).plus(tp.repmat(1, lx));
            index = rep.reshape(d * lx, 1);
        }
        else
        {
            if (sizexx.length() > 1)
            {
                dd = null;
            }
            else
            {
                dd = new Indices(1, 1, 1);
            }
        }

        // apply nested multiplication:
        arr = index.getRowPackedCopy(); // check to see if 'index' is not going
                                        // to be null
        Matrix v = c.getMatrix(arr, 0);// c(index,1);
        Matrix xsCol = xs.toColVector();

        for (int i = 1; i < k; i++)
        {
            v = xsCol.arrayTimes(v).plus(c.getMatrix(arr, i));
        }

        v = v.reshape(d, lx);// reshape(v,d,lx);

        if (ix != null)
        {
            arr = ix.getRowPackedCopy();
            v = v.getElements(arr).toRowVector();
        }

        // v = reshape(v,[dd,sizexx])
        if (dd == null)
        {
            v = v.reshape((int) sizexx.getElementAt(0), (int) sizexx.getElementAt(1));
        }
        else
        {
            v = v.reshape(dd.get(0, 0), (int) sizexx.getElementAt(0));
        }

        return v;
    }

    public void print(int i, int j)
    {
        System.out.println("\n--------------- breaks ---------------");
        breaks.print(i, j);

        System.out.println("\n--------------- coefs ---------------");
        coefs.print(i, j);

        System.out.println("\n pieces = " + pieces);
        System.out.println("\n order  = " + order);
        System.out.println("\n dim    = " + dim.get(0, 0));
        System.out.println("\n");
    }
}// -------------------------- End Class Definition
// -----------------------------
