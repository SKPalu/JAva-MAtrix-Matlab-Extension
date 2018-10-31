/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.toolbox.optim;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.OptimOpt;
import jamaextension.jamax.constants.HUpdate;
import jamaextension.jamax.constants.IHessType;
import jamaextension.jamax.constants.OptDisplay;
import jamaextension.jamax.funfun.FunctionFunctions;

/**
 * FMINUNC finds a local minimum of a function of several variables. X =
 * FMINUNC(FUN,X0) starts at X0 and attempts to find a local minimizer X of the
 * function FUN. FUN accepts input X and returns a scalar function value F
 * evaluated at X. X0 can be a scalar, vector or matrix.
 * 
 * X = FMINUNC(FUN,X0,OPTIONS) minimizes with the default optimization
 * parameters replaced by values in the structure OPTIONS, an argument created
 * with the OPTIMSET function. See OPTIMSET for details. Used options are
 * Display, TolX, TolFun, DerivativeCheck, Diagnostics, FunValCheck, GradObj,
 * HessPattern, Hessian, HessMult, HessUpdate, InitialHessType,
 * InitialHessMatrix, MaxFunEvals, MaxIter, DiffMinChange and DiffMaxChange,
 * LargeScale, MaxPCGIter, PrecondBandWidth, TolPCG, PlotFcns, OutputFcn, and
 * TypicalX. Use the GradObj option to specify that FUN also returns a second
 * output argument G that is the partial derivatives of the function df/dX, at
 * the point X. Use the Hessian option to specify that FUN also returns a third
 * output argument H that is the 2nd partial derivatives of the function (the
 * Hessian) at the point X. The Hessian is only used by the large-scale method,
 * not the line-search method.
 * 
 * X = FMINUNC(PROBLEM) finds the minimum for PROBLEM. PROBLEM is a structure
 * with the function FUN in PROBLEM.objective, the start point in PROBLEM.x0,
 * the options structure in PROBLEM.options, and solver name 'fminunc' in
 * PROBLEM.solver. Use this syntax to solve at the command line a problem
 * exported from OPTIMTOOL. The structure PROBLEM must have all the fields.
 * 
 * [X,FVAL] = FMINUNC(FUN,X0,...) returns the value of the objective function
 * FUN at the solution X.
 * 
 * [X,FVAL,EXITFLAG] = FMINUNC(FUN,X0,...) returns an EXITFLAG that describes
 * the exit condition of FMINUNC. Possible values of EXITFLAG and the
 * corresponding exit conditions are
 * 
 * 1 Magnitude of gradient smaller than the specified tolerance. 2 Change in X
 * smaller than the specified tolerance. 3 Change in the objective function
 * value smaller than the specified tolerance (only occurs in the large-scale
 * method). 0 Maximum number of function evaluations or iterations reached. -1
 * Algorithm terminated by the output function. -2 Line search cannot find an
 * acceptable point along the current search direction (only occurs in the
 * medium-scale method).
 * 
 * [X,FVAL,EXITFLAG,OUTPUT] = FMINUNC(FUN,X0,...) returns a structure OUTPUT
 * with the number of iterations taken in OUTPUT.iterations, the number of
 * function evaluations in OUTPUT.funcCount, the algorithm used in
 * OUTPUT.algorithm, the number of CG iterations (if used) in
 * OUTPUT.cgiterations, the first-order optimality (if used) in
 * OUTPUT.firstorderopt, and the exit message in OUTPUT.message.
 * 
 * [X,FVAL,EXITFLAG,OUTPUT,GRAD] = FMINUNC(FUN,X0,...) returns the value of the
 * gradient of FUN at the solution X.
 * 
 * [X,FVAL,EXITFLAG,OUTPUT,GRAD,HESSIAN] = FMINUNC(FUN,X0,...) returns the value
 * of the Hessian of the objective function FUN at the solution X.
 * 
 * Examples FUN can be specified using @: X = fminunc(@myfun,2)
 * 
 * where myfun is a MATLAB function such as:
 * 
 * function F = myfun(x) F = sin(x) + 3;
 * 
 * To minimize this function with the gradient provided, modify the function
 * myfun so the gradient is the second output argument: function [f,g] =
 * myfun(x) f = sin(x) + 3; g = cos(x); and indicate the gradient value is
 * available by creating an options structure with OPTIONS.GradObj set to 'on'
 * (using OPTIMSET): options = optimset('GradObj','on'); x =
 * fminunc(@myfun,4,options);
 * 
 * FUN can also be an anonymous function: x = fminunc(@(x) 5*x(1)^2 +
 * x(2)^2,[5;1])
 * 
 * If FUN is parameterized, you can use anonymous functions to capture the
 * problem-dependent parameters. Suppose you want to minimize the objective
 * given in the function myfun, which is parameterized by its second argument c.
 * Here myfun is an M-file function such as
 * 
 * function [f,g] = myfun(x,c)
 * 
 * f = c*x(1)^2 + 2*x(1)*x(2) + x(2)^2; * function g = [2*c*x(1) + 2*x(2) *
 * gradient 2*x(1) + 2*x(2)];
 * 
 * To optimize for a specific value of c, first assign the value to c. Then
 * create a one-argument anonymous function that captures that value of c and
 * calls myfun with two arguments. Finally, pass this anonymous function to
 * FMINUNC:
 * 
 * c = 3; * define parameter first options = optimset('GradObj','on'); *
 * indicate gradient is provided x = fminunc(@(x) myfun(x,c),[1;1],options)
 * 
 * See also OPTIMSET, FMINSEARCH, FMINBND, FMINCON, @, INLINE.
 * 
 * When options.LargeScale=='on', the algorithm is a trust-region method. When
 * options.LargeScale=='off', the algorithm is the BFGS Quasi-Newton method with
 * a mixed quadratic and cubic line search procedure.
 * 
 * @author Sione
 */
public class FminUnc
{

    private double x;
    private double FVAL;
    private int EXITFLAG;
    private Object OUTPUT;
    private Object GRAD;
    private Object HESSIAN;
    // input
    private OptimOpt opt;

    public FminUnc(FunctionFunctions funfcn, Matrix X)
    {
        this(funfcn, X, null);
    }

    public FminUnc(FunctionFunctions funfcn, Matrix X, OptimOpt opt)
    {
        this(funfcn, X, opt, (Object[]) null);
    }

    public FminUnc(FunctionFunctions funfcn, Matrix X, OptimOpt opt, Object... varargin)
    {
        if (opt == null)
        {
            this.opt = this.genDefaultOpt();
        }
        else
        {
            this.opt = opt;
        }
    }

    /**
     * @return the x
     */
    public double getX()
    {
        return x;
    }

    /**
     * @return the FVAL
     */
    public double getFVAL()
    {
        return FVAL;
    }

    /**
     * @return the EXITFLAG
     */
    public int getEXITFLAG()
    {
        return EXITFLAG;
    }

    /**
     * @return the OUTPUT
     */
    public Object getOUTPUT()
    {
        return OUTPUT;
    }

    /**
     * @return the GRAD
     */
    public Object getGRAD()
    {
        return GRAD;
    }

    /**
     * @return the HESSIAN
     */
    public Object getHESSIAN()
    {
        return HESSIAN;
    }

    private OptimOpt genDefaultOpt()
    {
        OptimOpt defaultOpt = new OptimOpt();

        /*
         * 'Display','final','LargeScale','on', ...
         * 'TolX',1e-6,'TolFun',1e-6,'DerivativeCheck','off',...
         * 'Diagnostics','off','FunValCheck','off',...
         * 'GradObj','off','MaxFunEvals','100*numberOfVariables',...
         * 'DiffMaxChange',1e-1,'DiffMinChange',1e-8,...
         * 'PrecondBandWidth',0,'TypicalX','ones(numberOfVariables,1)',...
         * 'MaxPCGIter','max(1,floor(numberOfVariables/2))', ...
         * 'TolPCG',0.1,'MaxIter',400,... 'Hessian','off','HessMult',[],...
         * 'HessPattern','sparse(ones(numberOfVariables))',...
         * 'HessUpdate','bfgs','OutputFcn',[],'PlotFcns',[], ...
         * 'InitialHessType','scaled-identity','InitialHessMatrix',[]
         */

        defaultOpt.setDisplay(OptDisplay.Final);
        defaultOpt.setLargeScale(Boolean.TRUE);
        defaultOpt.setTolX(1e-6);
        defaultOpt.setTolFun(1e-6);
        defaultOpt.setDerivativeCheck(Boolean.FALSE);
        defaultOpt.setDiagnostics(Boolean.FALSE);
        defaultOpt.setFunValCheck(Boolean.FALSE);
        defaultOpt.setGradObj(Boolean.FALSE);
        defaultOpt.setMaxFunEvals("100*numberOfVariables");
        defaultOpt.setDiffMaxChange(1e-1);
        defaultOpt.setDiffMinChange(1e-8);
        defaultOpt.setPrecondBandWidth(0.0);
        defaultOpt.setTypicalX("ones(numberOfVariables,1)");
        defaultOpt.setMaxPCGIter("max(1,floor(numberOfVariables/2))");
        defaultOpt.setTolPCG(0.1);
        defaultOpt.setMaxIter(400);
        defaultOpt.setHessian(false);
        defaultOpt.setHessMult(null);
        defaultOpt.setHessPattern("ones(numberOfVariables)");
        defaultOpt.setHessUpdate(HUpdate.bfgs);
        defaultOpt.setOutputFcn(null);
        defaultOpt.setPlotFcns(null);
        defaultOpt.setInitialHessType(IHessType.scaled_identity);
        defaultOpt.setInitialHessMatrix(null);

        return defaultOpt;
    }
}
