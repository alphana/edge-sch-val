package com.example.edgeschval.schemavalidation.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class SwaggerGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    String location;
    String name;
    String swaggerVersion;
    String url;
}
