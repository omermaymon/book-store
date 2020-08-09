package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Frame> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    @Override
    public Frame decodeNextByte(byte nextByte) {
        if (nextByte == '\u0000') {
            return FrameCreator();
        }

        pushByte(nextByte);
        return null; //not a line yet
    }

    @Override
    public byte[] encode(Frame message) {
        return (message.asString() + "\n\u0000").getBytes();
    }



    private void pushByte(byte nextByte){
            if (len >= bytes.length) {
                bytes = Arrays.copyOf(bytes, len * 2);
            }

            bytes[len++] = nextByte;
    }


    private Frame FrameCreator(){
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        String[] str = result.split("\n");
        String headLine = str[0];
        for (int i = 0; i <str.length ; i++) {
            System.out.println(str[i]);
        }

        List<String> headers = new LinkedList<>();
        String body ="";
        if (headLine.equals("SEND")|| headLine.equals("MESSAGE")) {
            body = str[str.length - 1]; // TODO see if it's good

            for (int i = 1; i < str.length - 2; i++) {
                headers.add(str[i]);
            }
        }
        else{
            body ="";
            for (int i = 1; i <str.length  ; i++) {
                headers.add(str[i]);
            }
        }
        return new FrameImpl(headLine,headers,body);
    }


}
