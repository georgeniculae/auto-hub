package com.autohub.ai.service;

import com.autohub.dto.ai.AvailableCarDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarVectorStoreService {

    private static final String CAR_ID = "carId";
    private static final String DOCUMENT_ID_PREFIX = "car-";

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.ai.vectorstore.pgvector.table-name:vector_store}")
    private String tableName;

    public void addCar(AvailableCarDetails car) {
        vectorStore.delete(List.of(toDocumentId(car.id())));
        vectorStore.add(List.of(buildDocument(car)));

        log.info("Car with id {} added to vector store", car.id());
    }

    public void deleteCar(Long carId) {
        vectorStore.delete(List.of(toDocumentId(carId)));

        log.info("Car with id {} removed from vector store", carId);
    }

    public void deleteAllCars() {
        jdbcTemplate.update("DELETE FROM " + tableName);

        log.info("Vector store cleared");
    }

    public List<Document> searchSimilarCars(String queryText, int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(queryText)
                        .topK(topK)
                        .build()
        );
    }

    private String toDocumentId(Long carId) {
        return UUID.nameUUIDFromBytes((DOCUMENT_ID_PREFIX + carId).getBytes(StandardCharsets.UTF_8)).toString();
    }

    private Document buildDocument(AvailableCarDetails car) {
        return Document.builder()
                .id(toDocumentId(car.id()))
                .text(buildCarText(car))
                .metadata(buildMetadata(car))
                .build();
    }

    private Map<String, Object> buildMetadata(AvailableCarDetails car) {
        return Map.of(
                CAR_ID, car.id(),
                "make", car.make(),
                "model", car.model(),
                "bodyCategory", car.bodyCategory().name(),
                "yearOfProduction", car.yearOfProduction(),
                "color", car.color(),
                "amount", car.amount().toString(),
                "carLocation", car.carLocation()
        );
    }

    private String buildCarText(AvailableCarDetails car) {
        return "%s %s %s from %d, %s, %d km, price %s per day, located in %s".formatted(
                car.make(),
                car.model(),
                car.bodyCategory().getDisplayName(),
                car.yearOfProduction(),
                car.color(),
                car.mileage(),
                car.amount(),
                car.carLocation()
        );
    }

}
