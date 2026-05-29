package com.richcodes.hookrelay.services.endpoint;

import com.richcodes.hookrelay.dto.endpoint.EndpointRegisterRequest;
import com.richcodes.hookrelay.entities.Endpoint;
import com.richcodes.hookrelay.entities.Event;
import com.richcodes.hookrelay.entities.Merchant;
import com.richcodes.hookrelay.enums.EndpointStatus;
import com.richcodes.hookrelay.repository.EndpointRepository;
import com.richcodes.hookrelay.response.EndpointResponse;
import com.richcodes.hookrelay.response.MerchantRegisterResponse;
import com.richcodes.hookrelay.utils.merchant.FindAuthenticatedUser;
import com.richcodes.hookrelay.utils.secret.WebhookSecretGenerator;
import io.micrometer.observation.Observation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EndpointServiceImpl implements EndpointService {
    private final EndpointRepository endpointRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final WebhookSecretGenerator webhookSecretGenerator;

    public EndpointServiceImpl(EndpointRepository endpointRepository, FindAuthenticatedUser findAuthenticatedUser, WebhookSecretGenerator webhookSecretGenerator) {
        this.endpointRepository = endpointRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.webhookSecretGenerator = webhookSecretGenerator;
    }
    @Override
    public EndpointResponse createEndpoint(EndpointRegisterRequest request) {

        Merchant merchant = findAuthenticatedUser.findAuthenticatedUser();

        String secret = WebhookSecretGenerator.generateSecret();

        Endpoint endpoint = new Endpoint();
        endpoint.setUrl(request.url());
        endpoint.setDescription(request.description());
        endpoint.setSubscribedEvents(request.events());
        endpoint.setStatus(EndpointStatus.ACTIVE);
        endpoint.setSecretHash(secret);
        endpoint.setMerchant(merchant);

        Endpoint savedEndpoint = endpointRepository.save(endpoint);
        return convertMerchantResponse(savedEndpoint,secret);
    }

    @Override
    @Transactional
    public EndpointResponse getEndpoint(String endpointId) {
        return null;
    }

    @Override
    @Transactional
    public List<EndpointResponse> getEndpoints() {
        Merchant merchant = findAuthenticatedUser.findAuthenticatedUser();

        List<Endpoint> endpoints =  endpointRepository.findByMerchant(merchant);

        return endpoints.stream()
                .map( endpoint -> convertMerchantResponse(endpoint,""))
                .toList();
    }

    private EndpointResponse convertMerchantResponse(Endpoint endpoint,String secret) {

        return new EndpointResponse(
                endpoint.getId(),
                endpoint.getUrl(),
                endpoint.getDescription(),
                endpoint.getSubscribedEvents()
                        .stream()
                        .toList(),
                secret,
                endpoint.getStatus(),
                endpoint.getCreated_at()
        );
    }

}
