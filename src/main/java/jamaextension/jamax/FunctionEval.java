/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax;

/**
 * 
 * @author Feynman Perceptrons
 */
public interface FunctionEval
{
    public void setArguments(Object... args);

    public Object[] evaluate(int LHS);
}
