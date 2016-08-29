/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Pintu
 */
@Getter
@Setter
@JsonPropertyOrder({"data", "time"})
@EqualsAndHashCode(exclude = "data")
public class TimeTempHumidData implements Serializable {

    @JsonProperty("data")
    private TempHumidData data;

    @JsonProperty("time")
    private long time;
}
