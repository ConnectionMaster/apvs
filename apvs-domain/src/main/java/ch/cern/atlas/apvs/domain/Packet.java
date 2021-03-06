package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Packet implements Serializable, IsSerializable {

	private static final long serialVersionUID = -1225311491360327143L;

	private String sender;
	private String receiver;
	private Integer frameID;
	private Boolean acknowledge;
	
	List<Message> messages = new ArrayList<Message>();
	
	protected Packet() {
	}
	
	public Packet(String sender, String receiver, int frameID, boolean acknowledge) {
		this.sender = sender;
		this.receiver = receiver;
		this.frameID = frameID;
		this.acknowledge = acknowledge;
	}
	
	public Packet(String sender, int frameID, boolean acknowledge, Message order) {
		this(sender, order.getDevice().getName(), frameID, acknowledge);
		addMessage(order);
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public Integer getFrameID() {
		return frameID;
	}

	public Boolean getAcknowledge() {
		return acknowledge;
	}

	public void addMessage(Message msg) {
		messages.add(msg);
	}
	
	public List<Message> getMessages() {
		return messages;
	}
		
	public String toString() {
		String s = "Packet sender:"+sender+", receiver:"+receiver+", frameId:"+frameID+", ack:"+acknowledge+", #messages:"+messages.size();
		for (Message m: messages) {
			s += "\n   "+m.toString();
		}
		return s;
	}

	
}
