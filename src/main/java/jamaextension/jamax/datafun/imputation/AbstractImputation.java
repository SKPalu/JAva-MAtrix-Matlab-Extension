/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.imputation;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.KeyValue;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author Sione
 */
public abstract class AbstractImputation
{

    protected Matrix rawData;
    protected Matrix imputedData;
    protected boolean containMissing;
    protected Dimension rowCol;
    protected boolean imputatedDone = false;
    protected boolean verbose = false;
    protected KeyValue params = new KeyValue();
    protected ImputationType imputation;

    public AbstractImputation(Matrix data)
    {
        this(data, null);
    }

    public AbstractImputation(Matrix data, Dimension rowCol)
    {
        if (data == null || data.isNull())
        {
            throw new ConditionalRuleException("AbsMissingDataImputation",
                    "Input matrix must be non-null or non-empty matrix.");
        }
        if (rowCol == null)
        {
            this.rowCol = Dimension.COL;
        }
        else
        {
            this.rowCol = rowCol;
        }

        this.containMissing = data.isnanBoolean();
        this.rawData = data;
    }

    /**
     * @return the imputation
     */
    public ImputationType getImputation()
    {
        return imputation;
    }

    public void addParam(String key, Object value)
    {

        String str = "";
        if (key == null || "".equals(key))
        {
            str = "String parameter \"key\" must be non-null or non-empty.";
            throw new ConditionalRuleException("addParam", str);
        }
        if ("".equals(MathUtil.deSpace(key)))
        {
            str = "String parameter \"key\" must not be all white-spaces.";
            throw new ConditionalRuleException("addParam", str);
        }

        str = key.trim();

        if (!params.containsKey(str.toLowerCase()))
        {
            params.add(str.toLowerCase(), value);
        }
        else
        {
            str = "String parameter \"key\" (" + str + ") already existed.";
            throw new ConditionalRuleException("addParam", str);
        }

    }

    public abstract void impute();

    /**
     * @return the imputedData
     */
    public Matrix getImputedData()
    {
        if (!imputatedDone)
        {
            throw new ConditionalRuleException("getImputedData", "The method \"impute\" must be called first.");
        }
        return imputedData;
    }

    /**
     * @return the imputatedDone
     */
    public boolean isImputatedDone()
    {
        return imputatedDone;
    }

    /**
     * @return the containMissing
     */
    public boolean isContainMissing()
    {
        return containMissing;
    }

    /**
     * @return the verbose
     */
    public boolean isVerbose()
    {
        return verbose;
    }

    /**
     * @param verbose
     *            the verbose to set
     */
    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public boolean isEmptyParams()
    {
        return this.params.isempty();
    }

}
