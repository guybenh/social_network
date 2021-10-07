#include <stdlib.h>
#include <connectionHandler.h>
#include <ReadFromKeyboard.h>
#include <../include/ReadFromServer.h>
#include <thread>
#include <mutex>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = static_cast<short>(atoi(argv[2]));

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    //From here we will see the rest of the ehco client implementation::

    ReadFromKeybord readFromKeyboard(connectionHandler);
    ReadFromServer readFromServer(connectionHandler);
    //will start the threads
    std::thread t1(std::ref(readFromKeyboard));
    std::thread t2(std::ref(readFromServer));
    t2.join();
    t1.join();


    return 0;
}


