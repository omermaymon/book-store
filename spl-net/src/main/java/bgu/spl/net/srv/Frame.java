package bgu.spl.net.srv;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public interface Frame {

    String getHeadLine();

    HashMap<String, String> getHeaders();

    String getBody();

    String asString();
}