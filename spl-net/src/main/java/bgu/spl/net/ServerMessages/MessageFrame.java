package bgu.spl.net.ServerMessages;
import bgu.spl.net.srv.Frame;
import java.util.HashMap;

public class MessageFrame implements Frame{
    private String headLine = "MESSAGE";
    private HashMap<String,String> headers;
    private String body;

    public MessageFrame(Frame f, String subscription, String MsgID){ // constructor for SEND
        headers = new HashMap<>();
        headers.put("subscription",subscription);
        headers.put("Message-id",MsgID);
        headers.put("destination",f.getHeaders().get("destination"));
        this.body = f.getBody();
    }

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

    public void changeHeaderValue(String header, String change){
        if (headers.containsKey(header))
            headers.replace(header,change);
    }

    @Override
    public String asString() {
        String ret = headLine + "\n";
        for (String key : headers.keySet()) {
            ret += key + ":" + this.headers.get(key) + "\n";
        }
        if (this.getBody() != "")
            return ret + "\n"+this.getBody();
        return ret;
    }
}