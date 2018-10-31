/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.funfun;

/**
 * 
 * @author Sione
 */
public abstract class FunFuncAbstract implements FunctionFunctions
{

    protected Object[] output;
    protected Object[] input;
    protected String name;

    public FunFuncAbstract(Object[] input)
    {
        this.input = input;
    }

    public FunFuncAbstract()
    {
        this((Object[]) null);
    }

    public Object[] output()
    {
        return this.output;
    }

    public Object[] input()
    {
        return this.input;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
