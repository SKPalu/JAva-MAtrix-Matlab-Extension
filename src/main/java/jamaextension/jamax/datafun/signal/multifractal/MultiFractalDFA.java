/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun.signal.multifractal;

import java.io.FileNotFoundException;
import java.io.IOException;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.FindInd;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.elfun.JElfun;
import jamaextension.jamax.polyfun.PolyFit;
import jamaextension.jamax.polyfun.PolyVal;

/**
 * 
 * @author Sione
 */
public class MultiFractalDFA
{

    private Matrix s;
    private Matrix q;
    private Matrix Hq;
    private Matrix h;
    private Matrix Dh;
    private Matrix logFq;
    private static String filePath = "C:/Users/Sione/Documents/MATLAB/datafiles/multifractaltoolbox/";

    public MultiFractalDFA(Matrix signal, int m, double scmin, double scmax, int ressc, double qmin, double qmax,
            int qres)
    {

        if (signal == null || signal.isNull())
        {
            throw new ConditionalException("MultiFractalDFA : Parameter \"signal\" must be non-null or non-empty.");
        }
        if (!signal.isVector())
        {
            throw new ConditionalException("MultiFractalDFA : Parameter \"signal\" must be a vector and not a matrix.");
        }
        if (signal.isColVector())
        {
            signal = signal.toRowVector();
        }

        if (m < 1)
        {
            throw new ConditionalException("MultiFractalDFA : Order parameter \"m\" (= " + m + ") must be at least 1 ;");
        }

        double tmpVal = 0.0;
        if (scmin == scmax)
        {
            throw new ConditionalException("MultiFractalDFA : Parameters \"scmin\" and \"scmax\" (= " + scmin
                    + ") must have different values ;");
        }
        if (scmin > scmax)
        {
            tmpVal = scmin;
            scmin = scmax;
            scmax = tmpVal;
        }

        if (ressc < 10)
        {
            throw new ConditionalException("MultiFractalDFA : Parameter \"ressc\" (= " + ressc
                    + ") must be at least 10 ;");
        }

        if (qmin == qmax)
        {
            throw new ConditionalException("MultiFractalDFA : Parameters \"qmin\" and \"qmax\" (= " + qmin
                    + ") must have different values ;");
        }
        if (qmin > qmax)
        {
            tmpVal = qmin;
            qmin = qmax;
            qmax = tmpVal;
        }

        if (qres < 10)
        {
            throw new ConditionalException("MultiFractalDFA : Parameter \"qres\" (= " + qres
                    + ") must be at least 10 ;");
        }

        Matrix sigMean = JDatafun.mean(signal);
        Matrix sigStd = JDatafun.std(signal);
        Matrix zScore = signal.minus(sigMean).arrayRightDivide(sigStd);// signal-mean(signal)./std(signal)
        Matrix Fluct = JDatafun.cumsum(zScore);
        // Fluct.printInLabel("Fluct");
        Matrix FluctRev = Fluct.flipLR();
        // FluctRev.printInLabel("FluctRev");

        double tr = 1.0E-8;

        /*
         * Matrix[] flu = readFluctMat(); boolean condFlu =
         * Fluct.eqBoolean(flu[0], tr); if (condFlu) {
         * System.out.println(" Fluct EQUAL"); } else {
         * System.out.println(" Fluct NOT_EQUAL"); } condFlu =
         * FluctRev.eqBoolean(flu[1], tr); if (condFlu) {
         * System.out.println(" FluctRev EQUAL"); } else {
         * System.out.println(" FluctRev NOT_EQUAL"); }
         */

        int N = Fluct.length();

        double start = JElfun.logBase2(scmin);
        // System.out.println("start = " + start);
        double end = JElfun.logBase2(scmax); // (Double) JElfun.log2(scmax)[0];
        // System.out.println("end = " + end);
        Matrix ScaleNumb = Matrix.linspace(start, end, ressc);// (log2(scmin),log2(scmax),ressc);

        Matrix tmp = JElfun.pow(2.0, ScaleNumb);
        s = tmp.round();

        // s.sizeIndices().printInLabel("==== size(s) ====");

        q = Matrix.linspace(qmin, qmax, qres);

        // q.sizeIndices().printInLabel("==== size(q) ====");

        // znumb=find(q==0);
        FindInd find = q.EQ(0.0).findIJ();
        Indices znumb = find.getIndexInd();
        Matrix Fq = Matrix.zeros(s.length(), q.length());

        // /////////////////////////////////////
        /*
         * Matrix NNSS = s.reciprocate().arrayTimes((double) N); NNSS =
         * JElfun.floor(NNSS); Indices NNSSInd = NNSS.toIndices();
         * NNSSInd.printInLabel("NNSSInd");
         */
        // ////////////////////////////////////

        // int count = 1;

        for (int ns = 0; ns < s.length(); ns++)
        {// ,//disp(strcat('computing scale number_',num2str(ns)));

            int Ns = (int) Math.floor((double) N / s.getElementAt(ns));
            Matrix Var = null;
            Matrix Varr = null;
            // if (ns == 15) {
            // Var.sizeIndices().printInLabel("Var : Size");
            // }
            if (Ns != 0)
            {
                Var = Matrix.zeros(Ns, q.length());
                Varr = Matrix.zeros(Ns, q.length());
            }

            if (Ns > 0)
            {
                for (int v = 0; v < Ns; v++)
                {
                    int V = v + 1;
                    int from = (((V - 1) * (int) s.getElementAt(ns)) + 1) - 1;
                    int to = (V * (int) s.getElementAt(ns)) - 1;

                    // SegNumb=((((v-1)*s(ns))+1):(v*s(ns)))';
                    Indices SegNumb = Indices.linspace(from, to);// .linIncrement(from,
                                                                 // to,
                                                                 // 1.0).transpose();
                    // SegNumb.printInLabel("SegNumb");
                    Matrix SegNumbMat = Matrix.indicesToMatrix(SegNumb).plus(1.0); // add
                                                                                   // 1,
                                                                                   // since
                                                                                   // java
                                                                                   // index
                                                                                   // is
                                                                                   // less
                                                                                   // by
                                                                                   // 1
                                                                                   // than
                                                                                   // matlab
                    int[] intArr = SegNumb.getRowPackedCopy();

                    Matrix Seg = Fluct.getEls(intArr);
                    // Seg.sizeIndices().printInLabel("Seg : Size");
                    Matrix SegRev = FluctRev.getEls(intArr);
                    PolyFit poly = new PolyFit(SegNumbMat, Seg.transpose(), m);
                    poly.setSorted(true);
                    poly.build();
                    Matrix polyCoeff = poly.getCoeffs();
                    // polyCoeff.printInLabel("polyCoeff["+ns+"]["+v+"]");

                    PolyFit polyr = new PolyFit(SegNumbMat, SegRev.transpose(), m);
                    polyr.build();
                    poly.setSorted(true);
                    Matrix polyrCoeff = polyr.getCoeffs();
                    // polyrCoeff.printInLabel("polyrCoeff["+ns+"]["+v+"]");

                    // fit=polyval(poly,SegNumb);
                    PolyVal pvalFit = new PolyVal(polyCoeff, SegNumbMat);
                    Matrix fit = pvalFit.getY().toColVector();
                    // fit.sizeIndices().printInLabel("fit : Size");
                    // fit.printInLabel("fitVal["+ns+"]["+v+"]");

                    // fitr=polyval(polyr,SegNumb);
                    PolyVal pvalFitr = new PolyVal(polyrCoeff, SegNumbMat);
                    Matrix fitr = pvalFitr.getY().toColVector();
                    // fitr.printInLabel("fitrVal["+ns+"]["+v+"]");

                    double val = 0.0;

                    for (int nq = 0; nq < q.length(); nq++)
                    {
                        // Var(v,nq)=((sum((Seg'-fit).^2))/s(ns))^(q(nq)/2);
                        double qnq2 = q.getElementAt(nq) / 2.0;
                        double sns = s.getElementAt(ns);
                        Matrix Segfit = Seg.transpose().minus(fit);
                        Segfit = JElfun.pow(Segfit, 2.0);
                        val = JDatafun.sum(Segfit).start() / sns;
                        val = Math.pow(val, qnq2);

                        Var.set(v, nq, val);

                        // Varr(v,nq)=((sum((SegRev'-fitr).^2))/s(ns))^(q(nq)/2);
                        Matrix SegRevfit = SegRev.transpose().minus(fitr);
                        SegRevfit = JElfun.pow(SegRevfit, 2.0);
                        val = JDatafun.sum(SegRevfit).start() / sns;
                        val = Math.pow(val, qnq2);
                        // if (nq <= 9) {
                        // System.out.println("Varrnq = " + val);
                        // }
                        Varr.set(v, nq, val);

                    }// end; for
                     // clear SegNumb Seg SedRev poly polyr fit fitr

                    // System.out.println("count_#" + count);
                    // System.out.println("############################################\n");
                }// end inner for 1
            }

            /*
             * if (ns == 0 && Var != null) { Matrix readVar = readVarMat();
             * double tol = 1.0E-10; boolean varEQ = Var.eqBoolean(readVar,
             * tol); if (varEQ) { System.out.println(ns + ") EQUAL"); } else {
             * System.out.println(ns + ") NOT_EQUAL"); } }
             */

            for (int nq = 0; nq < q.length(); nq++)
            {
                // Fq(ns,nq)=((sum(Var(:,nq))+sum(Varr(:,nq)))/(2*Ns))^(1/q(nq));
                double val = Double.NaN;
                if (Ns > 0)
                {
                    double qnqres = 1.0 / q.getElementAt(nq);
                    double Ns2 = 2.0 * Ns;
                    Matrix varMat = Var.getColumnAt(nq);

                    // System.out.println("###################### ns = " + ns +
                    // " ;  nq = " + nq + " ######################");
                    double sumVar = 0.0;
                    // if (ns == 14 && nq == 59) {
                    Matrix sumVarMat = JDatafun.sum(varMat);
                    sumVar = sumVarMat.start();
                    // }

                    varMat = Varr.getColumnAt(nq);
                    double sumVarr = JDatafun.sum(varMat).start();
                    val = (sumVar + sumVarr) / Ns2;
                    val = Math.pow(val, qnqres);
                }

                if (ns == 3 && nq <= 5)
                {
                    // System.out.println("Fq_nsnq = " + val);
                }

                Fq.set(ns, nq, val);
            }// end

            // System.out.println(" %%%%%%%%%%%% REACHED HERE %%%%%%%%%%%%\n");

            if (znumb != null)
            {
                // znumb.printInLabel("znumb");
                int indVal = znumb.start();
                // System.out.println("indVal = " + indVal);
                double val = 0.0;
                boolean cond = (indVal != 0) && (indVal != (q.length() - 1));
                // Fq(ns,znumb)=(Fq(ns,znumb-1)+Fq(ns,znumb+1))./2;
                if (cond)
                {
                    val = (Fq.get(ns, indVal - 1) + Fq.get(ns, indVal + 1)) / 2.0;
                    // System.out.println("znumb : BLOCK_#1");
                }
                else if (indVal == 0)
                {
                    // val = Fq.get(ns, indVal + 1) / 2.0;
                    // System.out.println("znumb : BLOCK_#2");
                    throw new ConditionalException("MultiFractalDFA : Index (= -1) is out-of-bound ;");
                }
                else if (indVal != (q.length() - 1))
                {
                    // val = Fq.get(ns, indVal - 1) / 2.0;
                    // System.out.println("znumb : BLOCK_#3");
                    int I = q.length();
                    throw new ConditionalException("MultiFractalDFA : Index (= " + I + ") is out-of-bound ;");
                }

                // System.out.println("");

                Fq.set(ns, indVal, val);
            }
            else
            {
                // System.out.println("znumb == NULL");
            }

            // Fq.printInLabel("Fq");

            // System.out.println(" ############################### \n");
            // clear Var Varr
            // count += 1;

        }// end outer for

        // s.printInLabel("s");

        // Fq.printInLabel("Fq2");

        double tol = 1.0E-8;
        /*
         * Matrix FqRead = readFqMat(); boolean eqFq = Fq.eqBoolean(FqRead,
         * tol); if (eqFq) {
         * System.out.println("============== Fq EQUAL =============="); } else
         * { System.out.println("============== Fq NOT_EQUAL =============="); }
         */

        logFq = JElfun.logBase2(Fq);// log2(Fq);
        // logFq.printInLabel("logFq");

        Hq = Matrix.zeros(1, q.length());
        Matrix lin_fit = Matrix.zeros(s.length(), q.length());
        for (int nq = 0; nq < q.length(); nq++)
        {
            // P=polyfit(log2(s'),logFq(:,nq),1);
            Matrix log2s = JElfun.logBase2(s.transpose());
            // log2s.printInLabel("log2s");

            Matrix logFqnq = logFq.getColumnAt(nq);
            // logFqnq.printInLabel("logFqnq");

            PolyFit poly = new PolyFit(log2s, logFqnq, 1);
            // System.out.println("nq = " + nq + "");
            poly.build();
            poly.setSorted(true);
            Matrix P = poly.getCoeffs();

            // lin_fit(1:length(s),nq)=polyval(P,log2(s'));
            PolyVal pval = new PolyVal(P, log2s);
            Matrix yVal = pval.getY();
            lin_fit.setMatrix(0, s.length() - 1, nq, nq, yVal);

            // Hq(nq)=P(1);
            Hq.setElementAt(nq, P.start());
        }// end;

        // Hq.printInLabel("Hq");

        Matrix tau = q.arrayTimes(Hq).minus(1.0);// (q.*Hq)-1;
        // tau.printInLabel("tau");

        /*
         * Matrix tauMat = readTauMat(); boolean compare = tau.equalAll(tauMat,
         * tol).allBoolean();
         * System.out.println("#######################################"); if
         * (compare) { System.out.println(" tau :  EQUAL"); } else {
         * System.out.println(" tau :  NOT_EQUAL"); }
         * System.out.println("#######################################\n");
         * tau.printInLabel("tau", 16);
         */

        double q2q1 = q.getElementAt(1) - q.start();
        // System.out.println(" q2q1 =  " + q2q1);

        Matrix diffTau = JDatafun.diff(tau);
        // diffTau.printInLabel("diffTau",16);

        Matrix hh = diffTau.arrayRightDivide(q2q1);// ./(q(2)-q(1));
        // hh.printInLabel("hh", 16);

        int qlen2 = q.length() - 2;
        // int[] qlen2Arr = Indices.linspace(0, qlen2).getRowPackedCopy();
        tmp = q.getEls(0, qlen2).arrayTimes(hh);
        // Dh=(q(1:(end-1)).*hh)-tau(1:(end-1));
        Dh = tmp.minus(tau.getEls(0, qlen2));
        // Dh.printInLabel("Dh");

        h = hh.minus(1.0);
        // h.printInLabel("h");

    }

    /**
     * @return the s
     */
    public Matrix getS()
    {
        return s;
    }

    /**
     * @return the q
     */
    public Matrix getQ()
    {
        return q;
    }

    /**
     * @return the Hq
     */
    public Matrix getHq()
    {
        return Hq;
    }

    /**
     * @return the h
     */
    public Matrix getH()
    {
        return h;
    }

    /**
     * @return the Dh
     */
    public Matrix getDh()
    {
        return Dh;
    }

    /**
     * @return the logFq
     */
    public Matrix getLogFq()
    {
        return logFq;
    }

    static Matrix getSignalMatrix2()
    {
        double[] xx =
        {
                22, 19, 38, 40, 9, 24, 22, 32, 35, 38, 14, 34, 33, 8, 6, 25, 48, 17, 29, 11, 38, 13, 25, 35, 45, 48,
                27, 7, 7, 13
        };
        Matrix sig = new Matrix(xx);
        return sig;
    }

    static Matrix getSignalMatrix1()
    {
        Matrix tmpRead = null;
        try
        {
            // String fileNameInd = SvmUtil.getSvmDataFolder() +"ind.txt";
            String FL = filePath + "diffVH1.txt";// fileNameInd +
                                                 // fileNumStr[fileCount++];
            // System.out.println("FL =>> " + FL);
            tmpRead = Matrix.read(FL);
            // ind = tmpRead.toIndices().minus(1);
            // ind.printInLabel("ind");
        }
        catch (FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        return tmpRead;
    }

    static Matrix getSignalMat()
    {
        Matrix tmpRead = null;
        try
        {
            // String fileNameInd = SvmUtil.getSvmDataFolder() +"ind.txt";
            String FL = filePath + "sig.txt";// fileNameInd +
                                             // fileNumStr[fileCount++];
            // System.out.println("FL =>> " + FL);
            tmpRead = Matrix.read(FL);
            // ind = tmpRead.toIndices().minus(1);
            // ind.printInLabel("ind");
        }
        catch (FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        return tmpRead;
    }

    static Matrix[] readFluctMat()
    {
        Matrix[] matArr = new Matrix[2];
        try
        {
            String FL = filePath + "Fluct.txt";
            matArr[0] = Matrix.read(FL);
            FL = filePath + "FluctRev.txt";
            matArr[1] = Matrix.read(FL);
        }
        catch (FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        return matArr;
    }

    static Matrix readTauMat()
    {
        Matrix matArr = null;
        try
        {
            String FL = filePath + "tau.txt";
            matArr = Matrix.read(FL);

        }
        catch (FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        return matArr;
    }

    static Matrix readFqMat()
    {
        Matrix matArr = null;
        try
        {
            String FL = filePath + "Fq.txt";
            matArr = Matrix.read(FL);
        }
        catch (FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        return matArr;
    }

    static Matrix readVarMat()
    {
        Matrix matArr = null;
        try
        {
            String FL = filePath + "Var.txt";
            matArr = Matrix.read(FL);
        }
        catch (FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        return matArr;
    }

    static Matrix[] getOutputs()
    {
        Matrix[] matArr = new Matrix[6];

        System.out.println("\nOUTPUTS READINGS STARTS");

        // s,q,Hq1,h1,Dh1,logFq1
        try
        {
            String FL = filePath + "s.txt";
            matArr[0] = Matrix.read(FL);

            FL = filePath + "q.txt";
            matArr[1] = Matrix.read(FL);

            FL = filePath + "Hq1.txt";
            matArr[2] = Matrix.read(FL);

            FL = filePath + "h1.txt";
            matArr[3] = Matrix.read(FL);

            FL = filePath + "Dh1.txt";
            matArr[4] = Matrix.read(FL);

            FL = filePath + "logFq1.txt";
            matArr[5] = Matrix.read(FL);

        }
        catch (FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }

        System.out.println("OUTPUTS READINGS SUCCESSFUL\n");

        return matArr;
    }

    public static void main(String[] args)
    {
        Matrix sig = getSignalMat(); // sig,1,10,200,40,1,7,61

        MultiFractalDFA MDFA = new MultiFractalDFA(sig, 1, 10, 200, 40, -3, 3, 61);// sig,1,10,200,40,-3,3,61);//(sig,
                                                                                   // 1,
                                                                                   // 10,
                                                                                   // 200,
                                                                                   // 40,
                                                                                   // 1,
                                                                                   // 7,
                                                                                   // 61);

        if (true)
        {
            // return;
        }

        double tolerance = 1.0E-7;

        Matrix[] outputs = getOutputs();

        boolean tf = outputs[0].equalAll(MDFA.s, tolerance).allBoolean();
        if (tf)
        {
            System.out.println(" s :  EQUAL");
        }
        else
        {
            System.out.println(" s :  NOT_EQUAL");
        }
        System.out.println("\n");

        tf = outputs[1].equalAll(MDFA.q, tolerance).allBoolean();
        if (tf)
        {
            System.out.println(" q :  EQUAL");
        }
        else
        {
            System.out.println(" q :  NOT_EQUAL");
        }
        System.out.println("\n");

        tf = outputs[2].equalAll(MDFA.Hq, tolerance).allBoolean();
        if (tf)
        {
            System.out.println(" Hq1 :  EQUAL");
        }
        else
        {
            System.out.println(" Hq1 :  NOT_EQUAL");
        }
        System.out.println("\n");

        tf = outputs[3].equalAll(MDFA.h, tolerance).allBoolean();
        if (tf)
        {
            System.out.println(" h1 :  EQUAL");
        }
        else
        {
            System.out.println(" h1 :  NOT_EQUAL");
        }
        System.out.println("\n");

        tf = outputs[4].equalAll(MDFA.Dh, tolerance).allBoolean();
        if (tf)
        {
            System.out.println(" Dh1 :  EQUAL");
        }
        else
        {
            System.out.println(" Dh1 :  NOT_EQUAL");
        }
        System.out.println("\n");

        tf = outputs[5].equalAll(MDFA.logFq, tolerance).allBoolean();
        if (tf)
        {
            System.out.println(" logFq1 :  EQUAL");
        }
        else
        {
            System.out.println(" logFq1 :  NOT_EQUAL");
        }
        System.out.println("\n");

        System.out.println("\n\nEXECUTION COMPLETED");

    }
}
