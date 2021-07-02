package at.downdrown.somfy.domain;

import lombok.Getter;

import java.util.Set;

import static at.downdrown.somfy.domain.Capability.CLOSE;
import static at.downdrown.somfy.domain.Capability.IDENTIFY;
import static at.downdrown.somfy.domain.Capability.OPEN;
import static at.downdrown.somfy.domain.Capability.POSITION;
import static at.downdrown.somfy.domain.Capability.POSITION_LOW_SPEED;
import static at.downdrown.somfy.domain.Capability.STOP;

/**
 * A list of device types.
 * This list may be incomplete, please submit an issue if you find missing device types.
 *
 * @author Manfred Huber
 */
@Getter
public enum DeviceType {

    // Hubs
    HUB_CONNEXOON("hub_connexoon"),

    // Roller Shutters
    DISCRETE_GENERIC("roller_shutter_discrete_generic", CLOSE, OPEN, STOP, IDENTIFY),
    POSITIONABLE_ORIENTABLE_STATEFUL_GENERIC("roller_shutter_positionable_orientable_stateful_generic", CLOSE, OPEN, POSITION, STOP, IDENTIFY),
    POSITIONABLE_ORIENTABLE_STATEFUL_GRADHERMETIC("roller_shutter_positionable_orientable_stateful_gradhermetic", CLOSE, OPEN, POSITION, STOP, IDENTIFY),
    POSITIONABLE_ORIENTABLE_STATEFUL_HOLLA("roller_shutter_positionable_orientable_stateful_holla", CLOSE, OPEN, POSITION, STOP, IDENTIFY),
    POSITIONABLE_STATEFUL_DUAL("roller_shutter_positionable_stateful_dual", CLOSE, OPEN, POSITION, STOP, IDENTIFY),
    POSITIONABLE_STATEFUL_GENERIC("roller_shutter_positionable_stateful_generic", CLOSE, OPEN, POSITION, STOP, IDENTIFY),
    POSITIONABLE_STATEFUL_PROJECTION("roller_shutter_positionable_stateful_projection", CLOSE, OPEN, POSITION, STOP, IDENTIFY),
    POSITIONABLE_STATEFUL_ROOF("roller_shutter_positionable_stateful_roof", CLOSE, OPEN, POSITION, STOP, IDENTIFY),
    POSITIONABLE_STATEFUL_RS100("roller_shutter_positionable_stateful_rs100", CLOSE, OPEN, POSITION, POSITION_LOW_SPEED, STOP, IDENTIFY),
    POSITIONABLE_STATELESS_UNO("roller_shutter_positionable_stateless_uno", CLOSE, OPEN, POSITION, STOP, IDENTIFY);

    private final String identification;
    private final Set<Capability> capabilities;

    DeviceType(String identification, Capability... capabilities) {
        this.identification = identification;
        this.capabilities = Set.of(capabilities);
    }

}
