/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.ops;

import jamaextension.jamax.Indices;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Feynman Perceptrons
 */
public abstract class SetDifference
{

    protected Indices maxIndices;
    protected Object diffObject;
    private Dimension flag;
    private Object firstObj;
    private Object secondObject;

    public SetDifference(Object firstObj, Object secondObject)
    {
        this(firstObj, secondObject, null);
    }

    public SetDifference(Object firstObj, Object secondObject, Dimension flag)
    {
        String msg = "";
        /*
         * if (firstObj == null) { msg =
         * "Parameter \"firstObj\" must be non-null."; throw new
         * ConditionalRuleException("SetDifference", msg); } if (secondObject ==
         * null) { msg = "Parameter \"secondObj\" must be non-null."; throw new
         * ConditionalRuleException("SetDifference", msg); }
         */
        this.firstObj = firstObj;
        this.secondObject = secondObject;
        this.flag = flag;
    }

    /**
     * @return the maxIndices
     */
    public Indices getMaxIndices()
    {
        return maxIndices;
    }

    /**
     * @return the diffObject
     */
    public Object getDiffObject()
    {
        return diffObject;
    }

    protected abstract void findDiff();

    /**
     * @return the flag
     */
    public Dimension getFlag()
    {
        return flag;
    }

    /**
     * @return the firstObj
     */
    public Object getFirstObj()
    {
        return firstObj;
    }

    /**
     * @return the secondObject
     */
    public Object getSecondObject()
    {
        return secondObject;
    }
}
