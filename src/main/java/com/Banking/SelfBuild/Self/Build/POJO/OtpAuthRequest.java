package com.Banking.SelfBuild.Self.Build.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpAuthRequest
{
    @JsonProperty(value = "callbacksInput1")
    private CallbacksInput callbacksInput_customerId;

    @JsonProperty(value = "callbacksValue1")
    private CallbacksValue callbacksValue_customerId;

    @JsonProperty(value = "callbacksInput2")
    private CallbacksInput callbacksInput_accessKey;

    @JsonProperty(value = "callbacksValue2")
    private CallbacksValue callbacksValue_accessKeyValue;

    public CallbacksInput getCallbacksInput_customerId() {
        return callbacksInput_customerId;
    }

    public void setCallbacksInput_customerId(CallbacksInput callbacksInput_customerId) {
        this.callbacksInput_customerId = callbacksInput_customerId;
    }

    public CallbacksValue getCallbacksValue_customerId() {
        return callbacksValue_customerId;
    }

    public void setCallbacksValue_customerId(CallbacksValue callbacksValue_customerId) {
        this.callbacksValue_customerId = callbacksValue_customerId;
    }

    public CallbacksInput getCallbacksInput_accessKey() {
        return callbacksInput_accessKey;
    }

    public void setCallbacksInput_accessKey(CallbacksInput callbacksInput_accessKey) {
        this.callbacksInput_accessKey = callbacksInput_accessKey;
    }

    public CallbacksValue getCallbacksValue_accessKeyValue() {
        return callbacksValue_accessKeyValue;
    }

    public void setCallbacksValue_accessKeyValue(CallbacksValue callbacksValue_accessKeyValue) {
        this.callbacksValue_accessKeyValue = callbacksValue_accessKeyValue;
    }


    @Override
    public String toString() {
        return "OtpAuthRequest{" +
                "callbacksInput_customerId=" + callbacksInput_customerId +
                ", callbacksValue_customerId=" + callbacksValue_customerId +
                ", callbacksInput_accessKey=" + callbacksInput_accessKey +
                ", callbacksValue_accessKeyValue=" + callbacksValue_accessKeyValue +
                '}';
    }
}
