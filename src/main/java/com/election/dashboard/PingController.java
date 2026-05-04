package com.election.dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PingController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "online");
        response.put("message", "Bharat Voter system solid hai!");
        return response;
    }
}
