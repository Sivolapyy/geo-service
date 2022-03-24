import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSender;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;

public class serviceTests {

    // Тестирование MessageSender
    @ParameterizedTest
    @CsvSource({
            "172.1.2.3, Добро пожаловать",
            "96.1.2.3, Welcome"
    })
    void messageSenderTest(String ip, String expected) {

        Location location = expected.equals("Добро пожаловать")
                ? new Location(null, Country.RUSSIA, null, 3)
                : new Location(null, Country.USA, null, 2);

        Map<String, String > headers = new HashMap<String, String>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);

        GeoService geoService = Mockito.mock(GeoService.class);
        Mockito.when(geoService.byIp(ip)).thenReturn(location);

        LocalizationService localizationService = Mockito.mock(LocalizationService.class);
        Mockito.when(localizationService.locale(location.getCountry())).thenReturn(expected);

        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);

        Assertions.assertEquals(expected, messageSender.send(headers));
        System.out.println();

    }

    // Тест метода byIP класса GeoServiceImpl
    @ParameterizedTest
    @ValueSource(strings = {"172.", "96."})
    void locationByIPTest(String ip) {
        GeoService geoService = new GeoServiceImpl();

        Country expected = ip.startsWith("172")
                ? Country.RUSSIA
                : Country.USA;

        Assertions.assertEquals(expected, geoService.byIp(ip).getCountry());
        Assertions.assertNull(geoService.byIp(""));
    }

    // Тест метода locale класса LocalizationServiceImpl
    @Test
    void localeTest() {
        LocalizationServiceImpl localizationService = new LocalizationServiceImpl();

        for (Country country : Country.values()) {
            String expected = country == Country.RUSSIA ? "Добро пожаловать" : "Welcome";
            Assertions.assertEquals(expected, localizationService.locale(country));
        }
    }

    // Тест метода byCoordinates класса GeoServiceImpl
    @Test
    void byCoordinatesTest() throws RuntimeException {
        final GeoServiceImpl geoService = new GeoServiceImpl();

        try {
            geoService.byCoordinates(53.87, 68.32);
        } catch (Exception exc) {
            Assertions.assertEquals("Not implemented", exc.getMessage());
        }
    }

}
