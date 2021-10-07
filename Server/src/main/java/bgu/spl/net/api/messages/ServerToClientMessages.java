package bgu.spl.net.api.messages;


public abstract class ServerToClientMessages {

    public short opCode;

    public ServerToClientMessages() {
    }

    public abstract byte[] encode();

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}


