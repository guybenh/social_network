//
// Created by guy on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_READFROMSERVER_H
#define BOOST_ECHO_CLIENT_READFROMSERVER_H


#include <mutex>
#include <condition_variable>
#include "connectionHandler.h"

class ReadFromServer {

private:
    ConnectionHandler& connectionHandler;



public:
    ReadFromServer(ConnectionHandler &connectionHandler );

    void operator()();
    void decode(char *type) ;

    short bytesToShort(char* bytesArr);

    void printNotificationMessage(char *type);

    void printACKMessage(char *type);

    void printErrorMessage(char *type);
};


#endif //BOOST_ECHO_CLIENT_READFROMSERVER_H
