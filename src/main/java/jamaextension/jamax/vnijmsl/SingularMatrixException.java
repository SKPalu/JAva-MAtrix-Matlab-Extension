/*
 * SingularMatrixException.java
 *
 * Created on 31 March 2007, 16:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax.vnijmsl;

import jamaextension.jamax.ConditionalRuleException;

public class SingularMatrixException extends ConditionalRuleException
{

    static final long serialVersionUID = 0x840bf55ffb062cd1L;

    public SingularMatrixException()
    {
        super();
    }

    public SingularMatrixException(Object obj)
    {
        super(obj);
    }

}