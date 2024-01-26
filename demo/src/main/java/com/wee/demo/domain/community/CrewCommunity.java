package com.wee.demo.domain.community;

import com.wee.demo.domain.enums.CrewStatus;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@DiscriminatorValue("Crew")
@Table(name = "crew_community")
public class CrewCommunity extends Community{
    private Date startDate;
    private String location;
    private String type;
    private int headCount;
    @Enumerated(EnumType.STRING)
    private CrewStatus crewStatus;
}
