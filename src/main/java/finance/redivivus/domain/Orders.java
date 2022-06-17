package finance.redivivus.domain;

public abstract class Orders {
    public static Order buy(Identifier identifier, Instrument stock, Quantity qtyStock, long bid) {
        return new Order(
                identifier,
                OrderState.SUBMITTED,
                stock,
                qtyStock,
                Instruments.cash,
                new Quantity(bid)
        );
    }

    public static Order sell(Identifier identifier, Instrument stock, Quantity qtyStock, long ask) {
        return new Order(
                identifier,
                OrderState.SUBMITTED,
                Instruments.cash,
                new Quantity(ask),
                stock,
                qtyStock
        );
    }
}
