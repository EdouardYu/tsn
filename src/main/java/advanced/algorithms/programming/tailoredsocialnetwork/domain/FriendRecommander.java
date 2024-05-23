package advanced.algorithms.programming.tailoredsocialnetwork.domain;
import java.util.*;
import java.util.stream.Collectors;
public class FriendRecommander {

        Set<User> friends;
        Set<Integer> likedPosts;
        Set<String> interests;

        public User(int id) {
            this.id = id;
            this.friends = new HashSet<>();
            this.likedPosts = new HashSet<>();
            this.interests = new HashSet<>();
        }

        public void addFriend(int friendId) {
            friends.add(friendId);
        }

        public void likesPost(int postId) {
            likedPosts.add(postId);
        }

        public void addInterest(String interest) {
            interests.add(interest);
        }


    class RecommenderSystem {
        private Map<Integer, User> users = new HashMap<>();
        private Map<Integer, Set<String>> postInterests = new HashMap<>();

        public void addUser(User user) {
            users.put(user.id, user);
        }

        public void addPostInterest(int postId, String interest) {
            postInterests.computeIfAbsent(postId, k -> new HashSet<>());
            postInterests.get(postId).add(interest);
        }

        public List<Integer> recommendFriends(int userId) {
            User user = users.get(userId);
            Set<Integer> existingFriends = user.friends;

            Map<Integer, Integer> scoredUsers = new HashMap<>();
            computeFriendScores(user, existingFriends, scoredUsers);

            return scoredUsers.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        private void computeFriendScores(User user, Set<Integer> existingFriends, Map<Integer, Integer> scoredUsers) {
            for (User otherUser : users.values()) {
                if (otherUser.id != user.id && !existingFriends.contains(otherUser.id)) {
                    int score = 0;

                    // Amis en commun
                    score += (countMutualFriends(user, otherUser))*5;

                    // Posts likés en commun
                    score += countCommonLikedPosts(user, otherUser);

                    // Intérêts en commun
                    score += (countCommonInterests(user, otherUser))*2;

                    scoredUsers.put(otherUser.id, score);
                }
            }
        }

        private int countMutualFriends(User user1, User user2) {
            Set<Integer> mutualFriends = new HashSet<>(user1.friends);
            mutualFriends.retainAll(user2.friends);
            return mutualFriends.size();
        }

        private int countCommonLikedPosts(User user1, User user2) {
            Set<Integer> commonLikedPosts = new HashSet<>(user1.likedPosts);
            commonLikedPosts.retainAll(user2.likedPosts);
            return commonLikedPosts.size();
        }

        private int countCommonInterests(User user1, User user2) {
            int score = 0;
            for (int postId : user1.likedPosts) {
                if (postInterests.containsKey(postId)) {
                    Set<String> postInterests = this.postInterests.get(postId);
                    for (String interest : postInterests) {
                        if (user2.interests.contains(interest)) {
                            score++;
                        }
                    }
                }
            }
            return score;
        }
    }
}