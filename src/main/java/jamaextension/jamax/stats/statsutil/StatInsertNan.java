/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.stats.statsutil;

import java.util.ArrayList;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Indices3D;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.Matrix3D;

/**
 * 
 * @author Sione
 */
public class StatInsertNan
{

    private ArrayList<Object> varargout;

    public StatInsertNan(Object wasnan, Object... varargin)
    {
        String msg = "";
        if (wasnan == null)
        {
            msg = "Object parameter \"wasnan\" must be non-null or non-empty.";
            throw new ConditionalRuleException("StatInsertNan", msg);
        }
        boolean cond = !(wasnan instanceof Indices) && !(wasnan instanceof Indices3D);
        if (cond)
        {
            msg = "Object parameter \"wasnan\" must be an instance of \"Indices\" or \"Indices3D\".";
            throw new ConditionalRuleException("StatInsertNan", msg);
        }
        if (wasnan instanceof Indices)
        {
            if (((Indices) wasnan).isNull())
            {
                msg = "Indices parameter \"wasnan\" must be non-empty.";
                throw new ConditionalRuleException("StatInsertNan", msg);
            }
        }

        if (varargin == null || varargin.length == 0)
        {
            msg = "Object array parameter \"varargin\" must be non-null or non-empty.";
            throw new ConditionalRuleException("StatInsertNan", msg);
        }
        if (varargin.length < 1)
        {
            msg = "Length of object array parameter \"varargin\" must be at least 1.";
            throw new ConditionalRuleException("StatInsertNan", msg);
        }

        int nargin = varargin.length;

        Object nanvec = null;
        if (varargin[0] instanceof Matrix)
        {
            Matrix mat = (Matrix) varargin[0];
            nanvec = new Matrix(mat.sizeIntArr(), Double.NaN);
        }
        else if (varargin[0] instanceof Matrix3D)
        {
            msg = "First element of object array parameter \"varargin\" must be matrix only. To Do. (Uncomment lines below).";
            throw new ConditionalRuleException("StatInsertNan", msg);
            // Matrix3D mat3 = (Matrix3D) varargin[0];
            // nanvec = new Matrix3D(mat3.size(), Double.NaN);
        }
        else
        {
            msg = "First element of object array parameter \"varargin\" must be an\n"
                    + "instance of \"Matrix\" or \"Matrix3D\".";
            throw new ConditionalRuleException("StatInsertNan", msg);
        }

        Object ok = null;
        if (wasnan instanceof Indices)
        {
            ok = ((Indices) wasnan).NOT();
        }
        else
        {
            msg = "Parameter \"wasnan\" must be Indices only. To Do. (Uncomment lines below).";
            throw new ConditionalRuleException("StatInsertNan", msg);
            // ok = ((Indices3D)wasnan).NOT();
        }

        varargout = new ArrayList<Object>(nargin);

        // % Find NaN, check length, and store outputs temporarily
        for (int j = 0; j < nargin; j++)
        {// j=1:nargin-1
            Matrix y = (Matrix) varargin[j];
            if (y.getRowDimension() == 1)
            {// (size(y,1)==1),
                y = y.transpose();
            }// end

            // [n p] = size(y);
            int n = y.getRowDimension();
            int p = y.getColumnDimension();

            Matrix x = null;
            if (p == 1)
            {
                x = (Matrix) nanvec;
            }
            else
            {
                x = ((Matrix) nanvec).repmat(1, p); // x = repmat(nanvec,1,p);
            }// end

            FindInd find = ((Indices) ok).findIJ();
            int[] okInd = find.getIndex();
            x.setRows(okInd, y);// x(ok,:) = y;
            varargout.add(j, x);

        }// end for

    }

    /**
     * @return the varargout
     */
    public ArrayList<Object> getVarargout()
    {
        return varargout;
    }
}
