package org.challenge;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.challenge.config.FirebaseConfig;
import org.challenge.config.SecretValue;
import org.challenge.config.SendGridConfig;
import org.challenge.config.TwilioSmsConfig;
import org.challenge.core.ChannelPipeline;
import org.challenge.core.DefaultChannelPipeline;
import org.challenge.core.DefaultNotificationClient;
import org.challenge.core.HandlerRegistry;
import org.challenge.decorator.RetryingNotificationProvider;
import org.challenge.decorator.retry.RetryPolicy;
import org.challenge.event.InMemoryNotificationEventPublisher;
import org.challenge.event.NotificationFailedEvent;
import org.challenge.event.NotificationSentEvent;
import org.challenge.model.email.EmailAddress;
import org.challenge.model.email.EmailNotification;
import org.challenge.model.push.DeviceToken;
import org.challenge.model.push.PushNotification;
import org.challenge.model.sms.PhoneNumber;
import org.challenge.model.sms.SmsNotification;
import org.challenge.notification.api.Notification;
import org.challenge.notification.api.NotificationClient;
import org.challenge.notification.api.NotificationId;
import org.challenge.notification.api.SendResult;
import org.challenge.provider.FixedProviderRouting;

import org.challenge.provider.adapter.email.MockSendGridProvider;
import org.challenge.provider.adapter.push.MockFirebaseProvider;
import org.challenge.provider.adapter.sms.MockTwilioProvider;
import org.challenge.validation.EmailNotificationValidator;
import org.challenge.validation.PushNotificationValidator;
import org.challenge.validation.SmsNotificationValidator;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Punto de entrada de la aplicación de ejemplo.
 * <p>
 * Construye y configura el cliente de notificaciones con pipelines para email,
 * SMS y push, usando proveedores simulados y un publicador de eventos en memoria.
 */
public final class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);


    /**
     * Ejecuta envíos síncronos y asíncronos de ejemplo a través de los tres canales.
     *
     */
    static void main() {
        /*
         * En una integración real, la aplicación consumidora puede obtener
         * estas credenciales de variables de entorno, un vault o cualquier
         * otro mecanismo.
         *
         * Como los proveedores son simulados, se incluyen valores por defecto.
         */
        SendGridConfig sendGridConfig = createSendGridConfig();
        TwilioSmsConfig twilioConfig = createTwilioConfig();
        FirebaseConfig firebaseConfig = createFirebaseConfig();

        /*
         * Publicador de eventos compartido por todos los canales.
         */
        InMemoryNotificationEventPublisher eventPublisher = createEventPublisher();

        /*
         * Configuración común de reintentos.
         *
         * maxAttempts = 3:
         */
        RetryPolicy retryPolicy = RetryPolicy.exponentialBackoff(3, Duration.ofMillis(250), Duration.ofSeconds(2));

        /*
         * Proveedores concretos.
         */
        var sendGridProvider = new MockSendGridProvider(sendGridConfig);

        var twilioProvider = new MockTwilioProvider(twilioConfig);

        var firebaseProvider = new MockFirebaseProvider(firebaseConfig);

        /*
         * Decorators de reintento.
         *
         * RetryingNotificationProvider
         *      ↓
         * Proveedor simulado
         */
        var retryingEmailProvider = new RetryingNotificationProvider<>(sendGridProvider, retryPolicy);

        var retryingSmsProvider = new RetryingNotificationProvider<>(twilioProvider, retryPolicy);

        var retryingPushProvider = new RetryingNotificationProvider<>(firebaseProvider, retryPolicy);

        /*
         * Cada pipeline contiene:
         *
         * Validador
         *      ↓
         * Estrategia de selección del proveedor
         *      ↓
         * Proveedor decorado
         *      ↓
         * Publicador de eventos
         */
        var emailPipeline = new DefaultChannelPipeline<>(new EmailNotificationValidator(), new FixedProviderRouting<>(retryingEmailProvider), eventPublisher);

        var smsPipeline = new DefaultChannelPipeline<>(new SmsNotificationValidator(), new FixedProviderRouting<>(retryingSmsProvider), eventPublisher);

        var pushPipeline = new DefaultChannelPipeline<>(new PushNotificationValidator(), new FixedProviderRouting<>(retryingPushProvider), eventPublisher);

        /*
         * Registro que relaciona cada tipo de notificación
         * con su pipeline correspondiente.
         */
        Map<Class<? extends Notification>, ChannelPipeline<? extends Notification>> pipelines = new HashMap<>();

        pipelines.put(EmailNotification.class, emailPipeline);

        pipelines.put(SmsNotification.class, smsPipeline);

        pipelines.put(PushNotification.class, pushPipeline);

        HandlerRegistry handlerRegistry = new HandlerRegistry(pipelines);

        /*
         * executor basado en virtual threads.
         *
         * Se utiliza para los envíos asíncronos del cliente.
         */

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            NotificationClient notificationClient = new DefaultNotificationClient(handlerRegistry, executor);

            executeSynchronousExamples(notificationClient);
            executeAsynchronousExamples(notificationClient);
        }
    }

    /**
     * Crea la configuración simulada para el proveedor de email SendGrid.
     *
     * <p>Lee variables de entorno o utiliza valores por defecto.</p>
     *
     * @return configuración de SendGrid
     */
    private static SendGridConfig createSendGridConfig() {
        return SendGridConfig.builder().apiKey(SecretValue.of(envOrDefault("SENDGRID_API_KEY", "simulated-sendgrid-api-key"))).fromAddress(EmailAddress.of(envOrDefault("SENDGRID_FROM_ADDRESS", "notifications@example.com"))).fromName("Notification Library").build();
    }

    /**
     * Crea la configuración simulada para el proveedor de SMS Twilio.
     *
     * <p>Lee variables de entorno o utiliza valores por defecto.</p>
     *
     * @return configuración de Twilio
     */
    private static TwilioSmsConfig createTwilioConfig() {
        return TwilioSmsConfig.builder().accountSid(envOrDefault("TWILIO_ACCOUNT_SID", "AC00000000000000000000000000000000")).authToken(SecretValue.of(envOrDefault("TWILIO_AUTH_TOKEN", "simulated-twilio-auth-token"))).fromNumber(PhoneNumber.of(envOrDefault("TWILIO_FROM_NUMBER", "+15551234567"))).build();
    }

    /**
     * Crea la configuración simulada para el proveedor de push Firebase.
     *
     * <p>Lee variables de entorno o utiliza valores por defecto.</p>
     *
     * @return configuración de Firebase
     */
    private static FirebaseConfig createFirebaseConfig() {
        return FirebaseConfig.builder().projectId(envOrDefault("FIREBASE_PROJECT_ID", "simulated-notifications-project")).serviceAccountJson(SecretValue.of(envOrDefault("FIREBASE_SERVICE_ACCOUNT_JSON", """
                {
                  "type": "service_account",
                  "project_id": "simulated-project"
                }
                """))).validateOnly(false).build();
    }

    /**
     * Crea un publicador de eventos en memoria y suscribe un logger a los eventos
     * de éxito y fallo de las notificaciones.
     *
     * @return publicador de eventos configurado
     */
    private static InMemoryNotificationEventPublisher createEventPublisher() {

        InMemoryNotificationEventPublisher publisher = new InMemoryNotificationEventPublisher();

        publisher.subscribe(event -> {
            if (event instanceof NotificationSentEvent sentEvent) {
                logger.info("""
                        
                        [EVENT: SUCCESS]
                        Notification: {}
                        Provider: {}
                        Provider message ID: {}
                        Status: {}
                        
                        """, sentEvent.notificationId(), sentEvent.result().provider(), sentEvent.result().providerMessageId(), sentEvent.result().status());

                return;
            }

            if (event instanceof NotificationFailedEvent failedEvent) {
                logger.error("""
                        
                        [EVENT: FAILURE]
                        Notification: {}
                        Error code: {}
                        Message: {}
                        
                        """, failedEvent.notificationId(), failedEvent.exception().errorCode(), failedEvent.exception().getMessage());
            }
        });

        return publisher;
    }

    /**
     * Ejecuta envíos síncronos de ejemplo para email, SMS y push.
     *
     * <p>Si un envío falla, se imprime el error y se continúa con el siguiente canal.</p>
     *
     * @param notificationClient cliente configurado para enviar notificaciones
     */
    private static void executeSynchronousExamples(NotificationClient notificationClient) {
        logger.info("\n========== SYNCHRONOUS SENDS ==========\n");

        EmailNotification email = createEmailNotification();
        SmsNotification sms = createSmsNotification();
        PushNotification push = createPushNotification();

        SendResult emailResult = sendSafely(notificationClient, email, "EMAIL");
        SendResult smsResult = sendSafely(notificationClient, sms, "SMS");
        SendResult pushResult = sendSafely(notificationClient, push, "PUSH");

        if (emailResult != null) {
            printResult("EMAIL", emailResult);
        }

        if (smsResult != null) {
            printResult("SMS", smsResult);
        }

        if (pushResult != null) {
            printResult("PUSH", pushResult);
        }
    }

    /**
     * Envía una notificación de forma síncrona y captura cualquier excepción.
     *
     * @param notificationClient cliente de notificaciones
     * @param notification       notificación a enviar
     * @param channel            nombre del canal, usado para el mensaje de error
     * @param <N>                tipo de notificación
     * @return resultado del envío, o {@code null} si falló
     */
    private static <N extends Notification> SendResult sendSafely(
            NotificationClient notificationClient,
            N notification,
            String channel
    ) {
        try {
            return notificationClient.send(notification);
        } catch (Exception exception) {
            logger.error(
                    "Failed to send {} notification: {}",
                    channel,
                    exception.getMessage(),
                    exception
            );
            return null;
        }
    }

    /**
     * Ejecuta envíos asíncronos de ejemplo para email, SMS y push.
     *
     * <p>Los resultados se imprimen a medida que cada {@link CompletableFuture}
     * termina, sin esperar a los demás.</p>
     *
     * @param notificationClient cliente configurado para enviar notificaciones
     */
    private static void executeAsynchronousExamples(NotificationClient notificationClient) {
        logger.info("\n========== ASYNCHRONOUS SENDS ==========\n");

        EmailNotification email = createEmailNotification();

        SmsNotification sms = createSmsNotification();

        PushNotification push = createPushNotification();

        CompletableFuture<SendResult> emailFuture = notificationClient.sendAsync(email);
        emailFuture.thenAccept(result -> printResult("ASYNC EMAIL", result));

        CompletableFuture<SendResult> smsFuture = notificationClient.sendAsync(sms);
        smsFuture.thenAccept(result -> printResult("ASYNC SMS", result));

        CompletableFuture<SendResult> pushFuture = notificationClient.sendAsync(push);
        pushFuture.thenAccept(result -> printResult("ASYNC PUSH", result));

        CompletableFuture.allOf(emailFuture, smsFuture, pushFuture).join();
    }

    /**
     * Crea una notificación de email de ejemplo.
     *
     * @return notificación de email
     */
    private static EmailNotification createEmailNotification() {
        return new EmailNotification(NotificationId.generate(), Instant.now(), EmailAddress.of("user@example.com"), "Bienvenido", "Gracias por registrarte en nuestra aplicación.", Map.of("source", "main-example", "language", "es"));
    }

    /**
     * Crea una notificación SMS de ejemplo.
     *
     * @return notificación SMS
     */
    private static SmsNotification createSmsNotification() {
        return new SmsNotification(NotificationId.generate(), Instant.now(), PhoneNumber.of("+593991234567"), "Tu código de verificación es 123456.", Map.of("source", "main-example"));
    }

    /**
     * Crea una notificación push de ejemplo.
     *
     * @return notificación push
     */
    private static PushNotification createPushNotification() {
        return new PushNotification(NotificationId.generate(), Instant.now(), DeviceToken.of("simulated-firebase-device-token-123456"), "Nueva notificación", "Tienes una nueva actualización disponible.", Map.of("screen", "notifications", "notificationType", "update"), Map.of("source", "main-example"));
    }

    /**
     * Imprime el resultado de un envío mediante el logger.
     *
     * @param channel canal o tipo de notificación (por ejemplo, "EMAIL")
     * @param result  resultado del envío
     */
    private static void printResult(String channel, SendResult result) {
        logger.info("""
                
                [{} RESULT]
                Notification ID: {}
                Provider: {}
                Provider message ID: {}
                Status: {}
                Processed at: {}
                
                """, channel, result.notificationId(), result.provider(), result.providerMessageId(), result.status(), result.processedAt());
    }

    /**
     * Lee una variable de entorno y devuelve un valor por defecto si no está
     * definida o está en blanco.
     *
     * @param variableName nombre de la variable de entorno
     * @param defaultValue valor por defecto
     * @return valor de la variable de entorno o {@code defaultValue}
     */
    private static String envOrDefault(String variableName, String defaultValue) {
        String value = System.getenv(variableName);

        return value == null || value.isBlank() ? defaultValue : value;
    }
}