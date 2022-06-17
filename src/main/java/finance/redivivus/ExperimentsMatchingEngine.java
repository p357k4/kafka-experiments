package finance.redivivus;

import finance.redivivus.domain.*;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.util.HashSet;
import java.util.Set;

import static finance.redivivus.serdes.DomainSerde.*;

public class ExperimentsMatchingEngine {
    public static final String topicInstrument = "topic-instrument";
    public static final String topicProcessed = "topic-processed";
    public static final String topicMatched = "topic-matched";

    static void createStream(final StreamsBuilder builder) {
        final var streamInstrument = builder
                .stream(topicInstrument, Consumed.with(serdeIdentifier, serdeOrder));

        final var streamProcessed = builder
                .stream(topicProcessed, Consumed.with(serdeIdentifier, serdeOrder));

        final var tableProcessed = streamProcessed.toTable();
        final var streamNotProcessed = streamInstrument
                .leftJoin(
                        tableProcessed,
                        (value1, value2) -> {
                            if (value2 != null) {
                                return null;
                            } else {
                                return value1;
                            }
                        },
                        Joined.with(serdeIdentifier, serdeOrder, serdeOrder)
                );

        final var aggregated = streamNotProcessed
                .groupBy(
                        (k, v) -> new OrderPair(v.debit(), v.credit()),
                        Grouped.with(serdeOrderPair, serdeOrder))
                .aggregate(
                        () -> new OrderSet(Set.of()),
                        (key, value, aggregate) -> {
                            final var orders = new HashSet<>(Set.copyOf(aggregate.orders()));
                            orders.add(value);
                            return new OrderSet(orders);
                        },
                        Named.as("order-matching"),
                        Materialized.with(serdeOrderPair, serdeOrderSet)
                );

        aggregated
                .toStream()
                .selectKey((k, v) -> k.from())
                .mapValues(value -> value.orders().stream().findAny().get())
                .to(topicProcessed, Produced.with(serdeInstrument, serdeOrder));

        aggregated
                .toStream()
                .to(topicMatched);
    }
}
