package com.wee.demo.domain.community;

import com.wee.demo.domain.enums.CrewStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@DiscriminatorValue("Crew")
@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "crew_community")
public class CrewCommunity extends Community{
    private String period;
    private String location;
    private String type;
    private int headCount;
    @Enumerated(EnumType.STRING)
    private CrewStatus crewStatus;
}
