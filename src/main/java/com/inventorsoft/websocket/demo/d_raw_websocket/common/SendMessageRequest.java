package com.inventorsoft.websocket.demo.d_raw_websocket.common;

import lombok.Value;

@Value
public class SendMessageRequest {

    String message;
    String recipient;

}
