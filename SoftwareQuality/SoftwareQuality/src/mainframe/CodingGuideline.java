package mainframe;

import java.io.Serializable;

public class CodingGuideline implements Serializable {
    String codingStandard;
    String identifier;
    String description;
    int limitValue;
    Boolean maintainability = false;
    Boolean performance = false;
    Boolean reliability = false;
    Boolean security = false;
    Boolean portability = false;

    int severity;

    public CodingGuideline(String codingStandard, String identifier) {
        this.codingStandard = codingStandard;
        this.identifier = identifier;
    }
}

