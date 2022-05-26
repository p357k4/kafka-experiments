bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic-bought
bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic-sold
bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic-delta
bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic-command
bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic-wallet
bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic-portfolio
