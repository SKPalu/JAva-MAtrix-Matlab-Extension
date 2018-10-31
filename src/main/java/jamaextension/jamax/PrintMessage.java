/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

/**
 * 
 * @author Feynman Perceptrons
 */
public class PrintMessage
{

    private boolean print = false;
    private String flanks = "";

    public PrintMessage()
    {
        this(false);
    }

    public PrintMessage(boolean print)
    {
        this.print = print;
    }

    public void setPrint(boolean print)
    {
        this.print = print;
    }

    public boolean isPrint()
    {
        return this.print;
    }

    public void print(String message)
    {
        if (print == false || message == null || "".equals(message))
        {
            return;
        }
        if ("".equals(flanks) || flanks == null)
        {
            System.out.println(" " + message + " ");
        }
        else
        {
            System.out.println(flanks + " " + message + " " + flanks);
        }
    }

    /**
     * @param flanks
     *            the flanks to set
     */
    public void setFlanks(String flanks)
    {
        this.flanks = flanks;
    }
}
