package at.downdrown.somfy;

import at.downdrown.somfy.config.SomfyProperties;
import at.downdrown.somfy.exception.SomfyTokenExpiredException;
import at.downdrown.somfy.exception.SomfyTokenFetchException;
import org.testng.annotations.Test;
import org.testng.reporters.Files;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertThrows;

@SuppressWarnings("unchecked")
public class SomfyAuthenticatorImplTest {

    private final SomfyProperties properties = SomfyProperties.builder()
        .callbackUrl("https://my.callback.url")
        .consumerKey("a-consumer-key")
        .consumerSecret("a-consumer-secret")
        .build();

    private static String readFile(String filename) {
        try {
            return Files.readFile(Objects.requireNonNull(SomfyClientImplTest.class.getResourceAsStream(filename)));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file " + filename);
        }
    }

    @Test
    public void buildAuthenticationRequestUri_shouldProduceValidAuthenticationUrl() {

        Pattern authUrlPattern = Pattern.compile(
            "^https?://accounts\\.somfy\\.com/oauth/oauth/v2/auth" +
                "\\?response_type=code" +
                "&client_id=a-consumer-key" +
                "&redirect_uri=https?://my\\.callback\\.url" +
                "&state=\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b" +
                "&grant_type=authorization_code");

        SomfyAuthenticator somfyAuthenticator = SomfyAuthenticator.newAuthenticator(properties);

        assertThat(somfyAuthenticator.buildAuthenticationRequestUri().toString())
            .matches(authUrlPattern);
    }

    @Test
    public void extractAuthorizationCodeFromRedirectUri_shouldExtractAuthorizationCode() {

        SomfyAuthenticator somfyAuthenticator = SomfyAuthenticator.newAuthenticator(properties);

        URI redirectUri = URI.create("https://my.callback.url?code=my-somfy-authorization-code");
        String authorizationCode = somfyAuthenticator.extractAuthorizationCodeFromRedirectUri(redirectUri);

        assertThat(authorizationCode)
            .isEqualTo("my-somfy-authorization-code");
    }

    @Test
    public void extractAuthorizationCodeFromRedirectUri_shouldThrowIllegalArgumentException() {
        SomfyAuthenticator somfyAuthenticator = SomfyAuthenticator.newAuthenticator(properties);
        URI redirectUri = URI.create("https://my.callback.url");
        assertThrows(IllegalArgumentException.class, () -> somfyAuthenticator.extractAuthorizationCodeFromRedirectUri(redirectUri));
    }

    @Test
    public void issueToken() throws IOException, InterruptedException, SomfyTokenFetchException {

        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(HTTP_OK);
        when(mockResponse.body())
            .thenReturn(readFile("token.json"));

        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SomfyAuthenticator authenticator = SomfyAuthenticator.newAuthenticator(properties, mockHttpClient);
        SomfyToken somfyToken = authenticator.issueToken("a-valid-authorization-token");

        assertThat(somfyToken)
            .extracting(
                SomfyToken::getAccessToken,
                SomfyToken::getRefreshToken)
            .containsExactly(
                "a-valid-access-token",
                "a-valid-refresh-token"
            );

        assertThat(somfyToken.getIssuedAt())
            .isBefore(now());
        assertThat(somfyToken.getAccessTokenExpiresAt())
            .isAfter(now());
    }

    @Test
    public void refreshToken_shouldRefreshToken() throws IOException, InterruptedException, SomfyTokenFetchException, SomfyTokenExpiredException {

        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(HTTP_OK);
        when(mockResponse.body())
            .thenReturn(readFile("token.json"));

        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SomfyToken currentToken = new SomfyToken(now().minusHours(1), "access-token", "refresh-token", now().minusMinutes(1));

        SomfyAuthenticator authenticator = SomfyAuthenticator.newAuthenticator(properties, mockHttpClient);
        SomfyToken somfyToken = authenticator.refreshToken(currentToken);

        assertThat(somfyToken)
            .extracting(
                SomfyToken::getAccessToken,
                SomfyToken::getRefreshToken)
            .containsExactly(
                "a-valid-access-token",
                "a-valid-refresh-token"
            );

        assertThat(somfyToken.getIssuedAt())
            .isBefore(now());
        assertThat(somfyToken.getAccessTokenExpiresAt())
            .isAfter(now());
    }

    @Test
    public void refreshToken_shouldNotRefreshToken() throws IOException, InterruptedException, SomfyTokenFetchException, SomfyTokenExpiredException {

        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(HTTP_OK);
        when(mockResponse.body())
            .thenReturn(readFile("token.json"));

        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SomfyToken currentToken = new SomfyToken(now(), "access-token", "refresh-token", now().plusHours(1));

        SomfyAuthenticator authenticator = SomfyAuthenticator.newAuthenticator(properties, mockHttpClient);
        SomfyToken somfyToken = authenticator.refreshToken(currentToken);

        assertThat(somfyToken).isSameAs(currentToken);

    }
}
