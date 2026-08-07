package com.autohub.ai.service;

import com.autohub.dto.agency.BodyCategory;
import com.autohub.dto.ai.AvailableCarDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CarVectorStoreServiceTest {

    @InjectMocks
    private CarVectorStoreService carVectorStoreService;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Captor
    private ArgumentCaptor<List<Document>> captor;

    @Test
    void addCarTest_contentDescribesTheCar() {
        carVectorStoreService.addCar(car());

        verify(vectorStore).add(captor.capture());

        assertEquals(
                "Volkswagen Golf Hatchback from 2010, black, 250000 km, price 500 per day, located in Ploiesti",
                captor.getValue().getFirst().getText()
        );
    }

    @Test
    void addCarTest_metadataCarriesTheCarId() {
        carVectorStoreService.addCar(car());

        verify(vectorStore).add(captor.capture());

        assertEquals(1L, captor.getValue().getFirst().getMetadata().get("carId"));
        assertTrue(captor.getValue().getFirst().getMetadata().containsKey("carLocation"));
    }

    @Test
    void deleteAllCarsTest_clearsTheTable() {
        carVectorStoreService.deleteAllCars();

        verify(jdbcTemplate).update(anyString());
    }

    private static AvailableCarDetails car() {
        return AvailableCarDetails.builder()
                .id(1L)
                .make("Volkswagen")
                .model("Golf")
                .bodyCategory(BodyCategory.HATCHBACK)
                .yearOfProduction(2010)
                .color("black")
                .mileage(250000)
                .amount(BigDecimal.valueOf(500))
                .carLocation("Ploiesti")
                .build();
    }

}
