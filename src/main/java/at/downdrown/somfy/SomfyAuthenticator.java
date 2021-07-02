package at.downdrown.somfy;

import at.downdrown.somfy.config.SomfyProperties;
import at.downdrown.somfy.exception.SomfyTokenExpiredException;
import at.downdrown.somfy.exception.SomfyTokenFetchException;

import java.net.URI;
import java.net.http.HttpClient;

/**
 * Provides all required functionalities to authenticate a user, fetch & refresh tokens.
 *
 * @author Manfred Huber
 */
public interface SomfyAuthenticator {

    static SomfyAuthenticator newAuthenticator(SomfyProperties properties) {
        return newAuthenticator(properties, HttpClient.newHttpClient());
    }

    static SomfyAuthenticator newAuthenticator(SomfyProperties properties, HttpClient httpClient) {
        return new SomfyAuthenticatorImpl(properties, httpClient);
    }

    /**
     * Creates the URL that must be used to authenticate a user.
     * You either need to interact with the user's User-Agent or
     * provide a Servlet that is reachable at the url you configured
     * as redirect url in Somfy's developer portal in order to be able
     * to use {@link #extractAuthorizationCodeFromRedirectUri(URI)} to
     * extract the {@code authorizationCode} from the redirect url.
     *
     * @return the {@link URI} that must be used to authenticate the user.
     */
    URI buildAuthenticationRequestUri();

    /**
     * Once you have successfully authenticated & granted access, Somfy will redirect you
     * to the redirect url that you configured in Somfy's developer portal.
     * The {@code redirectUri} will also contain the {@code authorizationCode} as URL-Parameter.
     * This method extracts the required {@code authorizationCode} from the {@code redirectUri}.
     *
     * @param redirectUri the redirect url containing the {@code authorizationCode} as URL-Parameter.
     * @return the extracted {@code authorizationCode}
     * @throws IllegalArgumentException if the given {@code redirectUri} does not contain an {@code authorizationCode} URL-Parameter.
     */
    String extractAuthorizationCodeFromRedirectUri(URI redirectUri) throws IllegalArgumentException;

    /**
     * Issues a new token using the given {@code authorizationCode}.
     *
     * @param authorizationCode a valid {@code authorizationCode}.
     * @return a new {@link SomfyToken} instance containing {@code access_token} & {@code refresh_token}.
     * @throws SomfyTokenFetchException if no token could be fetched from Somfy.
     * @see #extractAuthorizationCodeFromRedirectUri(URI)
     */
    SomfyToken issueToken(String authorizationCode) throws SomfyTokenFetchException;

    /**
     * Refreshes an existing {@link SomfyToken} using it's {@code refresh_token} only if
     * the given {@link SomfyToken} is already expired, else it returns the given {@link SomfyToken}.
     *
     * @param token the existing {@link SomfyToken} that should be refreshed.
     * @return a valid {@link SomfyToken}.
     * @throws SomfyTokenFetchException if no token could be fetched from Somfy.
     */
    SomfyToken refreshToken(SomfyToken token) throws SomfyTokenFetchException, SomfyTokenExpiredException;

}
