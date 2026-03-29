package ee.romet.deliveryfeecalculation.parser;

import ee.romet.deliveryfeecalculation.model.entity.WeatherObservation;
import ee.romet.deliveryfeecalculation.model.enums.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class WeatherXmlParser {

    private static final Logger log = LoggerFactory.getLogger(WeatherXmlParser.class);

    private static final List<String> trackedStations = Arrays.stream(City.values())
            .map(City::getStationName)
            .toList();

    /**
     * Parses weather XML input stream and returns observations
     * for the tracked stations.
     *
     * @param inputStream   XML data from ilmateenistus.ee/ilma_andmed/xml/observations.php
     * @param timestamp     time of observation batch
     * @return              list of WeatherObservation entities
     */
    public List<WeatherObservation> parse(InputStream inputStream, LocalDateTime timestamp) throws Exception {
        List<WeatherObservation> observations = new ArrayList<>();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        doc.getDocumentElement().normalize();

        NodeList stations = doc.getElementsByTagName("station");

        for (int i = 0; i < stations.getLength(); i++) {
            Element station = (Element) stations.item(i);
            String name = getText(station, "name");

            if (!trackedStations.contains(name)) continue;

            WeatherObservation obs = new WeatherObservation();
            obs.setStationName(name);
            obs.setWmoCode(getText(station, "wmocode"));
            obs.setAirTemperature(parseDouble(getText(station, "airtemperature")));
            obs.setWindSpeed(parseDouble(getText(station, "windspeed")));
            obs.setWeatherPhenomenon(getText(station, "phenomenon"));
            obs.setTimestamp(timestamp);

            observations.add(obs);
        }

        log.info("Parsed {} weather observations", observations.size());

        return observations;
    }

    private String getText(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return null;

        String value = nodes.item(0).getTextContent().trim();

        return value.isEmpty() ? null : value;
    }

    private Double parseDouble(String value) {
        if (value == null) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}