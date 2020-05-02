package pl.airq.weather.openweather;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
class OpenWeatherCurrentInfo {

    Coordinates coord;
    Main main;
    Wind wind;
    Long dt;

    @RegisterForReflection
    static class Coordinates {
        String lon;
        String lat;
    }

    @RegisterForReflection
    static class Main {
        Float temp;
        Float pressure;
        Float humidity;
    }

    @RegisterForReflection
    static class Wind {
        Float speed;
        Float deg;
    }
}
