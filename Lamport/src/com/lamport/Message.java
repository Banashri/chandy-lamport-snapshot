package com.lamport;

import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private boolean snapshot;
	private int code;// Code is 0 for Normal message. 1 for Marker.
	private int data;
	private int sourceId, destinationId;

	
	public boolean isSnapshot() {
		return snapshot;
	}

	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public void setDestinationId(int destinationId) {
		this.destinationId = destinationId;
	}

	public int getDestinationId() {
		return destinationId;
	}
	
	public int getSourceId() {
		return sourceId;
	}
	
	public Message(boolean snapshot, int code, int data, int sourceId, int destinationId) {
		this.snapshot = snapshot;
		this.code = code;
		this.data = data;
		this.sourceId = sourceId;
		this.destinationId = destinationId;
	}
}
