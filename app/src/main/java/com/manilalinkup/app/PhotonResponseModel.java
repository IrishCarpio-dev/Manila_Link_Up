package com.manilalinkup.app;

import java.util.List;

public class PhotonResponseModel {
    public List<Feature> features;

    public static class Feature {
        public Properties properties;
        public Geometry geometry;
    }

    public static class Properties {
        public String name;
        public String city;
        public String state;
        public String country;
    }
    public static class Geometry {
        public List<Double> coordinates; // [longitude, latitude]
    }
}