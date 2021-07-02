package at.downdrown.somfy.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public final class Site {

    @SerializedName("id")
    private String id;

    @SerializedName("label")
    private String label;

}
