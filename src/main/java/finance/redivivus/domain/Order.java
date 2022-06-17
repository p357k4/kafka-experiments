package finance.redivivus.domain;

public record Order(Identifier identifier, OrderState state, Instrument debit, Quantity qtyDebit, Instrument credit, Quantity qtyCredit) {
}
