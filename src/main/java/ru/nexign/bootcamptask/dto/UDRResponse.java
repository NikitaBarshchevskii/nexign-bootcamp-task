package ru.nexign.bootcamptask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для представления UDR-отчёта по абоненту.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UDRResponse {

    /**
     * Номер абонента.
     */
    private String msisdn;

    /**
     * Данные по входящим вызовам.
     */
    private CallInfo incomingCall;

    /**
     * Данные по исходящим вызовам.
     */
    private CallInfo outcomingCall;

    /**
     * Вложенный класс с информацией о суммарной длительности вызовов.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CallInfo {
        /**
         * Суммарная длительность в формате HH:mm:ss.
         */
        private String totalTime;
    }
}

