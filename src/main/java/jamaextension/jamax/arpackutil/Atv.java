/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.arpackutil;

import org.netlib.util.Dummy;

public class Atv
{

    // c
    // c-------------------------------------------------------------------
    // c
    // c
    // c computes y <- A'*w
    // c
    // c
    public static void atv(int m, int n, double[] w, int _w_offset, double[] y, int _y_offset)
    {

        int i = 0;
        int j = 0;
        double h = 0.0d;
        double k = 0.0d;
        double s = 0.0d;
        double t = 0.0d;
        h = (1.0 / (double) ((m + 1)));
        k = (1.0 / (double) ((n + 1)));
        {
            for (i = 1; i <= n; i++)
            {
                y[(i - (1)) + _y_offset] = 0.0;
                Dummy.label("Atv", 5);
            } // Close for() loop.
        }
        t = 0.0;
        // c
        {
            for (j = 1; j <= n; j++)
            {
                t = (t + k);
                s = 0.0;
                {
                    for (i = 1; i <= j; i++)
                    {
                        s = (s + h);
                        y[(j - (1)) + _y_offset] = (y[(j - (1)) + _y_offset] + (((k * s) * ((t - 1.0))) * w[(i - (1))
                                + _w_offset]));
                        Dummy.label("Atv", 10);
                    } // Close for() loop.
                }
                {
                    for (i = (j + 1); i <= m; i++)
                    {
                        s = (s + h);
                        y[(j - (1)) + _y_offset] = (y[(j - (1)) + _y_offset] + (((k * t) * ((s - 1.0))) * w[(i - (1))
                                + _w_offset]));
                        Dummy.label("Atv", 20);
                    } // Close for() loop.
                }
                Dummy.label("Atv", 30);
            } // Close for() loop.
        }
        // c
        Dummy.go_to("Atv", 999999);
        Dummy.label("Atv", 999999);
        return;
    }

    public static void atv(int m, int n, float[] w, int _w_offset, float[] y, int _y_offset)
    {

        int i = 0;
        int j = 0;
        float h = 0.0f;
        float k = 0.0f;
        float s = 0.0f;
        float t = 0.0f;
        h = (1.0f / (float) ((m + 1)));
        k = (1.0f / (float) ((n + 1)));
        {
            for (i = 1; i <= n; i++)
            {
                y[(i - (1)) + _y_offset] = 0.0f;
                Dummy.label("Atv", 5);
            } // Close for() loop.
        }
        t = 0.0f;
        // c
        {
            for (j = 1; j <= n; j++)
            {
                t = (t + k);
                s = 0.0f;
                {
                    for (i = 1; i <= j; i++)
                    {
                        s = (s + h);
                        y[(j - (1)) + _y_offset] = (y[(j - (1)) + _y_offset] + (((k * s) * ((t - 1.0f))) * w[(i - (1))
                                + _w_offset]));
                        Dummy.label("Atv", 10);
                    } // Close for() loop.
                }
                {
                    for (i = (j + 1); i <= m; i++)
                    {
                        s = (s + h);
                        y[(j - (1)) + _y_offset] = (y[(j - (1)) + _y_offset] + (((k * t) * ((s - 1.0f))) * w[(i - (1))
                                + _w_offset]));
                        Dummy.label("Atv", 20);
                    } // Close for() loop.
                }
                Dummy.label("Atv", 30);
            } // Close for() loop.
        }
        // c
        Dummy.go_to("Atv", 999999);
        Dummy.label("Atv", 999999);
        return;
    }

} // End class.

