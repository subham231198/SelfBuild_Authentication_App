package com.Banking.SelfBuild.Self.Build.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernameAuthRequest
{
    @JsonProperty(value = "callbacklogInput")
    private CallbacksInput callbacksInput;

    @JsonProperty(value = "callbacklogValue")
    private CallbacksValue callbacksValue;

    public CallbacksInput getCallbacksInput() {
        return callbacksInput;
    }

    public void setCallbacksInput(CallbacksInput callbacksInput) {
        this.callbacksInput = callbacksInput;
    }

    public CallbacksValue getCallbacksValue() {
        return callbacksValue;
    }

    public void setCallbacksValue(CallbacksValue callbacksValue) {
        this.callbacksValue = callbacksValue;
    }

    @Override
    public String toString() {
        return "UsernameAuthRequest {" +
                "callbacksInput=" + callbacksInput +
                ", callbacksValue=" + callbacksValue +
                '}';
    }
}
