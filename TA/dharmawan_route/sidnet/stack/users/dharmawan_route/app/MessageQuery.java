/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidnet.stack.users.dharmawan_route.app;

import jist.swans.misc.Message;
import sidnet.core.query.Query;

/**
 *
 * @author dharmawan
 */
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
