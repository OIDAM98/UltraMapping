package Processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.model.GeocodingResult;

public class ProcessedResult {
    private String userName;
    private String message;
    private double latitude;
    private double longitude;
    private String location;

    public ProcessedResult(String name, String text, double lat, double lon, String loc){
        this.userName = name;
        this.message = text;
        this.latitude = lat;
        this.longitude = lon;
        this.location = loc;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return ("@" + userName + ": " + message + " {" + latitude + ", " + longitude + "} [" + location + "]") ;
    }
}
