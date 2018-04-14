package Processing;


import java.util.ArrayList;

public class SearchResult {

    private static ArrayList<String> keywords;
    private String userName;
    private String message;
    private String location;

    public SearchResult(String name, String text, String loc){
        this.userName = name;
        this.message = text;
        this.location = loc != null ? loc : "!-!";
    }

    public SearchResult(FilterResult toConvert){
        this.userName = toConvert.getUserName();
        this.message = toConvert.getMessage();
        this.location = toConvert.getLocation();
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return ("@" + userName + ": " + message + " {" + location + "}") ;
    }
}
