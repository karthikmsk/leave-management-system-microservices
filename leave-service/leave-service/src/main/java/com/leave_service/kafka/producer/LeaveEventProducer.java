package com.leave_service.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.leave_service.kafka.event.LeaveEvent;


@Service
public class LeaveEventProducer {

    private static final String TOPIC = "leave-events";

    private final KafkaTemplate<String, LeaveEvent> kafkaTemplate;

    public LeaveEventProducer(KafkaTemplate<String, LeaveEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishLeaveEvent(LeaveEvent event){
        kafkaTemplate.send(TOPIC, String.valueOf(event.getLeaveRequestId()), event);
    }
}
