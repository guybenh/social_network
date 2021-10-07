package bgu.spl.net.api.messages;


public class ACK extends ServerToClientMessages {
    public byte[] toAdd;
    public byte[] output;
    short typeOfACK;

    public ACK(short s) {
        super();
        this.typeOfACK =s;
        opCode=10;
        toAdd = new byte[2];
        output=new byte[4];
    }

    @Override
    public  byte[] encode() {
        toAdd = shortToBytes(opCode);
        output[0] = toAdd[0];
        output[1] = toAdd[1];
        toAdd = shortToBytes(typeOfACK);
        output[2] = toAdd[0];
        output[3] =  toAdd[1];

        return output;
    }
}
