package com.example.edgeschval.schemavalidation.domain;

import lombok.Data;

import java.io.Serializable;


@Data
public class Url implements Serializable {
    private String name;
    private String url;
}
