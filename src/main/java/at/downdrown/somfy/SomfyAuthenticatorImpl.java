package at.downdrown.somfy;

import at.downdrown.somfy.config.SomfyProperties;
import at.downdrown.somfy.exception.SomfyTokenFetchException;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.time.LocalDateTime.now;

@Slf4j(topic = "somfyclient")
@SuppressWarnings("StringBufferReplaceableByString")
final class SomfyAuthenticatorImpl implements SomfyAuthenticator {

    private final String state = UUID.randomUUID().toString();

    private final SomfyProperties somfyProperties;
    private final HttpClient httpClient;

    SomfyAuthenticatorImpl(SomfyProperties properties, HttpClient httpClient) {
        this.somfyProperties = properties;
        this.httpClient = httpClient;
    }

    /**
     * Builds the {@link URI} that is used to request an authorization code from Somfy.
     *
     * @return the generated {@link URI}.
     */
    @Override
    public URI buildAuthenticationRequestUri() {

        StringBuilder uriBuilder = new StringBuilder(somfyProperties.getAuthUrl());
        uriBuilder.append("/auth");
        uriBuilder.append("?response_type=code");
        uriBuilder.append("&client_id=");
        uriBuilder.append(somfyProperties.getConsumerKey());
        uriBuilder.append("&redirect_uri=");
        uriBuilder.append(somfyProperties.getCallbackUrl());
        uriBuilder.append("&state=");
        uriBuilder.append(state);
        uriBuilder.append("&grant_type=authorization_code");

        final URI uri = URI.create(uriBuilder.toString());

        log.debug("Built authentication request URI : {}", uri);

        return uri;
    }

    /**
     * Builds the {@link URI} that is used to fetch a token from Somfy.
     *
     * @return the generated {@link URI}.
     */
    private URI buildTokenRequestUri(String authorizationCode) {

        Objects.requireNonNull(authorizationCode, "No authorizationCode present.");

        StringBuilder uriBuilder = new StringBuilder(somfyProperties.getAuthUrl());
        uriBuilder.append("/token");
        uriBuilder.append("?response_type=code");
        uriBuilder.append("&client_id=");
        uriBuilder.append(somfyProperties.getConsumerKey());
        uriBuilder.append("&client_secret=");
        uriBuilder.append(somfyProperties.getConsumerSecret());
        uriBuilder.append("&redirect_uri=");
        uriBuilder.append(somfyProperties.getCallbackUrl());
        uriBuilder.append("&code=");
        uriBuilder.append(authorizationCode);
        uriBuilder.append("&state=");
        uriBuilder.append(state);
        uriBuilder.append("&grant_type=authorization_code");

        final URI uri = URI.create(uriBuilder.toString());

        log.debug("Built token request URI : {}", uri);

        return uri;
    }

    /**
     * Builds the {@link URI} that is used to refresh a token from Somfy.
     *
     * @return the generated {@link URI}.
     */
    private URI buildTokenRefreshUri(String refreshToken) {

        Objects.requireNonNull(refreshToken, "No refreshToken present.");

        StringBuilder uriBuilder = new StringBuilder(somfyProperties.getAuthUrl());
        uriBuilder.append("/token");
        uriBuilder.append("?client_id=");
        uriBuilder.append(somfyProperties.getConsumerKey());
        uriBuilder.append("&client_secret=");
        uriBuilder.append(somfyProperties.getConsumerSecret());
        uriBuilder.append("&refresh_token=");
        uriBuilder.append(refreshToken);
        uriBuilder.append("&grant_type=refresh_token");

        final URI uri = URI.create(uriBuilder.toString());

        log.debug("Built token refresh URI : {}", uri);

        return uri;
    }

    @Override
    public String extractAuthorizationCodeFromRedirectUri(URI redirectUri) {
        final Map<String, List<String>> queryParams = UriUtils.getQueryParams(redirectUri);
        if (queryParams.containsKey("code")) {
            return queryParams.get("code").get(0);
        }
        throw new IllegalArgumentException("The redirect URI does not contain a 'code' query parameter.");
    }

    @Override
    public SomfyToken issueToken(String authorizationCode) throws SomfyTokenFetchException {
        return obtainToken(buildTokenRequestUri(authorizationCode)).toSomfyToken();
    }

    @Override
    public SomfyToken refreshToken(SomfyToken token) throws SomfyTokenFetchException {
        if (token.isAccessTokenExpired()) {
            return obtainToken(buildTokenRefreshUri(token.getRefreshToken())).toSomfyToken();
        }
        return token;
    }

    private AccessTokenResponse obtainToken(final URI uri) throws SomfyTokenFetchException {
        try {
            final HttpRequest tokenRequest = HttpRequest.newBuilder(uri).GET().build();
            final HttpResponse<String> tokenResponse = httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (tokenResponse.statusCode() == HTTP_OK) {
                AccessTokenResponse accessTokenResponse = Json.fromJson(tokenResponse.body(), AccessTokenResponse.class);
                log.debug("Successfully obtained access token: {}", accessTokenResponse);
                return accessTokenResponse;
            } else {
                throw new SomfyTokenFetchException("Could not fetch a token from URI=" + uri);
            }
        } catch (IOException | InterruptedException e) {
            throw new SomfyTokenFetchException("Could not fetch a token from URI=" + uri, e);
        }
    }

    @NoArgsConstructor
    @Data
    @ToString
    private static final class AccessTokenResponse {

        private final LocalDateTime issuedAt = now();

        @SerializedName("access_token")
        private String accessToken;

        @SerializedName("refresh_token")
        private String refreshToken;

        @SerializedName("expires_in")
        private Long expiresIn;

        @SerializedName("token_type")
        private String tokenType;

        @SerializedName("scope")
        private String scope;

        public SomfyToken toSomfyToken() {
            return new SomfyToken(
                getIssuedAt(),
                getAccessToken(),
                getRefreshToken(),
                getIssuedAt().plusSeconds(getExpiresIn()));
        }
    }
}
