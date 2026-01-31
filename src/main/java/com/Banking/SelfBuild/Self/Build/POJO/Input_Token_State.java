package com.Banking.SelfBuild.Self.Build.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Input_Token_State
{
    @JsonProperty(value = "token_type")
    private String token_type;

    @JsonProperty(value = "tokenId")
    private String tokenId;

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return "Input_Token_State{" +
                "token_type='" + token_type + '\'' +
                ", tokenId='" + tokenId + '\'' +
                '}';
    }
}
