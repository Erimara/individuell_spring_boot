package com.example.individuell.session;

import org.springframework.context.event.EventListener;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.stereotype.Component;

@Component
public class SessionEventListener {
    public SessionEventListener() {
        System.out.println("SessionEventListener bean created");
    }

    @EventListener
    public void processSessionCreatedEvent(SessionCreatedEvent event) {
        SessionCreatedEvent sess = event.getSession();
        String sess_id = event.getSessionId();
        System.out.println(sess + "lightweeeeight");
        System.out.println(sess_id + "yeeee baby");

    }

    @EventListener
    public void processSessionDeletedEvent(SessionDeletedEvent event) {
        System.out.println(event + "deleted");
    }

    @EventListener
    public void processSessionDestroyedEvent(SessionDestroyedEvent event) {
        System.out.println(event + "destroyed");
    }

    @EventListener
    public void processSessionExpiredEvent(SessionExpiredEvent event) {
        System.out.println(event + "expired");
    }

}