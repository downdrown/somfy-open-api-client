package at.downdrown.somfy;

import at.downdrown.somfy.config.SomfyProperties;
import at.downdrown.somfy.domain.Capability;
import at.downdrown.somfy.domain.Command;
import at.downdrown.somfy.domain.Device;
import at.downdrown.somfy.domain.Site;
import at.downdrown.somfy.exception.SomfyTokenExpiredException;
import at.downdrown.somfy.exception.SomfyTokenFetchException;
import org.testng.annotations.Test;
import org.testng.reporters.Files;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class SomfyClientImplTest {

    private final SomfyProperties properties = SomfyProperties.builder()
        .callbackUrl("https://my.callback.url")
        .consumerKey("a-consumer-key")
        .consumerSecret("a-consumer-secret")
        .build();

    private final SomfyToken token = SomfyToken.builder()
        .issuedAt(now())
        .accessToken("a-access-token")
        .refreshToken("a-refresh-token")
        .accessTokenExpiresAt(now().plusHours(1))
        .build();

    private static String readFile(String filename) {
        try {
            return Files.readFile(Objects.requireNonNull(SomfyClientImplTest.class.getResourceAsStream(filename)));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file " + filename);
        }
    }

    @Test
    public void listSites() throws SomfyTokenFetchException, SomfyTokenExpiredException, IOException, InterruptedException {

        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(HTTP_OK);
        when(mockResponse.body())
            .thenReturn(readFile("sites.json"));

        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SomfyClient client = SomfyClient.newClient(properties, token, mockHttpClient);
        List<Site> sites = client.listSites();

        assertThat(sites)
            .isNotEmpty()
            .element(0)
            .extracting(Site::getLabel, Site::getId)
            .containsExactly("Connexoon", "1234-5678-91011-121314");
    }

    @Test
    public void getSiteById() throws SomfyTokenFetchException, SomfyTokenExpiredException, IOException, InterruptedException {

        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(HTTP_OK);
        when(mockResponse.body())
            .thenReturn(readFile("site.json"));

        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SomfyClient client = SomfyClient.newClient(properties, token, mockHttpClient);
        Site site = client.getSiteById("1234-5678-91011-121314");

        assertThat(site)
            .isNotNull()
            .extracting(Site::getLabel, Site::getId)
            .containsExactly("Connexoon", "1234-5678-91011-121314");
    }

    @Test
    public void listDevices() throws SomfyTokenFetchException, SomfyTokenExpiredException, IOException, InterruptedException {

        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(HTTP_OK);
        when(mockResponse.body())
            .thenReturn(readFile("devices.json"));

        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SomfyClient client = SomfyClient.newClient(properties, token, mockHttpClient);
        List<Device> devices = client.listDevices("1234-5678-91011-121314");

        assertThat(devices)
            .isNotEmpty()
            .hasSize(17);
    }

    @Test
    public void execute() throws SomfyTokenFetchException, SomfyTokenExpiredException, IOException, InterruptedException {

        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(HTTP_OK);
        when(mockResponse.body())
            .thenReturn(readFile("execution.json"));

        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SomfyClient client = SomfyClient.newClient(properties, token, mockHttpClient);
        String jobId = client.execute(Command.ofCapability(Capability.IDENTIFY), "1");

        assertThat(jobId)
            .isEqualTo("a-job-id");
    }

    @Test
    public void getDeviceById() throws SomfyTokenFetchException, SomfyTokenExpiredException, IOException, InterruptedException {

        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(HTTP_OK);
        when(mockResponse.body())
            .thenReturn(readFile("device.json"));

        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SomfyClient client = SomfyClient.newClient(properties, token, mockHttpClient);
        Device device = client.getDeviceById("1");

        assertThat(device)
            .isNotNull();
    }
}
