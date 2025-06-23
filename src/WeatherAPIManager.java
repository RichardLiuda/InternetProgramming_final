import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherAPIManager {
    private String apiKey = "8f7e5afc2937d5a681d09356b53f6de7";
    private String baseURL = "https://api.openweathermap.org/data/2.5/";
    private UserPreferences userPreferences;

    public WeatherAPIManager(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    public WeatherData getCurrentWeather(Location location) {
        String response = fetchWeatherData(location, "weather");
        return parseAPIResponse(response);
    }

    public WeatherData[] getForecast(Location location, int days) {
        String response = fetchWeatherData(location, "forecast");
        return parseForecastResponse(response, days);
    }

    private String fetchWeatherData(Location location, String endpointType) {
        String units = userPreferences.getPreferredUnits(); // "metric" or "imperial"
        String endpoint = baseURL + endpointType + "?q=" + location.getCity() + "&appid=" + apiKey + "&units=" + units;
        StringBuilder inline = new StringBuilder();

        try {
            URL url = URI.create(endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            Scanner sc = new Scanner(url.openStream());
            while (sc.hasNext()) {
                inline.append(sc.nextLine());
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inline.toString();
    }

    private WeatherData parseAPIResponse(String response) {
        JsonObject jsonObj = JsonParser.parseString(response).getAsJsonObject();
        double temp = jsonObj.getAsJsonObject("main").get("temp").getAsDouble();
        double humidity = jsonObj.getAsJsonObject("main").get("humidity").getAsDouble();
        double windSpeed = jsonObj.getAsJsonObject("wind").get("speed").getAsDouble();
        String description = jsonObj.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

        // Passing userPreferences to WeatherData
        return new WeatherData(temp, humidity, windSpeed, description, "Current", userPreferences);
    }

    private WeatherData[] parseForecastResponse(String response, int days) {
        JsonObject jsonObj = JsonParser.parseString(response).getAsJsonObject();
        JsonArray list = jsonObj.getAsJsonArray("list");
        WeatherData[] forecastData = new WeatherData[days];

        for (int i = 0; i < days && i < list.size(); i++) {
            JsonObject dayData = list.get(i).getAsJsonObject();
            double temp = dayData.getAsJsonObject("main").get("temp").getAsDouble();
            double humidity = dayData.getAsJsonObject("main").get("humidity").getAsDouble();
            double windSpeed = dayData.getAsJsonObject("wind").get("speed").getAsDouble();
            String description = dayData.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

            // Passing userPreferences to WeatherData
            forecastData[i] = new WeatherData(temp, humidity, windSpeed, description, "Forecast Day " + (i + 1), userPreferences);
        }

        return forecastData;
    }
}
