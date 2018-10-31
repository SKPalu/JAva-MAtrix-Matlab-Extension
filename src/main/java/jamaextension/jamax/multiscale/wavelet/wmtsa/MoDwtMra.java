/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.multiscale.wavelet.wmtsa;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.multiscale.wavelet.wmtsa.filter.modwt.MoDwtLA8;

//import com.feynmanperceptron.jnumeric.jamax.*;  
//import com.feynmanperceptron.jnumeric.jamax.constants.*;
//import com.feynmanperceptron.jnumeric.jamax.datafun.*; 

//import com.feynmanperceptron.jnumeric.wavelet.wmtsa.filter.modwt.*;

/**
 * 
 * @author Feynman Perceptrons
 */
public class MoDwtMra
{

    private Matrix X;
    private WaveletFilter filter = new MoDwtLA8();
    private Boundary boundary = Boundary.reflection;
    // private boolean retainVJ = false;
    private Object nthLevel = NthLevelChoice.conservative;

    private Matrix[] detailsCoeff;
    private Matrix[] smoothsCoeff;

    private boolean hasTransformed = false;

    public MoDwtMra(Matrix X)
    {

        if (X == null)
        {
            throw new IllegalArgumentException(" MoDwtMra : Parameter 'X' must be non-null.");
        }
        if (X.isVector())
        {
            if (X.isRowVector())
            {
                this.X = X.toColVector();
            }
            else
            {
                this.X = X;
            }
        }
        else
        {
            this.X = X;
        }

    }

    public void setFilter(WaveletFilter filter)
    {
        if (filter == null)
        {
            return;
        }
        if (filter.getTransform() != Transform.MODWT)
        {
            throw new IllegalArgumentException(" setFilter : Parameter 'filter' must be a \"MODWT\" choice.");
        }
        this.filter = filter;
    }

    public void setBoundary(Boundary boundary)
    {
        if (boundary == null)
        {
            return;
        }
        this.boundary = boundary;
    }

    public void setNthLevel(Object nthLevel)
    {
        if (nthLevel == null)
        {
            return;
        }
        if (!(nthLevel instanceof Integer) && !(nthLevel instanceof NthLevelChoice))
        {
            throw new IllegalArgumentException(
                    " setNthLevel : Parameter 'nthLevel' must be an instanceof \"Integer\" or \"NthLevelChoice\".");
        }
    }

    private int chooseNthlevel(NthLevelChoice choice, int N)
    {
        int L = filter.getL();
        int j0 = 0;
        if (choice == NthLevelChoice.conservative)
        {
            double num = ((double) N / ((double) L - 1.0)) - 1.0;
            j0 = (int) Math.floor(MathUtil.log2(num));
        }
        else if (choice == NthLevelChoice.max)
        {
            j0 = (int) Math.floor(MathUtil.log2((double) N));
        }
        else if (choice == NthLevelChoice.supermax)
        {
            j0 = (int) Math.floor(MathUtil.log2(1.5 * (double) N));
        }
        else
        {
            throw new ConditionalException(
                    " chooseNthlevel : Invalid choices found. Valid ones are \"conservative\" , \"max\" and \"supermax\".");
        }
        return j0;
    }

    public Matrix[] getDetailsCoeff()
    {
        if (!hasTransformed)
        {
            throw new ConditionalException(" getDetailsCoeff : Method \"transform\" must be called first.");
        }
        return detailsCoeff;
    }

    public Matrix[] getSmoothsCoeff()
    {
        if (!hasTransformed)
        {
            throw new ConditionalException(" getSmoothsCoeff : Method \"transform\" must be called first.");
        }
        return smoothsCoeff;
    }

    public void transform()
    {
        MoDwt Mo = new MoDwt(X);
        Mo.setBoundary(boundary);
        Mo.setFilter(filter);
        Mo.setNthLevel(nthLevel);
        Mo.transform();

        InverseMoDwtMra IMRA = new InverseMoDwtMra(Mo);
        detailsCoeff = IMRA.getDetailsCoeff();
        smoothsCoeff = IMRA.getSmoothsCoeff();
        hasTransformed = true;
    }

    public boolean isTransformed()
    {
        return hasTransformed;
    }

}
