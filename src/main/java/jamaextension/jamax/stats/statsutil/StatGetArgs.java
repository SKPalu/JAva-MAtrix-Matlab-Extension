/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.stats.statsutil;

import jamaextension.jamax.Cell;
import jamaextension.jamax.KeyValue;

/**
 * 
 * @author Sione
 */
public class StatGetArgs
{

    private String emsg;
    private String eid;
    private Cell varargout;

    public StatGetArgs(Cell pnames, Cell dflts, KeyValue kval)
    {
        // Initialize some variables

        int nparams = pnames.length();
        varargout = dflts;
        Cell unrecog = null;// {};
        // nargs = length(varargin);
    }
}
