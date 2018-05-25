package sidnet.batch;

import java.util.List;
import java.util.Map;

public class DataBean {
	public final String xHeader, yHeader;
	public final List<Pair> xyList;
	public final Map<String, Object> groupInfo; // information about the SPECIFIC groupby	
	public final Map<String, String> tagsInfo;  // information about all the group categories
	public final String catKey;
	
	public DataBean(String xHeader, String yHeader, List<Pair> xyList, Map<String, Object> groupInfo, Map<String, String> tagsInfo, String catKey) {
		this.xHeader = xHeader;
		this.yHeader = yHeader;
		this.xyList = xyList;
		this.groupInfo = groupInfo;
		this.tagsInfo = tagsInfo;
		this.catKey = catKey;
	}
}
