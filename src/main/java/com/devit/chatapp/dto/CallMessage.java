package com.devit.chatapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CallMessage {
    private String senderId;
    private String receiverId;
    private String type; // "offer", "answer", "ice-candidate"
    private String sdp; // para offer/answer
    private String candidate; // para ICE
    private String sdpMid;
    private int sdpMLineIndex;
}
