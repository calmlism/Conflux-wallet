
package pro.conflux.wallet.utils.bip44;


import java.io.Serializable;


public abstract class PrivateKey implements BitcoinSigner, Serializable {
   private static final long serialVersionUID = 1L;

   public abstract PublicKey getPublicKey();

   @Override
   public byte[] makeStandardBitcoinSignature(Sha256Hash transactionSigningHash) {
      byte[] signature = signMessage(transactionSigningHash);
      ByteWriter writer = new ByteWriter(1024);
      // Add signature
      writer.putBytes(signature);
      // Add hash type
      writer.put((byte) 1); // move to constant to document
      return writer.toBytes();
   }

   protected byte[] signMessage(Sha256Hash message) {
      return generateSignature(message).derEncode();
   }

   // Sign the message with a signature based random k-Value
   protected abstract Signature generateSignature(Sha256Hash message, RandomSource randomSource);

   // Sign the message deterministic, according to rfc6979
   protected abstract Signature generateSignature(Sha256Hash message);

   @Override
   public int hashCode() {
      return getPublicKey().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof PrivateKey)) {
         return false;
      }
      PrivateKey other = (PrivateKey) obj;
      return getPublicKey().equals(other.getPublicKey());
   }

   public SignedMessage signMessage(String message) {
      byte[] data = Signatures.formatMessageForSigning(message);
      Sha256Hash hash = HashUtils.doubleSha256(data);
      return signHash(hash);
   }

   public SignedMessage signHash(Sha256Hash hashToSign) {
      Signature sig = generateSignature(hashToSign);

      // Now we have to work backwards to figure out the recId needed to recover the signature.
      PublicKey targetPubKey = getPublicKey();
      boolean compressed = targetPubKey.isCompressed();
      int recId = -1;
      for (int i = 0; i < 4; i++) {

         PublicKey k = SignedMessage.recoverFromSignature(i, sig, hashToSign, compressed);
         if (k != null && targetPubKey.equals(k)) {
            recId = i;
            break;
         }
      }
      return SignedMessage.from(sig, targetPubKey, recId);
//      return Base64.encodeToString(sigData,false);
   }
}
