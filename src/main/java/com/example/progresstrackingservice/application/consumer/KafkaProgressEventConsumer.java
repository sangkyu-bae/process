//package com.example.progresstrackingservice.application.consumer;
//
//import com.example.progresstrackingservice.domain.model.ProgressEvent;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class KafkaProgressEventConsumer {
//    private final ObjectMapper objectMapper;
//    private final KafkaTemplate<String,String> kafkaTemplate;
//
//    @KafkaListener(topics = "${kafka.process.topic}")
//    public void listener(String event){
//        ProgressEvent progressEvent = null;
//
//        try{
//            progressEvent = objectMapper.readValue(event, ProgressEvent.class);
//        }catch (Exception e){
//
//        }
//    }
//}
