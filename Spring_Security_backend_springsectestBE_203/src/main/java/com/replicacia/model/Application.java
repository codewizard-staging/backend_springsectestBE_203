package com.replicacia.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Application extends BaseEntity {
    private String appName;
    private AppUser appUser;
    private Role roles;
}
