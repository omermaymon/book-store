package bgu.spl.net.srv;

import java.io.IOException;
import bgu.spl.net.srv.ConnectionsImpl;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void send(String channel, T msg);

    void disconnect(int connectionId);

    void removeActiveUser(int connectionId);
}
