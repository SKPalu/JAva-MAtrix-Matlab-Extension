/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.stats.statsutil;

import jamaextension.jamax.Cell;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Sione
 */
public class StatRemoveNan
{

    private int badin;
    private Indices wasnan;
    private Cell varargout;

    public StatRemoveNan(Matrix... varargin)
    {
        badin = 0;
        wasnan = new Indices(1, 1);
        int n = -1;
        int nargin = varargin.length;

        // Find NaN, check length, and store outputs temporarily
        varargout = new Cell(nargin, 1);

        for (int j = 0; j < nargin; j++)
        {
            Matrix y = varargin[j];
            int sizy1 = y.getRowDimension();
            if (sizy1 == 1 && n != 1)
            {
                y = y.transpose();
            }// end

            int ny = y.getRowDimension();// size(y,1);
            if (n == -1)
            {
                n = ny;
            }
            else if (n != ny && ny != 0)
            {
                if (badin == 0)
                {
                    badin = j;
                }
            }// end

            varargout.set(j, 0, y);// {j} = y;

            if (badin == 0 && ny > 0)
            {
                // wasnan = wasnan | any(isnan(y),2);
                Indices anyIsnanY2 = y.isnan().ANY(Dimension.COL);
                if (wasnan.length() == 1)
                {
                    int r = anyIsnanY2.getRowDimension();
                    int c = anyIsnanY2.getColumnDimension();
                    anyIsnanY2 = anyIsnanY2.OR(wasnan.repmat(r, c));
                }
                else
                {
                    wasnan = wasnan.OR(anyIsnanY2);
                }
            }// end
        }// end for

        if (badin > 0)
        {
            return;
        }

        // Fix outputs
        if (wasnan.anyBoolean())
        {
            Indices t = wasnan.NOT();
            int[] tArr = t.getRowPackedCopy();
            for (int j = 0; j < nargin; j++)
            {
                Matrix y = (Matrix) varargout.getElementAt(j);// {j};
                if (y.length() > 0)
                {
                    Matrix yt = y.getRows(tArr);
                    varargout.setElementAt(j, yt);// varargout{j} = y(t,:);
                }
            }// end
        }// end

    }

    /**
     * @return the badin
     */
    public int getBadin()
    {
        return badin;
    }

    /**
     * @return the wasnan
     */
    public Indices getWasnan()
    {
        return wasnan;
    }

    /**
     * @return the varargout
     */
    public Cell getVarargout()
    {
        return varargout;
    }
}
