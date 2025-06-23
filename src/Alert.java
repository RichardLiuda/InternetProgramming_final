public class Alert {
    private String alertType;
    private String alertMessage;

    public Alert(String alertType, String alertMessage) {
        this.alertType = alertType;
        this.alertMessage = alertMessage;
    }

    public void sendAlert() {
        System.out.println("[" + alertType + "]: " + alertMessage);
    }
}
