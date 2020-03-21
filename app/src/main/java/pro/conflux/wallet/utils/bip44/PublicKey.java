
package pro.conflux.wallet.utils.bip44;


import pro.conflux.wallet.utils.bip44.ec.Parameters;
import pro.conflux.wallet.utils.bip44.ec.Point;

import java.io.Serializable;
import java.util.Arrays;


public class PublicKey implements Serializable {
   private static final long serialVersionUID = 1L;

   private final byte[] _pubKeyBytes;
   private byte[] _pubKeyHash;
   private Point _Q;

   public PublicKey(byte[] publicKeyBytes) {
      _pubKeyBytes = publicKeyBytes;
   }

   public Address toAddress(NetworkParameters networkParameters) {
      byte[] hashedPublicKey = getPublicKeyHash();
      return Address.fromStandardBytes(hashedPublicKey, networkParameters);
   }

   public byte[] getPublicKeyBytes() {
      return _pubKeyBytes;
   }

   public byte[] getPublicKeyHash() {
      if (_pubKeyHash == null) {
         _pubKeyHash = HashUtils.addressHash(_pubKeyBytes);
      }
      return _pubKeyHash;
   }

   @Override
   public int hashCode() {
      byte[] bytes = getPublicKeyHash();
      int hash = 0;
      for (int i = 0; i < bytes.length; i++) {
         hash = (hash << 8) + (bytes[i] & 0xff);
      }
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof PublicKey)) {
         return false;
      }
      PublicKey other = (PublicKey) obj;
      return Arrays.equals(getPublicKeyHash(), other.getPublicKeyHash());
   }

   @Override
   public String toString() {
      return HexUtils.toHex(_pubKeyBytes);
   }

   public boolean verifyStandardBitcoinSignature(pro.conflux.wallet.utils.bip44.Sha256Hash data, byte[] signature, boolean forceLowS) {
      // Decode parameters r and s
      ByteReader reader = new ByteReader(signature);
      Signature params = Signatures.decodeSignatureParameters(reader);
      if (params == null) {
         return false;
      }
      // Make sure that we have a hash type at the end
      if (reader.available() != 1) {
         return false;
      }
      if (forceLowS) {
         return Signatures.verifySignatureLowS(data.getBytes(), params, getQ());
      } else {
         return Signatures.verifySignature(data.getBytes(), params, getQ());
      }

   }

   // same as verifyStandardBitcoinSignature, but dont enforce the hash-type check
   public boolean verifyDerEncodedSignature(pro.conflux.wallet.utils.bip44.Sha256Hash data, byte[] signature){
      // Decode parameters r and s
      ByteReader reader = new ByteReader(signature);
      Signature params = Signatures.decodeSignatureParameters(reader);
      if (params == null) {
         return false;
      }
      return Signatures.verifySignature(data.getBytes(), params, getQ());
   }

   /**
    * Is this a compressed public key?
    */
   public boolean isCompressed() {
      return getQ().isCompressed();
   }

  public Point getQ() {
      if (_Q == null) {
         _Q = Parameters.curve.decodePoint(_pubKeyBytes);
      }
      return _Q;
   }

}
