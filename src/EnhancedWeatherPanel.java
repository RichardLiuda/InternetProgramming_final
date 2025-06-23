import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;

public class EnhancedWeatherPanel extends JPanel {
    private WeatherApp weatherApp;
    private JTextField locationField;
    private JPanel weatherCardsPanel;
    private JPanel forecastPanel;
    private JButton refreshButton;
    private JLabel lastUpdateLabel;
    private Timer autoRefreshTimer;
    
    // Weather data display components
    private JPanel temperatureCard;
    private JPanel humidityCard;
    private JPanel windCard;
    private JPanel descriptionCard;
    
    public EnhancedWeatherPanel(WeatherApp weatherApp) {
        this.weatherApp = weatherApp;
        setupUI();
        setupAutoRefresh();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(ModernUIComponents.Colors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create top input panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBackground(ModernUIComponents.Colors.BACKGROUND);
        
        // Create weather cards panel
        weatherCardsPanel = createWeatherCardsPanel();
        mainContent.add(weatherCardsPanel, BorderLayout.CENTER);
        
        // Create forecast panel
        forecastPanel = createForecastPanel();
        mainContent.add(forecastPanel, BorderLayout.SOUTH);
        
        add(mainContent, BorderLayout.CENTER);
        
        // Create bottom status panel
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUIComponents.Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Title
        JLabel titleLabel = ModernUIComponents.createModernLabel(
            "Weather Query", 
            ModernUIComponents.Fonts.TITLE, 
            ModernUIComponents.Colors.TEXT_PRIMARY
        );
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(ModernUIComponents.Colors.BACKGROUND);
        
        JLabel locationLabel = ModernUIComponents.createModernLabel(
            "Location:", ModernUIComponents.Fonts.BODY, ModernUIComponents.Colors.TEXT_PRIMARY);
        inputPanel.add(locationLabel);
        
        locationField = ModernUIComponents.createModernTextField("Enter city name");
        locationField.setPreferredSize(new Dimension(280, 35));
        inputPanel.add(locationField);
        
        JButton searchButton = ModernUIComponents.createModernButton("Search Weather", ModernUIComponents.Colors.ACCENT);
        searchButton.addActionListener(this::fetchWeatherData);
        inputPanel.add(searchButton);
        
        refreshButton = ModernUIComponents.createModernButton("Refresh", ModernUIComponents.Colors.SUCCESS);
        refreshButton.addActionListener(this::refreshWeatherData);
        refreshButton.setEnabled(false);
        inputPanel.add(refreshButton);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createWeatherCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(ModernUIComponents.Colors.BACKGROUND);
        
        // Create weather info cards
        temperatureCard = ModernUIComponents.createInfoCard("Temperature", "--", "°C");
        humidityCard = ModernUIComponents.createInfoCard("Humidity", "--", "%");
        windCard = ModernUIComponents.createInfoCard("Wind Speed", "--", "m/s");
        descriptionCard = createDescriptionCard();
        
        panel.add(temperatureCard);
        panel.add(humidityCard);
        panel.add(windCard);
        panel.add(descriptionCard);
        
        return panel;
    }
    
    private JPanel createDescriptionCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        
        card.setBackground(ModernUIComponents.Colors.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernUIComponents.Colors.BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(180, 100));
        
        JLabel titleLabel = ModernUIComponents.createModernLabel(
            "Weather Description", ModernUIComponents.Fonts.SMALL, ModernUIComponents.Colors.TEXT_SECONDARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel descLabel = ModernUIComponents.createModernLabel(
            "No data", ModernUIComponents.Fonts.BODY, ModernUIComponents.Colors.TEXT_PRIMARY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createForecastPanel() {
        JPanel panel = ModernUIComponents.createModernPanel("5-Day Weather Forecast");
        panel.setLayout(new GridLayout(1, 5, 10, 10));
        panel.setPreferredSize(new Dimension(0, 120));
        
        // Add placeholders
        for (int i = 0; i < 5; i++) {
            JPanel dayCard = createForecastDayCard("Day " + (i + 1), "--°", "No data");
            panel.add(dayCard);
        }
        
        return panel;
    }
    
    private JPanel createForecastDayCard(String day, String temp, String desc) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ModernUIComponents.Colors.SURFACE);
        card.setBorder(BorderFactory.createLineBorder(ModernUIComponents.Colors.BORDER));
        
        JLabel dayLabel = ModernUIComponents.createModernLabel(
            day, ModernUIComponents.Fonts.SMALL, ModernUIComponents.Colors.TEXT_SECONDARY);
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel tempLabel = ModernUIComponents.createModernLabel(
            temp, ModernUIComponents.Fonts.SUBTITLE, ModernUIComponents.Colors.TEXT_PRIMARY);
        tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel descLabel = ModernUIComponents.createModernLabel(
            desc, ModernUIComponents.Fonts.SMALL, ModernUIComponents.Colors.TEXT_SECONDARY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(dayLabel, BorderLayout.NORTH);
        card.add(tempLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUIComponents.Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        lastUpdateLabel = ModernUIComponents.createModernLabel(
            "Last updated: Not updated", ModernUIComponents.Fonts.SMALL, ModernUIComponents.Colors.TEXT_SECONDARY);
        
        // Auto refresh control
        JPanel autoRefreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        autoRefreshPanel.setBackground(ModernUIComponents.Colors.BACKGROUND);
        
        JCheckBox autoRefreshCheck = new JCheckBox("Auto Refresh (5 minutes)");
        autoRefreshCheck.setFont(ModernUIComponents.Fonts.SMALL);
        autoRefreshCheck.setBackground(ModernUIComponents.Colors.BACKGROUND);
        autoRefreshCheck.addActionListener(e -> toggleAutoRefresh(autoRefreshCheck.isSelected()));
        
        autoRefreshPanel.add(autoRefreshCheck);
        
        panel.add(lastUpdateLabel, BorderLayout.WEST);
        panel.add(autoRefreshPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void setupAutoRefresh() {
        // Create 5-minute auto refresh timer
        autoRefreshTimer = new Timer(5 * 60 * 1000, e -> {
            if (!locationField.getText().trim().isEmpty() && 
                !locationField.getText().equals("Enter city name")) {
                refreshWeatherData(null);
            }
        });
    }
    
    private void toggleAutoRefresh(boolean enabled) {
        if (enabled) {
            autoRefreshTimer.start();
        } else {
            autoRefreshTimer.stop();
        }
    }
    
    private void fetchWeatherData(ActionEvent e) {
        String location = locationField.getText().trim();
        if (location.isEmpty() || location.equals("Enter city name")) {
            showErrorDialog("Input Error", "Please enter a valid city name");
            return;
        }
        
        // Disable button and show loading status
        setUIEnabled(false);
        updateLastUpdateTime("Loading...");
        
        CompletableFuture.runAsync(() -> {
            try {
                Location loc = new Location(location);
                WeatherData currentWeather = weatherApp.getApiManager().getCurrentWeather(loc);
                WeatherData[] forecast = weatherApp.getApiManager().getForecast(loc, 5);
                
                SwingUtilities.invokeLater(() -> {
                    updateWeatherDisplay(currentWeather);
                    updateForecastDisplay(forecast);
                    updateLastUpdateTime("Just updated");
                    refreshButton.setEnabled(true);
                    setUIEnabled(true);
                });
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    String errorMessage = ex.getMessage();
                    String errorTitle = "Weather Data Error";
                    
                    // Categorize errors for better user experience
                    if (errorMessage.contains("not found") || errorMessage.contains("spelling")) {
                        errorTitle = "City Not Found";
                    } else if (errorMessage.contains("timeout") || errorMessage.contains("connection")) {
                        errorTitle = "Connection Error";
                    } else if (errorMessage.contains("authentication") || errorMessage.contains("API key")) {
                        errorTitle = "Service Error";
                    }
                    
                    showErrorDialog(errorTitle, errorMessage);
                    updateLastUpdateTime("Update failed");
                    setUIEnabled(true);
                });
            }
        });
    }
    
    private void showErrorDialog(String title, String message) {
        // Create a more user-friendly error dialog
        JOptionPane optionPane = new JOptionPane(
            "<html><div style='width: 300px;'>" + message + "</div></html>",
            JOptionPane.ERROR_MESSAGE
        );
        
        JDialog dialog = optionPane.createDialog(this, title);
        dialog.setModal(true);
        dialog.setVisible(true);
    }
    
    private void refreshWeatherData(ActionEvent e) {
        fetchWeatherData(e);
    }
    
    private void updateWeatherDisplay(WeatherData weather) {
        System.out.println("=== Updating Weather Display ===");
        System.out.println("Temperature (Celsius): " + weather.getTemperatureCelsius());
        System.out.println("Humidity: " + weather.getHumidity());
        System.out.println("Wind Speed: " + weather.getWindSpeed());
        System.out.println("Description: " + weather.getDescription());
        
        // Update temperature card - ensure using Celsius
        updateInfoCard(temperatureCard, String.format("%.1f", weather.getTemperatureCelsius()));
        
        // Update humidity card
        updateInfoCard(humidityCard, String.format("%.0f", weather.getHumidity()));
        
        // Update wind speed card
        updateInfoCard(windCard, String.format("%.1f", weather.getWindSpeed()));
        
        // Update description card
        Component[] components = descriptionCard.getComponents();
        if (components.length > 1 && components[1] instanceof JLabel) {
            JLabel descLabel = (JLabel) components[1];
            descLabel.setText("<html><center>" + weather.getDescription() + "</center></html>");
            System.out.println("Updated description card with: " + weather.getDescription());
        }
        
        System.out.println("=== Weather Display Update Complete ===");
    }
    
    private void updateInfoCard(JPanel card, String value) {
        // Debug: Print card structure
        System.out.println("Updating info card with value: " + value);
        System.out.println("Card components count: " + card.getComponentCount());
        
        Component[] components = card.getComponents();
        
        // MD3InfoCard structure: [titleLabel, valuePanel]
        // valuePanel contains: [valueLabel, unitLabel]
        if (components.length >= 2 && components[1] instanceof JPanel) {
            JPanel valuePanel = (JPanel) components[1];
            Component[] valuePanelComponents = valuePanel.getComponents();
            
            System.out.println("ValuePanel components count: " + valuePanelComponents.length);
            
            // Find the value label (should be the first component in valuePanel)
            if (valuePanelComponents.length >= 1 && valuePanelComponents[0] instanceof JLabel) {
                JLabel valueLabel = (JLabel) valuePanelComponents[0];
                String currentText = valueLabel.getText();
                System.out.println("Current value label text: " + currentText);
                
                // Update only the value, keep the formatting
                valueLabel.setText(value);
                System.out.println("Updated value label text to: " + value);
                
                // Force repaint
                valueLabel.revalidate();
                valueLabel.repaint();
            } else {
                System.out.println("Could not find value label in valuePanel");
            }
        } else {
            System.out.println("Card structure does not match expected MD3InfoCard format");
            // Fallback for legacy structure
            if (components.length > 1 && components[1] instanceof JLabel) {
                JLabel valueLabel = (JLabel) components[1];
                String[] parts = valueLabel.getText().split(" ");
                if (parts.length > 1) {
                    valueLabel.setText(value + " " + parts[parts.length - 1]);
                } else {
                    valueLabel.setText(value);
                }
            }
        }
        
        // Force card repaint
        card.revalidate();
        card.repaint();
    }
    
    private void updateForecastDisplay(WeatherData[] forecast) {
        Component[] dayCards = forecastPanel.getComponents();
        
        for (int i = 0; i < Math.min(forecast.length, dayCards.length); i++) {
            if (dayCards[i] instanceof JPanel && forecast[i] != null) {
                JPanel dayCard = (JPanel) dayCards[i];
                Component[] components = dayCard.getComponents();
                
                // Update temperature with Celsius unit - use getTemperatureCelsius()
                if (components.length > 1 && components[1] instanceof JLabel) {
                    JLabel tempLabel = (JLabel) components[1];
                    tempLabel.setText(String.format("%.0f°C", forecast[i].getTemperatureCelsius()));
                }
                
                // Update description
                if (components.length > 2 && components[2] instanceof JLabel) {
                    JLabel descLabel = (JLabel) components[2];
                    String desc = forecast[i].getDescription();
                    if (desc.length() > 8) {
                        desc = desc.substring(0, 8) + "...";
                    }
                    descLabel.setText(desc);
                }
            }
        }
        
        forecastPanel.revalidate();
        forecastPanel.repaint();
    }
    
    private void updateLastUpdateTime(String time) {
        lastUpdateLabel.setText("Last updated: " + time);
    }
    
    private void setUIEnabled(boolean enabled) {
        Component[] inputComponents = ((JPanel) ((JPanel) getComponent(0)).getComponent(1)).getComponents();
        for (Component comp : inputComponents) {
            if (comp instanceof JButton) {
                comp.setEnabled(enabled);
            }
        }
        locationField.setEnabled(enabled);
    }
    
    public void cleanup() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
    }
} 