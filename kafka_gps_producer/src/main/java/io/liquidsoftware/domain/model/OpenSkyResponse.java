package io.liquidsoftware.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Date;
import java.util.List;

/**
 * Created by erikdreyer on 3/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = OpenSkyResponse.OpenSkyResponseBuilder.class)
@Builder
@Value
@ToString
public class OpenSkyResponse {

    private Date time;
    private List<List<Object>> states;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class OpenSkyResponseBuilder {}

}
