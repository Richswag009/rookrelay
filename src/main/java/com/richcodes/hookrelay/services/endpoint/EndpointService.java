package com.richcodes.hookrelay.services.endpoint;

import com.richcodes.hookrelay.dto.endpoint.EndpointRegisterRequest;
import com.richcodes.hookrelay.dto.endpoint.StatusRequest;
import com.richcodes.hookrelay.enums.EndpointStatus;
import com.richcodes.hookrelay.repository.MerchantRepository;
import com.richcodes.hookrelay.response.EndpointResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface EndpointService {

    EndpointResponse createEndpoint(EndpointRegisterRequest endpointRegisterRequest );

    EndpointResponse getEndpoint(String id);

    List<EndpointResponse> getEndpoints();

    EndpointResponse updateEndpoint(String id, StatusRequest status);

    void deleteEndpoint(String id);
}
