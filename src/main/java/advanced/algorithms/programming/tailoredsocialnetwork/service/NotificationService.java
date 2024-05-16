package advanced.algorithms.programming.tailoredsocialnetwork.service;

import advanced.algorithms.programming.tailoredsocialnetwork.entity.Validation;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificationService {
    private final JavaMailSender javaMailSender;

    public void sendActivationCodeEmail(Validation validation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@tailored-social-network.com");
        message.setTo(validation.getUser().getEmail());
        message.setSubject("Tailored Social Network activation code");

        String text = "Here's the activation code to create your Tailored Social Network activation account\n"
            + validation.getCode()
            + "\nThis code is only valid for 10 minutes";
        message.setText(text);

        this.javaMailSender.send(message);
    }

    public void sendPasswordResetEmail(Validation validation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@tailored-social-network.com");
        message.setTo(validation.getUser().getEmail());
        message.setSubject("Tailored Social Network password reset code");

        String text = "Here's the code to reset your Tailored Social Network account password\n"
            + validation.getCode()
            + "\nThis code is only valid for 10 minutes";
        message.setText(text);

        this.javaMailSender.send(message);
    }
}
