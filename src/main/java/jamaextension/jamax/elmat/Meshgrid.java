/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.elmat;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Matrix;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Meshgrid
{

    private Matrix XX;
    private Matrix YY;

    public Meshgrid(Matrix X, Matrix Y)
    {
        if (!X.isVector())
        {
            throw new ConditionalException("Meshgrid : Parameter \"X\" must be a vector and not a matrix.");
        }
        if (!Y.isVector())
        {
            throw new ConditionalException("Meshgrid : Parameter \"Y\" must be a vector and not a matrix.");
        }

        // Make sure XX is a full row vector
        if (!X.isRowVector())
        {
            this.XX = X.toRowVector();
        }
        else
        {
            this.XX = X;
        }

        // Make sure y is a full column vector.
        if (!Y.isColVector())
        {
            this.YY = Y.toColVector();
        }
        else
        {
            this.YY = Y;
        }

        int nx = this.XX.length();
        int ny = this.YY.length();

        this.XX = this.XX.repmat(ny, 1);
        this.YY = this.YY.repmat(1, nx);
    }

    public Matrix getXX()
    {
        return XX;
    }

    public Matrix getYY()
    {
        return YY;
    }
}
