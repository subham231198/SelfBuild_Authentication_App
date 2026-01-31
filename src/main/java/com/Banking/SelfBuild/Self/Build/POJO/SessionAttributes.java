package com.Banking.SelfBuild.Self.Build.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionAttributes
{
    @NonNull
    @JsonProperty(value = "tokenId")
    private String tokenId;

    public @NonNull String getTokenId() {
        return tokenId;
    }

    public void setTokenId(@NonNull String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return "Incoming Request{" +
                "tokenId='" + tokenId + '\'' +
                '}';
    }
}
