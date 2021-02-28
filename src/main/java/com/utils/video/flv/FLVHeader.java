package com.utils.video.flv;

public class FLVHeader {

	private short version;
	private short typeFlags;
	private long dataOffset;
	public short getVersion() {
		return version;
	}
	public void setVersion(short version) {
		this.version = version;
	}
	public short getTypeFlags() {
		return typeFlags;
	}
	public void setTypeFlags(short typeFlags) {
		this.typeFlags = typeFlags;
	}
	
	public long getDataOffset() {
		return dataOffset;
	}
	public void setDataOffset(long dataOffset) {
		this.dataOffset = dataOffset;
	}
	@Override
	public String toString() {
		return String.format("version=%d, typeFlags=%d, dataOffset=%d", version, typeFlags, dataOffset);
	}
}
