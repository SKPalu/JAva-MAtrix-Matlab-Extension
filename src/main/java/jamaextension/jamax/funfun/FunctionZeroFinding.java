/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.funfun;

import java.util.HashMap;

import jamaextension.jamax.ConditionalRuleException;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.Optim;
import jamaextension.jamax.constants.OptDisplay;

/**
 * 
 * @author Sione
 */
public class FunctionZeroFinding
{

    private double b = 0.0;
    private double fval = 0.0;
    private int exitflag = 1;
    private HashMap<String, Object> output = new HashMap<String, Object>();

    public static interface Function
    {

        public abstract double f(double d, Object[] obj);
    }

    public FunctionZeroFinding()
    {
    }

    /**
     * @return the b
     */
    public double getB()
    {
        return b;
    }

    /**
     * @return the fval
     */
    public double getFval()
    {
        return fval;
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

    public synchronized void fZero(Function FunFcn, double x[], Optim options, Object[] varargin)
    {

        // Initialization
        int fcount = 0;
        int iter = 0;
        int intervaliter = 0;
        String msg = "";

        String procedure = " ";

        if (FunFcn == null)
        {
            msg = "Parameter \"FunFcn\" must be non-null.";
            throw new ConditionalRuleException("computeZeros", msg);
        }
        if (x == null || x.length == 0)
        {
            msg = "Parameter \"x\" must be non-null or empty.";
            throw new ConditionalRuleException("computeZeros", msg);
        }
        if (x.length > 2)
        {
            msg = "Array parameter \"x\" must have a length of at most 2;";
            throw new ConditionalRuleException("computeZeros", msg);
        }
        if (options == null)
        {
            options = new Optim();
            options.setDisplay(OptDisplay.Notify);
            options.setTolX(MathUtil.EPS);
            options.setFunValCheck(false);
        }
        if (varargin == null || varargin.length == 0)
        {
            msg = "Parameter \"varargin\" must be non-null or empty.";
            throw new ConditionalRuleException("computeZeros", msg);
        }

        double tol = options.getTolX();// optimget(options,'TolX',defaultopt,'fast');
        // System.out.println(" tol = "+tol);
        boolean funValCheck = options.isFunValCheck();// strcmp(optimget(options,'FunValCheck',defaultopt,'fast'),'on')

        OptDisplay printtype = options.getDisplay();

        int trace = 1;

        if ("notify".equals(printtype.toString()))
        {
            trace = 1;
        }
        else if ("none".equals(printtype.toString()) || "off".equals(printtype.toString()))
        {
            trace = 0;
        }
        else if ("iter".equals(printtype.toString()))
        {
            trace = 3;
        }
        else if ("final".equals(printtype.toString()))
        {
            trace = 2;
        }
        else
        {
            trace = 1;
        }

        // Handle the output functions
        Object outputfcn = options.getOutputFcn();// optimget(options,'OutputFcn',defaultopt,'fast')
        boolean haveoutputfcn = false;
        if (outputfcn != null)
        {
            haveoutputfcn = true;
            // Parse OutputFcn which is needed to support cell array syntax for
            // OutputFcn.
            // outputfcn = createCellArrayOfFunctions(outputfcn,'OutputFcn');
        }

        boolean haveplotfcn = false;

        // Add a wrapper function to check for Inf/NaN/complex values
        if (funValCheck)
        {
            // varargin = {FunFcn, varargin{:}};
            // FunFcn = @checkfun;
        }

        // Initialize the output and plot functions.
        if (haveoutputfcn || haveplotfcn)
        {
            msg = "This functionality is not yet implemented.";
            throw new ConditionalRuleException("computeZeros", msg);

        }

        if (!(new Matrix(x).isfiniteBoolean()))
        {// (~isfinite(x))
         // error('MATLAB:fzero:Arg2NotFinite', 'Second argument must be
         // finite.')
            msg = "All elements of array parameter \"x\" must be finite.";
            throw new ConditionalRuleException("computeZeros", msg);
        }

        double a = 0.0;
        double c = 0.0;
        double d = 0.0;
        double e = 0.0;
        double savea = 0.0;
        double saveb = 0.0;
        double fa = 0.0;
        double fb = 0.0;
        double fc = 0.0;
        double savefa = 0.0;
        double savefb = 0.0;
        double fx = 0.0;

        double s = 0.0;
        double p = 0.0;
        double q = 0.0;
        double r = 0.0;

        // clear any past output
        output.clear();

        // //////////////////////////////////////////////////////
        // Interval input
        if (x.length == 2)
        {
            if (trace > 2)
            {
                // disp(' ') //Initial blank line
            }
            a = x[0];
            savea = a;
            b = x[1];
            saveb = b;
            // Put first feval in try catch
            try
            {
                fa = FunFcn.f(a, varargin);
            }
            catch (Exception ex)
            {

                msg = "Zero-finding cannot continue because user supplied value failed.";
                throw new ConditionalRuleException("computeZeros", msg);

            }

            fb = FunFcn.f(b, varargin);
            double[] fafb =
            {
                    fa, fb
            };
            Matrix matFaFb = new Matrix(fafb);
            if (matFaFb.isNotFinite())
            {
                msg = "Function values at interval endpoints must be finite and real.";
                throw new ConditionalRuleException("computeZeros", msg);
            }
            fcount = fcount + 2;
            savefa = fa;
            savefb = fb;

            if (fa == 0)
            {
                b = a;
                msg = "Zero find terminated.";
                if (trace > 1)
                {
                    // disp(msg)
                    System.out.println("" + msg);
                }
                output.put("intervaliterations", new Integer(intervaliter));// output.intervaliterations
                                                                            // =
                                                                            // intervaliter;
                output.put("iterations", new Integer(iter));// output.iterations
                                                            // = iter;
                output.put("funcCount", new Integer(fcount));// output.funcCount
                                                             // = fcount;
                output.put("algorithm", "bisection, interpolation");// output.algorithm
                                                                    // =
                                                                    // 'bisection,
                                                                    // interpolation';
                output.put("message", msg + "");// output.message = msg;
                fval = fa;
                return;
            }
            else if (fb == 0)
            {
                // b = b;
                msg = "Zero find terminated.";
                if (trace > 1)
                {
                    // disp(msg)
                    System.out.println("" + msg);
                }
                output.put("intervaliterations", new Integer(intervaliter));// output.intervaliterations
                                                                            // =
                                                                            // intervaliter;
                output.put("iterations", new Integer(iter));// output.iterations
                                                            // = iter;
                output.put("funcCount", new Integer(fcount));// output.funcCount
                                                             // = fcount;
                output.put("algorithm", "bisection, interpolation");// output.algorithm
                                                                    // =
                                                                    // 'bisection,
                                                                    // interpolation';
                output.put("message", msg + "");// output.message = msg;

                fval = fb;
                return;
            }
            else if ((fa > 0.0) == (fb > 0.0))
            {
                msg = "The function values at the interval endpoints must differ in sign.";
                throw new ConditionalRuleException("computeZeros", msg);
            }

            // Starting guess scalar input
        }
        else if (x.length == 1)
        {
            // //////////////////////////////////////////////////////////////////
            if (trace > 2)
            {
                msg = "Search for an interval around { " + x + " } containing a sign change:\n";
                System.out.println("" + msg);
            }
            // Put first feval in try catch
            try
            {
                fx = FunFcn.f(x[0], varargin);
            }
            catch (Exception ex)
            {
                msg = "Zero-finding cannot continue because user supplied value failed.";
                throw new ConditionalRuleException("computeZeros", msg);
            }
            fcount = fcount + 1;
            if (fx == 0.0)
            {
                b = x[0];
                msg = "Zero find terminated.";
                if (trace > 1)
                {
                    // disp(msg)
                    System.out.println("" + msg);
                }

                output.put("intervaliterations", new Integer(intervaliter));// output.intervaliterations
                                                                            // =
                                                                            // intervaliter;
                output.put("iterations", new Integer(iter));// output.iterations
                                                            // = iter;
                output.put("funcCount", new Integer(fcount));// output.funcCount
                                                             // = fcount;
                output.put("algorithm", "bisection, interpolation");// output.algorithm
                                                                    // =
                                                                    // 'bisection,
                                                                    // interpolation';
                output.put("message", msg + "");// output.message = msg;

                fval = fx;
                return;
            }
            else if (Double.isInfinite(fx) || Double.isNaN(fx))
            {
                msg = "Function value at starting guess must be finite and real.";
                throw new ConditionalRuleException("computeZeros", msg);
            }

            double dx = 0.0;
            if (x[0] != 0.0)
            {
                dx = x[0] / 50.0;
            }
            else
            {
                dx = 1.0 / 50.0;
            }

            // Find change of sign.
            double twosqrt = Math.sqrt(2.0);
            a = x[0];
            fa = fx;
            b = x[0];
            fb = fx;

            if (trace > 2)
            {
                // disp(header)
                procedure = "initial interval";
                // disp(sprintf('%5.0f %13.6g %13.6g %13.6g %13.6g
                // %s',fcount,a,fa,b,fb, procedure));
                msg = procedure + " : {fcount = " + fcount + " , a = " + a + " , fa = " + fa + " , b = " + b
                        + " , fb = " + fb + "} \n";
                System.out.println("" + msg);
            }
            // OutputFcn and PlotFcns call
            if (haveoutputfcn || haveplotfcn)
            {

                msg = "This functionality is not yet implemented.";
                throw new ConditionalRuleException("computeZeros", msg);
            }

            while ((fa > 0.0) == (fb > 0.0))
            {
                intervaliter = intervaliter + 1;
                dx = twosqrt * dx;
                a = x[0] - dx;
                fa = FunFcn.f(a, varargin);
                fcount = fcount + 1;
                if (Double.isInfinite(fa) || Double.isNaN(fa))
                {
                    msg = "Aborting search for an interval containing a sign change\n"
                            + "because NaN or Inf function value encountered during search.\n"
                            + "Function value at \"fa\" is " + fa + " ;";
                    b = Double.NaN;
                    fval = Double.NaN;

                    output.put("intervaliterations", new Integer(intervaliter));// output.intervaliterations
                                                                                // =
                                                                                // intervaliter;
                    output.put("iterations", new Integer(iter));// output.iterations
                                                                // = iter;
                    output.put("funcCount", new Integer(fcount));// output.funcCount
                                                                 // = fcount;
                    output.put("algorithm", "bisection, interpolation");// output.algorithm
                                                                        // =
                                                                        // 'bisection,
                                                                        // interpolation';
                    output.put("message", msg + "");// output.message = msg;
                    return;
                }

                if ((fa > 0) != (fb > 0.0))
                { // check for different sign
                  // Before we exit the while loop, print out the latest
                  // interval
                    if (trace > 2)
                    {
                        procedure = "search";
                        msg = procedure + " : {fcount = " + fcount + " , a = " + a + " , fa = " + fa + " , b = " + b
                                + " , fb = " + fb + "} \n";
                        System.out.println("" + msg);
                    }
                    // OutputFcn and PlotFcns call
                    if (haveoutputfcn || haveplotfcn)
                    {
                        msg = "This functionality is not yet implemented.";
                        throw new ConditionalRuleException("computeZeros", msg);
                    }
                    break;
                }

                b = x[0] + dx;
                fb = FunFcn.f(b, varargin);
                if (Double.isInfinite(fb) || Double.isNaN(fb))
                {
                    msg = "Aborting search for an interval containing a sign change\n"
                            + "because NaN or Inf function value encountered during search.\n"
                            + "Function value at \"fb\" is " + fb + " ;";
                    b = Double.NaN;
                    fval = Double.NaN;

                    output.put("intervaliterations", new Integer(intervaliter));// output.intervaliterations
                                                                                // =
                                                                                // intervaliter;
                    output.put("iterations", new Integer(iter));// output.iterations
                                                                // = iter;
                    output.put("funcCount", new Integer(fcount));// output.funcCount
                                                                 // = fcount;
                    output.put("algorithm", "bisection, interpolation");// output.algorithm
                                                                        // =
                                                                        // 'bisection,
                                                                        // interpolation';
                    output.put("message", msg + "");// output.message = msg;
                    return;
                }
                fcount = fcount + 1;
                if (trace > 2)
                {
                    procedure = "search";
                    msg = procedure + " : {fcount = " + fcount + " , a = " + a + " , fa = " + fa + " , b = " + b
                            + " , fb = " + fb + "} \n";
                    System.out.println("" + msg);
                }

                // OutputFcn and PlotFcns call
                if (haveoutputfcn || haveplotfcn)
                {
                    msg = "This functionality is not yet implemented.";
                    throw new ConditionalRuleException("computeZeros", msg);
                }

            }// while

            if (trace > 2)
            {
                System.out.println("Search for a zero in the interval [" + a + " , " + b + "].");
            }
            savea = a;
            savefa = fa;
            saveb = b;
            savefb = fb;
            // //////////////////////////////////////////////////////////////////
        }

        fc = fb;
        procedure = "initial";
        // header2 = ' Func-count x f(x) Procedure';
        if (trace > 2)
        {
            // disp(header2)
        }

        // Main loop, exit from middle of the loop
        while (fb != 0.0 && a != b)
        {
            // Insure that b is the best result so far, a is the previous
            // value of b, and c is on the opposite side of the zero from b.
            if ((fb > 0.0) == (fc > 0.0))
            {
                c = a;
                fc = fa;
                d = b - a;
                e = d;
            }
            if (Math.abs(fc) < Math.abs(fb))
            {
                a = b;
                b = c;
                c = a;
                fa = fb;
                fb = fc;
                fc = fa;
            }

            // Convergence test and possible exit
            double m = 0.5 * (c - b);
            double toler = 2.0 * tol * Math.max(Math.abs(b), 1.0);
            if ((Math.abs(m) <= toler) || (fb == 0.0))
            {
                break;
            }
            if (trace > 2)
            {
                msg = procedure + " : {fcount = " + fcount + " , b = " + b + " , fb = " + fb + "} \n";
                System.out.println("" + msg);
            }

            // OutputFcn and PlotFcns call
            if (haveoutputfcn || haveplotfcn)
            {
                msg = "This functionality is not yet implemented.";
                throw new ConditionalRuleException("computeZeros", msg);
            }

            // Choose bisection or interpolation
            if ((Math.abs(e) < toler) || (Math.abs(fa) <= Math.abs(fb)))
            {
                // Bisection
                d = m;
                e = m;
                procedure = "bisection";
            }
            else
            {
                // Interpolation
                s = fb / fa;
                if (a == c)
                {
                    // Linear interpolation
                    p = 2.0 * m * s;
                    q = 1.0 - s;
                }
                else
                {
                    // Inverse quadratic interpolation
                    q = fa / fc;
                    r = fb / fc;
                    p = s * (2.0 * m * q * (q - r) - (b - a) * (r - 1.0));
                    q = (q - 1.0) * (r - 1.0) * (s - 1.0);
                }
                if (p > 0)
                {
                    q = -q;
                }
                else
                {
                    p = -p;
                }
                // Is interpolated point acceptable
                if ((2.0 * p < (3.0 * m * q - Math.abs(toler * q))) && (p < Math.abs(0.5 * e * q)))
                {
                    e = d;
                    d = p / q;
                    procedure = "interpolation";
                }
                else
                {
                    d = m;
                    e = m;
                    procedure = "bisection";
                }
            }// Interpolation

            // Next point
            a = b;
            fa = fb;
            if (Math.abs(d) > toler)
            {
                b = b + d;
            }
            else if (b > c)
            {
                b = b - toler;
            }
            else
            {
                b = b + toler;
            }

            fb = FunFcn.f(b, varargin);
            fcount = fcount + 1;
            iter = iter + 1;
        }// Main loop

        fval = fb;// b is the best value

        // Output last chosen b
        if (trace > 2)
        {
            // disp(sprintf('%5.0f %13.6g %13.6g %s',fcount, b, fb, procedure));
            msg = procedure + " : {fcount = " + fcount + " , b = " + b + " , fb = " + fb + "} \n";
            System.out.println("" + msg);
        }

        // OutputFcn and PlotFcns call
        if (haveoutputfcn || haveplotfcn)
        {
            msg = "This functionality is not yet implemented.";
            throw new ConditionalRuleException("computeZeros", msg);
        }

        output.put("intervaliterations", new Integer(intervaliter));// output.intervaliterations
                                                                    // =
                                                                    // intervaliter;
        output.put("iterations", new Integer(iter));// output.iterations = iter;
        output.put("funcCount", new Integer(fcount));// output.funcCount =
                                                     // fcount;
        output.put("algorithm", "bisection, interpolation");// output.algorithm
                                                            // = 'bisection,
                                                            // interpolation';

        if (Math.abs(fval) <= Math.max(Math.abs(savefa), Math.abs(savefb)))
        {
            msg = "Zero found in the interval [ " + savea + " , " + saveb + " ]";
        }
        else
        {
            exitflag = -5;
            msg = "Current point x may be near a singular point. The interval [ " + savea + " , " + saveb + " ] \n"
                    + "reduced to the requested tolerance and the function changes sign in the interval,\n"
                    + "but f(x) increased in magnitude as the interval reduced.";
        }

        if (trace > 1)
        {
            System.out.println(" ");
            System.out.println("" + msg);
        }
        output.put("message", msg + "");// output.message = msg;
        // Outputfcn and PlotFcns call
        if (haveoutputfcn || haveplotfcn)
        {
            // callOutputAndPlotFcns(outputfcn,plotfcns,b,'done',fcount,iter,intervaliter,fval,procedure,savea,savefa,saveb,savefb,varargin{:});
            msg = "This functionality is not yet implemented.";
            throw new ConditionalRuleException("computeZeros", msg);
        }

    }
}
