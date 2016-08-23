/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.th;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import static com.imos.pi.common.RaspberryPiConstant.*;
import java.io.IOException;
import lombok.Data;

/**
 *
 * @author Pintu
 */
@Data
@JsonPropertyOrder({"temp", "humid"})
public class TempHumidData implements DataSerializable {

    @JsonProperty(TEMP)
    private double temperature;

    @JsonProperty(HUMID)
    private double humidity;

    public TempHumidData() {

    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeDouble(temperature);
        out.writeDouble(humidity);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        temperature = in.readDouble();
        humidity = in.readDouble();
    }
}
