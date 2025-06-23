public class WeatherData {
    private double temperature;
    private double humidity;
    private double windSpeed;
    private String description;
    private String forecastDate;
    private UserPreferences userPreferences;

    public WeatherData(double temperature, double humidity, double windSpeed, String description, String forecastDate, UserPreferences userPreferences) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.description = description;
        this.forecastDate = forecastDate;
        this.userPreferences = userPreferences;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getDescription() {
        return description;
    }

    public String getForecastDate() {
        return forecastDate;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }
    

    @Override
    public String toString() {
        String tempUnit = userPreferences.getPreferredUnits().equals("imperial") ? "°F" : "°C";
        String windUnit = userPreferences.getPreferredUnits().equals("imperial") ? "mph" : "m/s";

        return "Date: " + forecastDate +
               "\nTemperature: " + temperature + tempUnit +
               "\nHumidity: " + humidity + "%" +
               "\nWind Speed: " + windSpeed + " " + windUnit +
               "\nDescription: " + description;
    }
}
