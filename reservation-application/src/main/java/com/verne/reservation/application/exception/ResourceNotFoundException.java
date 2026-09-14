package com.verne.reservation.application.exception;

/**
 * ユースケースの実行に必要なリソースが存在しない場合の例外です。
 */
public final class ResourceNotFoundException extends RuntimeException {

    /**
     * リソース種別と識別値から例外を生成します。
     *
     * @param resourceName リソース種別
     * @param identifier 識別値
     */
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("%s not found: %s".formatted(resourceName, identifier));
    }
}
