package advanced.algorithms.programming.tailoredsocialnetwork.domain;

import java.util.*;
import java.util.stream.Collectors;

public class PostRecommenderSystem{
    private Map<Integer, User> users = new HashMap<Integer, User>();
    private Map<Integer, Set<Integer>> postUserInteractions = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public void addInteraction(int userId, int postId) {
        User user = users.get(userId);
        user.addInteraction(postId);

        postUserInteractions.computeIfAbsent(postId, k -> new HashSet<>()).add(userId);
    }

    public List<Integer> recommendPosts(int userId, int maxRecommendations) {
        User user = users.get(userId);
        Set<Integer> interactedPosts = user.getInteractions();

        Map<Integer, Integer> postScores = new HashMap<>();
        computePostScores(user, interactedPosts, postScores);

        return postScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(maxRecommendations)
                .collect(Collectors.toList());
    }

    private void computePostScores(User user, Set<Integer> interactedPosts, Map<Integer, Integer> postScores) {
        for (int interest : user.getInterests()) {
            for (int postId : postUserInteractions.keySet()) {
                if (!interactedPosts.contains(postId)) {
                    int score = postScores.getOrDefault(postId, 0);
                    score += postUserInteractions.get(postId).stream()
                            .filter(uid -> users.get(uid).getInterests().contains(interest))
                            .count();
                    postScores.put(postId, score);
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
    public User(int id, Set<Integer> interests, Set<Integer> interactions) {
        this.id = id;
        this.interests = interests;
        this.interactions = interactions;
    }

    public int getId() {
        return id;
    }

    public Set<Integer> getInterests() {
        return interests;
    }

    public Set<Integer> getInteractions() {
        return interactions;
    }

    public void addInteraction(int post) {
        interactions.add(post);
    }
}

