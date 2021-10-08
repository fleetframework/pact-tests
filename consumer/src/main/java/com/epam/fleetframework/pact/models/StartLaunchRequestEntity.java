package com.epam.fleetframework.pact.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartLaunchRequestEntity {

    private String name;
    private String mode;
    private String startTime;
}
