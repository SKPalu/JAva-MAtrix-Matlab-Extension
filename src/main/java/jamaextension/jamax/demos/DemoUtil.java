/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.demos;

import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;

/**
 * 
 * @author Sione
 */
public final class DemoUtil
{

    private DemoUtil()
    {
    }

    public static Object[] nestdiss(Matrix G, int p)
    {

        // [m,n] = size(G);
        int m = G.getRowDimension();
        int n = G.getColumnDimension();
        int[] arr = null;
        Matrix tmp = null;
        Object[] Gp = null;
        Matrix G1 = null;
        Matrix G2 = null;
        Matrix GS = null;

        Matrix GG = null;
        int pp = 0;

        if ((m < 1) || (n < 1))
        {
            GG = G;
            pp = p;
            // % Number a single row or column.
        }
        else if (m == 1)
        {
            GG = Matrix.indicesToMatrix(Indices.linspace(1, n));// G =
                                                                // (p+1:p+n);
            pp = p + n;// p = p+n;
            // return
        }
        else if (n == 1)
        {
            GG = Matrix.indicesToMatrix(Indices.linspace(1, m));// G =
                                                                // (p+1:p+m)';
            pp = p + m;// p = p+m;
            // return
            // % Otherwise, split in half,
            // % number the two halves,
            // % then number the seperator.
        }
        else if (m > n)
        {
            int m2 = (int) MathUtil.fix((m + 1.0) / 2.0);// m2 = fix((m+1)/2);

            arr = Indices.linspace(0, m2 - 2).getRowPackedCopy();
            tmp = G.getRows(arr);
            Gp = nestdiss(tmp, p);// [G1,p] = nestdiss(G(1:m2-1,:),p);
            G1 = (Matrix) Gp[0];
            pp = (Integer) Gp[1];

            arr = Indices.linspace(m2, m - 1).getRowPackedCopy();
            tmp = G.getRows(arr);
            Gp = nestdiss(tmp, pp);// [G2,p] = nestdiss(G(m2+1:m,:),p);
            G2 = (Matrix) Gp[0];
            pp = (Integer) Gp[1];

            tmp = G.getRowAt(m2 - 1);
            Gp = nestdiss(tmp, pp);// [GS,p] = nestdiss(G(m2,:),p);
            GS = (Matrix) Gp[0];
            pp = (Integer) Gp[1];
            G = G1.mergeVerti(GS, G2);// G = [G1; GS; G2];
            // return
        }
        else
        {// % if (n >= m)
            int n2 = (int) MathUtil.fix((n + 1.0) / 2.0);

            arr = Indices.linspace(0, n2 - 2).getRowPackedCopy();
            tmp = G.getColumns(arr);
            Gp = nestdiss(tmp, p);// [G1,p] = nestdiss(G(:,1:n2-1),p);
            G1 = (Matrix) Gp[0];
            pp = (Integer) Gp[1];

            arr = Indices.linspace(n2, n - 1).getRowPackedCopy();
            tmp = G.getColumns(arr);
            Gp = nestdiss(tmp, pp);// [G2,p] = nestdiss(G(:,n2+1:n),p);
            G2 = (Matrix) Gp[0];
            pp = (Integer) Gp[1];

            tmp = G.getColumnAt(n2 - 1);
            Gp = nestdiss(tmp, pp);// [GS,p] = nestdiss(G(:,n2),p);
            GS = (Matrix) Gp[0];
            pp = (Integer) Gp[1];

            G = G1.mergeHoriz(GS, G2);// G = [G1 GS G2];
            // return
        }// end

        Object[] obj =
        {
                GG, pp
        };
        return obj;
    }

    public static Matrix nested(int n)
    {
        // Matrix mat = null;

        Matrix G = Matrix.zeros(n, n);
        Matrix tmp = Matrix.zeros(n - 2, n - 2);
        // G(2:n-1,2:n-1) = nestdiss(zeros(n-2,n-2),0);
        Object[] Gp = nestdiss(tmp, 0);
        int[] arr = Indices.linspace(1, n - 2).getRowPackedCopy();
        G.setMatrix(arr, arr, Gp[0]);

        return G;
    }

    public static Object delsq(Object G)
    {
        Object OBJ = null;

        // [m,n] = size(G);
        int m = ((Matrix) G).getRowDimension();
        int n = ((Matrix) G).getColumnDimension();

        // % Indices of interior points
        // p = find(G);
        FindInd findP = ((Matrix) G).toIndices().findIJ();
        Indices Pind = findP.getIndexInd();
        int[] p = Pind.getRowPackedCopy();// findP.getIndex();
        // % Connect interior points to themselves with 4's.
        Matrix i = ((Matrix) G).getEls(p);// i = G(p);
        Matrix j = i.copy();// j = G(p);
        Matrix s = new Matrix(p.length, 1, 4.0);// s = 4*ones(size(p));

        // % for k = north, east, south, west
        int[] kM =
        {
                -1, m, 1, -m
        };
        for (int k = 0; k < kM.length; k++)
        {// k = [-1 m 1 -m]
         // % Possible neighbors in k-th direction
            int K1 = kM[k];
            p = Pind.plus(K1 + 1).getRowPackedCopy();
            Matrix Q = ((Matrix) G).getEls(p);// G(p+k);
            // % Index of points with interior neighbors
            FindInd findQ = Q.toIndices().findIJ();
            int[] q = findQ.getIndex(); // q = find(Q);
            // % Connect interior points to neighbors with -1's.
            p = Pind.getEls(q).getRowPackedCopy();
            Matrix tmp = ((Matrix) G).getEls(q);
            i = i.mergeV(tmp);// [i; G(p(q))];
            tmp = Q.getEls(q);
            j = j.mergeV(tmp);// [j; Q(q)];
            tmp = new Matrix(q.length, 1, -1.0);
            s = s.mergeV(tmp);// s = [s; -ones(length(q),1)];
        }// end
         // D = sparse(i,j,s);
        return OBJ;
    }

    public static void main(String[] args)
    {
    }
}
