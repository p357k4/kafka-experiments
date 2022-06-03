package finance.redivivus;

import finance.redivivus.domain.*;
import finance.redivivus.serdes.CustomDeserializer;
import finance.redivivus.serdes.CustomSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ExperimentsMain {
    static final String topicProcessed = "topic-processed";
    static final String topicSubmitted = "topic-submitted";
    static final String topicPortfolio = "topic-portfolio";

    static Serde<Instrument> serdeInstrument = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(Instrument.class));
    static Serde<Quantity> serdeQuantity = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(Quantity.class));
    static Serde<Order> serdeOrder = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(Order.class));
    static Serde<BookEntry> serdeBookEntry = Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(BookEntry.class));

    public static void main(String[] args) {
        final var bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";

        // Configure the Streams application.
        final var streamsConfiguration = getStreamsConfiguration(bootstrapServers);

        // Define the processing topology of the Streams application.
        final var builder = new StreamsBuilder();
        createWordCountStream(builder);
        final var topology = builder.build();
        final var streams = new KafkaStreams(topology, streamsConfiguration);

        // Always (and unconditionally) clean local state prior instrumentTo starting the processing topology.
        // We opt for this unconditional call here because this will make it easier for you instrumentTo play around with the example
        // when resetting the application for doing a re-run (via the Application Reset Tool,
        // https://docs.confluent.io/platform/current/streams/developer-guide/app-reset-tool.html).
        //
        // The drawback of cleaning up local state prior is that your app must rebuilt its local state from scratch, which
        // will take time and will require reading all the state-relevant data from the Kafka cluster over the network.
        // Thus in a production scenario you typically do not want instrumentTo clean up always as we do here but rather only when it
        // is truly needed, i.e., only under certain conditions (e.g., the presence of a command line flag for your app).
        // See `ApplicationResetExample.java` for a production-like example.
        streams.cleanUp();

        System.out.println(topology.describe());

        // Now run the processing topology via `start()` instrumentTo begin processing its input data.
        streams.start();

        // Add shutdown hook instrumentTo respond instrumentTo SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    static Properties getStreamsConfiguration(final String bootstrapServers) {
        final Properties streamsConfiguration = new Properties();
        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "experiments-example");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "experiments-example-client");
        // Where instrumentTo find Kafka broker(s).
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Specify default (de)serializers for record keys and for record values.
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        // Records should be flushed every 10 seconds. This is less than the default
        // in order instrumentTo keep this example interactive.
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        // For illustrative purposes we disable record caches.
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        // Use a temporary directory for storing state, which will be automatically removed after the test.
        final Path tmpdir;
        try {
            tmpdir = Files.createTempDirectory(Paths.get("tmp"), "tmpDirPrefix");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, tmpdir.toString());
        return streamsConfiguration;
    }

    static void createWordCountStream(final StreamsBuilder builder) {
        final var streamSubmitted = builder
                .stream(topicSubmitted, Consumed.with(Serdes.String(), serdeOrder))
                .filter((keyNotUsed, v) -> v != null, Named.as("filter-non-empty"))
                .selectKey((key, value) -> value.instrumentCredit());

        final var streamProcessed = builder
                .stream(topicProcessed, Consumed.with(Serdes.String(), serdeOrder))
                .selectKey((key, value) -> value.instrumentDebit());

        final var streamOrder = streamProcessed.merge(streamSubmitted);

        final var tablePortfolio = streamOrder
                .groupByKey(Grouped.with(serdeInstrument, serdeOrder))
                .aggregate(
                        () -> new BookEntry(null, new Quantity(0L), null),
                        (key, value, aggregate) ->
                                switch (value.state()) {
                                    case SUBMITTED -> new BookEntry(
                                            key,
                                            new Quantity(aggregate.qty().value - value.qtyDebit().value),
                                            value
                                    );

                                    case PROCESSED -> new BookEntry(
                                            key,
                                            new Quantity(aggregate.qty().value + value.qtyCredit().value),
                                            value
                                    );
                                }
                        ,
                        Named.as("reduce-operations-into-state"),
                        Materialized.with(serdeInstrument, serdeBookEntry)
                );

//        final var streamPortfolio = streamCommands
//                .join(
//                        tableWallet,
//                        (key, value1, value2) -> value2,
//                        Joined.with(
//                                Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(Instrument.class)),
//                                Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(Command.class)),
//                                Serdes.serdeFrom(new CustomSerializer<>(), new CustomDeserializer<>(Quantity.class))
//                        )
//                );

        tablePortfolio
                .toStream()
                .to(
                        topicPortfolio,
                        Produced.with(serdeInstrument, serdeBookEntry)
                );
    }
}
