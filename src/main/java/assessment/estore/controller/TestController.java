package assessment.estore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("health")
public class TestController {

    @GetMapping("check")
    public ResponseEntity<?> check() {
        return ResponseEntity.ok("alive!");
    }

    @GetMapping("random-users")
    public ResponseEntity<?> randomUsers() {

        List<String> userIds = new ArrayList<>();
        userIds.add(UUID.randomUUID().toString());
        userIds.add(UUID.randomUUID().toString());
        userIds.add(UUID.randomUUID().toString());

        return ResponseEntity.ok(userIds);
    }
}
