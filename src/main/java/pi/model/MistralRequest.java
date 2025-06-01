package pi.enset.model;

import java.util.List;

public class MistralRequest {
    private String model;
    private List<MistralMessage> messages;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<MistralMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<MistralMessage> messages) {
        this.messages = messages;
    }
}
