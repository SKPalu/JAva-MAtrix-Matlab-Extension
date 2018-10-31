package jamaextension.jamax;

/**
 * A generic tuple implementation (Cut & Paste from Chris's project)
 * 
 * Ref: http://stackoverflow.com/questions/2670982/using-tuples-in-java/2671059
 * 
 * @param <X>
 *            the generic type
 * @param <Y>
 *            the generic type
 */
public class Tuple<X, Y>
{
    /** The x. */
    public final X x;

    /** The y. */
    public final Y y;

    /**
     * Instantiates a new tuple.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public Tuple(X x, Y y)
    {
        this.x = x;
        this.y = y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "(" + x + "," + y + ")";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other)
    {
        if (other == null)
        {
            return false;
        }
        if (other == this)
        {
            return true;
        }
        if (!(other instanceof Tuple))
        {
            return false;
        }
        @SuppressWarnings("unchecked")
        Tuple<X, Y> other_ = (Tuple<X, Y>) other;
        return other_.x.equals(this.x) && other_.y.equals(this.y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((x == null) ? 0 : x.hashCode());
        result = prime * result + ((y == null) ? 0 : y.hashCode());
        return result;
    }
}
