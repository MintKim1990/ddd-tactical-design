package kitchenpos.products.infra;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class ProductProductPurgomalumClient implements kitchenpos.products.tobe.domain.ProductPurgomalumClient {
    private final RestTemplate restTemplate;

    public ProductProductPurgomalumClient(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public boolean containsProfanity(final String text) {
        final URI url = UriComponentsBuilder.fromUriString("https://www.purgomalum.com/service/containsprofanity")
            .queryParam("text", text)
            .build()
            .toUri();
        return Boolean.parseBoolean(restTemplate.getForObject(url, String.class));
    }
}