package at.downdrown.somfy.config;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Required properties to interact with Somfy's Open API.
 *
 * @author Manfred Huber
 */
@Builder
@RequiredArgsConstructor
@Getter
public class SomfyProperties {

    /**
     * The base url for Somfy's Open API endpoints.
     */
    @Builder.Default
    private final String apiUrl = "https://api.somfy.com/api/v1";

    /**
     * The base url for Somfy's authentication endpoints.
     */
    @Builder.Default
    private final String authUrl = "https://accounts.somfy.com/oauth/oauth/v2";

    /**
     * The callback url you configured within your 'My Apps' section in Somfy's developer portal.
     */
    private final String callbackUrl;

    /**
     * The consumer key you configured within your 'My Apps' section in Somfy's developer portal.
     */
    private final String consumerKey;

    /**
     * The consumer secret you configured within your 'My Apps' section in Somfy's developer portal.
     */
    private final String consumerSecret;

}
