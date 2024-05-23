package advanced.algorithms.programming.tailoredsocialnetwork.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        PostRecommenderSystem rs = new PostRecommenderSystem();

        User user1 = new User(1, new HashSet<>(Arrays.asList(1, 2)), new HashSet<>(Arrays.asList(12)));
        rs.addUser(user1);

        User user2 = new User(2, new HashSet<>(Arrays.asList(2, 3)), new HashSet<>(Arrays.asList(12, 13)));
        rs.addUser(user2);

        rs.addInteraction(user1.getId(), 12);
        rs.addInteraction(user2.getId(), 12);
        rs.addInteraction(user2.getId(), 13);

        List<Integer> recommendations = rs.recommendPosts(user1.getId(), 5);
        System.out.println("Recommendations for user 1: " + recommendations);
    }
}
