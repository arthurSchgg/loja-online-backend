package br.com.lojinhadakatia.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdminMailTransportTest {
    @SuppressWarnings("unchecked")
    @Test void sendsOnlyToFixedEndpointAndUsesStableIdempotencyKey() throws Exception {
        ObjectProvider<JavaMailSender> smtp=mock(ObjectProvider.class);
        var http=mock(HttpClient.class);
        HttpResponse<Void> response=mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200,200,503);
        when(http.send(any(HttpRequest.class),any(HttpResponse.BodyHandler.class))).thenReturn(response);
        var json=new ObjectMapper();
        var transport=new AdminMailTransport(smtp,json,"resend","no-reply@example.invalid","test-only-key",http);
        var outbox=new AdminEmailOutbox("recipient@example.invalid","Notificação fictícia","ciphertext-with-nonce");
        assertTrue(transport.configured());
        transport.send(outbox,"Mensagem fictícia");
        transport.send(outbox,"Mensagem fictícia");
        assertThrows(IllegalStateException.class,()->transport.send(outbox,"Mensagem fictícia"));
        var captured=ArgumentCaptor.forClass(HttpRequest.class);
        verify(http,times(3)).send(captured.capture(),any(HttpResponse.BodyHandler.class));
        var requests=captured.getAllValues();
        assertEquals("https://api.resend.com/emails",requests.get(0).uri().toString());
        assertEquals("POST",requests.get(0).method());
        assertEquals(requests.get(0).headers().firstValue("Idempotency-Key"),requests.get(1).headers().firstValue("Idempotency-Key"));
        assertEquals(requests.get(0).headers().firstValue("Idempotency-Key"),requests.get(2).headers().firstValue("Idempotency-Key"));
        assertTrue(requests.get(0).headers().firstValue("Idempotency-Key").orElseThrow().startsWith("katia-admin-"));
        var payload=json.readTree(readBody(requests.get(0)));
        assertEquals("no-reply@example.invalid",payload.get("from").asText());
        assertEquals("recipient@example.invalid",payload.get("to").get(0).asText());
        assertEquals("Mensagem fictícia",payload.get("text").asText());
        verifyNoInteractions(smtp);
    }

    @SuppressWarnings("unchecked")
    @Test void missingConfigurationDoesNotFallbackOrTransmit() {
        ObjectProvider<JavaMailSender> smtp=mock(ObjectProvider.class);
        var http=mock(HttpClient.class);
        var transport=new AdminMailTransport(smtp,new ObjectMapper(),"resend","no-reply@example.invalid","",http);
        assertFalse(transport.configured());
        assertThrows(IllegalStateException.class,()->transport.send(
            new AdminEmailOutbox("recipient@example.invalid","Teste","ciphertext"),"mensagem"));
        verifyNoInteractions(http,smtp);
    }

    private String readBody(HttpRequest request) throws Exception {
        var result=new CompletableFuture<String>();
        var bytes=new ByteArrayOutputStream();
        request.bodyPublisher().orElseThrow().subscribe(new Flow.Subscriber<ByteBuffer>() {
            @Override public void onSubscribe(Flow.Subscription subscription){subscription.request(Long.MAX_VALUE);}
            @Override public void onNext(ByteBuffer item){byte[] b=new byte[item.remaining()];item.get(b);bytes.writeBytes(b);}
            @Override public void onError(Throwable failure){result.completeExceptionally(failure);}
            @Override public void onComplete(){result.complete(bytes.toString(StandardCharsets.UTF_8));}
        });
        return result.get(2,TimeUnit.SECONDS);
    }
}
