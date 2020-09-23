import json

from kafka import KafkaProducer

DATA_ENRICHED_TOPIC = 'data.enriched'
PHENOTYPE_CREATED_TOPIC = 'phenotype.created'
KAFKA_SERVER = '10.1.1.51:9092'
GROUP = 'TEST_GROUP'

DATA_ENRICHED_PAYLOAD = {
    "timestamp": 1600113159.746618000,
    "payload": {"enrichedData":
        {
            "timestamp": 1600110000.000000000,
            "pm10": 30.4075,
            "pm25": 20.8625,
            "temp": 289.15,
            "wind": 1.5,
            "windDirection": 200.0,
            "humidity": 77.0,
            "pressure": 1026.0,
            "lon": 18.620274,
            "lat": 54.38028,
            "provider": "GIOS",
            # "station": "AM8 Gdańsk Wrzeszcz"
            # "station": "AM1 Gdańsk Śródmieście"
            "station": "AM9 Gdynia Dąbrowa"
        }
    }
}

producer = KafkaProducer(bootstrap_servers=KAFKA_SERVER)


def main():
    message = json.dumps(DATA_ENRICHED_PAYLOAD).encode()
    print(message)
    producer.send(DATA_ENRICHED_TOPIC, message)
    producer.flush()


if __name__ == '__main__':
    main()
