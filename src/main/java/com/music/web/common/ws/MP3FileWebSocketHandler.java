package com.music.web.common.ws;

// MP3FileWebSocketHandler.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MP3FileWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MP3FileWebSocketHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyFileListUpdated() {
        messagingTemplate.convertAndSend("/topic/filesUpdated", true);
    }
}

