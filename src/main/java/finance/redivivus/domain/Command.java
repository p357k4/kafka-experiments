package finance.redivivus.domain;

import java.util.Objects;

public final class Command {
    public final String ticker;

    public Command() {
        this(null);
    }

    public Command(String ticker) {
        this.ticker = ticker;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Command) obj;
        return Objects.equals(this.ticker, that.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }

    @Override
    public String toString() {
        return "Command[" +
                "ticker=" + ticker + ']';
    }

}
