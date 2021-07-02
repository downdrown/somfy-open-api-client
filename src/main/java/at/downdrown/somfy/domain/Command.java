package at.downdrown.somfy.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class Command {

    private final String name;
    private final List<Parameter> parameters;

    private Command(String name, List<Parameter> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public static Command ofCapability(Capability capability, Parameter... parameters) {
        return new Command(capability.getIdentification(), List.of(parameters));
    }

    @Getter
    @ToString
    public static final class Parameter {

        private final String name;
        private final Object value;

        private Parameter(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public static Parameter ofCapabilityParameter(Capability.Parameter parameter, Object value) {
            return new Parameter(parameter.getIdentification(), value);
        }
    }
}
