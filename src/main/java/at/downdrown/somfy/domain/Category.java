package at.downdrown.somfy.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A list of device categories.
 * This list may be incomplete, please submit an issue if you find missing categories.
 *
 * @author Manfred Huber
 */
@AllArgsConstructor
@Getter
public enum Category {

    ACTUATOR("actuator"),
    ROLLER_SHUTTER("roller_shutter"),
    HUB("hub");

    private final String identification;

}
