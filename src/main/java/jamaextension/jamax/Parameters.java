package jamaextension.jamax;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Parameters
{

    private Map<Object, Object> params = new LinkedHashMap<Object, Object>();

    public Parameters()
    {
    }

    public Parameters(Object key, Object value)
    {
        add(key, value);
    }

    public void add(Object key, Object value)
    {
        params.put(key, value);
    }

    public boolean isNil()
    {
        return this.params.isEmpty();
    }

    public int getNumParams()
    {
        return this.params.size();
    }

    public Object get(Object key)
    {
        return params.get(key);
    }

    public boolean hasKey(Object key)
    {
        Object value = params.get(key);
        return value != null;
    }

    public Set<Object> getKeySet()
    {
        return params.keySet();
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
