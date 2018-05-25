package sidnet.batch;

import java.util.Comparator;

public class NumericComparator
implements Comparator{

	public int compare(Object arg0, Object arg1) {
		Double d0, d1;
		
		String str0 = arg0.toString();
		String str1 = arg1.toString();		
		
		int lb0 = -1, ub0 = -1, lb1 = -1, ub1 = -1;
		
		String s0 = "", s1 = "";
		
		while (ub0 < str0.length() && ub1 < str1.length()) {
			lb0++;
			lb1++;
			ub0++;
			ub1++;
			s0 = "";
			s1 = "";
			String prev_s0 = s0, prev_s1 = s1;			
			try {
				while(true) {
					prev_s0 = s0;
					s0 = str0.substring(lb0, ub0+1);
					Double.parseDouble(s0);
					ub0++;
				}
				
			} catch(Exception e){ 
				if (prev_s0.length() > 0)
					s0 = prev_s0;	
			}
			
			lb0 = ub0;
			
			try {
				while(true) {
					prev_s1 = s1;
					s1 = str1.substring(lb1, ub1+1);
					Double.parseDouble(s1);
					ub1++;
				}
				
			} catch(Exception e) {
				if (prev_s1.length() > 0)
					s1 = prev_s1;				
			}
			
			lb1 = ub1;
		
			try {
				d0 = Double.parseDouble(s0);
				d1 = Double.parseDouble(s1);
				if (d0 > d1)
					return 1;
				if (d0 < d1)
					return -1;
				continue;
			} catch(Exception e){;}
			
			int res = s0.compareTo(s1.toString());
			if (res != 0)
				return res;
			else 
				continue;
		}
		if (str0.length() > str1.length())
			return 1;
		if (str0.length() < str1.length())
			return -1;
		return 0;
	}
}
