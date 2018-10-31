/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

import java.util.ArrayList;

/**
 * 
 * @author Sione
 */
public class PropValPairList
{

    private ArrayList<PropertyValuePair> list = new ArrayList<PropertyValuePair>();

    // public PropValPairList() {
    // }
    public PropValPairList(PropertyValuePair pvp)
    {
        list.add(pvp);
    }

    public void add(PropertyValuePair pvp)
    {
        if (!list.contains(pvp))
        {
            getList().add(pvp);
        }
    }

    public boolean remove(PropertyValuePair pvp)
    {
        return getList().remove(pvp);
    }

    public int size()
    {
        return getList().size();
    }

    public boolean isEmpty()
    {
        return getList().isEmpty();
    }

    public PropertyValuePair get(int ind)
    {
        int len = getList().size();
        if (ind < 0 || ind >= len)
        {
            throw new IllegalArgumentException("get : Index is out-of-bound.");
        }
        return getList().get(ind);
    }

    public void set(int ind, PropertyValuePair pvp)
    {
        int len = getList().size();
        if (ind < 0 || ind >= len)
        {
            throw new IllegalArgumentException("set : Index is out-of-bound.");
        }
        if (!list.contains(pvp))
        {
            // PropertyValuePair set =
            getList().set(ind, pvp);
        }
       /* else
        {
            int idx = list.indexOf(pvp); 
            //PropertyValuePair set = list.get(idx);
           
        }*/
    }

    /**
     * @return the list
     */
    public ArrayList<PropertyValuePair> getList()
    {
        return list;
    }

    public boolean containsProperty(Object obj)
    {
        int siz = size();
        if (siz == 1)
        {
            return list.get(0).isPropertyExist(obj);
        }
        for (int i = 0; i < siz; i++)
        {
            boolean contain = list.get(i).isPropertyExist(obj);
            if (contain)
            {
                return true;
            }
        }
        return false;
    }
    
    
    public Object getValue(Object propertyKey) {
    	if(!containsProperty(propertyKey)) {
    		return null;
    	}
    	int siz = size();
    	Object key = null;
    	for (int i = 0; i < siz; i++)
        {            
    		PropertyValuePair pvpI = list.get(i);
    		if(pvpI.getProperty().equals(propertyKey)) {
    			key = pvpI.getValue();
    			break;
    		}
        }
    	return key;
    }
    
    
}
