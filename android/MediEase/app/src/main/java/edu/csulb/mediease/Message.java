package edu.csulb.mediease;

import java.io.Serializable;

public class Message implements Serializable {

    String hname;
    String uname;
    String message;

    public Message() {
    }

    public Message(String hname, String message, String uname) {
        this.hname = hname;
        this.message = message;
        this.uname = uname;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String gethname() {
        return hname;
    }

    public void sethname(String hname) {
        this.hname = hname;
    }


}
