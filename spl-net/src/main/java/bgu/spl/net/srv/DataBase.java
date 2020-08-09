package bgu.spl.net.srv;
import bgu.spl.net.srv.User;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataBase<T> {
    private ConcurrentHashMap<String, User> totalUserByName;
    private ConcurrentHashMap<Integer,User> CurrentUsersById;
    private ConcurrentHashMap<String, ConcurrentHashMap<User,Integer>> topic_activeUsers;
//    private ConcurrentHashMap<Integer,ConnectionHandler> activeUsers;
    private ReadWriteLock lock;
    private AtomicInteger userID;

    private DataBase(){
        totalUserByName = new ConcurrentHashMap<>();
        CurrentUsersById = new ConcurrentHashMap<>();
        topic_activeUsers = new ConcurrentHashMap<>();
        lock = new ReentrantReadWriteLock();
    }

    private static class DataBaseHolder{private static DataBase DataBase = new DataBase();}

    public static DataBase getInstance(){
        return DataBaseHolder.DataBase;
    }

    public ConcurrentHashMap<String, User> getTotalUserByNameMap(){return totalUserByName;}
    public ConcurrentHashMap<Integer,User> getCurrentUsersByIdMap(){return CurrentUsersById;}
    public ConcurrentHashMap<String, ConcurrentHashMap<User,Integer>> getTopic_activeUsersMap(){return topic_activeUsers;}


    public void subscribeToChannel(String channel, User user, int id){
        topic_activeUsers.putIfAbsent(channel, new ConcurrentHashMap<User,Integer>());
        topic_activeUsers.get(channel).put(user,id); //check if works
    }


    public void unsubscribeFromOneChannel(String channel, User user){
        topic_activeUsers.get(channel).remove(user);
    }

    public void unsubscribeFromChannels(User user){
        //List<String> topics = user.getTopics();
        for (Integer i : user.getTopics().keySet()) {
            topic_activeUsers.get(user.getTopics().get(i)).remove(user);
        }
        user.getTopics().clear();
    }

    public void removeUserFromCurrentUserByIdMap(int connectionId){CurrentUsersById.remove(connectionId,CurrentUsersById.get(connectionId));}



}
