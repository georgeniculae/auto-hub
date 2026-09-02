package com.autohub.ai.service;

import com.autohub.dto.agency.BodyCategory;
import com.autohub.dto.ai.AvailableCarDetails;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class CarVectorStoreIntegrationTest {

    private static final int DIMENSIONS = 768;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName
                    .parse("pgvector/pgvector:pg18")
                    .asCompatibleSubstituteFor("postgres")
    );

    @Autowired
    private CarVectorStoreService carVectorStoreService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private CarService carService;

    @TestConfiguration
    static class StubEmbeddingConfig {

        @Bean
        @Primary
        EmbeddingModel stubEmbeddingModel() {
            return new EmbeddingModel() {

                @Override
                public float @NonNull [] embed(@NonNull Document document) {
                    assert document.getText() != null;

                    return computeVector(document.getText());
                }

                private float[] computeVector(String text) {
                    float[] vector = new float[DIMENSIONS];

                    for (String token : text.toLowerCase().split("\\W+")) {
                        if (!token.isBlank()) {
                            vector[Math.abs(token.hashCode()) % DIMENSIONS] = 1.0F;
                        }
                    }

                    return vector;
                }

                @Override
                public int dimensions() {
                    return DIMENSIONS;
                }

                @Override
                @NonNull
                public EmbeddingResponse call(@NonNull EmbeddingRequest request) {
                    List<Embedding> embeddings = request.getInstructions()
                            .stream()
                            .map(this::computeVector)
                            .map(vector -> new Embedding(vector, 0))
                            .toList();

                    return new EmbeddingResponse(embeddings);
                }
            };
        }

    }

    @AfterEach
    void clearVectorStore() {
        jdbcTemplate.update("DELETE FROM vector_store");
    }

    @Test
    void migrationCreatesVectorStoreTable() {
        Boolean exists = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM information_schema.tables
                    WHERE table_schema = 'public' AND table_name = 'vector_store'
                )""", Boolean.class);

        assertEquals(Boolean.TRUE, exists);
    }

    @Test
    void migrationCreatesHnswIndex() {
        Boolean exists = jdbcTemplate.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1 FROM pg_indexes
                            WHERE tablename = 'vector_store' AND indexname = 'idx_vector_store_embedding'
                        )""",
                Boolean.class
        );

        assertEquals(Boolean.TRUE, exists);
    }

    @Test
    void addCarThenSearchReturnsIt() {
        carVectorStoreService.addCars(List.of(getAvailableCarDetails(1L, "Ploiesti")));

        List<Document> found = carVectorStoreService.searchSimilarCars("Volkswagen Golf in Ploiesti", 5);

        assertFalse(found.isEmpty());
    }

    @Test
    void addingSameCarTwiceKeepsOneRow() {
        carVectorStoreService.addCars(List.of(getAvailableCarDetails(1L, "Ploiesti")));
        carVectorStoreService.addCars(List.of(getAvailableCarDetails(1L, "Ploiesti")));

        assertEquals(1L, countRows());
    }

    @Test
    void replaceAllCarsTest_removesCarsMissingFromTheSnapshot() {
        carVectorStoreService.addCars(List.of(
                getAvailableCarDetails(1L, "Ploiesti"),
                getAvailableCarDetails(2L, "Bucuresti")
        ));

        carVectorStoreService.replaceAllCars(List.of(getAvailableCarDetails(1L, "Ploiesti")));

        assertEquals(1L, countRows());
        assertEquals(List.of("1"), getCarIdsInStore());
    }

    @Test
    void replaceAllCarsTest_updatesCarsPresentInTheSnapshot() {
        carVectorStoreService.addCars(List.of(getAvailableCarDetails(1L, "Ploiesti")));
        carVectorStoreService.replaceAllCars(List.of(getAvailableCarDetails(1L, "Cluj")));

        assertEquals(1L, countRows());
        assertTrue(getContentFor(1L).contains("Cluj"));
    }

    @Test
    void replaceAllCarsTest_emptySnapshotLeavesTheStoreUntouched() {
        carVectorStoreService.addCars(List.of(getAvailableCarDetails(1L, "Ploiesti")));
        carVectorStoreService.replaceAllCars(List.of());

        assertEquals(1L, countRows(), "un snapshot gol nu poate ajunge aici decat printr-un bug; nu golim indexul");
    }

    @Test
    void addCarsTest_updatesOneCarWithoutRemovingTheOthers() {
        carVectorStoreService.addCars(List.of(
                getAvailableCarDetails(1L, "Ploiesti"),
                getAvailableCarDetails(2L, "Bucuresti")
        ));

        carVectorStoreService.addCars(List.of(getAvailableCarDetails(1L, "Cluj")));

        assertEquals(2L, countRows());
        assertTrue(getContentFor(1L).contains("Cluj"));
    }

    @Test
    void deleteCarRemovesTheRow() {
        carVectorStoreService.addCars(List.of(getAvailableCarDetails(1L, "Ploiesti")));
        carVectorStoreService.deleteCar(1L);

        assertEquals(0L, countRows());
    }

    private List<String> getCarIdsInStore() {
        return jdbcTemplate.queryForList("SELECT metadata->>'carId' FROM vector_store", String.class);
    }

    private String getContentFor(Long carId) {
        return jdbcTemplate.queryForObject(
                "SELECT content FROM vector_store WHERE metadata->>'carId' = ?",
                String.class,
                carId.toString()
        );
    }

    private AvailableCarDetails getAvailableCarDetails(Long id, String location) {
        return AvailableCarDetails.builder()
                .id(id)
                .make("Volkswagen")
                .model("Golf")
                .bodyCategory(BodyCategory.HATCHBACK)
                .yearOfProduction(2010)
                .color("black")
                .mileage(250000)
                .amount(BigDecimal.valueOf(500))
                .carLocation(location)
                .build();
    }

    private long countRows() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM vector_store", Long.class);
    }

}
