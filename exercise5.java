import java.util.ArrayList;
import java.util.List;

// ==========================================
// 1. Strategy Pattern Interface & Implementations
// ==========================================

// The Strategy Interface
interface ComparisonStrategy {
    // Returns true if the content is considered "updated" based on the specific strategy
    boolean isUpdated(String oldContent, String newContent);
}

// Strategy 1: Identical content size
class SizeComparisonStrategy implements ComparisonStrategy {
    @Override
    public boolean isUpdated(String oldContent, String newContent) {
        if (oldContent == null || newContent == null) return true;
        // Updated if the lengths (sizes) of the content differ
        return oldContent.length() != newContent.length();
    }
}

// Strategy 2: Identical HTML content
class HtmlComparisonStrategy implements ComparisonStrategy {
    @Override
    public boolean isUpdated(String oldContent, String newContent) {
        if (oldContent == null || newContent == null) return true;
        // Updated if the raw HTML strings are not exactly identical
        return !oldContent.equals(newContent);
    }
}

// Strategy 3: Identical text content
class TextComparisonStrategy implements ComparisonStrategy {
    @Override
    public boolean isUpdated(String oldContent, String newContent) {
        if (oldContent == null || newContent == null) return true;
        // Basic simulation of stripping HTML tags to compare pure text
        String oldText = oldContent.replaceAll("<[^>]*>", "").trim();
        String newText = newContent.replaceAll("<[^>]*>", "").trim();
        // Updated if the extracted text differs
        return !oldText.equals(newText);
    }
}

// ==========================================
// 2. Updated Domain Classes
// ==========================================

class User { /* ... Omitted for brevity (same as Ex 5) ... */ }

// Subscription now owns a ComparisonStrategy and stores the last known content
class Subscription {
    private String targetURL;
    private User user;
    private String lastKnownContent;
    private ComparisonStrategy comparisonStrategy; // Strategy reference

    public Subscription(String url, User user, ComparisonStrategy strategy) {
        this.targetURL = url;
        this.user = user;
        this.comparisonStrategy = strategy;
        this.lastKnownContent = ""; // Initial empty state
    }
    
    public String getTargetURL() { return targetURL; }
    public User getUser() { return user; }
    public ComparisonStrategy getComparisonStrategy() { return comparisonStrategy; }
    public String getLastKnownContent() { return lastKnownContent; }
    public void setLastKnownContent(String content) { this.lastKnownContent = content; }
}

// ==========================================
// 3. Subject and Observer Interfaces (From Ex 5)
// ==========================================
interface Observer { void update(Subscription subscription, String message); }
interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(Subscription subscription, String message);
}

// ==========================================
// 4. Refactored WebsiteMonitor (Context for Strategy)
// ==========================================
class WebsiteMonitor implements Subject {
    private List<Subscription> activeSubscriptions = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();

    public void addSubscription(Subscription sub) { activeSubscriptions.add(sub); }
    @Override public void attach(Observer observer) { observers.add(observer); }
    @Override public void detach(Observer observer) { observers.remove(observer); }
    @Override public void notifyObservers(Subscription sub, String msg) {
        for (Observer observer : observers) observer.update(sub, msg);
    }

    // The method that utilizes the injected Strategy
    public void checkUpdates() {
        for (Subscription sub : activeSubscriptions) {
            // Simulating a network call to fetch current website content
            String fetchedContent = simulateWebFetch(sub.getTargetURL()); 
            
            // DELEGATION: The monitor asks the Strategy if an update occurred
            ComparisonStrategy strategy = sub.getComparisonStrategy();
            boolean isUpdated = strategy.isUpdated(sub.getLastKnownContent(), fetchedContent);
            
            if (isUpdated) {
                sub.setLastKnownContent(fetchedContent); // Update state
                String strategyName = strategy.getClass().getSimpleName();
                notifyObservers(sub, "Update detected on " + sub.getTargetURL() + " [Strategy: " + strategyName + "]");
            }
        }
    }

    private String simulateWebFetch(String url) {
        // Dummy method simulating fetching web data
        return "<html><body>New Content fetched at " + System.currentTimeMillis() + "</body></html>";
    }
}
