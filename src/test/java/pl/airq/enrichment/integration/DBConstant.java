package pl.airq.enrichment.integration;

class DBConstant {

    static final String CREATE_ENRICHED_DATA_TABLE = "CREATE TABLE ENRICHED_DATA\n" +
            "(\n" +
            "    id             BIGSERIAL PRIMARY KEY,\n" +
            "    timestamp      TIMESTAMPTZ,\n" +
            "    pm10           DOUBLE PRECISION,\n" +
            "    pm25           DOUBLE PRECISION,\n" +
            "    temperature    DOUBLE PRECISION,\n" +
            "    wind           DOUBLE PRECISION,\n" +
            "    winddirection  DOUBLE PRECISION,\n" +
            "    humidity       DOUBLE PRECISION,\n" +
            "    pressure       DOUBLE PRECISION,\n" +
            "    lon            DOUBLE PRECISION,\n" +
            "    lat            DOUBLE PRECISION,\n" +
            "    provider       VARCHAR(50),\n" +
            "    station        VARCHAR(100)\n" +
            ")";

    static final String DROP_ENRICHED_DATA_TABLE = "DROP TABLE IF EXISTS ENRICHED_DATA";

}
