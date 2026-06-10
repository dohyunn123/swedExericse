import java.util.ArrayList;
import java.util.List;

// ==========================================
// 1. Observer Pattern Interfaces
// ==========================================

// Observer (Subscriber) Interface
interface Observer {
    void update(Subscription subscription, String message);
}

// Subject (Publisher) Interface
interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(Subscription subscription, String message);
}

// ==========================================
// 2. Domain & Utility Classes (Unchanged)
// ==========================================
class User {
    private String name;
    private String email;
    public User(String name, String email) { this.name = name; this.email = email; }
    public String getEmail() { return email; }
    public String getName() { return name; }
}

class Subscription {
    private String targetURL;
    private User user;
    public Subscription(String url, User user) { this.targetURL = url; this.user = user; }
    public String getTargetURL() { return targetURL; }
    public User getUser() { return user; }
}

interface NotificationChannel { void send(String msg, String dest); }

class EmailChannel implements NotificationChannel {
    @Override
    public void send(String message, String destination) {
        System.out.println("Email sent to " + destination + " : " + message);
    }
}

class Notification {
    private Subscription subscription;
    private String message;
    public Notification(Subscription sub, String msg) { this.subscription = sub; this.message = msg; }
    public void deliver(NotificationChannel channel) {
        channel.send(message, subscription.getUser().getEmail());
    }
}

// ==========================================
// 3. Concrete Subject (Refactored WebsiteMonitor)
// ==========================================
class WebsiteMonitor implements Subject {
    private List<Subscription> activeSubscriptions = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>(); // List of subscribers

    public void addSubscription(Subscription sub) {
        activeSubscriptions.add(sub);
    }

    // Register an observer
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    // Remove an observer
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    // Broadcast event to all registered observers
    @Override
    public void notifyObservers(Subscription subscription, String message) {
        for (Observer observer : observers) {
            observer.update(subscription, message);
        }
    }

    // Core Business Logic
    public void checkUpdates() {
        for (Subscription sub : activeSubscriptions) {
            boolean isUpdated = true; // Simulating a detected update
            
            if (isUpdated) {
                // The Monitor no longer creates Notifications directly.
                // It simply broadcasts that an update has occurred.
                String msg = "Update detected on: " + sub.getTargetURL();
                notifyObservers(sub, msg);
            }
        }
    }
}

// ==========================================
// 4. Concrete Observer (New NotificationManager)
// ==========================================
class NotificationManager implements Observer {
    private NotificationChannel defaultChannel;

    public NotificationManager(NotificationChannel channel) {
        this.defaultChannel = channel;
    }

    // Logic executed when an event is received from the Subject
    @Override
    public void update(Subscription subscription, String message) {
        // This class now holds the responsibility of generating and sending notifications
        Notification notification = new Notification(subscription, message);
        notification.deliver(defaultChannel);
    }
}

// ==========================================
// 5. Execution Example (Wiring it together)
// ==========================================
class Main {
    public static void main(String[] args) {
        // 1. Initialize core components
        WebsiteMonitor monitor = new WebsiteMonitor();
        NotificationChannel email = new EmailChannel();
        
        // 2. Initialize the observer with its required dependencies
        NotificationManager notiManager = new NotificationManager(email);
        
        // 3. Attach the observer to the subject
        monitor.attach(notiManager);
        
        // 4. Execute the monitor (It will notify the manager automatically)
        // Assume subscriptions have been added prior to this check
        monitor.checkUpdates();
    }
}
