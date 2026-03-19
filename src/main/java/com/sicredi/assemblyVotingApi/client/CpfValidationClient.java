package com.sicredi.assemblyVotingApi.client;

import com.sicredi.assemblyVotingApi.client.dto.CpfValidationResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

// client/CpfValidationClient.java
@HttpExchange
public interface CpfValidationClient {

    @GetExchange("/users/{cpf}")
    CpfValidationResponse validateCpf(@PathVariable String cpf);
}
