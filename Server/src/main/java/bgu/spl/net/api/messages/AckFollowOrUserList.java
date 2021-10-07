package bgu.spl.net.api.messages;

import java.util.ArrayList;

public class AckFollowOrUserList extends ACK {

    private short numOfusers;
    private ArrayList<String> users;
    private byte[] toAdd;
    private byte[] output;
    private short s;
    private int index;


    public AckFollowOrUserList(short s , short numOfusers , ArrayList<String> users) {
        super(s);
        this.s = s;
        this.numOfusers = numOfusers;
        this.users = users;
        output = new byte[1<<10];
        toAdd = new byte[2];
        index = 0;
    }

    @Override
    public  byte[] encode() {
        toAdd = shortToBytes(opCode);
        output[0] = toAdd[0];
        output[1] = toAdd[1];
        toAdd = shortToBytes(s);
        output[2] = toAdd[0];
        output[3] =  toAdd[1];
        toAdd = shortToBytes(numOfusers);
        output[4] = toAdd[0];
        output[5] = toAdd[1];
        index = 6;
        //adding users to the byte array
        for (String userName : users){
            toAdd = userName.getBytes();
            for (int i=0;i<toAdd.length;i++){
                output[index]=toAdd[i];
                index++;
            }
            output[index]='\0';
            index++;
        }
        return output;
    }
}
