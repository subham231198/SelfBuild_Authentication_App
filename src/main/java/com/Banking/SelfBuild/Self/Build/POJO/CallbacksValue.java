package com.Banking.SelfBuild.Self.Build.POJO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallbacksValue
{
    @JsonProperty
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CallbacksValue{" +
                "value='" + value + '\'' +
                '}';
    }
}
