package bgu.spl.net.ServerMessages;

import bgu.spl.net.srv.Frame;
import java.util.HashMap;

public class ReceiptFrame implements Frame {

    private String headLine = "RECEIPT";
    private HashMap<String, String> headers = new HashMap<>();

    public ReceiptFrame(Frame f) {
        Frame frame = f;
        String str = frame.getHeaders().get("receipt");
        headers.putIfAbsent("receipt-id", frame.getHeaders().get("receipt"));
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

    public String asString() {
        String ret = headLine + '\n';
        for (String key : headers.keySet()) {
            ret += key + ":" + this.headers.get(key) + '\n';
        }
        return ret;
    }
}