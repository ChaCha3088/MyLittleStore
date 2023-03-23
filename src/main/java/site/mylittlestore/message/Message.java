package site.mylittlestore.message;

import lombok.Getter;

@Getter
public class Message {

    String message = "";
    String href = "";

    public Message(String message, String href) {
        this.message = message;
        this.href = href;
    }
}
