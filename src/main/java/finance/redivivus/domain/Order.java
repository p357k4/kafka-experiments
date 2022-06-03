package finance.redivivus.domain;

public record Order(OrderState state, Instrument instrumentDebit, Instrument instrumentCredit, Quantity qtyDebit, Quantity qtyCredit) {
}
