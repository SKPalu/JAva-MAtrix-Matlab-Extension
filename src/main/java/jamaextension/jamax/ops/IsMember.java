/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.ops;

import java.util.ArrayList;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;

/**
 * 
 * @author Sione
 */
public class IsMember
{

    private Indices memberIndex = null;

    public IsMember(Matrix A, Matrix B)
    {
        if (A == null)
        {
            return;
        }
        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        this.memberIndex = new Indices(m, n);
        this.memberIndex.setLogical(true);
        if (B == null)
        {
            return;
        }
        Matrix Bunique = (new Unique(B.toRowVector())).getUniqueData();
        ArrayList<Double> bList = new ArrayList<Double>();
        int totEl = Bunique.length();
        for (int i = 0; i < totEl; i++)
        {
            bList.add(Bunique.getElementAt(i));
        }

        if (A.isVector())
        {
            if (A.isColVector())
            {
                for (int i = 0; i < m; i++)
                {
                    if (bList.contains(new Double(A.get(i, 0))))
                    {
                        this.memberIndex.set(i, 0, Boolean.TRUE);
                    }
                }
            }
            else
            {
                for (int j = 0; j < n; j++)
                {
                    if (bList.contains(new Double(A.get(0, j))))
                    {
                        this.memberIndex.set(0, j, Boolean.TRUE);
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < m; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (bList.contains(new Double(A.get(i, j))))
                    {
                        this.memberIndex.set(i, j, Boolean.TRUE);
                    }
                }
            }
        }

    }

    /**
     * @return the memberIndex
     */
    public Indices getMemberIndex()
    {
        return memberIndex;
    }
}
