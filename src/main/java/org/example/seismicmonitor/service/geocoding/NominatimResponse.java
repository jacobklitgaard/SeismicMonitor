package org.example.seismicmonitor.service.geocoding;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class NominatimResponse {

    @JsonProperty("display_name")
    private String displayName;
}