package bgu.spl.net.api.messages;

public class AckStat extends ACK {

    private short s;
    private int numPosts;
    private int numFollowers;
    private int numFolowing;
    private byte[] toAdd;
    private byte[] output;


    public AckStat(short s ,int numPosts ,int numFollowers ,int numFolowing) {
        super(s);
        this.s = s;
        this.numPosts = numPosts;
        this.numFollowers = numFollowers;
        this.numFolowing = numFolowing;
        output = new byte[10];
        toAdd = new byte[2];
    }

    @Override
    public  byte[] encode() {
        toAdd = shortToBytes(opCode);
        output[0] = toAdd[0];
        output[1] = toAdd[1];
        toAdd = shortToBytes(s);
        output[2] = toAdd[0];
        output[3] =  toAdd[1];
        toAdd = shortToBytes((short)numPosts);
        output[4] = toAdd[0];
        output[5] = toAdd[1];
        toAdd = shortToBytes((short)numFollowers);
        output[6] = toAdd[0];
        output[7] = toAdd[1];
        toAdd = shortToBytes((short)numFolowing);
        output[8] = toAdd[0];
        output[9] = toAdd[1];

        return output;
    }
}
