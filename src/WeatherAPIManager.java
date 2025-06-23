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
        // Force metric units for Celsius temperature
        this.userPreferences.setPreferredUnits("metric");
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
        // Force metric units to ensure Celsius temperature
        String units = "metric";
        String endpoint = baseURL + endpointType + "?q=" + location.getCity() + "&appid=" + apiKey + "&units=" + units;
        
        System.out.println("API Request URL: " + endpoint); // Debug log
        
        StringBuilder inline = new StringBuilder();

        try {
            URL url = URI.create(endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5 second timeout
            conn.setReadTimeout(5000); // 5 second read timeout
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                Scanner sc = new Scanner(conn.getInputStream());
                while (sc.hasNext()) {
                    inline.append(sc.nextLine());
                }
                sc.close();
            } else {
                // Handle different HTTP error codes with friendly messages
                String errorMessage = getErrorMessage(responseCode, location.getCity());
                throw new RuntimeException(errorMessage);
            }
            
            conn.disconnect();
            
        } catch (java.io.FileNotFoundException e) {
            throw new RuntimeException("City '" + location.getCity() + "' not found. Please check the spelling and try again.");
        } catch (java.net.SocketTimeoutException e) {
            throw new RuntimeException("Request timeout. Please check your internet connection and try again.");
        } catch (java.net.UnknownHostException e) {
            throw new RuntimeException("Unable to connect to weather service. Please check your internet connection.");
        } catch (java.io.IOException e) {
            throw new RuntimeException("Network error: " + e.getMessage());
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e; // Re-throw our custom error messages
            }
            throw new RuntimeException("Failed to fetch weather data: " + e.getMessage());
        }
        
        System.out.println("API Response: " + inline.toString()); // Debug log
        return inline.toString();
    }

    private WeatherData parseAPIResponse(String response) {
        try {
            if (response == null || response.trim().isEmpty()) {
                throw new RuntimeException("Empty response from weather service.");
            }
            
            JsonObject jsonObj = JsonParser.parseString(response).getAsJsonObject();
            
            // Check if the response contains an error
            if (jsonObj.has("cod")) {
                String cod = jsonObj.get("cod").getAsString();
                if (!"200".equals(cod)) {
                    String message = jsonObj.has("message") ? 
                        jsonObj.get("message").getAsString() : 
                        "Unknown error from weather service";
                    throw new RuntimeException("Weather service error: " + message);
                }
            }
            
            double temp = jsonObj.getAsJsonObject("main").get("temp").getAsDouble();
            double humidity = jsonObj.getAsJsonObject("main").get("humidity").getAsDouble();
            double windSpeed = jsonObj.getAsJsonObject("wind").get("speed").getAsDouble();
            String description = jsonObj.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

            System.out.println("Parsed temperature: " + temp + "°C"); // Debug log
            
            // Ensure temperature is in Celsius (metric units should already provide this)
            // If for some reason we receive Fahrenheit, convert it
            if (temp > 50) { // Likely Fahrenheit if over 50 (rare for Celsius weather)
                temp = fahrenheitToCelsius(temp);
                System.out.println("Converted temperature: " + temp + "°C"); // Debug log
            }

            // Passing userPreferences to WeatherData with confirmed metric units
            return new WeatherData(temp, humidity, windSpeed, description, "Current", userPreferences);
            
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e; // Re-throw our custom error messages
            }
            throw new RuntimeException("Failed to parse weather data: " + e.getMessage());
        }
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

            // Ensure temperature is in Celsius
            if (temp > 50) { // Likely Fahrenheit if over 50
                temp = fahrenheitToCelsius(temp);
            }

            // Passing userPreferences to WeatherData with confirmed metric units
            forecastData[i] = new WeatherData(temp, humidity, windSpeed, description, "Forecast Day " + (i + 1), userPreferences);
        }

        return forecastData;
    }
    
    // Temperature conversion utility method
    private double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5.0 / 9.0;
    }

    private String getErrorMessage(int responseCode, String cityName) {
        switch (responseCode) {
            case 401:
                return "API authentication failed. Please check the API key configuration.";
            case 404:
                return "City '" + cityName + "' not found. Please check the spelling and try again.";
            case 429:
                return "Too many requests. Please wait a moment and try again.";
            case 500:
                return "Weather service is temporarily unavailable. Please try again later.";
            case 503:
                return "Weather service is under maintenance. Please try again later.";
            default:
                return "Failed to fetch weather data. HTTP Error: " + responseCode;
        }
    }
}
