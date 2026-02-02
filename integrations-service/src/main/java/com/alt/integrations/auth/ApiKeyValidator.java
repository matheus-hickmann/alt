package com.alt.integrations.auth;

/**
 * Validates API keys for webhook authentication.
 * Extensible: add new integrations by providing additional valid keys.
 */
public interface ApiKeyValidator {

    /**
     * Returns true if the given API key is valid for any known integration.
     */
    boolean isValid(String apiKey);
}
