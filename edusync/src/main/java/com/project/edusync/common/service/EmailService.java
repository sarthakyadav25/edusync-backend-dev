package com.project.edusync.common.service;

import com.project.edusync.iam.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    /**
     * Sends a password reset email to a user.
     * This implementation should be asynchronous.
     *
     * @param user The user to send the email to.
     * @param token The single-use password reset token.
     */
    void sendPasswordResetEmail(User user, String token);
// TODO:
    // We can add other email methods here later, e.g.:
    // void sendWelcomeEmail(User user);
    // void sendInvoiceEmail(Invoice invoice, User user);
}