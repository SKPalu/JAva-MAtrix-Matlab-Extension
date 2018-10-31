/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamaextension.jamax.vnijmsl;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Feynman Perceptrons
 */
public class JMSLTestClass
{

    public static void main(String[] args)
    {

        Matrix ad = new Matrix(new double[]
        {
                1, 2, 3, 4, 5, 6, 7, 8
        });
        Matrix ad1 = JElfun.sin(ad).arrayTimes(3.0);
        CubicSpline CS = new CubicSpline(ad, ad1);

        Matrix breakPoints = CS.getBreakpoints();
        System.out.println("------------ breakPoints ------------");
        breakPoints.print(8, 4);

        Matrix coefs = CS.getCoef();
        System.out.println("------------ coefs ------------");
        coefs.print(8, 4);
    }

}
