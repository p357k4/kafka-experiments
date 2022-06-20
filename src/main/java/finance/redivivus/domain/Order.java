package finance.redivivus.domain;

public record Order(Identifier identifier, OrderState state, Offer debit, Offer credit) {
}
