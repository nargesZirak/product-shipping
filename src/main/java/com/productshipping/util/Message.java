package com.productshipping.util;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class Message {
    private int statusCode;
    private String message;
    
    public static List<Message> getMessageList(String message, HttpStatus status){
        Message message1 = new Message();
        message1.setStatusCode(status.value());
        message1.setMessage(message);
        List<Message> messageList = new ArrayList<>();
        messageList.add(message1);
        return messageList;
    }
}
