package finance.redivivus.serdes;

import finance.redivivus.domain.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class DomainSerde {
    public static Serde<Identifier> serdeIdentifier = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(Identifier.class));
    public static Serde<Instrument> serdeInstrument = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(Instrument.class));
    public static Serde<Quantity> serdeQuantity = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(Quantity.class));
    public static Serde<Order> serdeOrder = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(Order.class));
    public static Serde<BookEntry> serdeBookEntry = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(BookEntry.class));
    public static Serde<OrderSet> serdeOrderSet = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(OrderSet.class));
    public static Serde<OrderPair> serdeOrderPair = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(OrderPair.class));

}
