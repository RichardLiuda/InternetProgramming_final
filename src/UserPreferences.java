import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UserPreferences {
    private String preferredUnits = "metric";
    private boolean notificationsEnabled = true;
    private static final String PREFERENCES_FILE = "resources/userPreferences.properties";

    public UserPreferences() {
        loadPreferences();
    }

    public String getPreferredUnits() {
        return preferredUnits;
    }

    public void setPreferredUnits(String preferredUnits) {
        this.preferredUnits = preferredUnits;
        savePreferences();
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled = enabled;
        savePreferences();
    }

    private void savePreferences() {
        Properties properties = new Properties();
        properties.setProperty("preferredUnits", preferredUnits);
        properties.setProperty("notificationsEnabled", String.valueOf(notificationsEnabled));

        try (FileOutputStream out = new FileOutputStream(PREFERENCES_FILE)) {
            properties.store(out, "User Preferences");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPreferences() {
        Properties properties = new Properties();

        try (FileInputStream in = new FileInputStream(PREFERENCES_FILE)) {
            properties.load(in);
            preferredUnits = properties.getProperty("preferredUnits", "metric");
            notificationsEnabled = Boolean.parseBoolean(properties.getProperty("notificationsEnabled", "true"));
        } catch (IOException e) {
            System.out.println("Preferences file not found. Using default preferences.");
        }
    }
}
