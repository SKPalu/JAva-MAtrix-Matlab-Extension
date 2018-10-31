/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.ops;

import java.util.ArrayList;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Feynman Perceptrons
 */
public class SetDifferenceInd extends SetDifference
{

    public SetDifferenceInd(Indices A, Indices B)
    {
        super(A, B);
        findDiff();
    }

    public SetDifferenceInd(Indices A, Indices B, Dimension flag)
    {
        super(A, B, flag);
        findDiff();
    }

    protected void findDiff()
    {
        Indices A = (Indices) this.getFirstObj();
        Indices B = (Indices) this.getSecondObject();

        boolean isrows = false;
        if (this.getFlag() == null)
        {
            isrows = false;
        }
        else
        {
            if (this.getFlag() == Dimension.COL)
            {
                isrows = false;
            }
            else
            {
                isrows = true;
            }
        }

        int rowsA = 0;// size(a,1);
        int colsA = 0;// size(a,2);
        if (A != null)
        {
            rowsA = A.getRowDimension();
            colsA = A.getColumnDimension();
        }
        int rowsB = 0;// size(b,1);
        int colsB = 0;// size(b,2);
        if (B != null)
        {
            rowsB = B.getRowDimension();
            colsB = B.getColumnDimension();
        }

        boolean rowvec = !((rowsA > 1 && colsB <= 1) || (rowsB > 1 && colsA <= 1) || isrows);
        String msg = null;
        if (!isrows)
        {// ////////////////////////////////////////////////////////
            int numelA = 0;
            if (A != null)
            {
                numelA = A.length();
            }
            int numelB = 0;
            if (B != null)
            {
                numelB = B.length();
            }

            if (A.numel() != numelA || B.numel() != numelB)
            {
                msg = "A and B must be vectors or \"flag == Dimension.ROW\" must be specified.";
                throw new ConditionalRuleException("findDiff", msg);
            }
            if (numelA == 0)
            {
                // Ambiguous if no way to determine whether to return a row or
                // column.
                boolean ambiguous = (rowsA == 0 && colsA == 0) && ((rowsB == 0 && colsB == 0) || numelB == 1);
                if (!ambiguous)
                {
                    this.diffObject = null;
                    this.maxIndices = null;
                }
            }
            else if (numelB == 0)
            {// If B is empty, invoke UNIQUE to remove duplicates from A.
                Object[] obj = A.getUnique();
                this.diffObject = (Indices) obj[0];
                this.maxIndices = (Indices) obj[1];
                return;
            }
            else if (numelA == 1)
            {
                if (!B.toArrayList().contains(new Integer(A.start())))
                {
                    this.diffObject = A.getAsIndices(0, 0);
                    this.maxIndices = new Indices(1, 1, 0);
                }
                else
                {
                    this.diffObject = null;
                    this.maxIndices = null;
                }
                return;
            }
            else
            { // General case
              // Convert to columns.
              // a = a(:);
                if (A.isVector())
                {
                    if (A.isRowVector())
                    {
                        A = A.toColVector();
                    }
                }
                else
                {
                    A = A.toColVector();
                }
                // b = b(:);
                if (B.isVector())
                {
                    if (B.isRowVector())
                    {
                        B = B.toColVector();
                    }
                }
                else
                {
                    B = B.toColVector();
                }

                ArrayList<Integer> list = B.toArrayList();
                // int lenList = list.size();
                int lenA = A.length();
                ArrayList<Integer> index = new ArrayList<Integer>();
                for (int i = 0; i < lenA; i++)
                {
                    if (!list.contains(new Integer(A.get(i, 0))))
                    {
                        index.add(new Integer(i));
                    }
                }

                int differLen = index.size();
                if (differLen == 0)
                {
                    this.diffObject = null;
                    this.maxIndices = null;
                }
                else
                {
                    int[] tf = new int[differLen];
                    for (int i = 0; i < differLen; i++)
                    {
                        tf[i] = index.get(i).intValue();
                    }
                    Indices temp = A.getRows(tf);
                    Object[] obj = temp.getUnique();
                    this.diffObject = (Indices) obj[0];
                    Indices ndx = (Indices) obj[1];
                    Indices tfInd = new Indices(tf).toColVector();
                    // ndx.printInLabel("ndx",0);
                    this.maxIndices = tfInd.getRows(ndx.getRowPackedCopy());
                    // this.maxIndices.printInLabel("this.maxIndices",0);
                }
            }

            // If row vector, return as row vector.
            if (rowvec)
            {
                A = (Indices) this.diffObject;
                if (A.isColVector())
                {
                    this.diffObject = A.toRowVector();
                    this.maxIndices = this.maxIndices.toRowVector();
                }
            }

        }
        else
        {// //////////////////////////////////////////////////////////////
            msg = "Row-wise not yet implemented.";
            throw new ConditionalRuleException("findDiff", msg);
        }// /////////////////////////////////////////////////////////////////////

    }

    public static void main(String[] args)
    {
        int[] as =
        {
                -3, 4, 2, -5, 9
        };
        Indices A = new Indices(as);
        int[] bs =
        {
                24, 5, 4, 22, 9, 16, 31, 2, 1, 32
        };
        Indices B = new Indices(bs);

        SetDifference SD = new SetDifferenceInd(A, B);
        Indices D = (Indices) SD.getDiffObject();
        Indices I = SD.getMaxIndices();
        D.printInLabel("D", 0);
        I.printInLabel("I", 0);
    }
}
