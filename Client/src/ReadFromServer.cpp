//
// Created by guy on 12/29/18.
//

#include <../include/ReadFromServer.h>

#include "ReadFromServer.h"

ReadFromServer::ReadFromServer(ConnectionHandler &connectionHandler ) :
connectionHandler(connectionHandler)  {}


void ReadFromServer::operator()() {
    char type[2]; //needs to delete in the end
    while (1) {

//        std::lock_guard<std::mutex> lock(mutex);
        if (!connectionHandler.getBytes(type, 2)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
        }

        //needs to decode answer
        decode(type);
        if (connectionHandler.isTerminate()) { //we got logout
            std::cout << "Exiting...\n" << std::endl;
            break;
        }
    }

}



    void ReadFromServer::decode(char *type) {
    short opCode = bytesToShort(type);
    if(opCode ==9)
        printNotificationMessage(type);
    else if(opCode==10)
        printACKMessage(type);
    else if(opCode==11)
        printErrorMessage(type);


}
short ReadFromServer::bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void ReadFromServer::printNotificationMessage(char *type) {
    std::string output = "NOTIFICATION ";
    connectionHandler.getBytes(type, 1);
    if(type[0]=='0')
        output=output+"PM ";
    else
        output=output+"Public ";
    //adding postingUser
    std::string postingUser = "";
    connectionHandler.getBytes(type, 1);
    while (type[0]!='\0') {
        postingUser = postingUser + type[0];
        connectionHandler.getBytes(type, 1);
    }
    output=output+" "+postingUser;
    //adding content
    std::string content = "";
    int counter = 0;
    connectionHandler.getBytes(type, 1);
    while (type[0]!='\0') {
        counter++;
        content = content + type[0];
        connectionHandler.getBytes(type, 1);
    }
    if(counter%2!=0)
        connectionHandler.getBytes(type, 1);
    output=output+" "+content;

    std::cout<<output<<std::endl;
}


void ReadFromServer::printACKMessage(char *type) {
    std::string output = "ACK";
    connectionHandler.getBytes(type, 2);
    short ackType = bytesToShort(type);
    output = output + " " + std::to_string(ackType);
    //check if its LOGOUT , will stop the program
    if(ackType==3)
        connectionHandler.setTerminate(true);
    //checks if it UserList or Follow ACK
    if (ackType == 4 | ackType == 7) {
        //getting the rest of the message
        std::string answer;
        connectionHandler.getBytes(type, 2);
        short numOfusers = bytesToShort(type);
        output =  output+" " + std::to_string(numOfusers);
        std::string userName = "";
        //inserting users to the string
        for(int i = 0;i<numOfusers;i++) {
            connectionHandler.getBytes(type, 1);
            while (type[0] != '\0') {
                userName = userName + type[0];
                connectionHandler.getBytes(type, 1);
            }
            output = output + " " + userName;
            userName = "";
        }
    }
    //checks if it Stat ACK
    if (ackType == 8) {
        //getting numPosts , numFolowers , numFollowing
        for (int i = 0; i < 3; i++) {
            connectionHandler.getBytes(type, 2);
            short sh = bytesToShort(type);
            output = output + " " + std::to_string(sh);
        }
    }

    std::cout << output << std::endl;

}

void ReadFromServer::printErrorMessage(char *type) {
    std::string output = "ERROR";
    connectionHandler.getBytes(type, 2);
    short errorType = bytesToShort(type);
    output = output + " " + std::to_string(errorType);
    std::cout<<output<<std::endl;
}


