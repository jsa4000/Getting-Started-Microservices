package com.example.gateway.config.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityProperties {
    private String name = StringUtils.EMPTY;
    private String secret = StringUtils.EMPTY;
}
