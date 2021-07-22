package com.codepath.cityslicker;

public class Utilities {
    public static String convertToDollars(Integer n) {
        switch(n) {
            case 0:
                return "Free";
            case 1:
                return "$";
            case 2:
                return "$$";
            case 3:
                return "$$$";
            case 4:
                return "$$$$";
            default:
                return "Unknown price level";
        }
    }
}
