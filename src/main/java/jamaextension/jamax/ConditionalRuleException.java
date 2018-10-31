/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

/**
 *
 * @author Sione
 */
//public class ConditionalRuleException {
//}
/**
 * 
 * @author Feynman Perceptrons
 */
public class ConditionalRuleException extends java.lang.IllegalArgumentException
{

    /**
     * Creates a new instance of <code>ConditionalRuleException</code> without
     * detail message.
     */
    public ConditionalRuleException()
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
    public ConditionalRuleException(String method, String message)
    {
        super(method + " : " + message);
    }

    public ConditionalRuleException(Object obj)
    {
        super(changeToString(obj));
    }

    private static String changeToString(Object obj)
    {
        String str = "";
        if (obj instanceof double[][])
        {
            Matrix A = new Matrix((double[][]) obj);
            if (A.numel() <= 100)
            {
                str = A.mat2str();
            }
        }
        else if (obj instanceof Matrix)
        {
            Matrix A = (Matrix) obj;
            if (A.numel() <= 100)
            {
                str = A.mat2str();
            }
        }
        else if (obj instanceof String)
        {
            str = (String) obj;
        }
        else
        {
            str = obj.toString();
        }
        return str;
    }

    ConditionalRuleException(String string)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
