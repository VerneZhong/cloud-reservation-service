package com.verne.reservation.api.exception;

import com.verne.reservation.api.common.ApiErrorResponse;
import com.verne.reservation.api.common.FieldViolation;
import com.verne.reservation.domain.exception.DomainException;
import com.verne.reservation.domain.exception.InsufficientInventoryException;
import com.verne.reservation.domain.exception.InvalidReservationStateException;
import com.verne.reservation.domain.exception.ReservationWindowClosedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * APIで発生した例外を共通形式のHTTPレスポンスへ変換します。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * リクエストボディの入力エラーを400レスポンスへ変換します。
     *
     * @param exception 入力検証例外
     * @param request HTTPリクエスト
     * @return 入力エラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<FieldViolation> violations = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldViolation(
                        error.getField(),
                        error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage()
                ))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Request validation failed",
                request,
                violations
        );
    }

    /**
     * パス変数やクエリパラメータの入力エラーを400レスポンスへ変換します。
     *
     * @param exception 制約違反例外
     * @param request HTTPリクエスト
     * @return 入力エラーレスポンス
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<FieldViolation> violations = exception.getConstraintViolations()
                .stream()
                .map(violation -> new FieldViolation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Request validation failed",
                request,
                violations
        );
    }

    /**
     * 在庫不足を409レスポンスへ変換します。
     *
     * @param exception 在庫不足例外
     * @param request HTTPリクエスト
     * @return 競合エラーレスポンス
     */
    @ExceptionHandler(InsufficientInventoryException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientInventory(
            InsufficientInventoryException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "INSUFFICIENT_INVENTORY",
                exception.getMessage(),
                request,
                List.of()
        );
    }

    /**
     * 不正な予約状態での操作を409レスポンスへ変換します。
     *
     * @param exception 予約状態例外
     * @param request HTTPリクエスト
     * @return 競合エラーレスポンス
     */
    @ExceptionHandler(InvalidReservationStateException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidReservationState(
            InvalidReservationStateException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "INVALID_RESERVATION_STATE",
                exception.getMessage(),
                request,
                List.of()
        );
    }

    /**
     * 予約受付期間外の操作を409レスポンスへ変換します。
     *
     * @param exception 予約受付期間外例外
     * @param request HTTPリクエスト
     * @return 競合エラーレスポンス
     */
    @ExceptionHandler(ReservationWindowClosedException.class)
    public ResponseEntity<ApiErrorResponse> handleReservationWindowClosed(
            ReservationWindowClosedException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "RESERVATION_WINDOW_CLOSED",
                exception.getMessage(),
                request,
                List.of()
        );
    }

    /**
     * その他のドメイン例外を422レスポンスへ変換します。
     *
     * @param exception ドメイン例外
     * @param request HTTPリクエスト
     * @return ドメインルール違反レスポンス
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainException(
            DomainException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.UNPROCESSABLE_CONTENT,
                "DOMAIN_RULE_VIOLATION",
                exception.getMessage(),
                request,
                List.of()
        );
    }

    /**
     * 不正な引数を400レスポンスへ変換します。
     *
     * @param exception 不正引数例外
     * @param request HTTPリクエスト
     * @return 不正リクエストレスポンス
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_ARGUMENT",
                exception.getMessage(),
                request,
                List.of()
        );
    }

    /**
     * 指定された情報から共通エラーレスポンスを生成します。
     *
     * @param status HTTPステータス
     * @param code アプリケーション固有のエラーコード
     * @param message エラー内容
     * @param request HTTPリクエスト
     * @param violations 項目単位の入力エラー
     * @return 共通エラーレスポンス
     */
    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            List<FieldViolation> violations
    ) {
        ApiErrorResponse response = ApiErrorResponse.of(
                status.value(),
                code,
                message,
                request.getRequestURI(),
                violations
        );
        return ResponseEntity.status(status).body(response);
    }
}
