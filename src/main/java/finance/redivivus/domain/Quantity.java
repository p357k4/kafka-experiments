package finance.redivivus.domain;

import java.util.Objects;

public final class Quantity {
    public final Long value;

    public Quantity() {
        this(null);
    }

    public Quantity(Long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Quantity) obj;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Quantity[" +
                "value=" + value + ']';
    }

}
