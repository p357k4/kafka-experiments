package finance.redivivus;

import finance.redivivus.domain.Instruments;
import finance.redivivus.domain.Order;
import finance.redivivus.domain.OrderState;
import finance.redivivus.domain.Quantity;
import finance.redivivus.serdes.CustomSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

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
                            ExperimentsMain.serdeOrder.serializer()
                    );

            final var topicSubmitted = testDriver
                    .createInputTopic(
                            ExperimentsMain.topicSubmitted,
                            Serdes.String().serializer(),
                            ExperimentsMain.serdeOrder.serializer()
                    );

            final var topicPortfolio = testDriver
                    .createOutputTopic(
                            ExperimentsMain.topicPortfolio,
                            ExperimentsMain.serdeInstrument.deserializer(),
                            ExperimentsMain.serdeBookEntry.deserializer()
                    );

            Order orderSold = new Order(
                    OrderState.PROCESSED,
                    Instruments.cash,
                    new Quantity(100L),
                    Instruments.cash,
                    new Quantity(0L)
            );

            Order orderBuy = new Order(
                    OrderState.SUBMITTED,
                    Instruments.stock1,
                    new Quantity(1L),
                    Instruments.cash,
                    new Quantity(10L)
            );

            Order orderSell = new Order(
                    OrderState.SUBMITTED,
                    Instruments.cash,
                    new Quantity(30L),
                    Instruments.stock1,
                    new Quantity(5L)
            );

            topicProcessed.pipeInput("whatever", orderSold);
            topicSubmitted.pipeInput("whatever", orderBuy);
            topicSubmitted.pipeInput("whatever", orderSell);

            final var record1 = topicPortfolio.readKeyValue();
            final var record2 = topicPortfolio.readKeyValue();
            final var record3 = topicPortfolio.readKeyValue();

            final var so = new String(ExperimentsMain.serdeOrder.serializer().serialize("", orderSold));
        }
    }
}