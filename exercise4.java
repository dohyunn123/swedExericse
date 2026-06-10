import java.util.ArrayList;
import java.util.List;

// 1. User Class (Information Expert)
class User {
    private String userId;
    private String name;
    private String email;

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
    
    public String getEmail() { return email; }
    public String getName() { return name; }
}

// 2. Subscription Class (Information Expert)
class Subscription {
    private String subscriptionId;
    private String targetURL;
    private User user;

    public Subscription(String id, String url, User user) {
        this.subscriptionId = id;
        this.targetURL = url;
        this.user = user;
    }
    
    public String getTargetURL() { return targetURL; }
    public User getUser() { return user; }
}

// 3. Controller (Controller Pattern)
class WebsiteMonitorController {
    private List<Subscription> subscriptions = new ArrayList<>();

    // Handles the system event for registering a new subscription
    public void handleRegisterSubscription(User user, String url) {
        Subscription sub = new Subscription("SUB" + System.currentTimeMillis(), url, user);
        subscriptions.add(sub);
        System.out.println("Subscription registered for " + user.getName() + " -> " + url);
    }
}

// 4. Notification Channel Interface & Implementation (Polymorphism)
interface NotificationChannel {
    void send(String message, String destination);
}

class EmailChannel implements NotificationChannel {
    @Override
    public void send(String message, String destination) {
        System.out.println("Sending Email to [" + destination + "] : " + message);
    }
}

// 5. Notification Class (Information Expert)
class Notification {
    private Subscription subscription;
    private String message;

    public Notification(Subscription subscription, String message) {
        this.subscription = subscription;
        this.message = message;
    }

    // Delivers the notification using the preferred channel
    public void deliver(NotificationChannel channel) {
        String destination = subscription.getUser().getEmail();
        channel.send(message, destination);
    }
}

// 6. WebsiteMonitor Class (Information Expert & Creator)
class WebsiteMonitor {
    private List<Subscription> activeSubscriptions = new ArrayList<>();

    public void addSubscription(Subscription sub) {
        activeSubscriptions.add(sub);
    }

    // Periodically checks for updates on subscribed websites
    public void checkUpdates() {
        for (Subscription sub : activeSubscriptions) {
            // Simulating that an update was detected on the website
            boolean isUpdated = true; 
            
            if (isUpdated) {
                // Creator Pattern: WebsiteMonitor creates the Notification
                String msg = "Update detected on: " + sub.getTargetURL();
                Notification notification = new Notification(sub, msg);
                
                // Using an email channel for delivery (could be injected)
                NotificationChannel channel = new EmailChannel();
                notification.deliver(channel);
            }
        }
    }
}
