package finance.redivivus;

import finance.redivivus.domain.*;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.util.*;

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
                        (k, v) -> {
                            if (v.debit().equals(Instruments.cash)) {
                                return new InstrumentPair(v.credit(), v.debit());
                            } else {
                                return new InstrumentPair(v.debit(), v.credit());
                            }
                        },
                        Grouped.with(serdeInstrumentPair, serdeOrder))
                .aggregate(
                        () -> new OrderSet(new TreeSet<>(Comparator.comparingLong(order -> order.qtyDebit().value()))),
                        (key, value, aggregate) -> {
                            aggregate.orders().add(value);
                            return aggregate;
                        },
                        Named.as("order-matching"),
                        Materialized.with(serdeInstrumentPair, serdeOrderSet)
                );

        aggregated
                .toStream()
                .selectKey((k, v) -> k.from())
                .mapValues(value -> value.orders().stream().findFirst().get())
                .to(topicProcessed, Produced.with(serdeInstrument, serdeOrder));

        aggregated
                .toStream()
                .to(topicMatched);
    }
}
