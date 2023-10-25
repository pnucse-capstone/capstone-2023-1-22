package mainframe;

import java.io.Serializable;

public class Violation implements Serializable {
    private final String id;
    private final String codingStandard;
    private final String identifier;
    private final String description;
    private final String function;
    private final String module;

    public Violation(String id, String codingStandard, String identifier, String description, String function, String module) {
        this.id = id;
        this.codingStandard = codingStandard;
        this.identifier = identifier;
        this.description = description;
        this.function = function;
        this.module = module;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "id=" + id +
                ", codingStandard='" + codingStandard + '\'' +
                ", identifier='" + identifier + '\'' +
                ", description='" + description + '\'' +
                ", function='" + function + '\'' +
                ", file='" + module + '\'' +
                "'}"+"\n";
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFunction() {
        return function;
    }

    public String getModule() {
        return module;
    }
}
