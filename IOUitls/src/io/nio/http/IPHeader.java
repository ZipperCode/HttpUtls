package io.nio.http;

import java.nio.ByteBuffer;

public class IPHeader {

	/**
	 * IP协议版本号（4bit）：	4 - 0100 为 IPv4  6 - 0110 为IPv6
	 * IP头长度（4bit):		IP头的字节长度，最大60字节,一般为20字节。
	 * 计算：				头部总字节数 * 8 / 32 （20 * 8 / 32 = 5 - 0101 ）
	 */
	static final int VER_AND_HEADER_LEN_OFFSET = 0;
	/**
	 * 服务类型（8bit）：前三位定义包的优先级
	 * PPP: 				000-111
	 * 依次为：普通的->优先的->立即地发送->闪电式的->比闪电式还快的->CRI/TIC/ECP->网间控制->网络控制
	 * D 时延: 				0:普通 1:延迟尽量小
	 * T 吞吐量: 			0:普通 1:流量尽量大
	 * R 可靠性: 			0:普通 1:可靠性尽量大
	 * M 传输成本: 			0:普通 1:成本尽量小
	 * 0 最后一位被保留，		恒定为0
	 */
	static final int TYPE_OF_SERVICE_OFFSET = 1;

	/**
	 * IP数据包的总长度(16bit)，包括头部和数据，最大值65535，由于MTU限制一般为1500
	 */
	static final int TOTAL_LEN_OFFSET = 2;

	/**
	 * 身份标识（16bit）将较大的数据包进行分段，为了保证数据的完整，每一个分段的包,<br>
	 * 都必须包含该标识
	 */
	static final int IDENTIFIER_OFFSET = 4;

	/**
	 * 标记和片偏移（16bit）
	 * Flags（3bit) 0 不使用 ， 1 DF位该标志位1时标识不进行分段，2 MF位 分段后最后一段数据包该位置1表示结束
	 * Fragment Offset(13bit) 数据包分片时，每一片数据包的分段位置，用于数据包重组
	 */
	static final int FLAGS_AND_FRAGMENT_OFFSET = 6;

	/**
	 * 生存时间（8bit）
	 */
	static final int TTL_OFFSET = 8;

	/**
	 * 上层协议（8bit）	6-TCP，17-UDP
	 */
	static final int PROTOCOL_OFFSET = 9;

	/**
	 * 头部校验（16bit）		IP头校验
	 */
	static final int HEADER_CHECKSUM_OFFSET = 10;

	/**
	 * 源IP地址
	 */
	static final int SOURCE_ADDRESS_OFFSET = 12;

	/**
	 * 目标IP地址
	 */
	static final int DESTINATION_ADDRESS_OFFSET = 16;

	private ByteBuffer mData;
	private int mDataOffset;

	public IPHeader(ByteBuffer mData, int mDataOffset) {
		this.mData = mData;
		this.mDataOffset = mDataOffset;
	}


}
