package finance.redivivus.domain;

public record Order(OrderState state, Instrument debit, Quantity qtyDebit, Instrument credit, Quantity qtyCredit) {
}
