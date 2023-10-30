package com.example.edgeschval.schemavalidation.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
public class SwaggerGroup implements Serializable {

    String configUrl;
    String oauth2RedirectUrl;
    List<Url> urls;
    String validatorUrl;
}
