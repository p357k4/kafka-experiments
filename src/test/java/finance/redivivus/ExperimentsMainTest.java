package finance.redivivus;

import finance.redivivus.domain.*;
import finance.redivivus.serdes.CustomSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static finance.redivivus.serdes.DomainSerde.*;
import static org.junit.jupiter.api.Assertions.*;

class ExperimentsMainTest {
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void main() {
        final var builder = new StreamsBuilder();
        ExperimentsMain.createStream(builder);
        final var topology = builder.build();

        // setup test driver
        try (final var testDriver = new TopologyTestDriver(topology)) {

            final var topicProcessed = testDriver
                    .createInputTopic(
                            ExperimentsMain.topicProcessed,
                            Serdes.String().serializer(),
                            serdeOrder.serializer()
                    );

            final var topicSubmitted = testDriver
                    .createInputTopic(
                            ExperimentsMain.topicSubmitted,
                            Serdes.String().serializer(),
                            serdeOrder.serializer()
                    );

            final var topicPortfolio = testDriver
                    .createOutputTopic(
                            ExperimentsMain.topicPortfolio,
                            serdeInstrument.deserializer(),
                            serdeBookEntry.deserializer()
                    );

            Order orderSold = new Order(
                    Identifiers.random(),
                    OrderState.PROCESSED,
                    Instruments.cash,
                    new Quantity(100L),
                    Instruments.cash,
                    new Quantity(0L)
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

            topicProcessed.pipeInput("whatever", orderSold);
            topicSubmitted.pipeInput("whatever", orderBuy);
            topicSubmitted.pipeInput("whatever", orderSell);

            final var record1 = topicPortfolio.readKeyValue();
            final var record2 = topicPortfolio.readKeyValue();
            final var record3 = topicPortfolio.readKeyValue();

            final var so = new String(serdeOrder.serializer().serialize("", orderSold));
        }
    }
}