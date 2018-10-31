package jamaextension.jamax.testmisc;

//public class Solution {

//}

import java.io.*;
import java.util.*;

public class Solution {
	
	public static void main(String args[]) throws Exception {
		String stockpriceTextFile = args[0];
		BufferedReader buff = new BufferedReader(new FileReader(stockpriceTextFile));
		try {
			String sline = buff.readLine();

			int count = 0;
			int indexCount = 0;
			List<Double> priceD = new ArrayList<Double>();
			List<Double> priceDzeros = new ArrayList<Double>();
			List<Integer> daysI = new ArrayList<Integer>();
			int N = -1;

			while (sline != null) {
				if (count == 0) {
					N = Integer.parseInt(sline);
				} else {
					String[] priceCol = sline.split("\t");
					if (priceCol[2].contains("Missing")) {
						priceD.add(Double.NaN);
						priceDzeros.add(0.0);
					} else {
						priceD.add(Double.parseDouble(priceCol[2]));
						priceDzeros.add(Double.parseDouble(priceCol[2]));
					}
					daysI.add(indexCount);
					indexCount++;
				}
				count++;
			} // end while

			int siz = priceD.size();			
			for (int i = 0; i < siz; i++) {
				double prc = priceD.get(i);
				// double prc2 = priceDzeros.get(i);
				int[] neighbors2 = { i - 1, i, i + 1 };

				// impute here by taking the max of 2 non-missing neighbors
				if (Double.isNaN(prc)) {
					if (neighbors2[0] < 0) {
						int shiftu = Math.abs(neighbors2[0]);
						neighbors2 = new int[] { i - 1 + shiftu, i + shiftu, i + 1 + shiftu };
					} else if (neighbors2[2] > siz - 1) {
						int shiftd = Math.abs(neighbors2[2]);
						neighbors2 = new int[] { i - 1 - shiftd, i - shiftd, i + 1 - shiftd };
					}
					double maxVal = -1.0;
					for (int u = 0; u < neighbors2.length; u++) {
						maxVal = Math.max(maxVal, priceDzeros.get(neighbors2[u]));
					}
					// fill in the missing value here at index i
					priceDzeros.set(i, maxVal);
				}
			} // end for
				// Loop through elements.
			for (int i = 0; i < priceDzeros.size(); i++) {
				double val = priceDzeros.get(i);
				System.out.println(" " + val);
			}
		} catch (NumberFormatException nfe) {
			throw nfe;
		} finally {
			buff.close();
		}

	}// end main

}// end class
