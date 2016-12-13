package com.wildcherrycandy;

/**
 * Created by a.akifev on 13/12/2016.
 */
public class KeyPair {

  private String privateKey;

  private String publicKey;

  public KeyPair(String privateKey, String publicKey) {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public String getPublicKey() {
    return publicKey;
  }
}
