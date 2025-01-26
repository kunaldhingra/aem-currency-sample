package com.currency.core.core;

//P was capital in the name of the class
public class Option {

    //Both the fields can be made private
    protected String name;
    private String value;

    // P is capital in constructor
    public Option(){

    }

    //P is capital in constructor
    public Option(String value, String name) {
        this.value = value;
        this.name = name;
        //name and value are incorrectly assigned
       // name = this.name;
       // value = this.value;
    }

}
