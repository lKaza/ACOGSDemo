package AC0;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.Waypoint;

public class City {
    private static final double EARTH_EQUATORIAL_RADIUS = 6371.1370D;
    private static final double CONVERT_DEGREES_TO_RADIANS = Math.PI/180D;
    private double longitude;
    private double longitudedegree;
    private double latitude;
    private double latitudedegree;
    private float altitude;
    private String name;


    public City(String name,double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude * CONVERT_DEGREES_TO_RADIANS;
        this.longitude = longitude * CONVERT_DEGREES_TO_RADIANS;
        this.latitudedegree = latitude;
        this.longitudedegree = longitude;
    }


    public City(float altitude,double latitude, double longitude) {
        this.altitude = altitude;
        this.latitude = latitude * CONVERT_DEGREES_TO_RADIANS;
        this.longitude = longitude * CONVERT_DEGREES_TO_RADIANS;
    }



    public double measureDistance(City city) {
        double deltaLongitude = (city.getLongitude() - this.getLongitude());
        double deltaLatitude = (city.getLatitude() - this.getLatitude());
        double a = Math.pow(Math.sin(deltaLatitude/ 2D),2D)+
                Math.cos(this.getLatitude())* Math.cos(city.getLatitude())* Math.pow(Math.sin(deltaLongitude/2D),2D);
        return EARTH_EQUATORIAL_RADIUS * 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D-a));
    }

    public String getName() {return name;}
    public double getLatitude() { return this.latitude;}
    public double getLatitudeDegree() { return this.latitudedegree;}
    public double getLongitude() { return this.longitude;}
    public double getLongitudeDegree() { return this.longitudedegree;}
    public String toString() { return this.name;}

}

