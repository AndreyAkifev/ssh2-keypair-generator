package com.wildcherrycandy;

/**
 * Created by a.akifev on 13/12/2016.
 */
public class KeypairGenerationException extends Exception {

  public KeypairGenerationException() {
  }

  public KeypairGenerationException(String message) {
    super(message);
  }

  public KeypairGenerationException(String message, Throwable cause) {
    super(message, cause);
  }

  public KeypairGenerationException(Throwable cause) {
    super(cause);
  }

  public KeypairGenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
