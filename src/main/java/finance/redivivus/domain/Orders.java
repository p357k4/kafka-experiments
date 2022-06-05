package finance.redivivus.domain;

public class Orders {
    public static Order buy(Instrument stock, Quantity qtyStock, long bid) {
        return new Order(
                OrderState.SUBMITTED,
                stock,
                qtyStock,
                Instruments.cash,
                new Quantity(bid)
        );
    }

    public static Order sell(Instrument stock, Quantity qtyStock, long ask) {
        return new Order(
                OrderState.SUBMITTED,
                Instruments.cash,
                new Quantity(ask),
                stock,
                qtyStock
        );
    }
}
