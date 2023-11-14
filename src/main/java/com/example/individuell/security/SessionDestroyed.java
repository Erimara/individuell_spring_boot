package com.example.individuell.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.events.SessionDestroyedEvent;

public class SessionDestroyed implements ApplicationListener<SessionDestroyedEvent> {
    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {

    }
    private void sessionLogOut(Object principal){
        if (principal instanceof SecurityContext){

        }
    }
}
