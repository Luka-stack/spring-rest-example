package com.nisshoku.springrestclientexamples.Service;

import com.nisshoku.api.domain.User;
import com.nisshoku.api.domain.UserData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ApiServiceImpl implements ApiService{

    private RestTemplate restTemplate;

    private final String API_URL;

    public ApiServiceImpl(RestTemplate restTemplate, @Value("${api.uri}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.API_URL = apiUrl;
    }

    @Override
    public List<User> getUsers(Integer limit) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(API_URL).queryParam("limit", limit);

        UserData userData = restTemplate.getForObject(uriBuilder.toUriString(), UserData.class);

        return userData.getData();
    }

    @Override
    public Flux<User> getUsers(Mono<Integer> limit) {

        return WebClient.create(API_URL)
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("limit", limit.block()).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(resp -> resp.bodyToMono(UserData.class))
                .flatMapIterable(UserData::getData);
    }
}
