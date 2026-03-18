package com.sicredi.assemblyVotingApi.entity;

import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Vote {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private String cpf;

    private VoteEnum vote;

    @ManyToOne
    @JoinColumn(name = "agenda_id")
    private Agenda agenda;

}
