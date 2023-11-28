package com.example.individuell.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ScheduleSessionCheck {



 /*   @Scheduled(fixedDelay = 3000)
    public void cookieHandler(){
        *//*HttpServletRequest request = getCurrentRequest();*//*
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies){
            if (cookie.getName().equals("duration")){
                long timeRemaining = cookie.getMaxAge() * 1000L;
                if (timeRemaining < System.currentTimeMillis())
                    request.getSession().invalidate();
                *//*response.sendRedirect("");*//*
                System.out.println("session invalidated");
            }
        }
    }*/

    /*private HttpServletRequest getCurrentRequest(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null){
            return attributes.getRequest();
        }
    }*/
}
