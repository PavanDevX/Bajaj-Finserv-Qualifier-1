package com.example.bajaj;

import com.example.bajaj.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootApplication
public class BajajApplication implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(BajajApplication.class, args);
    }

    @Override
    public void run(String... args) {
        String registerUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";
        UserInfo userInfo = new UserInfo("John Doe", "REG12347", "john@example.com");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UserInfo> request = new HttpEntity<>(userInfo, headers);

            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(registerUrl, request, WebhookResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                WebhookResponse resp = response.getBody();

                List<List<Integer>> mutualFollows = findMutualFollows(resp.getData().getUsers());

                ResultQ1 result = new ResultQ1(userInfo.getRegNo(), mutualFollows);

                sendResultWithRetry(resp.getWebhook(), resp.getAccessToken(), result, 4);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<List<Integer>> findMutualFollows(List<User> users) {
        Map<Integer, Set<Integer>> followsMap = new HashMap<>();
        for (User user : users) {
            followsMap.put(user.getId(), new HashSet<>(user.getFollows()));
        }

        Set<String> seen = new HashSet<>();
        List<List<Integer>> result = new ArrayList<>();

        for (User user : users) {
            int id1 = user.getId();
            for (int id2 : user.getFollows()) {
                if (followsMap.containsKey(id2) && followsMap.get(id2).contains(id1)) {
                    int min = Math.min(id1, id2);
                    int max = Math.max(id1, id2);
                    String key = min + "-" + max;
                    if (!seen.contains(key)) {
                        result.add(Arrays.asList(min, max));
                        seen.add(key);
                    }
                }
            }
        }
        return result;
    }

    private void sendResultWithRetry(String url, String token, ResultQ1 result, int retries) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<ResultQ1> request = new HttpEntity<>(result, headers);

        while (retries-- > 0) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Successfully sent result");
                    return;
                }
            } catch (Exception e) {
                System.err.println("Retrying... " + retries + " left");
            }
        }

        System.err.println("Failed to send result after retries.");
    }
}