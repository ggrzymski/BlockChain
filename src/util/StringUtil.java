package util;

import components.Block;
import components.Transaction;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

public class StringUtil
{
    /**
     * Uses Apache Commons Digest Utility to convert a block's collective data into hashed SHA-256 format.
     * @param block Input that comprises the concatenation of the block's fields.
     * @return
     */
    public static String calculateHash(Block block)
    {
        String input = block.getPreviousHash() + Long.toString(block.getTimeStamp()) + Integer.toString(block.getNonce()) + block.getData();
        return DigestUtils.sha256Hex(input);
    }

    /**
     * Returns encoded string from any key.
     * @param key Public or Private Key
     * @return Encoded key
     */
    public static String getStringFromKey(Key key)
    {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Takes in the senders private key and string input, signs it, and returns an array of bytes.
     * @param privateKey Private Key.
     * @param input String input.
     * @return
     */
    public static byte[] applyECDSASignature(PrivateKey privateKey, String input)
    {
        try
        {
            Signature digitalSignature = Signature.getInstance("ECDSA", "BC");
            digitalSignature.initSign(privateKey);

            digitalSignature.update(input.getBytes());

            return digitalSignature.sign();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Takes in the signature, public key, and string data and returns true or false if the signature is valid
     * @param publicKey public key
     * @param data data transaction data
     * @param signature digital signature
     * @return
     */
    public static boolean verifyECDSASignature(PublicKey publicKey, String data, byte[] signature)
    {
        try
        {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");

            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());

            return ecdsaVerify.verify(signature);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions)
    {
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < previousTreeLayer.size(); i++)
            {
                treeLayer.add(DigestUtils.sha256Hex(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }

    public static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }
}
