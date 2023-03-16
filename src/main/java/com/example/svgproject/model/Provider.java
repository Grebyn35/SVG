package com.example.svgproject.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class Provider implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String county;
    private String email;
    private String website;

    @Column(length=200000000,columnDefinition="LONGTEXT")
    private String about;

    private String logoSrc;
    private String qualityTaleSrc;
    private String brochureSrc;
    private String extraInfoSrc;

    @Column(length=200000000,columnDefinition="LONGTEXT")
    private String remark;

    private boolean operation1;
    private boolean operation2;
    private boolean operation3;
}
