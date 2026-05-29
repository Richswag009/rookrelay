package com.richcodes.hookrelay.controller;

import com.richcodes.hookrelay.dto.endpoint.EndpointRegisterRequest;
import com.richcodes.hookrelay.response.EndpointResponse;
import com.richcodes.hookrelay.services.endpoint.EndpointService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/endpoints")

public class EndpointController {

    private  final EndpointService endpointService;

    public EndpointController(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @PostMapping("")
    public EndpointResponse createEndpoint(
            @RequestBody
            EndpointRegisterRequest endpointRegisterRequest) {

        return endpointService.createEndpoint(endpointRegisterRequest);
    }

    @GetMapping("")
    public List<EndpointResponse> getEndpoints() {
        return endpointService.getEndpoints();
    }
}
