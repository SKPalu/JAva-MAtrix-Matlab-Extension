/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

/**
 * 
 * @author Sione
 */
public class NonSingularMatrixException extends RuntimeException
{

    /**
     * Creates a new instance of <code>ConditionalRuleException</code> without
     * detail message.
     */
    public NonSingularMatrixException()
    {
        super();
    }

    /**
     * Constructs an instance of <code>ConditionalRuleException</code> with the
     * specified detail message.
     * 
     * @param msg
     *            the detail message.
     */
    public NonSingularMatrixException(String method, String message)
    {
        super(method + " : " + message);
    }

}
