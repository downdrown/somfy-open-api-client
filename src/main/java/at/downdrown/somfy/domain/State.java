package at.downdrown.somfy.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public final class State {

    @SerializedName("name")
    private String name;

    @SerializedName("value")
    private Object value;

    @SerializedName("type")
    private String type;

}
