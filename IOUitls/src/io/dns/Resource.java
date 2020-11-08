package io.dns;

import java.nio.ByteBuffer;

/**
 * DNS 数据包Resource区域
 */
public class Resource {
    /**
     * 域名，与Question区域的域名类似，
     * 有一点不同就是，当报文中域名重复出现的时候，该字段使用2个字节的偏移指针来表示。
     * 比如：在资源记录中，域名通常是查询问题部分的域名的重复，因此用2字节的指针来表示，
     * 具体格式是最前面的两个高位是 11，用于识别指针。其余的14位从DNS报文的开始处计数
     * （从0开始），指出该报文中的相应字节数。一个典型的例子，C00C(1100000000001100，
     * 12正好是头部的长度，其正好指向Queries区域的查询名字字段)。
     */
    public String mDomain;

    /**
     * 查询类型。与Question区域的类型类似，
     */
    public short mQueryType;
    /**
     * 查询类CLASS。对于Internet信息，总是1，代表IN。表示RDATA的类
     */
    public short mQueryClass;
    /**
     * 生存时间。4字节无符号整数表示资源记录可以缓存的时间。
     * 以秒为单位，表示的是资源记录的生命周期，一般用于当地址解析程序取出资源记录后决定保存
     * 及使用缓存数据的时间，它同时也可以表明该资源记录的稳定程度，极为稳定的信息会被分配一
     * 个很大的值（比如86400，这是一天的秒数）。0代表只能被传输，但是不能被缓存。
     *
     * TTL 是 DNS 记录中的一个值，可决定对该记录所做的后续更改需要多少秒才会生效。网域的每条
     * DNS 记录（如 MX 记录、CNAME 记录等）都有一个 TTL 值。一条记录目前所设的 TTL 决定了您
     * 现在所做的任何更改需要多久才会生效。例如，如果一条记录的 TTL 为 86400 秒，则对该记录
     * 的更改最多需要 24 小时才会生效
     *
     * 请注意，更改记录的 TTL 会影响之后的所有更改需要多久才会生效。我们建议将 TTL 值设置为 3600，
     * 即让整个互联网中的服务器每小时检查一次该记录的更新情况。较短的 TTL 在之前的有效期到期后才
     * 会生效。这意味着，下次更新该记录时，您的更改最多要一个小时才会生效。要使后续的更改更快生效
     * （例如，如果您想快速还原一项更改），则可以设置较短的 TTL 值，如 300 秒（5 分钟）。正确配置
     * 记录后，我们建议将 TTL 值设置为 86400，即让整个互联网中的服务器每 24 小时检查一次记录的更新情况。
     */
    public int mTtl;
    /**
     * 资源数据长度。URDLENGT**H 2个字节无符号整数表示RDATA的长度
     */
    public short mDataLength;

    /**
     *资源数据 RDATA该字段是一个可变长字段，不定长字符串来表示记录，格式与TYPE和CLASS有关。
     * 比如，TYPE是A，CLASS 是 IN，那么RDATA就是一个4个字节的ARPA网络地址。表示按照查询段的
     * 要求返回的相关资源记录的数据。可以是Address（表明查询报文想要的回应是一个IP地址）或者
     * CNAME（表明查询报文想要的回应是一个规范主机名）等。
     */
    public byte[] mrData;

    private int offset;

    public int offset() {
        return offset;
    }

    private int length;

    public int length() {
        return length;
    }

    public Resource(){

    }

    public static Resource fromBytes(ByteBuffer buffer) {
        Resource r = new Resource();
        r.offset = buffer.arrayOffset() + buffer.position();
        r.mDomain = DnsPacket.readDomain(buffer, buffer.arrayOffset());
        r.mQueryType = buffer.getShort();
        r.mQueryClass = buffer.getShort();
        r.mTtl = buffer.getInt();
        r.mDataLength = buffer.getShort();
        r.mrData = new byte[r.mDataLength & 0xFFFF];
        buffer.get(r.mrData);
        r.length = buffer.arrayOffset() + buffer.position() - r.offset;
        return r;
    }

    public void toBytes(ByteBuffer buffer) {
        if (this.mrData == null) {
            this.mrData = new byte[0];
        }
        this.mDataLength = (short) this.mrData.length;

        this.offset = buffer.position();
        DnsPacket.WriteDomain(this.mDomain, buffer);
        buffer.putShort(this.mQueryType);
        buffer.putShort(this.mQueryClass);
        buffer.putInt(this.mTtl);

        buffer.putShort(this.mDataLength);
        buffer.put(this.mrData);
        this.length = buffer.position() - this.offset;
    }

    public int getFirstIp(){
        int ip = 0;
        ip |= mrData[0] << 24;
        ip |= mrData[1] << 16;
        ip |= mrData[2] << 8;
        ip |= mrData[3];
        return ip;
    }


    public String getIpString(){
        if(mrData.length == 4){
            StringBuilder stringBuilder = new StringBuilder(15);
            return stringBuilder.append(mrData[0] & 0xFF)
                    .append(".")
                    .append(mrData[1] & 0xFF)
                    .append(".")
                    .append(mrData[2] & 0xFF)
                    .append(".")
                    .append(mrData[3] & 0xFF)
                    .toString();
        }else{
            return "domain";
        }


    }

    @Override
    public String toString() {
        return "Resource{" +
                "mDomain='" + mDomain + '\'' +
                ", mQueryType=" + mQueryType +
                ", mQueryClass=" + (mQueryClass == 1 ? "IN" : "1") +
                ", mTtl=" + mTtl +
                ", mDataLength=" + mDataLength +
                ", mrData=" + getIpString() +
                ", offset=" + offset +
                ", length=" + length +
                '}';
    }

    static final short OFFSET_DOMAIN = 0;
    static final short OFFSET_TYPE = 2;
    static final short OFFSET_CLASS = 4;
    static final int OFFSET_TTL = 6;
    static final short OFFSET_DATA_LENGTH = 10;
    static final int OFFSET_IP = 12;

    ByteBuffer mData;
    int mDataOffset;

    public Resource(byte[] data, int offset) {
        this.mData = ByteBuffer.wrap(data);
        this.mDataOffset = offset;
    }

    public Resource setDomain(short value) {
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
