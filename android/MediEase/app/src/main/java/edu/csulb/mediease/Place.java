package edu.csulb.mediease;


class Place {
    private String place_id;
    private String hname;
    private double lat;
    private double lng;
    private String addr;

    Place() {
    }

    Place(String place_id, String hname, double lat, double lng, String addr) {
        this.place_id = place_id;
        this.hname = hname;
        this.lat = lat;
        this.lng = lng;
        this.addr = addr;

    }

    String getAddr() {
        return addr;
    }

    void setAddr(String addr) {
        this.addr = addr;
    }

    String getPlace_id() {
        return place_id;
    }

    void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    String getHname() {
        return hname;
    }

    void setHname(String hname) {
        this.hname = hname;
    }

    double getLat() {
        return lat;
    }

    void setLat(double lat) {
        this.lat = lat;
    }

    double getLng() {
        return lng;
    }

    void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "place_id = " + place_id + " hname = " + hname + " lat = " + lat + " lng = " + lng + " addr = " + addr;
    }
}
