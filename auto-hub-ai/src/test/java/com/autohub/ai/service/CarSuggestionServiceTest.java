package com.autohub.ai.service;

import com.autohub.dto.ai.CarSuggestionResponse;
import com.autohub.dto.ai.TripInfo;
import com.autohub.ai.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarSuggestionServiceTest {

    @InjectMocks
    private CarSuggestionService carSuggestionService;

    @Mock
    private CarVectorStoreService carVectorStoreService;

    @Mock
    private ChatService chatService;

    @Test
    void getChatOutputTest_success() {
        TripInfo tripInfo = TestUtil.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        CarSuggestionResponse expected =
                TestUtil.getResourceAsJson("/data/CarSuggestionResponse.json", CarSuggestionResponse.class);

        Document document = Document.builder()
                .id("00000000-0000-0000-0000-000000000001")
                .text("Volkswagen Golf Hatchback from 2010, black, 250000 km, price 500 per day, located in Ploiesti")
                .build();

        when(carVectorStoreService.searchSimilarCars(anyString(), anyInt())).thenReturn(List.of(document));
        when(chatService.getChatReply(anyString(), anyMap())).thenReturn(expected);

        CarSuggestionResponse actual = carSuggestionService.getChatOutput(tripInfo);

        assertNotNull(actual);
    }

    @Test
    void getChatOutputTest_queryContainsStartLocation() {
        TripInfo tripInfo = TestUtil.getResourceAsJson("/data/TripInfo.json", TripInfo.class);
        CarSuggestionResponse expected =
                TestUtil.getResourceAsJson("/data/CarSuggestionResponse.json", CarSuggestionResponse.class);

        when(carVectorStoreService.searchSimilarCars(anyString(), anyInt())).thenReturn(List.of());
        when(chatService.getChatReply(anyString(), anyMap())).thenReturn(expected);

        carSuggestionService.getChatOutput(tripInfo);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(carVectorStoreService).searchSimilarCars(queryCaptor.capture(), anyInt());

        assertTrue(queryCaptor.getValue().contains(tripInfo.startLocation()));
        assertTrue(queryCaptor.getValue().contains(tripInfo.destination()));
    }

}
