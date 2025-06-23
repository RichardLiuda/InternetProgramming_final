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
    
    // Get temperature in Celsius (always)
    public double getTemperatureCelsius() {
        // If temperature seems to be in Fahrenheit (high values), convert it
        if (temperature > 50) {
            return fahrenheitToCelsius(temperature);
        }
        return temperature;
    }
    
    // Temperature conversion utility
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5.0 / 9.0;
    }
    
    // Temperature conversion utility
    public static double celsiusToFahrenheit(double celsius) {
        return (celsius * 9.0 / 5.0) + 32;
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
        // Always display in Celsius regardless of API response
        double tempCelsius = getTemperatureCelsius();
        
        return "Date: " + forecastDate +
               "\nTemperature: " + String.format("%.1f", tempCelsius) + "°C" +
               "\nHumidity: " + humidity + "%" +
               "\nWind Speed: " + windSpeed + " m/s" +
               "\nDescription: " + description;
    }
}
