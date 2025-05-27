package com.autohub.dto.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CarState {

    NOT_AVAILABLE("Not available"),
    BROKEN("Broken"),
    IN_REPAIR("In repair"),
    IN_SERVICE("In service"),
    AVAILABLE("Available");

    private final String displayName;

}
