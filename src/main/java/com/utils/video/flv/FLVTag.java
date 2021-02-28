package com.utils.video.flv;

public class FLVTag {

	private byte filter;
	private byte tagType;
	private int dataSize;
	private int timestamp;
	private int timestampExtended;
	
	@Override
	public String toString() {
		return String.format("tageType=%d, dataSize=%d, timestamp=%d", tagType, dataSize, timestamp+(timestampExtended<<24));
	}
	
	public byte getFilter() {
		return filter;
	}
	public void setFilter(byte filter) {
		this.filter = filter;
	}
	public byte getTagType() {
		return tagType;
	}
	public void setTagType(byte tagType) {
		this.tagType = tagType;
	}
	public int getDataSize() {
		return dataSize;
	}
	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getTimestampExtended() {
		return timestampExtended;
	}

	public void setTimestampExtended(int timestampExtended) {
		this.timestampExtended = timestampExtended;
	}
	
}
