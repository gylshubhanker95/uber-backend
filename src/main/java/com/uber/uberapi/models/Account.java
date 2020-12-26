package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account extends Auditable{
    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles=new ArrayList<>();
}
