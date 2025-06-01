package pi.enset.model;

import java.util.List;

public class MistralResponse {
    private List<MistralChoice> choices;

    public List<MistralChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<MistralChoice> choices) {
        this.choices = choices;
    }
}
