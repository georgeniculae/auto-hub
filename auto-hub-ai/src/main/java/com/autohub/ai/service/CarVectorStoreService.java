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
import java.sql.PreparedStatement;
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

    public void replaceAllCars(List<AvailableCarDetails> cars) {
        if (cars.isEmpty()) {
            log.warn("Empty snapshot received for reindexing; leaving the vector store untouched");

            return;
        }

        addCars(cars);
        deleteCarsNotIn(cars);
    }

    public void addCars(List<AvailableCarDetails> cars) {
        List<Document> carDocuments = getCarDocuments(cars);

        vectorStore.add(carDocuments);

        log.info("Car(s) with id(s) {} added to vector store", getCarIds(cars));
    }

    public void deleteCar(Long carId) {
        vectorStore.delete(List.of(toDocumentId(carId)));

        log.info("Car with id {} removed from vector store", carId);
    }

    public List<Document> searchSimilarCars(String queryText, int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(queryText)
                        .topK(topK)
                        .build()
        );
    }

    private void deleteCarsNotIn(List<AvailableCarDetails> cars) {
        List<String> carIds = getCarIds(cars);

        int removed = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("DELETE FROM " + tableName + " WHERE id <> ALL (?)");
            preparedStatement.setArray(1, connection.createArrayOf("uuid", carIds.toArray()));

            return preparedStatement;
        });

        log.info("Removed {} car(s) no longer available from vector store", removed);
    }

    private List<String> getCarIds(List<AvailableCarDetails> cars) {
        return cars.stream()
                .map(availableCarDetails -> toDocumentId(availableCarDetails.id()))
                .toList();
    }

    private List<Document> getCarDocuments(List<AvailableCarDetails> cars) {
        return cars.stream()
                .map(this::buildDocument)
                .toList();
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
