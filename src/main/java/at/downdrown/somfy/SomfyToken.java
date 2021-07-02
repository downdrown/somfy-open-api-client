package at.downdrown.somfy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public final class SomfyToken implements Serializable {

    private final LocalDateTime issuedAt;
    private final String accessToken;
    private final String refreshToken;
    private final LocalDateTime accessTokenExpiresAt;

    public boolean isAccessTokenExpired() {
        return accessTokenExpiresAt.isBefore(LocalDateTime.now());
    }

}
