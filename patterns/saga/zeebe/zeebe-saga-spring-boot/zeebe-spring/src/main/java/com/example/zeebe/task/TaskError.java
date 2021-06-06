package com.example.zeebe.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskError {

    public static final TaskError INPUT_ERROR = new TaskError("001", "Expected Input from Job");

    String code;
    String message;

}
