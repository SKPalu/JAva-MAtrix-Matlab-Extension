/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.sparfun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.constants.Dimension;

/**
 * 
 * @author sionep
 */
public class TestSparse
{

    public static void main(String[] args)
    {
        exmp2();
    }

    static void exmp6()
    {
        double[][] xx =
        {
                {
                        11, 0, 11, 11, 2
                },
                {
                        0, 5, 0, 11, 7
                },
                {
                        0, 14, 3, 4, 14
                },
                {
                        1, 1, 0, 0, 0
                },
                {
                        1, 0, 0, 10, 9
                },
                {
                        0, 6, 10, 2, 0
                }
        };

        Matrix matX = new Matrix(xx);

        Sparse X = new Sparse(matX);
        System.out.println("========= X =========");
        X.print();

        Sparse Xt = X.transpose();
        System.out.println("========= Xt =========");
        Xt.print();

        if (true)
        {
            return;
        }

        double[][] yy =
        {
                {
                        1, 6, 3, 7, 2
                },
                {
                        0, 0, 0, 0, 0
                },
                {
                        5, 0, 2, 0, 5
                },
                {
                        8, 0, 0, 1, 10
                },
                {
                        9, 2, 0, 2, 0
                },
                {
                        1, 0, 0, 9, 4
                }
        };

        Matrix matY = new Matrix(yy);

        Sparse Y = new Sparse(matY);
        System.out.println("========= Y =========");
        Y.print();

        /*
         * Sparse XY = X.arrayTimes(Y);
         * 
         * System.out.println("========= X.*Y ========="); XY.print();
         */

        Sparse XplusY = X.add(Y);// .addOld(Y, 1.0, 1.0);

        System.out.println("========= X + Y =========");
        XplusY.print();
        int stopHere = 0;

    }

    static void sparseMul()
    {
    }

    static Set<Integer> toSet(int[] arr)
    {
        Set<Integer> set = new TreeSet<Integer>();
        int len = arr.length;
        for (int v = 0; v < len; v++)
        {
            int d = arr[v];
            set.add(d);
        }
        return set;
    }

    static Indices toIndices(Set<Integer> set)
    {
        int len = set.size();
        Object[] indObj = set.toArray();
        Indices ind = new Indices(1, len);
        for (int i = 0; i < len; i++)
        {
            ind.set(0, i, (Integer) indObj[i]);
        }
        return ind;
    }

    static void exmp5()
    {

        int[] pos1 =
        {
                1, 4, 5, 8, 9, 10, 12, 13, 15, 18, 19, 20, 21, 23, 24, 25, 26, 27, 29
        };
        new Indices(pos1).printInLabel("pos1");
        Set<Integer> pos1Set = toSet(pos1);
        int len = pos1.length;
        int[] pos1Ind = Indices.linspace(0, len - 1).getRowPackedCopy();
        HashMap<Integer, Integer> hm1 = new HashMap<Integer, Integer>();
        for (int i = 0; i < len; i++)
        {
            hm1.put(pos1[i], pos1Ind[i]);
        }

        int[] pos2 =
        {
                1, 3, 4, 5, 6, 7, 11, 13, 15, 19, 22, 23, 24, 25, 27, 28, 30
        };
        new Indices(pos2).printInLabel("pos2");
        Set<Integer> pos2Set = toSet(pos2);
        int len2 = pos2.length;
        int[] pos2Ind = Indices.linspace(0, len2 - 1).getRowPackedCopy();
        HashMap<Integer, Integer> hm2 = new HashMap<Integer, Integer>();
        for (int i = 0; i < len2; i++)
        {
            hm2.put(pos2[i], pos2Ind[i]);
        }
        // HashMap hm1 = null;

        boolean intersect = pos1Set.retainAll(pos2Set);
        Indices inter = toIndices(pos1Set);
        inter.printInLabel("intersection");
        /*
         * Set<HashMap> hs1 = new HashSet<HashMap>();
         * 
         * Set<Integer> s1 = new HashSet<Integer>(); Indices ind1 =
         * Indices.linspace(2, 12); len = ind1.length(); for (int k = 0; k <
         * len; k++) { s1.addOld(ind1.get(0, k)); }
         * 
         * Set<Integer> s2 = new HashSet<Integer>(); s2.addOld(2); s2.addOld(5);
         * s2.addOld(6);
         * 
         * s1.retainAll(s2); // s1 now contains only elements in both sets
         * 
         * len = s1.size();
         * 
         * Object[] T = s1.toArray(); len = T.length;
         * 
         * for (int k = 0; k < len; k++) { System.out.println((Integer) T[k] +
         * ""); }
         */

        // If you want to preserve the sets, create a new set to hold the
        // intersection:

        // Set<Integer> intersection = new HashSet<Integer>(s1); // use the copy
        // constructor
        // intersection.retainAll(s2);

    }

    static void exmp4()
    {

        ArrayList<int[]> list = new ArrayList<int[]>();

        int[] a =
        {
                1, 1
        };

        list.add(new int[]
        {
                1, 1
        });

        int[] b =
        {
                4, 1
        };
        int[] c =
        {
                5, 1
        };
        int[] d =
        {
                6, 1
        };
        int[] e =
        {
                1, 1
        };
        int[] f =
        {
                1, 3
        };

        boolean tf = list.contains(new int[]
        {
                1, 1
        });

        System.out.println(" tf = " + tf);
    }

    static void exmp3()
    {

        double[][] M =
        {
                {
                        11, 0, 11, 0, 8, 0
                },
                {
                        0, 0, 9, 0, 14, 0
                },
                {
                        0, 0, 11, 0, 0, 1
                },
                {
                        2, 7, 0, 0, 7, 0
                },
                {
                        11, 0, 6, 0, 0, 5
                },
                {
                        9, 0, 6, 0, 4, 0
                },
                {
                        0, 0, 0, 5, 1, 0
                },
                {
                        0, 0, 9, 0, 0, 11
                }
        };
        Matrix x3 = new Matrix(M);
        x3.printInLabel("x3", 0);
        Sparse C = new Sparse(x3);

        double[][] N =
        {
                {
                        0, 0, 0, 0, 0, 4, 9, 0
                },
                {
                        0, 0, 13, 0, 0, 0, 0, 8
                },
                {
                        0, 5, 1, 0, 0, 13, 0, 11
                },
                {
                        0, 10, 0, 13, 0, 3, 8, 8
                },
                {
                        10, 0, 9, 0, 0, 8, 0, 7
                },
                {
                        0, 0, 11, 0, 0, 3, 3, 9
                }
        };
        Matrix x4 = new Matrix(N);
        x4 = x4.transpose();
        x4.printInLabel("x4", 0);

        Sparse D = new Sparse(x4);

        Sparse E = C.arrayTimes(D);
        System.out.println("----- E -----");
        E.print();

    }

    static void exmp2()
    {

        double[][] M =
        {
                {
                        11, 0, 11, 0, 8, 0
                },
                {
                        0, 0, 9, 0, 14, 0
                },
                {
                        0, 0, 11, 0, 0, 1
                },
                {
                        2, 7, 0, 0, 7, 0
                },
                {
                        11, 0, 6, 0, 0, 5
                },
                {
                        9, 0, 6, 0, 4, 0
                },
                {
                        0, 0, 0, 5, 1, 0
                },
                {
                        0, 0, 9, 0, 0, 11
                }
        };
        Matrix x3 = new Matrix(M);
        x3.printInLabel("x", 0);
        Sparse C = new Sparse(x3);

        System.out.println("----- C -----");
        C.print();

        Sparse Sm = C.sum(Dimension.COL);
        System.out.println("\n\n----- Col -----");
        Sm.print();

        // System.out.println("\n\n----- Full 1 -----");
        Matrix matFull = Sm.full();
        matFull.printInLabel("Full 1", 0);

        Sm = C.sum(Dimension.ROW);
        System.out.println("\n\n----- Row -----");
        Sm.print();

        Sm.full().printInLabel("Full 2", 0);
    }

    static void exmp()
    {
        String dir = "C:\\Datasets\\Matrix\\";
        String fname = "testsparse.txt";

        Sparse A = Dcs_load.cs_load(dir + fname);

        Dcs_print.cs_print(A, false);

        Matrix fullA = A.full();
        fullA.sizeIndices().printInLabel("Size fullA");
        fullA.printInLabel("full_A", 0);

        System.out.println("=========== Back to Sparse Again ===========");
        A = new Sparse(fullA);
        Dcs_print.cs_print(A, false);
    }
}
