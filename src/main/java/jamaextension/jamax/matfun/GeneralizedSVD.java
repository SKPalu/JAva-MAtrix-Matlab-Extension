/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.matfun;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jamaextension.jamax.ConditionalException;
import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.QrJLapack;
import jamaextension.jamax.SvdJLapack;
import jamaextension.jamax.datafun.JDatafun;

/**
 * 
 * @author Sione
 */
public class GeneralizedSVD
{
    // gsvdV,V,X,C,S
    /* Main variables */

    private Matrix V;
    private Matrix U;
    private Matrix X;
    private Matrix S;
    private Matrix C;
    /* Internal variables */
    private Matrix QA;
    private Matrix QB;
    private int m;
    private int n;
    private int p;

    // private Econ economy = Econ.ZERO;

    public GeneralizedSVD(Matrix Aa, Matrix Bb, QrJLapack.Economy arg3)
    {
        if (Aa == null)
        {
            throw new ConditionalException("GeneralizedSVD : Matrix \"Aa\" must be non-null.");
        }
        if (Bb == null)
        {
            throw new ConditionalException("GeneralizedSVD : Matrix \"Bb\" must be non-null.");
        }

        this.m = Aa.getRowDimension();
        this.p = Aa.getColumnDimension();
        this.n = Bb.getRowDimension();
        int pb = Bb.getColumnDimension();

        if (pb != this.p)
        {
            throw new ConditionalException(
                    "GeneralizedSVD : Matrices \"A\" and \"B\" must have the same number of columns.");
        }// end

        QrJLapack QR = null;

        Matrix A = Aa;
        Matrix B = Bb;

        if (arg3 != null)
        {// nargin > 2
            Matrix[] matArr = null;
            // Economy-sized.
            if (this.m > this.p)
            { // block test - passed
              // [QA,Aa] = qr(Aa,0);
                QR = new QrJLapack(Aa, arg3);
                QA = QR.getQ();
                A = QR.getR();
                // [QA,Aa] = diagp(QA,Aa,0);
                matArr = diagp(QA, A, 0);
                QA = matArr[0];
                A = matArr[1];
                this.m = this.p;

                // QA.printInLabel("**QA**");
            }// end
            if (this.n > this.p)
            {// block test - passed
             // [QB,Bb] = qr(Bb,0);
                QR = new QrJLapack(Bb, arg3);
                QB = QR.getQ();
                B = QR.getR();
                // [QB,Bb] = diagp(QB,Bb,0);
                matArr = diagp(QB, B, 0);
                QB = matArr[0];
                B = matArr[1];
                this.n = this.p;

                // QB.printInLabel("**QB**");
            }// end
        }// end

        Matrix AB = A.mergeV(B);

        // [Q,R] = qr([A;B],0);
        QR = new QrJLapack(AB, QrJLapack.Economy.ZERO);
        Matrix Q = QR.getQ();
        Matrix R = QR.getR();

        // Q.printInLabel("gsvdU");
        // R.printInLabel("R1");

        // if(true){ return; }

        int[] intArr = Indices.linspace(0, this.m - 1).getRowPackedCopy();
        Matrix Q1m = Q.getRows(intArr);
        // System.out.println(" Q1m = " + (Q1m.isSquare() ? "Square" :
        // "Rectangular"));
        // Q1m.printInLabel("Q1m");
        intArr = Indices.linspace(this.m, this.m + this.n - 1).getRowPackedCopy();
        Matrix Qmn = Q.getRows(intArr);
        // Qmn.printInLabel("Qmn");

        boolean test = false;
        // [gsvdV,V,Z,C,S] = csd(Q(1:mm,:),Q(mm+1:mm+nn,:));
        Matrix[] matArr = csd(Q1m, Qmn, test);

        this.U = matArr[0];
        this.V = matArr[1];
        Matrix Z = matArr[2];
        this.C = matArr[3];
        this.S = matArr[4];

        // Z.printInLabel("Z");

        this.X = R.transpose().times(Z);
    }

    public GeneralizedSVD(Matrix A, Matrix B)
    {
        this(A, B, null);
    }

    private Matrix[] csd(Matrix Q1, Matrix Q2)
    {
        return csd(Q1, Q2, false);
    }

    private Matrix[] csd(Matrix Q1, Matrix Q2, boolean test)
    {

        // gsvdU.printInLabel("gsvdU");

        int mm = Q1.getRowDimension();
        int pp = Q1.getColumnDimension();
        int nn = Q2.getRowDimension();
        int pb = Q2.getColumnDimension();
        // [mm,pp] = size(gsvdU);
        // [nn,pb] = size(Q2);
        if (pb != pp)
        {
            throw new ConditionalException("csd : Matrices \"Q1\" and \"Q2\" must have the same number of columns.");
        }

        Matrix tmp1 = null;

        if (mm < nn)
        {// ############# Block test - passed ##################
         // System.out.println("\n csd : ========================= BLOCK #1 =========================\n");
         // [Vv,Uu,Zz,Ss,Cc] = csd(Q2,gsvdU);
            Matrix[] matArr = csd(Q2, Q1, test);
            Matrix Vv2 = matArr[0];
            Matrix Uu2 = matArr[1];
            Matrix Zz2 = matArr[2];
            Matrix Ss2 = matArr[3];
            Matrix Cc2 = matArr[4];

            // Vv2.printInLabel("Vv2 - a");

            // j = pp:-1:1;
            Indices PP = Indices.linspace(0, pp - 1).flipLR();
            // PP.printInLabel("PP");
            int[] intArr = PP.getRowPackedCopy();
            Cc2 = Cc2.getColumns(intArr);// Cc(:,j);
            Ss2 = Ss2.getColumns(intArr);// Ss(:,j);
            Zz2 = Zz2.getColumns(intArr);// Zz(:,j);
            // Zz2.printInLabel("Zz2");

            mm = Math.min(mm, pp);
            // i = mm:-1:1;
            Indices ind = Indices.linspace(0, mm - 1);// .flipLR().getRowPackedCopy();
            int[] indMn = ind.getRowPackedCopy();
            intArr = ind.flipLR().getRowPackedCopy();
            Matrix temp = null;

            // Cc(1:mm,:) = Cc(i,:);
            temp = Cc2.getRows(intArr);
            Cc2.setRows(indMn, temp);
            // Cc2.printInLabel("Cc2");
            // Uu(:,1:mm) = Uu(:,i);
            temp = Uu2.getColumns(intArr);
            Uu2.setColumns(indMn, temp);

            nn = Math.min(nn, pp);
            // i = nn:-1:1;
            ind = Indices.linspace(0, nn - 1);
            indMn = ind.getRowPackedCopy();
            intArr = ind.flipLR().getRowPackedCopy();
            // Ss(1:nn,:) = Ss(i,:);
            temp = Ss2.getRows(intArr);
            Ss2.setRows(indMn, temp);
            // Vv(:,1:nn) = Vv(:,i);
            temp = Vv2.getColumns(intArr);
            Vv2.setColumns(indMn, temp);

            // Vv2.printInLabel("Vv2");
            // Uu2.printInLabel("Uu2");
            // Zz2.printInLabel("Zz2");
            // Ss2.printInLabel("Ss2");
            // Cc2.printInLabel("Cc2");

            return new Matrix[]
            {
                    Vv2, Uu2, Zz2, Ss2, Cc2
            };//
        }// end
         // else{//This 'else' condition is unnecessary, but it's use to track
         // execution flow for possible bugs.
         // System.out.println(" csd : BLOCK #2");
         // }

        Matrix Vv = null;
        Matrix Uu = null;
        Matrix Zz = null;
        Matrix Ss = null;
        Matrix Cc = null;

        // gsvdU.printInLabel("Q1__");

        Matrix[] Q1R = null;

        if (test)
        {
            System.out.println("REMINDER : Comment out the following block, because it's use only as a test.");
            Q1R = fileReadExample();
            Q1 = Q1R[0];
        }

        // svdQ11.EQ(gsvdU, 1.0E-8).printInLabel("svdQ11 == gsvdU", 0);
        if (test)
        {
            System.out
                    .println("REMINDER : Change the variable 'svdQ11' below into 'Q1', because 'svdQ11' is used only as a test.");
        }

        SvdJLapack svd = new SvdJLapack(Q1);
        // Uu,Ss,Vv

        if (test)
        {
            System.out
                    .println("REMINDER : Change the variables 'svdU', 'svdC' and 'svdZ' into method calls 'svd.getU()', 'svd.getS()' and 'svd.getV()', because they're only used as a test.");
            Uu = Q1R[1];
            Cc = Q1R[2];
            Zz = Q1R[3];
        }
        else
        {
            Uu = svd.getU();
            Cc = svd.getS();
            Zz = svd.getV();
        }

        // Uu.printInLabel("U1");
        // Cc.printInLabel("C");
        // Zz.printInLabel("Z");

        // svdU.EQ(Uu, 1.0E-8).printInLabel("svdU == Uu", 0);

        int q = Math.min(mm, pp);
        Indices indQ = Indices.linspace(0, q - 1);// i = 1:q;
        int[] i = indQ.getRowPackedCopy();
        // j = q:-1:1;
        int[] j = indQ.flipLR().getRowPackedCopy();
        // Cc(i,i) = Cc(j,j);
        tmp1 = Cc.getMatrix(j, j);
        Cc.setMatrix(i, i, tmp1);
        // Uu(:,i) = Uu(:,j);
        tmp1 = Uu.getColumns(j);
        Uu.setColumns(i, tmp1);
        // Zz(:,i) = Zz(:,j);
        tmp1 = Zz.getColumns(j);
        Zz.setColumns(i, tmp1);
        Ss = Q2.times(Zz);

        /*
         * Cc.printInLabel("C"); Uu.printInLabel("U"); Zz.printInLabel("Z");
         * Ss.printInLabel("S");
         */

        int k;
        if (q == 1)
        {
            k = 0;
            // System.out.println("k #1");
        }
        else if (mm < pp)
        {
            k = nn;
            // System.out.println("k #2");
        }
        else
        {
            // k = max([0; find(diag(Cc) <= 1/sqrt(2))]);
            double sqrVal = 1.0 / Math.sqrt(2.0);
            Indices finDiC = Cc.diag().LTEQ(sqrVal).find();
            if (finDiC != null)
            {
                System.out.println("k #3");
                Indices findDC = finDiC.getColumnAt(0);
                findDC = new Indices(1, 1, 0).mergeV(findDC);
                k = JDatafun.max(findDC).start();
            }
            else
            {
                System.out.println("k #4");
                k = 0;
            }
        }// end

        i = Indices.linspace(0, k - 1).getRowPackedCopy(); // ====> can be null
                                                           // here if 'k=0'
        Matrix Skr = Ss.getColumns(i);
        // [Vv,R] = qr(Ss(:,1:k));
        QrJLapack QR = new QrJLapack(Skr);
        Vv = QR.getQ();
        // R = QR.getR();

        Ss = Vv.transpose().times(Ss);// Vv'*Ss;

        // Vv.printInLabel("V2");
        // Ss.printInLabel("S2");

        int r = Math.min(k, mm);
        i = Indices.linspace(0, r - 1).getRowPackedCopy(); // ====> can be null
                                                           // here if 'r=0'
        Skr = Ss.getColumns(i);
        // Ss(:,1:r) = diagf(Ss(:,1:r));
        Skr = diagf(Skr);
        // Skr.printInLabel("dfs");
        Ss.setColumns(i, Skr);
        // Ss.printInLabel("S3");

        if (mm == 1 && pp > 1)
        {
            // Ss(1,1) = 0;
            Ss.set(0, 0, 0.0);
        }

        // System.out.println("k = " + k);

        if (k < Math.min(nn, pp))
        {
            r = Math.min(nn, pp);
            System.out.println("nn - 1 = " + (nn - 1));
            // System.out.println("r - 1 = " + (r - 1));
            // i = k+1:nn;
            i = Indices.linspace(k, nn - 1).getRowPackedCopy(); // ====> can be
                                                                // null here if
                                                                // 'nn=0' or
                                                                // 'k>nn-1'
            // j = k+1:r;
            j = Indices.linspace(k, r - 1).getRowPackedCopy(); // ====> can be
                                                               // null here if
                                                               // 'r=0' or
                                                               // 'k>r-1'
            Skr = Ss.getMatrix(i, j);
            // [UT,ST,VT] = svd(Ss(i,j));
            svd = new SvdJLapack(Skr); // Uu,Ss,Vv
            Matrix UT = svd.getU();
            Matrix ST = svd.getS();
            Matrix VT = svd.getV();

            if (k > 0)
            {
                // Ss(1:k,j) = 0;
                int[] ind1k = Indices.linspace(0, k - 1).getRowPackedCopy(); // ====>
                                                                             // can
                                                                             // be
                                                                             // null
                                                                             // here
                                                                             // if
                                                                             // 'k=0'
                Matrix zeroMat = new Matrix(ind1k.length, j.length);
                Ss.setMatrix(ind1k, j, zeroMat);
            }

            // Ss(i,j) = ST;
            Ss.setMatrix(i, j, ST);

            // Cc(:,j) = Cc(:,j)*VT;
            Skr = Cc.getColumns(j).times(VT);
            Cc.setColumns(j, Skr);

            // Vv(:,i) = Vv(:,i)*UT;
            Skr = Vv.getColumns(i).times(UT);
            Vv.setColumns(i, Skr);

            // Zz(:,j) = Zz(:,j)*VT;
            Skr = Zz.getColumns(j).times(VT);
            Zz.setColumns(j, Skr);

            // i = k+1:q;
            i = Indices.linspace(k, q - 1).getRowPackedCopy(); // ====> can be
                                                               // null here if
                                                               // 'q=0' or
                                                               // 'k>q-1'

            // [Q,R] = qr(Cc(i,j));
            Skr = Cc.getMatrix(i, j);
            QR = new QrJLapack(Skr);
            Matrix Q = QR.getQ();
            Matrix R = QR.getR();

            // Cc(i,j) = diagf(R);
            Skr = diagf(R);
            Cc.setMatrix(i, j, Skr);

            // Uu(:,i) = Uu(:,i)*Q;
            Skr = Uu.getColumns(i).times(Q);
            Uu.setColumns(i, Skr);

        }// end

        if (mm < pp)
        { // Block test --> passed
          // Diagonalize final block of Ss and permute blocks.
          // nnz(abs(diagk(Cc,0))>10.0*(double)mm*MathUtil.EPS);
            int num1 = (diagk(Cc, 0).abs().GT(10.0 * (double) mm * MathUtil.EPS)).nnz();// nnz(abs(diagk(Cc,0))>10.0*(double)mm*MathUtil.EPS);
            int num2 = (diagk(Ss, 0).abs().GT(10.0 * (double) nn * MathUtil.EPS)).nnz();// nnz(abs(diagk(Ss,0))>10.0*(double)nn*MathUtil.EPS);
            q = Math.min(num1, num2);
            // System.out.println("q = " + q);
            /*
             * if (true) { //q =
             * min(nnz(abs(diagk(Cc,0))>10*mm*eps(class(Cc))),nnz
             * (abs(diagk(Ss,0))>10*nn*eps(class(Cc)))); throw new
             * ConditionalException("Block not yet implemented."); }
             */
            // i = q+1:nn;
            Indices II = Indices.linspace(q, nn - 1);
            // II.printInLabel("i",0);
            i = II.getRowPackedCopy(); // ====> can be null here if 'nn=0' or
                                       // 'q>nn-1'
            // j = mm+1:pp;
            Indices JJ = Indices.linspace(mm, pp - 1);
            // JJ.printInLabel("j",0);
            j = JJ.getRowPackedCopy(); // ====> can be null here if 'pp=0' or
                                       // 'mm>pp-1'

            // At this point, Ss(i,j) should have orthogonal columns and the
            // elements of Ss(:,q+1:pp) outside of Ss(i,j) should be negligible.
            Skr = Ss.getMatrix(i, j);
            // Skr.printInLabel("S(i,j)");
            // [Q,R] = qr(Ss(i,j));
            QR = new QrJLapack(Skr);
            Matrix Q = QR.getQ();
            Matrix R = QR.getR();
            // Q.printInLabel("QQ");
            // R.printInLabel("RR");
            // Ss.printInLabel("SS1");
            Indices QPP = Indices.linspace(q, pp - 1);
            // QPP.printInLabel("QPP",0);
            int[] qp = QPP.getRowPackedCopy(); // ====> can be null here if
                                               // 'pp=0' or 'q>pp-1'
            // Ss(:,q+1:pp) = 0;
            Matrix zeros = new Matrix(Ss.getRowDimension(), qp.length);
            Ss.setColumns(qp, zeros);
            // Ss.printInLabel("SS2");

            // Ss(i,j) = diagf(R);
            Skr = diagf(R);
            // Skr.printInLabel("diagf(R)");
            Ss.setMatrix(i, j, Skr);
            // Ss.printInLabel("SS3");

            // Vv(:,i) = Vv(:,i)*Q;
            Skr = Vv.getColumns(i).times(Q);
            Vv.setColumns(i, Skr);
            // Vv.printInLabel("Vv3");

            if (nn > 1)
            { // Block test --> passed
              // i = [q+1:q+pp-mm 1:q q+pp-mm+1:nn];
                Indices i1 = Indices.linspace(q, q + pp - mm - 1);

                Indices i2 = null;
                if ((q - 1) >= 0)
                {
                    i2 = Indices.linspace(0, q - 1);
                }
                Indices i3 = null;
                if ((nn - 1) >= (q + pp - mm))
                {
                    i3 = Indices.linspace(q + pp - mm, nn - 1);
                }
                Indices i4 = i1.mergeHoriz(i2, i3);
                // i4.plus(1).printInLabel("i");
                i = i4.getRowPackedCopy();

            }
            else
            {
                i = new int[]
                {
                    0
                };// 1;
            }// end

            // j = [mm+1:pp 1:mm];
            JJ = Indices.linspace(mm, pp - 1).mergeH(Indices.linspace(0, mm - 1));
            // JJ.plus(1).printInLabel("j");
            j = JJ.getRowPackedCopy();

            Cc = Cc.getColumns(j);// Cc(:,j);
            // Cc.printInLabel("Cc#");
            Ss = Ss.getMatrix(i, j);// Ss(i,j);
            // Ss.printInLabel("Ss#");
            Zz = Zz.getColumns(j);// Zz(:,j);
            // Zz.printInLabel("Zz#");
            Vv = Vv.getColumns(i);// Vv(:,i);
            // Vv.printInLabel("Vv#");
        }// end

        if (nn < pp)
        { // Block test --> passed
          // Final block of Ss is negligible.
            Indices NP = Indices.linspace(nn, pp - 1);
            int[] arr = NP.getRowPackedCopy();
            // Ss(:,nn+1:pp) = 0;
            Matrix zeros = new Matrix(Ss.getRowDimension(), arr.length);
            Ss.setColumns(arr, zeros);
            // Ss.printInLabel("Ss#nnpp");
        }// end

        // Make sure Cc and Ss are real and positive.
        // [Uu,Cc] = diagp(Uu,Cc,max(0,pp-mm)); Cc = real(Cc);
        Matrix[] matArr = diagp(Uu, Cc, Math.max(0, pp - mm));
        Uu = matArr[0];
        Cc = matArr[1];

        // Uu.printInLabel("Uu - diagp");
        // Cc.printInLabel("Cc - diagp");

        // [Vv,Ss] = diagp(Vv,Ss,0); Ss = real(Ss);
        matArr = diagp(Vv, Ss, 0);
        Vv = matArr[0];
        // Vv.printInLabel("Vv - csd");
        Ss = matArr[1];
        // Ss.printInLabel("Ss - csd");

        return new Matrix[]
        {
                Uu, Vv, Zz, Cc, Ss
        };
    }

    private Matrix diagk(Matrix X, int k)
    {
        // DIAGK K-th matrix diagonal.
        // DIAGK(X,k) is the k-th diagonal of X, even if X is a vector.
        int rowX = X.getRowDimension();
        int colX = X.getColumnDimension();
        Matrix D = null;
        if (Math.min(rowX, colX) > 1)
        {// min(size(X)) > 1
            D = X.diag(k);// diag(X,k);
        }
        else if (0 <= k && (1 + k) <= colX)
        {// size(X,2)
            D = new Matrix(1, 1, X.getElementAt(1 + k));// X(1+k);
        }
        else if (k < 0 && (1 - k) <= rowX)
        {// size(X,1)
            D = new Matrix(1, 1, X.getElementAt(1 - k));// X(1-k);
        }
        return D;
    }

    private Matrix diagf(Matrix X)
    {
        // DIAGF Diagonal force.
        // X = DIAGF(X) zeros all the elements off the main diagonal of X.
        // X = triu(tril(X));
        Matrix ttX = X.tril().triu();
        return ttX;
    }

    private Matrix[] diagp(Matrix Y, Matrix X, int k)
    {
        // DIAGP Diagonal positive.
        // [Y,X] = diagp(Y,X,k) scales the columns of Y and the rows of X by
        // unimodular factors to make the k-th diagonal of X real and positive.

        // Y.printInLabel("Y - diagp");
        // X.printInLabel("X - diagp");
        Matrix D = diagk(X, k);
        // D.printInLabel("D1");
        Indices j = D.LT(0.0).find();// find(real(D) < 0 | imag(D) ~= 0);
        Matrix Yc = Y.copy();
        Matrix Xc = X.copy();

        if (j != null)
        {// Block test passed
         // System.out.println(" j != NULL");
         // j.printInLabel("j");
            int[] indArr = j.getColIndicesAt(0);
            Matrix Dj = D.getFromFind(j);
            // Dj.printInLabel("Dj");
            // D = diag(conj(D(j))./abs(D(j)));
            D = Dj.arrayRightDivide(Dj.abs()).diag();
            // D.printInLabel("D2");
            // Y(:,j) = Y(:,j)*D';
            Matrix temp = Yc.getColumns(indArr).times(D.transpose());
            Yc.setColumns(indArr, temp);
            // Yc.printInLabel("Yc");
            // X(j,:) = D*X(j,:);
            temp = D.times(Xc.getRows(indArr));
            Xc.setRows(indArr, temp);
            // Xc.printInLabel("Xc");
        }
        return new Matrix[]
        {
                Yc, Xc
        };
    }

    /**
     * @return the gsvdV
     */
    public Matrix getGsvU()
    {
        int q = Math.min(m + n, p);
        Matrix mat1 = Matrix.zeros(q - m, 1);
        Matrix mat2 = diagk(C, Math.max(0, q - m));
        Matrix matNum = mat1.mergeV(mat2);
        Matrix mat3 = diagk(S, 0);
        Matrix mat4 = Matrix.zeros(q - n, 1);
        Matrix matDen = mat3.mergeV(mat4);
        // gsvdV = [zeros(q-mm,1,superiorfloat(A,B));
        // diagk(C,max(0,q-mm))]./[diagk(S,0); zeros(q-nn,1)];
        Matrix gsvU = matNum.arrayRightDivide(matDen);
        return gsvU;
    }

    /**
     * @return the V
     */
    public Matrix getV()
    {
        Matrix V2 = V;
        if (QB != null)
        {
            V2 = QB.times(V);
        }
        return V2;
    }

    /**
     * @return the gsvdV
     */
    public Matrix getU()
    {
        Matrix U2 = U;
        if (QA != null)
        {
            U2 = QA.times(U);
        }
        return U2;
    }

    /**
     * @return the S
     */
    public Matrix getS()
    {
        return S;
    }

    /**
     * @return the C
     */
    public Matrix getC()
    {
        return C;
    }

    /**
     * @return the X
     */
    public Matrix getX()
    {
        return X;
    }

    static Matrix[] fileReadExample()
    {
        BufferedReader svdQ11BufReader = null;
        BufferedReader svdUBufReader = null;
        BufferedReader svdCBufReader = null;
        BufferedReader svdZBufReader = null;

        String filePath = "C:/Users/Sione/Documents/MATLAB/datafiles/";

        System.out.println("### Begin File Reading ###");

        try
        {
            svdQ11BufReader = new BufferedReader(new FileReader(filePath + "svdQ1.txt"));
            svdUBufReader = new BufferedReader(new FileReader(filePath + "svdU.txt"));
            svdCBufReader = new BufferedReader(new FileReader(filePath + "svdC.txt"));
            svdZBufReader = new BufferedReader(new FileReader(filePath + "svdZ.txt"));

        }
        catch (FileNotFoundException fne)
        {
            fne.printStackTrace();
        }

        System.out.println("### File Reading Successful ###");

        Matrix svdQ11 = null;
        Matrix svdU = null;
        Matrix svdC = null;
        Matrix svdZ = null;

        try
        {
            svdQ11 = Matrix.read(svdQ11BufReader);
            svdU = Matrix.read(svdUBufReader);
            svdC = Matrix.read(svdCBufReader);
            svdZ = Matrix.read(svdZBufReader);

        }
        catch (IOException io)
        {
            io.printStackTrace();
        }

        System.out.println("### Matrix Reading Successful ###");

        return new Matrix[]
        {
                svdQ11, svdU, svdC, svdZ
        };
    }

    static Matrix[] fileReadGsvdInput()
    {
        BufferedReader gsvdAread = null;
        BufferedReader gsvdBread = null;

        String filePath = "C:/Users/Sione/Documents/MATLAB/datafiles/";

        System.out.println("### Begin File Reading ###");

        try
        {
            gsvdAread = new BufferedReader(new FileReader(filePath + "gsvdA.txt"));
            gsvdBread = new BufferedReader(new FileReader(filePath + "gsvdB.txt"));

        }
        catch (FileNotFoundException fne)
        {
            fne.printStackTrace();
        }

        System.out.println("### File Reading Successful ###");

        Matrix gsvdA = null;
        Matrix gsvdB = null;

        try
        {
            gsvdA = Matrix.read(gsvdAread);
            gsvdB = Matrix.read(gsvdBread);

        }
        catch (IOException io)
        {
            io.printStackTrace();
        }

        System.out.println("### Matrix Reading Successful ###");

        return new Matrix[]
        {
                gsvdA, gsvdB
        };
    }

    static Matrix[] fileReadGsvdOutput()
    {
        // svdU,V,X,C,S
        BufferedReader gsvdUread = null;
        BufferedReader gsvdVread = null;
        BufferedReader gsvdXread = null;
        BufferedReader gsvdCread = null;
        BufferedReader gsvdSread = null;

        String filePath = "C:/Users/Sione/Documents/MATLAB/datafiles/";

        System.out.println("\n### Begin File Reading ###");

        try
        {
            gsvdUread = new BufferedReader(new FileReader(filePath + "gsvdU.txt"));
            gsvdVread = new BufferedReader(new FileReader(filePath + "gsvdV.txt"));
            gsvdXread = new BufferedReader(new FileReader(filePath + "gsvdX.txt"));
            gsvdCread = new BufferedReader(new FileReader(filePath + "gsvdC.txt"));
            gsvdSread = new BufferedReader(new FileReader(filePath + "gsvdS.txt"));

        }
        catch (FileNotFoundException fne)
        {
            fne.printStackTrace();
        }

        System.out.println("\n### File Reading Successful ###\n");

        Matrix gsvdU = null;
        Matrix gsvdV = null;
        Matrix gsvdX = null;
        Matrix gsvdC = null;
        Matrix gsvdS = null;

        try
        {
            gsvdU = Matrix.read(gsvdUread);
            gsvdV = Matrix.read(gsvdVread);
            gsvdX = Matrix.read(gsvdXread);
            gsvdC = Matrix.read(gsvdCread);
            gsvdS = Matrix.read(gsvdSread);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }

        System.out.println("### Matrix Reading Successful ###\n");

        return new Matrix[]
        {
                gsvdU, gsvdV, gsvdX, gsvdC, gsvdS
        };
    }

    public static enum Econ
    {

        ZERO;
    }

    public static void main(String[] args)
    {
        /*
         * double[][] a = {{41, 14, 48, 40, 34, 35}, {45, 27, 24, 48, 38, 2},
         * {6, 48, 40, 33, 37, 14}, {46, 48, 7, 2, 20, 2}, {32, 8, 21, 42, 33,
         * 5}, {5, 49, 46, 47, 9, 41}}; Matrix A = new Matrix(a);
         * 
         * double[][] b = {{35, 22, 9, 35, 33, 48}, {16, 19, 24, 38, 8, 17},
         * {48, 38, 22, 14, 6, 29}, {2, 40, 32, 34, 25, 11}}; Matrix B = new
         * Matrix(b);
         */

        Matrix[] ab = fileReadGsvdInput();
        Matrix gsvdA = ab[0];
        Matrix gsvdB = ab[1];

        GeneralizedSVD gsvd = new GeneralizedSVD(gsvdA, gsvdB, QrJLapack.Economy.ZERO);
        Matrix U = gsvd.getU();
        Matrix V = gsvd.getV();
        Matrix X = gsvd.getX();
        Matrix C = gsvd.getC();
        Matrix S = gsvd.getS();

        if (true)
        {
            // return;
        }

        Matrix[] UVXCS = fileReadGsvdOutput();
        // svdU,V,X,C,S
        Matrix gsvdU = UVXCS[0];
        // gsvdU.printInLabel("gsvdU");
        // U.printInLabel("U");

        Matrix gsvdV = UVXCS[1];
        // gsvdV.printInLabel("gsvdV");
        // V.printInLabel("V");

        Matrix gsvdX = UVXCS[2];
        // gsvdX.printInLabel("gsvdX");
        // X.printInLabel("X");

        Matrix gsvdC = UVXCS[3];

        Matrix gsvdS = UVXCS[4];

        // if(true){ return;}

        double tol = 1.0E-6;

        if (gsvdU.EQ(U, tol).allBoolean())
        {
            System.out.println("gsvdU == U");
        }
        else
        {
            System.out.println("gsvdU NOT = U");
        }

        if (gsvdV.EQ(V, tol).allBoolean())
        {
            System.out.println("gsvdV == V");
        }
        else
        {
            System.out.println("gsvdV NOT = V");
        }

        if (gsvdX.EQ(X, tol).allBoolean())
        {
            System.out.println("gsvdX == X");
        }
        else
        {
            System.out.println("gsvdX NOT = X");
        }

        if (gsvdC.EQ(C, tol).allBoolean())
        {
            System.out.println("gsvdC == C");
        }
        else
        {
            System.out.println("gsvdC NOT = C");
        }

        if (gsvdS.EQ(S, tol).allBoolean())
        {
            System.out.println("gsvdS == S");
        }
        else
        {
            System.out.println("gsvdS NOT = S");
        }

        /*
         * svdU.printInLabel("svdU"); V.printInLabel("V"); X.printInLabel("X");
         * C.printInLabel("C"); S.printInLabel("S");
         */

    }
}
