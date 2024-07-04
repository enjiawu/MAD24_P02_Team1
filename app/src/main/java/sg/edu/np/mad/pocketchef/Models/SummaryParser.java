package sg.edu.np.mad.pocketchef.Models;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SummaryParser {

    public static String parseRecipeDetails(String html) {
        Document doc = Jsoup.parse(html);

        // Extracting recipe details
        String protein = extractValue(doc, "of protein");
        String fat = extractValue(doc, "of fat");
        String calories = extractValue(doc, "calories");
        String dailyRequirementsCoverage = extractValue(doc, "covers");

        // Constructing the extracted information as a string
        return  protein + "g" + "\n" +
                fat + "g" + "\n" +
                calories + "kcal" + "\n" +
                dailyRequirementsCoverage + "%" + "\n";
    }

    private static String extractValue(Document doc, String query) {
        String text = doc.select("b:containsOwn(" + query + ")").text();
        return extractNumber(text);
    }

    private static String extractNumber(String text) {
        // Extracting numbers from the text using regular expressions
        String regex = "\\d+";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}

