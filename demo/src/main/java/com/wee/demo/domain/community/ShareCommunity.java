package com.wee.demo.domain.community;

import com.wee.demo.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("Share")
@Table(name = "share_community")
public class ShareCommunity extends Community{

}
