package com.example.launcher.services;

public interface EmailService {
    void sendMail(String from, String to, String subject, String msg);
}
