/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.unsupervised.subspace.ica.efica;

import jamaextension.jamax.EigenvalueDecomposition;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;

/**
 * 
 * @author Feynman Perceptrons
 */
public class BlockEfficientFastICA
{

    // X, num_seg, ini, SaddleTest, Uniform, Identity
    private Matrix X;
    private int numSegment = 1;
    private Matrix ini = null;
    private boolean saddleTest = true;
    private boolean uniform = false;
    private boolean identity = true;
    private ConstrastCondition g = ConstrastCondition.rat1;
    private boolean built = false;
    private int numBasis = 2;

    public BlockEfficientFastICA(Matrix data)
    {
        if (data == null)
        {
            throw new IllegalArgumentException("EffFastICA : Parameter \"data\" must be non-null.");
        }
        if (data.isVector())
        {
            throw new IllegalArgumentException("EffFastICA : Parameter \"data\" must be a matrix and not a vector.");
        }
        int m = data.getRowDimension();
        int n = data.getColumnDimension();
        if (n > m)
        {
            throw new IllegalArgumentException("EffFastICA : Parameter \"data\" must be a wide rectangular matrix.");
        }
        this.X = data;
    }

    public void build()
    {
        int dim = X.getRowDimension();
        int N = X.getColumnDimension();
        if (ini == null)
        {
            ini = Matrix.randn(dim);
        }

        // String g = "rat1"; //Contrast function used for the symmetric part of
        // Extended EFICA (EEF1)
        double epsilon = 0.0001; // Stop criterion
        double fineepsilon = 1e-7; // Stop criterion for finetunings (EEF2)
        // double[][] r2d = {{1.0 / Math.SQR(2.0), 1.0 / Math.SQR(2.0)}, {-1.0 /
        // Math.SQR(2), 1.0 / Math.SQR(2)}};
        // Matrix rot2d = new Matrix(r2d);
        int MaxIt = 100; // Maximum number of iterations for symmetric part of
                         // Extended EFICA (EEF1)
        int MaxItAfterSaddleTest = 30;// Maximum number of iterations after a
                                      // saddle point was indicated
        int FinetuneMaxIt = 50; // Maximum number of finetuning iterations
                                // (EEF2)
        double min_correlation = 0.9; // additive noise...0.75, noise free...
                                      // 0.95, turn off (unstable)...0
        // String test_of_saddle_points_nonln = "rat2"; //nonlinearity used for
        // the test of saddle points
        ConstrastCondition test_of_saddle_points_nonln = ConstrastCondition.rat2;

        if (identity)
        {
            numBasis = 3; // Number of basis functions considered in the score
                          // function estimator
        }

        int seg_len = (int) MathUtil.fix((double) N / (double) numSegment); // The
                                                                            // length
                                                                            // of
                                                                            // segments
        boolean PerfEst = true; // If true, perform score function estimation in
                                // every iteration (EEF2)
        int minseglen = 100; // Minimal allowed segment length
        int extraseg = (int) MathUtil.rem(N, numSegment);// The length of the
                                                         // last segment

        if ((N / seg_len) < minseglen)
        {
            // error('Input data are too short for selected number of
            // segments!');
            throw new IllegalArgumentException(
                    "build : Parameter \"data\" are too short for selected number of segments.");
        }

        // fprintf('Starting Block EFICA, dim=%d, N=%d, M=%d, ',dim,N,num_seg);
        System.out.println("Starting Block EFICA, dim = " + dim + ", N = " + N + ", M = " + numSegment);

        Matrix W = symdecor(ini);
        int NumIt = 0;
        int TotalIt = 0;
        Matrix crit = Matrix.zeros(1, dim);
        int repeat = 1; // Further interations - symmetric part
        // boolean repeat2 = false;

        // Removing mean of data
        Matrix Xmean = JDatafun.mean(X, Dimension.COL); // mean(X,2);
        Matrix XmeanTimesOnes = Xmean.times(Matrix.ones(1, N));
        X = X.minus(XmeanTimesOnes);// X-Xmean*ones(1,N);

        // Preprocessing of data
        Matrix C = JDatafun.cov(X.transpose());
        Matrix CC = JElfun.pow(C, -0.5);// C^(-1/2);
        Matrix Z = CC.times(X); // Preprocessed data

        // Symmetrix FastICA with the Test of saddle points
        Matrix G = Matrix.zeros(dim, N);
        Matrix EGS = Matrix.zeros(dim, numSegment);
        Matrix Wold = null;

        int num_seg = numSegment;
        double val = 0.0;

        while (repeat != 0)
        {// ///////////////////////////////////////////////////////////

            Matrix ex = null;

            while ((1.0 - JDatafun.min(crit).start()) > epsilon && NumIt < MaxIt)
            {// $$$$$$$$$$$$$$$$$$$$$$$
                NumIt = NumIt + 1;
                Wold = W.copy();
                Matrix temp = null;
                Matrix temp2 = null;
                switch (g)
                {
                case estm:
                {
                    Matrix U = W.transpose().times(Z);
                    for (int j = 0; j < dim; j++)
                    {
                        Matrix[] se = scoreEstim(U.getRowAt(j), num_seg, true, Matrix.zeros(numBasis, num_seg),
                                this.identity);
                        // [EGS(j,:)
                        // G(j,:)]=scoreEstim(U(j,:),num_seg,true,zeros(numBasis,num_seg),identity);
                        EGS.setRowAt(j, se[0]);// (j,:) G(j,:)
                        G.setRowAt(j, se[1]);
                    }
                    temp = Z.times(G.transpose()).arrayRightDivide(N);
                    temp2 = Matrix.ones(dim, 1).times(JDatafun.mean(EGS, Dimension.COL).transpose()).arrayTimes(W);
                    W = temp.minus(temp2);// Z*G'/N-ones(dim,1)*mean(EGS,2)'.*W;
                    break;
                }
                case tanh:
                {
                    Matrix hypTan = JElfun.tanh(Z.transpose().times(W));
                    temp = Z.times(hypTan).arrayRightDivide(N);
                    temp2 = Matrix.ones(dim, 1).times(JDatafun.sum(JElfun.pow(hypTan, 2.0).uminus().plus(1.0)))
                            .arrayTimes(W).arrayRightDivide(N);
                    W = temp.minus(temp2);// Z*hypTan/N-ones(dim,1)*sum(1-hypTan.^2).*W/N;
                    break;
                }
                case pow3:
                {
                    temp = JElfun.pow(Z.transpose().times(W), 3.0);
                    temp = Z.times(temp).arrayRightDivide(N);
                    temp2 = W.arrayTimes(3.0);
                    W = temp.minus(temp2);// (Z*((Z'*W).^ 3))/N-3*W;
                    break;
                }
                case rat1:
                {
                    Matrix U = Z.transpose().times(W);
                    Matrix Usquared = JElfun.pow(U, 2.0);
                    Matrix RR = Usquared.plus(4.0).reciprocate(4.0);// 4./(4+Usquared);
                    Matrix Rati = U.arrayTimes(RR);
                    Matrix Rati2 = JElfun.pow(Rati, 2.0);
                    Matrix dRati = RR.minus(Rati2.arrayRightDivide(2.0));
                    Matrix nu = JDatafun.mean(dRati);
                    Matrix hlp = Z.times(Rati).arrayRightDivide(N);
                    temp = Matrix.ones(dim, 1).times(nu).arrayTimes(W);
                    W = hlp.minus(temp);// hlp-ones(dim,1)*nu.*W;
                    break;
                }
                case rat2:
                {
                    Matrix U = Z.transpose().times(W);
                    Matrix Ua = JDatafun.sign(U).arrayTimes(U).plus(1.0);
                    Matrix r1 = U.arrayRightDivide(Ua);
                    Matrix r2 = r1.arrayTimes(JDatafun.sign(r1));
                    Matrix Rati = r1.arrayTimes(r2.uminus().plus(2.0));

                    temp = r2.uminus().arrayTimes(r2.uminus().plus(2.0)).plus(1.0);
                    Matrix dRati = Ua.reciprocate(2.0).arrayTimes(temp);// (2./Ua).*(1-r2.*(2-r2));
                    Matrix nu = JDatafun.mean(dRati);
                    Matrix hlp = Z.times(Rati).arrayRightDivide(N);// Z*Rati/N;
                    temp = Matrix.ones(dim, 1).times(nu).arrayTimes(W);
                    W = hlp.minus(temp);// hlp-ones(dim,1)*nu.*W;
                    break;
                }
                case gaus:
                {
                    Matrix U = Z.transpose().times(W);// Z'*W;
                    Matrix Usquared = JElfun.pow(U, 2.0);// U.^2;
                    ex = JElfun.exp(Usquared.uminus().arrayRightDivide(-2.0));
                    Matrix gauss = U.arrayTimes(ex);
                    Matrix dGauss = Usquared.uminus().plus(1.0).arrayTimes(ex);
                    temp = Matrix.ones(dim, 1).times(JDatafun.sum(dGauss)).arrayTimes(W).arrayRightDivide(N);
                    W = Z.times(gauss).arrayRightDivide(N).minus(temp);// Z*gauss/N
                                                                       // -
                                                                       // ones(dim,1)*sum(dGauss).*W/N;
                    break;
                }
                default:
                    throw new IllegalArgumentException("build : Unknown \"ConstrastCondition\" object found.");
                }

                TotalIt = TotalIt + dim;
                W = symdecor(W);
                crit = JDatafun.sum(W.arrayTimes(Wold)).abs();
            }// % while iteration
             // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

            if (repeat == 1)
            {
                // fprintf('Iterations: %d\n',NumIt);
                System.out.println("Iterations: " + NumIt + "\n");
            }
            else if (repeat == 2)
            {
                System.out.println("Test of saddle points positive: " + NumIt + "  iterations.\n");
            }

            repeat = 0; // Do not repeat the Test of saddle points anymore

            // The Test of saddle points of the separated components
            if (saddleTest)
            {
                saddleTest = false; // The saddleTest may be done only one times
                Matrix u = Z.transpose().times(W);// '*W;
                Matrix table1 = null;
                Matrix temp = null;
                switch (test_of_saddle_points_nonln)
                {
                case tanh:
                {
                    temp = JDatafun.mean(JElfun.log(JElfun.cosh(u))).minus(0.37456);
                    table1 = JElfun.pow(temp, 2.0);
                    // table1 = (mean(log(cosh(u)))-0.37456).^2;
                    break;
                }
                case gaus:
                {
                    // table1=(mean(ex)-1/sqrt(2)).^2;
                    temp = JDatafun.mean(ex).minus(1.0 / Math.sqrt(2.0));
                    table1 = JElfun.pow(temp, 2.0);
                    break;
                }
                case rat1:
                {
                    // table1=(mean(2*log(4+u.^2))-3.1601).^2;
                    temp = JElfun.pow(u, 2.0).plus(4.0);
                    temp = JElfun.log(temp).arrayTimes(2.0);
                    temp = JDatafun.mean(temp).minus(3.1601);
                    table1 = JElfun.pow(temp, 2.0);
                    break;
                }
                case rat2:
                {
                    // table1=(mean(u.^2./(1+sign(u).*u))-0.4125).^2;
                    temp = JElfun.pow(u, 2.0);
                    temp = temp.arrayRightDivide(u.sign().arrayTimes(u).plus(1.0));
                    temp = JDatafun.mean(temp).minus(0.4125);// mean(u.^2./(1+abs(u)));
                    table1 = JElfun.pow(temp, 2.0);
                    break;
                }
                case pow3:
                {
                    // table1=(mean((pwr(u,4)))-3).^2;
                    temp = JDatafun.mean(pwr(u, 4));
                    temp = temp.minus(3.0);
                    table1 = JElfun.pow(temp, 2.0);
                    break;
                }
                /*
                 * case estm: { table1 = new Matrix(1, dim); for (int i = 0; i <
                 * dim; i++) { //table1(i)=(LogLike(u(:,i)',num_seg)-1/2)^2; val
                 * = LogLike(u.getColumnAt(i).transpose(), numSegment) - 0.5;
                 * val = val * val; table1.set(0, i, val); } break; }
                 */
                default:
                    throw new IllegalArgumentException("build : Unknown \"ConstrastCondition\" object found.");
                }

                // /////////////////////////////////////////////////////////////////
                // applying the round-Robin tournament scheme for parallel
                // processing
                int dimhalf = (int) Math.floor(((double) dim + 1.0) / 2.0);
                int dim2 = 2 * dimhalf;
                Indices da = Indices.linspace(1, dim2);// [1:dim2 2:dim2]; //%%
                                                       // auxiliary array
                da = da.mergeH(Indices.linspace(2, dim2));
                int[] arr = null;
                Indices ii = null;
                Indices jj = null;

                for (int delay = 0; delay <= (dim2 - 2); delay++)
                {// delay = 0:dim2-2

                    int Lo = dim2 - delay + 1;
                    int Up = 3 * dimhalf - delay - 1;
                    arr = Indices.linspace(Lo, Up).getRowPackedCopy();
                    ii = new Indices(1, 1, 1).mergeH(da.getEls(arr).toRowVector());
                    // ii=[1 da(dim2-delay+1:3*dimhalf-delay-1)];
                    Up = 4 * dimhalf - delay - 1;
                    Lo = 3 * dimhalf - delay;
                    // jj=da(4*dimhalf-delay-1:-1:3*dimhalf-delay);
                    arr = Indices.linDecrement(Up, Lo, -1).getRowPackedCopy();
                    jj = da.getEls(arr).toRowVector();

                    if (dim2 > dim)
                    {
                        int i0 = dimhalf - Math.abs(delay - dimhalf + 1 / 2) + 1 / 2;
                        // the pair containing index dim2 must be deleted
                        ii = ii.removeElAt(i0);// ii(i0)=[];
                        jj = jj.removeElAt(i0);// jj(i0)=[];
                    }// end
                    temp = table1.getEls(ii.getRowPackedCopy());// table1(ii)+table1(jj);
                    Matrix temp2 = table1.getEls(jj.getRowPackedCopy());
                    Matrix ctrl0 = temp.plus(temp2);
                    double SQR = Math.sqrt(2.0);
                    temp = u.getColumns(ii.getRowPackedCopy());
                    temp2 = u.getColumns(jj.getRowPackedCopy());
                    Matrix z1 = temp.plus(temp2).arrayRightDivide(SQR);// (u(:,ii)+u(:,jj))/SQR(2);
                    Matrix z2 = temp.minus(temp2).arrayRightDivide(SQR);// (u(:,ii)-u(:,jj))/SQR(2);

                    Matrix ctrl = null;
                    switch (test_of_saddle_points_nonln)
                    {
                    case tanh:
                    {
                        // ctrl=(mean(log(cosh(z1)))-0.37456).^2
                        // +(mean(log(cosh(z2)))-0.37456).^2;
                        temp = JElfun.cosh(z1);
                        temp = JElfun.log(temp);
                        temp = JDatafun.mean(temp).minus(0.37456);
                        temp = JElfun.pow(temp, 2.0);

                        temp2 = JElfun.cosh(z2);
                        temp2 = JElfun.log(temp2);
                        temp2 = JDatafun.mean(temp2).minus(0.37456);
                        temp2 = JElfun.pow(temp2, 2.0);

                        ctrl = temp.plus(temp2);
                        break;
                    }
                    case gaus:
                    {
                        // ctrl=(mean(exp(-z1.^2/2)-1/sqrt(2))).^2
                        // +(mean(exp(-z2.^2/2)-1/sqrt(2))).^2;
                        temp = JElfun.pow(z1, 2.0).uminus().arrayRightDivide(2.0);
                        temp = JElfun.exp(temp).minus(1.0 / SQR);
                        temp = JDatafun.mean(temp);
                        temp = JElfun.pow(temp, 2.0);

                        temp2 = JElfun.pow(z2, 2.0).uminus().arrayRightDivide(2.0);
                        temp2 = JElfun.exp(temp2).minus(1.0 / SQR);
                        temp2 = JDatafun.mean(temp2);
                        temp2 = JElfun.pow(temp2, 2.0);

                        ctrl = temp.plus(temp2);
                        break;
                    }
                    case rat1:
                    {
                        // ctrl=(mean(2*log(4+z1.^2))-3.1601).^2
                        // +(mean(2*log(4+z2.^2))-3.1601).^2;
                        temp = JElfun.pow(z1, 2.0).plus(4.0);
                        temp = JElfun.log(temp).arrayTimes(2.0);
                        temp = JDatafun.mean(temp).minus(3.1601);
                        temp = JElfun.pow(temp, 2.0);

                        temp2 = JElfun.pow(z2, 2.0).plus(4.0);
                        temp2 = JElfun.log(temp2).arrayTimes(2.0);
                        temp2 = JDatafun.mean(temp2).minus(3.1601);
                        temp2 = JElfun.pow(temp2, 2.0);

                        ctrl = temp.plus(temp2);
                        break;
                    }
                    case rat2:
                    {
                        // ctrl=(mean(z1.^2./(1+sign(z1).*z1))-0.4125).^2
                        // +(mean(z2.^2./(1+sign(z2).*z2))-0.4125).^2;
                        temp = JElfun.pow(z1, 2.0);
                        temp = temp.arrayRightDivide(z1.sign().arrayTimes(z1).plus(1.0));
                        temp = JDatafun.mean(temp).minus(0.4125);
                        temp = JElfun.pow(temp, 2.0);

                        temp2 = JElfun.pow(z2, 2.0);
                        temp2 = temp2.arrayRightDivide(z2.sign().arrayTimes(z2).plus(1.0));
                        temp2 = JDatafun.mean(temp2).minus(0.4125);
                        temp2 = JElfun.pow(temp2, 2.0);

                        ctrl = temp.plus(temp2);
                        break;
                    }
                    case pow3:
                    {
                        // ctrl=(mean((pwr(z1,4)))-3).^2
                        // +(mean((pwr(z2,4)))-3).^2;
                        temp = pwr(z1, 4);
                        temp = JDatafun.mean(temp).minus(3.0);
                        temp = JElfun.pow(temp, 2.0);

                        temp2 = pwr(z2, 4);
                        temp2 = JDatafun.mean(temp2).minus(3.0);
                        temp2 = JElfun.pow(temp2, 2.0);

                        ctrl = temp.plus(temp2);
                        break;
                    }
                    }// end switch

                    Indices indexes = ctrl.GT(ctrl0).find();// find(ctrl>ctrl0);
                    if (indexes != null)
                    {// length(indexes)>0
                        Indices irot = ii.getFromFind(indexes);// ii(indexes);
                        Indices jrot = jj.getFromFind(indexes);// jj(indexes);
                        // bad extrems indicated
                        // fprintf(' Block EFICA: rotating components: %d\n',
                        // [irot jrot]);

                        // ==================================
                        arr = indexes.getColIndicesAt(1); // 2nd column since
                                                          // 'indexes' was a
                                                          // result of callling
                                                          // 'find' from a
                                                          // row-vector

                        // u(:,irot)=z1(:,indexes);
                        temp = z1.getColumns(arr);
                        arr = irot.getRowPackedCopy();
                        u.setColumns(arr, temp);

                        // u(:,jrot)=z2(:,indexes);
                        temp = z2.getColumns(arr);
                        int[] arr2 = jrot.getRowPackedCopy();
                        u.setColumns(arr2, temp);

                        // Waux=W(:,irot);
                        Matrix Waux = W.getRows(arr);

                        // W(:,irot)=(W(:,irot)+W(:,jrot))/sqrt(2);
                        temp = W.getRows(arr);
                        temp2 = W.getRows(arr2);
                        temp = temp.plus(temp2).arrayRightDivide(SQR);
                        W.setColumns(arr, temp);

                        // W(:,jrot)=(Waux-W(:,jrot))/sqrt(2);
                        temp = Waux.minus(temp2).arrayRightDivide(SQR);
                        W.setColumns(arr2, temp);
                        // ==================================

                        NumIt = 0;
                        MaxIt = MaxItAfterSaddleTest;// status=1;
                        repeat = 2; // continue in iterating - the test of
                                    // saddle points is positive
                    }// end %if length(indeces)>0
                }// end% for delay
            }// end %if SaddleTest

            crit = Matrix.zeros(1, dim);
            // /////////////////////////////////////////////////////////////////

            /*
             * Indices rotated = new Indices(1, dim,
             * Boolean.FALSE);//false(1,dim);
             * 
             * for (int i = 0; i < dim; i++) { for (int j = i + 1; j < dim; j++)
             * { if (rotated.get(0, i) != 1 && rotated.get(0, j) != 1) { Matrix
             * ctrl = null; Matrix h =
             * u.getColumnAt(i).mergeH(u.getColumnAt(i)).times(rot2d);//[u(:,i)
             * u(:,j)]*rot2d; switch (test_of_saddle_points_nonln) { case tanh:
             * { //ctrl=(mean(log(cosh(h)))-0.37456).^2; temp =
             * JDatafun.mean(JElfun.log(JElfun.cosh(h))).minus(0.37456); ctrl =
             * JElfun.pow(temp, 2.0); break; } case gaus: {
             * //ctrl=(mean(exp(-h.^2/2)-1/SQR(2))).^2; temp = JElfun.pow(h,
             * 2.0).arrayRightDivide(-2.0); temp = JElfun.exp(temp).minus(1.0 /
             * Math.SQR(2.0)); temp = JDatafun.mean(temp); ctrl =
             * JElfun.pow(temp, 2.0); break; } case rat1: {
             * //ctrl=(mean(2*log(4+h.^2))-3.1601).^2; temp = JElfun.pow(h,
             * 2.0).plus(4.0); temp = JElfun.log(temp).arrayTimes(2.0); temp =
             * JDatafun.mean(temp).minus(3.1601); ctrl = JElfun.pow(temp, 2.0);
             * break; } case rat2: { //ctrl=(mean(h.^2./(1+abs(h)))-0.4125).^2;
             * temp = JElfun.pow(h, 2.0); temp =
             * temp.arrayRightDivide(h.abs().plus(1.0)); temp =
             * JDatafun.mean(temp).minus(0.4125); ctrl = JElfun.pow(temp, 2.0);
             * break; } case pow3: { //ctrl =(mean((pwr(h,4)))-3).^2; temp =
             * JDatafun.mean(pwr(h, 4)); temp = temp.minus(3.0); ctrl =
             * JElfun.pow(temp, 2.0); break; } case estm: { ctrl = new Matrix(1,
             * 2); //ctrl(1)=(LogLike(h(:,1)',num_seg)-1/2)^2; val =
             * LogLike(h.getColumnAt(0).transpose(), numSegment) - 0.5; val =
             * val * val; ctrl.set(0, 0, val);
             * //ctrl(2)=(LogLike(h(:,2)',num_seg)-1/2)^2; val =
             * LogLike(h.getColumnAt(1).transpose(), numSegment) - 0.5; val =
             * val * val; ctrl.set(0, 1, val); break; } default: throw new
             * IllegalArgumentException
             * ("build : Unknown \"ConstrastCondition\" object found."); }
             * 
             * if (JDatafun.max(ctrl).start() > Math.max(table1.getElementAt(i),
             * table1.getElementAt(j))) {//sum(ctrl)>table1(i)+table1(j) //bad
             * extrem indicated //rotated([i j]) = true; //do not test the
             * rotated signals anymore rotated.set(0, i, Boolean.TRUE);
             * rotated.set(0, j, Boolean.TRUE); //W(:,[i j]) = W(:,[i j])*rot2d;
             * int[] ij = {i, j}; Matrix tmpW = W.getColumns(ij).times(rot2d);
             * W.setColumns(ij, tmpW); repeat2 = true; //continue in iterating -
             * the test of saddle points is positive repeat = true; NumIt = 0;
             * MaxIt = MaxItAfterSaddleTest; //fprintf('The test of saddle
             * points: rotating components: %d and %d\n',i,j); } }//if rotated
             * }// for j }// for i } //if saddleTest crit = Matrix.zeros(1,
             * dim);
             */

        }// //////////////////////// END WHILE
         // //////////////////////////////////

        // Estimated signals by the Symmetric FastICA with the test of saddle
        // points (EEF1)
        Matrix Wsymm = W.transpose().times(CC);
        Matrix s = W.transpose().times(Z); // W'*Z;

        // [mu nu
        // beta]=params(reshape(s(:,1:(num_seg-1)*seg_len)',seg_len,(num_seg-1)*dim)',g);
        int[] arr = Indices.linspace(0, (numSegment - 1) * seg_len - 1).getRowPackedCopy();
        Matrix tempMat = s.getColumns(arr).transpose();
        tempMat = tempMat.reshape(seg_len, (numSegment - 1) * dim).transpose();
        Matrix[] matArr = params(tempMat, g);
        Matrix mu = matArr[0];
        Matrix nu = matArr[1];
        Matrix beta = matArr[2];

        // [tmpmu tmpnu tmpbeta]=params(s(:,(num_seg-1)*seg_len+1:N),g);
        // //Parameters of last segment with possible extraseg samples
        arr = Indices.linspace((numSegment - 1) * seg_len, N - 1).getRowPackedCopy();
        tempMat = s.getColumns(arr);
        matArr = params(tempMat, g);
        Matrix tmpmu = matArr[0];
        Matrix tmpnu = matArr[1];
        Matrix tmpbeta = matArr[2];

        mu = mu.reshape(numSegment - 1, dim).transpose(); // mu=reshape(mu,num_seg-1,dim)';
        nu = nu.reshape(numSegment - 1, dim).transpose();// nu=reshape(nu,num_seg-1,dim)';
        beta = beta.reshape(numSegment - 1, dim).transpose();// beta=reshape(beta,num_seg-1,dim)';
        mu.setColumnAt(numSegment - 1, tmpmu);// mu(:,num_seg)=tmpmu;
        nu.setColumnAt(numSegment - 1, tmpnu);// nu(:,num_seg)=tmpnu;
        beta.setColumnAt(numSegment - 1, tmpbeta);// beta(:,num_seg)=tmpbeta;

        // ----------------------------------------------------------------------//
        // ///////////// End of Checking. Continue on below //////////////////
        // ----------------------------------------------------------------------//
        // One-unit FastICA finetuning for piecewise stationary signals
        // utilizes Pham's score function estimator
        Matrix lambda = Matrix.ones(dim, numSegment);
        Matrix w = null;

        for (int j = 0; j < dim; j++)
        {

            w = W.getColumnAt(j);// W(:,j);
            Matrix estpars = Matrix.zeros(numBasis, numSegment); // initialization
                                                                 // of the score
                                                                 // function
                                                                 // estimator
            boolean estimate = true;
            Matrix wold = Matrix.zeros(dim, 1);
            int nit = 0;
            Matrix temp = null;

            while ((1.0 - w.transpose().times(wold).start() > fineepsilon) && (nit < FinetuneMaxIt)
                    && (W.getColumnAt(j).transpose().times(w).abs().start() > min_correlation))
            {
                // [EGS G
                // estpars]=scoreEstim((w'*Z),num_seg,estimate,estpars,Identity);
                Matrix[] SE = scoreEstim(w.transpose().times(Z), numSegment, estimate, estpars, this.identity);
                EGS = SE[0];
                G = SE[1];
                estpars = SE[2];

                Matrix tmpEXG = w.transpose().times(Z).arrayTimes(G);// (w'*Z).*G;
                // EXG(1:num_seg-1)=mean(reshape(tmpEXG(1:(num_seg-1)*seg_len),seg_len,num_seg-1));
                // EXG(num_seg)=mean(tmpEXG((num_seg-1)*seg_len+1:N)); //The
                // mean of last whole segment with added samples of the
                // extraseg(length(extraseg)<seg_len)
                arr = Indices.linspace(0, (num_seg - 1) * seg_len - 1).getRowPackedCopy();
                temp = tmpEXG.getMatrix(0, arr).reshape(seg_len, num_seg - 1);
                temp = JDatafun.mean(temp);
                Matrix EXG = new Matrix(1, num_seg);
                arr = Indices.linspace(0, num_seg - 2).getRowPackedCopy();
                EXG.setMatrix(0, arr, temp);
                arr = Indices.linspace((num_seg - 1) * seg_len, N - 1).getRowPackedCopy();
                temp = tmpEXG.getMatrix(0, arr);
                val = JDatafun.mean(temp).start();
                EXG.set(0, num_seg - 1, val);

                if (!PerfEst)
                {
                    estimate = false;
                }

                if (!uniform)
                {
                    // lambda(j,:)=(EXG-EGS)*inv(num_seg*diag(EGS)-EXG'*EXG);
                    temp = EGS.diag().arrayTimes(num_seg).minus(EXG.transpose().times(EXG));
                    temp = EXG.minus(EGS).times(temp.inverse());
                    lambda.setColumnAt(j, temp);

                    Matrix lambdas = Matrix.ones(seg_len, 1).times(lambda.getColumnAt(j)); // ones(seg_len,1)*lambda(j,:)
                    lambdas = lambdas.toColVector().transpose();// lambdas(:)';
                    if (extraseg != 0)
                    { // Lambda vector in case that there are samples in
                      // extraseg
                      // lambdas=[lambdas ones(1,extraseg)*lambda(j,num_seg)];
                        temp = new Matrix(1, extraseg, lambda.get(j, num_seg - 1));
                        lambdas = lambdas.mergeH(temp);
                    }
                    wold = w.copy();
                    // w = Z*(G.*lambdas)'/N-mean(EGS.*lambda(j,:))*w;
                    temp = JDatafun.mean(EGS.arrayTimes(lambda.getRowAt(j))).times(w);
                    w = Z.times(G.arrayTimes(lambdas)).transpose().arrayRightDivide(N).minus(temp);
                }
                else
                {
                    wold = w.copy();
                    // w=Z*G'/N-mean(EGS)*w;
                    temp = JDatafun.mean(EGS).times(w);
                    w = Z.times(G.transpose()).arrayRightDivide(N).minus(temp);
                }

                w = w.arrayRightDivide(w.norm2());// /norm(w);
                nit = nit + 1;
                TotalIt = TotalIt + 1;
            }// end while

            if (W.getColumnAt(j).transpose().times(w).abs().start() > min_correlation)
            {
                // the signal has not been changed too much, so the finetuning
                // was likely successful
                // [beta(j,:) G] =
                // scoreEstim(w'*Z,num_seg,false,estpars,Identity);
                Matrix[] SE2 = scoreEstim(w.transpose().times(Z), num_seg, false, estpars, this.identity);
                beta.setRowAt(j, SE2[0]);
                // nu(j,:) = beta(j,:);
                nu.setRowAt(j, SE2[0]);
                G = SE2[1];

                tmpmu = w.transpose().times(Z).arrayTimes(G);// (w'*Z).*G;

                // mu(j,1:num_seg-1)=mean(reshape(tmpmu(1:(num_seg-1)*seg_len),seg_len,num_seg-1));
                arr = Indices.linspace(0, (num_seg - 1) * seg_len - 1).getRowPackedCopy();
                temp = tmpmu.getMatrix(0, arr).reshape(seg_len, num_seg - 1);
                temp = JDatafun.mean(temp);
                arr = Indices.linspace(0, num_seg - 2).getRowPackedCopy();
                mu.setMatrix(j, arr, temp);

                // mu(j,num_seg)=mean(tmpmu((num_seg-1)*seg_len+1:N)); //The
                // mean of last whole segment with added samples of the
                // extraseg(length(extraseg)<seg_len)
                arr = Indices.linspace((num_seg - 1) * seg_len, N - 1).getRowPackedCopy();
                temp = tmpmu.getMatrix(0, arr);
                val = JDatafun.mean(temp).start();
                mu.set(j, num_seg - 1, val);

                W.setColumnAt(j, w);// W(:,j)=w;
            }
            else
            {
                temp = Matrix.ones(1, num_seg);
                // lambda(j,:)=ones(1,num_seg);
                lambda.setRowAt(j, temp);
            }

        }// end for(int j=0; j<dim; j++){

        // fprintf('Total number of iterations/component: %.1f\n',TotalIt/dim);
        System.out.println("Total number of iterations/component: " + (TotalIt / dim) + "\n");

        // ISR of one-unit de-mixing vector estimates
        // ISR1U=((mean(lambda.^2.*beta,2)-mean(lambda.*mu,2).^2)./(mean(lambda.*(mu-nu),2).^2))*ones(1,dim);
        Matrix temp = lambda.arrayTimes(mu);
        temp = JDatafun.mean(temp, Dimension.COL);
        temp = JElfun.pow(temp, 2.0);// mean(lambda.^2.*beta,2) -
                                     // mean(lambda.*mu,2).^2;
        Matrix temp2 = JElfun.pow(lambda, 2.0).arrayTimes(beta);
        temp2 = JDatafun.mean(temp2, Dimension.COL);
        Matrix numer = temp2.minus(temp);
        // mean(lambda.*(mu-nu),2).^2
        temp = lambda.arrayTimes(mu.minus(nu));
        temp = JDatafun.mean(temp, Dimension.COL);
        Matrix denom = JElfun.pow(temp, 2.0);

        Matrix ISR1U = numer.arrayRightDivide(denom).times(Matrix.ones(1, dim));

        // Refinement (EEF3)
        Matrix Werr = Matrix.zeros(dim);
        /*
         * for(int k=0; k<dim; k++){ temp =
         * ISR1U.getColumnAt(k).transpose().plus(1.0); Matrix ccc =
         * ISR1U.getRowAt
         * (k).arrayRightDivide(temp);//ISR1U(k,:)./(ISR1U(:,k)'+1); ccc.set(0,
         * k, 1.0);//ccc(k)=1; Matrix WW = W.times(ccc.diag()) ; sloupce =
         * setdiff(1:dim,find(sum(WW.^2)/max(sum(WW.^2))<1e-7)); // removing
         * almost zero rows Matrix M = WW(:,sloupce); M = symdecor(M); if
         * sum(sloupce==k)==1 Wextefica(:,k)=M(:,sloupce==k); else{ w =
         * JMatfun.nullSpace(M.transpose()); if (w.getColumnDimension()==1){
         * Wextefica(:,k)=w(:,1); }else{ // there are probably more than two
         * gaussian components => use the old result to get a regular matrix
         * Wextefica(:,k)=Wsymm(:,k); } } //Estimate variance of elements of the
         * gain matrix
         * Werr(k,:)=(ISR1U(k,:).*(ISR1U(:,k)'+1))./(ISR1U(k,:)+ISR1U(:,k)'+1);
         * 
         * }
         */
        Werr = Werr.minus(Werr.diag().diag());// Werr-diag(diag(Werr));

        built = true;
    }

    private Matrix[] params(Matrix s, ConstrastCondition gg)
    {
        Matrix mu = null;
        Matrix nu = null;
        Matrix beta = null;
        // [dim N]=size(s);
        int dim = s.getRowDimension();
        int N = s.getColumnDimension();
        // global numBasis;
        int num_basis = this.numBasis;

        Matrix tmp = null;

        switch (gg)
        {
        case estm:
        {
            Matrix G = Matrix.zeros(dim, N);
            Matrix EGS = Matrix.zeros(dim, 1);
            // Identity=evalin('caller','Identity');
            for (int j = 0; j < dim; j++)
            {
                // [EGS(j,:)
                // G(j,:)]=ScoreEstim(s(j,:),1,1,zeros(numBasis,1),Identity);
                Matrix[] scoreEs = scoreEstim(s.getRowAt(j), 1, true, Matrix.zeros(num_basis, 1), this.identity);
                EGS.setRowAt(j, scoreEs[0]);
                G.setRowAt(j, scoreEs[1]);
            }// end
            tmp = s.arrayTimes(G);
            mu = JDatafun.mean(tmp, Dimension.COL);
            nu = EGS;
            beta = EGS.copy();
            break;
        }
        case tanh:
        {
            tmp = s.arrayTimes(JElfun.tanh(s)); // s.*tanh(s)
            mu = JDatafun.mean(tmp, Dimension.COL);
            tmp = JElfun.cosh(s);
            tmp = JElfun.pow(tmp, 2.0).reciprocate();// 1./cosh(s).^2
            nu = JDatafun.mean(tmp, Dimension.COL);
            tmp = JElfun.tanh(s); // tanh(s).^2
            tmp = JElfun.pow(tmp, 2.0);
            beta = JDatafun.mean(tmp, Dimension.COL);
            break;
        }
        case rat1:
        {
            Matrix ssquare = JElfun.pow(s, 2.0);// s.^2;
            tmp = ssquare.arrayRightDivide(4.0);
            Matrix tmp2 = tmp.plus(1.0);
            mu = JDatafun.mean(ssquare.arrayRightDivide(tmp2), Dimension.COL);// ssquare./(1+ssquare/4),2);
            Matrix tmp3 = tmp.uminus().plus(1.0);
            Matrix tmp4 = tmp3.arrayRightDivide(tmp2);
            nu = JDatafun.mean(JElfun.pow(tmp4, 2.0), Dimension.COL);// (1-ssquare/4)./(ssquare/4+1).^2,2);
            tmp4 = ssquare.arrayRightDivide(tmp2);
            beta = JDatafun.mean(JElfun.pow(tmp4, 2.0), Dimension.COL);// ssquare./(1+ssquare/4).^2,2);
            break;
        }
        case rat2:
        {
            tmp = JDatafun.sign(s).arrayTimes(s).plus(1.0);
            Matrix r1 = s.arrayRightDivide(tmp);// s./(1+s.*sign(s));
            Matrix r2 = r1.arrayTimes(JDatafun.sign(r1));
            Matrix Rati = r1.arrayTimes(r2.uminus().plus(2.0));// r1.*(2-r2);
            Matrix tmp2 = tmp.reciprocate().arrayTimes(2.0);
            Matrix tmp3 = r2.arrayTimes(r2.uminus().plus(2.0)).uminus().plus(1.0);
            Matrix dRati = tmp3;// (2./(1+s.*sign(s))).*(1-r2.*(2-r2));
            mu = JDatafun.mean(s.arrayTimes(Rati), Dimension.COL);
            nu = JDatafun.mean(dRati, Dimension.COL);
            beta = JDatafun.mean(JElfun.pow(Rati, 2.0), Dimension.COL);
            break;
        }
        case gaus:
        {
            tmp = JElfun.pow(s, 2.0); // s.^2
            Matrix aexp = JElfun.exp(tmp.uminus().arrayRightDivide(2.0));// -s.^2/2;
            mu = JDatafun.mean(tmp.arrayTimes(aexp), Dimension.COL);// s.^2.*aexp,2);
            nu = JDatafun.mean(tmp.uminus().plus(1.0).arrayTimes(aexp), Dimension.COL);// (1-s.^2).*aexp,2);
            tmp = s.arrayTimes(aexp);// (s.*aexp).^2
            tmp = JElfun.pow(tmp, 2.0);
            beta = JDatafun.mean(tmp, Dimension.COL);
            break;
        }
        case pow3:
        {
            tmp = JElfun.pow(s, 4.0); // s.^4
            mu = JDatafun.mean(tmp, Dimension.COL);
            nu = new Matrix(dim, 1, 3.0);// 3*ones(dim,1);
            tmp = JElfun.pow(s, 6.0); // s.^4
            beta = JDatafun.mean(tmp, Dimension.COL);
            break;
        }

        }// end
        return new Matrix[]
        {
                mu, nu, beta
        };
    }

    // private double LogLike(Matrix data2, int num_seg) {
    // return 1.5;
    // }
    private Matrix pwr(Matrix a, int n)
    {
        Matrix x = a.copy();
        for (int i = 2; i <= n; i++)
        {
            x = x.arrayTimes(a);
        }
        return x;
    }

    private Matrix symdecor(Matrix M)
    {
        Matrix MtM = M.transpose().arrayTimes(M);
        // [V D]=eig(M'*M);
        EigenvalueDecomposition eig = MtM.eig();
        Matrix V = eig.getV();
        Matrix D = eig.getD();
        Matrix W = null;
        int col = M.getColumnDimension();
        // W=M*(V.*(ones(size(M,2),1)*(1./SQR(D'))))*V';
        Matrix tmp1 = JElfun.sqrt(D.diag().transpose()).reciprocate();
        tmp1 = Matrix.ones(col, 1).times(tmp1);
        Matrix tmp2 = V.arrayTimes(tmp1);
        W = M.times(tmp2).times(V.transpose());
        return W;
    }

    private Matrix[] scoreEstim(Matrix Data, int num_seg, boolean doest, Matrix estpars, boolean Identity)
    {

        Matrix tmp = null;

        int datlen = Data.length();
        int seg_len = (int) MathUtil.fix((double) datlen / (double) num_seg);
        Matrix EGS = Matrix.zeros(1, num_seg);
        Matrix psi = Matrix.zeros(1, datlen);

        // computation of the nonlinearities x^3 and x/(1+6*|x|)^2 and their
        // derivatives
        Matrix data = Data.transpose();
        Matrix x1pow2 = data.arrayTimes(data);
        Matrix x1pow3 = x1pow2.arrayTimes(data);
        Matrix x1pow4 = x1pow2.arrayTimes(x1pow2);
        Matrix denomMat = data.arrayTimes(JDatafun.sign(data)).arrayTimes(6.0).plus(1.0);
        Matrix r1 = denomMat.reciprocate();// 1./(1+6*data.*sign(data));
        Matrix r2 = r1.arrayTimes(r1);
        Matrix x2exp = data.arrayTimes(r2);
        tmp = x2exp.arrayTimes(JDatafun.sign(x2exp)).arrayTimes(r1).arrayTimes(12.0);
        Matrix x2expp = r2.minus(tmp);// r2-12*x2exp.*sign(x2exp).*r1;

        Matrix HH = null;
        Matrix reShape = null;
        Indices hSegLen = null;
        Matrix hSegLenSubMat = null;
        int[] arr = null;
        Matrix h = null;
        int newR = 0;
        int newC = 0;
        int rowLen = 0;

        if (Identity)
        {
            h = Matrix.zeros(datlen, 7);
            h.setColumnAt(0, x1pow2);// h(:,1)=x1pow2;
            h.setColumnAt(1, x1pow4);// h(:,2)=x1pow4;

            tmp = x1pow2.arrayTimes(r2);
            h.setColumnAt(2, tmp);// h(:,3)=x1pow2.*r2;

            tmp = JElfun.pow(x1pow3, 2.0);
            h.setColumnAt(3, tmp);// h(:,4)=x1pow3.^2;

            tmp = x1pow4.arrayTimes(r2);
            h.setColumnAt(4, tmp);// h(:,5)=x1pow4.*r2;

            tmp = JElfun.pow(x2exp, 2.0);
            h.setColumnAt(5, tmp);// h(:,6)=x2exp.^2;

            h.setColumnAt(6, x2expp);// h(:,7)=x2expp;

            // HH=reshape(sum(reshape(h(1:seg_len*(num_seg-1),:),seg_len,(num_seg-1)*7)),num_seg-1,7);

            // The 'minus(1)' method call at the end must be checked to see if
            // it is needed or not, just to avoid array out of bound index.
            hSegLen = Indices.linspace(1, seg_len * (num_seg - 1)).minus(1);
            arr = hSegLen.getRowPackedCopy();
            hSegLenSubMat = h.getRows(arr);

            newR = seg_len;
            newC = (num_seg - 1) * 7;
            reShape = hSegLenSubMat.reshape(newR, newC);
            reShape = JDatafun.sum(reShape);
            newR = num_seg;
            newC = 7;
            HH = reShape.reshape(newR, newC);

            rowLen = h.getRowDimension();
            // Check the indices here to see if there is an out of bound error.
            // Specifically check the -1 (minus 1) for the beginning of
            // 'linspace'
            hSegLen = Indices.linspace(seg_len * (num_seg - 1) + 1 - 1, rowLen - 1).minus(1);// seg_len*(num_seg-1)+1:end;
            arr = hSegLen.getRowPackedCopy();
            tmp = JDatafun.sum(h.getRows(arr));
            // HH(end+1,:)=sum(h(seg_len*(num_seg-1)+1:end,:));
            HH = HH.mergeV(tmp);
        }
        else
        {
            // h=[x1pow3.^2 x1pow4.*r2 x2exp.^2 x1pow2 x2expp];
            tmp = JElfun.pow(x1pow3, 2.0);
            tmp = tmp.mergeH(x1pow4.arrayTimes(r2));
            tmp = tmp.mergeH(JElfun.pow(x2exp, 2.0));
            tmp = tmp.mergeHoriz(x1pow2, x2expp);

            // HH=reshape(sum(reshape(h(1:seg_len*(num_seg-1),:),seg_len,(num_seg-1)*5)),num_seg-1,5);

            newR = seg_len;
            newC = (num_seg - 1) * 5;
            // The 'minus(1)' method call at the end must be checked to see if
            // it is needed or not, just to avoid array out of bound index.
            hSegLen = Indices.linspace(1, seg_len * (num_seg - 1)).minus(1);
            arr = hSegLen.getRowPackedCopy();
            hSegLenSubMat = h.getRows(arr);
            reShape = hSegLenSubMat.reshape(newR, newC);
            reShape = JDatafun.sum(reShape);
            newR = num_seg - 1;
            newC = 5;
            HH = reShape.reshape(newR, newC);

            rowLen = h.getRowDimension();
            // Check the indices here to see if there is an out of bound error.
            // Specifically check the -1 (minus 1) for the beginning of
            // 'linspace'
            hSegLen = Indices.linspace(seg_len * (num_seg - 1) + 1 - 1, rowLen - 1).minus(1);// seg_len*(num_seg-1)+1:end;
            arr = hSegLen.getRowPackedCopy();
            tmp = JDatafun.sum(h.getRows(arr));
            // HH(end+1,:)=sum(h(seg_len*(num_seg-1)+1:end,:));
            HH = HH.mergeV(tmp);
        }// end

        int first = 0;
        int last = 0;
        Matrix H = null;
        double val = 0.0;

        for (int i = 0; i < num_seg; i++)
        {
            first = (i - 1) * seg_len + 1;
            if ((i + 1) == num_seg)
            { // Last segment could have different length than the others
                last = datlen;
                seg_len = datlen - first + 1;
            }
            else
            {// Check here since 'i' has been argumented into 'i+1'
                last = (i + 1) * seg_len;
            }// end
            H = HH.getRowAt(i);// HH(i,:);
            Matrix w = null;
            if (Identity)
            {
                double H5sqr = H.getElementAt(4) * H.getElementAt(4);
                double H3sqr = H.getElementAt(2) * H.getElementAt(2);
                double dt = H.getElementAt(0) * (H.getElementAt(3) * H.getElementAt(5) - H5sqr) - H.getElementAt(1)
                        * (H.getElementAt(1) * H.getElementAt(5) - 2.0 * H.getElementAt(2) * H.getElementAt(4)) - H3sqr
                        * H.getElementAt(3);

                double a11 = H.getElementAt(5) * H.getElementAt(3) - H5sqr; // H(6)*H(4)
                                                                            // -
                                                                            // H(5)^2
                double a12 = H.getElementAt(2) * H.getElementAt(4) - H.getElementAt(1) * H.getElementAt(5);
                double a13 = H.getElementAt(1) * H.getElementAt(4) - H.getElementAt(2) * H.getElementAt(3);
                double a22 = H.getElementAt(0) * H.getElementAt(5) - H3sqr;
                double a23 = H.getElementAt(1) * H.getElementAt(2) - H.getElementAt(0) * H.getElementAt(4);
                double a33 = H.getElementAt(0) * H.getElementAt(3) - H.getElementAt(1) * H.getElementAt(1);

                w = new Matrix(1, 3);

                val = (seg_len * a11 + 3 * H.getElementAt(0) * a12 + a13 * H.getElementAt(6)) / dt;
                w.set(0, 0, val);
                val = (seg_len * a12 + 3 * H.getElementAt(0) * a22 + a23 * H.getElementAt(6)) / dt;
                w.set(0, 1, val);
                val = (seg_len * a13 + 3 * H.getElementAt(0) * a23 + a33 * H.getElementAt(6)) / dt;
                w.set(0, 2, val);
                val = (seg_len * w.getElementAt(0) + 3 * H.getElementAt(0) * w.getElementAt(1) + H.getElementAt(6)
                        * w.getElementAt(2))
                        / seg_len; // Note that, here, E[psi']=E[psi^2] !
                EGS.set(0, i, val);
            }
            else
            {
                double[][] hhTarray =
                {
                        {
                                H.getElementAt(0), H.getElementAt(1)
                        },
                        {
                                H.getElementAt(1), H.getElementAt(2)
                        }
                };
                Matrix hhT = new Matrix(hhTarray);// [H([1 2]);H([2 3])];
                double[][] sumdHarray =
                {
                        {
                            3.0 * H.getElementAt(3)
                        },
                        {
                            H.getElementAt(4)
                        }
                };
                Matrix sumdh = new Matrix(sumdHarray);// [3*H(4); H(5)];
                Matrix invhhT = inv2by2(hhT);// inv(hhT);
                w = invhhT.times(sumdh);
                val = w.transpose().times(sumdh).start() / (double) seg_len; // Note
                                                                             // that,
                                                                             // here,
                                                                             // E[psi']=E[psi^2]
                                                                             // !
                EGS.set(0, i, val);
            }// end
            if (doest)
            { // if true, re-estimate optimum linear combination of functions
              // estpars(:,i)=w;
                estpars.setColumnAt(i, w);
            }
            else
            {
                w = estpars.getColumnAt(i);// w=estpars(:,i);
            }// end

            Indices firstLast = Indices.linspace(first, last);
            arr = firstLast.getRowPackedCopy();
            if (Identity)
            {
                tmp = data.getEls(arr).arrayTimes(w.start());
                tmp = tmp.plus(x1pow3.getEls(arr).arrayTimes(w.getElementAt(1)));
                tmp = tmp.plus(x2exp.getEls(arr).arrayTimes(w.getElementAt(2)));
                // psi(first:last)=w(1)*data(first:last)+
                // w(2)*x1pow3(first:last)+ w(3)*x2exp(first:last);
                psi.setElements(arr, tmp);
            }
            else
            {
                tmp = x1pow3.getEls(arr).arrayTimes(w.start());
                tmp = tmp.plus(x2exp.getEls(arr).arrayTimes(w.getElementAt(1)));
                // psi(first:last)=w(1)*x1pow3(first:last)+w(2)*x2exp(first:last);
                psi.setElements(arr, tmp);
            }// end
        }// end for

        return new Matrix[]
        {
                EGS, psi, estpars
        };
    }

    private Matrix inv2by2(Matrix mat)
    {
        int row = mat.getRowDimension();
        int col = mat.getColumnDimension();
        if (row != 2 && col != 2)
        {
            throw new IllegalArgumentException("inv2by2 : Parameter \"mat\" must be a 2 by 2 matrix.");
        }
        Matrix inv = null;
        double[][] A = mat.getArray();
        double det = A[0][0] * A[1][1] - A[0][1] * A[1][0];
        double[][] cofactor =
        {
                {
                        A[1][1], -A[0][1]
                },
                {
                        -A[1][0], A[0][0]
                }
        };
        inv = new Matrix(cofactor).arrayRightDivide(det);
        return inv;
    }

    /**
     * @param numSegment
     *            the numSegment to set
     */
    public void setNumSegment(int num_seg)
    {
        this.numSegment = num_seg;
    }

    /**
     * @param ini
     *            the ini to set
     */
    public void setIni(Matrix ini)
    {
        int dim = X.getRowDimension();
        int N = X.getColumnDimension();
        if (ini == null && this.ini == null)
        {
            this.ini = Matrix.randn(dim);
        }
        else
        {
            if (!ini.isSquare())
            {
                throw new IllegalArgumentException("setIni : Parameter \"ini\" must be a square matrix.");
            }
            if (ini.length() != dim)
            {
                throw new IllegalArgumentException(
                        "setIni : Parameter \"ini\" must be a square matrix with sizes equal to the number of data sources/signals.");
            }
            this.ini = ini;
        }
    }

    /**
     * @param saddleTest
     *            the saddleTest to set
     */
    public void setSaddleTest(boolean SaddleTest)
    {
        this.saddleTest = SaddleTest;
    }

    /**
     * @param uniform
     *            the uniform to set
     */
    public void setUniform(boolean Uniform)
    {
        this.uniform = Uniform;
    }

    /**
     * @param identity
     *            the identity to set
     */
    public void setIdentity(boolean Identity)
    {
        this.identity = Identity;
    }

    /**
     * @return the built
     */
    public boolean isBuilt()
    {
        return built;
    }

    /**
     * @param g
     *            the g to set
     */
    public void setG(ConstrastCondition g)
    {
        if (g == null)
        {
            this.g = ConstrastCondition.rat1;
            return;
        }
        this.g = g;
    }
}
