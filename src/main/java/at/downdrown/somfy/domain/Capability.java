package at.downdrown.somfy.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * A list of device capabilities and their parameters.
 * This list may be incomplete, please submit an issue if you find missing capabilities.
 *
 * @author Manfred Huber
 */
@Getter
public enum Capability {

    CLOSE("close"),
    OPEN("open"),
    POSITION("position", Parameter.POSITION),
    POSITION_LOW_SPEED("position_low_speed", Parameter.POSITION),
    STOP("stop"),
    IDENTIFY("identify");

    private final String identification;
    private final Set<Parameter> parameters;
    Capability(String identification, Parameter... parameters) {
        this.identification = identification;
        this.parameters = Set.of(parameters);
    }

    @AllArgsConstructor
    @Getter
    public enum Parameter {

        POSITION("position", Integer.class, true);

        private final String identification;
        private final Class<?> parameterType;
        private final boolean required;

    }
}
