package advanced.algorithms.programming.tailoredsocialnetwork.domain;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        PostRecommenderSystem rs = new PostRecommenderSystem();

        User user1 = new User(/* initialiser les attributs */);
        rs.addUser(user1);

        User user2 = new User(/* initialiser les attributs */);
        rs.addUser(user2);

        rs.addInteraction(user1.getId(), 12);
        rs.addInteraction(user2.getId(), 12);
        rs.addInteraction(user2.getId(), 13);

        List<Integer> recommendations = rs.recommendItems(user1.getId(), 5);
        System.out.println("Recommendations for user 1: " + recommendations);
    }
}
