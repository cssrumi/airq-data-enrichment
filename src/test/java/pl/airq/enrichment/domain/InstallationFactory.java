package pl.airq.enrichment.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import pl.airq.common.domain.gios.installation.Installation;

import static org.junit.jupiter.api.Assertions.fail;

public class InstallationFactory {

    public static Installation installationWithPm10(Long id, Float pm10) {
        return createInstanceWith(id, pm10, "PM10");
    }

    public static Installation installationWithPm25(Long id, Float pm25) {
        return createInstanceWith(id, pm25, "PM25");
    }

    private static Installation createInstanceWith(Long id, Float value, String code) {
        try {
            final Constructor<?> constructor = Installation.class.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            return (Installation) constructor.newInstance(id, "Installation" + id, OffsetDateTime.now(), value, Float.valueOf(1f), Float.valueOf(1f), code);
//            return ConstructorUtils.invokeConstructor(Installation.class,
//                    id, "Installation" + id, OffsetDateTime.now(), value, Float.valueOf(1f), Float.valueOf(1f), code);
        } catch (Exception e) {
            fail("Unable to create Installation instance", e);
            return null;
        }
    }

}
