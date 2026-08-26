package com.autohub.autohubmcp.tool;

import com.autohub.autohubmcp.service.CarService;
import com.autohub.dto.agency.CarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CarTools {

    private final CarService carService;

    @McpTool(
            name = "get_available_cars",
            description = """
                    Returns the cars currently available for rental at Auto Hub. Takes no arguments.
                    Each car includes its id, make, model, body category, year of production, colour,
                    mileage, rental amount, current state (carState), the city where the car currently is
                    (carLocation), and the numeric identifiers of its home rental office
                    (initialRentalOfficeId) and its current rental office (actualRentalOfficeId) - these
                    are database IDs, not rental office names.
                    Returns an empty list when no car is available.""",
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = true,
                    destructiveHint = false,
                    idempotentHint = true,
                    openWorldHint = false
            )
    )
    public List<CarResponse> getAvailableCars() {
        return carService.getAllAvailableCars();
    }

}
