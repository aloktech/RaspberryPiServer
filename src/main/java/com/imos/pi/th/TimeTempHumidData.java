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
import java.io.IOException;
import lombok.Data;

/**
 *
 * @author Pintu
 */
@Data
@JsonPropertyOrder({"data", "time"})
public class TimeTempHumidData implements DataSerializable {

    @JsonProperty("data")
    private TempHumidData data;

    @JsonProperty("time")
    private long time;

    public TimeTempHumidData() {

    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(data);
        out.writeLong(time);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        data = in.readObject();
        time = in.readLong();
    }
}
