package bgu.spl.net.api.messages;

public class Error extends ServerToClientMessages {

    private short typeOfError;
    private byte[] output;
    private byte[] toAdd;

    public Error(short typeOfError) {
        super();
        opCode=11;
        this.typeOfError = typeOfError;
        output = new byte[4];
    }

    @Override
    public byte[] encode() {
        toAdd = shortToBytes(opCode);
        toAdd = shortToBytes(opCode);
        output[0] = toAdd[0];
        output[1] = toAdd[1];
        toAdd = shortToBytes(typeOfError);
        output[2] = toAdd[0];
        output[3] =  toAdd[1];
        return output;
    }
}
