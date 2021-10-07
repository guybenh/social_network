

#include <iostream>
#include <../include/ReadFromKeyboard.h>
#include <connectionHandler.h>
#include "ReadFromKeyboard.h"
#include <boost/algorithm/string.hpp>
#include <thread>

ReadFromKeybord::ReadFromKeybord(ConnectionHandler &connectionHandler ) :
    connectionHandler(connectionHandler)  {}

void ReadFromKeybord::operator()() {
    //getting line from keyboard

    while (!connectionHandler.isTerminate()) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        {
            //encoding line
            if (!encode(line)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            if (line == "LOGOUT" && connectionHandler.isLogedIn()) {
                break;
            }
        }
    }
    connectionHandler.setTerminate(true);
}


bool ReadFromKeybord::encode(std::string line) {
    std::vector<std::string> input;
    boost::split(input,line, boost::is_any_of(" "));

    if (input[0] == "REGISTER")
        return sendRegisterMessage(input);

    else if (input[0]=="LOGIN")
        return sendLoginMessage(input);

    else if (input[0]=="LOGOUT")
        return sendLogoutMessage();

    else if (input[0]=="FOLLOW")
        return sendFollowMessage(input);

    else if (input[0]=="POST")
        return sendPostMessage(input);

    else if (input[0]=="PM")
        return sendPmMessage(input);

    else if (input[0]=="USERLIST")
        return sendUserListMessage();

    else if (input[0]=="STAT")
        return sendStatMessage(input);

    else
        return false;
}

bool
ReadFromKeybord::sendRegisterMessage(std::vector<std::string> input) {
    int numOfBytes = static_cast<int>(input[1].size() + input[2].size() + 4);
    char bytesArray[numOfBytes];
    int ptr = static_cast<int>(2 + input[1].size());
    shortToBytes(1, bytesArray);
    std::strcpy(&bytesArray[2], input[1].c_str());
    bytesArray[ptr] = '\0';
    ptr++;
    std::strcpy(&bytesArray[ptr], input[2].c_str());
    ptr += input[2].size();
    bytesArray[ptr] = '\0';
    return connectionHandler.sendBytes(bytesArray, numOfBytes);

}

bool
ReadFromKeybord::sendLoginMessage(std::vector<std::string> input) {
    connectionHandler.setLogedIn(true);
    int numOfBytes = static_cast<int>(input[1].size() + input[2].size() + 4);
    char bytesArray[numOfBytes];
    int ptr = static_cast<int>(2 + input[1].size());
    shortToBytes(2, bytesArray);
    std::strcpy(&bytesArray[2], input[1].c_str());
    bytesArray[ptr] = '\0';
    ptr++;
    std::strcpy(&bytesArray[ptr], input[2].c_str());
    ptr += input[2].size();
    bytesArray[ptr] = '\0';

    return connectionHandler.sendBytes(bytesArray, numOfBytes);
}

bool
ReadFromKeybord::sendLogoutMessage() {
    char bytesArray[2];
    shortToBytes(3, bytesArray);

    return connectionHandler.sendBytes(bytesArray, 2);
}

bool
ReadFromKeybord::sendFollowMessage(std::vector<std::string> input) {
    auto numOfPeople = static_cast<short>(std::stoi(input[2]));
    auto follow = static_cast<short>(std::stoi(input[1]));
    // finding how many bytes we will need for the char array
    int ctr = 0;
    for (int i=3;(unsigned) i<input.size(); i++){
        ctr += input[i].size();
    }
    int numOfBytes = ctr + numOfPeople + 5;
    // initializing the char array = bytes array
    char bytesArray[numOfBytes];
    int ptr = 5;
    shortToBytes(4, bytesArray);
    bytesArray[2] = static_cast<char>(follow);
    shortToBytes(numOfPeople, &bytesArray[3]);
    for (int i=3;(unsigned) i<input.size(); i++){
        std::strcpy(&bytesArray[ptr], input[i].c_str());
        ptr += input[i].size();
        bytesArray[ptr] = '\0';
        ptr++;
    }

     return connectionHandler.sendBytes(bytesArray, numOfBytes);
}

bool ReadFromKeybord::sendPostMessage(std::vector<std::string> input) {
    // finding the number of words
    int numOfLetters = 0;
    for (int i=1; (unsigned)i<input.size(); i++){
        numOfLetters += input[i].size();
    }
    // finding the number of total bytes needed
    int numOfBytes = static_cast<int>(numOfLetters + (input.size() - 2) + 3);
    // setting a pointer to help copying the strings from the vector to the char array
    int ptr = 2;
    char bytesArray[numOfBytes];
    shortToBytes(5, bytesArray);
    for (int i=1; (unsigned)i<input.size();i++){
        std::strcpy(&bytesArray[ptr], input[i].c_str());
        ptr += input[i].size();
        bytesArray[ptr] = ' ';
        ptr++;
    }
    bytesArray[ptr-1] = '\0';

    return connectionHandler.sendBytes(bytesArray, numOfBytes);
}

bool ReadFromKeybord::sendPmMessage(std::vector<std::string> input) {
    int numOfWords = static_cast<int>(input.size() - 2);
    int ctr = 0;
    for (int i=1;(unsigned) i<input.size();i++){
        ctr += input[i].size();
    }
    int numOfBytes = 4 + numOfWords-1 + ctr;
    char bytesArray[numOfBytes];
    int ptr = 2;
    shortToBytes(6, bytesArray);
    std::strcpy(&bytesArray[ptr], input[1].c_str());
    ptr += input[1].size();
    bytesArray[ptr] = '\0';
    ptr ++ ;
    for (int i=2;(unsigned) i<input.size();i++){
        std::strcpy(&bytesArray[ptr], input[i].c_str());
        ptr += input[i].size();
        bytesArray[ptr] = ' ';
        ptr++;
    }
    bytesArray[ptr-1] = '\0';

    return connectionHandler.sendBytes(bytesArray, numOfBytes);
}

bool
ReadFromKeybord::sendUserListMessage() {
    char bytesArray[2];
    shortToBytes(7, bytesArray);

    return connectionHandler.sendBytes(bytesArray, 2);
}

bool ReadFromKeybord::sendStatMessage(std::vector<std::string> input) {
    int numOfBytes = static_cast<int>(3 + input[1].size());
    char bytesArray[numOfBytes];
    shortToBytes(8, bytesArray);
    std::strcpy(&bytesArray[2], input[1].c_str());
    bytesArray[2+input[1].size()] = '\0';

    return connectionHandler.sendBytes(bytesArray, numOfBytes);


}


void ReadFromKeybord::shortToBytes(short num, char *opCode) {
    opCode[0] = static_cast<char>((num >> 8) & 0xFF);
    opCode[1] = static_cast<char>(num & 0xFF);
}

