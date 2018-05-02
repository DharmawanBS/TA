package sidnet.batch.iterators;

import java.util.List;

public class Category {
	private String name;
	private List<String> params;
	
	public Category(String name, List<String> params) {
		this.name = name;
		this.params = params;
	}
	
	public String getName() { return name; };
	public List<String> getParameters() { return params; };
}
