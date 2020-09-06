package io.dns;


import java.nio.ByteBuffer;

/**
 * DNS 数据包的Queries区域
 */
public class Question {
    /**
     * 单个最大63字节
     *查询名：一般为要查询的域名，有时也会是 IP 地址，用于反向查询。
     * 例如：域名www.baidu.com
     * 转成十六进制方式： 03 www 05 baidu 03 com 00(以00结束)，字符需要转化为十六进制
     */
    public String mQueryDomain;
    /**
     *查询类型：DNS 查询请求的资源类型。通常查询类型为 A 类型，表示由域名获取对应的 IP 地址。
     * 1 - A        : 由域名获得IPv4地址
     * 2 - NS       : 查询授权的域名服务器
     * 5 - CNAME    : 查询规范名称（别名）
     * 6 - SOA      : 开始授权
     * 11 - WKS     : 熟知服务
     * 12 - PTR     : 把IP地址转换成域名（指针记录，反向查询）
     * 13 - HINFO   : 主机信息
     * 15 - MX      : 邮件交换记录
     * 28 - AAAA    : 由域名获得IPv6地址
     * 252 - AXFR   : 对区域转换的请求，传送整个区的请求。
     * 255 - ANY    : 对所有记录的请求 *
     */
    public short mQueryType;
    /**
     *查询类：地址类型，通常为互联网地址，值为 1。
     */
    public short mQueryClass;
    /**
     *
     */
    private int mOffset;

    public int offset() {
        return mOffset;
    }

    private int length;

    public int length() {
        return length;
    }

    public static Question fromBytes(ByteBuffer buffer) {
        Question q = new Question();
        q.mOffset = buffer.arrayOffset() + buffer.position();
        q.mQueryDomain = DnsPacket.readDomain(buffer, buffer.arrayOffset());
        q.mQueryType = buffer.getShort();
        q.mQueryClass = buffer.getShort();
        q.length = buffer.arrayOffset() + buffer.position() - q.mOffset;
        return q;
    }

    public void toBytes(ByteBuffer buffer) {
        this.mOffset = buffer.position();
        DnsPacket.WriteDomain(this.mQueryDomain, buffer);
        buffer.putShort(this.mQueryType);
        buffer.putShort(this.mQueryClass);
        this.length = buffer.position() - this.mOffset;
    }

    @Override
    public String toString() {
        return "Question{" +
                "mQueryDomain='" + mQueryDomain +
                ", mQueryType=" + mQueryType +
                ", mQueryClass=" + (mQueryClass == 1 ? "IN" : "1")  +
                ", mOffset=" + mOffset +
                ", length=" + length +
                '}';
    }
}
