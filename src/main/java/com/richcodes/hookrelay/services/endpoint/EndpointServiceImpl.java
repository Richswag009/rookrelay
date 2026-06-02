package com.richcodes.hookrelay.services.endpoint;

import com.richcodes.hookrelay.domain.Event;
import com.richcodes.hookrelay.dto.endpoint.EndpointRegisterRequest;
import com.richcodes.hookrelay.domain.Endpoint;
import com.richcodes.hookrelay.domain.Merchant;
import com.richcodes.hookrelay.dto.endpoint.StatusRequest;
import com.richcodes.hookrelay.enums.EndpointStatus;
import com.richcodes.hookrelay.repository.EndpointRepository;
import com.richcodes.hookrelay.response.EndpointResponse;
import com.richcodes.hookrelay.utils.merchant.FindAuthenticatedUser;
import com.richcodes.hookrelay.utils.secret.WebhookSecretGenerator;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
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

       Endpoint endpoint =  findMerchantEndpointById(endpointId);

       return convertMerchantResponse(endpoint,"");
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

    @Override
    public EndpointResponse updateEndpoint(String id, StatusRequest endpointStatus) {
        Endpoint endpoint =  findMerchantEndpointById(id);

        if (endpointStatus.status() == null) {
            throw new IllegalArgumentException("status is required");
        }

        EndpointStatus status = EndpointStatus.valueOf(
                String.valueOf(endpointStatus.status())
        );

        endpoint.setStatus(status);
        Endpoint updateTodo =  endpointRepository.save(endpoint);

        return convertMerchantResponse(updateTodo,"");
    }

    @Override
    @Transactional
    public void deleteEndpoint(String id) {
        System.out.println("Delete endpoint");
        Endpoint endpoint =  findMerchantEndpointById(id);
        if (EndpointStatus.ACTIVE.equals(endpoint.getStatus())) {
            throw new IllegalArgumentException("can't delete an active endpoint");
        }
        endpointRepository.delete(endpoint);
        System.out.println("Deleted endpoint");
    }

    private EndpointResponse convertMerchantResponse(Endpoint endpoint,String secret) {

        return new EndpointResponse(
                endpoint.getId(),
                endpoint.getUrl(),
                endpoint.getDescription(),
                endpoint.getSubscribedEvents()
                        .stream()// IMPORTANT FIX
                        .toList(),
                secret,
                endpoint.getStatus(),
                endpoint.getCreated_at()
        );
    }

    private Endpoint findMerchantEndpointById(String id) {

        Merchant merchant = findAuthenticatedUser.findAuthenticatedUser();
        Optional<Endpoint> endpoint= endpointRepository.findByIdAndMerchant(merchant,id);

        if(endpoint.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Endpoint not found");
        }
        return endpoint.get();


    }

}
