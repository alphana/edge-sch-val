package com.example.edgeschval.schemavalidation.domain;

import lombok.Data;

import java.util.Objects;

@Data
public class ServiceIdentity {
    private String serviceName;
    private String serviceVersion;
    private String serviceUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceIdentity that = (ServiceIdentity) o;
        return Objects.equals(serviceName, that.serviceName) && Objects.equals(serviceVersion, that.serviceVersion) && Objects.equals(serviceUrl, that.serviceUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, serviceVersion, serviceUrl);
    }
}
