import Processing.CSVParser;
import Processing.FilterResult;
import Processing.ProcessedResult;
import Processing.SearchResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProcessInfo {

    static private ArrayList<String> keywords;
    static private final int MAX_TWEETS = 150;
    static volatile private int tweetsRead = 0;
    static volatile private int tweetsAnalized = 0;
    static volatile private int tweetsFiltered = 0;
    static private boolean isRunning;
    private Ventana connection;

    public ProcessInfo(Ventana gui) {
        generateKeyWords();
        isRunning = false;
        this.connection = gui;
    }

    public void start(){

        File saveSearch = new File("search.txt");
        File saveCSV = new File("search.csv");
        File saveResult = new File("result.txt");

        try {
            FileOutputStream fos = new FileOutputStream(saveSearch);
            PrintStream out = new PrintStream(fos);

            FileOutputStream fos2 = new FileOutputStream(saveResult);
            PrintStream wRes = new PrintStream(fos2);

            FileOutputStream fos3 = new FileOutputStream(saveCSV);
            PrintStream csvOut = new PrintStream(fos3);

            ArrayList<FilterResult> searchResults = new ArrayList<>();
            ArrayList<SearchResult> validResults = new ArrayList<>();
            ArrayList<ProcessedResult> bestResults = new ArrayList<>();

            /*
                Set Configuration of Google Maps Geocoding API.
                Currently leftout credentials to be filled.
             */

            String apiKey = "";

            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(apiKey)
                    .build();

            /*
                Set Configuration of Twitter API.
                Currently leftout credentials to be filled.
             */

            String consKey = "";
            String consSecret = "";
            String authTok = "";
            String authTokSecret = "";

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true);
            cb.setOAuthConsumerKey(consKey);
            cb.setOAuthConsumerSecret(consSecret);
            cb.setOAuthAccessToken(authTok);
            cb.setOAuthAccessTokenSecret(authTokSecret);

            TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
            TwitterStream twitter = tf.getInstance();

            final BlockingQueue<ProcessedResult> statuses = new LinkedBlockingQueue<ProcessedResult>(MAX_TWEETS);

            StatusListener listener = new StatusListener() {
                @Override
                public void onStatus(Status status) {

                    if(!status.isRetweet()) {
                        //Update Label in GUI of Tweets Read
                        connection.updatetweetFiltered(String.valueOf(++tweetsFiltered));

                        FilterResult filtered = new FilterResult(status);

                        searchResults.add(filtered);
                        if(filtered.containsKeyWords()){
                            SearchResult validResult = new SearchResult(filtered);
                            validResults.add(validResult);

                            out.println(validResult); //Save to OutputFile
                            try {
                                if(!validResult.getLocation().equals("!-!")) {
                                    GeocodingResult[] results = GeocodingApi.geocode(context, validResult.getLocation()).await();
                                    if (results.length > 0) { //If Geocoding of Search Result is not null (length == 0).
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create(); //Instantiate new JSON Parser
                                        double longitude = Double.parseDouble(gson.toJson(results[0].geometry.location.lng)); //Get Longitude
                                        double latitude = Double.parseDouble(gson.toJson(results[0].geometry.location.lat)); //Get Latitude
                                        ProcessedResult finalRes = new ProcessedResult(validResult.getUserName(), validResult.getMessage(), latitude, longitude, validResult.getLocation());

                                        /*
                                            Try adding Processed Result to Queue.
                                            BlockingQueue add method returns false when it is no longer possible to add another Element to it.
                                         */
                                        statuses.add(finalRes);

                                        connection.updatetweetAnalized(String.valueOf(++tweetsAnalized)); //Update number of tweets analized in GUI
                                        //Add tweet to corresponding TextAreas in GUI
                                        connection.appendUbicacion(tweetsAnalized + ") " + finalRes.getLocation() + " {" + finalRes.getLatitude() + ", " + finalRes.getLongitude() + "}\n");
                                        connection.appendTweet(tweetsAnalized + ") " + "@" + finalRes.getUserName() + ": " + finalRes.getMessage() + "\n");

                                        wRes.println(finalRes); //Save to Text File
                                        csvOut.println(CSVParser.convertToCSV(finalRes)); //Save to CSV File
                                    }
                                }
                            }
                            catch (ApiException ex){
                                ex.printStackTrace();
                            }
                            catch (InterruptedException ex){
                                ex.printStackTrace();
                            }
                            catch (IOException ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) { }

                @Override
                public void onTrackLimitationNotice(int numberOfLimitedStatuses) { }

                @Override
                public void onScrubGeo(long userId, long upToStatusId) { }

                @Override
                public void onStallWarning(StallWarning warning) { }

                @Override
                public void onException(Exception ex) {
                    ex.printStackTrace();
                }
            };

            String[] languages = {"es"}; //Language to be listening will be Spanish
            String[] keywo = keywords.toArray(new String[keywords.size()]); //Convert Keywords to Array[String]
            double[][] location = {{-180, -90}, {180, 90}}; //Range [-180,-90] to [180,90] means all around the world

            twitter.addListener(listener); //Add Listener to Stream

            FilterQuery tweetFQ = new FilterQuery();
            tweetFQ.track(keywo); //Set Filter Keywords to be Tracking
            tweetFQ.language(languages); //Set Filter Language(s)
            tweetFQ.locations(location); //Set Filter Location

            //System.out.println("Started stream");


            twitter.filter(tweetFQ);
            isRunning = true;
            while (bestResults.size() < MAX_TWEETS) {
                final ProcessedResult status;
                try {
                    status = statuses.poll(10, TimeUnit.SECONDS);
                    if (status == null) {
                        continue;
                    }
                    bestResults.add(status);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //System.out.println("Ended stream");
            twitter.shutdown(); //End Twitter Streaming
            isRunning = false; //set to stopped

        }
        catch (FileNotFoundException ex){
            ex.printStackTrace();
        }

    }

    //Read keywords from text file and fill ArrayList
    static private void generateKeyWords(){
        keywords = new ArrayList<>();
        File keyText = new File("conceptos.txt");
        try{
            Scanner scan = new Scanner(keyText);
            while (scan.hasNextLine()){
                String key = scan.nextLine();
                keywords.add(key);
            }

        }
        catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public int getMaxTweets() {
        return MAX_TWEETS;
    }

    public boolean isIsRunning() {
        return isRunning;
    }
}
