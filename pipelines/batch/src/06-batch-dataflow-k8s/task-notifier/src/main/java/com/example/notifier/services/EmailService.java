package com.example.notifier.services;

public interface EmailService {
    void sendMail(String from, String to, String subject, String msg);
}
