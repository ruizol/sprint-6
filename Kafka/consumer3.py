from kafka import KafkaConsumer

def decode_str(string):
    if string is None:
        return(None)
    else:
        return(string.decode("utf-8"))

kafka_consumer = KafkaConsumer(
    "test",
    bootstrap_servers="localhost:9092",
    group_id="consumer",
    auto_offset_reset="earliest",
    key_deserializer=decode_str,
    value_deserializer=decode_str,
    enable_auto_commit=True,
)

for message in kafka_consumer:
    print(message.key, message.value)

kafka_consumer.close()
