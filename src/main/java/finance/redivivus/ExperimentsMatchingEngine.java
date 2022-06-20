package finance.redivivus;

import finance.redivivus.domain.*;
import org.apache.kafka.streams.KeyValue;
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

        final var tableProcessed = streamProcessed.toTable(Materialized.with(serdeIdentifier, serdeOrder));
        final var tableInstrument = streamInstrument.toTable(Materialized.with(serdeIdentifier, serdeOrder));
        final var tableNotProcessed = tableInstrument
                .leftJoin(
                        tableProcessed,
                        (value1, value2) -> {
                            if (value2 != null) {
                                return null;
                            } else {
                                return value1;
                            }
                        },
                        Materialized.with(serdeIdentifier, serdeOrder)
                );

        final var aggregated = tableNotProcessed
                .groupBy(
                        (k, v) -> {
                            if (v.debit().instrument().equals(Instruments.cash)) {
                                return KeyValue.pair(new InstrumentPair(v.credit().instrument(), v.debit().instrument()), v);
                            } else {
                                return KeyValue.pair(new InstrumentPair(v.debit().instrument(), v.credit().instrument()), v);
                            }
                        },
                        Grouped.with(serdeInstrumentPair, serdeOrder))
                .aggregate(
                        () -> new OrderSet(new TreeSet<>(Comparator.comparingLong(order -> order.debit().quantity().value()))),
                        (key, value, aggregate) -> {
                            aggregate.orders().add(value);
                            return aggregate;
                        },
                        (key, value, aggregate) -> {
                            aggregate.orders().remove(value);
                            return aggregate;
                        },
                        Named.as("order-matching"),
                        Materialized.with(serdeInstrumentPair, serdeOrderSet)
                );

        final var kStream = aggregated
                .toStream()
                .mapValues(value -> value.orders().stream().findFirst().get())
                .selectKey((k, v) -> v.identifier());

        kStream
                .print(Printed.toSysOut());

        kStream
                .to(topicProcessed, Produced.with(serdeIdentifier, serdeOrder));

        aggregated
                .toStream()
                .to(topicMatched);
    }
}
