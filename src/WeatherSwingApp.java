import java.awt.*;
import java.io.File;
import javax.swing.*;

public class WeatherSwingApp {

    private JFrame frame;
    private JPanel mainPanel;
    private WeatherApp weatherApp;
    private JLabel weatherResultLabel;
    private JTextArea forecastResultArea;
    private APISourceSelector apiSourceSelector;
    private NetworkStatusPanel networkStatusPanel;
    private EnhancedWeatherPanel enhancedWeatherPanel;
    private JPanel contentCards;
    private JButton[] navigationButtons; // Track navigation buttons for state management
    private int currentSelectedIndex = 0; // Track current selection

    public WeatherSwingApp() {
        weatherApp = new WeatherApp();
        enhancedWeatherPanel = new EnhancedWeatherPanel(weatherApp);
        
        // Initialize API source selector
        apiSourceSelector = new APISourceSelector();
        
        setupModernGUI();
    }

    private void setupModernGUI() {
        // Use modern Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        frame = new JFrame("Modern Weather App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setMinimumSize(new Dimension(900, 600)); // Set minimum size for proper display
        frame.setLocationRelativeTo(null);
        
        // Set application icon
        try {
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            // Ignore icon loading failure
        }

        // Create main layout
        createMainLayout();
        
        frame.setVisible(true);
    }

    private void createMainLayout() {
        // Main container with border layout
        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(ModernUIComponents.Colors.BACKGROUND);
        
        // Create main panel with enhanced spacing
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernUIComponents.Colors.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG
        ));
        
        // Create MD3 navigation panel
        JPanel navigationPanel = createMD3NavigationPanel();
        mainPanel.add(navigationPanel, BorderLayout.WEST);
        
        // Create content area with improved spacing
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(ModernUIComponents.Colors.BACKGROUND);
        contentArea.setBorder(BorderFactory.createEmptyBorder(
            0, ModernUIComponents.Spacing.LG, 0, 0
        ));
        
        // Create content with cards
        CardLayout cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(ModernUIComponents.Colors.BACKGROUND);
        
        // Add enhanced dashboard with proper spacing
        JPanel dashboard = createEnhancedDashboard();
        JScrollPane dashboardScroll = createOptimizedScrollPane(dashboard);
        contentCards.add(dashboardScroll, "Home");
        
        // Add existing enhanced weather panel with scroll support
        JScrollPane weatherScroll = createOptimizedScrollPane(enhancedWeatherPanel);
        contentCards.add(weatherScroll, "Weather");
        
        // Create modern forecast panel with scroll support
        JPanel forecastPanel = createModernForecastPanel();
        JScrollPane forecastScroll = createOptimizedScrollPane(forecastPanel);
        contentCards.add(forecastScroll, "Forecast");
        
        // Create enhanced preferences panel with scroll support
        JPanel preferencesPanel = createEnhancedPreferencesPanel();
        JScrollPane preferencesScroll = createOptimizedScrollPane(preferencesPanel);
        contentCards.add(preferencesScroll, "Preferences");
        
        contentArea.add(contentCards, BorderLayout.CENTER);
        mainPanel.add(contentArea, BorderLayout.CENTER);
        
        // Add modern status bar
        JPanel statusBar = createModernStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        container.add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createMD3NavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(ModernUIComponents.Colors.SURFACE);
        navPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, ModernUIComponents.Colors.OUTLINE_VARIANT),
            BorderFactory.createEmptyBorder(
                ModernUIComponents.Spacing.LG, 
                ModernUIComponents.Spacing.MD, 
                ModernUIComponents.Spacing.LG, 
                ModernUIComponents.Spacing.MD
            )
        ));
        navPanel.setPreferredSize(new Dimension(240, 0)); // Wider for MD3
        
        // Navigation title with MD3 typography
        JLabel navTitle = ModernUIComponents.createModernLabel(
            "Navigation", ModernUIComponents.Fonts.TITLE_MEDIUM, ModernUIComponents.Colors.ON_SURFACE);
        navTitle.setBorder(BorderFactory.createEmptyBorder(
            0, ModernUIComponents.Spacing.LG, ModernUIComponents.Spacing.XXL, ModernUIComponents.Spacing.LG
        ));
        navPanel.add(navTitle);
        
        String[] tabNames = {"Home", "Weather", "Forecast", "Settings"};
        String[] cardNames = {"Home", "Weather", "Forecast", "Preferences"};
        
        // Initialize navigation buttons array
        navigationButtons = new JButton[tabNames.length];
        
        for (int i = 0; i < tabNames.length; i++) {
            final int index = i;
            final String cardName = cardNames[i];
            
            // Create MD3 navigation item with proper selection state
            JButton navButton = ModernUIComponents.createMD3NavigationItem(
                tabNames[i], null, index == currentSelectedIndex);
            navigationButtons[i] = navButton;
            
            // MD3 navigation action with state management
            navButton.addActionListener(e -> {
                // Update selection state
                updateNavigationSelection(index);
                
                // Switch content
                CardLayout cl = (CardLayout) contentCards.getLayout();
                cl.show(contentCards, cardName);
            });
            
            // Full width alignment for MD3
            navButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            navButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, navButton.getPreferredSize().height));
            
            navPanel.add(navButton);
            
            // MD3 spacing between items
            if (i < tabNames.length - 1) {
                navPanel.add(Box.createVerticalStrut(ModernUIComponents.Spacing.XS));
            }
        }
        
        // Add flexible space at bottom
        navPanel.add(Box.createVerticalGlue());
        
        return navPanel;
    }
    
    // MD3 navigation state management
    private void updateNavigationSelection(int selectedIndex) {
        currentSelectedIndex = selectedIndex;
        
        // Update all navigation buttons
        for (int i = 0; i < navigationButtons.length; i++) {
            JButton button = navigationButtons[i];
            boolean isSelected = (i == selectedIndex);
            
            // Update visual state
            button.setForeground(isSelected ? 
                ModernUIComponents.Colors.ON_PRIMARY_CONTAINER : 
                ModernUIComponents.Colors.ON_SURFACE_VARIANT);
            
            // Force repaint to update selection indicator
            button.repaint();
            
            // Update button's selection state for paint method
            button.putClientProperty("isSelected", isSelected);
        }
    }
    
    private JPanel createEnhancedDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(ModernUIComponents.Colors.BACKGROUND);
        dashboard.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG
        ));
        
        // Dashboard title with MD3 typography
        JLabel titleLabel = ModernUIComponents.createModernLabel(
            "Dashboard", ModernUIComponents.Fonts.TITLE_LARGE, ModernUIComponents.Colors.ON_BACKGROUND);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(
            0, 0, ModernUIComponents.Spacing.XL, 0
        ));
        dashboard.add(titleLabel, BorderLayout.NORTH);
        
        // Create glass panel for main content
        JPanel glassContentPanel = ModernUIComponents.createGlassPanel("");
        glassContentPanel.setLayout(new BorderLayout());
        
        // Network status panel with enhanced spacing
        networkStatusPanel = new NetworkStatusPanel();
        networkStatusPanel.setBorder(BorderFactory.createEmptyBorder(
            0, 0, ModernUIComponents.Spacing.XL, 0
        ));
        glassContentPanel.add(networkStatusPanel, BorderLayout.NORTH);
        
        // Create cards grid with improved spacing
        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, ModernUIComponents.Spacing.LG, ModernUIComponents.Spacing.LG));
        cardsGrid.setOpaque(false);
        cardsGrid.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.MD, 0, 0, 0
        ));
        
        // Weather preview card
        JPanel weatherCard = createWeatherPreviewCard();
        cardsGrid.add(weatherCard);
        
        // API status card
        JPanel apiCard = createAPIStatusCard();
        cardsGrid.add(apiCard);
        
        // Quick actions card
        JPanel actionsCard = createQuickActionsCard();
        cardsGrid.add(actionsCard);
        
        // System info card
        JPanel systemCard = createSystemInfoCard();
        cardsGrid.add(systemCard);
        
        glassContentPanel.add(cardsGrid, BorderLayout.CENTER);
        dashboard.add(glassContentPanel, BorderLayout.CENTER);
        
        return dashboard;
    }
    
    private JPanel createWeatherPreviewCard() {
        JPanel card = ModernUIComponents.createGlassCard("Weather Preview");
        
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.SM, 0, 0, 0
        ));
        
        JLabel locationLabel = ModernUIComponents.createModernLabel("Location: Beijing", 
            ModernUIComponents.Fonts.BODY_MEDIUM, ModernUIComponents.Colors.TEXT_PRIMARY);
        JLabel tempLabel = ModernUIComponents.createModernLabel("Temperature: 22°C", 
            ModernUIComponents.Fonts.BODY_MEDIUM, ModernUIComponents.Colors.TEXT_PRIMARY);
        JLabel conditionLabel = ModernUIComponents.createModernLabel("Condition: Sunny", 
            ModernUIComponents.Fonts.BODY_MEDIUM, ModernUIComponents.Colors.TEXT_SECONDARY);
        
        // Stack labels with consistent spacing
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        labelsPanel.setOpaque(false);
        
        labelsPanel.add(locationLabel);
        labelsPanel.add(Box.createVerticalStrut(ModernUIComponents.Spacing.XS));
        labelsPanel.add(tempLabel);
        labelsPanel.add(Box.createVerticalStrut(ModernUIComponents.Spacing.XS));
        labelsPanel.add(conditionLabel);
        
        content.add(labelsPanel, BorderLayout.CENTER);
        
        JButton detailButton = ModernUIComponents.createModernButton("View Weather", ModernUIComponents.Colors.ACCENT);
        detailButton.setPreferredSize(new Dimension(120, 32));
        detailButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentCards.getLayout();
            cl.show(contentCards, "Weather");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.MD, 0, 0, 0
        ));
        buttonPanel.add(detailButton);
        
        content.add(buttonPanel, BorderLayout.SOUTH);
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createAPIStatusCard() {
        JPanel card = ModernUIComponents.createGlassCard("API Status");
        
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.SM, 0, 0, 0
        ));
        
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);
        
        // Status indicators with proper spacing
        JPanel openWeatherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        openWeatherPanel.setOpaque(false);
        openWeatherPanel.add(ModernUIComponents.createStatusIndicator("Online", ModernUIComponents.Colors.SUCCESS));
        openWeatherPanel.add(ModernUIComponents.createModernLabel("OpenWeatherMap"));
        
        JPanel weatherApiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        weatherApiPanel.setOpaque(false);
        weatherApiPanel.add(ModernUIComponents.createStatusIndicator("Offline", ModernUIComponents.Colors.ERROR));
        weatherApiPanel.add(ModernUIComponents.createModernLabel("WeatherAPI.com"));
        
        statusPanel.add(openWeatherPanel);
        statusPanel.add(Box.createVerticalStrut(ModernUIComponents.Spacing.SM));
        statusPanel.add(weatherApiPanel);
        
        content.add(statusPanel, BorderLayout.CENTER);
        
        JLabel summaryLabel = ModernUIComponents.createModernLabel("2/3 APIs Online", 
            ModernUIComponents.Fonts.SMALL, ModernUIComponents.Colors.SUCCESS);
        summaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.MD, 0, 0, 0
        ));
        
        content.add(summaryLabel, BorderLayout.SOUTH);
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createQuickActionsCard() {
        JPanel card = ModernUIComponents.createGlassCard("Quick Actions");
        
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.SM, 0, 0, 0
        ));
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        
        // Quick action buttons with consistent spacing
        JButton refreshButton = ModernUIComponents.createModernButton("Refresh Data", ModernUIComponents.Colors.ACCENT);
        refreshButton.setPreferredSize(new Dimension(140, 32));
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton forecastButton = ModernUIComponents.createModernButton("7-Day Forecast", ModernUIComponents.Colors.SUCCESS);
        forecastButton.setPreferredSize(new Dimension(140, 32));
        forecastButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        forecastButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentCards.getLayout();
            cl.show(contentCards, "Forecast");
        });
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(Box.createVerticalStrut(ModernUIComponents.Spacing.MD));
        buttonsPanel.add(forecastButton);
        
        content.add(buttonsPanel, BorderLayout.CENTER);
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createSystemInfoCard() {
        JPanel card = ModernUIComponents.createGlassCard("System Info");
        
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.SM, 0, 0, 0
        ));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        
        JLabel memoryLabel = ModernUIComponents.createModernLabel(
            String.format("Memory: %d/%d MB", usedMemory, maxMemory),
            ModernUIComponents.Fonts.SMALL, ModernUIComponents.Colors.TEXT_PRIMARY);
        
        JLabel javaLabel = ModernUIComponents.createModernLabel(
            "Java: " + System.getProperty("java.version"),
            ModernUIComponents.Fonts.SMALL, ModernUIComponents.Colors.TEXT_SECONDARY);
        
        JLabel osLabel = ModernUIComponents.createModernLabel(
            "OS: " + System.getProperty("os.name"),
            ModernUIComponents.Fonts.SMALL, ModernUIComponents.Colors.TEXT_SECONDARY);
        
        infoPanel.add(memoryLabel);
        infoPanel.add(Box.createVerticalStrut(ModernUIComponents.Spacing.XS));
        infoPanel.add(javaLabel);
        infoPanel.add(Box.createVerticalStrut(ModernUIComponents.Spacing.XS));
        infoPanel.add(osLabel);
        
        content.add(infoPanel, BorderLayout.CENTER);
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createModernForecastPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUIComponents.Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG
        ));
        
        JLabel titleLabel = ModernUIComponents.createModernLabel(
            "7-Day Weather Forecast", ModernUIComponents.Fonts.TITLE, ModernUIComponents.Colors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(
            0, 0, ModernUIComponents.Spacing.XL, 0
        ));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Placeholder for forecast content
        JPanel forecastContent = ModernUIComponents.createGlassPanel("Forecast will be displayed here");
        panel.add(forecastContent, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createEnhancedPreferencesPanel() {
        // Main container panel with proper layout for scrolling
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernUIComponents.Colors.BACKGROUND);
        
        // Content panel that will be scrolled
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ModernUIComponents.Colors.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG
        ));
        
        // Title
        JLabel titleLabel = ModernUIComponents.createModernLabel(
            "App Settings", ModernUIComponents.Fonts.TITLE, ModernUIComponents.Colors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(
            0, 0, ModernUIComponents.Spacing.XL, 0
        ));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        
        // API Configuration Section
        JPanel apiSection = ModernUIComponents.createGlassPanel("API Configuration");
        apiSection.setLayout(new BorderLayout());
        apiSection.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG
        ));
        apiSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        apiSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, apiSection.getPreferredSize().height));
        
        // Initialize API source selector if null
        if (apiSourceSelector == null) {
            apiSourceSelector = new APISourceSelector();
        }
        
        apiSourceSelector.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.MD, 0, 0, 0
        ));
        apiSection.add(apiSourceSelector, BorderLayout.CENTER);
        
        contentPanel.add(apiSection);
        contentPanel.add(Box.createVerticalStrut(ModernUIComponents.Spacing.XL));
        
        // Network Status Section
        JPanel networkSection = ModernUIComponents.createGlassPanel("Network Status Monitor");
        networkSection.setLayout(new BorderLayout());
        networkSection.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG
        ));
        networkSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        networkSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, networkSection.getPreferredSize().height));
        
        // Initialize network status panel if null
        if (networkStatusPanel == null) {
            networkStatusPanel = new NetworkStatusPanel();
        }
        
        networkStatusPanel.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.MD, 0, 0, 0
        ));
        networkSection.add(networkStatusPanel, BorderLayout.CENTER);
        
        contentPanel.add(networkSection);
        contentPanel.add(Box.createVerticalStrut(ModernUIComponents.Spacing.XL));
        
        // Add bottom spacer to ensure proper scrolling
        contentPanel.add(Box.createVerticalGlue());
        
        mainPanel.add(contentPanel, BorderLayout.NORTH);
        
        return mainPanel;
    }

    private JPanel createModernStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setPreferredSize(new Dimension(0, 40)); // Increased height for better spacing
        statusBar.setBackground(ModernUIComponents.Colors.PRIMARY_DARK);
        statusBar.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.SM, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.SM, 
            ModernUIComponents.Spacing.LG
        ));

        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(ModernUIComponents.Fonts.SMALL);
        statusLabel.setForeground(Color.WHITE);
        
        JLabel timeLabel = new JLabel("Current time: " + java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        timeLabel.setFont(ModernUIComponents.Fonts.SMALL);
        timeLabel.setForeground(Color.WHITE);
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        return statusBar;
    }

    /**
     * Create an optimized scroll pane with consistent styling
     */
    private JScrollPane createOptimizedScrollPane(JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component);
        
        // Set background colors
        scrollPane.setBackground(ModernUIComponents.Colors.BACKGROUND);
        scrollPane.getViewport().setBackground(ModernUIComponents.Colors.BACKGROUND);
        
        // Remove borders
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBorder(null);
        
        // Configure scroll policies
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Set scroll increments for smooth scrolling
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        
        // Optimize scrolling performance
        scrollPane.setWheelScrollingEnabled(true);
        
        return scrollPane;
    }

    // Public methods for accessing components
    public WeatherApp getWeatherApp() {
        return weatherApp;
    }

    public APISourceSelector getApiSourceSelector() {
        return apiSourceSelector;
    }

    public NetworkStatusPanel getNetworkStatusPanel() {
        return networkStatusPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system Look and Feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new WeatherSwingApp();
        });
    }
}
