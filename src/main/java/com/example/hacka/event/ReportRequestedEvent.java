package com.example.hacka.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReportRequestedEvent extends ApplicationEvent {
    private final String requestId;

    public ReportRequestedEvent(Object source, String requestId) {
        super(source);
        this.requestId = requestId;
    }
}
