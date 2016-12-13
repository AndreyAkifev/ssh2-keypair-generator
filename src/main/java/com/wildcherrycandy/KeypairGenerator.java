package com.wildcherrycandy;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;

/**
 * Created by a.akifev on 13/12/2016.
 */
public class KeypairGenerator {

  public static com.wildcherrycandy.KeyPair generateKeyPair() throws KeypairGenerationException {
    ByteArrayOutputStream binaryOS = new ByteArrayOutputStream();
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(1024, new SecureRandom());
      KeyPair keyPair = keyPairGenerator.generateKeyPair();

      writeSshString(binaryOS, "ssh-rsa");
      RSAPublicKey rsaPub = (RSAPublicKey) keyPair.getPublic();
      writeSshMPInt(binaryOS, rsaPub.getPublicExponent());
      writeSshMPInt(binaryOS, rsaPub.getModulus());

      // Now base64-encode the result.

      final String b64Encoded = sshBase64Encode(binaryOS.toByteArray());

      // Now write out the result

      final StringBuilder publicKeyBuilder = new StringBuilder();
      publicKeyBuilder.append("ssh-rsa ");
      publicKeyBuilder.append(b64Encoded);

      final String rowKey = new String(Base64.encode(keyPair.getPrivate().getEncoded()));

      final StringBuilder privateKeyBuilder = new StringBuilder();
      privateKeyBuilder.append("-----BEGIN RSA PRIVATE KEY-----").append(System.lineSeparator());
      int cur = 0;
      while (true) {
        if (cur + 64 <= rowKey.length()) {
          privateKeyBuilder.append(rowKey.substring(cur, cur + 64)).append(System.lineSeparator());
          cur += 64;
        } else {
          privateKeyBuilder.append(rowKey.substring(cur, rowKey.length())).append(System.lineSeparator());
          break;
        }
      }
      privateKeyBuilder.append("-----END RSA PRIVATE KEY-----");
      return new com.wildcherrycandy.KeyPair(privateKeyBuilder.toString(), publicKeyBuilder.toString());
    } catch (Exception e) {
      throw new KeypairGenerationException(e);
    } finally {
      IOUtils.closeQuietly(binaryOS);
    }
  }

  private static String sshBase64Encode(byte[] byteArray) {
    String b64_prelim = DatatypeConverter.printBase64Binary(byteArray);

    // Break into lines of at most 72 characters.

    StringBuilder b64_final = new StringBuilder(b64_prelim.length() * 2);

    while (b64_prelim.length() > 72) {
      b64_final.append(b64_prelim.substring(0, 72));
      b64_final.append("\n");
      b64_prelim = b64_prelim.substring(72);
    }
    b64_final.append(b64_prelim);
    return b64_final.toString();
  }

  private static void writeSshMPInt(OutputStream os, BigInteger mpint) throws IOException {
    ByteBuffer lengthBuf = ByteBuffer.allocate(4);
    lengthBuf.order(ByteOrder.BIG_ENDIAN);
    byte[] x;
    if (mpint.equals(BigInteger.ZERO)) {
      x = new byte[0];
    } else {
      x = mpint.toByteArray();
    }
    lengthBuf.putInt(x.length);
    os.write(lengthBuf.array());
    os.write(x);
  }

  private static void writeSshString(OutputStream os, String s) throws IOException {
    ByteBuffer lengthBuf = ByteBuffer.allocate(4);
    lengthBuf.order(ByteOrder.BIG_ENDIAN);
    byte[] encoded = s.getBytes(Charset.forName("UTF-8"));
    lengthBuf.putInt(encoded.length);
    os.write(lengthBuf.array());
    os.write(encoded);
  }
}
