/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.stats.statsutil;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;
import jamaextension.jamax.stats.JStats;

/**
 *
 * @author sionep
 */
//public class MultipleLinearRegressionJamax {

//}

/**
 * <p>
 * Note that for the case when the intercept of the model equals zero, the
 * definition of R-squared and the F-statistic change mathematically. For a
 * linear model containing the y-intercept, R-squared refers to the amount of
 * variance around the mean of the dependent variable (y) which is explained by
 * the variance around the mean of the independent variables (x). For a linear
 * model NOT containing the y-intercept, R-squared measures the amount of
 * variance around ZERO of the dependent variable which is explained by the
 * variance around ZERO of the independent variable. If the same equation for
 * R-squared is used for both with and without a y-intercept (namely R-squared =
 * [Sum of Squares of the Regression] / [ Total sum of the squares]), then
 * R-squared may be a NEGATIVE value for some data. For this reason, this
 * subroutine will calculate R-squared using the total un-corrected sum of the
 * squares. In effect, this approach avoids negative R-squares but may lack any
 * meaningful interpretation for the "goodness-of-fit" in the model. It has been
 * suggested by some texts that a more useful approach is to always use the case
 * where y-intercept is included in the model.
 * </p>
 * 
 * <p>
 * For further reading on regression through the origin (i.e. without a
 * y-intercept), please refer to:
 * </p>
 * 
 * <p>
 * <UL>
 * 
 * <LI>Neter J, Kutner MH, Nachtsheim CJ, and Wasserman W. "Applied Linear
 * Statistical Models" 4th ed. Irwin publishing (Boston, 1996), pp 159-163.</LI>
 * 
 * <LI>Myers R, "Classical and Modern Regression with Applications" Duxbury
 * Press (Boston, 1986), p. 30.</LI>
 * </UL>
 * </p>
 * 
 * @author JRA
 * @version 1.0
 */
public class MultipleLinearRegressionJamax
{

    /**
     * A matrix for predictors (indepedent variables). The matrix must have at
     * least 2 columns or more.
     */
    private Matrix predictors;
    /**
     * A matrix for the dependent variable (response). This matrix must be a
     * vector,meaning it is either a single column or a single row.
     */
    private Matrix dependent;
    /**
     * A matrix for the <I>standardized coefficients</I> of the predictors
     * (indepedent variables). This is a vector, meaning it is a single column.
     */
    private Matrix stdCoefficients;
    /**
     * A matrix for the <I>unstandardized coefficients</I> of the predictors
     * (indepedent variables). This is a vector, meaning it is a single column.
     */
    private Matrix unStdCoefficients;
    /**
     * A matrix for the <B>standard error</B> of the <I>unstandardized
     * coefficients</I> of the predictors (indepedent variables). This is a
     * vector, meaning it is a single column.
     */
    private Matrix unStdCoefficientsStandardError;
    /**
     * A matrix for the <I>2 tailed t-test</I> of <I>standardized
     * coefficients</I> of the predictors (indepedent variables). This is a
     * vector, meaning it is a single column.
     */
    private Matrix coefficientsTStatistics;
    /**
     * A matrix for the <I>significant values</I> of the <B>standardized
     * coefficients</B> <I>2 tailed t-test</I> of the predictors (indepedent
     * variables). This is a vector, meaning it is a single column.
     */
    private Matrix coefficientsSignificant;
    /**
     * A matrix for the <I>residuals</I> between the predicted value and the
     * actual data.
     */
    private Matrix residuals;
    /**
     * A matrix for the <I>standardized residuals</I> between the predicted
     * value and the actual data.
     */
    private Matrix stdResiduals;
    /**
     * A matrix for the values predicted by the model using the calculated
     * coefficients.
     */
    private Matrix predictedValues;
    /**
     * A matrix for the <I>standardized values</I> predicted by the model using
     * the calculated coefficients.
     */
    private Matrix stdPredictedValues;
    /**
     * The regression covariance matrix.
     */
    private Matrix covariance;
    /**
     * Estimate standard error.
     */
    private double standardErrorOfEstimate = 1.0;
    /**
     * The <I>correlation coefficient</I> which is the square root of the
     * <I>coefficient of determination</I>.
     */
    private double R = 0.8;
    /**
     * The coefficient of determination.
     */
    private double RSquared = 0.8;
    /**
     * The adjusted coefficient of determination.
     */
    private double adjustedRSquared = 0.8;
    /**
     * A boolean flag to indicate if intercept (constant) is to be included or
     * not (default = true).
     */
    private final boolean includeIntercept = true;
    /**
     * ANOVA regression sum of squares.
     */
    private double anovaRegressionSumOfSquares = 1.0;
    /**
     * ANOVA residual sum of squares.
     */
    private double anovaResidualSumOfSquares = 1.0;
    /**
     * ANOVA total sum of squares.
     */
    private double anovaTotalSumOfSquares = 1.0;
    /**
     * ANOVA regression number of <I>degree of freedom</I>.
     */
    private int anovaRegressionDF = 1;
    /**
     * ANOVA regression number of <I>degree of freedom</I>.
     */
    private int anovaResidualDF = 1;
    /**
     * ANOVA total <I>degree of freedom</I>.
     */
    private int anovaTotalDF = 1;
    /**
     * ANOVA regression <I>mean square</I>.
     */
    private double anovaRegressionMeanSquare = 1.0;
    /**
     * ANOVA residual <I>mean square</I>.
     */
    private double anovaResidualMeanSquare = 1.0;
    /**
     * ANOVA <I>F-statistic</I>.
     */
    private double anovaF = 1.0;
    /**
     * ANOVA significant <I>F-statistic</I>.
     */
    private double anovaSignificantF = 0.1;
    /**
     * The minimum predicted value from the regression model.
     */
    private double predictedValuesMinimum = 1.0;
    /**
     * The maximum predicted value from the regression model.
     */
    private double predictedValuesMaximum = 1.0;
    /**
     * The mean predicted value from the regression model.
     */
    private double predictedValuesMean = 1.0;
    /**
     * The standard deviation of the predicted value from the regression model.
     */
    private double predictedValuesStdDeviation = 1.0;
    /**
     * The minimum value of the residuals from the regression model.
     */
    private double residualsMinimum = 1.0;
    /**
     * The maximum value of the residuals from the regression model.
     */
    private double residualsMaximum = 1.0;
    /**
     * The mean value of residuals from the regression model.
     */
    private double residualsMean = 1.0;
    /**
     * The residuals standard deviation.
     */
    private double residualsStdDeviation = 1.0;
    /**
     * The standardized minimum predicted value from the regression model.
     */
    private double stdPredictedValuesMinimum = 1.0;
    /**
     * The standardized maximum predicted value from the regression model.
     */
    private double stdPredictedValuesMaximum = 1.0;
    /**
     * The standardized mean predicted value from the regression model.
     */
    private double stdPredictedValuesMean = 1.0;
    /**
     * The standardized minimum standard deviation from the regression model.
     */
    private double stdPredictedValuesStdDeviation = 1.0;
    /**
     * The minimum value of the standardized residuals from the regression
     * model.
     */
    private double stdResidualsMinimum = 1.0;
    /**
     * The maximum value of the standardized residuals from the regression
     * model.
     */
    private double stdResidualsMaximum = 1.0;
    /**
     * The mean value of the standardized residuals from the regression model.
     */
    private double stdResidualsMean = 1.0;
    /**
     * The standard deviation of the standardized residuals from the regression
     * model.
     */
    private double stdResidualsStdDeviation = 1.0;
    /**
     * Number of respondents (cases).
     */
    private int sampleNumbers = 1;
    /**
     * A boolean variable to flag if the debug is on or off.
     */
    private boolean debug = false;
    /**
     * An array of integers for the indices of the dependent variable
     * (response).
     */
    private int[] dependentColumnIndex = null;
    /**
     * String array with the names of all the columns (variables) in the
     * dataset.
     */
    private String[] overallDataSetColumnNames = null;
    /**
     * String array to store the names of the predictors.
     */
    private String[] predictorColumnNames = null;
    /**
     * String array to store the names of columns in the dataset that represent
     * the dependent variable (response).
     */
    private String[] dependentColumnNames = null;

    /**
     * This class performs multiple linear regression analysis of X (independent
     * or 2 or more variables) on Y (single dependent variables).
     */
    public MultipleLinearRegressionJamax()
    {
    }

    /**
     * A pointer to the <I>predictorColumnNames</I> variable.
     * 
     * @return Array of predictors names.
     */
    public String[] getPredictorColumnNames()
    {
        return predictorColumnNames;
    }

    /**
     * A pointer to the <I>dependentColumnNames</I> variable.
     * 
     * @return Array of dependent names (which have been averaged).
     */
    public String[] getDependentColumnNames()
    {
        return dependentColumnNames;
    }

    /**
     * The number of respondents (cases).
     * 
     * @return total number of observations recorded (sample numbers).
     */
    public int getSampleNumbers()
    {
        return sampleNumbers;
    }

    /**
     * A setter to flag if debugging mode is on or off.
     * 
     * @param debug
     *            Set the flag if output to the standard systems output.
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /**
     * Find out the anova regression Sum-Of-Squares.
     * 
     * @return Anova regression sum of squares.
     */
    public double getAnovaRegressionSumOfSquares()
    {
        return anovaRegressionSumOfSquares;
    }

    /**
     * Find out the anova Residual-Sum-Of-Squares.
     * 
     * @return double - anova residual sum of squares.
     */
    public double getAnovaResidualSumOfSquares()
    {
        return anovaResidualSumOfSquares;
    }

    /**
     * Return the anova Total-Sum-Of-Squares.
     * 
     * @return double - anova total sum of squares.
     */
    public double getAnovaTotalSumOfSquares()
    {
        return anovaTotalSumOfSquares;
    }

    /**
     * Return the anova Regression-Mean-Squares.
     * 
     * @return double - anova regression mean square.
     */
    public double getAnovaRegressionMeanSquare()
    {
        return anovaRegressionMeanSquare;
    }

    /**
     * Return the anova Residual-Mean-Squares.
     * 
     * @return double - anova residual mean square.
     */
    public double getAnovaResidualMeanSquare()
    {
        return anovaResidualMeanSquare;
    }

    /**
     * Return the anova F-statistics.
     * 
     * @return double - anova F statistics.
     */
    public double getAnovaF()
    {
        return anovaF;
    }

    /**
     * Return the significant anova F-statistics.
     * 
     * @return double - anova F statistics significant.
     */
    public double getAnovaSignificantF()
    {
        return anovaSignificantF;
    }

    /**
     * Return the anova regression degree of freedom.
     * 
     * @return int - anova regression degree of freedom.
     */
    public int getAnovaRegressionDF()
    {
        return anovaRegressionDF;
    }

    /**
     * Return the anova residual degree of freedom.
     * 
     * @return int - anova residual degree of freedom.
     */
    public int getAnovaResidualDF()
    {
        return anovaResidualDF;
    }

    /**
     * Return the anova total degree of freedom.
     * 
     * @return int - anova total degree of freedom.
     */
    public int getAnovaTotalDF()
    {
        return anovaTotalDF;
    }

    /**
     * Return the estimate standard error.
     * 
     * @return double - standard error of estimates.
     */
    public double getStandardErrorOfEstimate()
    {
        return standardErrorOfEstimate;
    }

    /**
     * Return the correlation coefficient.
     * 
     * @return double - square root of R square statistics.
     */
    public double getR()
    {
        return R;
    }

    /**
     * Return the coefficient of determination.
     * 
     * @return double - R squared statistics.
     */
    public double getRSquared()
    {
        return RSquared;
    }

    /**
     * Return the adjusted coefficient of determination.
     * 
     * @return double - adjusted R squared statistics.
     */
    public double getAdjustedRSquared()
    {
        return adjustedRSquared;
    }

    /**
     * A pointer to the residuals matrix.
     * 
     * @return Matrix - matrix of residuals.
     */
    public Matrix getResiduals()
    {
        return residuals;
    }

    /**
     * A pointer to the standardized residuals matrix.
     * 
     * @return Matrix - matrix of Standardized Residuals.
     */
    public Matrix getStdResiduals()
    {
        return stdResiduals;
    }

    /**
     * A pointer to the predicted value matrix.
     * 
     * @return Matrix - matrix of model predicted values.
     */
    public Matrix getPredictedValues()
    {
        return predictedValues;
    }

    /**
     * A pointer to the standardized predicted value matrix.
     * 
     * @return Matrix - matrix of Standardized Predicted values.
     */
    public Matrix getStdPredictedValues()
    {
        return stdPredictedValues;
    }

    /**
     * A pointer to the covariance matrix.
     * 
     * @return Matrix - a matrix of covariance values.
     */
    public Matrix getCovariance()
    {
        return covariance;
    }

    /**
     * A pointer to the standardized coefficient matrix.
     * 
     * @return Matrix - matrix of standard coefficient values.
     */
    public Matrix getStdCoefficients()
    {
        return stdCoefficients;
    }

    /**
     * A pointer to the un-standardized coefficient matrix.
     * 
     * @return Matrix - matrix of Unstandardized coefficient values.
     */
    public Matrix getUnStdCoefficients()
    {
        return unStdCoefficients;
    }

    /**
     * A pointer to the standard error un-standardized coefficient matrix.
     * 
     * @return Matrix - standard error matrix of Unstandardized coefficient.
     */
    public Matrix getUnStdCoefficientsStandardError()
    {
        return unStdCoefficientsStandardError;
    }

    /**
     * A pointer to <I>coefficientsTStatistics</I> member variable.
     * 
     * @return Matrix - matrix of the T statistics of the Unstandardized
     *         coefficient values.
     */
    public Matrix getCoefficientsTStatistics()
    {
        return coefficientsTStatistics;
    }

    /**
     * A pointer to <I>coefficientsSignificant</I> member variable.
     * 
     * @return Matrix
     */
    public Matrix getCoefficientsSignificant()
    {
        return coefficientsSignificant;
    }

    /**
     * Return the minimum predicted values from regression model.
     * 
     * @return Minimum predicted values.
     */
    public double getPredictedValuesMinimum()
    {
        return predictedValuesMinimum;
    }

    /**
     * Return the maximum predicted values from regression model.
     * 
     * @return Maximum predicted values.
     */
    public double getPredictedValuesMaximum()
    {
        return predictedValuesMaximum;
    }

    /**
     * Return the mean predicted values from regression model.
     * 
     * @return Mean predicted values.
     */
    public double getPredictedValuesMean()
    {
        return predictedValuesMean;
    }

    /**
     * Return the standard deviation of the predicted values from regression
     * model.
     * 
     * @return Standard deviation of the predicted values.
     */
    public double getPredictedValuesStdDeviation()
    {
        return predictedValuesStdDeviation;
    }

    /**
     * Return the standardized minimum predicted values from regression model.
     * 
     * @return Standardized minimum predicted values from regression model.
     */
    public double getStdPredictedValuesMinimum()
    {
        return stdPredictedValuesMinimum;
    }

    /**
     * Return the standardized maximum predicted values from regression model.
     * 
     * @return Return the standardized maximum predicted values from regression
     *         model.
     */
    public double getStdPredictedValuesMaximum()
    {
        return stdPredictedValuesMaximum;
    }

    /**
     * Return the standardized mean predicted values from regression model.
     * 
     * @return Standardized mean predicted values.
     */
    public double getStdPredictedValuesMean()
    {
        return stdPredictedValuesMean;
    }

    /**
     * Return the standardized mean predicted values standard deviation from
     * regression model.
     * 
     * @return Standardized predicted values standard deviation.
     */
    public double getStdPredictedValuesStdDeviation()
    {
        return stdPredictedValuesStdDeviation;
    }

    /**
     * Return the minimum of the residuals.
     * 
     * @return Residuals minimum.
     */
    public double getResidualsMinimum()
    {
        return residualsMinimum;
    }

    /**
     * Return the maximum of the residuals.
     * 
     * @return Residuals maximum.
     */
    public double getResidualsMaximum()
    {
        return residualsMaximum;
    }

    /**
     * Return the mean of the residuals.
     * 
     * @return Mean of the residuals.
     */
    public double getResidualsMean()
    {
        return residualsMean;
    }

    /**
     * Return the standard deviation of the residuals.
     * 
     * @return Standard deviation of the residuals.
     */
    public double getResidualsStdDeviation()
    {
        return residualsStdDeviation;
    }

    /**
     * Return the minimum of the standardized residuals.
     * 
     * @return Standardized residuals minimum.
     */
    public double getStdResidualsMinimum()
    {
        return stdResidualsMinimum;
    }

    /**
     * Return the maximum of the standardized residuals.
     * 
     * @return Maximum of the standardized residuals.
     */
    public double getStdResidualsMaximum()
    {
        return stdResidualsMaximum;
    }

    /**
     * Return the mean of the standardized residuals.
     * 
     * @return Mean of the standardized residuals.
     */
    public double getStdResidualsMean()
    {
        return stdResidualsMean;
    }

    /**
     * Return the standard deviation of the standardized residuals.
     * 
     * @return Standard deviation of the standardized residuals.
     */
    public double getStdResidualsStdDeviation()
    {
        return stdResidualsStdDeviation;
    }

    /**
     * A setter for the <I>predictor</I> matrix.
     * 
     * @param predictor
     *            A predictor (independent variables) matrix.
     */
    public void setPredictor(Matrix predictor)
    {
        if (predictor == null)
        {
            throw new IllegalArgumentException(" setPredictory : Parameter 'predictor' must be non-null.");
        }
        int rows = predictor.getRowDimension();
        int cols = predictor.getColumnDimension();
        if (Math.min(rows, cols) < 2)
        {
            throw new IllegalArgumentException(" setPredictory : Parameter 'predictor' must have at least 2 variables.");
        }
        if (Math.max(rows, cols) < 4)
        {
            throw new IllegalArgumentException(
                    " setPredictory : Parameter 'predictor' must have at least 4 observations.");
        }

        this.predictors = predictor;
    }

    /**
     * Set the dependent matrix for regression. The matrix must be a vector, ie,
     * a single row or a column.
     * 
     * @param dependent
     *            Matrix - This must be a 'm' rows by one column . Example is a
     *            100 rows by 1 column, that is a single column with 100
     *            elements.
     */
    public void setDependent(Matrix dependent)
    {
        if (dependent == null)
        {
            throw new IllegalArgumentException(" dependent : Parameter 'dependent' must be non-null.");
        }
        if (dependent.isVector() == false)
        {
            throw new IllegalArgumentException(" dependent : Parameter 'dependent' must be a row or colum vector.");
        }
        // int rows = dependent.getRowDimension();
        // int cols = dependent.getColumnDimension();
        if (dependent.isRowVector())
        {
            this.dependent = dependent.toColVector();
        }
        else
        {
            this.dependent = dependent;
        }
    }

    /**
     * Main computation method for Multiple Linear Regression - Last method to
     * be called before retrieval method calls.
     */
    public void computeStats()
    {
        if (predictors == null)
        {
            throw new IllegalArgumentException(" computeStats : Parameter 'predictor' must be non-null.");
        }
        if (dependent == null)
        {
            throw new IllegalArgumentException(" computeStats : Parameter 'dependent' must be non-null.");
        }

        int rows = predictors.getRowDimension();
        int cols = predictors.getColumnDimension();

        int rowY = dependent.getRowDimension();
        if (rows != rowY)
        {
            throw new IllegalArgumentException(" computeStats : Rows for 'predictor' must equal rows for 'dependent' .");
        }

        if ((rows - cols - 1) <= 0)
        {
            throw new IllegalArgumentException(
                    " computeStats : Observations or rows for 'predictor' must equal be greater than the number of predictors (or columns) by 1 .");
        }

        this.sampleNumbers = rows;

        anovaRegressionDF = cols;
        anovaTotalDF = rows - 1;
        anovaResidualDF = anovaTotalDF - anovaRegressionDF;

        Matrix temp = null;
        Matrix predictorCopy = predictors.copy();
        if (includeIntercept)
        {
            temp = new Matrix(rows, 1, 1.0);
            predictors = temp.mergeH(predictors);
        }

        unStdCoefficients = computeCoefficients(predictors, dependent);
        temp = new Matrix(rows, 1, 1.0);
        stdCoefficients = computeCoefficients(temp.mergeH(JStats.zscore(predictorCopy)), JStats.zscore(dependent));
        predictedValues = predictors.times(unStdCoefficients);
        stdPredictedValues = JStats.zscore(predictedValues);

        double RSS = 1.0;
        double TSS = 1.0;

        if (includeIntercept)
        {
            RSS = predictedValues.minus(JDatafun.mean(dependent).get(0, 0)).norm2(); // Regression
                                                                                     // sum
                                                                                     // of
                                                                                     // squares.
            RSS = RSS * RSS;
            TSS = dependent.minus(JDatafun.mean(dependent).get(0, 0)).norm2(); // Total
                                                                               // sum
                                                                               // of
                                                                               // squares
                                                                               // (regression
                                                                               // plus
                                                                               // residual).
            TSS = TSS * TSS;
        }
        else
        {
            RSS = predictedValues.norm2(); // Regression sum of squares.
            RSS = RSS * RSS;
            TSS = dependent.norm2(); // Total, un-corrected sum of squares.
            TSS = TSS * TSS;
        }

        anovaRegressionSumOfSquares = RSS;
        anovaTotalSumOfSquares = TSS;

        RSquared = RSS / TSS; // R-squared statistic.
        adjustedRSquared = 1.0 - ((double) (rows - 1) / (double) (rows - (cols + 1))) * (1.0 - RSquared); // adjusted
                                                                                                          // R^2
        R = Math.sqrt(RSquared);

        // Calculate residuals and standard error
        // --------------------------------------
        residuals = dependent.minus(predictedValues);
        stdResiduals = residuals.arrayTimes(2.0);
        temp = residuals.transpose().times(residuals);
        double val = 1.0;

        if (includeIntercept)
        {
            val = (double) (rows - cols - 1);
        }
        else
        {
            val = (double) (rows - cols);
        }

        standardErrorOfEstimate = Math.sqrt(temp.arrayRightDivide(val).get(0, 0));

        // Calculate the standard deviation and t-values for the regression
        // unStdCoefficients
        // -----------------------------------------------------------------------------
        Matrix XTXI = predictors.transpose().times(predictors).inverse();
        covariance = XTXI.arrayTimes(standardErrorOfEstimate * standardErrorOfEstimate);

        unStdCoefficientsStandardError = JElfun.sqrt(covariance.diag());
        int sizCoeffsRow = unStdCoefficients.getRowDimension();
        int sizCoeffsCol = unStdCoefficients.getColumnDimension();
        coefficientsSignificant = new Matrix(sizCoeffsRow, sizCoeffsCol);
        int v = 0;

        // (n.b. Need to perform a 2-tailed t-test)
        // ========================================
        if (includeIntercept)
        {
            v = rows - (cols + 1);
        }
        else
        {
            v = rows - cols;
        }

        for (int i = 0; i < sizCoeffsRow; i++)
        {
            for (int j = 0; j < sizCoeffsCol; j++)
            {
                val = Math.abs(unStdCoefficients.get(i, j) / unStdCoefficientsStandardError.get(i, j));
                val = 2.0 * (1.0 - JStats.tcdf(val, v));
                coefficientsSignificant.set(i, j, val);
            }
        }

        coefficientsTStatistics = unStdCoefficients.arrayRightDivide(unStdCoefficientsStandardError);

        double SSR_residuals = 1.0;

        // Estimator of error variance.
        SSR_residuals = dependent.minus(predictedValues).norm2();
        SSR_residuals = SSR_residuals * SSR_residuals;
        anovaResidualSumOfSquares = SSR_residuals;

        if (includeIntercept)
        {
            TSS = dependent.minus(JDatafun.mean(dependent).get(0, 0)).norm2(); // Total
                                                                               // sum
                                                                               // of
                                                                               // squares
                                                                               // (regression
                                                                               // plus
                                                                               // residual).
            TSS = TSS * TSS;
            val = (TSS - SSR_residuals) / (double) cols / (SSR_residuals / (double) (rows - (cols + 1)));
            anovaF = val;
            /*
             * System.out.println(
             * "=======================================================");
             * System.out.println("  CDF 1 = "+CumulativeDF.fcdf(val, cols,
             * (rows - (cols + 1))) ); System.out.println(
             * "=======================================================");
             */
            val = 1.0 - JStats.fcdf(val, cols, (rows - (cols + 1)));
            anovaSignificantF = val;
        }
        else
        {
            TSS = dependent.norm2();
            TSS = TSS * TSS;
            val = (TSS - SSR_residuals) / (double) cols / (SSR_residuals / (double) (rows - cols));
            anovaF = val;
            /*
             * System.out.println(
             * "=======================================================");
             * System.out.println("  CDF 2 = "+CumulativeDF.fcdf(val, cols,
             * (rows - (cols + 1))) ); System.out.println(
             * "=======================================================");
             */
            val = 1.0 - JStats.fcdf(val, cols, (rows - cols));
            anovaSignificantF = val;
        }

        anovaRegressionMeanSquare = anovaRegressionSumOfSquares / (double) anovaRegressionDF;
        anovaResidualMeanSquare = anovaResidualSumOfSquares / (double) anovaResidualDF;

        predictedValuesMinimum = JDatafun.min(predictedValues).get(0, 0);
        predictedValuesMaximum = JDatafun.max(predictedValues).get(0, 0);
        predictedValuesMean = JDatafun.mean(predictedValues).get(0, 0);
        predictedValuesStdDeviation = JDatafun.std(predictedValues).get(0, 0);

        stdPredictedValuesMinimum = JDatafun.min(stdPredictedValues).get(0, 0);
        stdPredictedValuesMaximum = JDatafun.max(stdPredictedValues).get(0, 0);
        stdPredictedValuesMean = JDatafun.mean(stdPredictedValues).get(0, 0);
        stdPredictedValuesStdDeviation = JDatafun.std(stdPredictedValues).get(0, 0);

        residualsMinimum = JDatafun.min(residuals).get(0, 0);
        residualsMaximum = JDatafun.max(residuals).get(0, 0);
        residualsMean = JDatafun.mean(residuals).get(0, 0);
        residualsStdDeviation = JDatafun.std(residuals).get(0, 0);

        stdResidualsMinimum = JDatafun.min(stdResiduals).get(0, 0);
        stdResidualsMaximum = JDatafun.max(stdResiduals).get(0, 0);
        stdResidualsMean = JDatafun.mean(stdResiduals).get(0, 0);
        stdResidualsStdDeviation = JDatafun.std(stdResiduals).get(0, 0);

        // show output
        if (debug)
        {
            printOutput("");
        }
    }

    /**
     * Print the overall regression result to a StringBuffer for output either
     * as to be stored in a file or out to the console.
     * 
     * @param modelName
     *            Name of the regression to be printed.
     * @return Regression output.
     */
    public String printOutput(String modelName)
    {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(printModelSummary(modelName));
        strBuf.append(printAnova());
        strBuf.append(printCoefficients());
        strBuf.append(printResiduals());
        // printColumnNames();
        return strBuf.toString();
    }

    /**
     * Print the residuals.
     * 
     * @return Residual matrix as formatted string.
     */
    private String printResiduals()
    {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("================ Residuals ==============\n");

        strBuf.append("predicted Values  Minimum      = " + predictedValuesMinimum + "\n");
        strBuf.append("predicted Values  Maximum      = " + predictedValuesMaximum + "\n");
        strBuf.append("predicted Values  Mean         = " + predictedValuesMean + "\n");
        strBuf.append("predicted Values Std.Deviation =  " + predictedValuesStdDeviation + "\n");

        strBuf.append("\n");
        strBuf.append("residuals  Minimum      = " + residualsMinimum + "\n");
        strBuf.append("residuals  Maximum      = " + residualsMaximum + "\n");
        strBuf.append("residuals  Mean         = " + residualsMean + "\n");
        strBuf.append("residuals Std.Deviation =  " + residualsStdDeviation + "\n");

        strBuf.append("\n");
        strBuf.append("Std.Predicted Values  Minimum      = " + stdPredictedValuesMinimum + "\n");
        strBuf.append("Std.Predicted Values  Maximum      = " + stdPredictedValuesMaximum + "\n");
        strBuf.append("Std.Predicted Values  Mean         = " + stdPredictedValuesMean + "\n");
        strBuf.append("Std.Predicted Values Std.Deviation =  " + stdPredictedValuesStdDeviation + "\n");

        strBuf.append("\n");
        strBuf.append("Std.Residuals Minimum      = " + stdResidualsMinimum + "\n");
        strBuf.append("Std.Residuals Maximum      = " + stdResidualsMaximum + "\n");
        strBuf.append("Std.Residuals Mean         = " + stdResidualsMean + "\n");
        strBuf.append("Std.Residuals StdDeviation =  " + stdResidualsStdDeviation + "\n");

        strBuf.append("\n");

        return strBuf.toString();
    }

    /**
     * Print the regression model summary result only to a StringBuffer for
     * output either as to be stored in a file or out to the console.
     * 
     * @param modelName
     *            Name of the regression model.
     * @return Model summary.
     */
    private String printModelSummary(String modelName)
    {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("\n=============== " + modelName + " Model Summary =============\n");
        strBuf.append(" R = " + R + "\n");
        strBuf.append("\n");
        strBuf.append(" R-Squared = " + RSquared + "\n");
        strBuf.append("\n");
        strBuf.append(" Adjusted R-Squared = " + adjustedRSquared + "\n");
        strBuf.append("\n");
        strBuf.append(" Standard Error Of Estimates = " + standardErrorOfEstimate + "\n");
        strBuf.append("\n");
        return strBuf.toString();
    }

    /**
     * Print ANOVA.
     * 
     * @return ANOVA formatted string.
     */
    private String printAnova()
    {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("\n=============== Anova =============\n");

        strBuf.append("Anova Regression SumOfSquares = " + anovaRegressionSumOfSquares + "\n"); // System.out.println("Anova Regression SumOfSquares = "+anovaRegressionSumOfSquares);
        strBuf.append("Anova Residual SumOfSquares   = " + anovaResidualSumOfSquares + "\n"); // System.out.println("Anova Residual SumOfSquares   = "+anovaResidualSumOfSquares);
        strBuf.append("Anova Total SumOfSquares      = " + anovaTotalSumOfSquares + "\n"); // System.out.println("Anova Total SumOfSquares      = "+anovaTotalSumOfSquares);
        strBuf.append("\n"); // System.out.println("\n");
        strBuf.append("Anova Regression DF = " + anovaRegressionDF + "\n"); // System.out.println("Anova Regression DF = "+anovaRegressionDF);
        strBuf.append("Anova Residual DF   = " + anovaResidualDF + "\n"); // System.out.println("Anova Residual DF   = "+anovaResidualDF);
        strBuf.append("Anova Total DF      = " + anovaTotalDF + "\n"); // System.out.println("Anova Total DF      = "+anovaTotalDF);
        strBuf.append("\n"); // System.out.println("\n");
        strBuf.append("Anova Regression Mean-Square = " + anovaRegressionMeanSquare + "\n"); // System.out.println("Anova Regression Mean-Square = "+anovaRegressionMeanSquare);
        strBuf.append("Anova Residual Mean-Square   = " + anovaResidualMeanSquare + "\n"); // System.out.println("Anova Residual Mean-Square   = "+
                                                                                           // anovaResidualMeanSquare
                                                                                           // );
        strBuf.append("\n"); // System.out.println("\n");
        strBuf.append("Anova F = " + anovaF + "\n"); // System.out.println("Anova F = "+anovaF
                                                     // );
        strBuf.append("\n"); // System.out.println("\n");
        strBuf.append("Anova Significant F = " + anovaSignificantF + "\n"); // System.out.println("Anova Significant F = "+anovaSignificantF);
        strBuf.append("\n"); // System.out.println("\n");

        return strBuf.toString();
    }

    /**
     * Print the calculated regression co-efficients.
     * 
     * @return Regression co-efficients formatted string.
     */
    private String printCoefficients()
    {
        StringBuffer strBuf = new StringBuffer();

        /*
         * strBuf.append("-------- Covariance --------\n");
         * 
         * strBuf.append(covariance.printToString(4, 6)); strBuf.append("\n");
         * //System.out.println("\n");
         * 
         * strBuf.append("-------- UnStandardized Coefficients --------\n");
         * strBuf.append(unStdCoefficients.printToString(4, 6));
         * strBuf.append("\n"); //System.out.println("\n");
         * 
         * strBuf.append(
         * "-------- UnStandardized Coefficients Standard Error --------\n");
         * strBuf.append(unStdCoefficientsStandardError.printToString(4, 6));
         * strBuf.append("\n"); //System.out.println("\n");
         * 
         * strBuf.append("-------- Standardized Coefficients --------\n");
         * strBuf.append(stdCoefficients.printToString(4, 6));
         * strBuf.append("\n"); //System.out.println("\n");
         * 
         * strBuf.append("-------- Coefficients T Statistics --------\n");
         * strBuf.append(coefficientsTStatistics.printToString(4, 6));
         * strBuf.append("\n"); //System.out.println("\n");
         * 
         * strBuf.append("-------- Coefficients Significant --------\n");
         * strBuf.append(coefficientsSignificant.printToString(4, 6));
         * strBuf.append("\n"); //System.out.println("\n");
         */

        return strBuf.toString();
    }

    /**
     * Print the column names (variable names) or items.
     */
    private void printColumnNames()
    {
        StringBuffer strBuf = new StringBuffer();
        int len = this.overallDataSetColumnNames.length;
        for (int i = 0; i < len; i++)
        {
            strBuf.append(overallDataSetColumnNames[i] + "   ");
        }
        System.out.println("Overall Dataset Column Names :  [ " + strBuf.toString() + " ]");
        System.out.println("\n");

        len = this.predictorColumnNames.length;
        strBuf = new StringBuffer();
        for (int i = 0; i < len; i++)
        {
            strBuf.append(predictorColumnNames[i] + "   ");
        }
        System.out.println("Predictor Names :  [ " + strBuf.toString() + " ]");
        System.out.println("\n");

        strBuf = new StringBuffer();
        len = this.dependentColumnNames.length;
        for (int i = 0; i < len; i++)
        {
            strBuf.append(dependentColumnNames[i] + "  ");
        }
        System.out.println("Dependent Names :  [ " + strBuf.toString() + " ]");
        System.out.println("\n");
    }

    /**
     * Return a copy of the predictors matrix.
     * 
     * @return Copy of the predictors matrix.
     */
    public Matrix getPredictor()
    {
        if (this.predictors == null)
        {
            return null;
        }
        return predictors.copy();
    }

    /**
     * Return a copy of the dependent factor matrix.
     * 
     * @return Copy of the dependent factor matrix.
     */
    public Matrix getDependent()
    {
        if (this.dependent == null)
        {
            return null;
        }
        return this.dependent.copy();
    }

    /**
     * Compute the regression coefficients, given the predictors (independent)
     * and the response (dependent) variables.
     * 
     * @param X
     *            Matrix - indpendent variable (predictors).
     * @param Y
     *            Matrix - dependent variable
     * @return Matrix - regression of Y agaist X.
     */
    private Matrix computeCoefficients(Matrix X, Matrix Y)
    {
        Matrix mat = X.transpose().times(X).inverse();
        mat = mat.times(X.transpose()).times(Y);
        return mat;
    }

    /**
     * A Main method for standalone testing.
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        // create new object
        MultipleLinearRegressionJamax MLR = new MultipleLinearRegressionJamax();

        // run the test with hard-coded data --> This hard coded data was given
        // by Ian (JRA).
        MLR.runTest();

        // retrieve results by calling member variables of MLR object
    }

    /**
     * A method for internal testing only. It is not used anywhere.
     */
    public void runTest()
    {
        /*
         * MultipleLinearRegressionJamax mlr = new
         * MultipleLinearRegressionJamax(); Matrix x = TestMain.getXData();
         * int[] ind = Matrix.intLinspace(0,11); x = x.getRows(ind); Matrix y =
         * TestMain.getYData(); y = y.getRows(ind);
         * System.out.println("===== x ====="); x.print(2,0);
         * System.out.println("\n"); System.out.println("===== y =====");
         * y.print(2,0); mlr.setPredictor(x); mlr.setDependent(y);
         * mlr.setDebug(true); mlr.computeStats();
         */
    }

    // ////////////////////////////////////////////////////////////////////////////
    /**
     * A formatted HTML of the regression output.
     * 
     * @param modelName
     *            String
     * @return String
     */
    public String[] getHtmlOutput(String modelName)
    {
        // StringBuffer strBuf = new StringBuffer();
        // strBuf.append(printModelSummary(modelName));
        // strBuf.append(printAnova());
        // strBuf.append(printCoefficients());
        // strBuf.append(printResiduals());
        String[] table =
        {
                getHtmlModelSummary(modelName), getHtmlAnova(), getHtmlCoefficients(), getHtmlResiduals()
        };

        return table;
    }

    private String getHtmlModelSummary(String modelName)
    {

        String title = "";
        if (modelName == null || "".equals(modelName))
        {
            title = "Model Summary";
        }
        else
        {
            title = modelName + " Model Summary";
        }

        String str = "<table border=\"0\">" + "<thead>" + "<tr>" + "<th align=\"left\"><u>" + title + "</u></th>"
                + "</tr>" + "</thead>" + "<tbody>" + "<tr>" + "<td>R</td>" + "<td>=</td>" + "<td>" + R + "</td>"
                + "</tr>" + "<tr>" + "<td>R-Squared</td>" + "<td>=</td>" + "<td>" + RSquared + "</td>" + "</tr>"
                + "<tr>" + "<td>Adjusted R-Squared</td>" + "<td>=</td>" + "<td>" + adjustedRSquared + "</td>" + "</tr>"
                + "<tr>" + "<td>Standard Error Of Estimates</td>" + "<td>=</td>" + "<td>" + standardErrorOfEstimate
                + "</td>" + "</tr>" + "<tr><td>&nbsp;</td></tr>" + "</tbody>" + "</table>";

        return str;
    }

    private String getHtmlAnova()
    {

        String str = "<table border=\"0\">" + "<thead>" + "<tr>" + "<th align=\"left\"><u>Anova</u></th>" + "</tr>"
                + "</thead>" + "<tbody>" + "<tr>" + "<td>Anova Regression SumOfSquares</td>" + "<td>=</td>" + "<td>"
                + anovaRegressionSumOfSquares
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Anova Residual SumOfSquares</td>"
                + "<td>=</td>"
                + "<td>"
                + anovaResidualSumOfSquares
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Anova Total SumOfSquares</td>"
                + "<td>=</td>"
                + "<td>"
                + anovaTotalSumOfSquares
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Anova Regression DF</td>"
                + "<td>=</td>"
                + "<td>"
                + anovaRegressionDF
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Anova Residual DF</td>"
                + "<td>=</td>"
                + "<td>"
                + anovaResidualDF
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Anova Total DF</td>"
                + "<td>=</td>"
                + "<td>"
                + anovaTotalDF
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Anova Regression Mean-Square</td>"
                + "<td>=</td>"
                + "<td>"
                + anovaRegressionMeanSquare
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Anova Residual Mean-Square</td>"
                + "<td>=</td>"
                + "<td>"
                + anovaResidualMeanSquare
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Anova F</td>"
                + "<td>=</td>"
                + "<td>"
                + anovaF
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Anova Significant F</td>"
                + "<td>=</td>"
                + "<td>"
                + anovaSignificantF
                + "</td>"
                + "</tr>"
                + "<tr><td>&nbsp;</td></tr>"
                + "</tbody>" + "</table>";

        return str;
    }// end method

    private String getHtmlCoefficients()
    {

        int rows = covariance.getRowDimension();
        int cols = covariance.getColumnDimension();

        int width = 100 / cols;

        // Covariance table
        String str = "<table border=\"1\" width=90% >";
        str += "<thead>" + "<tr>" + "<th colspan=\"" + cols + "\"><u>Covariance</u></th>" + "</tr>" + "</thead>"
                + "<tbody>";
        // "<tr><td>&nbsp;</td></tr>";

        for (int i = 0; i < rows; i++)
        {
            str += "<tr >";
            for (int j = 0; j < cols; j++)
            {
                if (i == 0)
                {
                    str += "<td width=" + width + "%> " + covariance.get(i, j) + " </td>";
                }
                else
                {
                    str += "<td > " + covariance.get(i, j) + " </td>";
                }
            }
            str += "</tr>";
        }

        str += "</tbody>" + "</table>";

        // Coefficients table
        String str2 = "<table border=\"0\" width=90% >";
        str2 += "<thead>" + "<tr>" + "<th colspan=\"5\"><u>Coefficients</u></th>" + "</tr>" + "</thead>" + "<tbody>"
                + "<tr><td>&nbsp;</td></tr>" + "<tr >" + "<td width=20%><u>UnStandardized</u></td>"
                + "<td width=20%><u>UnStandardized Std-Error</u></td>" + "<td width=20%><u>Standardized</u></td>"
                + "<td width=20%><u>T-Statistics</u></td>" + "<td width=20%><u>Significant</u></td>" + "</tr>";

        int len = unStdCoefficients.length();

        for (int i = 0; i < len; i++)
        {
            str2 += "<tr>" + "<td>" + unStdCoefficients.get(i, 0) + "</td>" + "<td>"
                    + unStdCoefficientsStandardError.get(i, 0) + "</td>" + "<td>" + stdCoefficients.get(i, 0) + "</td>"
                    + "<td>" + coefficientsTStatistics.get(i, 0) + "</td>" + "<td>" + coefficientsSignificant.get(i, 0)
                    + "</td>" + "</tr>";
        }// end for

        str2 += "</tbody>" + "</table>";

        // Covariance and Coefficients table
        String str3 = "<table border=\"0\">" + "<tbody>" + "<tr>" + "<td>" + str + "</td>" + "</tr>" + "<tr>" + "<td>"
                + str2 + "</td>" + "</tr>" + "</tbody>" + "</table>";

        return str3;
    }// end method

    private String getHtmlResiduals()
    {

        String str = "<table border=\"0\">" + "<thead>" + "<tr>" + "<th align=\"left\"><u>Residuals</u></th>" + "</tr>"
                + "</thead>" + "<tbody>" + "<tr>" + "<td>predicted Values  Minimum</td>" + "<td>=</td>" + "<td>"
                + predictedValuesMinimum
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>predicted Values  Maximum</td>"
                + "<td>=</td>"
                + "<td>"
                + predictedValuesMaximum
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>predicted Values  Mean</td>"
                + "<td>=</td>"
                + "<td>"
                + predictedValuesMean
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>predicted Values Std.Deviation</td>"
                + "<td>=</td>"
                + "<td>"
                + predictedValuesStdDeviation
                + "</td>"
                + "</tr>"
                + // ========================
                "<tr>"
                + "<td>residuals  Minimum</td>"
                + "<td>=</td>"
                + "<td>"
                + residualsMinimum
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>residuals  Maximum</td>"
                + "<td>=</td>"
                + "<td>"
                + residualsMaximum
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>residuals  Mean</td>"
                + "<td>=</td>"
                + "<td>"
                + residualsMean
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>residuals Std.Deviation</td>"
                + "<td>=</td>"
                + "<td>"
                + residualsStdDeviation
                + "</td>"
                + "</tr>"
                + // ========================
                "<tr>"
                + "<td>Std.Predicted Values  Minimum</td>"
                + "<td>=</td>"
                + "<td>"
                + stdPredictedValuesMinimum
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Std.Predicted Values  Maximum</td>"
                + "<td>=</td>"
                + "<td>"
                + stdPredictedValuesMaximum
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Std.Predicted Values  Mean</td>"
                + "<td>=</td>"
                + "<td>"
                + stdPredictedValuesMean
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Std.Predicted Values Std.Deviation</td>"
                + "<td>=</td>"
                + "<td>"
                + stdPredictedValuesStdDeviation
                + "</td>"
                + "</tr>"
                + // ========================
                "<tr>"
                + "<td>Std.Residuals Minimum</td>"
                + "<td>=</td>"
                + "<td>"
                + stdResidualsMinimum
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Std.Residuals Maximum</td>"
                + "<td>=</td>"
                + "<td>"
                + stdResidualsMaximum
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Std.Residuals Mean</td>"
                + "<td>=</td>"
                + "<td>"
                + stdResidualsMean
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Std.Residuals StdDeviation</td>"
                + "<td>=</td>"
                + "<td>"
                + stdResidualsStdDeviation + "</td>" + "</tr>" + "<tr><td>&nbsp;</td></tr>" + "</tbody>" + "</table>";

        return str;

    }
}// -------------------------- End Class Definition
// -----------------------------

