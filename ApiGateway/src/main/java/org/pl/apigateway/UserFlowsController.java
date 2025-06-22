package org.pl.apigateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserFlowsController {
    @Autowired
    private EurekaClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/register-visit")
    public ResponseEntity<String> registerClientVisit(@RequestBody VisitRequest visitRequest) throws JsonProcessingException {
        /**
         * 1. Login (Create Client TODO)
         * 2. Create Visit details
         * 3. Assign employee
         * 4. After the visit generate user invoice endpoint
         */
        // we will pass client email, using the client service we will find his id, and then vehicle i d(by its name) and then employee id by his name and combine that stuff together
//        String clientUrl = discoveryClient.getNextServerFromEureka("CLIENT-SERVICE", false).getHomePageUrl();
        String staffUrl = discoveryClient.getNextServerFromEureka("STAFF-SERVICE", false).getHomePageUrl();
//        String apiGatewayUrl = discoveryClient.getNextServerFromEureka("API-GATEWAY", false).getHomePageUrl();
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
    //TODO create invoice (only for pracownik)
    // generate invoice (by client)
}


record VisitRequest (
    String vehicleId,
    String email,
    String details, // additional details user provides
    String employeeEmail
){}
