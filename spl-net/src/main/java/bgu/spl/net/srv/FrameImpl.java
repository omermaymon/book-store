package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
public class FrameImpl implements Frame{


    private String headLine;
    private HashMap<String,String> headers;
    private String body;

    public FrameImpl(String headline, List<String> headers, String body){
        headLine=headline;
        this.body=body;
        this.headers = new HashMap<>();
        insertHeader(headers);
    }

    public String getHeadLine(){return this.headLine;}

    public HashMap<String,String> getHeaders(){return this.headers;}

    public String getBody(){return this.body;}

    private void insertHeader(List<String> heads){
        String head;
        String content;
        for (String header:heads) {
            head = header.substring(0,header.indexOf(':'));
            content = header.substring(header.indexOf(':')+1,header.length());
            this.headers.put(head,content);
        }
    }

    public String asString() {
        String ret = headLine + "\n";
        for (String key : headers.keySet()) {
            ret += key + ":" + this.headers.get(key) + "\n";
        }
        if (this.getBody() != "")
            return ret + "\n" + this.getBody();
        else
            return ret;
    }
}