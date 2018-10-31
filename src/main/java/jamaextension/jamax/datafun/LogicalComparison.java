/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.datafun;

import jamaextension.jamax.constants.Logicals;

/**
 * 
 * @author Feynman Perceptrons
 */
public class LogicalComparison
{

    private Logicals logical = Logicals.GT;

    public LogicalComparison()
    {
        this(Logicals.GT);
    }

    public LogicalComparison(Logicals logic)
    {
        if (logic != null)
        {
            this.logical = logic;
        }
    }

    public void setLogicals(Logicals logic)
    {
        if (logical == null)
        {
            logical = Logicals.GT;
        }
        else
        {
            this.logical = logic;
        }
    }

    public boolean compareDoubles(double A1, double A2)
    {
        boolean nanA1 = Double.isNaN(A1);
        boolean nanA2 = Double.isNaN(A2);
        boolean tf = false;

        if (nanA1 && !nanA2)
        {
            tf = true;
        }
        else if (!nanA1 && nanA2)
        {
            tf = false;
        }
        else
        {
            tf = predicate(A1, A2);
        }

        return tf;
    }

    private boolean predicate(double A, double B)
    {
        boolean tf = false;
        switch (logical)
        {
        case EQ:
        {
            tf = A == B;
            break;
        }
        case NEQ:
        {
            tf = A != B;
            break;
        }
        case GT:
        {
            tf = A > B;
            break;
        }
        case LT:
        {
            tf = A < B;
            break;
        }
        case GTEQ:
        {
            tf = A >= B;
            break;
        }
        case LTEQ:
        {
            tf = A <= B;
            break;
        }
        default:
        {
            throw new IllegalArgumentException("predicate : Unknown \"Logicals\" object.");
        }
        }
        return tf;
    }

}
