package bgu.spl.net.ServerMessages;



import bgu.spl.net.srv.Frame;
import java.util.HashMap;

public class ConnectedFrame implements Frame{

    private String headLine = "CONNECTED";
    private HashMap<String,String> headers;

    public ConnectedFrame(Frame f){
        this.headers = new HashMap<>();
        String acceptVer = f.getHeaders().get("accept-version");
        headers.put("version",acceptVer);
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
        return "";
    }

    @Override
    public String asString() {
        String ret = headLine+"\n";
        for (String key:headers.keySet()) {
            ret += key+":"+this.headers.get(key)+"\n";
        }
        return ret;
    }

}