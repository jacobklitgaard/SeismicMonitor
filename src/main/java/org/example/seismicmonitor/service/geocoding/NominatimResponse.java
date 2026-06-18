package org.example.seismicmonitor.service.geocoding;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// maps JSON response from Nominatim to a Java-objekt
@Getter @Setter @NoArgsConstructor
public class NominatimResponse {

    @JsonProperty("display_name")
    private String displayName;
}