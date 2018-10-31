/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.funfun;

import java.util.HashMap;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.OptimOpt;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.constants.OptDisplay;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.specfun.JSpecfun;

/**
 * FMINSEARCH Multidimensional unconstrained nonlinear minimization
 * (Nelder-Mead). X = FMINSEARCH(FUN,X0) starts at X0 and attempts to find a
 * local minimizer X of the function FUN. FUN is a function handle. FUN accepts
 * input X and returns a scalar function value F evaluated at X. X0 can be a
 * scalar, vector or matrix.
 * 
 * X = FMINSEARCH(FUN,X0,OPTIONS) minimizes with the default optimization
 * parameters replaced by values in the structure OPTIONS, created with the
 * OPTIMSET function. See OPTIMSET for details. FMINSEARCH uses these options:
 * Display, TolX, TolFun, MaxFunEvals, MaxIter, FunValCheck, PlotFcns, and
 * OutputFcn.
 * 
 * X = FMINSEARCH(PROBLEM) finds the minimum for PROBLEM. PROBLEM is a structure
 * with the function FUN in PROBLEM.objective, the start point in PROBLEM.x0,
 * the options structure in PROBLEM.options, and solver name 'fminsearch' in
 * PROBLEM.solver. The PROBLEM structure must have all the fields.
 * 
 * [X,FVAL]= FMINSEARCH(...) returns the value of the objective function,
 * described in FUN, at X.
 * 
 * [X,FVAL,EXITFLAG] = FMINSEARCH(...) returns an EXITFLAG that describes the
 * exit condition of FMINSEARCH. Possible values of EXITFLAG and the
 * corresponding exit conditions are
 * 
 * 1 Maximum coordinate difference between current best point and other points
 * in simplex is less than or equal to TolX, and corresponding difference in
 * function values is less than or equal to TolFun. 0 Maximum number of function
 * evaluations or iterations reached. -1 Algorithm terminated by the output
 * function.
 * 
 * [X,FVAL,EXITFLAG,OUTPUT] = FMINSEARCH(...) returns a structure OUTPUT with
 * the number of iterations taken in OUTPUT.iterations, the number of function
 * evaluations in OUTPUT.funcCount, the algorithm name in OUTPUT.algorithm, and
 * the exit message in OUTPUT.message.
 * 
 * Examples FUN can be specified using @: X = fminsearch(@sin,3) finds a minimum
 * of the SIN function near 3. In this case, SIN is a function that returns a
 * scalar function value SIN evaluated at X.
 * 
 * FUN can also be an anonymous function: X = fminsearch(@(x) norm(x),[1;2;3])
 * returns a point near the minimizer [0;0;0].
 * 
 * If FUN is parameterized, you can use anonymous functions to capture the
 * problem-dependent parameters. Suppose you want to optimize the objective
 * given in the function myfun, which is parameterized by its second argument c.
 * Here myfun is an M-file function such as
 * 
 * function f = myfun(x,c) f = x(1)^2 + c*x(2)^2;
 * 
 * To optimize for a specific value of c, first assign the value to c. Then
 * create a one-argument anonymous function that captures that value of c and
 * calls myfun with two arguments. Finally, pass this anonymous function to
 * FMINSEARCH:
 * 
 * c = 1.5; * define parameter first x = fminsearch(@(x) myfun(x,c),[0.3;1])
 * 
 * FMINSEARCH uses the Nelder-Mead simplex (direct search) method.
 * 
 * See also OPTIMSET, FMINBND, FUNCTION_HANDLE.
 * 
 * Reference: Jeffrey C. Lagarias, James A. Reeds, Margaret H. Wright, Paul E.
 * Wright, "Convergence Properties of the Nelder-Mead Simplex Method in Low
 * Dimensions", SIAM Journal of Optimization, 9(1): p.112-147, 1998.
 * 
 * @author Sione
 */
public class FminSearch
{

    private Matrix xOptimVals;
    private double funcOptimVal;
    private int exitflag;
    private HashMap<String, Object> output = new HashMap<String, Object>();

    public FminSearch(FunctionFunctions funfcn, Matrix x)
    {
        this(funfcn, x, null, (Object[]) null);
    }

    // [x,fval,exitflag,output] = fminsearch(funfcn,x,options,varargin)
    public FminSearch(FunctionFunctions funfcn, Matrix X, OptimOpt opt, Object... varargin)
    {

        if (funfcn == null)
        {
            throw new ConditionalException("FminSearch :  Parameter \"funfcn\" must be non-null.");

        }
        if (X == null)
        {
            throw new ConditionalException("FminSearch :  Matrix parameter \"X\" must be non-null.");

        }

        Matrix x = X.copy();
        int rowX = x.getRowDimension();
        int colX = x.getColumnDimension();

        int n = x.numel();
        int numberOfVariables = n;

        // ---------------------------------------------
        OptimOpt defaultopt = new OptimOpt();
        // 'Display','notify','MaxIter','200*numberOfVariables',...
        // 'MaxFunEvals','200*numberOfVariables','TolX',1e-4,'TolFun',1e-4, ...
        // 'FunValCheck','off','OutputFcn',[],'PlotFcns',[]);
        defaultopt.setDisplay(OptDisplay.Notify);
        defaultopt.setMaxIter(new Integer(200 * numberOfVariables));
        defaultopt.setMaxFunEvals(new Integer(200 * numberOfVariables));
        defaultopt.setTolFun(1.0E-4);
        defaultopt.setTolX(1.0E-4);
        defaultopt.setFunValCheck(false);
        defaultopt.setOutputFcn(null);
        defaultopt.setPlotFcns(null);

        OptimOpt options = null;
        if (opt == null)
        {
            options = new OptimOpt();
            options.setTolFun(1.0E-4);
            options.setTolX(1.0E-4);
        }
        else
        {
            options = opt;
        }
        // options.setDefaultOpt(defaultopt);
        // ---------------------------------------------
        OptDisplay printtype = (OptDisplay) OptimOpt.optimget(options.getDisplay(), defaultopt.getDisplay());
        Double tolx = (Double) OptimOpt.optimget(options.getTolX(), defaultopt.getTolX());
        Double tolf = (Double) OptimOpt.optimget(options.getTolFun(), defaultopt.getTolFun());
        Integer maxfun = (Integer) OptimOpt.optimget(options.getMaxFunEvals(), defaultopt.getMaxFunEvals());
        Integer maxiter = (Integer) OptimOpt.optimget(options.getMaxIter(), defaultopt.getMaxIter());
        Boolean funValCheck = (Boolean) OptimOpt.optimget(options.isFunValCheck(), defaultopt.isFunValCheck());

        /*
         * try { printtype = (OptDisplay) options.optimget("Display",
         * defaultopt)
         * ;//.getDisplay();//optimget(options,'Display',defaultopt,'fast');
         * tolx = (Double) options.optimget("TolX",
         * defaultopt);//options.getTolX
         * ();//optimget(options,'TolX',defaultopt,'fast'); tolf = (Double)
         * options.optimget("TolFun",
         * defaultopt);//options.getTolFun();//optimget
         * (options,'TolFun',defaultopt,'fast'); maxfun = (Integer)
         * options.optimget("MaxFunEvals", defaultopt);//(Integer)
         * options.getMaxFunEvals
         * ();//optimget(options,'MaxFunEvals',defaultopt,'fast'); maxiter =
         * (Integer) options.optimget("MaxIter", defaultopt);//(Integer)
         * options.getMaxIter();//optimget(options,'MaxIter',defaultopt,'fast');
         * funValCheck = (Boolean) options.optimget("FunValCheck",
         * defaultopt);//options.isFunValCheck() ==
         * true;//strcmp(optimget(options
         * ,'FunValCheck',defaultopt,'fast'),'on'); } catch
         * (NoSuchMethodException nme) { throw new
         * ConditionalException("FminSearch :  Non-existent method found."); }
         */

        if (maxiter < 200 * numberOfVariables)
        {
            maxiter = 200 * numberOfVariables;
            maxfun = 200 * numberOfVariables;
        }

        Object outputfcn = options.getOutputFcn();
        int prnt = -1;
        switch (printtype)
        {
        case Notify:
        {
            prnt = 1;
            break;
        }
        case Off:
        {
            prnt = 0;
            break;
        }
        case Iter:
        {
            prnt = 3;
            break;
        }
        case Final:
        {
            prnt = 2;
            break;
        }
        case Simplex:
        {
            prnt = 4;
            break;
        }
        default:
            prnt = 1;
        }
        // end

        boolean haveoutputfcn = false;
        if (outputfcn != null)
        {
            haveoutputfcn = true;
        }

        Object plotfcns = options.getPlotFcns();
        boolean haveplotfcn = false;
        if (plotfcns != null)
        {
            haveplotfcn = true;
        }

        String header = " Iteration   Func-count     min f(x)         Procedure";

        // Convert to function handle as needed.
        // funfcn = fcnchk(funfcn,length(varargin));

        // Add a wrapper function to check for Inf/NaN/complex values
        if (funValCheck)
        {
            // Add a wrapper function, CHECKFUN, to check for NaN/complex values
            // without
            // having to change the calls that look like this:
            // f = funfcn(x,varargin{:});
            // x is the first argument to CHECKFUN, then the user's function,
            // then the elements of varargin. To accomplish this we need to add
            // the
            // user's function to the beginning of varargin, and change funfcn
            // to be
            // CHECKFUN.
            // --------------------------------------------------------
            // varargin = {funfcn, varargin{:}};
            // funfcn = @checkfun;
            throw new ConditionalException("FminSearch :  To be implemented.");
        }// end

        // Initialize parameters
        double rho = 1;
        double chi = 2;
        double psi = 0.5;
        double sigma = 0.5;

        Indices onesn = Indices.ones(1, n).minus(1);
        Indices two2np1 = Indices.linspace(1, n);// 2:n+1;
        Indices one2n = Indices.linspace(0, n - 1);

        // Set up a simplex near the initial guess.
        Matrix xin = null;// x(:); // Force xin to be a column vector
        if (!x.isColVector())
        {
            xin = x.toColVector();
        }
        else
        {
            xin = x.copy();
        }
        Matrix v = Matrix.zeros(n, n + 1);
        Matrix fv = Matrix.zeros(1, n + 1);
        // v(:,1) = xin; // Place input guess in the simplex! (credit L.Pfeffer
        // at Stanford)
        v.setColumnAt(0, xin);

        // x(:) = xin; // Change x to the form expected by funfcn
        x = xin.reshape(rowX, colX);

        // fv(:,1) = funfcn(x,varargin{:});
        funfcn.funfunc(1, x, varargin);
        // funfcn.funfunc(1, x, 1); //original line is the commented line above.
        Matrix mat = (Matrix) funfcn.output()[0];
        // Matrix mat2 = (Matrix) funfcn.output()[1];
        // mat.printInLabel("mat " + 1, 8);
        fv.setColumnAt(0, mat);
        // fv.printInLabel("fv 1");

        int func_evals = 1;
        int itercount = 0;
        String how = "";

        // Initialize the output and plot functions.
        if (haveoutputfcn || haveplotfcn)
        {
            boolean stop = false;
            // [xOutputfcn, optimValues, stop] =
            // callOutputAndPlotFcns(outputfcn,plotfcns,v(:,1),xOutputfcn,'init',itercount,
            // ...
            // func_evals, how, fv(:,1),varargin{:});

            if (stop)
            {
                // [x,fval,exitflag,output] =
                // cleanUpInterrupt(xOutputfcn,optimValues);
                if (prnt > 0)
                {
                    // disp(output.message)
                    throw new ConditionalException("FminSearch :  To be implemented.");
                }// end
                return;
            }// end
            throw new ConditionalException("FminSearch :  To be implemented.");
        }// end

        // Print out initial f(x) as 0th iteration
        if (prnt == 3)
        {
            // disp(' ')
            System.out.println(" ");
            // disp(header)
            System.out.println("" + header);
            // disp(sprintf(' %5.0f %5.0f %12.6g %s', itercount, func_evals,
            // fv(1), how));

            String formatted = " " + itercount + "        " + func_evals + "     " + fv.start() + "         " + how;// ,
                                                                                                                    // itercount,
                                                                                                                    // func_evals,
                                                                                                                    // fv.start(),
                                                                                                                    // how);
            System.out.println("" + formatted);

        }
        else if (prnt == 4)
        {
            // clc
            // formatsave = get(0,{'format','formatspacing'});
            // format compact
            // format short e

            System.out.println(" ");
            // disp(how)
            System.out.println("" + how);
            // v
            v.printInLabel("v");
            System.out.println(" ");
            // fv
            fv.printInLabel("fv");
            System.out.println(" ");
            // func_evals
            System.out.println(" func_evals = " + func_evals);

        }// end

        // OutputFcn and PlotFcns call
        if (haveoutputfcn || haveplotfcn)
        {
            boolean stop = false;
            // [xOutputfcn, optimValues, stop] =
            // callOutputAndPlotFcns(outputfcn,plotfcns,v(:,1),xOutputfcn,'iter',itercount,
            // ...
            // func_evals, how, fv(:,1),varargin{:});
            if (stop)
            { // Stop per user request.
              // [x,fval,exitflag,output] =
              // cleanUpInterrupt(xOutputfcn,optimValues);
                if (prnt > 0)
                {
                    // disp(output.message)
                    throw new ConditionalException("FminSearch :  To be implemented.");
                }// end
                return;
            }// end
            throw new ConditionalException("FminSearch :  To be implemented.");
        }// end

        // Continue setting up the initial simplex.
        // Following improvement suggested by L.Pfeffer at Stanford
        double usual_delta = 0.05; // 5 percent deltas for non-zero terms
        double zero_term_delta = 0.00025; // Even smaller delta for zero
                                          // elements of x
        double val = 0.0;
        for (int j = 0; j < n; j++)
        {
            Matrix y = xin.copy();
            if (y.getElementAt(j) != 0.0)
            {
                // y(j) = (1 + usual_delta)*y(j);
                val = (1.0 + usual_delta) * y.getElementAt(j);
            }
            else
            {
                // y(j) = zero_term_delta;
                val = zero_term_delta;
            }// end
            y.setElementAt(j, val);

            // v(:,j+1) = y;
            v.setColumnAt(j + 1, y);

            // x(:) = y;
            x = y.reshape(rowX, colX);

            // f = funfcn(x,varargin{:});
            funfcn.funfunc(1, x, varargin);
            // funfcn.funfunc(1, x, (1 + (j + 1))); //original line is the
            // commented line above.
            Matrix f = (Matrix) funfcn.output()[0];
            // f.printInLabel("f " + (1 + (j + 1)), 8);

            // fv(1,j+1) = f;
            fv.set(0, j + 1, f.start());
        }// end

        // fv.printInLabel("fv 2");

        // sort so v(1,:) has the lowest function value
        // [fv,j] = sort(fv);
        QuickSort sort = new QuickSortMat(fv, null, true);
        fv = (Matrix) sort.getSortedObject();
        Indices j = sort.getIndices();

        // v = v(:,j);
        v = v.getColumns(j.getRowPackedCopy());
        // v.printInLabel("v : FminSearch", 8);

        how = "initial simplex";
        itercount = itercount + 1;
        func_evals = n + 1;
        if (prnt == 3)
        {
            // disp(sprintf(' %5.0f %5.0f %12.6g %s', itercount, func_evals,
            // fv(1), how))
        }
        else if (prnt == 4)
        {
            // disp(' ')
            // disp(how)
            // v
            // fv
            // func_evals
        }// end

        // OutputFcn and PlotFcns call
        if (haveoutputfcn || haveplotfcn)
        {
            boolean stop = false;
            // [xOutputfcn, optimValues, stop] =
            // callOutputAndPlotFcns(outputfcn,plotfcns,v(:,1),xOutputfcn,'iter',itercount,
            // ...
            // func_evals, how, fv(:,1),varargin{:});
            if (stop)
            { // Stop per user request.
              // [x,fval,exitflag,output] =
              // cleanUpInterrupt(xOutputfcn,optimValues);
                if (prnt > 0)
                {
                    // disp(output.message)
                    throw new ConditionalException("FminSearch :  To be implemented.");
                }// end
                return;
            }// end
            throw new ConditionalException("FminSearch :  To be implemented.");
        }// end

        exitflag = 1;

        /*--------------------- Main algorithm: iterate until ------------------*
         * (a) the maximum coordinate difference between the current best point and the
         * other points in the simplex is less than or equal to TolX. Specifically,
         * until max(||v2-v1||,||v2-v1||,...,||v(n+1)-v1||) <= TolX,
         * where ||.|| is the infinity-norm, and v1 holds the
         * vertex with the current lowest value; AND
         * (b) the corresponding difference in function values is less than or equal
         * to TolFun. (Cannot use OR instead of AND.)
         * The iteration stops if the maximum number of iterations or function evaluations
         * are exceeded.
         *-----------------------------------------------------------------------*/

        int[] onesnArr = onesn.getRowPackedCopy();
        int[] two2np1Arr = two2np1.getRowPackedCopy();
        int debugStop = 0;

        // //////////////////////////////////////////////////////////////////////
        while (func_evals < maxfun && itercount < maxiter)
        {
            // max(abs(fv(1)-fv(two2np1))) <= tolf &&
            // max(max(abs(v(:,two2np1)-v(:,onesn)))) <= tolx
            debugStop++;

            if (debugStop == 2)
            {
                // System.out.println("debugStop = " + debugStop);
            }

            Matrix tmp = fv.getEls(two2np1Arr).uminus().plus(fv.start()).abs();
            boolean tf = JDatafun.max(tmp).start() <= Math.max(tolf, 10.0 * JSpecfun.EPS(fv.start()));// max(abs(fv(1)-fv(two2np1)))
                                                                                                      // <=
                                                                                                      // tolf;
            tmp = v.getColumns(two2np1Arr).minus(v.getColumns(onesnArr)).abs();// v(:,two2np1)-v(:,onesn);
            boolean tf2 = JDatafun.max(JDatafun.max(tmp)).start() <= Math.max(tolx,
                    10.0 * JSpecfun.EPS(JDatafun.max(v.getColumnAt(0)).start()));// tolx;

            if (tf && tf2)
            {// max(abs(fv(1)-fv(two2np1))) <= tolf &&
             // max(max(abs(v(:,two2np1)-v(:,onesn)))) <= tolx
                break;
            }// end

            // Compute the reflection point

            // xbar = average of the n (NOT n+1) best points
            tmp = v.getColumns(one2n.getRowPackedCopy());
            Matrix xbar = JDatafun.sum(tmp, Dimension.COL).arrayRightDivide((double) n);// sum(v(:,one2n),
                                                                                        // 2)/n;
            int end = v.getColumnDimension();
            tmp = v.getColumnAt(end - 1).arrayTimes(rho);
            Matrix xr = xbar.arrayTimes(rho + 1.0).minus(tmp);// (1 + rho)*xbar
                                                              // - rho*v(:,end);
            // x(:) = xr;
            x = xr.reshape(rowX, colX);

            double fxr = 0.0;
            funfcn.funfunc(1, x, varargin);
            // funfcn.funfunc(1, x, func_evals ); //original line is the
            // commented line above.
            fxr = ((Matrix) funfcn.output()[0]).start();

            func_evals = func_evals + 1;

            if (fxr < fv.getColumnAt(0).start())
            {
                // Calculate the expansion point
                end = v.getColumnDimension();
                tmp = v.getColumnAt(end - 1).arrayTimes(rho * chi);
                Matrix xe = xbar.arrayTimes(1.0 + rho * chi).minus(tmp);// (1 +
                                                                        // rho*chi)*xbar
                                                                        // -
                                                                        // rho*chi*v(:,end);
                // x(:) = xe;
                x = xe.reshape(rowX, colX);
                funfcn.funfunc(1, x, varargin);
                // funfcn.funfunc(1, x, func_evals); //original line is the
                // commented line above.
                double fxe = ((Matrix) funfcn.output()[0]).start();

                func_evals = func_evals + 1;
                if (fxe < fxr)
                {
                    // v(:,end) = xe;
                    v.setColumnAt(end - 1, xe);
                    // fv(:,end) = fxe;
                    tmp = new Matrix(fv.getRowDimension(), 1, fxe);
                    end = fv.getColumnDimension();
                    fv.setColumnAt(end - 1, tmp);

                    how = "expand";
                }
                else
                {
                    // v(:,end) = xr;
                    v.setColumnAt(end - 1, xr);
                    // fv(:,end) = fxr;
                    tmp = new Matrix(fv.getRowDimension(), 1, fxr);
                    end = fv.getColumnDimension();
                    fv.setColumnAt(end - 1, tmp);
                    how = "reflect";
                }// end
            }
            else
            {// * fv(:,1) <= fxr
                if (fxr < fv.getColumnAt(n - 1).start())
                {
                    // v(:,end) = xr;
                    end = v.getColumnDimension();
                    v.setColumnAt(end - 1, xr);
                    // fv(:,end) = fxr;
                    tmp = new Matrix(fv.getRowDimension(), 1, fxr);
                    end = fv.getColumnDimension();
                    fv.setColumnAt(end - 1, tmp);
                    how = "reflect";
                }
                else
                { // * fxr >= fv(:,n)
                  // Perform contraction
                    end = fv.getColumnDimension();
                    if (fxr < fv.getColumnAt(end - 1).start())
                    {
                        // Perform an outside contraction
                        end = v.getColumnDimension();
                        tmp = v.getColumnAt(end - 1).arrayTimes(psi * rho);
                        Matrix xc = xbar.arrayTimes(1.0 + psi * rho).minus(tmp);// (1
                                                                                // +
                                                                                // psi*rho)*xbar
                                                                                // -
                                                                                // psi*rho*v(:,end);
                        x = xc.reshape(rowX, colX);
                        funfcn.funfunc(1, x, varargin);
                        // funfcn.funfunc(1, x, func_evals); //original line is
                        // the commented line above.
                        double fxc = ((Matrix) funfcn.output()[0]).start();
                        func_evals = func_evals + 1;

                        if (fxc <= fxr)
                        {
                            // v(:,end) = xc;
                            end = v.getColumnDimension();
                            v.setColumnAt(end - 1, xc);
                            end = fv.getColumnDimension();
                            // fv(:,end) = fxc;
                            tmp = new Matrix(fv.getRowDimension(), 1, fxc);
                            fv.setColumnAt(end - 1, tmp);
                            how = "contract outside";
                        }
                        else
                        {
                            // perform a shrink
                            how = "shrink";
                        }// end
                    }
                    else
                    {
                        // Perform an inside contraction
                        end = v.getColumnDimension();
                        tmp = v.getColumnAt(end - 1).arrayTimes(psi);
                        Matrix xcc = xbar.arrayTimes(1.0 - psi).plus(tmp);// (1-psi)*xbar
                                                                          // +
                                                                          // psi*v(:,end);
                        // x(:) = xcc;
                        x = xcc.reshape(rowX, colX);
                        funfcn.funfunc(1, x, varargin);
                        // funfcn.funfunc(1, x, func_evals); //original line is
                        // the commented line above.
                        double fxcc = ((Matrix) funfcn.output()[0]).start();
                        func_evals = func_evals + 1;

                        end = fv.getColumnDimension();
                        if (fxcc < fv.getColumnAt(end - 1).start())
                        {
                            end = v.getColumnDimension();
                            // v(:,end) = xcc;
                            v.setColumnAt(end - 1, xcc);

                            end = fv.getColumnDimension();
                            // fv(:,end) = fxcc;

                            tmp = new Matrix(fv.getRowDimension(), 1, fxcc);
                            fv.setColumnAt(end - 1, tmp);

                            how = "contract inside";
                        }
                        else
                        {
                            // perform a shrink
                            how = "shrink";
                        }// end
                    }// end
                    if ("shrink".equals(how))
                    {
                        int count = 0;
                        for (int jj = two2np1.getElementAt(count); count < two2np1.length(); count++)
                        {
                            // v(:,jj)=v(:,1)+sigma*(v(:,jj) - v(:,1));
                            tmp = v.getColumnAt(jj).minus(v.getColumnAt(0)).arrayTimes(sigma);
                            tmp = v.getColumnAt(0).plus(tmp);
                            v.setColumnAt(jj, tmp);
                            // x(:) = v(:,jj);
                            x = v.getColumnAt(jj).reshape(rowX, colX);
                            end = fv.getColumnDimension();
                            // fv(:,jj) = funfcn(x,varargin{:});
                            funfcn.funfunc(1, x, varargin);
                            // funfcn.funfunc(1, x, func_evals); //original line
                            // is the commented line above.
                            val = ((Matrix) funfcn.output()[0]).start();
                            tmp = new Matrix(fv.getRowDimension(), 1, val);
                            fv.setColumnAt(jj, tmp);
                        }// end
                        func_evals = func_evals + n;
                    }// end
                }// end
            }// end

            // [fv,j] = sort(fv);
            sort = new QuickSortMat(fv, null, true);
            fv = (Matrix) sort.getSortedObject();
            j = sort.getIndices();

            // v = v(:,j);
            v = v.getColumns(j.getRowPackedCopy());

            itercount = itercount + 1;
            if (prnt == 3)
            {
                // disp(sprintf(' %5.0f %5.0f %12.6g %s', itercount, func_evals,
                // fv(1), how))
                String formatted = " " + itercount + "        " + func_evals + "     " + fv.start() + "         " + how;// ,
                                                                                                                        // itercount,
                                                                                                                        // func_evals,
                                                                                                                        // fv.start(),
                                                                                                                        // how);
                System.out.println("" + formatted);
            }
            else if (prnt == 4)
            {
                // disp(' ')
                System.out.println(" ");
                // disp(how)
                System.out.println("" + how);
                // v
                v.printInLabel("v");
                System.out.println(" ");
                // fv
                fv.printInLabel("fv");
                System.out.println(" ");
                // func_evals
                System.out.println(" func_evals = " + func_evals);
            }// end

            // OutputFcn and PlotFcns call
            if (haveoutputfcn || haveplotfcn)
            {

                boolean stop = false;
                // [xOutputfcn, optimValues, stop] =
                // callOutputAndPlotFcns(outputfcn,plotfcns,v(:,1),xOutputfcn,'iter',itercount,
                // ...
                // func_evals, how, fv(:,1),varargin{:});
                if (stop)
                { // Stop per user request.
                  // [x,fval,exitflag,output] =
                  // cleanUpInterrupt(xOutputfcn,optimValues);
                    if (prnt > 0)
                    {
                        // disp(output.message)
                        throw new ConditionalException("FminSearch :  To be implemented.");
                    }// end
                    return;
                }// end
                throw new ConditionalException("FminSearch :  To be implemented.");
            }// end

        }// end // while
         // //////////////////////////////////////////////////////////////////////

        // x(:) = v(:,1);
        x = v.getColumnAt(0).reshape(rowX, colX);
        // x.printInLabel("x");
        this.xOptimVals = x.copy();
        // fval = fv(:,1);
        double fval = fv.getColumnAt(0).start();
        // System.out.println(" ");
        // System.out.println(" fval = " + fval);
        this.funcOptimVal = fval;

        if (prnt == 4)
        {
            // reset format
            // set(0,{'format','formatspacing'},formatsave);
        }// end

        output.put("iterations", new Integer(itercount));
        output.put("funcCount", new Integer(func_evals));
        output.put("algorithm", "Nelder-Mead simplex direct search");

        // OutputFcn and PlotFcns call
        if (haveoutputfcn || haveplotfcn)
        {
            // callOutputAndPlotFcns(outputfcn,plotfcns,x,xOutputfcn,'done',itercount,
            // func_evals, how, fval, varargin{:});
        }// end

        String msg = "";
        if (func_evals >= maxfun)
        {
            msg = "Exiting: Maximum number of function evaluations has been exceeded\n"
                    + "         - increase MaxFunEvals option.\n" + "         Current function value: " + fval + " \n";
            if (prnt > 0)
            {
                // disp(' ')
                System.out.println(" ");
                // disp(msg)
                System.out.println("" + msg);
            }// end
            exitflag = 0;
        }
        else if (itercount >= maxiter)
        {
            msg = "Exiting: Maximum number of iterations has been exceeded\n" + "         - increase MaxIter option.\n"
                    + "         Current function value: " + fval + " \n";
            if (prnt > 0)
            {
                // /disp(' ')
                System.out.println(" ");
                // disp(msg)
                System.out.println("" + msg);
            }// end
            exitflag = 0;
        }
        else
        {
            msg = "Optimization terminated:\n"
                    + " the current x satisfies the termination criteria using OPTIONS.TolX of " + tolx + " \n"
                    + " and F(X) satisfies the convergence criteria using OPTIONS.TolFun of " + tolf + " \n";
            if (prnt > 1)
            {
                // disp(' ')
                System.out.println(" ");
                // disp(msg)
                System.out.println("" + msg);
            }// end
            exitflag = 1;
        }// end

        output.put("message", msg);
    }

    /**
     * @return the xOptimVals
     */
    public Matrix getxOptimVals()
    {
        return xOptimVals;
    }

    /**
     * @return the funcOptimVal
     */
    public double getFuncOptimVal()
    {
        return funcOptimVal;
    }

    /**
     * @return the exitflag
     */
    public int getExitflag()
    {
        return exitflag;
    }

    /**
     * @return the output
     */
    public HashMap<String, Object> getOutput()
    {
        return output;
    }
}
