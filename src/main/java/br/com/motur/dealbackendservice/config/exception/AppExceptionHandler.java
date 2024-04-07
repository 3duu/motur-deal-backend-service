package br.com.motur.dealbackendservice.config.exception;


import br.com.motur.dealbackendservice.common.utils.StackTracerToStringHelper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
public class AppExceptionHandler {

    private final MessageSource messageSource;
    private final Logger logger;
    private final ApplicationEventPublisher publisher;
    private final StackTracerToStringHelper stackTracerToStringHelper;

    public AppExceptionHandler(MessageSource messageSource, ApplicationEventPublisher publisher, final StackTracerToStringHelper stackTracerToStringHelper) {

        this.messageSource = messageSource;
        this.publisher = publisher;
        this.stackTracerToStringHelper = stackTracerToStringHelper;
        this.logger = LoggerFactory.getLogger(AppExceptionHandler.class + " [EXCEPTION HANDLER]");
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<List<ExceptionDto>> handleHttpMessageNotReadableException(Exception exception) {
        String message = messageSource.getMessage("CodigoErroValidacaoAnuncio.PAYLOAD.INVALIDO", new String[]{}, Locale.getDefault());
        logger.error(exception.getMessage() + "{}", message, exception);

        return new ResponseEntity<>(List.of(new ExceptionDto(
                "CodigoErroValidacaoAnuncio.PAYLOAD.INVALIDO",
                message,
                ExceptionDto.AlertType.MODAL
        )), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<List<ExceptionDto>> handleGenericException(Exception exception) {
        String mensagem = messageSource.getMessage(CodigoErroAnuncio.ERRO_GERAL_NAO_TRATADO.getMessage(), new String[]{}, Locale.getDefault());
        logger.error("ERRO[{}] KEY[{}]{}",
                CodigoErroAnuncio.ERRO_GERAL_NAO_TRATADO.getCode(),
                CodigoErroAnuncio.ERRO_GERAL_NAO_TRATADO.getCode(),
                mensagem, exception);

        return new ResponseEntity<>(List.of(new ExceptionDto(
                CodigoErroAnuncio.ERRO_GERAL_NAO_TRATADO.getMessage(),
                mensagem,
                ExceptionDto.AlertType.MODAL
        )), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*@ExceptionHandler(value = {ValidacaoImagemUseCaseException.class})
    public ResponseEntity<List<ExceptionDto>> handleValidacaoImagemUseCaseException(ValidacaoImagemUseCaseException e) {

        final List<ExceptionDto> listaErros = new ArrayList<>();

        for (ExceptionDto exceptionDto : e.getErros()) {
            logger.error("ERRO[{}] KEY[{}] Detalhe [{}] ",exceptionDto.getCode(),e.getFalha().getCode(),exceptionDto.getMessage());
            listaErros.add(new ExceptionDto(e.getFalha().getCode().toString(), e.getMessage(), AlertType.ALERT)
            );
        }
        publisher.publishEvent(new IncidenteAnuncioEvent(
                e.getFalha(), stackTracerToStringHelper.transformStackTraceToString(e)
        ));
        return new ResponseEntity<>(listaErros, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {ValidacaoAnuncioUseCaseException.class})
    public ResponseEntity<List<ExceptionDto>> handleValidacaoAnuncioUseCaseException(ValidacaoAnuncioUseCaseException e) {

        List<ExceptionDto> listaErros = new ArrayList<>();

        for (ExceptionErrorMessage objectError : e.getErros()) {
            String mensagem = messageSource.getMessage(
                    objectError.getCode() == null ? "" : objectError.getCode(),
                    objectError.getArgs(),
                    objectError.getCode(),
                    Locale.getDefault());
            logger.error("ERRO[{}] KEY[{}] {}", + e.getFalha().getCode() ,objectError.getCode() ,  mensagem);
            listaErros.add(new ExceptionDto(e.getFalha().getCode().toString(), mensagem, ExceptionDto.AlertType.ALERT)
            );
        }
        publisher.publishEvent(new IncidenteAnuncioEvent(
                e.getFalha(), stackTracerToStringHelper.transformStackTraceToString(e)
        ));
        return new ResponseEntity<>(listaErros, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {BaseAnuncioException.class})
    public ResponseEntity<List<ExceptionDto>> handleExceptionDashBoard(BaseAnuncioException e) {
        List<ExceptionDto> listaErros = new ArrayList<>();
        String message = messageSource.getMessage(e.getFalha().getMessage(), new String[]{}, Locale.getDefault());
        logger.error("ERRO[{}] KEY[{}]{}" ,e.getFalha().getCode(),
                e.getFalha().getMessage(),
                message, e);

        publisher.publishEvent(new IncidenteAnuncioEvent(
                e.getFalha(), stackTracerToStringHelper.transformStackTraceToString(e)
        ));

        listaErros.add(new ExceptionDto(String.valueOf(e.getFalha().getCode()), message, ExceptionDto.AlertType.MODAL));
        return new ResponseEntity<>(listaErros, HttpStatus.INTERNAL_SERVER_ERROR);
    }*/
    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<List<ExceptionDto>> handleEntityNotFoundException(EntityNotFoundException e) {
        List<ExceptionDto> listaErros = new ArrayList<>();
        String message = messageSource.getMessage(e.getMessage(), new String[]{}, Locale.getDefault());
        logger.error("ERRO[{}] KEY[{}]" , message, e.getMessage(), e);

        return new ResponseEntity<>(listaErros, HttpStatus.NOT_FOUND);
    }

}
