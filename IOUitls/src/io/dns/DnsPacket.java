package io.dns;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DnsPacket {

    public DnsHeader mHeader;

    public Question[] mQuestions;

    public Resource[] mAnswers;

    public Resource[] mAuthoritys;

    public Resource[] mAdditionals;

    public int mSize;

    public static DnsPacket parseFromBuffer(ByteBuffer buffer){
        if (buffer.limit() < 12){
            return null;
        }
        if (buffer.limit() > 512){
            return null;
        }


        DnsPacket dnsPacket = new DnsPacket();
        //
        dnsPacket.mSize = buffer.limit();
        dnsPacket.mHeader = DnsHeader.fromBytes(buffer);
        if (dnsPacket.mHeader.mQuestionCount > 2
                || dnsPacket.mHeader.mAnswerCount > 50
                || dnsPacket.mHeader.mAuthorityCount > 50
                || dnsPacket.mHeader.mAdditionalCount > 50) {
            return null;
        }
        dnsPacket.mQuestions = new Question[dnsPacket.mHeader.mQuestionCount];
        dnsPacket.mAnswers = new Resource[dnsPacket.mHeader.mAnswerCount];
        dnsPacket.mAuthoritys = new Resource[dnsPacket.mHeader.mAuthorityCount];
        dnsPacket.mAdditionals = new Resource[dnsPacket.mHeader.mAdditionalCount];

        for (int i = 0; i < dnsPacket.mQuestions.length; i++) {
            dnsPacket.mQuestions[i] = Question.fromBytes(buffer);
        }

        for (int i = 0; i < dnsPacket.mAnswers.length; i++) {
            dnsPacket.mAnswers[i] = Resource.fromBytes(buffer);
        }

        for (int i = 0; i < dnsPacket.mAuthoritys.length; i++) {
            dnsPacket.mAuthoritys[i] = Resource.fromBytes(buffer);
        }

        for (int i = 0; i < dnsPacket.mAdditionals.length; i++) {
            dnsPacket.mAdditionals[i] = Resource.fromBytes(buffer);
        }

        return dnsPacket;
    }


    @Deprecated
    public static DnsPacket fromBytes(ByteBuffer buffer) {
        if (buffer.limit() < 12){
            return null;
        }
        if (buffer.limit() > 512){
            return null;
        }

        DnsPacket packet = new DnsPacket();
        packet.mSize =  buffer.limit();
        packet.mHeader = DnsHeader.fromBytes(buffer);

        if (packet.mHeader.mQuestionCount > 2 || packet.mHeader.mAnswerCount > 50 || packet.mHeader.mAuthorityCount > 50 || packet.mHeader.mAdditionalCount > 50) {
            return null;
        }

        packet.mQuestions = new Question[packet.mHeader.mQuestionCount];
        packet.mAnswers = new Resource[packet.mHeader.mAnswerCount];
        packet.mAuthoritys = new Resource[packet.mHeader.mAuthorityCount];
        packet.mAdditionals = new Resource[packet.mHeader.mAdditionalCount];

        for (int i = 0; i < packet.mQuestions.length; i++) {
            packet.mQuestions[i] = Question.fromBytes(buffer);
        }

        for (int i = 0; i < packet.mAnswers.length; i++) {
            packet.mAnswers[i] = Resource.fromBytes(buffer);
        }

        for (int i = 0; i < packet.mAuthoritys.length; i++) {
            packet.mAuthoritys[i] = Resource.fromBytes(buffer);
        }

        for (int i = 0; i < packet.mAdditionals.length; i++) {
            packet.mAdditionals[i] = Resource.fromBytes(buffer);
        }

        return packet;
    }

    public void toBytes(ByteBuffer buffer) {
        mHeader.mQuestionCount = 0;
        mHeader.mAnswerCount = 0;
        mHeader.mAuthorityCount = 0;
        mHeader.mAdditionalCount = 0;

        if (mQuestions != null){
            mHeader.mQuestionCount = (short) mQuestions.length;
        }
        if (mAnswers != null){
            mHeader.mAnswerCount = (short) mAnswers.length;
        }
        if (mAuthoritys != null){
            mHeader.mAuthorityCount = (short) mAuthoritys.length;
        }
        if (mAdditionals != null){
            mHeader.mAdditionalCount = (short) mAdditionals.length;
        }

        this.mHeader.toBytes(buffer);

        for (int i = 0; i < mHeader.mQuestionCount; i++) {
            this.mQuestions[i].toBytes(buffer);
        }

        for (int i = 0; i < mHeader.mAnswerCount; i++) {
            this.mAnswers[i].toBytes(buffer);
        }

        for (int i = 0; i < mHeader.mAuthorityCount; i++) {
            this.mAuthoritys[i].toBytes(buffer);
        }

        for (int i = 0; i < mHeader.mAdditionalCount; i++) {
            this.mAdditionals[i].toBytes(buffer);
        }
        buffer.limit(buffer.position());
    }

    public static String readDomain(ByteBuffer buffer, int dnsHeaderOffset) {
        StringBuilder sb = new StringBuilder();
        int len = 0;
        while (buffer.hasRemaining() && (len = (buffer.get() & 0xFF)) > 0) {
            if ((len & 0xc0) == 0xc0)// pointer 高2位为11表示是指针。如：1100 0000
            {
                // 指针的取值是前一字节的后6位加后一字节的8位共14位的值。
                int pointer = buffer.get() & 0xFF;// 低8位
                pointer |= (len & 0x3F) << 8;// 高6位

                ByteBuffer newBuffer = ByteBuffer.wrap(buffer.array(), dnsHeaderOffset + pointer, dnsHeaderOffset + buffer.limit());
                sb.append(readDomain(newBuffer, dnsHeaderOffset));
                return sb.toString();
            } else {
                while (len > 0 && buffer.hasRemaining()) {
                    sb.append((char) (buffer.get() & 0xFF));
                    len--;
                }
                sb.append('.');
            }
        }

        if (len == 0 && sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);//去掉末尾的点（.）
        }
        return sb.toString();
    }

    public static void WriteDomain(String domain, ByteBuffer buffer) {
        if (domain == null || domain == "") {
            buffer.put((byte) 0);
            return;
        }

        String[] arr = domain.split("\\.");
        for (String item : arr) {
            if (arr.length > 1) {
                buffer.put((byte) item.length());
            }

            for (int i = 0; i < item.length(); i++) {
                buffer.put((byte) item.codePointAt(i));
            }
        }
        buffer.put((byte) 0x00);
    }

    @Override
    public String toString() {
        return "DnsPacket{" +
                "mHeader=" + mHeader +
                ", mQuestions=" + Arrays.toString(mQuestions) +
                ", mAnswers=" + Arrays.toString(mAnswers) +
                ", mAuthoritys=" + Arrays.toString(mAuthoritys) +
                ", mAdditionals=" + Arrays.toString(mAdditionals) +
                ", mSize=" + mSize +
                '}';
    }
}
