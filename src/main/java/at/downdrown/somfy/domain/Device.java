package at.downdrown.somfy.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString
public final class Device {

    @SerializedName("id")
    private String id;

    @SerializedName("parent_id")
    private String parentId;

    @SerializedName("site_id")
    private String siteId;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private DeviceType type;

    @SerializedName("categories")
    private Set<Category> categories;

    @SerializedName("states")
    private Set<State> states;

    @SerializedName("capabilities")
    private Set<Capability> capabilities;

    @SerializedName("available")
    private boolean available;

    @SerializedName("version")
    private String version;

}
