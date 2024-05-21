package advanced.algorithms.programming.tailoredsocialnetwork.domain;
import java.util.*;
import java.util.stream.Collectors;

public class PostRecommenderSystem {
    private Map<Integer, User> users = new HashMap<>();
    private Map<Integer, Set<Integer>> itemUserInteractions = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public void addInteraction(int userId, int itemId) {
        User user = users.get(userId);
        user.addInteraction(itemId);

        itemUserInteractions.computeIfAbsent(itemId, k -> new HashSet<>());
        itemUserInteractions.get(itemId).add(userId);
    }

    public List<Integer> recommendItems(int userId, int maxRecommendations) {
        User user = users.get(userId);
        Set<Integer> interactedItems = user.getInteractions();

        Map<Integer, Integer> itemScores = new HashMap<>();
        computeItemScores(user, interactedItems, itemScores);

        return itemScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(maxRecommendations)
                .collect(Collectors.toList());
    }

    private void computeItemScores(User user, Set<Integer> interactedItems, Map<Integer, Integer> itemScores) {
        for (int interest : user.getInterests()) {
            for (int itemId : itemUserInteractions.keySet()) {
                if (!interactedItems.contains(itemId)) {
                    int score = itemScores.getOrDefault(itemId, 0);
                    score += itemUserInteractions.get(itemId).stream()
                            .filter(uid -> users.get(uid).getInterests().contains(interest))
                            .count();
                    itemScores.put(itemId, score);
                }
            }
        }
    }
}

// Classe User existante
class User {
    private int id;
    private Set<Integer> interests;
    private Set<Integer> interactions;

    // Constructeur et getters/setters

    public int getId() {
        return id;
    }

    public Set<Integer> getInterests() {
        return interests;
    }

    public Set<Integer> getInteractions() {
        return interactions;
    }

    public void addInteraction(int item) {
        interactions.add(item);
    }
}

