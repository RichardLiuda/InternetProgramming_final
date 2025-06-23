import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernUIComponents {
    
    // Material Design 3 spacing system based on 4dp grid
    public static class Spacing {
        public static final int XS = 4;    // Extra small
        public static final int SM = 8;    // Small  
        public static final int MD = 16;   // Medium
        public static final int LG = 24;   // Large
        public static final int XL = 32;   // Extra large
        public static final int XXL = 48;  // Double extra large
    }
    
    // Material Design 3 color system
    public static class Colors {
        // Primary colors
        public static final Color PRIMARY = new Color(103, 80, 164);         // #6750A4
        public static final Color PRIMARY_CONTAINER = new Color(234, 221, 255); // #EADDFF
        public static final Color ON_PRIMARY = new Color(255, 255, 255);     // #FFFFFF
        public static final Color ON_PRIMARY_CONTAINER = new Color(33, 0, 93); // #21005D
        
        // Secondary colors  
        public static final Color SECONDARY = new Color(98, 91, 113);        // #625B71
        public static final Color SECONDARY_CONTAINER = new Color(230, 223, 244); // #E6DFFD
        public static final Color ON_SECONDARY = new Color(255, 255, 255);   // #FFFFFF
        public static final Color ON_SECONDARY_CONTAINER = new Color(29, 26, 34); // #1D1A22
        
        // Surface colors
        public static final Color SURFACE = new Color(252, 248, 253);        // #FCF8FD
        public static final Color SURFACE_VARIANT = new Color(229, 224, 233); // #E5E0E9
        public static final Color ON_SURFACE = new Color(28, 27, 31);        // #1C1B1F
        public static final Color ON_SURFACE_VARIANT = new Color(73, 69, 79); // #49454F
        
        // Background
        public static final Color BACKGROUND = new Color(252, 248, 253);     // #FCF8FD
        public static final Color ON_BACKGROUND = new Color(28, 27, 31);     // #1C1B1F
        
        // Outline
        public static final Color OUTLINE = new Color(121, 116, 126);        // #79747E
        public static final Color OUTLINE_VARIANT = new Color(201, 196, 206); // #C9C4CE
        
        // State layers
        public static final Color PRIMARY_8 = new Color(103, 80, 164, 20);   // 8% opacity
        public static final Color PRIMARY_12 = new Color(103, 80, 164, 31);  // 12% opacity
        public static final Color PRIMARY_16 = new Color(103, 80, 164, 41);  // 16% opacity
        
        // Legacy colors for compatibility
        public static final Color TEXT_PRIMARY = ON_SURFACE;
        public static final Color TEXT_SECONDARY = ON_SURFACE_VARIANT;
        public static final Color ACCENT = PRIMARY;
        public static final Color SUCCESS = new Color(76, 175, 80);
        public static final Color WARNING = new Color(255, 152, 0);
        public static final Color ERROR = new Color(244, 67, 54);
        public static final Color BORDER = OUTLINE_VARIANT;
        public static final Color PRIMARY_DARK = new Color(79, 55, 139);
        
        // Glass effect colors
        public static final Color SURFACE_GLASS = new Color(252, 248, 253, 180);
        public static final Color BORDER_GLASS = new Color(201, 196, 206, 60);
    }
    
    // Material Design 3 typography scale
    public static class Fonts {
        public static final Font DISPLAY_LARGE = new Font("Segoe UI", Font.BOLD, 57);
        public static final Font DISPLAY_MEDIUM = new Font("Segoe UI", Font.BOLD, 45);
        public static final Font DISPLAY_SMALL = new Font("Segoe UI", Font.BOLD, 36);
        
        public static final Font HEADLINE_LARGE = new Font("Segoe UI", Font.BOLD, 32);
        public static final Font HEADLINE_MEDIUM = new Font("Segoe UI", Font.BOLD, 28);
        public static final Font HEADLINE_SMALL = new Font("Segoe UI", Font.BOLD, 24);
        
        public static final Font TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 22);
        public static final Font TITLE_MEDIUM = new Font("Segoe UI", Font.BOLD, 16);
        public static final Font TITLE_SMALL = new Font("Segoe UI", Font.BOLD, 14);
        
        public static final Font LABEL_LARGE = new Font("Segoe UI", Font.PLAIN, 14);
        public static final Font LABEL_MEDIUM = new Font("Segoe UI", Font.PLAIN, 12);
        public static final Font LABEL_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
        
        public static final Font BODY_LARGE = new Font("Segoe UI", Font.PLAIN, 16);
        public static final Font BODY_MEDIUM = new Font("Segoe UI", Font.PLAIN, 14);
        public static final Font BODY_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
        
        // Legacy fonts for compatibility
        public static final Font TITLE = TITLE_LARGE;
        public static final Font SUBTITLE = TITLE_MEDIUM;
        public static final Font BODY = BODY_MEDIUM;
        public static final Font SMALL = BODY_SMALL;
    }
    
    // MD3 Component variants
    public enum ComponentVariant {
        FILLED, OUTLINED, ELEVATED, TONAL
    }
    
    // MD3 Selector Component (for API source selection)
    public static JPanel createMD3Selector(String title, String[] options, int selectedIndex) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        
        // Title label
        JLabel titleLabel = createModernLabel(title, Fonts.TITLE_MEDIUM, Colors.ON_SURFACE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, Spacing.MD, 0));
        container.add(titleLabel, BorderLayout.NORTH);
        
        // Options container
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);
        
        ButtonGroup group = new ButtonGroup();
        
        for (int i = 0; i < options.length; i++) {
            JRadioButton option = createMD3RadioButton(options[i], i == selectedIndex);
            group.add(option);
            optionsPanel.add(option);
            
            if (i < options.length - 1) {
                optionsPanel.add(Box.createVerticalStrut(Spacing.SM));
            }
        }
        
        container.add(optionsPanel, BorderLayout.CENTER);
        return container;
    }
    
    // MD3 Radio Button
    public static JRadioButton createMD3RadioButton(String text, boolean selected) {
        JRadioButton radio = new JRadioButton(text, selected) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background on hover/selected
                if (getModel().isSelected() || getModel().isRollover()) {
                    g2.setColor(getModel().isSelected() ? Colors.PRIMARY_12 : Colors.PRIMARY_8);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                }
                
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        radio.setFont(Fonts.BODY_LARGE);
        radio.setForeground(Colors.ON_SURFACE);
        radio.setOpaque(false);
        radio.setBorder(BorderFactory.createEmptyBorder(Spacing.MD, Spacing.LG, Spacing.MD, Spacing.LG));
        radio.setFocusPainted(false);
        
        return radio;
    }
    
    // MD3 Status Chip
    public static JPanel createMD3StatusChip(String status, Color statusColor) {
        JPanel chip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background with status color
                Color bgColor = new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 24);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Border
                g2.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 64));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                
                g2.dispose();
            }
        };
        
        chip.setLayout(new BorderLayout());
        chip.setOpaque(false);
        chip.setPreferredSize(new Dimension(80, 32));
        
        JLabel statusLabel = createModernLabel(status, Fonts.LABEL_MEDIUM, statusColor);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        chip.add(statusLabel, BorderLayout.CENTER);
        
        return chip;
    }
    
    // MD3 Info Card (Enhanced version)
    public static JPanel createMD3InfoCard(String title, String value, String unit, ComponentVariant variant) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background and elevation based on variant
                switch (variant) {
                    case FILLED:
                        g2.setColor(Colors.PRIMARY_CONTAINER);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                        break;
                    case OUTLINED:
                        g2.setColor(Colors.SURFACE);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                        g2.setColor(Colors.OUTLINE);
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                        break;
                    case ELEVATED:
                        // Multi-layer shadow for elevation
                        drawMD3Shadow(g2, getWidth(), getHeight(), 24);
                        g2.setColor(Colors.SURFACE);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                        break;
                    default:
                        g2.setColor(Colors.SURFACE_VARIANT);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                }
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(200, 120));
        card.setBorder(BorderFactory.createEmptyBorder(Spacing.LG, Spacing.LG, Spacing.LG, Spacing.LG));
        
        // Title
        JLabel titleLabel = createModernLabel(title, Fonts.TITLE_SMALL, 
            variant == ComponentVariant.FILLED ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, BorderLayout.NORTH);
        
        // Value with unit
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        valuePanel.setOpaque(false);
        
        JLabel valueLabel = createModernLabel(value, Fonts.HEADLINE_MEDIUM, 
            variant == ComponentVariant.FILLED ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE);
        JLabel unitLabel = createModernLabel(unit, Fonts.BODY_MEDIUM, 
            variant == ComponentVariant.FILLED ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);
        
        valuePanel.add(valueLabel);
        if (!unit.isEmpty()) {
            valuePanel.add(Box.createHorizontalStrut(Spacing.XS));
            valuePanel.add(unitLabel);
        }
        
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    // MD3 Status Card for API/Network monitoring
    public static JPanel createMD3StatusCard(String title, String serviceName, String status, Color statusColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Elevated surface
                drawMD3Shadow(g2, getWidth(), getHeight(), 20);
                g2.setColor(Colors.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(Spacing.LG, Spacing.LG, Spacing.LG, Spacing.LG));
        
        // Header with title
        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = createModernLabel(title, Fonts.TITLE_SMALL, Colors.ON_SURFACE);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, Spacing.MD, 0));
            card.add(titleLabel, BorderLayout.NORTH);
        }
        
        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Service name
        JLabel serviceLabel = createModernLabel(serviceName, Fonts.BODY_LARGE, Colors.ON_SURFACE);
        contentPanel.add(serviceLabel, BorderLayout.CENTER);
        
        // Status chip
        JPanel statusChip = createMD3StatusChip(status, statusColor);
        contentPanel.add(statusChip, BorderLayout.EAST);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    // MD3 Shadow drawing utility
    private static void drawMD3Shadow(Graphics2D g2, int width, int height, int radius) {
        // Three-layer shadow system for MD3 elevation
        
        // Shadow layer 1 (ambient)
        g2.setColor(new Color(0, 0, 0, 6));
        g2.fillRoundRect(0, 2, width, height, radius, radius);
        
        // Shadow layer 2 (key light)
        g2.setColor(new Color(0, 0, 0, 12));
        g2.fillRoundRect(0, 1, width, height, radius, radius);
        
        // Shadow layer 3 (directional)
        g2.setColor(new Color(0, 0, 0, 8));
        g2.fillRoundRect(1, 3, width - 2, height - 1, radius, radius);
    }
    
    // Enhanced button creation with MD3 variants
    public static JButton createMD3Button(String text, ComponentVariant variant, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background based on variant and state
                Color bgColor = Colors.SURFACE;
                Color textColor = Colors.ON_SURFACE;
                
                switch (variant) {
                    case FILLED:
                        bgColor = getModel().isPressed() ? 
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 230) : color;
                        textColor = Colors.ON_PRIMARY;
                        break;
                    case OUTLINED:
                        bgColor = getModel().isPressed() ? Colors.PRIMARY_12 : 
                                  getModel().isRollover() ? Colors.PRIMARY_8 : Colors.SURFACE;
                        textColor = color;
                        break;
                    case ELEVATED:
                        drawMD3Shadow(g2, getWidth(), getHeight(), 20);
                        bgColor = getModel().isPressed() ? Colors.PRIMARY_12 : 
                                  getModel().isRollover() ? Colors.PRIMARY_8 : Colors.SURFACE;
                        textColor = color;
                        break;
                    case TONAL:
                        Color containerColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 40);
                        bgColor = getModel().isPressed() ? 
                            new Color(containerColor.getRed(), containerColor.getGreen(), containerColor.getBlue(), 180) : 
                            containerColor;
                        textColor = color;
                        break;
                }
                
                // Draw background
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw outline for outlined variant
                if (variant == ComponentVariant.OUTLINED) {
                    g2.setColor(Colors.OUTLINE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                }
                
                // Draw text
                g2.setColor(textColor);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), textX, textY);
                
                g2.dispose();
            }
        };
        
        button.setFont(Fonts.LABEL_LARGE);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBorder(BorderFactory.createEmptyBorder(Spacing.SM, Spacing.LG, Spacing.SM, Spacing.LG));
        
        return button;
    }
    
    // MD3 Combo Box
    public static JComboBox<String> createMD3ComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2.setColor(Colors.SURFACE_VARIANT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Border
                g2.setColor(Colors.OUTLINE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        combo.setFont(Fonts.BODY_MEDIUM);
        combo.setOpaque(false);
        combo.setBorder(BorderFactory.createEmptyBorder(Spacing.MD, Spacing.LG, Spacing.MD, Spacing.LG));
        
        return combo;
    }
    
    // Legacy methods for backward compatibility
    public static JLabel createModernLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
    
    public static JLabel createModernLabel(String text) {
        return createModernLabel(text, Fonts.BODY_MEDIUM, Colors.TEXT_PRIMARY);
    }
    
    public static JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(Fonts.BODY_MEDIUM);
        field.setBackground(Colors.SURFACE_VARIANT);
        field.setForeground(Colors.ON_SURFACE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.OUTLINE, 1),
            BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
        ));
        return field;
    }
    
    public static JButton createModernButton(String text, Color color) {
        return createMD3Button(text, ComponentVariant.FILLED, color);
    }
    
    public static JPanel createModernPanel(String title) {
        return createGlassCard(title);
    }
    
    public static JPanel createInfoCard(String title, String value, String unit) {
        return createMD3InfoCard(title, value, unit, ComponentVariant.OUTLINED);
    }
    
    public static JLabel createStatusIndicator(String status, Color color) {
        JLabel indicator = new JLabel("● " + status);
        indicator.setFont(Fonts.LABEL_MEDIUM);
        indicator.setForeground(color);
        return indicator;
    }
    
    public static JPanel createGlassCard(String title) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Multi-layer shadow for glass effect
                drawMD3Shadow(g2, getWidth(), getHeight(), 24);
                
                // Glass background
                g2.setColor(Colors.SURFACE_GLASS);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                
                // Glass border
                g2.setColor(Colors.BORDER_GLASS);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(Spacing.LG, Spacing.LG, Spacing.LG, Spacing.LG));
        
        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = createModernLabel(title, Fonts.TITLE_MEDIUM, Colors.ON_SURFACE);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, Spacing.MD, 0));
            card.add(titleLabel, BorderLayout.NORTH);
        }
        
        return card;
    }
    
    public static JPanel createGlassPanel(String title) {
        return createGlassCard(title);
    }
    
    // Material Design 3 Navigation Rail Item
    public static JButton createMD3NavigationItem(String text, Icon icon, boolean isSelected) {
        JButton navItem = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Check for dynamic selection state
                Boolean dynamicSelected = (Boolean) getClientProperty("isSelected");
                boolean actualSelected = (dynamicSelected != null) ? dynamicSelected : isSelected;
                
                // Calculate indicator bounds (full width, 56dp height minimum)
                int indicatorWidth = getWidth() - (Spacing.MD * 2); // 16dp margin on each side
                int indicatorHeight = Math.max(getHeight(), 56);
                int indicatorX = Spacing.MD;
                int indicatorY = (getHeight() - indicatorHeight) / 2;
                
                // Draw selection indicator (full width container)
                if (actualSelected) {
                    g2.setColor(Colors.SECONDARY_CONTAINER);
                    g2.fillRoundRect(indicatorX, indicatorY, indicatorWidth, indicatorHeight, 28, 28);
                } else if (getModel().isRollover()) {
                    g2.setColor(Colors.PRIMARY_8);
                    g2.fillRoundRect(indicatorX, indicatorY, indicatorWidth, indicatorHeight, 28, 28);
                }
                
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        navItem.setFont(Fonts.LABEL_LARGE);
        navItem.setForeground(isSelected ? Colors.ON_SECONDARY_CONTAINER : Colors.ON_SURFACE_VARIANT);
        navItem.setOpaque(false);
        navItem.setBorderPainted(false);
        navItem.setContentAreaFilled(false);
        navItem.setFocusPainted(false);
        navItem.setHorizontalAlignment(SwingConstants.LEFT);
        navItem.setBorder(BorderFactory.createEmptyBorder(Spacing.MD, Spacing.LG, Spacing.MD, Spacing.LG));
        navItem.setPreferredSize(new Dimension(200, 56)); // MD3 minimum touch target
        
        return navItem;
    }
} 