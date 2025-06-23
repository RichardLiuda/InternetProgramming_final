import java.awt.*;
import java.io.File;
import javax.swing.*;

public class WeatherSwingApp {

    private JFrame frame;
    private JPanel mainPanel;
    private WeatherApp weatherApp;
    private JLabel weatherResultLabel;
    private JTextArea forecastResultArea;

    public WeatherSwingApp() {
        weatherApp = new WeatherApp();
        setupGUI();
    }

    private void setupGUI() {
        frame = new JFrame("Weather Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createWeatherPanel(), "Weather");
        mainPanel.add(createForecastPanel(), "Forecast");
        mainPanel.add(createPreferencesPanel(), "Preferences");

        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(new Color(245, 245, 245));

        JButton homeButton = createButton("Home", new Color(255, 239, 213));
        JButton weatherButton = createButton("Current Weather", new Color(180, 240, 180));
        JButton forecastButton = createButton("Weather Forecast", new Color(250, 250, 210));
        JButton preferencesButton = createButton("Preferences", new Color(173, 216, 230));

        homeButton.addActionListener(e -> showPanel("Home"));
        weatherButton.addActionListener(e -> showPanel("Weather"));
        forecastButton.addActionListener(e -> showPanel("Forecast"));
        preferencesButton.addActionListener(e -> showPanel("Preferences"));

        navigationPanel.add(homeButton);
        navigationPanel.add(weatherButton);
        navigationPanel.add(forecastButton);
        navigationPanel.add(preferencesButton);

        frame.add(navigationPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        return button;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    File imageFile = new File("resources/home_background.jpg");
                    if (imageFile.exists()) {
                        ImageIcon background = new ImageIcon(imageFile.getAbsolutePath());
                        g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
                    } else {
                        System.out.println("Background image not found at: " + imageFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    System.out.println("Error loading background image: " + e.getMessage());
                }
            }
        };

        panel.setLayout(new GridBagLayout());

        JLabel welcomeLabel = new JLabel("Welcome to the Weather Application!");
        welcomeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        panel.add(welcomeLabel);
        return panel;
    }

    private JPanel createWeatherPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        weatherResultLabel = new JLabel("Enter a location to see the weather details.", JLabel.CENTER);
        JTextField locationField = new JTextField(20);
        JButton fetchButton = createButton("Fetch Weather", new Color(152, 251, 152));

        fetchButton.addActionListener(e -> {
            String location = locationField.getText();
            if (!location.isEmpty()) {
                Location loc = new Location(location);
                String result = weatherApp.displayWeather(loc);
                weatherResultLabel.setText("<html>" + result.replaceAll("\\n", "<br>") + "</html>");
            } else {
                weatherResultLabel.setText("Please enter a valid location.");
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Location:"));
        inputPanel.add(locationField);
        inputPanel.add(fetchButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(weatherResultLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createForecastPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        forecastResultArea = new JTextArea();
        forecastResultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(forecastResultArea);
        JTextField locationField = new JTextField(20);
        JTextField daysField = new JTextField(5);
        JButton fetchButton = createButton("Fetch Forecast", new Color(255, 239, 170));

        fetchButton.addActionListener(e -> {
            String location = locationField.getText();
            String daysText = daysField.getText();
            try {
                int days = Integer.parseInt(daysText);
                Location loc = new Location(location);
                String result = weatherApp.displayForecast(loc, days);
                forecastResultArea.setText(result);
            } catch (NumberFormatException ex) {
                forecastResultArea.setText("Please enter a valid number of days.");
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Location:"));
        inputPanel.add(locationField);
        inputPanel.add(new JLabel("Days:"));
        inputPanel.add(daysField);
        inputPanel.add(fetchButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPreferencesPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JCheckBox notificationsCheck = new JCheckBox("Enable Notifications", weatherApp.getUserPreferences().isNotificationsEnabled());
        JComboBox<String> unitComboBox = new JComboBox<>(new String[]{"Metric", "Imperial"});
        unitComboBox.setSelectedItem(weatherApp.getUserPreferences().getPreferredUnits().equals("metric") ? "Metric" : "Imperial");

        JButton saveButton = createButton("Save Preferences", new Color(175, 238, 238));
        saveButton.addActionListener(e -> {
            boolean notificationsEnabled = notificationsCheck.isSelected();
            String selectedUnit = unitComboBox.getSelectedItem().toString().toLowerCase();

            weatherApp.getUserPreferences().setNotificationsEnabled(notificationsEnabled);
            weatherApp.getUserPreferences().setPreferredUnits(selectedUnit);

            JOptionPane.showMessageDialog(frame, "Preferences saved!");
        });

        panel.add(notificationsCheck);
        panel.add(new JLabel("Preferred Units:"));
        panel.add(unitComboBox);
        panel.add(saveButton);
        return panel;
    }

    private void showPanel(String panelName) {
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, panelName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WeatherSwingApp::new);
    }
}
