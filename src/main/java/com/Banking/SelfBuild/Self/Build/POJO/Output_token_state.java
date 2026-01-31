package com.Banking.SelfBuild.Self.Build.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Output_token_state
{
    @JsonProperty(value = "token_type")
    private String token_type;

    @JsonProperty(value = "subject_confirmation")
    private String subject_confirmation;

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getSubject_confirmation() {
        return subject_confirmation;
    }

    public void setSubject_confirmation(String subject_confirmation) {
        this.subject_confirmation = subject_confirmation;
    }

    @Override
    public String toString() {
        return "Output_token_state{" +
                "token_type='" + token_type + '\'' +
                ", subject_confirmation='" + subject_confirmation + '\'' +
                '}';
    }
}
