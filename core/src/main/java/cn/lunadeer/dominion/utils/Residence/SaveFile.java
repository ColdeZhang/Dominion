package cn.lunadeer.dominion.utils.Residence;

import java.util.Map;

public class SaveFile {
    public Map<String, Residence> Residences;
    public Map<Integer, Message> Messages;

    // getters and setters
    public Map<String, Residence> getResidences() {
        return Residences;
    }

    public void setResidences(Map<String, Residence> residences) {
        Residences = residences;
    }

    public Map<Integer, Message> getMessages() {
        return Messages;
    }

    public void setMessages(Map<Integer, Message> messages) {
        Messages = messages;
    }
}