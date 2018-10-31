/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 
 * @author Sione
 */
public class KeyValue
{

    private HashMap<String, Object> keyValue = new LinkedHashMap<String, Object>();
    private ArrayList<String> keyNames = new ArrayList<String>();

    public KeyValue()
    {
    }

    public KeyValue(String key, Object value)
    {
        /*
         * String msg = ""; if (key == null || "".equals(key)) { msg =
         * "String parameter \"key\" must be non-null or non-empty."; throw new
         * ConditionalRuleException("KeyValue", msg); } if
         * ("".equals(MathUtil.deSpace(key))) { msg =
         * "String parameter \"key\" must not be all white-spaces."; throw new
         * ConditionalRuleException("KeyValue", msg); } keyNames.add(key + "");
         * keyValue.put(key, value);
         */

        add(key, value);
    }

    public void add(String key)
    {
        add(key, null);
    }

    public void add(String key, Object value)
    {
        // if (!keyValue.containsKey(key)) {

        String msg = "";
        if (key == null || "".equals(key.trim()))
        {
            msg = "String parameter \"key\" must be non-null or non-empty.";
            throw new ConditionalRuleException("addKeyValPair", msg);
        }
        if ("".equals(key.trim()))
        {
            msg = "String parameter \"key\" must not be all white-spaces.";
            throw new ConditionalRuleException("addKeyValPair", msg);
        }

        String keyTrimLow = key.trim().toLowerCase();
        keyNames.add(keyTrimLow);
        keyValue.put(keyTrimLow, value);
        // }
    }

    public int size()
    {
        return this.keyValue.size();
    }

    public Object getKey(String strKey)
    {
        return this.keyValue.get(strKey.toLowerCase());
    }

    public boolean containsKey(String strKey)
    {
        return this.keyValue.containsKey(strKey.trim().toLowerCase());
    }

    public boolean isempty()
    {
        return this.keyNames.isEmpty();
    }

    public KeyValue copy()
    {
        HashMap<String, Object> kval = (HashMap<String, Object>) this.keyValue.clone();
        ArrayList<String> knam = (ArrayList<String>) this.keyNames.clone();
        int siz = knam.size();
        String name = knam.get(0);
        if (siz == 1)
        {
            return new KeyValue(name + "", kval.get(name));
        }
        KeyValue newSKV = new KeyValue(name, kval.get(name));
        for (int i = 1; i < siz; i++)
        {
            String name2 = knam.get(i);
            newSKV.add(name2 + "", kval.get(name2));
        }
        return newSKV;// new SsmKeyValue(propCopy, valCopy);
    }

    public KeyValue union(KeyValue skv)
    {
        KeyValue copy = this.copy();
        int siz = skv.size();
        String[] nameKeys = skv.keyNamesToString();
        for (int i = 0; i < siz; i++)
        {
            if (!copy.containsKey(nameKeys[i].toLowerCase()))
            {
                Object skvVal = skv.getKey(nameKeys[i].toLowerCase());
                copy.add(nameKeys[i].toLowerCase(), skvVal);
            }
        }
        return copy;
    }

    public ArrayList<String> getKeyNames()
    {
        return this.keyNames;
    }

    public String[] keyNamesToString()
    {
        int siz = this.keyNames.size();
        String[] names = new String[siz];
        for (int i = 0; i < siz; i++)
        {
            names[i] = this.keyNames.get(i) + "";
        }
        return names;
    }

    /**
     * @return the keyValue
     */
    public HashMap<String, Object> getKeyValue()
    {
        return keyValue;
    }

    public void disp()
    {
        // System.out.printf("\tComponents (%d):\n\t==================\n\n",
        // lenInfo);
        int siz = this.keyNames.size();
        String[] names = this.keyNamesToString();
        for (int i = 0; i < siz; i++)
        {
            System.out.printf("\t%1$s : %2$s \n", names[i], this.keyValue.get(names[i]));
            // System.out.printf("\tValue (%d):\n\t==================\n\n",
            // (i+1));
        }
    }

    public void printKeyValue(String nameStr)
    {
        int siz = this.keyNames.size();
        String[] names = this.keyNamesToString();
        System.out.println("------------------------- " + nameStr + " -------------------------");
        for (int i = 0; i < siz; i++)
        {
            System.out.printf("\t%1$s : %2$s \n", names[i], this.keyValue.get(names[i]));
            // System.out.printf("\tValue (%d):\n\t==================\n\n",
            // (i+1));
        }
    }

    public static void main(String[] args)
    {
        /*
         * SsmKeyValue spv = new SsmKeyValue("type", "Gaussian noise");
         * spv.add("p", 4); spv.add("k");
         * 
         * 
         * Object p4 = spv.getKey("p3"); System.out.println("p = " + p4);
         * System.out.println("size = " + spv.size());
         */

        KeyValue skv = new KeyValue("type", "Gaussian noise");
        skv.disp();
        System.out.println("---- ---- ----");

        KeyValue skv2 = new KeyValue("p", 3);
        skv = skv.union(skv2);
        skv.disp();

        System.out.println("---- DONE ----");
    }
}
