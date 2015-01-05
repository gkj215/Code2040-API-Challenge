package apichallenge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class APIChallenge {

    public static void main(String[] args) throws UnsupportedEncodingException, Exception {
        String identity = HttpPostFunc(new URL("http://challenge.code2040.org/api/register"), "{ \"email\": \"gkj215@lehigh.edu\", \"github\": \"https://github.com/gkj215/Code2040-API-Challenge/\"}");
        String identityParsed = Parse(identity);

        //Challenge 1
        String originalString = HttpPostFunc(new URL("http://challenge.code2040.org/api/getstring"), "{ \"token\": \"" + identityParsed + "\"}");
        String originalParsed = Parse(originalString);

        String reversedString = reverseStr(originalParsed);
        System.out.println("Reversed String is: " + reversedString);

        String solution = HttpPostFunc(new URL("http://challenge.code2040.org/api/validatestring"), "{ \"token\": \"" + identityParsed + "\", \"string\": \"" + reversedString + "\"}");
        System.out.println(solution);

        //Challenge 2
        String dictionary = HttpPostFunc(new URL("http://challenge.code2040.org/api/haystack"), "{ \"token\": \"" + identityParsed + "\"}");
        int index = Challenge2(dictionary);

        String result = HttpPostFunc(new URL("http://challenge.code2040.org/api/validateneedle"), "{ \"token\": \"" + identityParsed + "\", \"needle\": \"" + index + "\"}");
        System.out.println(result);

        //Challenge 3 
        //Works but could not determine how to send back String Array       
        String dictionaryPrefix = HttpPostFunc(new URL("http://challenge.code2040.org/api/prefix"), "{ \"token\": \"" + identityParsed + "\"}");

        ArrayList<String> listDict = Challenge3(dictionaryPrefix);
        String arrayPrefix[] = new String[listDict.size()];
        for (int i = 0; i < listDict.size(); i++) {
            arrayPrefix[i] = listDict.get(i);
        }
        String solutionArray = "";
        for (int j = 0; j < arrayPrefix.length; j++) {
            solutionArray += arrayPrefix[j] + " ";

        }

        String solutionCh3 = HttpPostFunc(new URL("http://challenge.code2040.org/api/validateprefix"), "{ \"token\": \"" + identityParsed + "\", \"array\": \"" + arrayPrefix + "\"}");

        System.out.println(solutionCh3);

    }

    //Challenge 1
    public static String reverseStr(String word) {
        String reverse = new StringBuilder(word).reverse().toString();
        return reverse;
    }

    //Challenge 2
    public static int Challenge2(String word) {
        //Replace and split to parse easier
        word = word.replace('"', ',');
        String strArray[];
        strArray = word.split(",+");
        String needle = "";
        int count = 0;
        //Search for the needle and then loop through the haystack, stopping when "needle" is found, signifying the end
        for (int i = 0; i < strArray.length; i++) {
            if ("needle".equals(strArray[i])) {
                i += 2;
                needle = strArray[i];
            }
        }
        for (int j = 5; j < strArray.length; j++) {
            if (needle.equals(strArray[j])) {
                if ("needle".equals(strArray[j])) {
                    return 0;
                }
                return count;
            } else {
                count++;
            }
        }
        return count;
    }

    //Challenge 3
    public static ArrayList Challenge3(String word) {
        //Same process for Challenge 2
        word = word.replace('"', ',');
        String strArray[];
        ArrayList<String> solution = new ArrayList<String>();
        strArray = word.split(",+");
        String prefix = "";
        for (int i = 0; i < strArray.length; i++) {
            if ("prefix".equals(strArray[i])) {
                i += 2;
                prefix = strArray[i];
            }
        }
        for (int j = 5; j < strArray.length; j++) {
            if (strArray[j].equals("]")) {
                return solution;
            }
            if (strArray[j].contains(prefix)) {
                if ("prefix".equals(strArray[j])) {
                    return solution;
                }
            } else {
                solution.add(strArray[j]);
            }
        }
        return solution;
    }

    public static String Parse(String word) {
        word = word.replace('"', ',');
        String strArray[];
        strArray = word.split(",");
        word = strArray[3];
        return word;
    }

    //HTTP POST    
    public static String HttpPostFunc(URL url, String input) {

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());

            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                return output;

            }
        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

        }
        return "";
    }
}
