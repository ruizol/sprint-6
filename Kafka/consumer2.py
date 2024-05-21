from kafka import KafkaConsumer

def decode_str(string):
    if string is None:
        return(None)
    else:
        return(string.decode("utf-8"))

kafka_consumer = KafkaConsumer(
    "test",
    bootstrap_servers="localhost:9092",
    auto_offset_reset="earliest",
    key_deserializer=decode_str,
    value_deserializer=decode_str
)

for message in kafka_consumer:
    print(message.key, message.value)
