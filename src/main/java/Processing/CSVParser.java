package Processing;

public class CSVParser {
    public static String convertToCSV(ProcessedResult toConvert){
        return String.format("%s|%s|%s|%f|%f", toConvert.getUserName(), toConvert.getMessage(), toConvert.getLocation(), toConvert.getLatitude(), toConvert.getLongitude());
    }
}
