package len.project.RapiraSpringBot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RegistrationDateService {
    private static final Logger logger = Logger.getLogger(RegistrationDateService.class.getName());

    private Map<String, Integer> registrationDates;
    private Function function;

    @Value("${data.file.path}")
    private String dataFilePath;

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Используем путь к файлу для десериализации JSON
            File dataFile = new File(dataFilePath);
            if (!dataFile.exists()) {
                logger.log(Level.SEVERE, "Data file not found: " + dataFilePath);
                return;
            }

            registrationDates = mapper.readValue(dataFile, mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class));
            logger.log(Level.INFO, "Loaded registration dates: " + registrationDates);

            // Инициализируем функцию интерполяции
            function = new Function(registrationDates);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load data file: " + dataFilePath, e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during initialization", e);
        }
    }

    public String getRegistrationDate(Long id) {
        Integer timestamp = registrationDates.get(String.valueOf(id));
        if (timestamp != null) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(timestamp * 1000L));
        } else {
            // Используем функцию интерполяции для оценки даты регистрации
            long estimatedTimestamp = function.estimateRegistrationDate(id);
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(estimatedTimestamp * 1000L));
        }
    }
}
