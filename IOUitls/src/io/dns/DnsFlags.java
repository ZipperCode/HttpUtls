package io.dns;


import java.nio.ByteBuffer;

public class DnsFlags {

    /**
     * 1bit
     * QR（Response）：查询请求/响应的标志信息。查询请求时，值为 0；响应时，值为 1。
     */
    public boolean mQr;
    /**
     * 4bit
     * Opcode：操作码。其中，0 表示标准查询；1 表示反向查询；2 表示服务器状态请求。
     */
    public int mOpCode;
    /**
     * 1bit
     * AA（Authoritative）：授权应答，该字段在响应报文中有效。值为 1 时，
     * 表示名称服务器是权威服务器；值为 0 时，表示不是权威服务器。
     */
    public boolean mAa;
    /**
     * 1bit
     * TC（Truncated）：表示是否被截断。值为 1 时，表示响应已超过 512 字节并已被截断，只返回前 512 个字节。
     */
    public boolean mTc;
    /**
     * 1bit
     * RD（Recursion Desired）：期望递归。该字段能在一个查询中设置，并在响应中返回。
     * 该标志告诉名称服务器必须处理这个查询，这种方式被称为一个递归查询。如果该位为 0，
     * 且被请求的名称服务器没有一个授权回答，它将返回一个能解答该查询的其他名称服务器列表。
     * 这种方式被称为迭代查询。
     */
    public boolean mRd;
    /**
     * 1bit
     * RA（Recursion Available）：可用递归。该字段只出现在响应报文中。当值为 1 时，表示服务器支持递归查询。
     */
    public boolean mRa;
    /**
     * 3bit
     * Z：保留字段，在所有的请求和应答报文中，它的值必须为 0。
     */
    public int mZ;
    /**
     * 4bit
     * rCode（Reply code）：返回码字段，表示响应的差错状态。
     * 当值为 0 时，表示没有错误；
     * 当值为 1 时，表示报文格式错误（Format error），服务器不能理解请求的报文；
     * 当值为 2 时，表示域名服务器失败（Server failure），因为服务器的原因导致没办法处理这个请求；
     * 当值为 3 时，表示名字错误（Name Error），只有对授权域名解析服务器有意义，指出解析的域名不存在；
     * 当值为 4 时，表示查询类型不支持（Not Implemented），即域名服务器不支持查询类型；
     * 当值为 5 时，表示拒绝（Refused），一般是服务器由于设置的策略拒绝给出应答，如服务器不希望对某些请求者给出应答。
     */
    public int mrCode;


    public static DnsFlags parse(short value) {
        int flagsNum = value & 0xFFFF;
        DnsFlags flags = new DnsFlags();
        flags.mQr = ((flagsNum >> 15) & 0x01) == 1;
        flags.mOpCode = (flagsNum >> 11) & 0x0F;
        flags.mAa = ((flagsNum >> 10) & 0x01) == 1;
        flags.mTc = ((flagsNum >> 9) & 0x01) == 1;
        flags.mRd = ((flagsNum >> 8) & 0x01) == 1;
        flags.mRa = ((flagsNum >> 7) & 0x01) == 1;
        flags.mZ = (flagsNum >> 4) & 0x07;
        flags.mrCode = (flagsNum  & 0xF);
        return flags;
    }

    public static short format(DnsFlags dnsFlags){
        short flagNum = 0;
        flagNum |= (dnsFlags.mQr ? 0x01 : 0x00) << 15;
        flagNum |= (dnsFlags.mOpCode & 0x0F) << 12;
        flagNum |= (dnsFlags.mAa ? 0x01 : 0x00) << 11;
        flagNum |= (dnsFlags.mTc ? 0x01 : 0x00) << 10;
        flagNum |= (dnsFlags.mRd ? 0x01 : 0x00) << 9;
        flagNum |= (dnsFlags.mRa ? 0x01 : 0x00) << 8;
        flagNum |= (dnsFlags.mZ & 0x07) << 4;
        flagNum |= dnsFlags.mrCode & 0x0F;
        return flagNum;
    }

    public void toBuffer(ByteBuffer byteBuffer){
        short flagNum = 0;
        flagNum |= (this.mQr ? 0x01 : 0x00) << 15;
        flagNum |= (this.mOpCode & 0x0F) << 12;
        flagNum |= (this.mAa ? 0x01 : 0x00) << 11;
        flagNum |= (this.mTc ? 0x01 : 0x00) << 10;
        flagNum |= (this.mRd ? 0x01 : 0x00) << 9;
        flagNum |= (this.mRa ? 0x01 : 0x00) << 8;
        flagNum |= (this.mZ & 0x07) << 4;
        flagNum |= this.mrCode & 0x0F;
        byteBuffer.putShort(flagNum);
    }

    public short toShort() {
        short flagNum = 0;
        flagNum |= (this.mQr ? 0x01 : 0x00) << 15;
        flagNum |= (this.mOpCode & 0x0F) << 12;
        flagNum |= (this.mAa ? 0x01 : 0x00) << 11;
        flagNum |= (this.mTc ? 0x01 : 0x00) << 10;
        flagNum |= (this.mRd ? 0x01 : 0x00) << 9;
        flagNum |= (this.mRa ? 0x01 : 0x00) << 8;
        flagNum |= (this.mZ & 0x07) << 4;
        flagNum |= this.mrCode & 0x0F;
        return flagNum;
    }

    @Override
    public String toString() {
        return "DnsFlags{" +
                "mQr=" + (mQr ? 1 : 0) +
                ", mOpCode=" + mOpCode +
                ", mAa=" + (mAa ? 1: 0) +
                ", mTc=" + (mTc ? 1: 0) +
                ", mRd=" + (mRd ? 1: 0) +
                ", mRa=" + (mRa ? 1 : 0) +
                ", mZ=" + "0x" + Integer.toHexString(mZ).toUpperCase() +
                ", mrCode=" +  "0x" + Integer.toHexString(mrCode).toUpperCase() +
                '}';
    }
}
