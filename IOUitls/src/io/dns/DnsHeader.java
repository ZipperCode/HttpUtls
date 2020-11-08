package io.dns;

import java.nio.ByteBuffer;

/**
 * DNS 报头格式
 */
public class DnsHeader {

    public static final int DNS_HEADER_SIZE = 12;
    /**
     * 事务ID DNS 报文的 ID 标识。对于请求报文和其对应的应答报文，该字段的值是相同的。
     * 通过它可以区分 DNS 应答报文是对哪个请求进行响应的。
     */
    public short mTransactionId;
    /**
     * DNS 标志
     * DNS 报文中的标志字段。
     */
    public DnsFlags mDnsFlags;
    /**
     * 问题计数
     * DNS 查询请求的数目。
     */
    public short mQuestionCount;
    /**
     * 回答资源计数
     * DNS 响应的数目。
     */
    public short mAnswerCount;
    /**
     * 权威名称服务器计数
     * 权威名称服务器的数目。
     */
    public short mAuthorityCount;
    /**
     * 附加资源计数
     * 额外的记录数目（权威名称服务器对应 IP 地址的数目）
     */
    public short mAdditionalCount;


    public static DnsHeader fromBytes(ByteBuffer buffer) {
        DnsHeader header = new DnsHeader(buffer.array(), buffer.arrayOffset() + buffer.position());
        header.mTransactionId = buffer.getShort();
        header.mDnsFlags = DnsFlags.parse(buffer.getShort());
        header.mQuestionCount = buffer.getShort();
        header.mAnswerCount = buffer.getShort();
        header.mAuthorityCount = buffer.getShort();
        header.mAdditionalCount = buffer.getShort();
        return header;
    }

    public void toBytes(ByteBuffer buffer) {
        buffer.putShort(this.mTransactionId);
        buffer.putShort(this.mDnsFlags.toShort());
        buffer.putShort(this.mQuestionCount);
        buffer.putShort(this.mAnswerCount);
        buffer.putShort(this.mAuthorityCount);
        buffer.putShort(this.mAdditionalCount);
    }

    /* ID偏移 */
    public static final short OFFSET_ID = 0;
    /* 标志位偏移 */
    public static final short OFFSET_FLAGS = 2;
    /* 问题数偏移 */
    public static final short OFFSET_QUESTION_COUNT = 4;
    /* 回答数偏移 */
    public static final short OFFSET_ANSWER_COUNT = 6;
    /* 授权数 */
    public static final short OFFSET_AUTHORITY_COUNT = 8;
    /* 附加偏移 */
    public static final short OFFSET_ADDITIONAL_COUNT = 10;

    private ByteBuffer mData;
    private int mDataOffset;

    public DnsHeader() {
    }

    public DnsHeader(byte[] data, int offset) {
        this.mData = ByteBuffer.wrap(data);

        this.mDataOffset = offset;
    }

    public short getTransactionId() {
        return mData.getShort(OFFSET_ID);
    }

    public short getDnsFlags() {
        return mData.getShort(OFFSET_FLAGS);
    }

    public short getQuestionCount() {
        return mData.getShort(OFFSET_QUESTION_COUNT);
    }

    public short getAnswerCount() {
        return mData.getShort(OFFSET_ANSWER_COUNT);
    }

    public short getAuthorityCount() {
        return mData.getShort(OFFSET_AUTHORITY_COUNT);
    }

    public short getAdditionalCount() {
        return mData.getShort(OFFSET_ADDITIONAL_COUNT);
    }

    public DnsHeader setTransactionId(short transactionId) {
        mData.putShort(OFFSET_ID, transactionId);
        return this;
    }

    public DnsHeader setDnsFlags(short dnsFlags) {
        mData.putShort(OFFSET_FLAGS, dnsFlags);
        return this;
    }

    public DnsHeader setDnsFlags(DnsFlags dnsFlags) {
        mData.putShort(OFFSET_FLAGS, DnsFlags.format(dnsFlags));
        return this;
    }

    public DnsHeader setQuestionCount(short questionCount) {
        mData.putShort(OFFSET_QUESTION_COUNT, questionCount);
        return this;
    }

    public DnsHeader setAnswerCount(short answerCount) {
        mData.putShort(OFFSET_ANSWER_COUNT, answerCount);
        return this;
    }

    public DnsHeader setAuthorityCount(short authorityCount) {
        mData.putShort(OFFSET_AUTHORITY_COUNT, authorityCount);
        return this;
    }

    public DnsHeader setAdditionalCount(short additionalCount) {
        mData.putShort(OFFSET_ADDITIONAL_COUNT, additionalCount);
        return this;
    }

    @Override
    public String toString() {
        return "DnsHeader{" +
                "事务ID = 0x" + Integer.toHexString(mTransactionId).toUpperCase() +
                ", mDnsFlags=" + mDnsFlags +
                ", mQuestionCount=" + mQuestionCount +
                ", mAnswerCount=" + mAnswerCount +
                ", mAuthorityCount=" + mAuthorityCount +
                ", mAdditionalCount=" + mAdditionalCount +
                ", mData=" + mData +
                ", mDataOffset=" + mDataOffset +
                '}';
    }
}
