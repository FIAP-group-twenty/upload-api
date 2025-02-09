package br.com.soat.uploadapi.api.exception

import br.com.soat.uploadapi.core.IntegrationErrorException
import br.com.soat.uploadapi.core.entities.ExceptionResponse
import br.com.soat.uploadapi.core.exceptions.ConflictException
import br.com.soat.uploadapi.core.exceptions.NotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handlerNotFoundException(ex: NotFoundException, request: HttpServletRequest): ResponseEntity<ExceptionResponse>{

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ExceptionResponse(
                    ex.message,
                    request.requestURI.toString(),
                    LocalDateTime.now()
                )
            )
    }

    @ExceptionHandler(ConflictException::class)
    fun handlerConflictException(ex: ConflictException, request: HttpServletRequest): ResponseEntity<ExceptionResponse>{

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ExceptionResponse(
                    ex.message,
                    request.requestURI.toString(),
                    LocalDateTime.now()
                )
            )
    }

    @ExceptionHandler(IntegrationErrorException::class)
    fun handleIntegrationException(ex: IntegrationErrorException, request: HttpServletRequest): ResponseEntity<ExceptionResponse>{

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ExceptionResponse(
                    ex.message,
                    request.requestURI.toString(),
                    LocalDateTime.now()
                )
            )
    }

}