/*
 * ComplexElfun.java
 *
 * Created on 29 June 2007, 01:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jamaextension.jamax;

/**
 * 
 * @author Feynman Perceptrons
 */
public final class ComplexElfun
{

    /**
     * Creates a new instance of ComplexElfun
     */
    private ComplexElfun()
    {
    }

    public static ComplexMatrix exp(ComplexMatrix C)
    {
        Complex[][] cArray = C.getArray();
        int r = C.getRowDimension();
        int c = C.getColumnDimension();
        ComplexMatrix R = new ComplexMatrix(r, c);
        Complex[][] X = R.getArray();
        for (int i = 0; i < r; i++)
        {
            for (int j = 0; j < c; j++)
            {
                X[i][j] = Complex.exp(cArray[i][j]);
            }
        }
        return R;
    }

    public static ComplexMatrix pow(ComplexMatrix C, double index)
    {
        Complex[][] cArray = C.getArray();
        int r = C.getRowDimension();
        int c = C.getColumnDimension();
        ComplexMatrix R = new ComplexMatrix(r, c);
        Complex[][] X = R.getArray();
        for (int i = 0; i < r; i++)
        {
            for (int j = 0; j < c; j++)
            {
                X[i][j] = Complex.pow(cArray[i][j], index);
            }
        }
        return R;
    }

}// -------------------------- End Class Definition
// -----------------------------
