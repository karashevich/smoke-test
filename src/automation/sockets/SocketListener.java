package automation.sockets;

import org.apache.log4j.net.SocketAppender;

/**
 * Created by jetbrains on 07/04/16.
 */
public class SocketListener extends SocketAppender {

    final String SOCKET_URL = "http://localhost";
    final int SOCKET_PORT = 4560;
}
