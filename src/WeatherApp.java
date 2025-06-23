public class WeatherApp {
    private UserPreferences userPreferences = new UserPreferences();
    private ForecastHistory forecastHistory = new ForecastHistory();
    private NotificationManager notificationManager = new NotificationManager();
    private WeatherAPIManager apiManager = new WeatherAPIManager(userPreferences);

    public String displayWeather(Location location) {
        WeatherData currentWeather = apiManager.getCurrentWeather(location);
        forecastHistory.addForecast(currentWeather);
        notificationManager.checkAndSendWeatherAlert(currentWeather);
        return currentWeather.toString();
    }

    public String displayForecast(Location location, int days) {
        WeatherData[] forecast = apiManager.getForecast(location, days);
        StringBuilder result = new StringBuilder();
        for (WeatherData data : forecast) {
            forecastHistory.addForecast(data);
            notificationManager.checkAndSendWeatherAlert(data);
            result.append(data.toString()).append("\n\n");
        }
        return result.toString();
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    public WeatherAPIManager getApiManager() {
        return apiManager;
    }
}
