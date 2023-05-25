package com.lucas.todosimple.exceptions;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.lucas.todosimple.services.exceptions.DataBindingViolationException;
import com.lucas.todosimple.services.exceptions.ObjectNotFoundException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements AuthenticationFailureHandler{
    
    @Value("${server.error.include-exception}") //Para printar o strackTrace, passado no properties como true.
    private boolean printStackTrace;

    /*
     * Captura qualquer tipo de exceção que for invalida.
     */
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid( MethodArgumentNotValidException methodArgumentNotValidException, HttpHeaders headers, 
        HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de validação. Verifique o campo 'erros' para obter detalhes.");
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    /*
     * Programa gera alguma exceção que não sabia que existia vai retorna essa exeção.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request){
        final String errorMessage = "Ocorreu um erro desconhecido";
        log.error(errorMessage, exception); //Printar a mensagem no console.
        return buildErrorResponse(exception, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, request);

    }

    /*
     * Essa execeção é para quando tentar criar o mesmo usuario novamente.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException dataIntegrityViolationException, WebRequest request){
        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Falha ao salvar entidade com problemas de integridade:" + errorMessage, dataIntegrityViolationException);
        return buildErrorResponse(dataIntegrityViolationException, errorMessage, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException constraintViolationException, WebRequest request){
        log.error("Falha ao validar o elemento", constraintViolationException);
        return buildErrorResponde(constraintViolationException, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(ObjectNotFoundException objectNotFoundException, WebRequest webRequest){
        log.error("Falha ao encontrar o elemento solicitado", objectNotFoundException);
        return buildErrorResponde(objectNotFoundException, HttpStatus.NOT_FOUND, webRequest);
    }

    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataBindingViolationException(DataBindingViolationException dataBindingViolationException, WebRequest webRequest){
        log.error("Falha ao salvar entidade com dados associados", dataBindingViolationException);
        return buildErrorResponde(dataBindingViolationException, HttpStatus.CONFLICT, webRequest);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception, String mensagem, HttpStatus httpStatus, WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), mensagem);
        if(this.printStackTrace){
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private ResponseEntity<Object> buildErrorResponde(Exception exception, HttpStatus httpStatus, WebRequest request){
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
       Integer status = HttpStatus.FORBIDDEN.value();
       response.setStatus(status);
       response.setContentType("application/json");
       ErrorResponse errorResponse = new ErrorResponse(status, "Email ou senha invalidos");
       /*
        * getWriter Responsavel por escrever dados de texto na resposta.
        * append esta sendo usado para adicionar a representação json do objeto.
        * Que sera enviado posteriormente ao usuario.
        */
       response.getWriter().append(errorResponse.toJson());
    }
}
