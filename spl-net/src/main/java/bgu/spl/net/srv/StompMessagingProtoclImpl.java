package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import bgu.spl.net.ServerMessages.ConnectedFrame;
import bgu.spl.net.ServerMessages.ErrorFrame;
import bgu.spl.net.ServerMessages.MessageFrame;
import bgu.spl.net.ServerMessages.ReceiptFrame;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.User;
import bgu.spl.net.srv.DataBase;

public class StompMessagingProtoclImpl implements StompMessagingProtocol<Frame> {
    private int id;
    private boolean shouldTerminate = false;
    private Connections connections;
    private DataBase dataBase;
    private static AtomicInteger msgCounter = new AtomicInteger(0);

    @Override
    public void start(int connectionId, Connections<Frame> connections) { //TODO check if connections settings are correct
        this.id = connectionId;
        this.connections = connections;
        this.dataBase = DataBase.getInstance();
    }
    //processor...........................................................................................................................................
    @Override
    public void process(Frame message) {
        System.out.println("processing:" + message.getHeadLine() + " request");
        if (message.getHeadLine().equals("CONNECT")) {
            Frame toSend = processConnect(message);
            connections.send(id, toSend);
        } else if (message.getHeadLine().equals("SEND")) {
            MessageFrame toSend = processSend(message);
            String channel = message.getHeaders().get("destination");
            connections.send(channel, toSend);
        } else if (message.getHeadLine().equals("SUBSCRIBE")) {
            Frame toSend = processSubscribe(message);
            connections.send(id, toSend);
        } else if (message.getHeadLine().equals("UNSUBSCRIBE")) {
            Frame toSend = processUnsubscribe(message);
            connections.send(id, toSend);
        }
        else if (message.getHeadLine().equals("DISCONNECT")) {
                Frame toSend = new ReceiptFrame(message);
                connections.disconnect(this.id);
                connections.send(this.id, toSend);
                connections.removeActiveUser(this.id);
                shouldTerminate = true;
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    //processing connection request.........................................................................................................................................................
    private Frame processConnect(Frame message) {
        String name = message.getHeaders().get("login");
        String pass = message.getHeaders().get("passcode");
        if (dataBase.getTotalUserByNameMap().containsKey(name)) {
            User user = (User) dataBase.getTotalUserByNameMap().get(name);
            user.setId(this.id);
            if (!user.getPassword().equals(pass)) {
                shouldTerminate = true;
                LinkedList<String> list = new LinkedList<String>();
                list.add("message:wrong password");
                ErrorFrame frame = new ErrorFrame(message, "wrong password");
                return frame;
            } else {
                dataBase.getCurrentUsersByIdMap().put(user.getId(), user); // connects the user to the grid
                return new ConnectedFrame(message);
            }
        }
        else {
            User user = new User(this.id, name, pass);
            dataBase.getTotalUserByNameMap().put(user.getName(), user);
            dataBase.getCurrentUsersByIdMap().put(user.getId(), user);
            ConnectedFrame connected = new ConnectedFrame(message);
            System.out.println(connected.asString());
            return connected;
        }
    }

    //processing subscribe request.........................................................................................................................................................
    private Frame processSubscribe(Frame message) {
        User user = (User) dataBase.getCurrentUsersByIdMap().get(this.id);
        user.addToTopics(message.getHeaders().get("destination"), Integer.parseInt(message.getHeaders().get("id")));
        dataBase.subscribeToChannel(message.getHeaders().get("destination"), user, id);
        return new ReceiptFrame(message);

    }
    //processing unsubscribe request.........................................................................................................................................................
    private Frame processUnsubscribe(Frame message) {
        User user = (User) dataBase.getCurrentUsersByIdMap().get(id);
        String topic = user.getTopics().get(Integer.parseInt(message.getHeaders().get("id")));
        user.removeTopic(Integer.parseInt(message.getHeaders().get("id")));
        dataBase.unsubscribeFromOneChannel(topic, user);
        return new ReceiptFrame(message);

    }
    //processing send request.........................................................................................................................................................
    private MessageFrame processSend(Frame message) {
        User user = (User) dataBase.getCurrentUsersByIdMap().get(id);
        String channel = message.getHeaders().get("destination");
        int subscription = -1;
        for (Integer i : user.getTopics().keySet()) {
            if (user.getTopics().get(i) == channel) {
                subscription = i;
                break;
            }
        }
        msgCounter.incrementAndGet();
        return new MessageFrame(message, String.valueOf(subscription), msgCounter.toString());
    }
}