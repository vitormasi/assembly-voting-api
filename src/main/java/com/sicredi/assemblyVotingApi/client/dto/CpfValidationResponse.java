package com.sicredi.assemblyVotingApi.client.dto;

import com.sicredi.assemblyVotingApi.client.enumeration.CpfStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CpfValidationResponse {

    private CpfStatusEnum status;

}
