package advanced.algorithms.programming.tailoredsocialnetwork.database;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Gender;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Nationality;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Role;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.enumeration.Visibility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class UserGenerator {

    private static final String API_URL = "https://randomuser.me/api/?results=1000&inc=gender,name,email,login,dob,cell,picture";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {
        List<User> users = fetchUsersFromAPI();
        writeUsersToCSV(users);
    }

    private static List<User> fetchUsersFromAPI() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response.body());

        List<User> users = new ArrayList<>();
        for (JsonNode userNode : root.get("results")) {
            User user = new User();
            user.setEmail(userNode.get("email").asText());
            user.setPassword(userNode.get("login").get("password").asText());
            user.setFirstname(userNode.get("name").get("first").asText());
            user.setLastname(userNode.get("name").get("last").asText());
            user.setUsername(userNode.get("login").get("username").asText());
            user.setBirthday(LocalDate.parse(userNode.get("dob").get("date").asText().substring(0, 10), DATE_FORMATTER));
            user.setGender(userNode.get("gender").asText().equals("male") ? Gender.MALE : Gender.FEMALE);

            user.setNationality(randomNationality());

            user.setPicture(userNode.get("picture").get("large").asText());
            user.setBio("");
            user.setVisibility(randomVisibility());
            user.setCreatedAt(Instant.now());
            user.setEnabled(false);
            user.setRole(randomRole());

            users.add(user);
        }
        return users;
    }

    private static Nationality randomNationality() {
        Nationality[] nationalities = Nationality.values();
        return nationalities[RANDOM.nextInt(nationalities.length)];
    }

    private static Visibility randomVisibility() {
        Visibility[] visibilities = Visibility.values();
        return visibilities[RANDOM.nextInt(visibilities.length)];
    }

    private static Role randomRole() {
        // Assigning probabilities for each role
        double adminProbability = 0.01; // Very low probability
        double certifiedUserProbability = 0.1; // Low probability
        double normalUserProbability = 1 - adminProbability - certifiedUserProbability; // High probability

        double randomValue = RANDOM.nextDouble();

        if (randomValue < adminProbability) {
            return Role.ADMINISTRATOR;
        } else if (randomValue < adminProbability + certifiedUserProbability) {
            return Role.CERTIFIED_USER;
        } else {
            return Role.USER;
        }
    }

    private static void writeUsersToCSV(List<User> users) throws IOException {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter("src/main/resources/pop_db/users.csv"), CSVFormat.DEFAULT
                .withHeader("email", "password", "firstname", "lastname", "username", "birthday", "gender", "nationality", "picture", "bio", "visibility", "created_at", "is_enabled", "role"))) {
            for (User user : users) {
                printer.printRecord(user.getEmail(), user.getPassword(), user.getFirstname(), user.getLastname(), user.getUsername(),
                        user.getBirthday().format(DATE_FORMATTER), user.getGender(), user.getNationality(), user.getPicture(),
                        user.getBio(), user.getVisibility(), user.getCreatedAt(), user.isEnabled(), user.getRole());
            }
        }
    }
}
