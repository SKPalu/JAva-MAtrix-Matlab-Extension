/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.signal;

import jamaextension.jamax.Complex;
import jamaextension.jamax.ComplexMatrix;
import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Matrix;

/**
 * Class <code>SigProc</code> contains methods to perform basic signal
 * processing tasks, such as FFTs, convolutions, correlations, etc.
 * 
 * @author S. J. Chapman
 * @version 1.00, 08/15/99
 */
public final class SigProc
{

    // *************************************************
    // Constants
    // *************************************************
    /**
     * The <code>double</code> representation of twice PI.
     */
    private static final double TWO_PI = 2.0 * Math.PI;
    /**
     * Constant to specify no cross-correlation normalization.
     */
    public final static int NO_NORM = 0;
    /**
     * Constant to specify that cross-correlations should be normalized so that
     * the coefficients at zero lag are exactly 1.0.
     */
    public final static int NORM = 1;

    // *************************************************
    // Methods
    // *************************************************
    /**
     * This method calculates the frequency associated with each element of the
     * output array from an FFT, taking into account the ambiguities above the
     * Nyquist frequency.
     * 
     * @param fs
     *            The sampling frequency of the input data to the FFT
     * @param fftSize
     *            The size of the FFT.
     * @return an array containing the frequency of each bin (element) in the
     *         output from the FFT.
     */
    public static double[] calcFreq(double fs, int fftSize)
    {

        // Calculate df
        double df = fs / fftSize; // Frequency step size

        // Initialize output array
        double fArray[] = new double[fftSize];

        // Calculate frequency step
        fArray[0] = 0;
        fArray[fftSize / 2] = fs / 2;
        for (int i = 1; i < fftSize / 2; i++)
        {
            fArray[i] = i * df;
            fArray[fftSize - i] = -i * df;
        }

        return fArray;
    }

    /**
     * This method calculates the cross-correlation between two
     * <code>Complex</code> arrays. It can also be used to generate the
     * autocorrelation by simply using the same array for both arguments. Note
     * that this algorithm returns the auto- or cross-correlations for all
     * possible lags. It is possible to reduce the computational load if fewer
     * lags are required. For that case, you should consider using a different
     * algorithm.
     * <p>
     * This algorithm accepts input arrays <code>za</code> and <code>zb</code>
     * of arbitrary length. It returns an array whose length
     * <code>za.length + zb.length - 1</code>. The resulting array has zero lag
     * in the element with index <code>za.length/2 - 1</code>.
     * <p>
     * This algorithm is designed so that if <code>zb</code> <i>lags</i>
     * <code>za</code>, (is shifted to the right of it), then the peak of the
     * cross correlation function will appear at indices greater than
     * <code>za.length/2 - 1</code>. If <code>zb</code> <i>leads</i>
     * <code>za</code>, then the peak of the cross correlation function will
     * appear at indices less than <code>za.length/2 - 1</code>.
     * <p>
     * Method <code>calcLags</code> may be used to calculate the lag associated
     * with each position in the output array.
     * 
     * @see SigProc#calcLags(Complex[],Complex[])
     * @param za
     *            Input array 1.
     * @param zb
     *            Input array 2.
     * @return the cross-correlation of <code>za</code> and <code>zb</code>.
     */
    public static Complex[] correl(Complex[] za, Complex[] zb)
    {

        // Call the core code with the no-normalization option.
        return correlCore(za, zb, NO_NORM);
    }

    /**
     * This method calculates the cross-correlation between two
     * <code>double</code> arrays. It can also be used to generate the
     * autocorrelation by simply using the same array for both arguments. Note
     * that this algorithm returns the auto- or cross-correlations for all
     * possible lags. It is possible to reduce the computational load if fewer
     * lags are required. For that case, you should consider using a different
     * algorithm.
     * <p>
     * This algorithm accepts input arrays <code>xa</code> and <code>xb</code>
     * of arbitrary length. It returns an array whose length
     * <code>xa.length + xb.length - 1</code>. The resulting array has zero lag
     * in the element with index <code>xa.length/2 - 1</code>.
     * <p>
     * This algorithm is designed so that if <code>xb</code> <i>lags</i>
     * <code>xa</code>, (is shifted to the right of it), then the peak of the
     * cross correlation function will appear at indices greater than
     * <code>xa.length/2 - 1</code>. If <code>xb</code> <i>leads</i>
     * <code>xa</code>, then the peak of the cross correlation function will
     * appear at indices less than <code>xa.length/2 - 1</code>.
     * <p>
     * Method <code>calcLags</code> may be used to calculate the lag associated
     * with each position in the output array.
     * 
     * @see SigProc#calcLags(double[],double[])
     * @param xa
     *            Input array 1.
     * @param xb
     *            Input array 2.
     * @return the cross-correlation of <code>xa</code> and <code>xb</code>.
     */
    public static double[] correl(double[] xa, double[] xb)
    {

        // Call the core code with the no-normalization option.
        return correlCore(xa, xb, NO_NORM);
    }

    /**
     * This method calculates the cross-correlation between two
     * <code>Complex</code> arrays. It can also be used to generate the
     * autocorrelation by simply using the same array for both arguments. Note
     * that this algorithm returns the auto- or cross-correlations for all
     * possible lags. It is possible to reduce the computational load if fewer
     * lags are required. For that case, you should consider using a different
     * algorithm.
     * <p>
     * The third input parameter specifies the type of normalization to be
     * applied to the cross correlation. The value <code>NONORM</code> specifies
     * that no normalization should be performed. The value <code>NORM</code>
     * specifies that the output cross correlation should be divided by the
     * constant <blockquote>
     * 
     * <pre>
     * constant = 1 / sqrt(sum(abs(za) &circ; 2) * sum(abs(zb) &circ; 2));
     * </pre>
     * 
     * </blockquote> With this normalization, the peak of the cross correlation
     * function will be 1.0 if and only if the two arrays are identical except
     * for lag. If the two arrays differ, the cross-correlation will be less
     * than 1.0, and the size of the value will be a measure of the similarity
     * between the two arrays.
     * <p>
     * This algorithm accepts input arrays <code>za</code> and <code>zb</code>
     * of arbitrary length. It returns an array whose length
     * <code>za.length + zb.length - 1</code>. The resulting array has zero lag
     * in the element with index <code>za.length/2 - 1</code>.
     * <p>
     * This algorithm is designed so that if <code>zb</code> <i>lags</i>
     * <code>za</code>, (is shifted to the right of it), then the peak of the
     * cross correlation function will appear at indices greater than
     * <code>za.length/2 - 1</code>. If <code>zb</code> <i>leads</i>
     * <code>za</code>, then the peak of the cross correlation function will
     * appear at indices less than <code>za.length/2 - 1</code>.
     * <p>
     * Method <code>calcLags</code> may be used to calculate the lag associated
     * with each position in the output array.
     * 
     * @see SigProc#calcLags(Complex[],Complex[])
     * @param za
     *            Input array 1.
     * @param zb
     *            Input array 2.
     * @return the cross-correlation of <code>za</code> and <code>zb</code>.
     */
    public static Complex[] correl(Complex[] za, Complex[] zb, int norm)
    {

        // Call the core code with the normalization option.
        return correlCore(za, zb, norm);
    }

    /**
     * This method calculates the cross-correlation between two
     * <code>double</code> arrays. It can also be used to generate the
     * autocorrelation by simply using the same array for both arguments. Note
     * that this algorithm returns the auto- or cross-correlations for all
     * possible lags. It is possible to reduce the computational load if fewer
     * lags are required. For that case, you should consider using a different
     * algorithm.
     * <p>
     * The third input parameter specifies the type of normalixation to be
     * applied to the cross correlation. The value <code>NONORM</code> specifies
     * that no normalixation should be performed. The value <code>NORM</code>
     * specifies that the output cross correlation should be divided by the
     * constant <blockquote>
     * 
     * <pre>
     * constant = 1 / sqrt(sum(abs(xa) &circ; 2) * sum(abs(xb) &circ; 2));
     * </pre>
     * 
     * </blockquote> With this normalixation, the peak of the cross correlation
     * function will be 1.0 if and only if the two arrays are identical except
     * for lag. If the two arrays differ, the cross-correlation will be less
     * than 1.0, and the size of the value will be a measure of the similarity
     * between the two arrays.
     * <p>
     * This algorithm accepts input arrays <code>xa</code> and <code>xb</code>
     * of arbitrary length. It returns an array whose length
     * <code>xa.length + xb.length - 1</code>. The resulting array has zero lag
     * in the element with index <code>xa.length/2 - 1</code>.
     * <p>
     * This algorithm is designed so that if <code>xb</code> <i>lags</i>
     * <code>xa</code>, (is shifted to the right of it), then the peak of the
     * cross correlation function will appear at indices greater than
     * <code>xa.length/2 - 1</code>. If <code>xb</code> <i>leads</i>
     * <code>xa</code>, then the peak of the cross correlation function will
     * appear at indices less than <code>xa.length/2 - 1</code>.
     * <p>
     * Method <code>calcLags</code> may be used to calculate the lag associated
     * with each position in the output array.
     * 
     * @see SigProc#calcLags(double[],double[])
     * @param xa
     *            Input array 1.
     * @param xb
     *            Input array 2.
     * @return the cross-correlation of <code>xa</code> and <code>xb</code>.
     */
    public static double[] correl(double[] xa, double[] xb, int norm)
    {

        // Call the core code with the normalixation option.
        return correlCore(xa, xb, norm);
    }

    /**
     * This method calculates the convolution of two arrays of arbitrary length,
     * returning the result in an array of length
     * <code>za.length + zb.length - 1</code>.
     * 
     * @param za
     *            Input array 1.
     * @param zb
     *            Input array 2.
     * @return the convolution of <code>za</code> and <code>zb</code>.
     */
    public static Complex[] conv(Complex[] za, Complex[] zb)
    {

        // Call the core code.
        return convCore(za, zb);
    }

    public static ComplexMatrix fft(Matrix x)
    {
        ComplexMatrix z = new ComplexMatrix(x);
        z = fft(z);
        return z;
    }

    public static ComplexMatrix fft(ComplexMatrix z)
    {
        int row = z.getRowDimension();
        int col = z.getColumnDimension();
        ComplexMatrix fftCM = null;
        Complex[] cdata = null;
        if (z.isVector())
        {
            // System.out.println("vector ---");
            boolean isrow = false;
            if (z.isRowVector())
            {
                cdata = z.getRowPackedCopy();
                isrow = true;
            }
            else
            {
                cdata = z.getColumnPackedCopy();
            }
            cdata = fft(cdata);
            fftCM = new ComplexMatrix(cdata);
            if (!isrow)
            {
                fftCM = fftCM.transpose();
            }
            return fftCM;
        }

        // If matrix, then do the 'fft' columnwise
        fftCM = new ComplexMatrix(row, col);
        for (int j = 0; j < col; j++)
        {
            ComplexMatrix colCM = z.getColumnAt(j);
            colCM = fft(colCM);
            fftCM.setColumnAt(j, colCM);
        }

        return fftCM;
    }

    /**
     * This method calculates the Fast Fourier Transform of a
     * <code>Complex</code> array, provided that the length of the array is a
     * power of 2. If the array length is <i>not</i> a power of two, the method
     * throws an <code>InvalidArraySizeException</code>. This method does not
     * destroy its input data.
     * 
     * @param z
     *            The input array.
     * @return the FFT of <code>z</code>.
     * @exception <code>InvalidArraySizeException</code> if the array length is
     *            not a power of 2
     */
    public static Complex[] fft(Complex[] z)
    {

        // Check for a valid size
        int exp = 0; // Power of 2
        int m = 2; // 2^exp
        int n = z.length; // Array length

        // Work out the power of 2 that is >= the array length
        do
        {
            exp = exp + 1;
            m = 2 * m;
        }
        while (m < z.length);

        // Is this array length NOT a power of 2? If so, throw
        // a runtime InvalidArraySizeException.
        if (m != z.length)
        {
            String s = "Invalid FFT array size: " + z.length;
            // throw new ConditionalException("fft : " + s);
        }

        // Declare additional variables and arrays
        int i; // index variable
        double x[] = new double[n]; // x values
        double y[] = new double[n]; // y values

        // Separate the x and y components
        for (i = 0; i < n; i++)
        {
            x[i] = z[i].re();
            y[i] = z[i].im();
        }

        // Calculate FFT
        fftCore(x, y, false);

        // Restore the x and y components
        Complex z1[] = new Complex[z.length];
        for (i = 0; i < n; i++)
        {
            z1[i] = new Complex(x[i], y[i]);
        }

        return z1;
    }

    public static ComplexMatrix fft(Matrix x, int size)
    {
        ComplexMatrix z = new ComplexMatrix(x);
        z = fft(z, size);
        return z;
    }

    public static ComplexMatrix fft(ComplexMatrix z, int size)
    {

        int row = z.getRowDimension();
        int col = z.getColumnDimension();
        ComplexMatrix fftCM = null;
        Complex[] cdata = null;
        if (z.isVector())
        {
            // System.out.println("vector ---");
            boolean isrow = false;
            if (z.isRowVector())
            {
                cdata = z.getRowPackedCopy();
                isrow = true;
            }
            else
            {
                cdata = z.getColumnPackedCopy();
            }
            cdata = fft(cdata, size);
            fftCM = new ComplexMatrix(cdata);
            if (!isrow)
            {
                fftCM = fftCM.transpose();
            }
            return fftCM;
        }

        // If matrix, then do the 'fft' columnwise
        fftCM = new ComplexMatrix(row, col);
        for (int j = 0; j < col; j++)
        {
            ComplexMatrix colCM = z.getColumnAt(j);
            colCM = fft(colCM, size);
            fftCM.setColumnAt(j, colCM);
        }

        return fftCM;

    }

    /**
     * This method calculates the Fast Fourier Transform of a
     * <code>Complex</code> array, provided that the length of the array is a
     * power of 2. If the array length is <i>not</i> a power of two, the method
     * throws an <code>InvalidArraySizeException</code>. This method does not
     * destroy its input data.
     * 
     * @param z
     *            The input array.
     * @return the FFT of <code>z</code>.
     * @exception <code>InvalidArraySizeException</code> if the array length is
     *            not a power of 2
     */
    public static Complex[] fft(Complex[] z, int size)
    {

        // Check for a valid size
        int exp = 0; // Power of 2
        int m = 2; // 2^exp
        int n = size; // Array length

        // Work out the power of 2 that is >= the array length
        do
        {
            exp = exp + 1;
            m = 2 * m;
        }
        while (m < size);

        // Is this array length NOT a power of 2? If so, throw
        // a runtime InvalidArraySizeException.
        if (m != size)
        {
            String s = "Invalid FFT array size: " + size;
            throw new ConditionalException("fft : " + s);
        }

        // Declare additional variables and arrays
        int i; // index variable
        double x[] = new double[n]; // x values
        double y[] = new double[n]; // y values

        // Separate the x and y components
        for (i = 0; i < n; i++)
        {
            x[i] = z[i].re();
            y[i] = z[i].im();
        }

        // Calculate FFT
        fftCore(x, y, false);

        // Restore the x and y components
        Complex z1[] = new Complex[size];
        for (i = 0; i < n; i++)
        {
            z1[i] = new Complex(x[i], y[i]);
        }

        return z1;
    }

    /**
     * This method shifts the output of an FFT so that the dc component appears
     * in the middle of the spectrum.
     * 
     * @param z
     *            The input array.
     * @return The array with the upper and lower halves of <code>z</code>
     *         swapped.
     */
    public static Complex[] fftswap(Complex[] z)
    {

        int i, k, mid;

        // Create output array
        Complex z1[] = new Complex[z.length];

        // Swap the data
        mid = z.length / 2;

        k = 0;
        for (i = mid + 1; i < z.length; i++)
        {
            z1[k++] = z[i];
        }
        for (i = 0; i < mid + 1; i++)
        {
            z1[k++] = z[i];
        }

        // Return the swapped array
        return (z1);
    }

    /**
     * This method shifts the output of an FFT so that the dc component appears
     * in the middle of the spectrum.
     * 
     * @param d
     *            The input array.
     * @return The array with the upper and lower halves of <code>d</code>
     *         swapped.
     */
    public static double[] fftswap(double[] d)
    {

        int i, k, mid;

        // Create output array
        double d1[] = new double[d.length];

        // Swap the data
        mid = d.length / 2;

        k = 0;
        for (i = mid + 1; i < d.length; i++)
        {
            d1[k++] = d[i];
        }
        for (i = 0; i < mid + 1; i++)
        {
            d1[k++] = d[i];
        }

        // Return the swapped array
        return (d1);
    }

    public static ComplexMatrix ifft(ComplexMatrix z, int size)
    {
        int row = z.getRowDimension();
        int col = z.getColumnDimension();
        ComplexMatrix ifftCM = null;
        Complex[] cdata = null;
        if (z.isVector())
        {
            // System.out.println("vector ---");
            boolean isrow = false;
            if (z.isRowVector())
            {
                cdata = z.getRowPackedCopy();
                isrow = true;
            }
            else
            {
                cdata = z.getColumnPackedCopy();
            }
            cdata = ifft(cdata, size);
            ifftCM = new ComplexMatrix(cdata);
            if (!isrow)
            {
                ifftCM = ifftCM.transpose();
            }
            return ifftCM;
        }

        // If matrix, then do the 'ifft' columnwise
        ifftCM = new ComplexMatrix(row, col);
        for (int j = 0; j < col; j++)
        {
            ComplexMatrix colCM = z.getColumnAt(j);
            colCM = ifft(colCM, size);
            ifftCM.setColumnAt(j, colCM);
        }

        return ifftCM;
    }

    public static ComplexMatrix ifft(ComplexMatrix z)
    {
        int row = z.getRowDimension();
        int col = z.getColumnDimension();
        ComplexMatrix ifftCM = null;
        Complex[] cdata = null;
        if (z.isVector())
        {
            // System.out.println("vector ---");
            boolean isrow = false;
            if (z.isRowVector())
            {
                cdata = z.getRowPackedCopy();
                isrow = true;
            }
            else
            {
                cdata = z.getColumnPackedCopy();
            }
            cdata = ifft(cdata);
            ifftCM = new ComplexMatrix(cdata);
            if (!isrow)
            {
                ifftCM = ifftCM.transpose();
            }
            return ifftCM;
        }

        // If matrix, then do the 'ifft' columnwise
        ifftCM = new ComplexMatrix(row, col);
        for (int j = 0; j < col; j++)
        {
            ComplexMatrix colCM = z.getColumnAt(j);
            colCM = ifft(colCM);
            ifftCM.setColumnAt(j, colCM);
        }

        return ifftCM;
    }

    public static Complex[] ifft(double[] powADbl)
    {
        Matrix M1 = new Matrix(powADbl);
        ComplexMatrix CM = new ComplexMatrix(M1);
        Complex[] CP = CM.getRowPackedCopy();
        return ifft(CP);
    }

    /**
     * This method calculates the inverse Fast Fourier Transform of a
     * <code>Complex</code> array, provided that the length of the array is a
     * power of 2. If the array lengty is <i>not</i> a power of two, the method
     * throws an <code>InvalidArraySizeException</code>. This method does not
     * destroy its input data.
     * 
     * @param z
     *            The input array.
     * @return the inverse FFT of <code>z</code>.
     * @exception <code>InvalidArraySizeException</code> if the array length is
     *            not a power of 2
     */
    public static Complex[] ifft(Complex[] z)
    {

        // Check for a valid size
        int exp = 0; // Power of 2
        int m = 2; // 2^exp
        int n = z.length; // Array length

        // Work out the power of 2 that is >= the array length
        do
        {
            exp = exp + 1;
            m = 2 * m;
        }
        while (m < z.length);

        // Is this array length NOT a power of 2? If so, throw
        // a runtime InvalidArraySizeException.
        if (m != z.length)
        {
            String s = "Invalid FFT array size: " + z.length;
            throw new ConditionalException("ifft : " + s);
        }

        // Declare additional variables and arrays
        int i; // index variable
        double x[] = new double[n]; // x values
        double y[] = new double[n]; // y values

        // Separate the x and y components
        for (i = 0; i < n; i++)
        {
            x[i] = z[i].re();
            y[i] = z[i].im();
        }

        // Calculate FFT
        fftCore(x, y, true);

        // Restore the x and y components
        Complex z1[] = new Complex[z.length];
        for (i = 0; i < n; i++)
        {
            z1[i] = new Complex(x[i], y[i]);
        }

        return z1;
    }

    /**
     * This method calculates the inverse Fast Fourier Transform of a
     * <code>Complex</code> array, provided that the length of the array is a
     * power of 2. If the array lengty is <i>not</i> a power of two, the method
     * throws an <code>InvalidArraySizeException</code>. This method does not
     * destroy its input data.
     * 
     * @param z
     *            The input array.
     * @return the inverse FFT of <code>z</code>.
     * @exception <code>InvalidArraySizeException</code> if the array length is
     *            not a power of 2
     */
    public static Complex[] ifft(Complex[] z, int size)
    {

        // Check for a valid size
        int exp = 0; // Power of 2
        int m = 2; // 2^exp
        int n = size; // Array length

        // Work out the power of 2 that is >= the array length
        do
        {
            exp = exp + 1;
            m = 2 * m;
        }
        while (m < size);

        // Is this array length NOT a power of 2? If so, throw
        // a runtime InvalidArraySizeException.
        if (m != size)
        {
            String s = "Invalid FFT array size: " + size;
            throw new ConditionalException("ifft : " + s);
        }

        // Declare additional variables and arrays
        int i; // index variable
        double x[] = new double[n]; // x values
        double y[] = new double[n]; // y values

        // Separate the x and y components
        for (i = 0; i < n; i++)
        {
            x[i] = z[i].re();
            y[i] = z[i].im();
        }

        // Calculate FFT
        fftCore(x, y, true);

        // Restore the x and y components
        Complex z1[] = new Complex[size];
        for (i = 0; i < n; i++)
        {
            z1[i] = new Complex(x[i], y[i]);
        }

        return z1;
    }

    /**
     * This method calculates the power of two greater than or equal to a given
     * input size.
     * 
     * @param size
     *            The input size
     * @return an <code>int</code> containing the required power of two.
     */
    public static int nextMul(int arraySize)
    {

        // Check for a valid size
        int exp = 0; // Power of 2
        int m = 2; // 2^exp

        // Work out the power of 2 that is >= the array length
        do
        {
            exp = exp + 1;
            m = 2 * m;
        }
        while (m < arraySize);

        return m;
    }

    /**
     * This method calculates the lags associated with each position in the
     * output of a cross correlation.
     * 
     * @param xa
     *            The input values for array 1
     * @param xb
     *            The input values for array 2
     * @return an array containing the lags associated with each array index in
     *         the output of the cross correlation of <code>za</code> and
     *         <code>zb</code>
     */
    public static double[] calcLags(double xa[], double xb[])
    {

        // Local variables
        int i, k; // Index variables

        // Create output array
        double lags[] = new double[xa.length + xb.length - 1];

        // Restore the x and y components with the zero lag in the middle
        k = -xa.length + 1;
        for (i = 0; i < xa.length + xb.length - 1; i++)
        {
            lags[i] = k++;
        }

        return lags;
    }

    /**
     * This method calculates the lags associated with each position in the
     * output of a cross correlation.
     * 
     * @param za
     *            The input values for array 1
     * @param zb
     *            The input values for array 2
     * @return an array containing the lags associated with each array index in
     *         the output of the cross correlation of <code>za</code> and
     *         <code>zb</code>
     */
    public static double[] calcLags(Complex za[], Complex zb[])
    {

        // Local variables
        int i, k; // Index variables

        // Create output array
        double lags[] = new double[za.length + zb.length - 1];

        // Restore the x and y components with the zero lag in the middle
        k = -za.length + 1;
        for (i = 0; i < za.length + zb.length - 1; i++)
        {
            lags[i] = k++;
        }

        return lags;
    }

    /**
     * This method is the computational core of the convolution algorithm. This
     * method destroys its input data, and returns the resulting
     * cross-correlation in <i>zout<i>.
     * 
     * @param za
     *            The input values for array 1
     * @param zb
     *            The input values for array 2
     * @return the convolution of <code>za</code> and <code>zb</code>
     */
    private static Complex[] convCore(Complex za[], Complex zb[])
    {

        // Calculate the size of FFT required
        int fftSize = nextMul(za.length + zb.length);

        // Declare additional variables and arrays
        int i, k; // Loop index
        int mid; // Midpoint of array
        double xa[] = new double[fftSize]; // x values from array za
        double xb[] = new double[fftSize]; // x values from array zb
        double xt; // Temp variable
        double ya[] = new double[fftSize]; // y values from array za
        double yb[] = new double[fftSize]; // y values from array zb
        double yt; // Temp variable

        // Separate the x and y components of za.
        for (i = 0; i < za.length; i++)
        {
            xa[i] = za[i].re();
            ya[i] = za[i].im();
        }

        // Separate the x and y components of zb.
        for (i = 0; i < zb.length; i++)
        {
            xb[i] = zb[i].re();
            yb[i] = zb[i].im();
        }

        // Zero-pad za
        for (i = za.length; i < fftSize; i++)
        {
            xa[i] = 0;
            ya[i] = 0;
        }

        // Zero-pad zb
        for (i = zb.length; i < fftSize; i++)
        {
            xb[i] = 0;
            yb[i] = 0;
        }

        // Calculate the FFTs
        fftCore(xa, ya, false);
        fftCore(xb, yb, false);

        // Perform convolution in the frequency domain, re-using
        // arrays xa and ya for the output.
        for (i = 0; i < fftSize; i++)
        {
            xt = xa[i] * xb[i] - ya[i] * yb[i];
            yt = xa[i] * yb[i] + xb[i] * ya[i];
            xa[i] = xt;
            ya[i] = yt;
        }

        // Calculate the inverse FFT
        fftCore(xa, ya, true);

        // Create output array
        Complex zout[] = new Complex[za.length + zb.length - 1];

        // Restore the x and y components
        for (i = 0; i < za.length + zb.length - 1; i++)
        {
            zout[i] = new Complex(xa[i], ya[i]);
        }
        return zout;
    }

    /**
     * This method is the computational core of the correlation algorithm. This
     * method accepts real arrays and returns a real result. This method
     * destroys its input data, and returns the resulting cross-correlation in
     * <i>zout<i>.
     * 
     * @param za
     *            The input values for array 1
     * @param zb
     *            The input values for array 2
     * @param norm
     *            Normalization flag
     * @return the cross correlation of <code>za</code> and <code>zb</code>
     */
    private static double[] correlCore(double za[], double zb[], int norm)
    {

        // Calculate the size of FFT required
        int fftSize = nextMul(2 * Math.max(za.length, zb.length));

        // Declare additional variables and arrays
        double dena; // Sum of squares of za
        double denb; // Sum of squares of zb
        int i, k; // Loop index
        double scale; // Normalization factor
        double xa[] = new double[fftSize]; // x values from array za
        double xb[] = new double[fftSize]; // x values from array zb
        double xt; // Temp variable
        double ya[] = new double[fftSize]; // Array of zeros
        double yb[] = new double[fftSize]; // Array of zeros
        double yt; // Temp variable

        // Separate the x and y components of za. Calculate
        // the sum of the squares of the coefficients while
        // separating the components.
        dena = 0;
        for (i = 0; i < za.length; i++)
        {
            xa[i] = za[i];
            ya[i] = 0;
            dena += xa[i] * xa[i];
        }

        // Separate the x and y components of zb. Calculate
        // the sum of the squares of the coefficients while
        // separating the components.
        denb = 0;
        for (i = 0; i < zb.length; i++)
        {
            xb[i] = zb[i];
            yb[i] = 0;
            denb += xb[i] * xb[i];
        }

        // Calculate normalization factor
        if (norm == NORM && dena != 0 && denb != 0)
        {
            scale = 1 / Math.sqrt(dena * denb);
        }
        else
        {
            scale = 1;
        }

        // Zero-pad za
        for (i = za.length; i < fftSize; i++)
        {
            xa[i] = 0;
            ya[i] = 0;
        }

        // Zero-pad zb
        for (i = zb.length; i < fftSize; i++)
        {
            xb[i] = 0;
            yb[i] = 0;
        }

        // Calculate the FFTs
        fftCore(xa, ya, false);
        fftCore(xb, yb, false);

        // Perform correlation in the frequency domain, re-using
        // arrays xa and ya for the output.
        for (i = 0; i < fftSize; i++)
        {
            xt = xa[i] * xb[i] + ya[i] * yb[i];
            yt = xa[i] * yb[i] - xb[i] * ya[i];
            xa[i] = xt;
            ya[i] = yt;
        }

        // Calculate the inverse FFT
        fftCore(xa, ya, true);

        // Create output array
        double zout[] = new double[za.length + zb.length - 1];

        // Restore the x and y components with the zero lag in the middle
        k = 0;
        if (norm == NORM)
        {
            for (i = xa.length - za.length + 1; i < xa.length; i++)
            {
                zout[k++] = xa[i] * scale;
            }
            for (i = 0; i < zb.length; i++)
            {
                zout[k++] = xa[i] * scale;
            }
        }
        else
        {
            for (i = xa.length - za.length + 1; i < xa.length; i++)
            {
                zout[k++] = xa[i];
            }
            for (i = 0; i < zb.length; i++)
            {
                zout[k++] = xa[i];
            }
        }

        return zout;
    }

    /**
     * This method is the computational core of the correlation algorithm. This
     * method destroys its input data, and returns the resulting
     * cross-correlation in <i>zout<i>.
     * 
     * @param za
     *            The input values for array 1
     * @param zb
     *            The input values for array 2
     * @param norm
     *            Normalization flag
     * @return the cross correlation of <code>za</code> and <code>zb</code>
     */
    private static Complex[] correlCore(Complex za[], Complex zb[], int norm)
    {

        // Calculate the size of FFT required
        int fftSize = nextMul(za.length + zb.length);

        // Declare additional variables and arrays
        double dena; // Sum of squares of za
        double denb; // Sum of squares of zb
        int i, k; // Loop index
        double scale; // Normalization factor
        double xa[] = new double[fftSize]; // x values from array za
        double xb[] = new double[fftSize]; // x values from array zb
        double xt; // Temp variable
        double ya[] = new double[fftSize]; // y values from array za
        double yb[] = new double[fftSize]; // y values from array zb
        double yt; // Temp variable

        // Separate the x and y components of za. Calculate
        // the sum of the squares of the coefficients while
        // separating the components.
        dena = 0;
        for (i = 0; i < za.length; i++)
        {
            xa[i] = za[i].re();
            ya[i] = za[i].im();
            dena += xa[i] * xa[i] + ya[i] * ya[i];
        }

        // Separate the x and y components of zb. Calculate
        // the sum of the squares of the coefficients while
        // separating the components.
        denb = 0;
        for (i = 0; i < zb.length; i++)
        {
            xb[i] = zb[i].re();
            yb[i] = zb[i].im();
            denb += xb[i] * xb[i] + yb[i] * yb[i];
        }

        // Calculate normalization factor
        if (norm == NORM && dena != 0 && denb != 0)
        {
            scale = 1 / Math.sqrt(dena * denb);
        }
        else
        {
            scale = 1;
        }

        // Zero-pad za
        for (i = za.length; i < fftSize; i++)
        {
            xa[i] = 0;
            ya[i] = 0;
        }

        // Zero-pad zb
        for (i = zb.length; i < fftSize; i++)
        {
            xb[i] = 0;
            yb[i] = 0;
        }

        // Calculate the FFTs
        fftCore(xa, ya, false);
        fftCore(xb, yb, false);

        // Perform correlation in the frequency domain, re-using
        // arrays xa and ya for the output.
        for (i = 0; i < fftSize; i++)
        {
            xt = xa[i] * xb[i] + ya[i] * yb[i];
            yt = xa[i] * yb[i] - xb[i] * ya[i];
            xa[i] = xt;
            ya[i] = yt;
        }

        // Calculate the inverse FFT
        fftCore(xa, ya, true);

        // Create output array
        Complex zout[] = new Complex[za.length + zb.length - 1];

        // Restore the x and y components with the zero lag in the middle
        k = 0;
        if (norm == NORM)
        {
            for (i = xa.length - za.length + 1; i < xa.length; i++)
            {
                zout[k++] = new Complex(xa[i] * scale, ya[i] * scale);
            }
            for (i = 0; i < zb.length; i++)
            {
                zout[k++] = new Complex(xa[i] * scale, ya[i] * scale);
            }
        }
        else
        {
            for (i = xa.length - za.length + 1; i < xa.length; i++)
            {
                zout[k++] = new Complex(xa[i], ya[i]);
            }
            for (i = 0; i < zb.length; i++)
            {
                zout[k++] = new Complex(xa[i], ya[i]);
            }
        }

        return zout;
    }

    /**
     * This method is the computational core of the forward and inverse Fast
     * Fourier Transforms. This method destroys its input data. The method
     * <i>assumes</i> that the length of the data passed to it is a power of
     * two, and that arrays <code>x</code> and <code>y</code> have equal length.
     * These facts must be verified before calling the method.
     * 
     * @param x
     *            The input real values.
     * @param y
     *            The input imaginary values values.
     * @param inv
     *            Forward/inverse flag: true = inverse
     * @return the FFT/inverse FFT of <code>(x,y)</code>.
     */
    private static void fftCore(double[] x, double[] y, boolean inv)
    {

        // Declare variables and arrays
        double a; // angle in radians
        double cosa, sina; // cos(a), sin(a)
        double e; // Angular step size
        int exp = 0; // Power of 2
        int i, j, k, l, n1, n2; // Loop index
        int m = 2; // 2^exp
        int n = x.length; // Array length
        double xt; // swap variable
        double yt; // swap variable

        // Work out the power of 2 that is >= the array length
        do
        {
            exp = exp + 1;
            m = 2 * m;
        }
        while (m < x.length);

        // Main FFT Loops
        n2 = n;
        for (k = 1; k <= m; k++)
        {
            n1 = n2;
            n2 = n2 / 2;
            e = TWO_PI / n1;
            a = 0;
            for (j = 1; j <= n2; j++)
            {
                if (inv)
                {
                    // Inverse FFT
                    cosa = Math.cos(a);
                    sina = Math.sin(a);
                }
                else
                {
                    // Forward FFT
                    cosa = Math.cos(a);
                    sina = -Math.sin(a);
                }
                a = j * e;
                for (i = j; i <= n; i += n1)
                {
                    l = i + n2;
                    xt = x[i - 1] - x[l - 1];
                    x[i - 1] = x[i - 1] + x[l - 1];
                    yt = y[i - 1] - y[l - 1];
                    y[i - 1] = y[i - 1] + y[l - 1];
                    x[l - 1] = xt * cosa - yt * sina;
                    y[l - 1] = xt * sina + yt * cosa;
                }
            }
        }

        // Digit reverse counter
        j = 1;
        n1 = n - 1;
        for (i = 1; i <= n1; i++)
        {
            if (i < j)
            {
                xt = x[j - 1];
                x[j - 1] = x[i - 1];
                yt = y[j - 1];
                y[j - 1] = y[i - 1];
                x[i - 1] = xt;
                y[i - 1] = yt;
            }
            k = n / 2;
            while (k < j)
            {
                j = j - k;
                k = k / 2;
            }
            j = j + k;
        }

        // Divide by array size for inverse FFT
        if (inv)
        {
            for (i = 0; i < n; i++)
            {
                x[i] = x[i] / n;
                y[i] = y[i] / n;
            }
        }
    }

    static void fftEx()
    {
        double[][] x =
        {
                {
                        22, 10, 20, 17, 3, 15, 13, 8, 1, 13, 2, 7, 6, 24, 1, 8
                },// 10, 22, 16, 20},
                {
                        6, 2, 12, 11, 20, 2, 21, 20, 4, 23, 17, 18, 11, 11, 19, 6
                },// 25, 8, 12, 14},
                {
                        18, 11, 0, 5, 24, 9, 21, 16, 19, 6, 18, 9, 12, 1, 15, 13
                },// 24, 4, 23, 14},
                {
                        7, 6, 7, 24, 4, 7, 18, 1, 2, 5, 12, 5, 22, 13, 21, 11
                },// 21, 25, 17, 4}
        };
        Matrix xt = new Matrix(x).transpose();
        int row = xt.getRowDimension();
        int col = xt.getColumnDimension();
        Matrix xt2 = new Matrix(row + 4, col);
        xt2.setMatrix(0, row - 1, 0, col - 1, xt);
        ComplexMatrix fftCM = SigProc.fft(xt);
        fftCM.printCP("fftCM");
    }

    public static void main(String[] args)
    {
        fftEx();
    }

}
