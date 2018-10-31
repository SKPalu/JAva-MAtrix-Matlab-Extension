/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 * 
 * @author Sione
 */
public enum FactorLoadingMethods
{

    PRINCIPAL_COMPONENT_MODEL(-1), PRINCIPAL_FACTOR_MODEL(0), UNWEIGHTED_LEAST_SQUARES(1), GENERALIZED_LEAST_SQUARES(2), MAXIMUM_LIKELIHOOD(
            3), IMAGE_FACTOR_ANALYSIS(4), ALPHA_FACTOR_ANALYSIS(5);
    private int num;

    FactorLoadingMethods(int n)
    {
        this.num = n;
    }

    public int getNum()
    {
        return num;
    }
}
