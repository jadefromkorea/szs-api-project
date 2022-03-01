package com.api.szsapiproject.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
/**
 *
 */
public class Result {
    String appVer;
    String hostNm;
    String workerReqDt;
    String workerResDt;

    JsonList jsonList;
}
