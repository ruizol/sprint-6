from kafka.admin import KafkaAdminClient, NewTopic
from kafka.errors import TopicAlreadyExistsError

test = NewTopic(name="test", num_partitions=4, replication_factor=1)

kafka_admin = KafkaAdminClient(
    bootstrap_servers="localhost:9092",
    client_id="admin"
)

try:
    kafka_admin.create_topics([test])
except TopicAlreadyExistsError:
    print("Le topic test existe déjà.")

kafka_admin.close()
