/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.datafun;

import java.util.Random;

public class RandomI extends Random
{

    public RandomI()
    {
        super();
    }

    public RandomI(long seed)
    {
        super(seed);
    }

    public int nextBoundInt(int from, int to)
    {
        if (from > to)
        {
            throw new IllegalArgumentException("nextBoundInt : Paramter from must be equal or less than to.");
        }

        if (from == to)
        {
            return nextInt(from);
        }

        int next = nextInt(to);
        while (next < from || next >= to)
        {
            next = nextInt(to);
        }

        return next;
    }
}
