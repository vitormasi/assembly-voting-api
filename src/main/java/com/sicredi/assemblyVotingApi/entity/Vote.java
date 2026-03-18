package com.sicredi.assemblyVotingApi.entity;

import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Vote {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String cpf;

    private VoteEnum vote;

    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "agenda_id")
    private Agenda agenda;

}
