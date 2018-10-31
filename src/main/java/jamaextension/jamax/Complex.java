package jamaextension.jamax;

import java.io.Serializable;

/**
 * The class <code>Complex</code> defines a complex data type, and implements
 * operations with that data type. The methods in this class fall into two
 * categories--<b>instance methods</b> and <b>static methods</b>. The instance
 * methods are mostly designed to work on <code>Complex</code> objects and to
 * return references to <code>Complex</code> objects, so that they can be strung
 * together in expressions. For example, a <code>Complex</code> expression such
 * as
 * 
 * <pre>
 * z3 = z1 * z2 + z3
 * </pre>
 * 
 * can be implemented as
 * 
 * <pre>
 * z3 = z1.mul(z2).add(z3)
 * </pre>
 * 
 * However, <i>these methods are always evaluated from left to right</i>, so if
 * the order of operations is not purely left-to-right, you will have to use
 * parentheses or re-arrange the order of the methods to get the order of
 * operations right. For example, a <code>Complex</code> expression such as
 * 
 * <pre>
 * z3 = z1 + z2 * z3
 * </pre>
 * 
 * requires that the multiplication occur before the addition. This can be
 * implemented as
 * 
 * <pre>
 * z3 = z1.add(z2.mul(z3));
 * </pre>
 * 
 * or alternatively as
 * 
 * <pre>
 * z3 = z2.mul(z3).add(z1);
 * </pre>
 * 
 * The instance methods in this class that can be used to build such expressions
 * include <code>add</code>, <code>sub</code>, <code>mul</code>,
 * <code>div</code>, <code>pow</code>, <code>inv</code>, <code>neg</code>,
 * <code>abs</code>, <code>arg</code>, <code>cong</code>, <code>re</code>, and
 * <code>im</code>.
 * <p>
 * This class also contains many static methods that accept <code>Complex</code>
 * arguments and return appropriate values. In general, the <code>static</code>
 * methods in this class qre similar to the <code>static</code> methods in class
 * <code>java.lang.Math</code>. Examples include <code>sin(z)</code>,
 * <code>cos(z)</code>, <code>tan(z)</code>, <code>log(z)</code>, etc.
 * <p>
 * Class <code>Complex</code> is a subclass of <code>Number</code>, and it
 * overrides all of the abstract methods in that class. The overridden methods
 * in class <code>Complex</code> work with the <i>real part</i> of the
 * <code>Complex</code> number only.
 * <p>
 * This class also implements the <code>java.lang.Comparable</code> interface.
 * In this case, the comparisons are between the <i>absolute values</i> of the
 * two <code>Complex</code> numbers.
 * 
 * @author S. J. Chapman
 * @version 1.00, 07/15/98
 */
public final class Complex extends Number implements Cloneable, Serializable, Comparable
{

    // *************************************************
    // Constants
    // *************************************************
    /**
     * The <code>double</code> representation of twice PI.
     */
    private static final double TWO_PI = 2.0 * Math.PI;
    /**
     * Constant representing <i><b>i</b></i>, the square root of -1.
     * 
     */
    public static final Complex I = new Complex(0.0, 1.0);
    /**
     * Constant representing <i><b>i</b></i>, the square root of -1. (For
     * electrical engineers...)
     * 
     */
    public static final Complex J = new Complex(0.0, 1.0);
    // *************************************************
    // Instance variables
    // *************************************************
    /**
     * Real part of <tt>Complex</tt> number.
     * 
     * @serial
     */
    private double re;
    /**
     * imaginary part of <tt>Complex</tt> number.
     * 
     * @serial
     */
    private double im;

    // *************************************************
    // Constructors
    // *************************************************
    /**
     * Constructs a <tt>Complex</tt> object representing the number zero.
     * <p>
     */
    public Complex()
    {
        this.re = 0;
        this.im = 0;
    }

    /**
     * Constructs a <tt>Complex</tt> from a real number. The imaginary part is
     * zero.
     * <p>
     * 
     * @param re
     *            The real number
     *            <p>
     * @see Complex#complex(double)
     */
    public Complex(double re)
    {
        this.re = re;
        this.im = 0;
    }

    /**
     * Constructs a new <tt>Complex</tt> object from an existing
     * <tt>Complex</tt> object.
     * 
     * <p>
     * 
     * @param z
     *            A <tt>Complex</tt> value
     *            <p>
     */
    public Complex(Complex z)
    {
        this.re = z.re;
        this.im = z.im;
    }

    /**
     * Constructs a <tt>Complex</tt> object from real and imaginary parts.
     * 
     * <p>
     * 
     * @param re
     *            Real part
     * @param im
     *            Imaginary part
     *            <p>
     * @see Complex#complex(double, double)
     */
    public Complex(double re, double im)
    {
        this.re = re;
        this.im = im;
    }

    // *************************************************
    // Support for superclass Number methods
    // *************************************************
    /**
     * Returns the value of the <i><b>real</b></i> part of the specified number
     * as a <code>byte</code>. This may involve rounding.
     * 
     * @return the numeric value represented by the real part this object after
     *         conversion to type <code>byte</code>.
     */
    @Override
    public byte byteValue()
    {
        return (byte) Math.floor(re + 0.5);
    }

    /**
     * Returns the value of the <i><b>real</b></i> part of the specified number
     * as a <code>double</code>.
     * 
     * @return the numeric value represented by the real part this object after
     *         conversion to type <code>double</code>.
     */
    public double doubleValue()
    {
        return re;
    }

    /**
     * Returns the value of the <i><b>real</b></i> part of the specified number
     * as a <code>float</code>. This may involve rounding.
     * 
     * @return the numeric value represented by the real part this object after
     *         conversion to type <code>float</code>.
     */
    public float floatValue()
    {
        return (float) re;
    }

    /**
     * Returns the value of the <i><b>real</b></i> part of the specified number
     * as an <code>int</code>. This may involve rounding.
     * 
     * @return the numeric value represented by the real part this object after
     *         conversion to type <code>int</code>.
     */
    public int intValue()
    {
        return (int) Math.floor(re + 0.5);
    }

    /**
     * Returns the value of the <i><b>real</b></i> part of the specified number
     * as a <code>long</code>. This may involve rounding.
     * 
     * @return the numeric value represented by the real part this object after
     *         conversion to type <code>long</code>.
     */
    public long longValue()
    {
        return (long) Math.floor(re + 0.5);
    }

    /**
     * Returns the value of the <i><b>real</b></i> part of the specified number
     * as a <code>short</code>. This may involve rounding.
     * 
     * @return the numeric value represented by the real part this object after
     *         conversion to type <code>short</code>.
     */
    @Override
    public short shortValue()
    {
        return (short) Math.floor(re + 0.5);
    }

    /**
     * Converts a <tt>Complex</tt> value into a <tt>String</tt> of the form
     * <tt>(</tt><i>re</i><tt> + i</tt><i>im</i><tt>)</tt>.
     * 
     * @return <tt>String</tt> containing the rectangular coordinate
     *         representation of the number
     */
    @Override
    public String toString()
    {
        if (im < 0.0)
        {
            return ("(" + re + " - i " + (-im) + ")");
        }
        else if (1.0 / im == Double.NEGATIVE_INFINITY)
        {
            return ("(" + re + " + i " + 0.0 + ")");
        }
        else
        {
            return ("(" + re + " + i " + (+im) + ")");
        }
    }

    // *************************************************
    // Static methods
    // *************************************************
    public Complex copy()
    {
        return new Complex(re, im);
    }

    /**
     * Returns the absolute value (magnitude) of a <tt>Complex</tt> number.
     * 
     * @param z
     *            a <tt>Complex</tt> number
     * @return a <tt>double</tt> containing the absolute value of the
     *         <tt>Complex</tt> number
     * @see Complex#abs()
     */
    public static double abs(Complex z)
    {
        double absRe = Math.abs(z.re);
        double absIm = Math.abs(z.im);

        // This algorithm avoids overflows that might otherwise
        // occur when evaluating Math.sqrt(re*re + im*im);
        if (absRe == 0.0 && absIm == 0.0)
        {
            return 0.;
        }
        else if (absRe >= absIm)
        {
            double d = z.im / z.re;
            return absRe * Math.sqrt(1.0 + d * d);
        }
        else
        {
            double d = z.re / z.im;
            return absIm * Math.sqrt(1.0 + d * d);
        }
    }

    /**
     * Returns the principal angle of a <tt>Complex</tt> number, in radians,
     * measured counter-clockwise from the real axis. The method <tt>arg(z)</tt>
     * always returns a <tt>double</tt> between -<tt><b>PI</b></tt> and +
     * <tt><b>PI</b></tt>.
     * 
     * @param z
     *            <tt>Complex</tt> number
     * @return a <tt>double</tt> containing the principal angle of the
     *         <tt>Complex</tt> value
     * @see Complex#arg()
     */
    public static double arg(Complex z)
    {
        return Math.atan2(z.im, z.re);
    }

    /**
     * Returns the principal inverse cosine of a <tt>Complex</tt> number.
     * 
     * <pre>
     *     acos(z) = -<i>i</i> * log( z + <i>i</i> * sqrt(1 - z*z) )
     * </pre>
     * 
     * @param z
     *            a <tt>Complex</tt> number
     * @return the <tt>Complex</tt> result
     * @see Complex#cos(Complex)
     */
    public static Complex acos(Complex z)
    {

        double re1, im1; // (1 - z*z)

        double re2, im2; // i * sqrt(1 - z*z)

        double re3, im3; // log(z + i * sqrt(1 - z*z))

        Complex result; // Result of intermediate calcs

        // Build (1 - z*z)
        re1 = 1.0 - ((z.re * z.re) - (z.im * z.im));
        im1 = 0.0 - ((z.re * z.im) + (z.im * z.re));

        // Build sqrt(1 - z*z)
        result = complex(re1, im1);
        sqrt1(result);

        // Build i * sqrt(1 - z*z)
        re2 = -result.im;
        im2 = +result.re;

        // Build z + i * sqrt(1 - z*z)
        result.re = z.re + re2;
        result.im = z.im + im2;

        // Build log(z + i * sqrt(1 - z*z))
        re3 = Math.log(abs(result));
        im3 = arg(result);

        // Build -i * log(z + i * sqrt(1 - z*z))
        result.re = im3;
        result.im = -re3;
        return result;
    }

    /**
     * Returns the principal inverse hyperbolic cosine of a <tt>Complex</tt>
     * number.
     * 
     * <pre>
     * acosh(z) = log(z + sqrt(z * z - 1))
     * </pre>
     * 
     * @param z
     *            a <tt>Complex</tt> number
     * @return the <tt>Complex</tt> result
     * @see Complex#cosh(Complex)
     */
    public static Complex acosh(Complex z)
    {

        double re1, im1; // (z*z + 1)

        double re2, im2; // log(z + sqrt(z*z - 1))

        Complex result; // Result of intermediate calcs

        // Build (z*z - 1)
        re1 = ((z.re * z.re) - (z.im * z.im)) - 1.0;
        im1 = ((z.re * z.im) + (z.im * z.re));

        // Build sqrt(z*z - 1)
        result = complex(re1, im1);
        sqrt1(result);

        // Build z + sqrt(z*z - 1)
        result.re = z.re + result.re;
        result.im = z.im + result.im;

        // Build log(z + sqrt(z*z - 1))
        re2 = Math.log(abs(result));
        im2 = arg(result);

        // Build acosh(z)
        result.re = re2;
        result.im = im2;
        return result;
    }

    /**
     * Returns the principal inverse sine of a <tt>Complex</tt> number.
     * 
     * <pre>
     *     asin(z) = -<i>i</i> * log(<i>i</i>*z + sqrt(1 - z*z))
     * </pre>
     * 
     * @param z
     *            a <tt>Complex</tt> number
     * @return the <tt>Complex</tt> result
     * @see Complex#sin(Complex)
     */
    public static Complex asin(Complex z)
    {

        double re1, im1; // (1 - z*z)

        double re2, im2; // i*z

        double re3, im3; // log (i*z + sqrt(1 - z*z))

        Complex result; // Result of intermediate calcs

        // Build (1 - z*z)
        re1 = 1.0 - ((z.re * z.re) - (z.im * z.im));
        im1 = 0.0 - ((z.re * z.im) + (z.im * z.re));

        // Build sqrt(1 - z*z)
        result = complex(re1, im1);
        sqrt1(result);

        // Build iz = i*z
        re2 = -z.im;
        im2 = +z.re;

        // Build i*z + sqrt(1 - z*z)
        result.re = re2 + result.re;
        result.im = im2 + result.im;

        // Build log (i*z + sqrt(1 - z*z))
        re3 = Math.log(abs(result));
        im3 = arg(result);

        // Build -i * log (i*z + sqrt(1 - z*z))
        result.re = im3;
        result.im = -re3;
        return result;
    }

    /**
     * Returns the principal inverse hyperbolic sine of a <tt>Complex</tt>
     * number.
     * 
     * <pre>
     * asinh(z) = log(z + sqrt(z * z + 1))
     * </pre>
     * 
     * @param z
     *            a <tt>Complex</tt> number
     * @return the <tt>Complex</tt> result
     * @see Complex#sinh(Complex)
     */
    public static Complex asinh(Complex z)
    {

        double re1, im1; // (z*z + 1)

        double re2, im2; // log(z + sqrt(z*z + 1))

        Complex result; // Intermediate results of calc

        // Build z*z + 1
        re1 = ((z.re * z.re) - (z.im * z.im)) + 1.0;
        im1 = ((z.re * z.im) + (z.im * z.re));

        // Build sqrt(z*z + 1)
        result = complex(re1, im1);
        sqrt1(result);

        // Build z + sqrt(z*z + 1)
        result.re = z.re + result.re;
        result.im = z.im + result.im;

        // Build log(z + sqrt(z*z + 1))
        re2 = Math.log(result.abs());
        im2 = result.arg();

        // Build asinh(z)
        result.re = re2;
        result.im = im2;
        return result;
    }

    /**
     * Returns the principal inverse tangent of a <tt>Complex</tt> number.
     * 
     * <pre>
     *     atan(z) = -<i>i</i>/2 * log( (<i>i</i>-z)/(<i>i</i>+z) )
     * </pre>
     * 
     * @param z
     *            a <tt>Complex</tt> number
     * @return the <tt>Complex</tt> result
     * @see Complex#tan(Complex)
     */
    public static Complex atan(Complex z)
    {

        double re1, im1; // (i+z)

        double re2, im2; // log((i-z)/(i+z))

        Complex result; // Intermediate results of calc

        // Build (i-z)
        result = complex(-z.re, 1.0 - z.im);

        // Build (i+z)
        re1 = z.re;
        im1 = 1.0 + z.im;

        // Build (i-z)/(i+z)
        div(result, re1, im1);

        // Build log((i-z)/(i+z))
        re2 = Math.log(abs(result));
        im2 = arg(result);

        // Build -i/2 * log((i-z)/(i+z))
        result.re = 0.5 * im2;
        result.im = -0.5 * re2;
        return result;
    }

    /**
     * Returns the principal inverse hyperbolic tangent of a <tt>Complex</tt>
     * number.
     * 
     * <pre>
     * atanh(z) = 1 / 2 * log((1 + z) / (1 - z))
     * </pre>
     * 
     * @param z
     *            a <tt>Complex</tt> number
     * @return the <tt>Complex</tt> result
     * @see Complex#tanh(Complex)
     */
    public static Complex atanh(Complex z)
    {

        double re1, im1; // (1 - z)

        double re2, im2; // log((1+z)/(1-z))

        Complex result; // Intermediate results of calc

        // Build (1+z)
        result = complex(1.0 + z.re, +z.im);

        // Build (1-z)
        re1 = 1.0 - z.re;
        im1 = -z.im;

        // Build (1+z)/(1-z)
        div(result, re1, im1);

        // Build log((1+z)/(1-z))
        re2 = Math.log(abs(result));
        im2 = arg(result);

        // Build atanh(z) = 0.5*log((1+z)/(1-z))
        result.re = 0.5 * re2;
        result.im = 0.5 * im2;
        return result;
    }

    /**
     * Creates a new <tt>Complex</tt> number from a <tt>double</tt>. The
     * imaginary part of the <tt>Complex</tt> number is initialized to zero.
     * 
     * @param re
     *            real part
     * @return <tt>Complex</tt> of the form (re + i*0)
     * @see Complex#complex(double, double)
     */
    public static Complex complex(double re)
    {
        return new Complex(re, 0.);
    }

    /**
     * Creates a new <tt>Complex</tt> from real and imaginary parts.
     * 
     * @param re
     *            Real part
     * @param im
     *            Imaginary part
     * @return <tt>Complex</tt> number from Rectangular coordinates
     * @see Complex#complex(double)
     */
    public static Complex complex(double re, double im)
    {
        return new Complex(re, im);
    }

    /**
     * Returns the <tt>Complex</tt> conjugate of a <tt>Complex</tt> number. The
     * <tt>Complex</tt> conjugate of a number is the complex number having the
     * same real component and the negative of the imaginary component.
     * 
     * @param z
     *            <tt>Complex</tt> input parameter
     * @return <tt>Complex</tt> conjugate of <tt>z</tt>.
     */
    public static Complex conj(Complex z)
    {
        return complex(z.re, -z.im);
    }

    /**
     * Returns the cosine of a <tt>Complex</tt> number.
     * 
     * <pre>
     *     cos(z) = ( exp(<i>i</i>*z) + exp(-<i>i</i>*z) ) / (2*<i>i</i>)
     * </pre>
     * 
     * @param z
     *            an <tt>Complex</tt> angle
     * @return the <tt>Complex</tt> cosine
     * @see Complex#acos(Complex)
     */
    public static Complex cos(Complex z)
    {

        double scalar;
        double izRe, izIm; // Real and imag parts of iz

        double re1, im1; // Real and imag parts of exp(i*z)

        double re2, im2; // Real and imag parts of exp(-i*z)

        // Build i*z
        izRe = -z.im;
        izIm = z.re;

        // Build exp(i*z) using Euler's equation
        scalar = Math.exp(izRe);
        re1 = scalar * Math.cos(izIm);
        im1 = scalar * Math.sin(izIm);

        // Build exp(-i*z)
        scalar = Math.exp(-izRe);
        re2 = scalar * Math.cos(-izIm);
        im2 = scalar * Math.sin(-izIm);

        // Build exp(i*z) + exp(-i*z)
        re1 = re1 + re2;
        im1 = im1 + im2;

        // result: (exp(i*z) + exp(-i*z)) / 2
        return (complex(0.5 * re1, 0.5 * im1));
    }

    /**
     * Returns the hyperbolic cosine of a <tt>Complex</tt> number.
     * 
     * <pre>
     * cosh(z) = (exp(z) + exp(-z)) / 2
     * </pre>
     * 
     * @param z
     *            a <tt>Complex</tt> value
     * @return the <tt>Complex</tt> hyperbolic cosine
     * @see Complex#acosh(Complex)
     */
    public static Complex cosh(Complex z)
    {

        double re1, im1; // Real and imag parts of exp(z)

        double re2, im2; // Real and imag parts of exp(-z)

        double scalar;

        // Build exp(z)
        scalar = Math.exp(z.re);
        re1 = scalar * Math.cos(z.im);
        im1 = scalar * Math.sin(z.im);

        // Build exp(-z)
        scalar = Math.exp(-z.re);
        re2 = scalar * Math.cos(-z.im);
        im2 = scalar * Math.sin(-z.im);

        // Build exp(z) + exp(-z)
        re1 = re1 + re2;
        im1 = im1 + im2;

        // cosh(z) = ( exp(z) + exp(-z) ) / 2
        return complex(0.5 * re1, 0.5 * im1);
    }

    /**
     * Returns the exponential number <i>e</i> (i.e., 2.718...) raised to the
     * power of the <code>Complex</code> value z.
     * 
     * @param z
     *            a <code>double</code> value.
     * @return the value <i>e</i><sup>z</sup>, where <i>e</i> is the base of the
     *         natural logarithms.
     * @see Complex#log(Complex)
     */
    public static Complex exp(Complex z)
    {
        double scalar = Math.exp(z.re);
        return complex(scalar * Math.cos(z.im), scalar * Math.sin(z.im));
    }

    /**
     * Returns the imaginary part of a <tt>Complex</tt> as a <tt>double</tt>.
     * 
     * @param z
     *            <tt>Complex</tt> number
     * @return <tt>double</tt> containing the imaginary part of z
     *         <p>
     * @see Complex#im()
     */
    public static double im(Complex z)
    {
        return z.im;
    }

    /**
     * Returns the reciprocal of a <tt>Complex</tt> number (1/z).
     * 
     * @param z
     *            <tt>Complex</tt> number
     * @return The <tt>Complex</tt> reciprocal
     */
    public static Complex inv(Complex z)
    {
        double x = z.re;
        double y = z.im;
        double scalar, zRe, zIm;

        // Calculate to minimize roundoff errors. This algorithm is
        // taken from "Numerical Recipes in Fortran 77: The Art of
        // Scientific Computing", by Press et al
        if (Math.abs(x) >= Math.abs(y))
        {
            scalar = 1.0 / (x + y * (y / x));
            zRe = scalar;
            zIm = -scalar * (y / x);
        }
        else
        {
            scalar = 1.0 / (x * (x / y) + y);
            zRe = scalar * (x / y);
            zIm = -scalar;
        }
        return complex(zRe, zIm);
    }

    /**
     * Returns the principal natural logarithm of a <tt>Complex</tt> number.
     * 
     * @param z
     *            a <tt>Complex</tt> number.
     * @return the value ln&nbsp;<code>z</code>, the natural logarithm of
     *         <code>z</code>.
     * @see Complex#exp(Complex)
     */
    public static Complex log(Complex z)
    {
        return complex(Math.log(abs(z)), arg(z));
    }

    /**
     * Returns the L2 norm of a <tt>Complex</tt> number, which is the sum of the
     * squares of the real and imaginary parts.
     * 
     * @param z
     *            a <tt>Complex</tt> number
     * @return a <tt>double</tt> containing the norm of the <tt>Complex</tt>
     *         number
     */
    public static double norm(Complex z)
    {
        return (z.re * z.re + z.im * z.im);
    }

    /**
     * Returns the phase of a <tt>Complex</tt> value. Note that this method
     * duplicates the functionality of the <tt>arg</tt> method.
     * <p>
     * 
     * @return Phase of <tt>Complex</tt> value in <i> radians</i>
     */
    public static double phase(Complex c)
    {
        return Math.atan2(c.im, c.re);
    }

    /**
     * Returns a new <tt>Complex</tt> from a magnitude and angle.
     * 
     * @param r
     *            Magnitude
     * @param theta
     *            Angle (in <i>radians</i>)
     * @return <tt>Complex</tt> from Polar coordinates
     */
    public static Complex polar(double r, double theta)
    {
        if (r < 0.0)
        {
            theta += Math.PI;
            r = -r;
        }
        theta = theta % TWO_PI;
        return complex(r * Math.cos(theta), r * Math.sin(theta));
    }

    /**
     * Returns the <tt>Complex</tt> base raised to the power of the
     * <tt>double</tt> exponent.
     * 
     * @param base
     *            The <tt>Complex</tt> base value
     * @param exponent
     *            The <tt>double</tt> exponent
     * @return <tt>Complex</tt> base raised to the power of the exponent.
     * @see Complex#pow(double, Complex)
     * @see Complex#pow(Complex, Complex)
     */
    public static Complex pow(Complex base, double exponent)
    {
        // return exp( exponent * log(base) );

        double re = exponent * Math.log(base.abs());
        double im = exponent * base.arg();

        double scalar = Math.exp(re);
        return complex(scalar * Math.cos(im), scalar * Math.sin(im));
    }

    /**
     * Returns the <tt>double</tt> base raised to the power of the
     * <tt>Complex</tt> exponent.
     * 
     * @param base
     *            The <tt>double</tt> base value
     * @param exponent
     *            The <tt>Complex</tt> exponent
     * @return base raised to the power of the <tt>Complex</tt> exponent.
     * @see Complex#pow(Complex, double)
     * @see Complex#pow(Complex, Complex)
     */
    public static Complex pow(double base, Complex exponent)
    {
        // return exp( exponent * log(base) );

        double re = Math.log(Math.abs(base));
        double im = Math.atan2(0.0, base);

        double re2 = (re * exponent.re) - (im * exponent.im);
        double im2 = (re * exponent.im) + (im * exponent.re);

        double scalar = Math.exp(re2);

        return complex(scalar * Math.cos(im2), scalar * Math.sin(im2));
    }

    /**
     * Returns the <tt>Complex</tt> base raised to the power of the
     * <tt>Complex</tt> exponent.
     * 
     * @param base
     *            The <tt>Complex</tt> base value
     * @param exponent
     *            The <tt>Complex</tt> exponent
     * @return <tt>Complex</tt> base raised to the power of the <tt>Complex</tt>
     *         exponent.
     * @see Complex#pow(double, Complex)
     * @see Complex#pow(Complex, double)
     */
    public static Complex pow(Complex base, Complex exponent)
    {
        // return exp( exponent * log(base) );

        double re = Math.log(base.abs());
        double im = base.arg();

        double re2 = (re * exponent.re) - (im * exponent.im);
        double im2 = (re * exponent.im) + (im * exponent.re);

        double scalar = Math.exp(re2);

        return complex(scalar * Math.cos(im2), scalar * Math.sin(im2));
    }

    /**
     * Returns the real part of a <tt>Complex</tt> as a <tt>double</tt>.
     * 
     * @param z
     *            <tt>Complex</tt> number
     * @return <tt>double</tt> containing the real part of z
     * @see Complex#re()
     */
    public static double re(Complex z)
    {
        return z.re;
    }

    /**
     * Returns the sine of a <tt>Complex</tt> number.
     * 
     * <pre>
     *     sin(z) = ( exp(<i>i</i>*z) - exp(-<i>i</i>*z) ) / (2*<i>i</i>)
     * </pre>
     * 
     * @param z
     *            an angle, in radians.
     * @return the <tt>Complex</tt> sine
     * @see Complex#asin(Complex)
     */
    public static Complex sin(Complex z)
    {

        double scalar;
        double izRe, izIm; // Real and imag parts of iz

        double re1, im1; // Real and imag parts of exp(i*z)

        double re2, im2; // Real and imag parts of exp(-i*z)

        // Build i*z
        izRe = -z.im;
        izIm = z.re;

        // Build exp(i*z)
        scalar = Math.exp(izRe);
        re1 = scalar * Math.cos(izIm);
        im1 = scalar * Math.sin(izIm);

        // Build exp(-i*z)
        scalar = Math.exp(-izRe);
        re2 = scalar * Math.cos(-izIm);
        im2 = scalar * Math.sin(-izIm);

        // Build exp(i*z) - exp(-i*z)
        re1 = re1 - re2;
        im1 = im1 - im2;

        // result: (exp(i*z) - exp(-i*z)) / 2
        return (complex(0.5 * im1, -0.5 * re1));
    }

    /**
     * Returns the hyperbolic sine of a <tt>Complex</tt> number.
     * 
     * <pre>
     * sinh(z) = (exp(z) - exp(-z)) / 2
     * </pre>
     * 
     * @param z
     *            a <tt>Complex</tt> value
     * @return the <tt>Complex</tt> hyperbolic sine
     * @see Complex#asinh(Complex)
     */
    public static Complex sinh(Complex z)
    {

        double re1, im1; // Real and imag parts of exp(z)

        double re2, im2; // Real and imag parts of exp(-z)

        double scalar;

        // Build exp(z)
        scalar = Math.exp(z.re);
        re1 = scalar * Math.cos(z.im);
        im1 = scalar * Math.sin(z.im);

        // Build exp(-z)
        scalar = Math.exp(-z.re);
        re2 = scalar * Math.cos(-z.im);
        im2 = scalar * Math.sin(-z.im);

        // Build exp(z) - exp(-z)
        re1 = re1 - re2;
        im1 = im1 - im2;

        // Build sinh(z) = ( exp(z) - exp(-z) ) / 2
        return complex(0.5 * re1, 0.5 * im1);
    }

    /**
     * Returns the square root of a <code>Complex</code> value.
     * 
     * @param z
     *            a <code>Complex</code> value.
     * @return the square root of <code>z</code>.
     */
    public static Complex sqrt(Complex z)
    {
        // adapted from "Numerical Recipies in C" by Press et al

        double re = 0, im = 0;
        double u = 0, v = 0;
        double mag = z.abs();

        if (mag > 0.0)
        {
            if (z.re > 0.0)
            {
                double temp = Math.sqrt(0.5 * (mag + z.re));
                re = temp;
                im = 0.5 * z.im / temp;
            }
            else
            {
                double temp = Math.sqrt(0.5 * (mag - z.re));
                if (z.im < 0.0)
                {
                    temp = -temp;
                }
                re = 0.5 * z.im / temp;
                im = temp;
            }
        }
        else
        {
            re = 0.0;
            im = 0.0;
        }
        return complex(re, im);
    }

    /**
     * Returns the tangent of a <tt>Complex</tt> number.
     * 
     * <pre>
     * tan(z) = sin(z) / cos(z)
     * </pre>
     * 
     * @param z
     *            a <tt>Complex</tt> angle
     * @return the <tt>Complex</tt> tangent
     * @see Complex#atan(Complex)
     */
    public static Complex tan(Complex z)
    {

        Complex result; // Result of calculation

        double scalar; // Scalar

        double izRe, izIm; // Real and imag parts of iz

        double re1, im1; // Real and imag parts of exp(i*z)

        double re2, im2; // Real and imag parts of exp(-i*z)

        double re3, im3; // exp(i*z) - exp(-i*z)

        double re4, im4; // exp(i*z) + exp(i*z)

        double cosRe, cosIm; // cos(z)

        // First, calculate sin(z):

        // Build iz = i * z
        izRe = -z.im;
        izIm = z.re;

        // Build z1 = exp(iz)
        scalar = Math.exp(izRe);
        re1 = scalar * Math.cos(izIm);
        im1 = scalar * Math.sin(izIm);

        // Build z2 = exp(-iz)
        scalar = Math.exp(-izRe);
        re2 = scalar * Math.cos(-izIm);
        im2 = scalar * Math.sin(-izIm);

        // Build z3 = z1 - z2
        re3 = re1 - re2;
        im3 = im1 - im2;

        // Build sin(z) = z3 / (2*i)
        result = complex(0.5 * im3, -0.5 * re3);

        // Now, calculate cos(z):

        // z4 = z1 + z2
        re4 = re1 + re2;
        im4 = im1 + im2;

        // cos(z) = z4 / 2
        cosRe = 0.5 * re4;
        cosIm = 0.5 * im4;

        // tan(z) = sin(z) / cos(z)
        div(result, cosRe, cosIm);
        return result;
    }

    /**
     * Returns the hyperbolic tangent of a <tt>Complex</tt> number.
     * 
     * <pre>
     * tanh(z) = sinh(z) / cosh(z)
     * </pre>
     * 
     * @param z
     *            a <tt>Complex</tt> value
     * @return the <tt>Complex</tt> hyperbolic tangent
     * @see Complex#atanh(Complex)
     */
    public static Complex tanh(Complex z)
    {
        // tanh(z) = sinh(z) / cosh(z)

        Complex result; // Result of calculation

        double scalar; // Scalar

        double izRe, izIm; // Real and imag parts of iz

        double re1, im1; // Real and imag parts of exp(z)

        double re2, im2; // Real and imag parts of exp(-z)

        double re3, im3; // exp(z) - exp(-z)

        double re4, im4; // exp(z) + exp(z)

        double coshRe, coshIm; // cosh(z)

        // sinh(z) = (exp(z) - exp(-z)) / 2
        // cosh(z) = (exp(z) + exp(-z)) / 2
        // tanh(z) = (exp(z) - exp(-z)) / (exp(z) + exp(-z))

        // z1 = exp(z)
        scalar = Math.exp(z.re);
        re1 = scalar * Math.cos(z.im);
        im1 = scalar * Math.sin(z.im);

        // z2 = exp(-z)
        scalar = Math.exp(-z.re);
        re2 = scalar * Math.cos(-z.im);
        im2 = scalar * Math.sin(-z.im);

        // z3 = exp(z) - exp(-z)
        re3 = re1 - re2;
        im3 = im1 - im2;

        // z4 = exp(z) + exp(-z)
        re4 = re1 + re2;
        im4 = im1 + im2;

        // tanh(z) = z3 / z4
        result = complex(re3, im3);
        div(result, re4, im4);
        return result;
    }

    // *************************************************
    // Other elementary conversions
    // *************************************************
    /**
     * Extracts the real part of a <tt>Complex</tt> object as a <tt>double</tt>.
     * 
     * @return <tt>double</tt> containing the real part of the <tt>Complex</tt
     *         object
     * @see Complex#re(Complex)
     */
    public double re()
    {
        return this.re;
    }

    /**
     * Extracts the imaginary part of a <tt>Complex</tt> object as a
     * <tt>double</tt>.
     * 
     * @return <tt>double</tt> containing the imaginary part of the
     *         <tt>Complex</tt object
     * @see Complex#im(Complex)
     */
    public double im()
    {
        return this.im;
    }

    /**
     * Sets a <tt>double</tt> value into the imaginary part of a
     * <tt>Complex</tt> object.
     * 
     * @see Complex#im()
     * @see Complex#setRe(double)
     * @see Complex#setZ(double,double)
     */
    public void setIm(double im)
    {
        this.im = im;
    }

    /**
     * Sets a <tt>double</tt> value into the real part of a <tt>Complex</tt>
     * object.
     * 
     * @see Complex#re()
     * @see Complex#setIm(double)
     * @see Complex#setZ(double,double)
     */
    public void setRe(double re)
    {
        this.re = re;
    }

    /**
     * Sets two <tt>double</tt> values into the real and imaginary parts of a
     * <tt>Complex</tt> object.
     * 
     * @see Complex#re()
     * @see Complex#setRe(double)
     * @see Complex#setIm(double)
     */
    public void setZ(double re, double im)
    {
        this.re = re;
        this.im = im;
    }

    /**
     * Returns the absolute value (magnitude) of a <tt>Complex</tt> object.
     * 
     * @return <tt>double</tt> containing the absoulute value of the
     *         <tt>Complex</tt number
     * @see Complex#abs(Complex)
     * @see Complex#arg()
     * @see Complex#arg(Complex)
     */
    public double abs()
    {
        double absRe = Math.abs(re);
        double absIm = Math.abs(im);

        // This algorithm avoids overflows that might otherwise
        // occur when evaluating Math.sqrt(re*re + im*im);
        if (absRe == 0.0 && absIm == 0.0)
        {
            return 0.;
        }
        else if (absRe >= absIm)
        {
            double d = im / re;
            return absRe * Math.sqrt(1.0 + d * d);
        }
        else
        {
            double d = re / im;
            return absIm * Math.sqrt(1.0 + d * d);
        }
    }

    /**
     * Returns the principal angle of a <tt>Complex</tt> number, in radians,
     * measured counter-clockwise from the real axis. <tt>arg()</tt> always
     * returns a <tt>double</tt> between -<tt><b>PI</b></tt> and +
     * <tt><b>PI</b></tt>.
     * 
     * @return <tt>double</tt> containing the principal angle of the
     *         <tt>Complex</tt value
     * @see Complex#arg(Complex)
     */
    public double arg()
    {
        return Math.atan2(im, re);
    }

    /**
     * Returns the <tt>Complex</tt> conjugate of a <tt>Complex</tt> object. The
     * <tt>Complex</tt> conjugate of a number is the complex number having the
     * same real component and the negative of the imaginary component.
     * 
     * @return <tt>Complex</tt> conjugate.
     * @see Complex#conj(Complex)
     */
    public Complex conj()
    {
        return complex(re, -im);
    }

    // *************************************************
    // Elementary operations
    // *************************************************
    /**
     * Returns the sum of two <tt>Complex</tt> values. To perform z1 + z2, write
     * <tt>z1.add(z2)</tt>. Note that this method returns a <tt>Complex</tt>
     * object, so calls to these methods can be chained. For example,
     * 
     * <pre>
     *     z1 + z2 + z3 == <tt>z1.add(z2).add(z3)</tt>
     * </pre>
     * 
     * @param z2
     *            A Complex number
     * @return <tt>Complex</tt> sum z1 + z2
     * @see Complex#add(double)
     * @see Complex#sub(Complex)
     * @see Complex#sub(double)
     * @see Complex#mul(Complex)
     * @see Complex#mul(double)
     * @see Complex#div(Complex)
     * @see Complex#div(double)
     */
    public Complex add(Complex z)
    {
        return complex(re + z.re, im + z.im);
    }

    /**
     * Returns the sum of a <tt>Complex</tt> and a <tt>double</tt> value. To
     * perform z1 + d2, write <tt>z1.add(d2)</tt>.
     * 
     * @param d2
     *            A <tt>double</tt> value
     * @return <tt>Complex</tt> sum z1 + d2
     * @see Complex#add(Complex)
     * @see Complex#sub(Complex)
     * @see Complex#sub(double)
     * @see Complex#mul(Complex)
     * @see Complex#mul(double)
     * @see Complex#div(Complex)
     * @see Complex#div(double)
     */
    public Complex add(double d)
    {
        return complex(re + d, im);
    }

    /**
     * Returns the difference of two <tt>Complex</tt> values. To perform z1 -
     * z2, write <tt>z1.sub(z2)</tt>. Note that this method returns a
     * <tt>Complex</tt> object, so calls to these methods can be chained. For
     * example,
     * 
     * <pre>
     *     z1 - z2 + z3 == <tt>z1.sub(z2).add(z3)</tt>
     * </pre>
     * 
     * @param z2
     *            A Complex number
     * @return <tt>Complex</tt> difference z1 - z2
     * @see Complex#add(Complex)
     * @see Complex#add(double)
     * @see Complex#sub(double)
     * @see Complex#mul(Complex)
     * @see Complex#mul(double)
     * @see Complex#div(Complex)
     * @see Complex#div(double)
     */
    public Complex sub(Complex z)
    {
        return complex(re - z.re, im - z.im);
    }

    /**
     * Returns the difference between a <tt>Complex</tt> and a <tt>double</tt>
     * value. To perform z1 - d2, write <tt>z1.sub(d2)</tt>.
     * 
     * @param d2
     *            A <tt>double</tt> value
     * @return <tt>Complex</tt> difference z1 - d2
     * @see Complex#add(Complex)
     * @see Complex#add(double)
     * @see Complex#sub(Complex)
     * @see Complex#mul(Complex)
     * @see Complex#mul(double)
     * @see Complex#div(Complex)
     * @see Complex#div(double)
     */
    public Complex sub(double d)
    {
        return complex(re - d, im);
    }

    /**
     * Returns the product of two <tt>Complex</tt> values. To perform z1 * z2,
     * write <tt>z1.mul(z2)</tt>. Note that this method returns a
     * <tt>Complex</tt> object, so calls to these methods can be chained. For
     * example,
     * 
     * <pre>
     *     z1 * z2 / z3 == <tt>z1.mul(z2).div(z3)</tt>
     * </pre>
     * 
     * @param z2
     *            A Complex number
     * @return <tt>Complex</tt> product z1 * z2
     * @see Complex#add(Complex)
     * @see Complex#add(double)
     * @see Complex#sub(Complex)
     * @see Complex#sub(double)
     * @see Complex#mul(double)
     * @see Complex#div(Complex)
     * @see Complex#div(double)
     */
    public Complex mul(Complex z)
    {
        return complex((re * z.re) - (im * z.im), (re * z.im) + (im * z.re));
    }

    /**
     * Returns the product of a <tt>Complex</tt> and a <tt>double</tt> value. To
     * perform z1 * d2, write <tt>z1.mul(d2)</tt>.
     * 
     * @param d2
     *            A <tt>double</tt> value
     * @return <tt>Complex</tt> product z1 * d2
     * @see Complex#add(Complex)
     * @see Complex#add(double)
     * @see Complex#sub(Complex)
     * @see Complex#sub(double)
     * @see Complex#mul(Complex)
     * @see Complex#div(Complex)
     * @see Complex#div(double)
     */
    public Complex mul(double d)
    {
        return complex(re * d, im * d);
    }

    /**
     * Returns the division of two <tt>Complex</tt> values. To perform z1 / z2,
     * write <tt>z1.div(z2)</tt>. Note that this method returns a
     * <tt>Complex</tt> object, so calls to these methods can be chained. For
     * example,
     * 
     * <pre>
     *     z1 * z2 / z3 == <tt>z1.mul(z2).div(z3)</tt>
     * </pre>
     * 
     * @param z2
     *            A Complex number
     * @return <tt>Complex</tt> result of z1 / z2
     * @see Complex#add(Complex)
     * @see Complex#add(double)
     * @see Complex#sub(Complex)
     * @see Complex#sub(double)
     * @see Complex#mul(Complex)
     * @see Complex#mul(double)
     * @see Complex#div(double)
     */
    public Complex div(Complex z)
    {
        double x = z.re;
        double y = z.im;
        double scalar, zRe, zIm;

        // Calculate to minimize roundoff errors. This algorithm is
        // taken from "Numerical Recipes in Fortran 77: The Art of
        // Scientific Computing"
        if (Math.abs(x) >= Math.abs(y))
        {
            scalar = 1.0 / (x + y * (y / x));
            zRe = scalar * (re + im * (y / x));
            zIm = scalar * (im - re * (y / x));
        }
        else
        {
            scalar = 1.0 / (x * (x / y) + y);
            zRe = scalar * (re * (x / y) + im);
            zIm = scalar * (im * (x / y) - re);
        }
        return complex(zRe, zIm);
    }

    public Complex reciprocate(Complex scale)
    {
        return Complex.inv(this).mul(scale);
    }

    public Complex reciprocate(double scale)
    {
        return Complex.inv(this).mul(scale);
    }

    public Complex reciprocate()
    {
        return Complex.inv(this);
    }

    /**
     * Returns the division of a <tt>Complex</tt> value by a <tt>double</tt>
     * value. To perform z1 / d2, write <tt>z1.div(d2)</tt>.
     * 
     * @param d2
     *            A <tt>double</tt> number
     * @return <tt>Complex</tt> result of z1 / d2
     * @see Complex#add(Complex)
     * @see Complex#add(double)
     * @see Complex#sub(Complex)
     * @see Complex#sub(double)
     * @see Complex#mul(Complex)
     * @see Complex#mul(double)
     * @see Complex#div(Complex)
     */
    public Complex div(double d)
    {
        return complex(re / d, im / d);
    }

    /**
     * Returns the reciprocal of a <tt>Complex</tt> number (1/z).
     * 
     * @return The <tt>Complex</tt> reciprocal
     */
    public Complex inv()
    {
        double x = re;
        double y = im;
        double scalar, zRe, zIm;

        // Calculate to minimize roundoff errors. This algorithm is
        // taken from "Numerical Recipes in Fortran 77: The Art of
        // Scientific Computing"
        if (Math.abs(x) >= Math.abs(y))
        {
            scalar = 1.0 / (x + y * (y / x));
            zRe = scalar;
            zIm = -scalar * (y / x);
        }
        else
        {
            scalar = 1.0 / (x * (x / y) + y);
            zRe = scalar * (x / y);
            zIm = -scalar;
        }
        return complex(zRe, zIm);
    }

    /**
     * Returns the negative of a <tt>Complex</tt> value.
     * 
     * @return Negative of <tt>Complex</tt> value
     */
    public Complex neg()
    {
        return complex(-re, -im);
    }

    /**
     * Returns the phase of a <tt>Complex</tt> object. Note that this method
     * duplicates the functionality of the <tt>arg()</tt> method.
     * 
     * @return Phase of <tt>Complex</tt> object in <i> radians</i>
     */
    public double phase()
    {
        return Math.atan2(im, re);
    }

    /**
     * Returns the <tt>Complex</tt> value raised to the power of the
     * <tt>double</tt> exponent.
     * 
     * @param exponent
     *            The <tt>double</tt> exponent
     * @return <tt>Complex</tt> base raised to the power of the exponent.
     * @see Complex#pow(Complex)
     * @see Complex#pow(Complex, double)
     * @see Complex#pow(double, Complex)
     * @see Complex#pow(Complex, Complex)
     */
    public Complex pow(double exponent)
    {
        // return exp( exponent * log(this) );

        double real = exponent * Math.log(this.abs());
        double imag = exponent * this.arg();

        double scalar = Math.exp(real);
        return complex(scalar * Math.cos(imag), scalar * Math.sin(imag));
    }

    /**
     * Returns the <tt>Complex</tt> base raised to the power of the
     * <tt>Complex</tt> exponent.
     * 
     * @param base
     *            The <tt>Complex</tt> base value
     * @param exponent
     *            The <tt>Complex</tt> exponent
     * @return <tt>Complex</tt> base raised to the power of the <tt>Complex</tt>
     *         exponent.
     * @see Complex#pow(double)
     * @see Complex#pow(Complex, double)
     * @see Complex#pow(double, Complex)
     * @see Complex#pow(Complex, Complex)
     */
    public Complex pow(Complex exponent)
    {
        // return exp( exponent * log(this) );

        double real = Math.log(this.abs());
        double imag = this.arg();

        double re2 = (real * exponent.re) - (imag * exponent.im);
        double im2 = (real * exponent.im) + (imag * exponent.re);

        double scalar = Math.exp(re2);

        return complex(scalar * Math.cos(im2), scalar * Math.sin(im2));
    }

    // *************************************************
    // Tests
    // *************************************************
    /**
     * Returns <tt>true</tt> if either the real or imaginary component of this
     * <tt>Complex</tt> has an infinite value.
     * 
     * @return <tt>true</tt> if either component of the <tt>Complex</tt> object
     *         is infinite; <tt>false</tt>, otherwise.
     */
    public boolean isInfinite()
    {
        return (Double.isInfinite(re) || Double.isInfinite(im));
    }

    /**
     * Returns <tt>true</tt> if either the real or the imaginary component of
     * this <tt>Complex</tt> is a Not-a-Number (<tt>NaN</tt>) value.
     * 
     * @return <tt>true</tt> if either component of the <tt>Complex</tt> object
     *         is <tt>NaN</tt>; <tt>false</tt>, otherwise.
     */
    public boolean isNaN()
    {
        return (Double.isNaN(re) || Double.isNaN(im));
    }

    /**
     * Decides if two <tt>Complex</tt> numbers are "sufficiently" alike to be
     * considered equal. This test should be used cautiously because of the
     * inexact nature of floating-point representations on computers. This test
     * takes the form <tt> z1.equals(z2)</tt>. The tolerance for this comparison
     * is scaled automatically based on the sizes of the numbers being compared.
     * 
     * @param z2
     *            A <tt>Complex</tt> number
     * @return <tt>true</tt> if equal; <tt>false</tt> otherwise
     * @see Complex#equals(Complex,double)
     */
    public boolean equals(Complex z)
    {
        double tol1 = 1.0E-13 * abs(this);
        return ((re - z.re) <= tol1) && ((im - z.im) <= tol1);
    }

    /**
     * Decides if two <tt>Complex</tt> numbers are "sufficiently" alike to be
     * considered equal. This test should be used cautiously because of the
     * inexact nature of floating-point representations on computers. This test
     * takes the form <tt> z1.equals(z2,tol)</tt>. The argument <tt>tol</tt> is
     * a scale factor used to determine the maximum magnitude of the difference
     * between them before they are considered <i>not</i> equal. This value is
     * multiplied by the absolute value of the first of the two numbers being
     * compared to make the tolerance relative to the magnitude of the numbers
     * being compared.
     * 
     * @param z2
     *            A <tt>Complex</tt> number
     * @param tol
     *            The tolerance with which to comapare the two numbers
     * @return <tt>true</tt> if equal; <tt>false</tt> otherwise
     * @see Complex#equals(Complex)
     */
    public boolean equals(Complex z, double tol)
    {
        double tol1 = Math.abs(tol) * abs(this);
        return ((re - z.re) <= tol1) && ((im - z.im) <= tol1);
    }

    /**
     * Compares two <code>Complex</code> values numerically according to their
     * absolute values.
     * 
     * @param anotherComplex
     *            the <code>Complex</code> to be compared.
     * @return the value <code>0</code> if the absolute value of the argument
     *         <code>anotherComplex</code> is equal to the absolute value of
     *         this <code>Complex</code>; a value less than <code>0</code> if
     *         the absolute value of this <code>Complex</code> is numerically
     *         less than the <code>Complex</code> argument; and a the absolute
     *         value of this <code>Complex</code> is numerically greater than
     *         the <code>Complex</code> argument. (signed comparison).
     * @see java.lang.Comparable
     */
    public int compareTo(Complex anotherComplex)
    {
        double thisVal = this.abs();
        double anotherVal = anotherComplex.abs();
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    /**
     * Compares this <code>Complex</code> to another Object. If the Object is a
     * <code>Complex</code>, this function behaves like
     * <code>compareTo(Complex)</code>. Otherwise, it throws a
     * <code>ClassCastException</code> (as <code>Complex</code> values are
     * comparable only to other <code>Complex</code> values).
     * 
     * @param o
     *            the <code>Object</code> to be compared.
     * @return the value <code>0</code> if the absolute value of the argument
     *         <code>Object</code> is equal to the absolute value of this
     *         <code>Complex</code>; a value less than <code>0</code> if the
     *         absolute value of this <code>Complex</code> is numerically less
     *         than the <code>Object</code> argument; and a the absolute value
     *         of this <code>Complex</code> is numerically greater than the
     *         <code>Object</code> argument. (signed comparison).
     * @exception <code>ClassCastException</code> if the argument is not a
     *            <code>Complex</code>.
     * @see java.lang.Comparable
     */
    public int compareTo(Object o)
    {
        return compareTo((Complex) o);
    }

    // *************************************************
    // Private methods
    // *************************************************
    /**
     * Implements division between two complex numbers, one expressed as as a
     * <tt>Complex</tt> and the other expressed as a pair of <tt>double</tt>s.
     * This approach allows us to return the result in the <tt>Complex</tt>
     * object without creating any new objects for garbage disposal.
     */
    private static void div(Complex z, double x, double y)
    {

        double zRe, zIm; // Components of z

        double scalar;

        if (Math.abs(x) >= Math.abs(y))
        {
            scalar = 1.0 / (x + y * (y / x));
            zRe = scalar * (z.re + z.im * (y / x));
            zIm = scalar * (z.im - z.re * (y / x));
        }
        else
        {
            scalar = 1.0 / (x * (x / y) + y);
            zRe = scalar * (z.re * (x / y) + z.im);
            zIm = scalar * (z.im * (x / y) - z.re);
        }

        // Return results in the complex object
        z.re = zRe;
        z.im = zIm;
    }

    /**
     * Implements square root of a <tt>Complex</tt> value, returning the result
     * in the same object as the input value.
     */
    private static void sqrt1(Complex z)
    {
        // adapted from "Numerical Recipies in C" by William H. Press et al

        double re = 0, im = 0;
        double u = 0, v = 0;
        double mag = z.abs();
        double temp;

        if (mag > 0.0)
        {
            if (z.re > 0.0)
            {
                temp = Math.sqrt(0.5 * (mag + z.re));
                re = temp;
                im = 0.5 * z.im / temp;
            }
            else
            {
                temp = Math.sqrt(0.5 * (mag - z.re));
                if (z.im < 0.0)
                {
                    temp = -temp;
                }
                re = 0.5 * z.im / temp;
                im = temp;
            }
        }
        else
        {
            re = 0.0;
            im = 0.0;
        }

        // Return results
        z.re = re;
        z.im = im;
    }
}
