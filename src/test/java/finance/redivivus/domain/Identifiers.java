package finance.redivivus.domain;

import java.util.UUID;

public class Identifiers {
    public static Identifier random() {
        return new Identifier(UUID.randomUUID().toString());
    }
}
