/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax;

/**
 * 
 * @author Sione
 */
public class PropertyValuePair {

	private Cell propVal = new Cell(1, 2);

	public PropertyValuePair(Object property, Object value) {
		if (property == null) {
			String msg = "Parameter \"property\" must be non-null.";
			throw new ConditionalRuleException("PropertyValuePair", msg);
		}
		propVal.set(0, 0, property);
		/*
		 * if (value == null) { String msg =
		 * "Parameter \"value\" must be non-null."; throw new
		 * ConditionalRuleException("PropertyValuePair", msg); }
		 */
		propVal.set(0, 1, value);
	}

	/**
	 * @return the propVal
	 */
	public Cell getPropVal() {
		return propVal;
	}

	public Object getProperty() {
		return this.propVal.get(0, 0);
	}

	public Object getValue() {
		return this.propVal.get(0, 1);
	}

	public boolean isPropertyExist(Object obj) {
		if (obj == null) {
			return false;
		}
		return propVal.start().equals(obj);
	}
}
