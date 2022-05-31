package finance.redivivus.domain;

public record Order(Instrument debit, Instrument credit, Long qtyDebit, Long qtyCredit) {
}
