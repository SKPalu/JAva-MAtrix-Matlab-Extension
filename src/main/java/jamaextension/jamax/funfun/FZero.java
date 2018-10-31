/*
 * FZero.java
 *
 * Created on 19 April 2007, 04:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jamaextension.jamax.funfun;

import java.util.HashMap;

import jamaextension.jamax.Optim;
import jamaextension.jamax.constants.OptDisplay;

/**
 * 
 * @author Feynman Perceptrons
 */
public class FZero
{

    // [b,fval,exitflag,output]
    /*
     * private int fcount = 0; private int iter = 0; private int intervaliter =
     * 0; private int exitflag = 1; private String procedure = "";
     */
    private double b = 0.0;
    private double fval = 0.0;
    private int exitflag = 1;
    // private HashMap output = new HashMap(); //--> Old line : commented on
    // 25/3/09
    private HashMap<String, Object> output = new HashMap<String, Object>(); // -->
                                                                            // New
                                                                            // line
                                                                            // :
                                                                            // added
                                                                            // on
                                                                            // 25/3/09

    public static interface Function
    {

        public abstract double f(double d, Object[] obj);
    }

    /** Creates a new instance of FZero */
    public FZero()
    {
    }

    public double getB()
    {
        return b;
    }

    public double getFval()
    {
        return fval;
    }

    public int getExitflag()
    {
        return exitflag;
    }

    public HashMap getOutput()
    {
        return output;
    }

    public synchronized void computeZeros(Function function, double[] x, Optim options, Object[] obj)
    {

        int fcount = 0;
        int iter = 0;
        int intervaliter = 0;
        int exitflag = 1;
        String procedure = "";

        double tol = options.getTolX();
        boolean funValCheck = options.isFunValCheck();
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

        double a = 0.0;
        // double b = 0.0;
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
        double fval = 0.0;
        double fx = 0.0;

        double s = 0.0;
        double p = 0.0;
        double q = 0.0;
        double r = 0.0;

        // clear any pass output
        output.clear();

        if (x.length == 2)
        {// =======================================================
            a = x[0];
            savea = a;
            b = x[1];
            saveb = b;

            fa = function.f(a, obj);
            fb = function.f(b, obj);

            fcount = fcount + 2;
            savefa = fa;
            savefb = fb;

            if (fa == 0.0)
            {
                b = a;
                // msg = sprintf('Zero find terminated.');
                if (trace > 1)
                {
                    System.out.println("Zero find terminated.");
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
                output.put("message", "Zero find terminated.");// output.message
                                                               // = msg;
                fval = fa;
                return;// new double[]{fval};
            }
            else if (fb == 0.0)
            {
                // b = b;
                // msg = sprintf('Zero find terminated.');
                if (trace > 1)
                {
                    System.out.println("Zero find terminated.");
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
                output.put("message", "Zero find terminated.");// output.message
                                                               // = msg;
                fval = fb;
                return;// new double[]{fval};
            }
            else if (fa > 0 && fb > 0)
            {
                // error('MATLAB:fzero:ValuesAtEndPtsSameSign', 'The function
                // values at the interval endpoints must differ in sign.')
                throw new IllegalArgumentException("The function values at the interval endpoints must differ in sign.");
            }

        }
        else if (x.length == 1)
        {// ==================================================
            if (trace > 2)
            {
                // disp(' ')
                // disp(['Search for an interval around ', num2str(x),'
                // containing a sign change:' ]);
                System.out.println("Search for an interval around   " + x[0] + "  containing a sign change:");
                // header = ' Func-count a f(a) b f(b) Procedure';
            }
            // Put first feval in try catch
            // try
            fx = function.f(x[0], obj);// FunFcn(x,varargin{:});
            /*
             * catch if ~isempty(Ffcnstr) es = sprintf(['FZERO cannot continue
             * because user supplied' ... ' %s ==> %s\nfailed with the error
             * below.\n\n%s '], ... Ftype,Ffcnstr,lasterr); else es =
             * sprintf(['FZERO cannot continue because user supplied' ... ' %s
             * \nfailed with the error below.\n\n%s '], ... Ftype,lasterr); end
             * error('MATLAB:fzero:InvalidFunctionSupplied', es) end
             */
            fcount = fcount + 1;
            if (fx == 0.0)
            {
                b = x[0];
                // msg = sprintf('Zero find terminated.');
                if (trace > 1)
                {
                    System.out.println("Zero find terminated.");// disp(msg)
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
                output.put("message", "Zero find terminated.");// output.message
                                                               // = msg;

                fval = fx;
                return;// new double[]{fval};
            }
            else if (Double.isInfinite(fx))
            {
                // error('MATLAB:fzero:ValueAtInitGuessComplexOrNotFinite',...
                // 'Function value at starting guess must be finite and real.');
                throw new IllegalArgumentException("Function value at starting guess must be finite and real.");
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
                // procedure='initial interval';
                // disp(sprintf('%5.0f %13.6g %13.6g %13.6g %13.6g
                // %s',fcount,a,fa,b,fb, procedure));
            }

            /*
             * //OutputFcn call if haveoutputfcn [xOutputfcn, optimValues, stop]
             * = callOutputFcn(outputfcn,x,'iter',fcount,iter,intervaliter, ...
             * fx,procedure,a,fa,b,fb,varargin{:}); // a and b are x to start if
             * stop [b,fval,exitflag,output] =
             * cleanUpInterrupt(xOutputfcn,optimValues); if trace > 0
             * disp(output.message) end return; end end
             */

            while ((fa > 0.0) == (fb > 0.0))
            {// ------------ while --------------

                intervaliter = intervaliter + 1;
                dx = twosqrt * dx;
                a = x[0] - dx;
                fa = function.f(a, obj);// FunFcn(a,varargin{:});
                fcount = fcount + 1;
                if (Double.isInfinite(fa))
                {
                    // [exitflag,msg] = disperr(a,fa,trace);
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
                    output.put("message", "[exitflag,msg] = disperr(a,fa,trace)");// output.message
                                                                                  // =
                                                                                  // msg;

                    b = Double.NaN;
                    fval = Double.NaN;
                    return;// new double[]{fval};//return
                }

                if ((fa > 0.0) != (fb > 0.0))
                { // check for different sign
                  // Before we exit the while loop, print out the latest
                  // interval
                    if (trace > 2)
                    {
                        // procedure='search';
                        // disp(sprintf('%5.0f %13.6g %13.6g %13.6g %13.6g
                        // %s',fcount,a,fa,b,fb, procedure));
                    }
                    // OutputFcn call
                    /*
                     * if haveoutputfcn [xOutputfcn, optimValues, stop] =
                     * callOutputFcn
                     * (outputfcn,x,'iter',fcount,iter,intervaliter, ...
                     * fx,procedure,a,fa,b,fb,varargin{:}); if stop
                     * [b,fval,exitflag,output] =
                     * cleanUpInterrupt(xOutputfcn,optimValues); if trace > 0
                     * disp(output.message) end return; end end
                     */
                    break;
                }

                b = x[0] + dx;
                fb = function.f(b, obj);// FunFcn(b,varargin{:});
                if (Double.isInfinite(fb))
                {
                    // [exitflag,msg] = disperr(b,fb,trace);
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
                    output.put("message", "[exitflag,msg] = disperr(b,fb,trace)");// output.message
                                                                                  // =
                                                                                  // msg;

                    b = Double.NaN;
                    fval = Double.NaN;
                    return;// new double[]{fval};
                }
                fcount = fcount + 1;

                if (trace > 2)
                {
                    // procedure='search';
                    // disp(sprintf('%5.0f %13.6g %13.6g %13.6g %13.6g
                    // %s',fcount,a,fa,b,fb, procedure));
                }
                // OutputFcn call
                /*
                 * if haveoutputfcn [xOutputfcn, optimValues, stop] =
                 * callOutputFcn(outputfcn,x,'iter',fcount,iter,intervaliter,
                 * ... fx,procedure,a,fa,b,fb,varargin{:}); if stop
                 * [b,fval,exitflag,output] =
                 * cleanUpInterrupt(xOutputfcn,optimValues); if trace > 0
                 * disp(output.message) end return; end end
                 */
            }// --------- end while ----------

            if (trace > 2)
            {
                // disp(' ')
                // disp(['Search for a zero in the interval [', num2str(a) , ',
                // ', num2str(b), ']:']);
                System.out.println("Search for a zero in the interval [" + a + " , " + b + "]");
            }
            savea = a;
            savefa = fa;
            saveb = b;
            savefb = fb;

        }
        else
        {// ==================================================================
            throw new IllegalArgumentException("Second argument (guesses) must be of length 1 or 2");
        }

        fc = fb;
        // procedure = 'initial';
        // header2 = ' Func-count x f(x) Procedure';
        if (trace > 2)
        {
            // disp(header2)
        }

        // Main loop, exit from middle of the loop
        while (fb != 0.0)
        {// ------------------------while---------------------
         // Insure that b is the best result so far, a is the previous
         // value of b, and c is on the opposite of the zero from b.
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
                // disp(sprintf('%5.0f %13.6g %13.6g %s',fcount, b, fb,
                // procedure));
            }

            // OutputFcn call
            /*
             * if haveoutputfcn [xOutputfcn, optimValues, stop] =
             * callOutputFcn(outputfcn,b,'iter',fcount,iter,intervaliter, ...
             * fb,procedure,savea,savefa,saveb,savefb,varargin{:}); if stop
             * [b,fval,exitflag,output] =
             * cleanUpInterrupt(xOutputfcn,optimValues); if trace > 0
             * disp(output.message) end return; end end
             */

            // Choose bisection or interpolation
            if ((Math.abs(e) < toler) || (Math.abs(fa) <= Math.abs(fb)))
            {
                // Bisection
                d = m;
                e = m;
                // procedure='bisection';
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
                if (p > 0.0)
                {
                    q = -q;
                }
                else
                {
                    p = -p;
                }
                // Is interpolated point acceptable
                if ((2.0 * p < 3.0 * m * q - Math.abs(toler * q)) && (p < Math.abs(0.5 * e * q)))
                {
                    e = d;
                    d = p / q;
                    // procedure='interpolation';
                }
                else
                {
                    d = m;
                    e = m;
                    // procedure='bisection';
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

            fb = function.f(b, obj);// FunFcn(b,varargin{:});
            fcount = fcount + 1;
            iter = iter + 1;
        }// ------------------------end while---------------------

        // Output last chosen b
        if (trace > 2)
        {
            // disp(sprintf('%5.0f %13.6g %13.6g %s',fcount, b, fb, procedure));
        }

        // OutputFcn call
        /*
         * if haveoutputfcn [xOutputfcn, optimValues, stop] =
         * callOutputFcn(outputfcn,b,'iter',fcount,iter,intervaliter, ...
         * fb,procedure,savea,savefa,saveb,savefb,varargin{:}); if stop
         * [b,fval,exitflag,output] = cleanUpInterrupt(xOutputfcn,optimValues);
         * if trace > 0 disp(output.message) end return; end end
         */

        output.put("intervaliterations", new Integer(intervaliter));// output.intervaliterations
                                                                    // =
                                                                    // intervaliter;
        output.put("iterations", new Integer(iter));// output.iterations = iter;
        output.put("funcCount", new Integer(fcount));// output.funcCount =
                                                     // fcount;
        output.put("algorithm", "bisection, interpolation");// output.algorithm
                                                            // = 'bisection,
                                                            // interpolation';

        fval = function.f(b, obj);// FunFcn(b,varargin{:});
        fcount = fcount + 1;

        String msg = "";
        if (Math.abs(fval) <= Math.max(Math.abs(savefa), Math.abs(savefb)))
        {
            msg = "Zero found in the interval [" + savea + " , " + saveb + "]";
        }
        else
        {
            exitflag = -5;
            msg = "Current point x may be near a singular point. The interval [" + savea + " , " + saveb + "] \n"
                    + "reduced to the requested tolerance and the function changes sign in the interval,\n"
                    + "but f(x) increased in magnitude as the interval reduced.";
        }

        if (trace > 1)
        {
            // disp(' ')
            // disp(msg)
            System.out.println(msg + "");
        }

        output.put("message", msg); // output.message = msg;

        // Outputfcn call
        /*
         * if haveoutputfcn callOutputFcn(outputfcn,b,
         * 'done',fcount,iter,intervaliter,fval,procedure,savea,savefa,saveb,savefb,varargin{:});
         * end
         */

    }
}// -------------------- End Class Definition
// -----------------------------------
