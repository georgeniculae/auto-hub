package com.autohub.autohubmcp.tool;

import com.autohub.autohubmcp.service.CarService;
import com.autohub.autohubmcp.util.TestUtil;
import com.autohub.dto.agency.CarResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarToolsTest {

    @InjectMocks
    private CarTools carTools;

    @Mock
    private CarService carService;

    @Test
    void getAvailableCarsTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.getAllAvailableCars()).thenReturn(List.of(carResponse));

        List<CarResponse> actualCars = carTools.getAvailableCars();

        assertEquals(1, actualCars.size());
        assertEquals("Volkswagen", actualCars.getFirst().make());
    }

    @Test
    void getAvailableCarsTest_noCarsAvailable() {
        when(carService.getAllAvailableCars()).thenReturn(List.of());

        assertTrue(carTools.getAvailableCars().isEmpty());
    }

}
