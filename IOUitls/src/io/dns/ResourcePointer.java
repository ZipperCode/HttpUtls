package io.dns;


import java.nio.ByteBuffer;

public class ResourcePointer {
    static final short OFFSET_DOMAIN = 0;
    static final short OFFSET_TYPE = 2;
    static final short OFFSET_CLASS = 4;
    static final int OFFSET_TTL = 6;
    static final short OFFSET_DATA_LENGTH = 10;
    static final int OFFSET_IP = 12;

    ByteBuffer mData;
    int mDataOffset;

    public ResourcePointer(byte[] data, int offset) {
        this.mData = ByteBuffer.wrap(data);
        this.mDataOffset = offset;
    }

    public ResourcePointer setDomain(short value) {
        mData.putShort(mDataOffset + OFFSET_DOMAIN, value);
        return this;
    }

    public short getType() {
        return mData.getShort(mDataOffset + OFFSET_TYPE);
    }

    public void setType(short value) {
        mData.putShort( mDataOffset + OFFSET_TYPE, value);
    }

    public short getClass(short value) {
        return mData.getShort( mDataOffset + OFFSET_CLASS);
    }

    public void setClass(short value) {
        mData.putShort( mDataOffset + OFFSET_CLASS, value);
    }

    public int getTtl() {
        return mData.getInt(mDataOffset + OFFSET_TTL);
    }

    public void setTtl(int value) {
        mData.putInt(mDataOffset + OFFSET_TTL, value);
    }

    public short getDataLength() {
        return mData.getShort( mDataOffset + OFFSET_DATA_LENGTH);
    }

    public void setDataLength(short value) {
        mData.putShort( mDataOffset + OFFSET_DATA_LENGTH, value);
    }

    public int getIp() {
        return mData.getInt( mDataOffset + OFFSET_IP);
    }

    public void setIp(int value) {
        mData.putInt(mDataOffset + OFFSET_IP, value);
    }
}
