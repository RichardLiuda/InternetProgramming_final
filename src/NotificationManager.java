import javax.swing.JOptionPane;

public class NotificationManager {
    private boolean enabledNotifications = true;

    public void sendNotification(String message) {
        if (enabledNotifications) {
            JOptionPane.showMessageDialog(null, "Notification: " + message);
        }
    }

    public void manageNotificationSettings(boolean enabled) {
        this.enabledNotifications = enabled;
    }

    public void checkAndSendWeatherAlert(WeatherData data) {
        if (enabledNotifications) {
            String units = data.getUserPreferences().getPreferredUnits();
            
            // Temperature thresholds for Celsius and Fahrenheit -> Below or Above this gon give alert
            double highTempThreshold = units.equals("imperial") ? 95.0 : 35.0;
            double lowTempThreshold = units.equals("imperial") ? 32.0 : 0.0;

            if (data.getTemperature() > highTempThreshold) {
                sendNotification("High temperature alert: " + data.getTemperature() + (units.equals("imperial") ? "°F" : "°C"));
            } else if (data.getTemperature() < lowTempThreshold) {
                sendNotification("Low temperature alert: " + data.getTemperature() + (units.equals("imperial") ? "°F" : "°C"));
            }
        }
    }
}
