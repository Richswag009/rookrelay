package com.richcodes.hookrelay.controller;

import com.richcodes.hookrelay.dto.endpoint.EndpointRegisterRequest;
import com.richcodes.hookrelay.dto.endpoint.StatusRequest;
import com.richcodes.hookrelay.enums.EndpointStatus;
import com.richcodes.hookrelay.response.EndpointResponse;
import com.richcodes.hookrelay.services.endpoint.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/endpoints")
public class EndpointController {

    @Autowired
    private  EndpointService endpointService;

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

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public EndpointResponse getEndpoint(@PathVariable String id ) {
        return endpointService.getEndpoint(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public EndpointResponse updateEndpoint(@PathVariable String id,@RequestBody StatusRequest endpointStatus) {
        return endpointService.updateEndpoint(id,endpointStatus);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEndpoint(@PathVariable String id) {
        endpointService.deleteEndpoint(id);
    }

}
