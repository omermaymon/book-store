package bgu.spl.net.srv;

import bgu.spl.net.ServerMessages.ErrorFrame;
import bgu.spl.net.ServerMessages.MessageFrame;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ConnectionsImpl<T> implements Connections<T> {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private DataBase dataBase;
    private ConcurrentHashMap<Integer,ConnectionHandler> activeUsers = new ConcurrentHashMap<>();
    private AtomicInteger userID;



    public ConnectionsImpl() {
        dataBase = DataBase.getInstance();
        userID = new AtomicInteger(0);
    }
    public ConcurrentHashMap<Integer,ConnectionHandler> getActiveUsersMap(){return activeUsers;}




     // this send just to one connection handler
    public boolean send(int connectionId, T msg) {
        try {
            lock.readLock().lock();
            ConnectionHandler<T> handler = (ConnectionHandler<T>) activeUsers.get(connectionId);
            lock.readLock().unlock();
            handler.send(msg);
            if (msg.getClass().equals(ErrorFrame.class)){
                activeUsers.remove(connectionId,activeUsers.get(connectionId));
            }
            return true;
      }
        catch (Exception e){
            return false;
        }
    }

    // sends to all active users in topic
    public void send(String channel, T msg) {
        dataBase.getTopic_activeUsersMap().putIfAbsent(channel,new ConcurrentHashMap<User,Integer>());
        ConcurrentHashMap<User,Integer> usersInChannel = (ConcurrentHashMap<User, Integer>) dataBase.getTopic_activeUsersMap().get(channel);
        MessageFrame message = (MessageFrame)msg;
            for (User user : usersInChannel.keySet()) {
                String idToSend = usersInChannel.get(user).toString();
                message.changeHeaderValue("subscription", idToSend);
                send(user.getId(), (T) message);
            }

    }
    public void removeActiveUser(int connectionId){activeUsers.remove(connectionId);}


    public void disconnect(int connectionId) {

        dataBase.unsubscribeFromChannels((User) dataBase.getCurrentUsersByIdMap().get(connectionId));
        dataBase.removeUserFromCurrentUserByIdMap(connectionId);
    }


    public int addHandler(ConnectionHandler<T> handler){
        int id = this.userID.getAndIncrement();
        lock.writeLock().lock();
        activeUsers.put(id,handler);
        lock.writeLock().unlock();
        return id;
    }

    public int subscribeHandler(ConnectionHandler handler){
        int id = this.userID.incrementAndGet();
        lock.writeLock().lock();
        activeUsers.put(id, handler);
        lock.writeLock().unlock();
        return id;
    }
}
