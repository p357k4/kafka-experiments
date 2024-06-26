package finance.redivivus.domain;

import java.util.Objects;

public final class WalletEntry {
    public final Instrument instrument;
    public final Long qty;

    public WalletEntry() {
        this(null, null);

    }

    public WalletEntry(Instrument instrument, Long qty) {
        this.instrument = instrument;
        this.qty = qty;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (WalletEntry) obj;
        return Objects.equals(this.instrument, that.instrument) &&
                Objects.equals(this.qty, that.qty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrument, qty);
    }

    @Override
    public String toString() {
        return "WalletEntry[" +
                "ticker=" + instrument + ", " +
                "qty=" + qty + ']';
    }

}
