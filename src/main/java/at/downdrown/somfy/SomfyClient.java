package at.downdrown.somfy;

import at.downdrown.somfy.config.SomfyProperties;
import at.downdrown.somfy.domain.Command;
import at.downdrown.somfy.domain.Device;
import at.downdrown.somfy.domain.Site;
import at.downdrown.somfy.exception.SomfyTokenExpiredException;
import at.downdrown.somfy.exception.SomfyTokenFetchException;

import java.net.http.HttpClient;
import java.util.List;

/**
 * Client for interacting with the Somfy Open API.
 *
 * @author Manfred Huber (downdrown)
 * @see SomfyAuthenticator
 */
public interface SomfyClient {

    static SomfyClient newClient(SomfyProperties properties, SomfyToken token) {
        return newClient(properties, token, HttpClient.newHttpClient());
    }

    static SomfyClient newClient(SomfyProperties properties, SomfyToken token, HttpClient httpClient) {
        return new SomfyClientImpl(properties, token, httpClient);
    }

    /**
     * Sets the {@link SomfyToken} that will be used when making requests.
     * Use this method if your {@link SomfyToken} has completely expired,
     * meaning not only the {@code access_token} but also the {@code refresh_token}
     * has expired.
     *
     * @param somfyToken a valid instance of {@link SomfyToken}.
     */
    void setToken(SomfyToken somfyToken);

    /**
     * List all {@link Site}s for the authenticated user.
     *
     * @return all available {@link Site}s.
     * @throws SomfyTokenExpiredException when the access- & refresh token has expired.
     */
    List<Site> listSites() throws SomfyTokenExpiredException, SomfyTokenFetchException;

    /**
     * Get a specific {@link Site} by it's {@code siteId}.
     *
     * @return the {@link Site} with the given {@code siteId} or {@code null}.
     * @throws SomfyTokenExpiredException when the access- & refresh token has expired.
     */
    Site getSiteById(String siteId) throws SomfyTokenExpiredException, SomfyTokenFetchException;

    /**
     * List all {@link Device}s for the {@link Site} with the given {@code siteId}.
     *
     * @return all available {@link Device}s.
     * @throws SomfyTokenExpiredException when the access- & refresh token has expired.
     */
    List<Device> listDevices(String siteId) throws SomfyTokenExpiredException, SomfyTokenFetchException;

    /**
     * Get a specific {@link Device} by it's {@code deviceId}.
     *
     * @return the {@link Device} with the given {@code deviceId} or {@code null}.
     * @throws SomfyTokenExpiredException when the access- & refresh token has expired.
     */
    Device getDeviceById(String deviceId) throws SomfyTokenExpiredException, SomfyTokenFetchException;

    /**
     * Executes a {@link Command} for a {@link Device}.
     *
     * @param command  the command that should be executed.
     * @param deviceId the ID of the device for which the command should be executed.
     * @return the {@code jobId} for the execution.
     * @throws SomfyTokenExpiredException when the access- & refresh token has expired.
     */
    String execute(Command command, String deviceId) throws SomfyTokenExpiredException, SomfyTokenFetchException;

}
