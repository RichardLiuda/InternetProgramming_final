import java.util.ArrayList;
import java.util.List;

public class ForecastHistory {
    private List<WeatherData> pastForecasts = new ArrayList<>();

    public void addForecast(WeatherData forecast) {
        pastForecasts.add(forecast);
    }

    public WeatherData[] getForecastHistory() {
        return pastForecasts.toArray(new WeatherData[0]);
    }
}
