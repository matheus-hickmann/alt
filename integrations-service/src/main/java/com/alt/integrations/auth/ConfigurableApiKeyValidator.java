package com.alt.integrations.auth;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ApiKeyValidator that validates against a configurable set of API keys.
 * Keys are provided via integration-specific properties and a comma-separated list of allowed keys.
 */
@ApplicationScoped
public class ConfigurableApiKeyValidator implements ApiKeyValidator {

    private final Set<String> validKeys;

    public ConfigurableApiKeyValidator(
            @ConfigProperty(name = "integrations.api-key.carrier", defaultValue = "carrier-api-key-secret") String carrierKey,
            @ConfigProperty(name = "integrations.api-key.processor", defaultValue = "processor-api-key-secret") String processorKey,
            @ConfigProperty(name = "integrations.api-key.additional", defaultValue = "") String additionalKeys) {
        this.validKeys = Stream.concat(
                        Stream.of(carrierKey, processorKey),
                        Stream.of(additionalKeys.split(",")).filter(k -> !k.isBlank())
                )
                .map(String::trim)
                .filter(k -> !k.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean isValid(String apiKey) {
        return apiKey != null && !apiKey.isBlank() && validKeys.contains(apiKey);
    }
}
