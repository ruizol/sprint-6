from kafka import KafkaProducer

def encode_str(string):
    return(string.encode("utf-8"))

kafka_producer = KafkaProducer(
    bootstrap_servers="localhost:9094",
    client_id="production_facile",
    value_serializer=encode_str
)
