package com.sicredi.assemblyVotingApi.config;

import com.sicredi.assemblyVotingApi.client.CpfValidationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {

    @Value("${cpf.validation.url}")
    private String cpfValidationUrl;

    @Bean
    public CpfValidationClient cpfValidationClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(cpfValidationUrl)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(CpfValidationClient.class);
    }
}
