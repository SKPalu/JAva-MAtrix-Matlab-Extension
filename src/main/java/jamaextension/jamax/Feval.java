/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

/**
 * 
 * @author Sione
 */
public abstract class Feval
{

    protected Object[] inputArgs;
    protected Object[] outputArgs;

    /**
     * @return the inputArgs
     */
    public Object[] getInputArgs()
    {
        return inputArgs;
    }

    /**
     * @param inputArgs
     *            the inputArgs to set
     */
    public void setInputArgs(Object... inputArgs)
    {
        validateInputArgs(inputArgs);
        this.inputArgs = inputArgs;
    }

    /**
     * @return the outputArgs
     */
    public Object[] getOutputArgs()
    {
        return outputArgs;
    }

    protected abstract void validateInputArgs(Object... iArgs);

    public abstract void eval();
}
