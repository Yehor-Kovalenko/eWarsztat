package org.pl.apigateway;

import com.netflix.discovery.EurekaClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class UserFlowsController {
    @Autowired
    private EurekaClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/register-visit")
    public ResponseEntity<String> registerClientVisit(@RequestBody VisitRequest visitRequest) {
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
        String staffs = restTemplate.getForObject(staffUrl + "/api/staff", String.class);
        return ResponseEntity.ok(staffs);
    }
}


record VisitRequest (
    String vehicleId,
    String email,
    String details, // additional details user provides
    String employeeEmail
){}
