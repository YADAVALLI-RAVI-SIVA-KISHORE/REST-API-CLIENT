import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherClient {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            // asks user for input the specific place
            System.out.print("Enter the city/place name : ");
            String place = sc.nextLine();

            // now we should encode the user input for URL
            String encodedPlace = URLEncoder.encode(place, "UTF-8");

            // now we perform step 1 : get latitude and longitude from Geocoding API
            String geoApiUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedPlace;
            JSONObject geoResponse = new JSONObject(getApiResponse(geoApiUrl));
            if (!geoResponse.has("results")) {
                System.out.println("Place not found. Please try again.");
                return;
            }
            JSONArray results = geoResponse.getJSONArray("results");
            JSONObject location = results.getJSONObject(0);

            // take the first result
            double latitude = location.getDouble("latitude");
            double longitude = location.getDouble("longitude");
            String city = location.getString("name");
            String country = location.getString("country");

            // now we perform step 2 : fetch weather using latitude and longitude
            // Removed spaces around '=' and '&' in the URL string
            String weatherApiUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude 
                                 + "&longitude=" + longitude 
                                 + "&current_weather=true";
            JSONObject weatherResponse = new JSONObject(getApiResponse(weatherApiUrl));
            JSONObject currentWeather = weatherResponse.getJSONObject("current_weather");

            // now we perform step 3 : displaying the results
            System.out.println("weather report for " + city + "," + country + ": ");
            System.out.println("Temperature\t: " + currentWeather.getDouble("temperature") + " °C");
            System.out.println("Windspeed\t: " + currentWeather.getDouble("windspeed") + " km/h");
            System.out.println("Time\t\t: " + currentWeather.getString("time"));
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    // helper method to call API and return response as string
    public static String getApiResponse(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}

