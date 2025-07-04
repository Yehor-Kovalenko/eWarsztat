package org.pl.apigateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
//import jakarta.servlet.http.HttpServletRequest;
import org.pl.securityservice.LoginRequest;
import org.pl.securityservice.RegisterRequest;
import org.pl.securityservice.RegisterResponse;
import org.pl.securityservice.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserFlowsController {

    private final String SECURITY_SERVICE = "http://localhost:8085";

    @Autowired
    private EurekaClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/register-visit")
    public ResponseEntity<String> registerClientVisit(@RequestBody VisitRequest visitRequest) throws JsonProcessingException {
        /**
         * 1. Login (Create Client)
         * 2. Create Visit details
         * 3. Assign employee
         * 4. After the visit generate user invoice endpoint
         */
        //TODO currently logged in user
        // we will pass client email, using the client service we will find his id, and then vehicle i d(by its name) and then employee id by his name and combine that stuff together
        String staffUrl = discoveryClient.getNextServerFromEureka("STAFF-SERVICE", false).getHomePageUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> staffRequest = new HttpEntity<>(visitRequest.email(), headers);
        ResponseEntity<String> staffResponse = restTemplate.postForEntity(staffUrl + "/api/staff/email", staffRequest, String.class);
        if (staffResponse.getStatusCode().is2xxSuccessful() && staffResponse.getBody() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(staffResponse.getBody());

            Long staffId = root.path("id").asLong();

            String addVehicleUrl = staffUrl + "/api/staff/" + staffId + "/vehicles/" + visitRequest.vehicleId();
            ResponseEntity<String> vehicleResponse = restTemplate.getForEntity(addVehicleUrl, String.class);
            if (vehicleResponse.getStatusCode().is2xxSuccessful()) {
                System.out.println("Vehicle added successfully: " + vehicleResponse.getBody());
            } else {
                System.out.println("Failed to add vehicle: " + vehicleResponse.getStatusCode());
            }
            return vehicleResponse;
        } else {
            System.out.println("Staff member not found or error occurred.");
        }
        return (ResponseEntity<String>) ResponseEntity.status(400);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String url = SECURITY_SERVICE + "/auth/token";
        try {
            // Forward the JSON body as-is
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(loginRequest, headers);

            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    url, requestEntity, TokenResponse.class);

            // Return the token response directly to client
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            // propagate error status and body
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with SecurityService: " + ex.getMessage());
        }
    }

    /** User registration: proxy to SecurityService /auth/register */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        String url = SECURITY_SERVICE + "/auth/register";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RegisterRequest> requestEntity = new HttpEntity<>(registerRequest, headers);

            ResponseEntity<RegisterResponse> response = restTemplate.postForEntity(
                    url, requestEntity, RegisterResponse.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with SecurityService: " + ex.getMessage());
        }
    }
}


record VisitRequest (
    String vehicleId,
    String email,
    String details, // additional details user provides
    String employeeEmail
){}
