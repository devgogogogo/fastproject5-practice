package com.example.board.exception;

public class ClientErrorException extends RuntimeException {
  public ClientErrorException(String message) {
    super(message);
  }
}
