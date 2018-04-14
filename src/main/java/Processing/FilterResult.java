package Processing;

import twitter4j.Status;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FilterResult {

    private static ArrayList<String> keywords;
    private String userName;
    private String message;
    private String location;

    public FilterResult(Status tweet){
        this.userName = tweet.getUser().getScreenName();
        this.message = tweet.getText().contains("\n") ? normalizeText(tweet.getText()) : tweet.getText();
        //this.message = tweet.getText();
        if(tweet.getUser().getLocation() != null)
            this.location = tweet.getUser().getLocation();
        else
            this.location = "!-!";

    }

    private String normalizeText(String text){
        String corrected = removeSpecialChars(text);
        String[] toNorm = corrected.split(String.valueOf('\n'));
        StringBuilder sb = new StringBuilder();
        for(String part : toNorm){
            sb.append(part);
            sb.append(" ");
        }
        return sb.toString();
    }

    private String removeSpecialChars(String text){
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = text;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }
        return output;
    }

    public FilterResult(){
        generateKeyWords();
    }

    public boolean containsKeyWords(){
        generateKeyWords();
        return containsKeyWords(message);
    }

    public static boolean containsKeyWords(String inputStr) {
        String[] items = keywords.toArray(new String[keywords.size()]);
        for(int i = 0; i < items.length; i++) {
            //String[] toCheck = items[i].split(" ");
            if(inputStr.contains(items[i])) {
                return true;
            }
        }
        return false;
    }

    private static void generateKeyWords(){
        keywords = new ArrayList<>();
        File keyText = new File("conceptos.txt");
        try{
            Scanner scan = new Scanner(keyText);
            while (scan.hasNextLine()){
                keywords.add(scan.nextLine());
            }
        }
        catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public String getLocation() {
        return location;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return (userName + "°_°" + message + "°_°" + location);
    }

}
