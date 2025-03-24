package ru.nexign.bootcamptask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UDRResponse {

    private  String msisdn;

    private CallInfo incomingCall;
    private CallInfo outcomingCall;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CallInfo {
        private String totalTime;
    }
}
