package io.liquidsoftware.domain.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Date;

/**
 * Created by erikdreyer on 3/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = FlightEvent.FlightEventBuilder.class)
@Builder
@Value
@ToString
public class FlightEvent {

    String icao24;
    Date timestamp;
    Double latitude;
    Double longitude;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class FlightEventBuilder {}

}
