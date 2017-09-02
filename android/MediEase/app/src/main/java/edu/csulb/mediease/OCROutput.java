package edu.csulb.mediease;


class OCROutput {

    private int accuracy;
    private String text;

    OCROutput(int accuracy, String text) {
        this.accuracy = accuracy;
        this.text = text;
    }

    int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
