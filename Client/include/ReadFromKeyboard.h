//
// Created by guy on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_READFROMKEYBORD_H
#define BOOST_ECHO_CLIENT_READFROMKEYBORD_H

#include <connectionHandler.h>
#include <mutex>
#include <condition_variable>



class ReadFromKeybord {
private:
    ConnectionHandler &connectionHandler;




public:
    explicit ReadFromKeybord(ConnectionHandler &connectionHandler );

    void operator()();

    bool encode(std::string basic_string);

    bool sendRegisterMessage(std::vector<std::string> vector);

    bool sendLoginMessage(std::vector<std::string> vector);

    bool sendLogoutMessage();

    bool sendFollowMessage(std::vector<std::string> vector);

    bool sendPostMessage(std::vector<std::string> vector);

    bool sendPmMessage(std::vector<std::string> vector);

    bool sendUserListMessage();

    bool sendStatMessage(std::vector<std::string> vector);

    void shortToBytes(short num, char* bytesArr);


};





#endif //BOOST_ECHO_CLIENT_READFROMKEYBORD_H
