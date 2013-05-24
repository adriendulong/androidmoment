package com.moment.classes;

/**
 * Created by adriendulong on 22/05/13.
 */

public class Place {

    public String placeOne;
    public String placeTwo;
    public String placeThree;

    public Place(){
        super();
    }

    public Place(String place){
        this.placeOne = place;
    }

    public String getPlaceOne() {
        return placeOne;
    }

    public String getPlaceTwo() {
        return placeTwo;
    }

    public String getPlaceThree() {
        return placeThree;
    }

    public void setPlaceThree(String placeThree) {
        this.placeThree = placeThree;
    }

    public void setPlaceTwo(String placeTwo) {
        this.placeTwo = placeTwo;
    }

    public void setPlaceOne(String placeOne) {
        this.placeOne = placeOne;
    }
}