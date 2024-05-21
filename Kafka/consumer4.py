from kafka import KafkaConsumer, TopicPartition

def decode_str(string):
    if string is None:
        return(None)
    else:
        return(string.decode("utf-8"))

kafka_consumer = KafkaConsumer(
    bootstrap_servers="localhost:9092",
    group_id="consumer_test0",
    auto_offset_reset="earliest",
    key_deserializer=decode_str,
    value_deserializer=decode_str,
    enable_auto_commit=False,
    consumer_timeout_ms=1000
)

test0 = TopicPartition("test", 0) ### On définit une partition d'un topic.
kafka_consumer.assign([test0]) ### Puis on l'assigne.

for message in kafka_consumer:
    print(message.key, message.value)
    kafka_consumer.commit() ### On commit pour déclarer le bon traitement du message.

kafka_consumer.close()
