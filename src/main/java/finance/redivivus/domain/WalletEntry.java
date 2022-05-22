package finance.redivivus.domain;

import java.util.Objects;

public final class WalletEntry {
    public final String ticker;
    public final Long qty;

    public WalletEntry() {
        this(null, null);

    }

    public WalletEntry(String ticker, Long qty) {
        this.ticker = ticker;
        this.qty = qty;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (WalletEntry) obj;
        return Objects.equals(this.ticker, that.ticker) &&
                Objects.equals(this.qty, that.qty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, qty);
    }

    @Override
    public String toString() {
        return "WalletEntry[" +
                "ticker=" + ticker + ", " +
                "qty=" + qty + ']';
    }

}
