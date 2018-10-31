/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.polyfun;

import java.util.ArrayList;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.PrintMessage;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.constants.InterpMethod;
import jamaextension.jamax.datafun.HistogramCount;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Sione
 */
public class Interp1
{

    private ArrayList<Object> varargout = new ArrayList<Object>();
    private PrintMessage pmessage = new PrintMessage(false);

    public Interp1(Object... varargin)
    {
        int xOffset = 0;
        // String message = "";
        pmessage.setFlanks("######################");
        if (varargin == null)
        {
            throw new ConditionalException("Interp1 : Array parameter \"varargin\" must be non-null.");
        }
        int nargin = varargin.length;
        if (nargin < 2)
        {
            throw new ConditionalException(
                    "Interp1 : Length of array parameter \"varargin\" must not be less than 2 or greater than 4;");
        }
        if (checkIfArgsElsNull(varargin))
        {
            throw new ConditionalException("Interp1 : All elements of array parameter \"varargin\" are null.");
        }

        boolean cond = (nargin == 2) || (nargin == 3 && (varargin[2] != null) && (varargin[2] instanceof InterpMethod));
        boolean cond2 = nargin == 4 && !(varargin[3] == null || varargin[3] instanceof InterpFlag);
        cond2 = cond2 || (nargin == 4 && (varargin[3] != null) && (varargin[3] instanceof InterpFlag));
        // ( nargin==4 && ~(ischar(varargin{4}) || isempty(varargin{4})) || ...
        // (nargin==4 && strcmp(varargin{4}, 'extrap')) );

        cond = cond || cond2;
        if (cond)
        {
            // message = "BLOCK 1";
            pmessage.print("BLOCK 1");
            xOffset = -1;
        }

        boolean ppOutput = false;
        // PP = INTERP1(X,Y,METHOD,'pp')
        if (nargin >= 4
                && ((varargin[2] != null) && (varargin[2] instanceof InterpMethod))
                && ((varargin[3] != null) && (varargin[3] instanceof InterpFlag) && (((InterpFlag) varargin[3]) == InterpFlag.PP)))
        {// isequal('pp',varargin{4})
            ppOutput = true;
            if (nargin > 4)
            {
                // error('MATLAB:interp1:ppOutput', ...
                // 'Use 4 inputs for PP=INTERP1(X,Y,METHOD,''pp'').')
                throw new ConditionalException("Interp1 : Use 4 inputs for PP=INTERP1(X,Y,METHOD,''pp'').");
            }// end
            pmessage.print("BLOCK 2");
        }// end

        // Process Y in INTERP1(Y,...) and INTERP1(X,Y,...)
        Matrix y = (Matrix) varargin[1 + xOffset];
        // siz_y = size(y);
        int sizyRow = y.getRowDimension();
        int sizyCol = y.getColumnDimension();
        // y may be an ND array, but collapse it down to a 2D yMat. If yMat is
        // a vector, it is a column vector.
        int n = 0;
        Matrix yMat = null;
        int ds = 0;
        int prodDs = 1;
        if (y.isVector())
        {
            n = sizyRow;
            if (sizyRow == 1)
            {
                // Prefer column vectors for y
                yMat = y.transpose();
                n = sizyCol;// siz_y(2);
                pmessage.print("BLOCK 3");
            }
            else
            {
                yMat = y;
                // n = siz_y(1);
                pmessage.print("BLOCK 4");
            }// end
            ds = 1;// new Indices(1,1,1);
            prodDs = 1;
            pmessage.print("BLOCK 5");
        }
        else
        {
            n = sizyRow;// siz_y(1);
            ds = sizyCol;// siz_y(2:end);
            prodDs = ds; // prod(ds);
            yMat = y.reshape(n, prodDs);// reshape(y,[n prodDs]);
            pmessage.print("BLOCK 6");
        }// end

        // Process X in INTERP1(X,Y,...), or supply default for INTERP1(Y,...)
        Matrix xCol = null;
        if (xOffset > -1)
        {
            Matrix x = (Matrix) varargin[xOffset];
            if (!x.isVector())
            {// ~isvector(x)
             // error('MATLAB:interp1:Xvector','X must be a vector.');
                throw new ConditionalException("Interp1 : X must be a vector.");
            }// end
            if (x.length() != n)
            {
                if (y.isVector())
                {// isvector(y)
                 // error('MATLAB:interp1:YInvalidNumRows', 'X and Y must be
                 // of the same length.')
                    throw new ConditionalException("Interp1 : X and Y must be of the same length.");
                }
                else
                {
                    // error('MATLAB:interp1:YInvalidNumRows', 'LENGTH(X) and
                    // SIZE(Y,1) must be the same.');
                    throw new ConditionalException("Interp1 : LENGTH(X) and SIZE(Y,1) must be the same.");
                }// end
            }// end
             // Prefer column vectors for x
            if (x.isRowVector())
            {
                xCol = x.toColVector();// x(:);
                pmessage.print("BLOCK 7");
            }
            else
            {
                xCol = x;
                pmessage.print("BLOCK 8");
            }
            pmessage.print("BLOCK 9");
        }
        else
        {
            // xCol = (1:n)';
            xCol = Matrix.linIncrement(1.0, (double) n, 1.0).transpose();
            pmessage.print("BLOCK 10");
        }// end

        // Process XI in INTERP1(Y,XI,...) and INTERP1(X,Y,XI,...)
        // Avoid syntax PP = INTERP1(X,Y,METHOD,'pp')
        Matrix xi = null;
        Matrix xiCol = null;
        Indices siz_yi = null;
        if (!ppOutput)
        {
            xi = (Matrix) varargin[2 + xOffset];
            Indices siz_xi = xi.sizeIndices();// size(xi);
            // xi may be an ND array, but flatten it to a column vector xiCol
            if (xi.isRowVector())
            {
                xiCol = xi.toColVector();// xi(:);
                pmessage.print("BLOCK 11");
            }
            else
            {
                xiCol = xi;
                pmessage.print("BLOCK 12");
            }
            // The size of the output YI
            if (y.isVector())
            {
                // Y is a vector so size(YI) == size(XI)
                siz_yi = siz_xi;
                pmessage.print("BLOCK 13");
            }
            else
            {
                if (xi.isVector())
                {
                    // Y is not a vector but XI is
                    siz_yi = new Indices(new int[]
                    {
                            xi.length(), ds
                    });// [length(xi) ds];
                    pmessage.print("BLOCK 14");
                }
                else
                {// ######################################################
                 // Both Y and XI are non-vectors
                    siz_yi = new Indices(new int[]
                    {
                            siz_xi.start(), siz_xi.end(), ds
                    });// [siz_xi ds];
                    pmessage.print("BLOCK 15");
                }// #############################################################
            }// end
            pmessage.print("BLOCK 16");
        }// end

        // Error check for NaN values in X and Y
        // check for NaN's
        if (xOffset > -1 && xCol.isnan().anyBoolean())
        {
            // error('MATLAB:interp1:NaNinX','NaN is not an appropriate value
            // for X.');
            throw new ConditionalException("Interp1 : NaN is not an appropriate value for X.");
        }// end

        // NANS are allowed as a value for F(X), since a function may be
        // undefined
        // for a given value.
        if (yMat.isnan().anyBoolean())
        {// any(isnan(yMat(:)))
         // warning('MATLAB:interp1:NaNinY', ...
         // ['NaN found in Y, interpolation at undefined values \n\t',...
         // ' will result in undefined values.']);
            System.out
                    .println("Interp1 : NaN found in Y, interpolation at undefined values \n and will result in undefined values.");
        }// end

        Matrix yi = null;
        if (n < 2)
        {
            if (ppOutput || xi != null)
            {
                // error('MATLAB:interp1:NotEnoughPts', ...
                // 'There should be at least two data points.')
                throw new ConditionalException("Interp1 : There should be at least two data points.");
            }
            else
            {
                // yi = zeros(siz_yi,superiorfloat(x,y,xi));
                yi = Matrix.zeros(siz_yi.getRowPackedCopy());
                // varargout{1} = yi;
                varargout.add(yi);
                pmessage.print("BLOCK 17");
                return;
            }// end
        }// end

        // Process METHOD in
        // PP = INTERP1(X,Y,METHOD,'pp')
        // YI = INTERP1(Y,XI,METHOD,...)
        // YI = INTERP1(X,Y,XI,METHOD,...)
        // including explicit specification of the default by an empty input.
        InterpMethod method = null;
        if (ppOutput)
        {
            if (varargin[2] == null)
            {
                method = InterpMethod.LINEAR;// 'linear';
                pmessage.print("BLOCK 18");
            }
            else
            {
                method = (InterpMethod) varargin[2];
                pmessage.print("BLOCK 19");
            }// end
        }
        else
        {
            // 1 is added to xOffset as in (xOffset + 1) because xOffset starts
            // at zero (java index) while matlab starts at 1
            cond = (nargin >= (3 + (xOffset + 1)))
                    && ((varargin[3 + xOffset] != null) && (varargin[3 + xOffset] instanceof InterpMethod));
            if (cond)
            {// nargin >= 3+xOffset && ~isempty(varargin{3+xOffset})
                method = (InterpMethod) varargin[3 + xOffset];// varargin{3+xOffset};
                pmessage.print("BLOCK 20");
            }
            else
            {
                method = InterpMethod.LINEAR;// 'linear';
                pmessage.print("BLOCK 21");
            }// end
        }// end

        // The v5 option, '*method', asserts that x is equally spaced.
        boolean eqsp = false;

        if (method != null)
        {
            eqsp = method.toString().charAt(0) == '*';// (method(1) == '*');
            pmessage.print("BLOCK 22");
        }

        if (eqsp)
        {
            // method(1) = [];
            throw new ConditionalException("Interp1 : Shouldn't have got here.");
        }// end

        // INTERP1([X,]Y,XI,METHOD,'extrap') and
        // INTERP1([X,]Y,Xi,METHOD,EXTRAPVAL)
        InterpFlag extrapval = null;
        if (!ppOutput)
        {
            // 1 is added to xOffset as in (xOffset + 1) because xOffset starts
            // at zero (java index) while matlab starts at 1
            if (nargin >= (4 + xOffset + 1))
            {
                extrapval = (InterpFlag) varargin[4 + xOffset];
                pmessage.print("BLOCK 23");
            }
            else
            {
                switch (method)
                {
                case SPLINE:
                case PCHIP:
                case CUBIC:
                {
                    extrapval = InterpFlag.Extrap;
                    pmessage.print("BLOCK 24");
                    break;
                }
                default:
                {
                    extrapval = null;
                    pmessage.print("BLOCK 25");
                }
                }
            }// end
        }// end

        // Start the algorithm
        // We now have column vector xCol, column vector or 2D matrix yMat and
        // column vector xiCol.
        Matrix h = null;
        double val = 0.0;
        if (xOffset > -1)
        {
            if (!eqsp)
            {
                h = JDatafun.diff(xCol);
                double numEps = MathUtil.EPS;// eps(norm(xCol,Inf))
                eqsp = JDatafun.diff(h).normInf() <= numEps;// (norm(diff(h),Inf)
                                                            // <=
                                                            // eps(norm(xCol,Inf)));
                if (xCol.isNotFinite())
                {// any(~isfinite(xCol))
                    eqsp = false; // if an INF in x, x is not equally spaced
                    pmessage.print("BLOCK 26");
                }// end
                pmessage.print("BLOCK 27");
            }// end
            if (eqsp)
            {
                double nmin1 = (double) (n - 1);
                val = xCol.getElementAt(n - 1) - xCol.start();
                val = val / nmin1;
                // h = (xCol(n)-xCol(1))/(n-1);
                h = new Matrix(1, 1, val);
                pmessage.print("BLOCK 28");
            }// end
        }
        else
        {
            h = new Matrix(1, 1, 1.0);
            eqsp = true;
            pmessage.print("BLOCK 29");
        }// end

        if (h.LT(0.0).anyBoolean())
        {// any(h < 0)
         // [xCol,p] = sort(xCol);
            QuickSort sort = new QuickSortMat(xCol, Dimension.ROW, true);// new
                                                                         // QuickSortMat(xCol);
            xCol = (Matrix) sort.getSortedObject();
            // yMat = yMat(p,:);
            int[] pInd = sort.getIndices().getRowPackedCopy();
            yMat = yMat.getRows(pInd);
            if (eqsp)
            {
                h = h.uminus();
                pmessage.print("BLOCK 30");
            }
            else
            {
                h = JDatafun.diff(xCol);
                pmessage.print("BLOCK 31");
            }// end
        }// end
        if (h.EQ(0.0).anyBoolean())
        {// any(h == 0)
         // error('MATLAB:interp1:RepeatedValuesX', ...
         // 'The values of X should be distinct.');
            throw new ConditionalException("Interp1 : The values of X should be distinct.");
        }// end

        // PP = INTERP1(X,Y,METHOD,'pp')
        cond = nargin == 4;
        cond = cond && (varargin[2] != null && (varargin[2] instanceof InterpMethod));
        cond = cond
                && (varargin[3] != null && (varargin[3] instanceof InterpFlag) && ((InterpFlag) varargin[3] == InterpFlag.PP));
        if (cond)
        {// nargin==4 && ischar(varargin{3}) && isequal('pp',varargin{4})
         // obtain pp form of output
            Object pp = ppinterp();
            // varargout{1} = pp;
            varargout.add(pp);
            pmessage.print("BLOCK 32");
            return;
        }// end

        // Interpolate
        int numelXi = xiCol.length();
        Indices p = null;

        Matrix yiMat = null;
        Indices k = null;
        switch (method)
        {
        // NEAREST ,LINEAR , SPLINE , PCHIP , CUBIC , V5CUBIC
        case SPLINE:
        {
            // spline is oriented opposite to interp1
            // yiMat = spline(xCol.',yMat.',xiCol.').';
            Spline spl = new Spline(xCol.transpose(), yMat.transpose());
            yiMat = spl.evaluate(xiCol.transpose());
            yiMat = yiMat.transpose();
            pmessage.print("BLOCK 33");
            break;
        }
        case CUBIC:
        case PCHIP:
        {
            // pchip is oriented opposite to interp1
            // yiMat = pchip(xCol.',yMat.',xiCol.').';
            yiMat = new Pchip(xCol.transpose(), yMat.transpose(), xiCol.transpose()).getInterpVals();
            pmessage.print("BLOCK 34");
            throw new IllegalArgumentException("Interp1 : Method \"pchip\" not yet implemented.");
            // break;
        }
        default:
        {
            yiMat = Matrix.zeros(numelXi, prodDs);// ,superiorfloat(xCol,yMat,xiCol));
            if (!eqsp && JDatafun.diff(xiCol).LT(0.0).anyBoolean())
            {// ~eqsp && any(diff(xiCol) < 0)
             // [xiCol,p] = sort(xiCol);
                QuickSort sort = new QuickSortMat(xiCol, Dimension.ROW, true); // QuickSortMat(Matrix
                                                                               // A,
                                                                               // Dimension
                                                                               // dim,
                                                                               // boolean
                                                                               // computeSortedIndex)
                xiCol = (Matrix) sort.getSortedObject();
                p = sort.getIndices();
                pmessage.print("BLOCK 35");
            }
            else
            {
                p = Indices.linspace(0, numelXi - 1);// 1:numelXi;
                pmessage.print("BLOCK 36");
            }// end

            // p.printInLabel("p");
            // Find indices of subintervals, x(k) <= u < x(k+1),
            // or u < x(1) or u >= x(m-1).

            if (xiCol == null)
            {
                k = xiCol.toIndices();
                pmessage.print("BLOCK 37");
            }
            else if (eqsp)
            {
                Indices minmax = JElfun.floor(xiCol.minus(xCol.start()).arrayRightDivide(h.start())).plus(1.0)
                        .toIndices();
                minmax = JDatafun.max(minmax, 1);
                minmax = JDatafun.min(minmax, n - 1);
                // Subtract 1 as shown in the statement below, since Java index
                // starts at zero.
                k = minmax.minus(1);// min(max(1+floor((xiCol-xCol(1))/h),1),n-1);
                pmessage.print("BLOCK 38");
            }
            else
            {
                // [ignore,k] = histc(xiCol,xCol);
                HistogramCount histc = new HistogramCount(xiCol, xCol);
                k = histc.getBinIndices();
                // k(xiCol<xCol(1) | ~isfinite(xiCol)) = 1;
                Indices kFind = xiCol.LT(xCol.start()).OR(xiCol.isfinite().NOT()).find();
                if (kFind != null)
                {
                    k.setFromFind(kFind, 0);
                    pmessage.print("BLOCK 39");
                }
                // k(xiCol>=xCol(n)) = n-1;
                kFind = xiCol.GTEQ(xCol.getElementAt(n - 1)).find();
                if (kFind != null)
                {
                    k.setFromFind(kFind, n - 2);
                    pmessage.print("BLOCK 40");
                }
                pmessage.print("BLOCK 41");
            }// end

            int[] kArr = k.getRowPackedCopy();
            int[] kArrP1 = k.plus(1).getRowPackedCopy();
            Indices Itemp = null;
            Matrix s = null;

            if (method == InterpMethod.NEAREST)
            {
                // i = find(xiCol >= (xCol(k)+xCol(k+1))/2);
                Matrix xC = xCol.getElements(kArr).plus(xCol.getElements(kArrP1)).arrayRightDivide(2.0);
                Itemp = xiCol.GTEQ(xC).find();
                if (Itemp != null)
                {
                    // k(i) = k(i)+1;
                    k.setFromFind(Itemp, k.getFromFind(Itemp).plus(1));
                    pmessage.print("BLOCK 42");
                }
                kArr = k.getRowPackedCopy();
                // yiMat(p,:) = yMat(k,:);
                Matrix yMTm = yMat.getRows(kArr);
                yiMat.setRows(p.getRowPackedCopy(), yMTm);
                pmessage.print("BLOCK 43");

            }
            else if (method == InterpMethod.LINEAR)
            {

                if (eqsp)
                {
                    // s = (xiCol - xCol(k))/h;
                    s = xiCol.minus(xCol.getElements(kArr)).arrayRightDivide(h.start());
                    pmessage.print("BLOCK 44");
                }
                else
                {
                    // s = (xiCol - xCol(k))./h(k);
                    s = xiCol.minus(xCol.getElements(kArr)).arrayRightDivide(h.getElements(kArr));
                    pmessage.print("BLOCK 45");
                }// end
                Matrix yMatkj = null;
                Matrix yMatkp1j = null;
                Matrix yMatTmp = null;
                for (int j = 0; j < prodDs; j++)
                {// j = 1:prodDs
                 // yiMat(p,j) = yMat(k,j) + s.*(yMat(k+1,j)-yMat(k,j));
                 // System.out.println(" j = " + j);
                    yMatkj = yMat.getMatrix(kArr, j);
                    yMatkp1j = yMat.getMatrix(kArrP1, j);
                    yMatTmp = yMatkj.plus(s.arrayTimes(yMatkp1j.minus(yMatkj)));
                    yiMat.setMatrix(p.getRowPackedCopy(), j, yMatTmp);
                }// end
                pmessage.print("BLOCK 46");
            }
            else if (method == InterpMethod.V5CUBIC)
            {
                extrapval = null;

                if (eqsp)
                {
                    // Data are equally spaced
                    s = xiCol.minus(xCol.getElements(kArr)).arrayRightDivide(h.start());// (xiCol
                                                                                        // -
                                                                                        // xCol(k))/h;
                    Matrix s2 = s.arrayTimes(s);// s.*s;
                    Matrix s3 = s.arrayTimes(s2);// s.*s2;

                    // Add extra points for first and last interval
                    Matrix yR1 = yMat.getRowAt(0).arrayTimes(3.0);
                    yR1 = yR1.minus(yMat.getRowAt(1).arrayTimes(3.0));
                    yR1 = yR1.plus(yMat.getRowAt(2));
                    // yMat = [3*yMat(1,:)-3*yMat(2,:)+yMat(3,:); ...
                    // yMat; ...
                    // 3*yMat(n,:)-3*yMat(n-1,:)+yMat(n-2,:)];
                    Matrix yR2 = yMat.getRowAt(n - 1).arrayTimes(3.0);
                    yR2 = yR2.minus(yMat.getRowAt(n - 2).arrayTimes(3.0));
                    yR2 = yR2.plus(yMat.getRowAt(n - 3));

                    yMat = yR1.mergeVerti(yMat, yR2);

                    for (int j = 0; j < prodDs; j++)
                    {
                        /*
                         * yiMat(p,j) = (yMat(k,j).*(-s3+2*s2-s) + ...
                         * yMat(k+1,j).*(3*s3-5*s2+2) + ...
                         * yMat(k+2,j).*(-3*s3+4*s2+s) + ...
                         * yMat(k+3,j).*(s3-s2))/2;
                         */
                        Matrix sSum = s3.plus(s).uminus().plus(s2.arrayTimes(2.0)); // -s3+2*s2-s
                        Matrix forMat1 = yMat.getMatrix(kArr, j).arrayTimes(sSum);// (yMat(k,j).*(-s3+2*s2-s)
                        sSum = s3.arrayTimes(3.0).minus(s2.arrayTimes(5.0)).plus(2.0);// 3*s3-5*s2+2
                        Matrix forMat2 = yMat.getMatrix(kArrP1, j).arrayTimes(sSum);// yMat(k+1,j).*(3*s3-5*s2+2)
                        sSum = s3.arrayTimes(-3.0).plus(s2.arrayTimes(4.0)).plus(s);// -3*s3
                                                                                    // +
                                                                                    // 4*s2
                                                                                    // +
                                                                                    // s
                        int[] kArrP2 = k.plus(2).getRowPackedCopy();
                        Matrix forMat3 = yMat.getMatrix(kArrP2, j).arrayTimes(sSum);// yMat(k+2,j).*(-3*s3+4*s2+s)
                        sSum = s3.minus(s2);
                        int[] kArrP3 = k.plus(3).getRowPackedCopy();
                        Matrix forMat4 = yMat.getMatrix(kArrP3, j).arrayTimes(sSum);// yMat(k+3,j).*(s3-s2)
                        Matrix yiMatTmp = forMat1.plus(forMat2).plus(forMat3).plus(forMat4).arrayRightDivide(2.0);
                        yiMat.setMatrix(p.getRowPackedCopy(), j, yiMatTmp);
                    }// end for
                    pmessage.print("BLOCK 47");
                }
                else
                {
                    // Data are not equally spaced
                    // spline is oriented opposite to interp1
                    // yiMat = spline(xCol.',yMat.',xiCol.').';
                    Spline spl = new Spline(xCol.transpose(), yMat.transpose());
                    yiMat = spl.evaluate(xiCol.transpose());
                    yiMat = yiMat.transpose();
                    pmessage.print("BLOCK 48");
                }// end
                 // //////////////////////////////////////////////

            }
            else
            {
                throw new ConditionalException("Interp1 : Unsupported interpolation method.");
            }
        }// end default
        }// end switch

        // Override extrapolation
        if (extrapval != null && extrapval != InterpFlag.Extrap)
        {// ~isequal(extrapval,'extrap')
            /*
             * if ischar(extrapval) error('MATLAB:interp1:InvalidExtrap',
             * 'Invalid extrap option.') elseif ~isscalar(extrapval)
             * error('MATLAB:interp1:NonScalarExtrapValue',... 'EXTRAP option
             * must be a scalar.') end
             */
            if (p == null)
            {
                // p = 1 : numelXi;
                p = Indices.linspace(0, numelXi - 1);// 1:numelXi;
                pmessage.print("BLOCK 49");
            }// end
             // outOfBounds = xiCol<xCol(1) | xiCol>xCol(n);
             // yiMat(p(outOfBounds),:) = extrapval;
            pmessage.print("BLOCK 50");
            throw new ConditionalException("Interp1 : This block is yet to be completed.");
        }// end
        else if (extrapval == null)
        {
            if (p == null)
            {
                // p = 1 : numelXi;
                p = Indices.linspace(0, numelXi - 1);// 1:numelXi;
                pmessage.print("BLOCK 51");
            }// end
            Indices outOfBounds = xiCol.LT(xCol.start()).OR(xiCol.GT(xCol.getElementAt(n - 1))).find();// <xCol(1)
                                                                                                       // |
                                                                                                       // xiCol>xCol(n);
            // yiMat(p(outOfBounds),:) = extrapval;
            if (outOfBounds != null)
            {
                int[] pInd = p.getFromFind(outOfBounds).getRowPackedCopy();
                yiMat.setRows(pInd, Double.NaN);
                pmessage.print("BLOCK 52");
            }
            pmessage.print("BLOCK 53");
        }

        // Reshape result, possibly to an ND array
        yi = yiMat.reshape(siz_yi.start(), siz_yi.end());// reshape(yiMat,siz_yi);
        varargout.add(yi);
        pmessage.print("BLOCK 54");
    }// ---------------------- end constructor -----------------------------

    public Matrix getInterpValues()
    {
        return (Matrix) varargout.get(0);
    }

    private boolean checkIfArgsElsNull(Object... varargin)
    {
        if (varargin == null)
        {
            return true;
        }
        int len = varargin.length;
        int count = 0;
        for (int i = 0; i < len; i++)
        {
            if (varargin[i] == null)
            {
                count++;
            }
        }
        if (len == count)
        {
            return true;
        }
        return false;
    }

    private Object ppinterp()
    {
        /*
         * switch method(1) case 'n' % nearest breaks = [xCol(1); ...
         * (xCol(1:end-1)+xCol(2:end))/2; ... xCol(end)].'; coefs = yMat.'; pp =
         * mkpp(breaks,coefs,ds); case 'l' % linear breaks = xCol.'; page1 =
         * (diff(yMat)./repmat(diff(xCol),[1, prodDs])).'; page2 =
         * (reshape(yMat(1:end-1,:),[n-1, prodDs])).'; coefs =
         * cat(3,page1,page2); pp = mkpp(breaks,coefs,ds); case {'p', 'c'} %
         * pchip and cubic pp = pchip(xCol.',reshape(yMat.',[ds, n])); case 's'
         * % spline pp = spline(xCol.',reshape(yMat.',[ds, n])); case 'v' %
         * v5cubic b = diff(xCol); if norm(diff(b),Inf) <= eps(norm(xCol,Inf)) %
         * data are equally spaced a = repmat(b,[1 prodDs]).'; yReorg =
         * [3*yMat(1,:)-3*yMat(2,:)+yMat(3,:); ... yMat; ...
         * 3*yMat(n,:)-3*yMat(n-1,:)+yMat(n-2,:)]; y1 = yReorg(1:end-3,:).'; y2
         * = yReorg(2:end-2,:).'; y3 = yReorg(3:end-1,:).'; y4 =
         * yReorg(4:end,:).'; breaks = xCol.'; page1 =
         * (-y1+3*y2-3*y3+y4)./(2*a.^3); page2 = (2*y1-5*y2+4*y3-y4)./(2*a.^2);
         * page3 = (-y1+y3)./(2*a); page4 = y2; coefs =
         * cat(3,page1,page2,page3,page4); pp = mkpp(breaks,coefs,ds); else %
         * data are not equally spaced pp = spline(xCol.',reshape(yMat.',[ds,
         * n])); end otherwise error('MATLAB:interp1:ppinterp:UnknownMethod',
         * ... 'Unrecognized method.'); end
         * 
         * % Even if method is 'spline' or 'pchip', we still need to record that
         * the % input data Y was oriented according to INTERP1's rules. % Thus
         * PPVAL will return YI oriented according to INTERP1's rules and % YI =
         * INTERP1(X,Y,XI,METHOD) will be the same as % YI =
         * PPVAL(INTERP1(X,Y,METHOD,'pp'),XI) pp.orient = 'first';
         */
        boolean impl = false;
        if (!impl)
        {
            throw new ConditionalException("ppinterp : Not implemented yet.");
        }
        return null;
    }

    public static void main(String[] args)
    {
        // InterpMethod method = InterpMethod.LINEAR;
        // System.out.println("method = " + method.toString());
        double[] xArr =
        {
                1, 1.5, 3
        };
        Matrix X = new Matrix(xArr);// Matrix.linIncrement(1.0, 3.0,
                                    // 1.0).transpose();//(1:3)';
        double[][] fDb =
        {
                {
                        0.11765, 0.08825, 0.06865
                },
                {
                        0.09765, 0.10825, 0.09865
                }
        };
        Matrix F = new Matrix(fDb).transpose();// new Matrix(new
                                               // double[]{0.11765, 0.08825,
                                               // 0.06865}).transpose();
        double[] xtiDb =
        {
                1.661202185792350, 0.659340659340659, 2.659340659340660
        };// {0.659340659340659, 1.661202185792350, 2.659340659340660};
        Matrix XTI = new Matrix(xtiDb).transpose();

        Interp1 ITP = new Interp1(X, F, XTI, null, InterpFlag.Extrap);
        Matrix YTI = ITP.getInterpValues();
        YTI.printInLabel("YTI");

    }
}
