package ru.moscow.hackathon.coordinator.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.moscow.hackathon.coordinator.enums.OperationType;
import ru.moscow.hackathon.coordinator.repository.CoordinatedRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class CommonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Bean
    public Map<OperationType, CoordinatedRepository> coordinatedRepositoryMap(
            @Autowired List<CoordinatedRepository> repositoryList
    ) {
        Map<OperationType, CoordinatedRepository> map = new HashMap<>();
        repositoryList.forEach(
                it -> map.put(it.myType(), it)
        );
        return map;
    }
}
