/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.elmat;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.MatrixUtil;
import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Feynman Perceptrons
 */
public final class JElmat
{

    private JElmat()
    {
    }

    public static Indices subToInd(int[] siz, Indices... varargin)
    {
        String msg = "";
        if (siz == null)
        {
            msg = "Parameter \"siz\" must be non-null.";
            throw new ConditionalRuleException("subToInd", msg);
        }
        if (varargin == null)
        {
            msg = "Parameter \"varargin\" must be non-null.";
            throw new ConditionalRuleException("subToInd", msg);
        }
        int len = varargin.length;
        // Check if any element in the array is null.
        for (int i = 0; i < len; i++)
        {
            if (varargin[i] == null)
            {
                msg = "Indices at \"varargin[" + i + "]\" is found to be null, which it shouldn't be.";
                throw new ConditionalRuleException("subToInd", msg);
            }
        }
        int nargin = len + 1;
        int sizlen = siz.length;

        if (sizlen < 2)
        {
            msg = "Vector array \"siz\" must have at least 2 elements.";
            throw new ConditionalRuleException("subToInd", msg);
        }

        // Adjust input
        if (sizlen <= (nargin - 1))
        {
            // Adjust for trailing singleton dimensions
            int ns = nargin - sizlen - 1;
            if (ns > 0)
            {
                // siz = [siz ones(1,ns)];
                siz = new Indices(siz).mergeH(Indices.ones(1, ns)).getRowPackedCopy();
            }
        }
        else
        {
            // Adjust for linear indexing on last element
            // siz = [siz(1:nargin-2) prod(siz(nargin-1:end))];
            Indices sizCopy = new Indices(siz).copy();
            int[] arr = Indices.linspace(0, nargin - 3).getRowPackedCopy();
            Indices Fir = sizCopy.getColumns(arr);
            arr = Indices.linspace(nargin - 2, sizlen - 1).getRowPackedCopy();
            Indices Sec = sizCopy.getColumns(arr);
            siz = Fir.mergeH(Sec).getRowPackedCopy();
        }

        // re-assign here since 'siz' could have changed from the adjusting
        // steps above.
        sizlen = siz.length;

        // Compute linear indices
        // k = [1 cumprod(siz(1:end-1))];
        Indices sizInd = new Indices(siz).copy();
        int[] sizArr = Indices.linspace(0, sizlen - 2).getRowPackedCopy();
        sizInd = JDatafun.cumprod(sizInd.getColumns(sizArr));
        // sizInd.printInLabel("sizInd",0);
        // May change the following mergeH call if it gives incorrect result.
        int[] k = new Indices(1, 1, 1).mergeH(sizInd).getRowPackedCopy(); // minus(1)
                                                                          // here
                                                                          // because
                                                                          // index
                                                                          // in
                                                                          // Java
                                                                          // starts
                                                                          // at
                                                                          // 0
                                                                          // (one
                                                                          // less)
        // test for k
        // new Indices(k).printInLabel("k",0);

        Indices ndx = null;
        Indices s = varargin[0].size(); // For size comparison
        for (int i = 0; i < sizlen; i++)
        {
            Indices v = varargin[i].plus(1); // Add 1, since Matlab starts
                                             // indexing at 1
            // Input checking
            if (s.NEQ(v.size()).anyBoolean())
            {// ~isequal(s,size(v))
             // Verify sizes of subscripts
             // error('MATLAB:sub2ind:SubscriptVectorSize', 'The subscripts
             // vectors must all be of the same size.');
                msg = "The subscripts vectors must all be of the same size.";
                throw new ConditionalRuleException("subToInd", msg);
            }
            boolean cond = v.toColVector().LT(1).anyBoolean() || v.toColVector().GT(siz[i]).anyBoolean();
            if (cond)
            {// (any(v(:) < 1)) || (any(v(:) > siz(i)))
             // Verify subscripts are within range
             // error('MATLAB:sub2ind:IndexOutOfRange','Out of range
             // subscript.');
                msg = "Out of range subscript.";
                throw new ConditionalRuleException("subToInd", msg);
            }
            // ndx = ndx + (v-1)*k(i);
            if (i == 0)
            {
                ndx = v.minus(1).arrayTimes(k[i]).plus(1);
            }
            else
            {
                ndx = v.minus(1).arrayTimes(k[i]).plus(ndx);
            }
        }

        return ndx.minus(1); // Subtract 1, since Java starts indexing at 0
    }

    /*
     * @author Feynmance
     * 
     * @deprecated - Use the method <b>subToInd</b> instead.
     */
    public static int sub2ind(int[] siz, Indices I, Indices J)
    {

        if (siz == null)
        {
            throw new IllegalArgumentException("sub2ind : Parameter \"siz\" must be non-null.");
        }
        if (siz.length != 2)
        {
            throw new IllegalArgumentException("sub2ind : Integer array parameter \"siz\" must have a length of two.");
        }
        if (I == null)
        {
            throw new IllegalArgumentException("sub2ind : Parameter \"I\" must be non-null.");
        }
        if (J == null)
        {
            throw new IllegalArgumentException("sub2ind : Parameter \"J\" must be non-null.");
        }
        if (!I.isVector())
        {
            throw new IllegalArgumentException("sub2ind : Parameter \"I\" must be a vector and not a matrix.");
        }
        else
        {
            if (I.isRowVector())
            {
                I = I.toColVector();
            }
        }
        if (!J.isVector())
        {
            throw new IllegalArgumentException("sub2ind : Parameter \"J\" must be a vector and not a matrix.");
        }
        else
        {
            if (J.isRowVector())
            {
                J = J.toColVector();
            }
        }

        int[] cum =
        {
                0, siz[0]
        };
        Indices k = new Indices(cum);
        Indices s = I.size();
        int ndx = 1;

        for (int i = 0; i < siz.length; i++)
        {
            Indices v = null;
            if (i == 0)
            {
                v = I;
            }
            else if (i == 1)
            {
                v = J;
            }
            else
            {
                throw new IllegalArgumentException("sub2ind : Array index is out of bound for parameter \"siz\".");
            }

            if (s.NEQ(v.size()).anyBoolean())
            {
                throw new IllegalArgumentException("sub2ind : The subscripts vectors must all be of the same size.");
            }

            if (v.toColVector().LT(0).anyBoolean() || v.toColVector().GT(siz[i]).anyBoolean())
            {// (any(v(:) < 1)) || (any(v(:) > siz(i)))

                throw new IllegalArgumentException("sub2ind : Out of range subscript.");
            }

            ndx = ndx + (v.start() - 1) * k.get(0, i);

        }

        return ndx;
    }

    public static Indices[] ind2sub(int[] siz, Indices I)
    {

        /*
         * if (siz == null) { throw new
         * IllegalArgumentException("ind2sub : Parameter \"siz\" must be non-null."
         * ); } if (siz.length != 2) { throw new IllegalArgumentException(
         * "ind2sub : Integer array parameter \"siz\" must have a length of two."
         * ); } if (I == null) { throw new
         * IllegalArgumentException("ind2sub : Parameter \"I\" must be non-null."
         * ); }
         * 
         * Indices[] In = new Indices[2];
         * 
         * int n = siz.length; int[] k = {0, siz[0]};//[1
         * cumprod(siz(1:end-1))];
         * 
         * Matrix ndx = Matrix.indicesToMatrix(I);
         * 
         * for (int i = (n - 1); i >= 0; i--) { Matrix vi =
         * MatrixUtil.rem(ndx.minus(1.0), (double) k[i]).plus(1.0); Matrix vj =
         * ndx.minus(vi).arrayRightDivide((double) k[i]).plus(1.0); In[i] =
         * vj.toIndices(); ndx = vi; }
         * 
         * return In;
         */

        Matrix ndx = Matrix.indicesToMatrix(I);

        return ind2sub(siz, ndx);
    }

    public static Indices[] ind2sub(int[] siz, Matrix I)
    {

        if (siz == null)
        {
            throw new IllegalArgumentException("ind2sub : Parameter \"siz\" must be non-null.");
        }
        if (siz.length != 2)
        {
            throw new IllegalArgumentException("ind2sub : Integer array parameter \"siz\" must have a length of two.");
        }
        if (I == null)
        {
            throw new IllegalArgumentException("ind2sub : Parameter \"I\" must be non-null.");
        }

        Indices[] In = new Indices[2];

        int n = siz.length;
        int[] k =
        {
                0, siz[0]
        };// [1 cumprod(siz(1:end-1))];

        Matrix ndx = I;

        for (int i = (n - 1); i >= 0; i--)
        {
            Matrix vi = MatrixUtil.rem(ndx.minus(1.0), (double) k[i]).plus(1.0);
            Matrix vj = ndx.minus(vi).arrayRightDivide((double) k[i]).plus(1.0);
            In[i] = vj.toIndices();
            ndx = vi;
        }

        return In;
    }

    public static void main(String[] args)
    {
        Indices I = new Indices(new int[]
        {
                0, 1
        }).transpose();
        Indices J = new Indices(new int[]
        {
                0, 1
        }).transpose();
        Indices ind = subToInd(new int[]
        {
                2, 17
        }, I, J);
        ind.printInLabel("ind");
    }
}
