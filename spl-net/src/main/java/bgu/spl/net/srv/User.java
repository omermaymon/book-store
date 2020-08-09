package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private int id;
    private String name;
    private String password;
    private ConcurrentHashMap<Integer,String> topics;

    public User(int id,String name, String password){
        this.id=id;
        this.name=name;
        this.password=password;
        topics = new ConcurrentHashMap();
    }

    public int getId(){return this.id;}
    public String getPassword(){return this.password;}
    public String getName(){return this.name;}
    public ConcurrentHashMap<Integer,String> getTopics(){return this.topics;}

    public void setId(int id){this.id = id;}

    public void addToTopics(String topic,int id){
        topics.put(id,topic);
    }

    public void clearTopics(){topics.clear();}

    public void removeTopic(int id){
        topics.remove(id);
    }
}
