package com.bridge.herofincorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class ELMErrorResponse {
    private String message;
    private String errorCode;
    ArrayList< Object > fields = new ArrayList < Object > ();
}
