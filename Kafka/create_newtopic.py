from kafka.admin import NewTopic
from kafka.admin import KafkaAdminClient

test = NewTopic(name="test", num_partitions=4, replication_factor=1)

kafka_admin = KafkaAdminClient(
    bootstrap_servers="localhost:9092",
    client_id="admin"
)

kafka_admin.create_topics([test])
