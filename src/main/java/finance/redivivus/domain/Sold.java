package finance.redivivus.domain;

import java.util.Objects;

public final class Sold {
    public final String ticker;
    public final Quantity qty;

    public Sold() {
        this(null, null);
    }

    public Sold(String ticker, Quantity qty) {
        this.ticker = ticker;
        this.qty = qty;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Sold) obj;
        return Objects.equals(this.ticker, that.ticker) &&
                Objects.equals(this.qty, that.qty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, qty);
    }

    @Override
    public String toString() {
        return "Sold[" +
                "ticker=" + ticker + ", " +
                "qty=" + qty + ']';
    }
}
