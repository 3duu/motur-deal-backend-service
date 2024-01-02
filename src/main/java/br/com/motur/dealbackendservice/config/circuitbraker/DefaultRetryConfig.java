package br.com.motur.dealbackendservice.config.circuitbraker;


import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

@Component
public class DefaultRetryConfig {

    private final Logger logger = LoggerFactory.getLogger(DefaultRetryConfig.class.getSimpleName() + " [CIRCUIT-BRAKER-RETRY]");

    private final MessageSource messageSource;

    public DefaultRetryConfig(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public void init(){

         RetryConfig config =  RetryConfig.<HttpResponse>custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(3000))
                .retryOnResult(response ->  {
                    System.out.println("teste");
                    return response.statusCode() == 500;
                    }
                )
//                .retryOnException(e -> e instanceof Exception)
//                .retryExceptions(Exception.class)
                .build();
        RetryRegistry registry = RetryRegistry.of(config);
        registry.retry("AppConstantes.RETRY.MIDIA_ANUNCIO",config);
    }

    @Bean
    public RegistryEventConsumer<Retry> retryEventConsumer() {

        return new RegistryEventConsumer<Retry>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onRetry(event ->  {
                            try {
                                /*CodigoErroInclusaoAnuncio c = ((BaseAnuncioException) event.getLastThrowable()).getFalha();
                                String message = messageSource.getMessage(c.getMessage(), new String[]{}, Locale.getDefault());
                                logger.error(String.format("CircuitBraker Retry ativado. %s",message));*/
                            }catch (Exception e) {
                                logger.error("CircuitBraker Retry ativado.");
                            }
                        } );
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<Retry> entryRemoveEvent) {

            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<Retry> entryReplacedEvent) {

            }
        };
    }
}
