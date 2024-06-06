package com.bridge.herofincorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
    List<QuestionDetail> questions;
}
