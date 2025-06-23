import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class APISourceSelector extends JPanel {
    private List<APISourceInfo> apiSources;
    private JPanel sourceContainer;
    private JButton refreshButton;
    private APISourceSelectionListener listener;
    private ButtonGroup sourceGroup;
    private int selectedIndex = 0;
    
    public interface APISourceSelectionListener {
        void onAPISourceChanged(APISourceInfo selectedSource);
        void onAPISourceStatusChanged(APISourceInfo source);
    }
    
    public APISourceSelector() {
        initializeAPISources();
        setupMD3UI();
        setSelectedSourceAsActive();
        checkAllAPIStatus();
    }
    
    private void initializeAPISources() {
        apiSources = new ArrayList<>();
        apiSources.add(new APISourceInfo("openweather", "OpenWeatherMap (Unknown)", 
            "https://api.openweathermap.org/data/2.5/", 1));
        apiSources.add(new APISourceInfo("weatherapi", "WeatherAPI.com", 
            "https://api.weatherapi.com/v1/", 2));
        apiSources.add(new APISourceInfo("accuweather", "AccuWeather", 
            "https://dataservice.accuweather.com/", 3));
    }
    
    private void setupMD3UI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG
        ));
        
        // Main content card with MD3 elevation
        JPanel mainCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // MD3 elevated surface with shadow
                drawMD3Shadow(g2, getWidth(), getHeight(), 24);
                g2.setColor(ModernUIComponents.Colors.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                
                g2.dispose();
            }
            
            private void drawMD3Shadow(Graphics2D g2, int width, int height, int radius) {
                // Three-layer shadow system
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fillRoundRect(0, 2, width, height, radius, radius);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(0, 1, width, height, radius, radius);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(1, 3, width - 2, height - 1, radius, radius);
            }
        };
        
        mainCard.setLayout(new BorderLayout());
        mainCard.setOpaque(false);
        mainCard.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.XL, 
            ModernUIComponents.Spacing.XL, 
            ModernUIComponents.Spacing.XL, 
            ModernUIComponents.Spacing.XL
        ));
        
        // Header section
        JPanel headerSection = createHeaderSection();
        mainCard.add(headerSection, BorderLayout.NORTH);
        
        // API source selection section
        JPanel selectionSection = createSelectionSection();
        mainCard.add(selectionSection, BorderLayout.CENTER);
        
        // Status monitoring section
        JPanel statusSection = createStatusSection();
        mainCard.add(statusSection, BorderLayout.SOUTH);
        
        add(mainCard, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, ModernUIComponents.Spacing.XL, 0));
        
        // Title with MD3 typography
        JLabel titleLabel = ModernUIComponents.createModernLabel(
            "Current API Source:", 
            ModernUIComponents.Fonts.TITLE_MEDIUM, 
            ModernUIComponents.Colors.ON_SURFACE
        );
        header.add(titleLabel, BorderLayout.WEST);
        
        // Current selection dropdown with MD3 style
        String[] sourceNames = apiSources.stream()
            .map(APISourceInfo::getDisplayName)
            .toArray(String[]::new);
        
        JComboBox<String> sourceDropdown = ModernUIComponents.createMD3ComboBox(sourceNames);
        sourceDropdown.setPreferredSize(new Dimension(300, 48));
        sourceDropdown.addActionListener(e -> {
            int newIndex = sourceDropdown.getSelectedIndex();
            if (newIndex != selectedIndex) {
                updateSelection(newIndex);
            }
        });
        
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dropdownPanel.setOpaque(false);
        dropdownPanel.add(sourceDropdown);
        
        // Refresh button
        refreshButton = ModernUIComponents.createMD3Button(
            "Refresh Status", 
            ModernUIComponents.ComponentVariant.OUTLINED, 
            ModernUIComponents.Colors.PRIMARY
        );
        refreshButton.addActionListener(e -> refreshAllAPIStatus());
        dropdownPanel.add(Box.createHorizontalStrut(ModernUIComponents.Spacing.MD));
        dropdownPanel.add(refreshButton);
        
        header.add(dropdownPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createSelectionSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(0, 0, ModernUIComponents.Spacing.XL, 0));
        
        // Section title
        JLabel sectionTitle = ModernUIComponents.createModernLabel(
            "API Status Monitor", 
            ModernUIComponents.Fonts.TITLE_SMALL, 
            ModernUIComponents.Colors.ON_SURFACE
        );
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, ModernUIComponents.Spacing.LG, 0));
        section.add(sectionTitle, BorderLayout.NORTH);
        
        // API sources container
        sourceContainer = new JPanel();
        sourceContainer.setLayout(new BoxLayout(sourceContainer, BoxLayout.Y_AXIS));
        sourceContainer.setOpaque(false);
        
        sourceGroup = new ButtonGroup();
        
        // Create MD3 status cards for each API source
        for (int i = 0; i < apiSources.size(); i++) {
            APISourceInfo source = apiSources.get(i);
            JPanel sourceCard = createMD3SourceCard(source, i == selectedIndex, i);
            sourceContainer.add(sourceCard);
            
            if (i < apiSources.size() - 1) {
                sourceContainer.add(Box.createVerticalStrut(ModernUIComponents.Spacing.MD));
            }
        }
        
        section.add(sourceContainer, BorderLayout.CENTER);
        
        return section;
    }
    
    private JPanel createMD3SourceCard(APISourceInfo source, boolean isSelected, int index) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card background with selection state
                Color bgColor = isSelected ? 
                    ModernUIComponents.Colors.PRIMARY_CONTAINER : 
                    ModernUIComponents.Colors.SURFACE_VARIANT;
                    
                if (getMousePosition() != null) {
                    // Hover effect
                    bgColor = isSelected ? 
                        ModernUIComponents.Colors.PRIMARY_CONTAINER : 
                        ModernUIComponents.Colors.PRIMARY_8;
                }
                
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Border for outlined style
                g2.setColor(isSelected ? 
                    ModernUIComponents.Colors.PRIMARY : 
                    ModernUIComponents.Colors.OUTLINE_VARIANT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG, 
            ModernUIComponents.Spacing.LG
        ));
        card.setPreferredSize(new Dimension(0, 72)); // MD3 list item height
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Left section: Radio button and name
        JPanel leftSection = new JPanel(new BorderLayout());
        leftSection.setOpaque(false);
        
        // Custom radio button for MD3 style
        JRadioButton radioButton = new JRadioButton("", isSelected) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 20;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Outer circle
                g2.setColor(isSelected() ? 
                    ModernUIComponents.Colors.PRIMARY : 
                    ModernUIComponents.Colors.OUTLINE);
                g2.drawOval(x, y, size, size);
                
                // Inner dot when selected
                if (isSelected()) {
                    g2.setColor(ModernUIComponents.Colors.PRIMARY);
                    g2.fillOval(x + 5, y + 5, size - 10, size - 10);
                }
                
                g2.dispose();
            }
        };
        
        radioButton.setOpaque(false);
        radioButton.setFocusPainted(false);
        radioButton.addActionListener(e -> updateSelection(index));
        sourceGroup.add(radioButton);
        
        // Service name
        JLabel nameLabel = ModernUIComponents.createModernLabel(
            source.getDisplayName(), 
            ModernUIComponents.Fonts.BODY_LARGE, 
            isSelected ? ModernUIComponents.Colors.ON_PRIMARY_CONTAINER : ModernUIComponents.Colors.ON_SURFACE
        );
        
        leftSection.add(radioButton, BorderLayout.WEST);
        leftSection.add(Box.createHorizontalStrut(ModernUIComponents.Spacing.MD), BorderLayout.CENTER);
        leftSection.add(nameLabel, BorderLayout.EAST);
        
        // Right section: Status chip
        JPanel statusChip = createStatusChip(source);
        
        // Click handler for entire card
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                updateSelection(index);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.repaint();
            }
        });
        
        card.add(leftSection, BorderLayout.WEST);
        card.add(statusChip, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createStatusChip(APISourceInfo source) {
        String statusText = source.getStatus().getDisplayName();
        Color statusColor = getStatusColor(source.getStatus());
        
        return ModernUIComponents.createMD3StatusChip(statusText, statusColor);
    }
    
    private JPanel createStatusSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, ModernUIComponents.Spacing.MD, 0));
        buttonPanel.setOpaque(false);
        
        JButton checkAllButton = ModernUIComponents.createMD3Button(
            "Check All APIs", 
            ModernUIComponents.ComponentVariant.TONAL, 
            ModernUIComponents.Colors.PRIMARY
        );
        checkAllButton.addActionListener(e -> checkAllAPIStatus());
        
        buttonPanel.add(checkAllButton);
        
        section.add(buttonPanel, BorderLayout.CENTER);
        
        return section;
    }
    
    private void updateSelection(int newIndex) {
        if (newIndex >= 0 && newIndex < apiSources.size()) {
            selectedIndex = newIndex;
            setSelectedSourceAsActive();
            updateSourceCards();
            
            if (listener != null) {
                listener.onAPISourceChanged(apiSources.get(selectedIndex));
            }
        }
    }
    
    private void updateSourceCards() {
        // Refresh the source container
        sourceContainer.removeAll();
        sourceGroup = new ButtonGroup();
        
        for (int i = 0; i < apiSources.size(); i++) {
            APISourceInfo source = apiSources.get(i);
            JPanel sourceCard = createMD3SourceCard(source, i == selectedIndex, i);
            sourceContainer.add(sourceCard);
            
            if (i < apiSources.size() - 1) {
                sourceContainer.add(Box.createVerticalStrut(ModernUIComponents.Spacing.MD));
            }
        }
        
        sourceContainer.revalidate();
        sourceContainer.repaint();
    }
    
    private Color getStatusColor(APISourceInfo.APIStatus status) {
        switch (status) {
            case ONLINE: return ModernUIComponents.Colors.SUCCESS;
            case OFFLINE: return ModernUIComponents.Colors.ERROR;
            case ERROR: return ModernUIComponents.Colors.WARNING;
            default: return ModernUIComponents.Colors.OUTLINE;
        }
    }
    
    public void refreshAllAPIStatus() {
        CompletableFuture.runAsync(() -> {
            for (APISourceInfo source : apiSources) {
                checkAPIStatus(source);
            }
            
            SwingUtilities.invokeLater(this::updateSourceCards);
        });
    }
    
    private void checkAllAPIStatus() {
        CompletableFuture.runAsync(() -> {
            for (APISourceInfo source : apiSources) {
                checkAPIStatus(source);
                try {
                    Thread.sleep(200); // Avoid too frequent requests
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            SwingUtilities.invokeLater(this::updateSourceCards);
        });
    }
    
    private void checkAPIStatus(APISourceInfo source) {
        try {
            URL url = URI.create(source.getBaseUrl()).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 401) { // 401 might indicate API key needed but service is up
                source.setStatus(APISourceInfo.APIStatus.ONLINE);
            } else {
                source.setStatus(APISourceInfo.APIStatus.ERROR);
            }
            
            conn.disconnect();
        } catch (Exception e) {
            source.setStatus(APISourceInfo.APIStatus.OFFLINE);
        }
        
        if (listener != null) {
            listener.onAPISourceStatusChanged(source);
        }
    }
    
    private void setSelectedSourceAsActive() {
        // Clear all active states
        for (APISourceInfo source : apiSources) {
            source.setActive(false);
        }
        
        // Set selected source as active
        if (selectedIndex >= 0 && selectedIndex < apiSources.size()) {
            apiSources.get(selectedIndex).setActive(true);
        }
    }
    
    public APISourceInfo getSelectedAPISource() {
        return selectedIndex >= 0 && selectedIndex < apiSources.size() ? 
            apiSources.get(selectedIndex) : null;
    }
    
    public void setAPISourceSelectionListener(APISourceSelectionListener listener) {
        this.listener = listener;
    }
    
    public List<APISourceInfo> getAllAPISources() {
        return new ArrayList<>(apiSources);
    }
    
    public APISourceInfo getActiveAPISource() {
        for (APISourceInfo source : apiSources) {
            if (source.isActive()) {
                return source;
            }
        }
        return null;
    }
} 