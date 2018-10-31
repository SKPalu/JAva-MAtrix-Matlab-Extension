package jamaextension.jamax.matfun;

import jamaextension.jamax.Indices;
import jamaextension.jamax.MathUtil;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.SingularValueDecomposition;
import jamaextension.jamax.SvdJLapack;
import jamaextension.jamax.constants.Dimension;
import jamaextension.jamax.datafun.JDatafun;
import jamaextension.jamax.vnijmsl.QR;

/**
 * <p>
 * Title: Extended JAMA
 * </p>
 * <p>
 * Description: Extension of JAMA package with more functionalities to be as
 * close to MatLab
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: FP
 * </p>
 * 
 * @author Sione Palu
 * @version 1.0
 */
// import com.feynmanperceptron.derived.numeric.QR;
public final class JMatfun
{

    private JMatfun()
    {
    }

    /**
     * %PINV Pseudoinverse. X = PINV(A) produces a matrix X of the same
     * dimensions as A' so that A*X*A = A, X*A*X = X and A*X and X*A are
     * Hermitian. The computation is based on SVD(A) and any singular values
     * less than a tolerance are treated as zero. The default tolerance is
     * MAX(SIZE(A)) * NORM(A) * EPS.
     */
    public static Matrix pinv(Matrix A, Double tol)
    {

        int m = A.getRowDimension();
        int n = A.getColumnDimension();

        // The following block must be removed if it causes infinite recursion
        // call.
        if (n > m)
        {
            return pinv(A.transpose(), tol).transpose();
        }

        SingularValueDecomposition svdA = new SingularValueDecomposition(A);
        Matrix U = svdA.getU();
        Matrix S = svdA.getS();
        Matrix V = svdA.getV();

        Matrix s = null;

        if (m > 1)
        {
            s = S.diag();
        }
        else if (m == 1)
        {
            s = new Matrix(1, 1, S.get(0, 0));
        }
        else
        {
            s = new Matrix(1, 1);
        }

        Matrix X = null, temp = null, temp2 = null;
        double val = 0.0, tolVal = 0.0;

        if (tol == null)
        {
            tolVal = // JDatafun.max( new Matrix(new
                     // double[][]{{m,n}})).get(0,0)*
            Math.max(m, n) * JDatafun.max(s).get(0, 0) * MathUtil.EPS;
        }
        else
        {
            tolVal = tol.doubleValue();
        }

        int r = s.GT(tolVal).sumAll();// JDatafun.sum(s.GT(tolVal)).get(0,0);//sum(s
                                      // > tol);
        if (r == 0)
        {
            // X = zeros(size(A'));
            X = new Matrix(n, m);
        }
        else
        {
            temp = new Matrix(r, 1);
            for (int i = 0; i <= (r - 1); i++)
            {
                val = s.getElementAt(i);
                temp.set(i, 0, 1.0 / val);
            }
            // s = diag(ones(r,1)./s(1:r));
            s = temp.diag();

            int mV = V.getRowDimension();
            int mU = U.getRowDimension();
            temp = V.getMatrix(0, mV - 1, 0, r - 1);
            temp2 = U.getMatrix(0, mU - 1, 0, r - 1).transpose();

            // X = V(:,1:r)*s*U(:,1:r)';
            X = temp.times(s).times(temp2);
        }
        return X;
    }// end method

    public static Matrix pinv(Matrix A)
    {
        return pinv(A, null);
    }

    /**
     * TRACE Sum of diagonal elements. TRACE(A) is the sum of the diagonal
     * elements of A, which is also the sum of the eigenvalues of A.
     */
    public static double trace(Matrix A)
    {
        if (A.isVector())
        {
            return JDatafun.sum(A).get(0, 0);
        }
        return JDatafun.sum(A.diag()).get(0, 0);
    }// end method

    /**
     * ORTH Orthogonalization. Q = ORTH(A) is an orthonormal basis for the range
     * of A. That is, Q'*Q = I, the columns of Q span the same space as the
     * columns of A, and the number of columns of Q is the rank of A.
     */
    public static Matrix orth(Matrix A)
    {
        SingularValueDecomposition svdA = new SingularValueDecomposition(A);
        Matrix U = svdA.getU();
        Matrix S = svdA.getS();
        // Matrix V = svdA.getV();

        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        Matrix s = null;

        if (m > 1)
        {
            s = S.diag();
        }
        else if (m == 1)
        {
            s = new Matrix(1, 1, S.get(0, 0));
        }
        else
        {
            s = new Matrix(1, 1);
        }

        // tol = max(m,n) * max(s) * eps;
        double tol = JDatafun.max(new Matrix(new double[][]
        {
            {
                    m, n
            }
        })).get(0, 0) * JDatafun.max(s).get(0, 0) * MathUtil.EPS;
        // r = sum(s > tol);
        int r = s.GT(tol).sumAll();// JDatafun.sum(s.GT(tol)).get(0,0);
        int mU = U.getRowDimension();
        // Matrix Q = U(:,1:r);
        Matrix Q = U.getMatrix(0, mU - 1, 0, r - 1);

        return Q;
    }// end method

    /*
     * PLANEROT Givens plane rotation. [G,Y] = PLANEROT(X), where X is a
     * 2-component column vector, returns a 2-by-2 orthogonal matrix G so that Y
     * = G*X has Y(2) = 0.
     */
    public static Matrix[] planerot(Matrix X)
    {
        /*------------------------------------------------------*
         double[][] x = {{3, 0}};
         Matrix A = new Matrix(x);
         Matrix[] Gx = planerot(A);
        
         System.out.println("\n------- G --------");
         Gx[0].print(4,4);
         System.out.println("\n------- x --------");
         Gx[1].print(4,4);
         *------------------------------------------------------*/
        if (!X.isVector())
        {
            throw new IllegalArgumentException(" planerot :  Parameter \"X\" must be a vector and not a matrix.");
        }
        if (X.length() != 2)
        {
            throw new IllegalArgumentException(" planerot :  Parameter \"Q\" must have a length of 2 elements.");
        }
        Matrix G = null;
        Matrix x = null;

        if (X.isRowVector())
        {
            x = X.toColVector();
        }
        else
        {
            x = X.copy();
        }

        if (x.getElementAt(1) != 0.0)
        {
            double r = x.norm2();
            // G = [x'; -x(2) x(1)]/r;
            Matrix temp = new Matrix(new double[][]
            {
                {
                        -x.get(1, 0), x.get(0, 0)
                }
            });
            G = x.transpose().mergeV(temp).arrayRightDivide(r);
            // x = [r; 0];
            temp = Matrix.zeros(2, 1);
            temp.set(0, 0, r);
            x = temp;
        }
        else
        {
            G = Matrix.identity(2);// eye(2);
        }

        return new Matrix[]
        {
                G, x
        };
    }

    /*
     * %QRDELETE Delete a column or row from QR factorization. % [Q1,R1] =
     * QRDELETE(Q,R,J) returns the QR factorization of the matrix A1, % where A1
     * is A with the column A(:,J) removed and [Q,R] = QR(A) is the QR %
     * factorization of A. Matrices Q and R can also be generated by % the
     * "economy size" QR factorization [Q,R] = QR(A,0). % %
     * QRDELETE(Q,R,J,'col') is the same as QRDELETE(Q,R,J). % % [Q1,R1] =
     * QRDELETE(Q,R,J,'row') returns the QR factorization of the matrix % A1,
     * where A1 is A with the row A(J,:) removed and [Q,R] = QR(A) is the QR %
     * factorization of A. % ==> The index , which is j=3 ,must be a positive
     * number, ie, j>=1
     * 
     * % Example: % A = magic(5); [Q,R] = qr(A); % j = 3; % [Q1,R1] =
     * qrdelete(Q,R,j,'row'); % returns a valid QR factorization, although
     * possibly different from % A2 = A; A2(j,:) = []; % [Q2,R2] = qr(A2);
     */
    public static Matrix[] qrdelete_bk(Matrix Q, Matrix R, int j, Dimension orient)
    {

        int mq = Q.getRowDimension();
        int nq = Q.getColumnDimension();
        // [m,n] = size(R);
        int m = R.getRowDimension();
        int n = R.getColumnDimension();

        if ((orient != Dimension.COL) && (mq != nq))
        {
            // error('MATLAB:qrdelete:QNotSquare','Q must be square when
            // deleting a row.')
            throw new IllegalArgumentException(" qrdelete :  Parameter \"Q\" must be square when deleting a row.");
        }
        else if (nq != m)
        {
            // error('MATLAB:qrdelete:InnerDimQRfactors', 'Inner matrix
            // dimensions of QR factors must agree.')
            throw new IllegalArgumentException(" qrdelete :  Inner matrix dimensions of QR factors must agree.");
        }
        else if (j <= 0)
        {
            // error('MATLAB:qrdelete:NegDeletionIndex', 'Deletion index must be
            // positive.')
            throw new IllegalArgumentException(" qrdelete :  Deletion index must be positive.");
        }

        int[] p = null;
        int[] pk = null;
        Matrix temp = null;
        Matrix G = null;
        Indices indices = null;

        if (orient == Dimension.COL)
        {// -----------------------------------------------
            if (j > n)
            {
                // error('MATLAB:qrdelete:InvalidDelIndex', 'Deletion index
                // exceeds matrix dimensions.')
                throw new IllegalArgumentException(" qrdelete :  Deletion index exceeds matrix dimensions.");
            }
            // Remove the j-th column. n = number of columns in modified R.
            R = R.removeColAt(j - 1);// R(:,j) = [];
            // System.out.println("\n------- R --------");
            // R.print(4,4);
            // [m,n] = size(R);
            m = R.getRowDimension();
            n = R.getColumnDimension();

            /*
             * R now has nonzeros below the diagonal in columns j through n. % R
             * = [x | x x x [x x x x % 0 | x x x 0 * * x % 0 | + x x G 0 0 * * %
             * 0 | 0 + x ---> 0 0 0 * % 0 | 0 0 + 0 0 0 0 % 0 | 0 0 0] 0 0 0 0]
             * % Use Givens rotations to zero the +'s, one at a time, from left
             * to right.
             */
            for (int k = j - 1; k < Math.min(n, m - 1); k++)
            {
                // p = k:k+1;
                indices = Indices.linspace(k, k + 1, 1);
                // System.out.println("\n------- p --------");
                // indices.print(4);

                p = indices.getColumnPackedCopy();
                temp = R.getMatrix(p, k);
                // System.out.println("\n------- Rpk --------");
                // temp.print(4,4);

                // [G,R(p,k)] = planerot(R(p,k));
                Matrix[] GR = planerot(temp);
                G = GR[0];
                Matrix Rpk = GR[1];
                R.setMatrix(p, k, Rpk);

                // System.out.println("\n k = "+k+"  :   n = "+(n-1));
                if (k < n - 1)
                {
                    // R(p,k+1:n) = G*R(p,k+1:n);
                    pk = Indices.linspace(k + 1, n - 1).getRowPackedCopy();
                    temp = R.getMatrix(p, pk);

                    // System.out.println("\n------- Rpkn --------");
                    // temp.print(4,4);
                    temp = G.times(temp);
                    R.setMatrix(p, pk, temp);
                }

                // Q(:,p) = Q(:,p)*G';
                temp = Q.getColumns(p).times(G.transpose());
                Q.setColumns(p, temp);

            }// end for loop

            // If Q is not square, Q is from economy size QR(A,0).
            // Both Q and R need further adjustments.
            if (mq != nq)
            {
                // R(m,:)=[];
                R = R.removeRowAt(m - 1);
                // Q(:,nq)=[];
                Q = Q.removeColAt(nq - 1);
            }
            /*
             * System.out.println("\n------- Q --------"); Q.print(4,4);
             * System.out.println("\n------- R --------"); R.print(4,4);
             */
        }
        else
        {// ---------------------------------------------------------------------
            if (j > m)
            {
                // error('MATLAB:qrdelete:InvalidDelIndex', 'Deletion index
                // exceeds matrix dimensions.')
                throw new IllegalArgumentException(" qrdelete :  Deletion index exceeds matrix dimensions.");
            }

            // This permutes row j of Q*R to row 1 of Q(p,:)*R
            if (j != 1)
            {// j ~= 1
             // p = [j, 1:j-1, j+1:m];
                Indices I1 = Indices.linspace(0, j - 2, 1);
                Indices I2 = Indices.linspace(j, m - 1, 1);
                indices = (new Indices(1, 1, j - 1)).mergeH(I1).mergeH(I2);

                // System.out.println("\n------- p --------");
                // indices.print(4);
                p = indices.getRowPackedCopy();
                // Q = Q(p,:);
                Q = Q.getRows(p);

                // System.out.println("\n------- Q --------");
                // Q.print(4,4);
            }

            Matrix q = Q.getRowAt(0).transpose();// Q(1,:)';
            // System.out.println("\n------- q --------");
            // q.print(4,4);

            /*
             * q is the transpose of the first row of Q. % q = [x [1 % - - % + G
             * 0 % + ---> 0 % + 0 % + 0 % +] 0] % % Use Givens rotations to zero
             * the +'s, one at a time, from bottom to top. % The result will
             * have a "1" in the first entry. % % Apply the same rotations to R,
             * which becomes upper Hessenberg. % R = [x x x x [* * * * % -------
             * ------- % x x x G * * * * % x x ---> * * * % x * * % 0 0 0 0 * %
             * 0 0 0 0] 0 0 0 0] % % Under (the transpose of) the same
             * rotations, Q becomes % Q = [x | x x x x x [1 | 0 0 0 0 0 %
             * --|---------- --|---------- % x | x x x x x G' 0 | * * * * * % x
             * | x x x x x ---> 0 | * * * * * % x | x x x x x 0 | * * * * * % x
             * | x x x x x 0 | * * * * * % x | x x x x x] 0 | * * * * *]
             * --------------------------------------------------------
             */
            for (int i = (m - 1); i >= 1; i--)
            {
                // p = i-1 : i;
                p = new int[]
                {
                        i - 1, i
                };
                // System.out.println("\n------- p --------"); //qsubp =
                // indices = new Indices(p);
                // indices.print(4);

                // [G,q(p)] = planerot(q(p));
                temp = q.getMatrix(p, 0);
                // System.out.println("\n------- qsubp --------");
                // temp.print(4,4);

                Matrix[] Gq = planerot(temp);
                G = Gq[0];
                Matrix qp = Gq[1];
                q.setMatrix(p, 0, qp);
                // R(p,i-1:n) = G * R(p,i-1:n);
                pk = Indices.linspace(i - 1, n - 1, 1).getRowPackedCopy();
                temp = G.times(R.getMatrix(p, pk));
                R.setMatrix(p, pk, temp);
                // Q(:,p) = Q(:,p) * G';
                temp = Q.getColumns(p).times(G.transpose());
                Q.setColumns(p, temp);
            }

            /*
             * System.out.println("\n------- Q --------"); Q.print(4,4);
             * System.out.println("\n------- R --------"); R.print(4,4);
             */
            // The boxed off (---) parts of Q and R are the desired factors.
            m = Q.getRowDimension();
            n = Q.getColumnDimension();
            // Q = Q(2:end,2:end);
            Q = Q.getMatrix(1, m - 1, 1, n - 1);
            // R(1,:) = [];
            R = R.removeRowAt(0);

            /*
             * System.out.println("\n------- Q --------"); Q.print(4,4);
             * System.out.println("\n------- R --------"); R.print(4,4);
             */
        }// ------------------------------------------------------------------------

        return new Matrix[]
        {
                Q, R
        };
    }

    public static Matrix[] qrdelete_bk(Matrix Q, Matrix R, int j)
    {
        return qrdelete_bk(Q, R, j, Dimension.COL);
    }

    /*
     * %QRDELETE Delete a column or row from QR factorization. % [Q1,R1] =
     * QRDELETE(Q,R,J) returns the QR factorization of the matrix A1, % where A1
     * is A with the column A(:,J) removed and [Q,R] = QR(A) is the QR %
     * factorization of A. Matrices Q and R can also be generated by % the
     * "economy size" QR factorization [Q,R] = QR(A,0). % %
     * QRDELETE(Q,R,J,'col') is the same as QRDELETE(Q,R,J). % % [Q1,R1] =
     * QRDELETE(Q,R,J,'row') returns the QR factorization of the matrix % A1,
     * where A1 is A with the row A(J,:) removed and [Q,R] = QR(A) is the QR %
     * factorization of A. % ==> The index , which is j=3 ,must be a
     * non-negative number, ie, j>=0 (array index in matlab starts at zero)
     * 
     * % Example: % A = magic(5); [Q,R] = qr(A); % j = 3; % [Q1,R1] =
     * qrdelete(Q,R,j,'row'); % returns a valid QR factorization, although
     * possibly different from % A2 = A; A2(j,:) = []; % [Q2,R2] = qr(A2);
     */
    public static Matrix[] qrdelete(Matrix Q, Matrix R, int index, Dimension orient)
    {

        // ------- Modified index 'j' here, because Java index is less by one as
        // in matlab.
        int j = index + 1;

        int mq = Q.getRowDimension();
        int nq = Q.getColumnDimension();
        // [m,n] = size(R);
        int m = R.getRowDimension();
        int n = R.getColumnDimension();

        if ((orient != Dimension.COL) && (mq != nq))
        {
            // error('MATLAB:qrdelete:QNotSquare','Q must be square when
            // deleting a row.')
            throw new IllegalArgumentException(" qrdelete :  Parameter \"Q\" must be square when deleting a row.");
        }
        else if (nq != m)
        {
            // error('MATLAB:qrdelete:InnerDimQRfactors', 'Inner matrix
            // dimensions of QR factors must agree.')
            throw new IllegalArgumentException(" qrdelete :  Inner matrix dimensions of QR factors must agree.");
        }
        else if (j <= 0)
        {
            // error('MATLAB:qrdelete:NegDeletionIndex', 'Deletion index must be
            // positive.')
            throw new IllegalArgumentException(" qrdelete :  Deletion index ( = " + index + ") must be non-negative.");
        }

        int[] p = null;
        int[] pk = null;
        Matrix temp = null;
        Matrix G = null;
        Indices indices = null;

        if (orient == Dimension.COL)
        {// -----------------------------------------------
            if (j > n)
            {
                // error('MATLAB:qrdelete:InvalidDelIndex', 'Deletion index
                // exceeds matrix dimensions.')
                throw new IllegalArgumentException(" qrdelete :  Deletion index exceeds matrix dimensions.");
            }
            // Remove the j-th column. n = number of columns in modified R.
            R = R.removeColAt(j - 1);// R(:,j) = [];
            // System.out.println("\n------- R --------");
            // R.print(4,4);
            // [m,n] = size(R);
            m = R.getRowDimension();
            n = R.getColumnDimension();

            /*
             * R now has nonzeros below the diagonal in columns j through n. % R
             * = [x | x x x [x x x x % 0 | x x x 0 * * x % 0 | + x x G 0 0 * * %
             * 0 | 0 + x ---> 0 0 0 * % 0 | 0 0 + 0 0 0 0 % 0 | 0 0 0] 0 0 0 0]
             * % Use Givens rotations to zero the +'s, one at a time, from left
             * to right.
             */
            for (int k = j - 1; k < Math.min(n, m - 1); k++)
            {
                // p = k:k+1;
                indices = Indices.linspace(k, k + 1, 1);
                // System.out.println("\n------- p --------");
                // indices.print(4);

                p = indices.getColumnPackedCopy();
                temp = R.getMatrix(p, k);
                // System.out.println("\n------- Rpk --------");
                // temp.print(4,4);

                // [G,R(p,k)] = planerot(R(p,k));
                Matrix[] GR = planerot(temp);
                G = GR[0];
                Matrix Rpk = GR[1];
                R.setMatrix(p, k, Rpk);

                // System.out.println("\n k = "+k+"  :   n = "+(n-1));
                if (k < n - 1)
                {
                    // R(p,k+1:n) = G*R(p,k+1:n);
                    pk = Indices.linspace(k + 1, n - 1).getRowPackedCopy();
                    temp = R.getMatrix(p, pk);

                    // System.out.println("\n------- Rpkn --------");
                    // temp.print(4,4);
                    temp = G.times(temp);
                    R.setMatrix(p, pk, temp);
                }

                // Q(:,p) = Q(:,p)*G';
                temp = Q.getColumns(p).times(G.transpose());
                Q.setColumns(p, temp);

            }// end for loop

            // If Q is not square, Q is from economy size QR(A,0).
            // Both Q and R need further adjustments.
            if (mq != nq)
            {
                // R(m,:)=[];
                R = R.removeRowAt(m - 1);
                // Q(:,nq)=[];
                Q = Q.removeColAt(nq - 1);
            }
            /*
             * System.out.println("\n------- Q --------"); Q.print(4,4);
             * System.out.println("\n------- R --------"); R.print(4,4);
             */
        }
        else
        {// ---------------------------------------------------------------------
            if (j > m)
            {
                // error('MATLAB:qrdelete:InvalidDelIndex', 'Deletion index
                // exceeds matrix dimensions.')
                throw new IllegalArgumentException(" qrdelete :  Deletion index exceeds matrix dimensions.");
            }

            // This permutes row j of Q*R to row 1 of Q(p,:)*R
            if (j != 1)
            {// j ~= 1
             // p = [j, 1:j-1, j+1:m];
                Indices I1 = Indices.linspace(0, j - 2, 1);
                Indices I2 = Indices.linspace(j, m - 1, 1);
                indices = (new Indices(1, 1, j - 1)).mergeH(I1).mergeH(I2);

                // System.out.println("\n------- p --------");
                // indices.print(4);
                p = indices.getRowPackedCopy();
                // Q = Q(p,:);
                Q = Q.getRows(p);

                // System.out.println("\n------- Q --------");
                // Q.print(4,4);
            }

            Matrix q = Q.getRowAt(0).transpose();// Q(1,:)';
            // System.out.println("\n------- q --------");
            // q.print(4,4);

            /*
             * q is the transpose of the first row of Q. % q = [x [1 % - - % + G
             * 0 % + ---> 0 % + 0 % + 0 % +] 0] % % Use Givens rotations to zero
             * the +'s, one at a time, from bottom to top. % The result will
             * have a "1" in the first entry. % % Apply the same rotations to R,
             * which becomes upper Hessenberg. % R = [x x x x [* * * * % -------
             * ------- % x x x G * * * * % x x ---> * * * % x * * % 0 0 0 0 * %
             * 0 0 0 0] 0 0 0 0] % % Under (the transpose of) the same
             * rotations, Q becomes % Q = [x | x x x x x [1 | 0 0 0 0 0 %
             * --|---------- --|---------- % x | x x x x x G' 0 | * * * * * % x
             * | x x x x x ---> 0 | * * * * * % x | x x x x x 0 | * * * * * % x
             * | x x x x x 0 | * * * * * % x | x x x x x] 0 | * * * * *]
             * --------------------------------------------------------
             */
            for (int i = (m - 1); i >= 1; i--)
            {
                // p = i-1 : i;
                p = new int[]
                {
                        i - 1, i
                };
                // System.out.println("\n------- p --------"); //qsubp =
                // indices = new Indices(p);
                // indices.print(4);

                // [G,q(p)] = planerot(q(p));
                temp = q.getMatrix(p, 0);
                // System.out.println("\n------- qsubp --------");
                // temp.print(4,4);

                Matrix[] Gq = planerot(temp);
                G = Gq[0];
                Matrix qp = Gq[1];
                q.setMatrix(p, 0, qp);
                // R(p,i-1:n) = G * R(p,i-1:n);
                pk = Indices.linspace(i - 1, n - 1, 1).getRowPackedCopy();
                temp = G.times(R.getMatrix(p, pk));
                R.setMatrix(p, pk, temp);
                // Q(:,p) = Q(:,p) * G';
                temp = Q.getColumns(p).times(G.transpose());
                Q.setColumns(p, temp);
            }

            /*
             * System.out.println("\n------- Q --------"); Q.print(4,4);
             * System.out.println("\n------- R --------"); R.print(4,4);
             */
            // The boxed off (---) parts of Q and R are the desired factors.
            m = Q.getRowDimension();
            n = Q.getColumnDimension();
            // Q = Q(2:end,2:end);
            Q = Q.getMatrix(1, m - 1, 1, n - 1);
            // R(1,:) = [];
            R = R.removeRowAt(0);

            /*
             * System.out.println("\n------- Q --------"); Q.print(4,4);
             * System.out.println("\n------- R --------"); R.print(4,4);
             */
        }// ------------------------------------------------------------------------

        return new Matrix[]
        {
                Q, R
        };
    }

    public static Matrix[] qrdelete(Matrix Q, Matrix R, int j)
    {
        return qrdelete(Q, R, j, Dimension.COL);
    }

    /*
     * QRINSERT Insert a column or row into QR factorization. % [Q1,R1] =
     * QRINSERT(Q,R,J,X) returns the QR factorization of the matrix A1, % where
     * A1 is A=Q*R with an extra column, X, inserted before A(:,J). If A has % N
     * columns and J = N+1, then X is inserted after the last column of A. % %
     * QRINSERT(Q,R,J,X,'col') is the same as QRINSERT(Q,R,J,X). % % [Q1,R1] =
     * QRINSERT(Q,R,J,X,'row') returns the QR factorization of the matrix % A1,
     * where A1 is A=Q*R with an extra row, X, inserted before A(J,:). % ==> The
     * index , which is j=3 ,must be a positive number, ie, j>=1
     * 
     * % Example: % A = magic(5); [Q,R] = qr(A); % j = 3; x = 1:5; % [Q1,R1] =
     * qrinsert(Q,R,j,x,'row'); % returns a valid QR factorization, although
     * possibly different from % A2 = [A(1:j-1,:); x; A(j:end,:)]; % [Q2,R2] =
     * qr(A2);
     */
    public static Matrix[] qrinsert_bk(Matrix QQ, Matrix RR, int j, Matrix xx, Dimension orient)
    {

        int mx = xx.getRowDimension();
        int nx = xx.getColumnDimension();
        ;

        int mq = QQ.getRowDimension();
        int nq = QQ.getColumnDimension();

        int m = 0;
        int n = 0;
        if (RR != null)
        {
            m = RR.getRowDimension();
            n = RR.getColumnDimension();
        }

        int[] ind = null;
        int[] ind2 = null;
        Matrix temp = null;
        Matrix G = null;
        Indices indices = null;
        Matrix matrix = null;

        Matrix Q = QQ.copy();
        Matrix R = null; // R = RR.copy();
        if (RR != null)
        {
            R = RR.copy();
        }
        Matrix x = xx.copy();

        // System.out.println("\n------- R1 --------");
        // R.print(4,4);
        if ((orient == Dimension.COL) && (n == 0))
        {
            // System.out.println("\n------- QRInsert #1 --------");
            // [Q,R] = qr(x);
            // QRDecomposition qr = x.qr();
            QR qr = new QR(x);
            Q = qr.getQ();
            R = qr.getR();
            return new Matrix[]
            {
                    Q, R
            };
        }

        if ((orient == Dimension.ROW) && (m == 0))
        {
            // [Q,R] = qr(x);
            QR qr = new QR(x);
            Q = qr.getQ();
            R = qr.getR();
            // System.out.println("\n------- QRInsert #2 --------");
            return new Matrix[]
            {
                    Q, R
            };
        }

        // Error checking
        if (mq != nq)
        {
            // error('MATLAB:qrinsert:QNotSquare', 'Q must be square.')
            throw new IllegalArgumentException(" qrinsert :  Parameter \"Q\" must be a square matrix.");
        }
        else if (nq != m)
        {
            // error('MATLAB:qrinsert:InnerDimQRfactors', 'Inner matrix
            // dimensions of QR factors must agree.')
            throw new IllegalArgumentException(" qrinsert :  Inner matrix dimensions of QR factors must agree.");
        }
        else if (j <= 0)
        {
            // error('MATLAB:qrinsert:NegInsertionIndex', 'Insertion index must
            // be positive.')
            throw new IllegalArgumentException(" qrinsert :  Insertion index must be zero or positive.");
        }

        if (orient == Dimension.COL)
        {// ==============================================
            if (j > n + 1)
            {
                // error('MATLAB:qrinsert:InvalidInsertionIndex', 'Insertion
                // index exceeds matrix dimensions.')
                throw new IllegalArgumentException(" qrinsert :  Insertion index exceeds matrix dimensions.");
            }
            else if ((mx != m) || (nx != 1))
            {
                // error('MATLAB:qrinsert:WrongSizeInsertedCol', 'Inserted
                // column has incorrect dimensions.')
                throw new IllegalArgumentException(" qrinsert :  Inserted column has incorrect dimensions.");
            }

            // System.out.println("\n------- R --------");
            // R.print(4,4);
            // Make room and insert x before j-th column.
            indices = Indices.linspace(j - 1, n - 1);
            // System.out.println("\n------- jn --------");
            // indices.print(4);

            ind = indices.getRowPackedCopy();
            // R(:,j+1:n+1) = R(:,j:n);
            temp = R.getColumns(ind);
            // ind = Indices.linspace(j,n-1,1).getRowPackedCopy();
            // System.out.println("\n------- Rjn --------");
            // temp.print(4,4);

            R = R.mergeH(Matrix.zeros(R.getRowDimension(), 1));

            // Use either one of the following, just comment out one and
            // uncomment the other which ever works.
            indices = Indices.linspace(j, n, 1);
            // System.out.println("\n------- j1n1 --------");
            // indices.print(4);

            ind = indices.getRowPackedCopy();
            R.setColumns(ind, temp);
            // System.out.println("\n------- R --------");
            // R.print(4,4);

            // R(:,j) = Q'*x;
            temp = Q.transpose().times(x);
            R.setColumnAt(j - 1, temp);

            // System.out.println("\n------- R --------");
            // R.print(4,4);
            n = n + 1;

            /*-------------------------------------------------------*
             % Now R has nonzeros below the diagonal in the j-th column,
             % and "extra" zeros on the diagonal in later columns.
             %    R = [x x x x x         [x x x x x
             %         0 x x x x    G     0 x x x x
             %         0 0 + x x   --->   0 0 * * *
             %         0 0 + 0 x          0 0 0 * *
             %         0 0 + 0 0]         0 0 0 0 *]
             % Use Givens rotations to zero the +'s, one at a time, from bottom to top.
             %
             % Q is treated to (the transpose of) the same rotations.
             %    Q = [x x x x x    G'   [x x * * *
             %         x x x x x   --->   x x * * *
             %         x x x x x          x x * * *
             %         x x x x x          x x * * *
             %         x x x x x]         x x * * *]
             *---------------------------------------------------------*/
            int[] p = null;
            int[] pk = null;

            // System.out.println("\n------- R --------");
            // R.print(4,4);
            for (int k = m - 1; k >= j; k--)
            {
                // System.out.println("##################################################");
                p = new int[]
                {
                        k - 1, k
                };
                // indices = new Indices(p);
                // System.out.println("\n------- {k, k+1} --------");
                // indices.print(4);

                temp = R.getMatrix(p, j - 1);
                // [G,R(p,j)] = planerot(R(p,j));
                // System.out.println("\n------- Rpj --------");
                // temp.print(4,4);

                Matrix[] GR = planerot(temp);
                G = GR[0];
                temp = GR[1];
                R.setMatrix(p, j - 1, temp);

                if (k < n)
                {
                    indices = Indices.linspace(k, n - 1);
                    pk = indices.getRowPackedCopy();
                    // R(p,k+1:n) = G*R(p,k+1:n);
                    temp = R.getMatrix(p, pk);
                    // System.out.println("\n------- Rpkn --------");
                    // temp.print(4,4);

                    temp = G.times(temp);
                    R.setMatrix(p, pk, temp);
                    // System.out.println("\n------- (k < n) --------");
                }
                // Q(:,p) = Q(:,p)*G';
                temp = Q.getColumns(p).times(G.transpose());
                Q.setColumns(p, temp);

                // System.out.println("\n------- R1 --------");
                // R.print(4,4);
            }

            /*
             * System.out.println("\n------- Q --------"); Q.print(4,4);
             * System.out.println("\n------- R --------"); R.print(4,4);
             */
        }
        else
        {// ====================================================================
            if (j > m + 1)
            {
                // error('MATLAB:qrinsert:InvalidInsertionIndex', 'Insertion
                // index exceeds matrix dimensions.')
                throw new IllegalArgumentException(" qrinsert :  Insertion index exceeds matrix dimensions.");
            }
            else if ((mx != 1) || (nx != n))
            {
                // error('MATLAB:qrinsert:WrongSizeInsertedRow', 'Inserted row
                // has incorrect dimensions.')
                throw new IllegalArgumentException(" qrinsert :  Inserted row has incorrect dimensions.");
            }

            R = x.mergeV(R);// [x; R];
            // Q = [1 zeros(1,m,class(R)); zeros(m,1) Q];
            temp = Matrix.zeros(1, m + 1);
            temp.set(0, 0, 1.0);
            Q = temp.mergeV(Matrix.zeros(m, 1).mergeH(Q));

            /*
             * System.out.println("\n------- R --------"); R.print(4,4);
             * System.out.println("\n------- Q --------"); Q.print(4,4);
             */

            /*---------------------------------------------------------------------*
             *Now R is upper Hessenberg.
             %    R = [x x x x         [* * * *
             %         + x x x    G       * * *
             %           + x x   --->       * *
             %             + x                *
             %               +          0 0 0 0
             %         0 0 0 0          0 0 0 0
             %         0 0 0 0]         0 0 0 0]
             % Use Givens rotations to zero the +'s, one at a time, from top to bottom.
             %
             % Q is treated to (the transpose of) the same rotations and then a row
             % permutation, p, to shuffle row 1 down to row j.
             %    Q = [1 | 0 0 0 0 0         [# # # # # #         [* * * * * *
             %         --|----------          -----------          -----------
             %         0 | x x x x x    G'    * * * * * *    p     * * * * * *
             %         0 | x x x x x   --->   * * * * * *   --->   # # # # # #
             %         0 | x x x x x          * * * * * *          * * * * * *
             %         0 | x x x x x          * * * * * *          * * * * * *
             %         0 | x x x x x]         * * * * * *]         * * * * * *]
             *--------------------------------------------------------------------*/
            int[] p = null;
            int[] pk = null;

            // System.out.println("\n------- R1 --------");
            // R.print(4,4);
            // System.out.println("\n minMN = "+Math.min(m,n));
            for (int i = 0; i < Math.min(m, n); i++)
            {
                // System.out.println("\n###############################################");
                p = new int[]
                {
                        i, i + 1
                };
                // [G,R(p,i)] = planerot(R(p,i));
                temp = R.getMatrix(p, i);
                // System.out.println("\n------- Rpi --------");
                // temp.print(4,4);

                Matrix[] GR = planerot(temp);

                G = GR[0];
                // System.out.println("\n------- G --------");
                // G.print(4,4);

                temp = GR[1];
                R.setMatrix(p, i, temp);
                // System.out.println("\n------- R --------");
                // R.print(4,4);

                // R(p,i+1:n) = G * R(p,i+1:n);
                if ((i + 1) <= (n - 1))
                {
                    indices = Indices.linspace(i + 1, n - 1, 1);
                    // System.out.println("\n------- indices --------");
                    // indices.print(4);

                    pk = indices.getRowPackedCopy();
                    matrix = R.getMatrix(p, pk);
                    temp = G.times(matrix);
                    R.setMatrix(p, pk, temp);
                }
                // Q(:,p) = Q(:,p) * G';
                temp = Q.getColumns(p).times(G.transpose());
                Q.setColumns(p, temp);
            }

            // This permutes row 1 of Q*R to row j of Q(p,:)*R
            if (j != 1)
            {
                // p = [2:j, 1, j+1:m+1];
                Indices tj = Indices.linspace(2, j, 1).mergeH(new Indices(1, 1, 1));
                Indices jm = Indices.linspace(j + 1, m + 1, 1);
                indices = tj.mergeH(jm);
                indices = indices.minus(1); // java index starts at zero.
                p = indices.getRowPackedCopy();
                // System.out.println("\n------- p --------");
                // indices.print(4);

                // Q = Q(p,:);
                Q = Q.getRows(p);
            }

        }// =======================================================================

        return new Matrix[]
        {
                Q, R
        };
    }

    public static Matrix[] qrinsert_bk(Matrix Q, Matrix R, int j, Matrix x)
    {
        return qrinsert_bk(Q, R, j, x, Dimension.COL);
    }

    /*
     * QRINSERT Insert a column or row into QR factorization. % [Q1,R1] =
     * QRINSERT(Q,R,J,X) returns the QR factorization of the matrix A1, % where
     * A1 is A=Q*R with an extra column, X, inserted before A(:,J). If A has % N
     * columns and J = N+1, then X is inserted after the last column of A. % %
     * QRINSERT(Q,R,J,X,'col') is the same as QRINSERT(Q,R,J,X). % % [Q1,R1] =
     * QRINSERT(Q,R,J,X,'row') returns the QR factorization of the matrix % A1,
     * where A1 is A=Q*R with an extra row, X, inserted before A(J,:). % ==> The
     * index , which is j=3 ,must be a positive number, ie, j>=1
     * 
     * % Example: % A = magic(5); [Q,R] = qr(A); % j = 3; x = 1:5; % [Q1,R1] =
     * qrinsert(Q,R,j,x,'row'); % returns a valid QR factorization, although
     * possibly different from % A2 = [A(1:j-1,:); x; A(j:end,:)]; % [Q2,R2] =
     * qr(A2);
     */
    public static Matrix[] qrinsert(Matrix QQ, Matrix RR, int index, Matrix xx, Dimension orient)
    {

        int j = index + 1; // add one here since Java starts array index starts
                           // at zero where matlab starts at one.

        int mx = xx.getRowDimension();
        int nx = xx.getColumnDimension();
        ;

        int mq = QQ.getRowDimension();
        int nq = QQ.getColumnDimension();

        int m = 0;
        int n = 0;
        if (RR != null)
        {
            m = RR.getRowDimension();
            n = RR.getColumnDimension();
        }

        int[] ind = null;
        int[] ind2 = null;
        Matrix temp = null;
        Matrix G = null;
        Indices indices = null;
        Matrix matrix = null;

        Matrix Q = QQ.copy();
        Matrix R = null; // R = RR.copy();
        if (RR != null)
        {
            R = RR.copy();
        }
        Matrix x = xx.copy();

        // System.out.println("\n------- R1 --------");
        // R.print(4,4);
        if ((orient == Dimension.COL) && (n == 0))
        {
            // System.out.println("\n------- QRInsert #1 --------");
            // [Q,R] = qr(x);
            // QRDecomposition qr = x.qr();
            QR qr = new QR(x);
            Q = qr.getQ();
            R = qr.getR();
            return new Matrix[]
            {
                    Q, R
            };
        }

        if ((orient == Dimension.ROW) && (m == 0))
        {
            // [Q,R] = qr(x);
            QR qr = new QR(x);
            Q = qr.getQ();
            R = qr.getR();
            // System.out.println("\n------- QRInsert #2 --------");
            return new Matrix[]
            {
                    Q, R
            };
        }

        // Error checking
        if (mq != nq)
        {
            // error('MATLAB:qrinsert:QNotSquare', 'Q must be square.')
            throw new IllegalArgumentException(" qrinsert :  Parameter \"Q\" must be a square matrix.");
        }
        else if (nq != m)
        {
            // error('MATLAB:qrinsert:InnerDimQRfactors', 'Inner matrix
            // dimensions of QR factors must agree.')
            throw new IllegalArgumentException(" qrinsert :  Inner matrix dimensions of QR factors must agree.");
        }
        else if (j <= 0)
        {
            // error('MATLAB:qrinsert:NegInsertionIndex', 'Insertion index must
            // be positive.')
            throw new IllegalArgumentException(" qrinsert :  Insertion index ( = " + index
                    + ") must be a non-negative number.");
        }

        if (orient == Dimension.COL)
        {// ==============================================
            if (j > n + 1)
            {
                // error('MATLAB:qrinsert:InvalidInsertionIndex', 'Insertion
                // index exceeds matrix dimensions.')
                throw new IllegalArgumentException(" qrinsert :  Insertion index exceeds matrix dimensions.");
            }
            else if ((mx != m) || (nx != 1))
            {
                // error('MATLAB:qrinsert:WrongSizeInsertedCol', 'Inserted
                // column has incorrect dimensions.')
                throw new IllegalArgumentException(" qrinsert :  Inserted column has incorrect dimensions.");
            }

            Matrix Qx = Q.transpose().times(x);// R(:,j) = Q'*x;

            // System.out.println("\n------- R --------");
            // R.print(4,4);
            // this block is to prevent the method 'linspace'
            // from throwing an exception.
            if (n >= j)
            {
                // Make room and insert x before j-th column.
                indices = Indices.linspace(j - 1, n - 1);
                // System.out.println("\n------- jn --------");
                // indices.print(4);

                ind = indices.getRowPackedCopy();
                // R(:,j+1:n+1) = R(:,j:n);
                temp = R.getColumns(ind);
                // ind = Indices.linspace(j,n-1,1).getRowPackedCopy();
                // System.out.println("\n------- Rjn --------");
                // temp.print(4,4);

                R = R.mergeH(Matrix.zeros(R.getRowDimension(), 1));

                // Use either one of the following, just comment out one and
                // uncomment the other which ever works.
                indices = Indices.linspace(j, n, 1);
                // System.out.println("\n------- j1n1 --------");
                // indices.print(4);

                ind = indices.getRowPackedCopy();
                R.setColumns(ind, temp);
                // System.out.println("\n------- R --------");
                // R.print(4,4);
            }

            // Do the insertion here
            R = R.setXtendedColAt(j - 1, Qx);// .setColumnAt(j-1,temp);

            // System.out.println("\n------- Rinsert --------");
            // R.print(4,4);
            n = n + 1;

            /*-------------------------------------------------------*
             % Now R has nonzeros below the diagonal in the j-th column,
             % and "extra" zeros on the diagonal in later columns.
             %    R = [x x x x x         [x x x x x
             %         0 x x x x    G     0 x x x x
             %         0 0 + x x   --->   0 0 * * *
             %         0 0 + 0 x          0 0 0 * *
             %         0 0 + 0 0]         0 0 0 0 *]
             % Use Givens rotations to zero the +'s, one at a time, from bottom to top.
             %
             % Q is treated to (the transpose of) the same rotations.
             %    Q = [x x x x x    G'   [x x * * *
             %         x x x x x   --->   x x * * *
             %         x x x x x          x x * * *
             %         x x x x x          x x * * *
             %         x x x x x]         x x * * *]
             *---------------------------------------------------------*/
            int[] p = null;
            int[] pk = null;

            // System.out.println("\n------- R --------");
            // R.print(4,4);
            for (int k = m - 1; k >= j; k--)
            {
                // System.out.println("##################################################");
                p = new int[]
                {
                        k - 1, k
                };
                // indices = new Indices(p);
                // System.out.println("\n------- {k, k+1} --------");
                // indices.print(4);

                temp = R.getMatrix(p, j - 1);
                // [G,R(p,j)] = planerot(R(p,j));
                // System.out.println("\n------- Rpj --------");
                // temp.print(4,4);

                Matrix[] GR = planerot(temp);
                G = GR[0];
                temp = GR[1];
                R.setMatrix(p, j - 1, temp);

                if (k < n)
                {
                    indices = Indices.linspace(k, n - 1);
                    pk = indices.getRowPackedCopy();
                    // R(p,k+1:n) = G*R(p,k+1:n);
                    temp = R.getMatrix(p, pk);
                    // System.out.println("\n------- Rpkn --------");
                    // temp.print(4,4);

                    temp = G.times(temp);
                    R.setMatrix(p, pk, temp);
                    // System.out.println("\n------- (k < n) --------");
                }
                // Q(:,p) = Q(:,p)*G';
                temp = Q.getColumns(p).times(G.transpose());
                Q.setColumns(p, temp);

                // System.out.println("\n------- R1 --------");
                // R.print(4,4);
            }

            /*
             * System.out.println("\n------- Q --------"); Q.print(4,4);
             * System.out.println("\n------- R --------"); R.print(4,4);
             */
        }
        else
        {// ====================================================================
            if (j > m + 1)
            {
                // error('MATLAB:qrinsert:InvalidInsertionIndex', 'Insertion
                // index exceeds matrix dimensions.')
                throw new IllegalArgumentException(" qrinsert :  Insertion index exceeds matrix dimensions.");
            }
            else if ((mx != 1) || (nx != n))
            {
                // error('MATLAB:qrinsert:WrongSizeInsertedRow', 'Inserted row
                // has incorrect dimensions.')
                throw new IllegalArgumentException(" qrinsert :  Inserted row has incorrect dimensions.");
            }

            R = x.mergeV(R);// [x; R];
            // Q = [1 zeros(1,m,class(R)); zeros(m,1) Q];
            temp = Matrix.zeros(1, m + 1);
            temp.set(0, 0, 1.0);
            Q = temp.mergeV(Matrix.zeros(m, 1).mergeH(Q));

            /*
             * System.out.println("\n------- R --------"); R.print(4,4);
             * System.out.println("\n------- Q --------"); Q.print(4,4);
             */

            /*---------------------------------------------------------------------*
             *Now R is upper Hessenberg.
             %    R = [x x x x         [* * * *
             %         + x x x    G       * * *
             %           + x x   --->       * *
             %             + x                *
             %               +          0 0 0 0
             %         0 0 0 0          0 0 0 0
             %         0 0 0 0]         0 0 0 0]
             % Use Givens rotations to zero the +'s, one at a time, from top to bottom.
             %
             % Q is treated to (the transpose of) the same rotations and then a row
             % permutation, p, to shuffle row 1 down to row j.
             %    Q = [1 | 0 0 0 0 0         [# # # # # #         [* * * * * *
             %         --|----------          -----------          -----------
             %         0 | x x x x x    G'    * * * * * *    p     * * * * * *
             %         0 | x x x x x   --->   * * * * * *   --->   # # # # # #
             %         0 | x x x x x          * * * * * *          * * * * * *
             %         0 | x x x x x          * * * * * *          * * * * * *
             %         0 | x x x x x]         * * * * * *]         * * * * * *]
             *--------------------------------------------------------------------*/
            int[] p = null;
            int[] pk = null;

            // System.out.println("\n------- R1 --------");
            // R.print(4,4);
            // System.out.println("\n minMN = "+Math.min(m,n));
            for (int i = 0; i < Math.min(m, n); i++)
            {
                // System.out.println("\n###############################################");
                p = new int[]
                {
                        i, i + 1
                };
                // [G,R(p,i)] = planerot(R(p,i));
                temp = R.getMatrix(p, i);
                // System.out.println("\n------- Rpi --------");
                // temp.print(4,4);

                Matrix[] GR = planerot(temp);

                G = GR[0];
                // System.out.println("\n------- G --------");
                // G.print(4,4);

                temp = GR[1];
                R.setMatrix(p, i, temp);
                // System.out.println("\n------- R --------");
                // R.print(4,4);

                // R(p,i+1:n) = G * R(p,i+1:n);
                if ((i + 1) <= (n - 1))
                {
                    indices = Indices.linspace(i + 1, n - 1, 1);
                    // System.out.println("\n------- indices --------");
                    // indices.print(4);

                    pk = indices.getRowPackedCopy();
                    matrix = R.getMatrix(p, pk);
                    temp = G.times(matrix);
                    R.setMatrix(p, pk, temp);
                }
                // Q(:,p) = Q(:,p) * G';
                temp = Q.getColumns(p).times(G.transpose());
                Q.setColumns(p, temp);
            }

            // This permutes row 1 of Q*R to row j of Q(p,:)*R
            if (j != 1)
            {
                // p = [2:j, 1, j+1:m+1];
                Indices tj = Indices.linspace(2, j, 1).mergeH(new Indices(1, 1, 1));
                Indices jm = Indices.linspace(j + 1, m + 1, 1);
                indices = tj.mergeH(jm);
                indices = indices.minus(1); // java index starts at zero.
                p = indices.getRowPackedCopy();
                // System.out.println("\n------- p --------");
                // indices.print(4);

                // Q = Q(p,:);
                Q = Q.getRows(p);
            }

        }// =======================================================================

        return new Matrix[]
        {
                Q, R
        };
    }

    public static Matrix[] qrinsert(Matrix Q, Matrix R, int j, Matrix x)
    {
        return qrinsert(Q, R, j, x, Dimension.COL);
    }

    public static boolean isPosDefinite(Matrix A)
    {
        boolean isposdef = true;
        if (A.isSquare() == false)
        {
            throw new IllegalArgumentException(" isPositiveDefinite :  Parameter \"A\" must be a square matrix.");
        }

        int lenA = A.length();

        for (int i = 0; i < lenA; i++)
        {
            int[] ind = null;
            if (i != 0)
            {
                ind = Indices.linspace(0, i - 1).getRowPackedCopy();
            }
            else
            {
                ind = new int[]
                {
                    0
                };
            }
            Matrix subA = A.getMatrix(ind, ind);
            if (subA.det() <= 0.0)
            {
                isposdef = false;
                break;
            }

        }
        return isposdef;
    }

    public static Matrix nullSpace(Matrix A)
    {
        // Orthonormal basis
        int m = A.getRowDimension();
        int n = A.getColumnDimension();
        // SingularValueDecomposition SVD = A.svd();

        SvdJLapack SVD = new SvdJLapack(A);

        Matrix U = SVD.getU();
        Matrix S = SVD.getS();
        Matrix V = SVD.getV();

        U.printInLabel("U");
        S.printInLabel("S");
        V.printInLabel("V");

        // [U,S,V] = svd(A,0);
        Matrix ss = null;
        if (m > 1)
        {
            ss = S.diag();
        }
        else if (m == 1)
        {
            ss = new Matrix(1, 1, S.start());
        }
        else
        {
            ss = new Matrix(1, 1);
        }

        // ss.printInLabel("ss");
        double tol = (double) Math.max(m, n) * JDatafun.max(ss).start() * MathUtil.EPS;// eps(class(A));
        Indices Ind = ss.GT(tol);
        int r = JDatafun.sum(Ind).get(0, 0);

        // System.out.println("r = "+r );
        Matrix Z = null;
        // Z = V(:,r+1:n);
        if (r <= (n - 1))
        {
            int[] arr = Indices.linspace(r, n - 1).getRowPackedCopy();
            Z = V.getColumns(arr);
        }
        return Z;
    }

    public static void main(String[] args)
    {

        double[][] aa =
        {
                {
                        10, 3, 1, 1, 3, 11
                },
                {
                        11, 11, 12, 7, 7, 4
                },
                {
                        11, 0, 10, 6, 7, 10
                },
                {
                        6, 4, 5, 11, 10, 10
                },
                {
                        10, 1, 14, 12, 11, 2
                }
        };

        Matrix A = new Matrix(aa);

        Matrix N = nullSpace(A.transpose());

        N.printInLabel("N");

        /*
         * double[][] q = {{-0.6076, -0.5693, -0.1937, -0.1573, -0.4943},
         * {-0.4051, -0.2497, 0.5037, 0.6022, 0.3965}, {-0.5570, 0.4195,
         * -0.5755, -0.0008, 0.4273}, {-0.3038, 0.1698, 0.5599, -0.7275,
         * 0.1900}, {-0.2532, 0.6393, 0.2531, 0.2888, -0.6162}}; Matrix Q = new
         * Matrix(q);
         * 
         * 
         * double[][] r = {{-19.7484, -12.1529, -15.8494, -21.7233, -16.6089,
         * -18.3306}, {0, 7.7011, 9.3992, 7.0119, -3.3561, 2.7568}, {0, 0,
         * 11.7666, 3.5519, 3.3403, 10.6709}, {0, 0, 0, -3.0524, 2.6639,
         * -1.4964}, {0, 0, 0, 0, -2.1507, -0.5316}};
         * 
         * 
         * Matrix R = new Matrix(r);
         * 
         * Matrix xx = new Matrix(new double[][]{{3, 15, 4, 4,
         * 13}}).transpose();
         */
        /*
         * System.out.println("\n------- Q --------"); Q.print(8,4);
         * 
         * System.out.println("\n------- R --------"); R.print(8,4);
         * 
         * //xx = xx.transpose().times(xx);
         * 
         * System.out.println("\n------- xx --------"); xx.print(8,4);
         */
        // CholeskyDecomposition cd = xx.times(xx.transpose()) .chol();
        // R = cd.getLR();
        // QR qr = new QR(xx);
        // Q = qr.getQ();
        // R = qr.getR();
        // System.out.println("\n------- Q2 --------");
        // Q.print(8,4);
        // System.out.println("\n------- R2 --------");
        // R.print(8,4);

        /*
         * Matrix[] QR = qrinsert(Q, R,0, xx);
         * 
         * System.out.println("\n------- Q2 --------"); QR[0].print(8,4);
         * 
         * System.out.println("\n------- R2 --------"); QR[1].print(8,4);
         */

        /*
         * Matrix mat = new Matrix(new double[][]{{0.8944,
         * 0.4472}}).transpose(); Matrix QQ = Matrix.identity(2); Matrix[] QR =
         * JMatfun.qrinsert(QQ, null, 0, mat);
         * 
         * System.out.println("\n------- Q --------"); QR[0].print(8,4);
         * 
         * System.out.println("\n------- R --------"); QR[1].print(8,4);
         * 
         * double[][] z = new double[][]{ {5, 2, 3}, {10, 5, 3}, {10, 9, 7}, {5,
         * 1, 4}, {10, 2, 3} };
         * 
         * Matrix Z = new Matrix(z); Matrix Zt = Z.transpose(); Matrix H =
         * JDatafun.magic(5);
         * 
         * Matrix projH = Zt.times(H).times(Z);//Z'*H*Z
         * 
         * System.out.println("\n------- projH --------"); projH.print(4,4);
         * 
         * Matrix A = new Matrix(new double[][]{{4532 , 5682}, {5634 , 7134}});
         * 
         * System.out.println("isPosDefinite = "+JMatfun.isPosDefinite(A.transpose
         * ()));
         * 
         * 
         * Matrix A = JDatafun.magic(5); //QR qr = new QR(A.getArray()); //[Q,R]
         * = qr(A); QRDecomposition QR = A.qr(); //Matrix Q = new
         * Matrix(qr.getQ()); Matrix Q = QR.getQ(); //Matrix R = new
         * Matrix(qr.getR());// Matrix R = QR.getR();
         * 
         * //System.out.println("\n------- Q --------"); //Q.print(4,4);
         * //System.out.println("\n------- R --------"); //R.print(4,4);
         * 
         * int j = 3; Matrix x = Matrix.linIncrement(1.0,5.0,1.0);//1:5;
         * 
         * //[Q1,R1] = qrinsert(Q,R,j,x,'row'); Matrix[] qrdel =
         * qrdelete(Q,R,j);//,Matrix.DIM_ROW);
         * 
         * 
         * System.out.println("\n------- Q --------"); qrdel[0].print(4,4);
         * System.out.println("\n------- R --------"); qrdel[1].print(4,4);
         */
    }
}// ----------------------------- End Class Definition
// --------------------------

