import threading

from kafka import KafkaConsumer

DATA_ENRICHED_TOPIC = 'data.enriched'
PHENOTYPE_CREATED_TOPIC = 'phenotype.created'
KAFKA_SERVER = '10.1.1.51:9092'
GROUP = 'TEST_GROUP'


def consume(topic: str):
    consumer = KafkaConsumer(topic, bootstrap_servers=KAFKA_SERVER, group_id=GROUP)
    print("Consumer for {} created!".format(topic))
    for message in consumer:
        print(message)


def main():
    t1 = threading.Thread(target=consume, args=(DATA_ENRICHED_TOPIC,))
    t2 = threading.Thread(target=consume, args=(PHENOTYPE_CREATED_TOPIC,))

    t1.start()
    t2.start()

    t1.join()
    t2.join()


if __name__ == '__main__':
    main()
