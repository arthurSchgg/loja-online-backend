package br.com.lojinhadakatia.security;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdminEmailOutboxDeliveryTest {
    @Test void unreadableMessageDoesNotStopTheRemainingQueue() {
        var repository=mock(AdminEmailOutboxRepository.class);
        @SuppressWarnings("unchecked") ObjectProvider<JavaMailSender> provider=mock(ObjectProvider.class);
        var sender=mock(JavaMailSender.class);
        when(provider.getIfAvailable()).thenReturn(sender);
        when(provider.getObject()).thenReturn(sender);
        // A deterministic test-only key, never a deployment credential.
        var service=new AdminEmailOutboxService(repository,provider,
            Base64.getEncoder().encodeToString(new byte[32]),"no-reply@example.invalid");
        service.enqueue("recipient@example.invalid","Notificação fictícia","Mensagem de teste");
        var captured=ArgumentCaptor.forClass(AdminEmailOutbox.class);
        verify(repository).save(captured.capture());
        var valid=captured.getValue();
        var unreadable=new AdminEmailOutbox("recipient@example.invalid","Incorreta","bad-ciphertext");
        when(repository.pending(any(Instant.class),any(Pageable.class))).thenReturn(List.of(unreadable,valid));
        service.deliverPending();
        assertEquals(1,unreadable.getAttempts());
        assertNull(unreadable.getSentAt());
        assertNotNull(valid.getSentAt());
        verify(sender,times(1)).send(any(SimpleMailMessage.class));
    }
}
