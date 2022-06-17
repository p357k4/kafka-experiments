package finance.redivivus;

import finance.redivivus.domain.*;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static finance.redivivus.serdes.DomainSerde.*;
import static finance.redivivus.serdes.DomainSerde.serdeOrder;
import static org.junit.jupiter.api.Assertions.*;

class ExperimentsMatchingEngineTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createStream() {
        final var builder = new StreamsBuilder();
        ExperimentsMatchingEngine.createStream(builder);
        final var topology = builder.build();

        // setup test driver
        try (final var testDriver = new TopologyTestDriver(topology)) {

            final var topicProcessed = testDriver
                    .createOutputTopic(
                            ExperimentsMatchingEngine.topicProcessed,
                            serdeIdentifier.deserializer(),
                            serdeOrder.deserializer()
                    );

            final var topicInstrument = testDriver
                    .createInputTopic(
                            ExperimentsMatchingEngine.topicInstrument,
                            serdeIdentifier.serializer(),
                            serdeOrder.serializer()
                    );

            Order orderBuy = Orders.buy(
                    Identifiers.random(),
                    Instruments.stock1,
                    new Quantity(1L),
                    10L
            );

            Order orderSell = Orders.sell(
                    Identifiers.random(),
                    Instruments.stock1,
                    new Quantity(5L),
                    30L
            );

            topicInstrument.pipeInput(orderBuy.identifier(), orderBuy);
            topicInstrument.pipeInput(orderSell.identifier(), orderSell);

            final var record1 = topicProcessed.readKeyValue();
            final var record2 = topicProcessed.readKeyValue();
            final var record3 = topicProcessed.readKeyValue();

        }
    }
}