package finance.redivivus.domain;

import java.util.Objects;

public final class Bought {
    public final String ticker;
    public final Quantity qty;

    public Bought() {
        this(null, null);
    }

    public Bought(String ticker, Quantity qty) {
        this.ticker = ticker;
        this.qty = qty;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Bought) obj;
        return Objects.equals(this.ticker, that.ticker) &&
                Objects.equals(this.qty, that.qty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, qty);
    }

    @Override
    public String toString() {
        return "Bought[" +
                "ticker=" + ticker + ", " +
                "qty=" + qty + ']';
    }


}
