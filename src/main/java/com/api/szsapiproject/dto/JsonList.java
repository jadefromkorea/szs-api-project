package com.api.szsapiproject.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Map;

@Getter
@Setter
@ToString
public class JsonList {
    String company;
    String userId;
    String svcCd;
    String errMsg;

    ArrayList<Map<String, String>> scrap001;
    ArrayList<Map<String, String>> scrap002;
}
