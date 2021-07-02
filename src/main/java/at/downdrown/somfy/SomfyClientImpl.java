package at.downdrown.somfy;

import at.downdrown.somfy.config.SomfyProperties;
import at.downdrown.somfy.domain.Command;
import at.downdrown.somfy.domain.Device;
import at.downdrown.somfy.domain.Site;
import at.downdrown.somfy.exception.SomfyApiRequestException;
import at.downdrown.somfy.exception.SomfyTokenExpiredException;
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
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

@Slf4j(topic = "somfyclient")
final class SomfyClientImpl implements SomfyClient {

    private final SomfyProperties somfyProperties;
    private final SomfyAuthenticator somfyAuthenticator;
    private final HttpClient httpClient;

    private SomfyToken somfyToken;

    SomfyClientImpl(SomfyProperties somfyProperties,
                    SomfyToken somfyToken,
                    HttpClient httpClient) {
        this.somfyProperties = somfyProperties;
        this.somfyAuthenticator = SomfyAuthenticator.newAuthenticator(somfyProperties, httpClient);
        this.somfyToken = somfyToken;
        this.httpClient = httpClient;
    }

    @Override
    public void setToken(SomfyToken somfyToken) {
        this.somfyToken = somfyToken;
    }

    @Override
    public List<Site> listSites() throws SomfyTokenFetchException, SomfyTokenExpiredException {
        final HttpRequest listSitesRequest = newRequest(URI.create(somfyProperties.getApiUrl() + "/site"))
            .GET()
            .build();
        final HttpResponse<String> listSitesResponse = sendRequest(listSitesRequest);
        return List.of(Json.fromJson(listSitesResponse.body(), Site[].class));
    }

    @Override
    public Site getSiteById(String siteId) throws SomfyTokenFetchException, SomfyTokenExpiredException {
        final HttpRequest getSiteRequest = newRequest(URI.create(somfyProperties.getApiUrl() + "/site/" + siteId))
            .GET()
            .build();
        final HttpResponse<String> listSitesResponse = sendRequest(getSiteRequest);
        return Json.fromJson(listSitesResponse.body(), Site.class);
    }

    @Override
    public List<Device> listDevices(String siteId) throws SomfyTokenFetchException, SomfyTokenExpiredException {
        final HttpRequest listDevicesRequest = newRequest(URI.create(somfyProperties.getApiUrl() + "/site/" + siteId + "/device"))
            .GET()
            .build();
        final HttpResponse<String> listDevicesResponse = sendRequest(listDevicesRequest);
        return List.of(Json.fromJson(listDevicesResponse.body(), Device[].class));
    }

    @Override
    public Device getDeviceById(String deviceId) throws SomfyTokenFetchException, SomfyTokenExpiredException {
        final HttpRequest getDeviceRequest = newRequest(URI.create(somfyProperties.getApiUrl() + "/device/" + deviceId))
            .GET()
            .build();
        final HttpResponse<String> getDevicesResponse = sendRequest(getDeviceRequest);
        return Json.fromJson(getDevicesResponse.body(), Device.class);
    }

    @Override
    public String execute(Command command, String deviceId) throws SomfyTokenFetchException, SomfyTokenExpiredException {
        final HttpRequest commandRequest = newRequest(URI.create(somfyProperties.getApiUrl() + "/device/" + deviceId + "/exec"))
            .header("Content-Type", "application/json; charset=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(Json.toJson(command)))
            .build();
        HttpResponse<String> commandResponse = sendRequest(commandRequest);
        Execution execution = Json.fromJson(commandResponse.body(), Execution.class);
        return execution.getJobId();
    }

    private HttpRequest.Builder newRequest(URI uri) throws SomfyTokenFetchException, SomfyTokenExpiredException {
        return HttpRequest.newBuilder(uri)
            .header("Authorization", "Bearer " + obtainValidAccessToken(somfyToken));
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != HTTP_OK) {
                throw new SomfyApiRequestException("API request was not successful", request.uri(), response.statusCode());
            }
            return response;
        } catch (IOException | InterruptedException e) {
            log.error("Could not send request to Somfy", e);
            throw new RuntimeException("Could not send request to Somfy", e);
        }
    }

    private String obtainValidAccessToken(SomfyToken somfyToken) throws SomfyTokenFetchException, SomfyTokenExpiredException {
        if (somfyToken.isAccessTokenExpired()) {
            this.somfyToken = somfyAuthenticator.refreshToken(somfyToken);
        }
        return somfyToken.getAccessToken();
    }

    @NoArgsConstructor
    @Data
    @ToString
    private static final class Execution {

        @SerializedName("job_id")
        private String jobId;

    }
}
