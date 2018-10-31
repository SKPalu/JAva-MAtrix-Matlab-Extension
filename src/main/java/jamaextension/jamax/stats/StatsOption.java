/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.stats;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.OptDisplay;
import jamaextension.jamax.constants.StatsTolType;
import jamaextension.jamax.constants.StatsWeightFunc;

/**
 * 
 * @author Sione
 */
public final class StatsOption
{

    /*
     * Display - Level of display. 'off', 'iter', or 'final'. private int
     * MaxFunEvals - Maximum number of objective function evaluations % allowed.
     * A positive integer. MaxIter - Maximum number of iterations allowed. A
     * positive integer. TolBnd - Parameter bound tolerance. A positive scalar.
     * TolFun - Termination tolerance for the objective function % value. A
     * positive scalar. TolX - Termination tolerance for the parameters. A
     * positive scalar. GradObj - Flag to indicate whether the objective
     * function can return a % gradient vector as a second output. 'off' or
     * 'on'. DerivStep - Relative difference used in finite difference
     * derivative % calculations. A positive scalar, or a vector of positive
     * scalars % the same size as the vector of model parameters being
     * estimated. FunValCheck - Check for invalid values, such as NaN or Inf,
     * from % the objective function. 'off' or 'on'. Robust - Flag to invoke the
     * robust fitting option. 'off' (the default) % or 'on'. WgtFun - A weight
     * function for robust fitting. Valid only when Robust % is 'on'. 'bisquare'
     * (the default), 'andrews', 'cauchy', % 'fair', 'huber', 'logistic',
     * 'talwar', or 'welsch'. Can % also be a function handle that accepts a
     * normalized residual % as input and returns the robust weights as output.
     * Tune - The tuning constant used in robust fitting to normalize the %
     * residuals before applying the weight function. A positive % scalar. The
     * default value depends upon the weight function. % This parameter is
     * required if the weight function is % specified as a function handle.
     */
    private OptDisplay display = OptDisplay.Off;// : [ {off} | final | iter
                                                // ]\n');
    private int maxFunEvals;// : [ positive integer ]\n');
    private int maxIter;// [ positive integer ]\n');
    private double tolBnd;// : [ positive scalar ]\n');
    private double tolFun;// [ positive scalar ]\n');
    private double tolX;// [ positive scalar ]\n')
    private boolean gradObj = false;// : [ {off} | on ]\n')
    private Matrix derivStep;// [ positive scalar or vector ]\n')
    private boolean funValCheck = false;// : [ off | {on} ]\n')
    private boolean robust = false;// : [ {off} | on ]\n')
    private StatsWeightFunc wgtFun = StatsWeightFunc.bisquare;// : [ {bisquare}
                                                              // | andrews |
                                                              // cauchy | fair |
                                                              // huber |
                                                              // logistic |
                                                              // talwar | welsch
                                                              // | function
                                                              // handle ]\n')
    private StatsTolType tolTypeFun;// [''abs'' |''rel'']\n')
    private StatsTolType tolTypeX;// [''abs'' |''rel'']\n')

    public StatsOption()
    {
    }

    /**
     * @return the display
     */
    public OptDisplay getDisplay()
    {
        return display;
    }

    /**
     * @param display
     *            the display to set
     */
    public void setDisplay(OptDisplay display)
    {
        this.display = display;
    }

    /**
     * @return the maxFunEvals
     */
    public int getMaxFunEvals()
    {
        return maxFunEvals;
    }

    /**
     * @param maxFunEvals
     *            the maxFunEvals to set
     */
    public void setMaxFunEvals(int maxFunEvals)
    {
        this.maxFunEvals = maxFunEvals;
    }

    /**
     * @return the maxIter
     */
    public int getMaxIter()
    {
        return maxIter;
    }

    /**
     * @param maxIter
     *            the maxIter to set
     */
    public void setMaxIter(int maxIter)
    {
        this.maxIter = maxIter;
    }

    /**
     * @return the tolBnd
     */
    public double getTolBnd()
    {
        return tolBnd;
    }

    /**
     * @param tolBnd
     *            the tolBnd to set
     */
    public void setTolBnd(double tolBnd)
    {
        this.tolBnd = tolBnd;
    }

    /**
     * @return the tolFun
     */
    public double getTolFun()
    {
        return tolFun;
    }

    /**
     * @param tolFun
     *            the tolFun to set
     */
    public void setTolFun(double tolFun)
    {
        this.tolFun = tolFun;
    }

    /**
     * @return the tolX
     */
    public double getTolX()
    {
        return tolX;
    }

    /**
     * @param tolX
     *            the tolX to set
     */
    public void setTolX(double tolX)
    {
        this.tolX = tolX;
    }

    /**
     * @return the gradObj
     */
    public boolean isGradObj()
    {
        return gradObj;
    }

    /**
     * @param gradObj
     *            the gradObj to set
     */
    public void setGradObj(boolean gradObj)
    {
        this.gradObj = gradObj;
    }

    /**
     * @return the derivStep
     */
    public Matrix getDerivStep()
    {
        return derivStep;
    }

    /**
     * @param derivStep
     *            the derivStep to set
     */
    public void setDerivStep(Matrix derivStep)
    {
        this.derivStep = derivStep;
    }

    /**
     * @return the funValCheck
     */
    public boolean isFunValCheck()
    {
        return funValCheck;
    }

    /**
     * @param funValCheck
     *            the funValCheck to set
     */
    public void setFunValCheck(boolean funValCheck)
    {
        this.funValCheck = funValCheck;
    }

    /**
     * @return the robust
     */
    public boolean isRobust()
    {
        return robust;
    }

    /**
     * @param robust
     *            the robust to set
     */
    public void setRobust(boolean robust)
    {
        this.robust = robust;
    }

    /**
     * @return the wgtFun
     */
    public StatsWeightFunc getWgtFun()
    {
        return wgtFun;
    }

    /**
     * @param wgtFun
     *            the wgtFun to set
     */
    public void setWgtFun(StatsWeightFunc wgtFun)
    {
        this.wgtFun = wgtFun;
    }

    /**
     * @return the tolTypeFun
     */
    public StatsTolType getTolTypeFun()
    {
        return tolTypeFun;
    }

    /**
     * @param tolTypeFun
     *            the tolTypeFun to set
     */
    public void setTolTypeFun(StatsTolType tolTypeFun)
    {
        this.tolTypeFun = tolTypeFun;
    }

    /**
     * @return the tolTypeX
     */
    public StatsTolType getTolTypeX()
    {
        return tolTypeX;
    }

    /**
     * @param tolTypeX
     *            the tolTypeX to set
     */
    public void setTolTypeX(StatsTolType tolTypeX)
    {
        this.tolTypeX = tolTypeX;
    }
}
