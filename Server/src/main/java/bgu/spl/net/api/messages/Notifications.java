package bgu.spl.net.api.messages;


public class Notifications extends ServerToClientMessages {

    private char noticationType; // 0 - PM, 1 - public
    private String postingUser;
    private String content;


    private byte[] output;
    private byte[] toAdd;
    private int index;

    public Notifications(char notificationTypeType, String postingUser, String content) {
        super();
        opCode = 9;
        output=new byte[1<<10];
        toAdd = new byte[2];
        this.index =0;
        this.noticationType=notificationTypeType;
        this.postingUser=postingUser;
        this.content=content;

    }

    @Override
    public byte[] encode() {
        toAdd = shortToBytes(opCode);
        output[0] = toAdd[0];
        output[1] = toAdd[1];
        byte b = (byte)noticationType;
        output[2] = b;

        //adding postingUser String
        toAdd = postingUser.getBytes();
        for(index=3; index<toAdd.length+3; index++)
            output[index]=toAdd[index-3];
        output[index]='\0';
        index++;

        //adding content String
        toAdd=content.getBytes();
        int i = index;
        for(index = i; index<toAdd.length+i; index++)
            output[index]=toAdd[index-i];
        output[index]='\0';

        return output;
    }

}
