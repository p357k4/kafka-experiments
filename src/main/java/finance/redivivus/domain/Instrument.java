package finance.redivivus.domain;

import java.util.Objects;

public final class Instrument {
    public final String ticker;

    public Instrument() {
        this(null);
    }

    public Instrument(String ticker) {
        this.ticker = ticker;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Instrument) obj;
        return Objects.equals(this.ticker, that.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }

    @Override
    public String toString() {
        return "Instrument[" +
                "ticker=" + ticker + ']';
    }

}
