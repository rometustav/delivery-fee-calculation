package ee.romet.deliveryfeecalculation.scheduler;

import ee.romet.deliveryfeecalculation.model.entity.WeatherObservation;
import ee.romet.deliveryfeecalculation.parser.WeatherXmlParser;
import ee.romet.deliveryfeecalculation.repository.WeatherObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class WeatherImportJob {

    private static final Logger log = LoggerFactory.getLogger(WeatherImportJob.class);

    private final WeatherXmlParser parser;
    private final WeatherObservationRepository repository;

    public WeatherImportJob(WeatherXmlParser parser, WeatherObservationRepository repository) {
        this.parser = parser;
        this.repository = repository;
    }

    /**
     * Fetches weather data from ilmateenistus.ee and saves
     * observations for tracked stations to the database.
     *
     * Runs on a cron scheduler, configurable in application.properties.
     */
    @Scheduled(cron = "${weather.cron}")
    public void importWeather() {
        log.info("Starting weather import");
        try {
            InputStream stream = URI.create("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php").toURL().openStream();
            List<WeatherObservation> observations = parser.parse(stream, LocalDateTime.now());
            repository.saveAll(observations);
            log.info("Weather import complete, saved {} observations", observations.size());
        } catch (Exception e) {
            log.error("Weather import failed", e);
        }
    }
}