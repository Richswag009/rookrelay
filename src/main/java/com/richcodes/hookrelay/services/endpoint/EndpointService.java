package com.richcodes.hookrelay.services.endpoint;

import com.richcodes.hookrelay.dto.endpoint.EndpointRegisterRequest;
import com.richcodes.hookrelay.repository.MerchantRepository;
import com.richcodes.hookrelay.response.EndpointResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface EndpointService {

    EndpointResponse createEndpoint(EndpointRegisterRequest endpointRegisterRequest );
    EndpointResponse getEndpoint(String endpointId);
    List<EndpointResponse> getEndpoints();
}
