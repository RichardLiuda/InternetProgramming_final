public class APISourceInfo {
    public enum APIStatus {
        ONLINE("Online", "green"),
        OFFLINE("Offline", "red"), 
        ERROR("Error", "orange"),
        UNKNOWN("Unknown", "gray");
        
        private final String displayName;
        private final String color;
        
        APIStatus(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
    
    private String name;
    private String displayName;
    private String baseUrl;
    private boolean isActive;
    private APIStatus status;
    private long lastChecked;
    private int priority;
    
    public APISourceInfo(String name, String displayName, String baseUrl, int priority) {
        this.name = name;
        this.displayName = displayName;
        this.baseUrl = baseUrl;
        this.priority = priority;
        this.isActive = false;
        this.status = APIStatus.UNKNOWN;
        this.lastChecked = 0;
    }
    
    // Getters
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public String getBaseUrl() { return baseUrl; }
    public boolean isActive() { return isActive; }
    public APIStatus getStatus() { return status; }
    public long getLastChecked() { return lastChecked; }
    public int getPriority() { return priority; }
    
    // Setters
    public void setActive(boolean active) { this.isActive = active; }
    public void setStatus(APIStatus status) { 
        this.status = status;
        this.lastChecked = System.currentTimeMillis();
    }
    public void setPriority(int priority) { this.priority = priority; }
    
    @Override
    public String toString() {
        return displayName + " (" + status.getDisplayName() + ")";
    }
} 