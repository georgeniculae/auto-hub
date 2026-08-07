package com.autohub.ai.service;

import com.autohub.dto.ai.CarSuggestionResponse;
import com.autohub.dto.ai.TripInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CarSuggestionService {

    private static final int TOP_K = 10;

    private final ChatService chatService;
    private final CarVectorStoreService carVectorStoreService;

    public CarSuggestionResponse getChatOutput(TripInfo tripInfo) {
        List<Document> documents = carVectorStoreService.searchSimilarCars(buildQueryText(tripInfo), TOP_K);

        List<String> cars = documents.stream()
                .map(Document::getText)
                .toList();

        return chatService.getChatReply(getText(), getParams(tripInfo, cars));
    }

    private String buildQueryText(TripInfo tripInfo) {
        return "Car rental for %d people starting from %s traveling to %s, Romania in %s for a %s trip".formatted(
                tripInfo.peopleCount(),
                tripInfo.startLocation(),
                tripInfo.destination(),
                getMonth(tripInfo.tripDate()),
                tripInfo.tripKind()
        );
    }

    private String getText() {
        return """
                Which car from the following list {cars} is more suitable for rental from a rental car
                agency for a trip for {peopleCount} people starting from {startLocation} to {destination},
                Romania in {month}? The car will be used for {tripKind}.""";
    }

    private Map<String, Object> getParams(TripInfo tripInfo, List<String> cars) {
        return Map.of(
                "cars", cars,
                "peopleCount", tripInfo.peopleCount(),
                "startLocation", tripInfo.startLocation(),
                "destination", tripInfo.destination(),
                "month", getMonth(tripInfo.tripDate()),
                "tripKind", tripInfo.tripKind()
        );
    }

    private String getMonth(LocalDate tripDate) {
        return tripDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

}
