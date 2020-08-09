package bgu.spl.net.ServerMessages;

import bgu.spl.net.srv.Frame;


import java.util.HashMap;

public class ErrorFrame implements Frame {
    private String headLine = "ERROR";
    private HashMap<String,String> headers = new HashMap<>();
    private String body;
    private Frame frame;

    public ErrorFrame(Frame f, String mainError){
        frame=f;
        //headers.putIfAbsent(f.getHeaders())
        headers.putIfAbsent("message",mainError);
        body = "";
    }

//    private String createBody(String optinalDescription){
//        String str = "The message:\n-----\n"+frame.asString();
//        str = str.substring(0,body.length()-1)+"\n-----";
//        if (optinalDescription!="")
//            str += "\n"+optinalDescription;
//        return str;
//    }

    @Override
    public String getHeadLine() {
        return headLine;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getBody() {
        return body;
    }

    public String asString() {
        String ret = headLine+"\n";
        for (String key:headers.keySet()) {
            ret += key+":"+this.headers.get(key)+"\n";
        }
        if (this.getBody()!="")
            return ret += "\n"+this.getBody();
        else
            return ret;
    }
}