package hu.kvcspt.ctreportingtoolbackend.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.annotation.PostConstruct;
import org.hl7.fhir.r5.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FhirClient {
    @Value("${fhir.server.url}")
    private String serverBaseUrl;
    private IGenericClient client;
    @PostConstruct
    public void init() {
        client = getFhirContextR5().newRestfulGenericClient(serverBaseUrl);
    }
    public <T extends Resource> T validateAndCreate(T resource) {
        if (getClient() == null) {
            throw new IllegalStateException("FhirClient is not initialized");
        }
        return (T) getClient().create().resource(resource).execute().getResource();
    }
    private IGenericClient getClient() {
        if (client == null) {
            throw new IllegalStateException("FhirClient is not initialized");
        }
        return client;
    }
    private FhirContext getFhirContextR5() {
        return FhirContext.forR5();
    }
}
