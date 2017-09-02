package edu.csulb.mediease;

class Medicine {
    private int id;
    private String name;
    private String frequency;
    private String times;

    Medicine() {
    }

    Medicine(String name, String frequency) {
        id = 0;
        this.name = name;
        this.frequency = frequency;
    }

    Medicine(int id, String name, String frequency) {
        this.id = id;
        this.name = name;
        this.frequency = frequency;
    }

    Medicine(int id, String name, String frequency, String times) {
        this.id = id;
        this.name = name;
        this.frequency = frequency;
        this.times = times;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getFrequency() {
        return frequency;
    }

    void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    String getTimes() {
        return times;
    }

    void setTimes(String times) {
        this.times = times;
    }
}
