/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.multiscale.wavelet.wmtsa.filter.modwt;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.multiscale.wavelet.wmtsa.Filter;
import jamaextension.jamax.multiscale.wavelet.wmtsa.Transform;
import jamaextension.jamax.multiscale.wavelet.wmtsa.TransformClass;
import jamaextension.jamax.multiscale.wavelet.wmtsa.WaveUtil;
import jamaextension.jamax.multiscale.wavelet.wmtsa.WaveletFilter;

/**
 * 
 * @author Feynman Perceptrons
 */
public class MoDwtC24 extends WaveletFilter
{

    public MoDwtC24()
    {
        super("C24", TransformClass.Coiflet, Transform.MODWT);
        Matrix f = Filter.wtf_c24();
        this.g = f.arrayRightDivide(Math.sqrt(2.0));
        this.L = g.length();
        this.h = WaveUtil.qmf(f).arrayRightDivide(Math.sqrt(2.0));
    }

}
