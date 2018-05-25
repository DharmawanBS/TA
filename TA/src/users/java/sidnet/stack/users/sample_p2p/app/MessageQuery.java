package sidnet.stack.users.sample_p2p.app;

import sidnet.core.query.Query;
import jist.swans.misc.Message;

public class MessageQuery implements Message {

	private final Query query;
	
	public MessageQuery(Query query) {
		this.query = query;
	}
	
	public Query getQuery() {
		return query;
	}
	
	public void getBytes(byte[] msg, int offset) {
		throw new RuntimeException("not implemented");
	}

	public int getSize() {
		return query.getAsMessageSize();
	}

}
