import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NetworkStatusPanel extends JPanel {
    private JPanel connectionCard;
    private JPanel latencyCard;
    private JPanel ipv6Card;
    private JPanel apiServerCardsContainer;
    private ScheduledExecutorService scheduler;
    private boolean isMonitoring = false;
    private JButton monitoringToggleButton;
    
    // Network diagnostic data
    private boolean isConnected = false;
    private long latency = 0;
    private boolean ipv6Supported = false;
    
    public NetworkStatusPanel() {
        setupMD3UI();
        scheduler = Executors.newScheduledThreadPool(2);
        startNetworkMonitoring();
    }
    
    private void setupMD3UI() {
        setLayout(new BorderLayout());
        setBackground(ModernUIComponents.Colors.BACKGROUND);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG
        ));
        
        // Main container with MD3 elevation
        JPanel mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // MD3 elevated surface
                drawMD3Shadow(g2, getWidth(), getHeight(), 24);
                g2.setColor(ModernUIComponents.Colors.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                
                g2.dispose();
            }
            
            private void drawMD3Shadow(Graphics2D g2, int width, int height, int radius) {
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fillRoundRect(0, 2, width, height, radius, radius);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(0, 1, width, height, radius, radius);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(1, 3, width - 2, height - 1, radius, radius);
            }
        };
        
        mainContainer.setLayout(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.XL, 
            ModernUIComponents.Spacing.XL, 
            ModernUIComponents.Spacing.XL, 
            ModernUIComponents.Spacing.XL
        ));
        
        // Header section
        JPanel headerSection = createHeaderSection();
        mainContainer.add(headerSection, BorderLayout.NORTH);
        
        // Content area with cards
        JPanel contentArea = createContentArea();
        mainContainer.add(contentArea, BorderLayout.CENTER);
        
        // Control section
        JPanel controlSection = createControlSection();
        mainContainer.add(controlSection, BorderLayout.SOUTH);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, ModernUIComponents.Spacing.XL, 0));
        
        // Title
        JLabel titleLabel = ModernUIComponents.createModernLabel(
            "Network Status Monitor", 
            ModernUIComponents.Fonts.TITLE_LARGE, 
            ModernUIComponents.Colors.ON_SURFACE
        );
        header.add(titleLabel, BorderLayout.WEST);
        
        // Status indicator
        JPanel statusIndicator = createOverallStatusIndicator();
        header.add(statusIndicator, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createOverallStatusIndicator() {
        JPanel indicator = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        indicator.setOpaque(false);
        
        // Create status chip
        JPanel statusChip = ModernUIComponents.createMD3StatusChip(
            isConnected ? "Online" : "Checking...", 
            isConnected ? ModernUIComponents.Colors.SUCCESS : ModernUIComponents.Colors.WARNING
        );
        
        indicator.add(statusChip);
        return indicator;
    }
    
    private JPanel createContentArea() {
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        
        // Network metrics cards section
        JPanel metricsSection = createNetworkMetricsSection();
        content.add(metricsSection, BorderLayout.NORTH);
        
        // API servers section
        JPanel apiSection = createAPIServersSection();
        content.add(apiSection, BorderLayout.CENTER);
        
        return content;
    }
    
    private JPanel createNetworkMetricsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(0, 0, ModernUIComponents.Spacing.XL, 0));
        
        // Section title
        JLabel sectionTitle = ModernUIComponents.createModernLabel(
            "Connection Status", 
            ModernUIComponents.Fonts.TITLE_MEDIUM, 
            ModernUIComponents.Colors.ON_SURFACE
        );
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, ModernUIComponents.Spacing.LG, 0));
        section.add(sectionTitle, BorderLayout.NORTH);
        
        // Metrics cards grid
        JPanel metricsGrid = new JPanel(new GridLayout(1, 3, ModernUIComponents.Spacing.LG, 0));
        metricsGrid.setOpaque(false);
        
        // Connection status card
        connectionCard = ModernUIComponents.createMD3InfoCard(
            "Network Connection", 
            "Checking...", 
            "", 
            ModernUIComponents.ComponentVariant.OUTLINED
        );
        
        // Latency card
        latencyCard = ModernUIComponents.createMD3InfoCard(
            "Network Latency", 
            "--", 
            "ms", 
            ModernUIComponents.ComponentVariant.OUTLINED
        );
        
        // IPv6 support card
        ipv6Card = ModernUIComponents.createMD3InfoCard(
            "IPv6 Support", 
            "Checking...", 
            "", 
            ModernUIComponents.ComponentVariant.OUTLINED
        );
        
        metricsGrid.add(connectionCard);
        metricsGrid.add(latencyCard);
        metricsGrid.add(ipv6Card);
        
        section.add(metricsGrid, BorderLayout.CENTER);
        
        return section;
    }
    
    private JPanel createAPIServersSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        
        // Section title
        JLabel sectionTitle = ModernUIComponents.createModernLabel(
            "API Server Status", 
            ModernUIComponents.Fonts.TITLE_MEDIUM, 
            ModernUIComponents.Colors.ON_SURFACE
        );
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, ModernUIComponents.Spacing.LG, 0));
        section.add(sectionTitle, BorderLayout.NORTH);
        
        // API servers container
        apiServerCardsContainer = new JPanel();
        apiServerCardsContainer.setLayout(new BoxLayout(apiServerCardsContainer, BoxLayout.Y_AXIS));
        apiServerCardsContainer.setOpaque(false);
        
        // Add API server cards
        String[] apiServers = {
            "api.openweathermap.org",
            "api.weatherapi.com", 
            "dataservice.accuweather.com"
        };
        
        for (String serverName : apiServers) {
            JPanel serverCard = createMD3ServerStatusCard(serverName);
            apiServerCardsContainer.add(serverCard);
            apiServerCardsContainer.add(Box.createVerticalStrut(ModernUIComponents.Spacing.MD));
        }
        
        section.add(apiServerCardsContainer, BorderLayout.CENTER);
        
        return section;
    }
    
    private JPanel createMD3ServerStatusCard(String serverName) {
        return ModernUIComponents.createMD3StatusCard(
            null, 
            serverName, 
            "Checking...", 
            ModernUIComponents.Colors.WARNING
        );
    }
    
    private JPanel createControlSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(ModernUIComponents.Spacing.XL, 0, 0, 0));
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, ModernUIComponents.Spacing.LG, 0));
        buttonPanel.setOpaque(false);
        
        // Refresh button
        JButton refreshButton = ModernUIComponents.createMD3Button(
            "Refresh Status", 
            ModernUIComponents.ComponentVariant.FILLED, 
            ModernUIComponents.Colors.PRIMARY
        );
        refreshButton.addActionListener(e -> refreshNetworkStatus());
        
        // Monitoring toggle button
        monitoringToggleButton = ModernUIComponents.createMD3Button(
            isMonitoring ? "Stop Monitoring" : "Start Monitoring", 
            ModernUIComponents.ComponentVariant.TONAL, 
            isMonitoring ? ModernUIComponents.Colors.ERROR : ModernUIComponents.Colors.SUCCESS
        );
        monitoringToggleButton.addActionListener(e -> toggleNetworkMonitoring());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(monitoringToggleButton);
        
        section.add(buttonPanel, BorderLayout.CENTER);
        
        return section;
    }
    
    private void updateConnectionCard(boolean connected) {
        Component[] components = connectionCard.getComponents();
        if (components.length > 0 && components[0] instanceof JPanel) {
            JPanel content = (JPanel) components[0];
            Component[] contentComponents = content.getComponents();
            
            // Find and update the value label (usually in center)
            for (Component comp : contentComponents) {
                if (comp instanceof JPanel) {
                    JPanel valuePanel = (JPanel) comp;
                    Component[] valueComponents = valuePanel.getComponents();
                    for (Component valueComp : valueComponents) {
                        if (valueComp instanceof JLabel) {
                            JLabel valueLabel = (JLabel) valueComp;
                            if (valueLabel.getFont().getSize() >= 20) { // Headline font
                                valueLabel.setText(connected ? "Connected" : "Disconnected");
                                valueLabel.setForeground(connected ? 
                                    ModernUIComponents.Colors.SUCCESS : 
                                    ModernUIComponents.Colors.ERROR);
                                break;
                            }
                        }
                    }
                }
            }
        }
        connectionCard.repaint();
    }
    
    private void updateLatencyCard(long latencyMs) {
        Component[] components = latencyCard.getComponents();
        if (components.length > 0 && components[0] instanceof JPanel) {
            JPanel content = (JPanel) components[0];
            Component[] contentComponents = content.getComponents();
            
            for (Component comp : contentComponents) {
                if (comp instanceof JPanel) {
                    JPanel valuePanel = (JPanel) comp;
                    Component[] valueComponents = valuePanel.getComponents();
                    for (Component valueComp : valueComponents) {
                        if (valueComp instanceof JLabel) {
                            JLabel valueLabel = (JLabel) valueComp;
                            if (valueLabel.getFont().getSize() >= 20) { // Headline font
                                valueLabel.setText(String.valueOf(latencyMs));
                                Color latencyColor = latencyMs < 50 ? ModernUIComponents.Colors.SUCCESS :
                                                   latencyMs < 200 ? ModernUIComponents.Colors.WARNING :
                                                   ModernUIComponents.Colors.ERROR;
                                valueLabel.setForeground(latencyColor);
                                break;
                            }
                        }
                    }
                }
            }
        }
        latencyCard.repaint();
    }
    
    private void updateIPv6Card(boolean supported) {
        Component[] components = ipv6Card.getComponents();
        if (components.length > 0 && components[0] instanceof JPanel) {
            JPanel content = (JPanel) components[0];
            Component[] contentComponents = content.getComponents();
            
            for (Component comp : contentComponents) {
                if (comp instanceof JPanel) {
                    JPanel valuePanel = (JPanel) comp;
                    Component[] valueComponents = valuePanel.getComponents();
                    for (Component valueComp : valueComponents) {
                        if (valueComp instanceof JLabel) {
                            JLabel valueLabel = (JLabel) valueComp;
                            if (valueLabel.getFont().getSize() >= 20) { // Headline font
                                valueLabel.setText(supported ? "Supported" : "Not Supported");
                                valueLabel.setForeground(supported ? 
                                    ModernUIComponents.Colors.SUCCESS : 
                                    ModernUIComponents.Colors.WARNING);
                                break;
                            }
                        }
                    }
                }
            }
        }
        ipv6Card.repaint();
    }
    
    private void updateAPIServerCard(int serverIndex, boolean isReachable) {
        Component[] cards = apiServerCardsContainer.getComponents();
        
        // Find the actual card component (skip Box.createVerticalStrut components)
        int cardIndex = 0;
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] instanceof JPanel && !(cards[i] instanceof Box.Filler)) {
                if (cardIndex == serverIndex) {
                    JPanel serverCard = (JPanel) cards[i];
                    
                    // Update the status chip in the card
                    Component[] cardComponents = serverCard.getComponents();
                    for (Component comp : cardComponents) {
                        if (comp instanceof JPanel) {
                            JPanel content = (JPanel) comp;
                            Component[] contentComponents = content.getComponents();
                            
                            // Find the status chip (usually on the right)
                            for (Component contentComp : contentComponents) {
                                if (contentComp instanceof JPanel && 
                                    contentComp.getPreferredSize().width == 80) { // Status chip
                                    
                                    JPanel statusChip = (JPanel) contentComp;
                                    Component[] chipComponents = statusChip.getComponents();
                                    
                                    for (Component chipComp : chipComponents) {
                                        if (chipComp instanceof JLabel) {
                                            JLabel statusLabel = (JLabel) chipComp;
                                            statusLabel.setText(isReachable ? "Online" : "Offline");
                                            statusLabel.setForeground(isReachable ? 
                                                ModernUIComponents.Colors.SUCCESS : 
                                                ModernUIComponents.Colors.ERROR);
                                            break;
                                        }
                                    }
                                    statusChip.repaint();
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
                cardIndex++;
            }
        }
    }
    
    private void startNetworkMonitoring() {
        if (!isMonitoring) {
            isMonitoring = true;
            
            // Check network status every 30 seconds
            scheduler.scheduleAtFixedRate(this::performNetworkCheck, 0, 30, TimeUnit.SECONDS);
            
            // Check API server status every 5 seconds
            scheduler.scheduleAtFixedRate(this::checkAPIServers, 5, 5, TimeUnit.SECONDS);
            
            updateMonitoringButton();
        }
    }
    
    private void stopNetworkMonitoring() {
        isMonitoring = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            scheduler = Executors.newScheduledThreadPool(2);
        }
        updateMonitoringButton();
    }
    
    private void toggleNetworkMonitoring() {
        if (isMonitoring) {
            stopNetworkMonitoring();
        } else {
            startNetworkMonitoring();
        }
    }
    
    private void updateMonitoringButton() {
        SwingUtilities.invokeLater(() -> {
            if (monitoringToggleButton != null) {
                monitoringToggleButton.setText(isMonitoring ? "Stop Monitoring" : "Start Monitoring");
                // Note: Button color change would require recreating the button with different variant
                monitoringToggleButton.repaint();
            }
        });
    }
    
    public void refreshNetworkStatus() {
        performNetworkCheck();
        checkAPIServers();
    }
    
    private void performNetworkCheck() {
        // Check internet connection
        checkInternetConnection();
        
        // Check IPv6 support
        checkIPv6Support();
        
        SwingUtilities.invokeLater(() -> {
            updateConnectionCard(isConnected);
            updateLatencyCard(latency);
            updateIPv6Card(ipv6Supported);
        });
    }
    
    private void checkInternetConnection() {
        try {
            long startTime = System.currentTimeMillis();
            InetAddress address = InetAddress.getByName("8.8.8.8");
            boolean reachable = address.isReachable(5000);
            long endTime = System.currentTimeMillis();
            
            isConnected = reachable;
            latency = reachable ? (endTime - startTime) : 0;
        } catch (Exception e) {
            isConnected = false;
            latency = 0;
        }
    }
    
    private void checkIPv6Support() {
        try {
            InetAddress ipv6Address = InetAddress.getByName("2001:4860:4860::8888");
            ipv6Supported = ipv6Address.isReachable(3000);
        } catch (Exception e) {
            ipv6Supported = false;
        }
    }
    
    private void checkAPIServers() {
        String[] servers = {
            "api.openweathermap.org",
            "api.weatherapi.com",
            "dataservice.accuweather.com"
        };
        
        for (int i = 0; i < servers.length; i++) {
            final int serverIndex = i;
            CompletableFuture.runAsync(() -> {
                boolean isReachable = checkServerReachability(servers[serverIndex]);
                SwingUtilities.invokeLater(() -> updateAPIServerCard(serverIndex, isReachable));
            });
        }
    }
    
    private boolean checkServerReachability(String serverName) {
        try {
            InetAddress address = InetAddress.getByName(serverName);
            return address.isReachable(3000);
        } catch (Exception e) {
            return false;
        }
    }
    
    public void cleanup() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
    
    public boolean isNetworkConnected() {
        return isConnected;
    }
    
    public long getNetworkLatency() {
        return latency;
    }
    
    public boolean isIPv6Supported() {
        return ipv6Supported;
    }
} 