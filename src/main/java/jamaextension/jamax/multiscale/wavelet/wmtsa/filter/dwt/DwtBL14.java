/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.multiscale.wavelet.wmtsa.filter.dwt;

import jamaextension.jamax.multiscale.wavelet.wmtsa.Filter;
import jamaextension.jamax.multiscale.wavelet.wmtsa.TransformClass;
import jamaextension.jamax.multiscale.wavelet.wmtsa.WaveUtil;
import jamaextension.jamax.multiscale.wavelet.wmtsa.WaveletFilter;

//import com.feynmanperceptron.jnumeric.wavelet.wmtsa.Filter;
//import com.feynmanperceptron.jnumeric.wavelet.wmtsa.TransformClass;
//import com.feynmanperceptron.jnumeric.wavelet.wmtsa.WaveletFilter;
//import com.feynmanperceptron.jnumeric.wavelet.wmtsa.*;

/**
 * 
 * @author Feynman Perceptrons
 */
public class DwtBL14 extends WaveletFilter
{

    public DwtBL14()
    {
        super("BL14", TransformClass.BestLocalized);
        this.g = Filter.wtf_bl14();
        this.L = g.length();
        this.h = WaveUtil.qmf(g);
    }

}
