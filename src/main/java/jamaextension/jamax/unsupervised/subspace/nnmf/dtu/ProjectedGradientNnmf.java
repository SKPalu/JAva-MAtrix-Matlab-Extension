/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.unsupervised.subspace.nnmf.dtu;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.NumericConditionalException;

/**
 * 
 * @author Sione
 */
public class ProjectedGradientNnmf extends DtuBaseNnmf
{

    public ProjectedGradientNnmf(Matrix data)
    {
        super(data);
    }

    public void extractFeatures()
    {
        if (isDataSampleNull())
        {
            throw new NumericConditionalException("extractFeatures : Data sample is null.");
        }
        if (this.isNonNegative())
        {
            throw new NumericConditionalException("extractFeatures : Data sample elements must be all be positive.");
        }

    }// end method

}
