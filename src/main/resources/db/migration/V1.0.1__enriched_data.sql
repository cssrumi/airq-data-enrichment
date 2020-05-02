CREATE TABLE ENRICHED_DATA
(
    id             BIGSERIAL PRIMARY KEY,
    timestamp      TIMESTAMPTZ,
    pm10           DOUBLE PRECISION,
    pm25           DOUBLE PRECISION,
    temperature    DOUBLE PRECISION,
    wind           DOUBLE PRECISION,
    winddirection  DOUBLE PRECISION,
    humidity       DOUBLE PRECISION,
    pressure       DOUBLE PRECISION,
    lon            DOUBLE PRECISION,
    lat            DOUBLE PRECISION,
    provider       VARCHAR(50),
    station        VARCHAR(100)
);
