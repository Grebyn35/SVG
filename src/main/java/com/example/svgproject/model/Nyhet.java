package com.example.svgproject.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class Nyhet implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String category;

    @Column(length=200000000,columnDefinition="LONGTEXT")
    private String content;

    private String coverImgSrc;
    private String published;
}
