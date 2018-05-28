package components;

import org.apache.commons.codec.digest.DigestUtils;
import util.StringUtil;

import java.security.PublicKey;

/**
 * Transaction outputs will show the final amount sent to each party from the transaction.
 * When referenced as inputs in new transactions, they act as proof there are available coins to send.
 */
public class TransactionOutput
{
    public String id;

    /**
     * New Owner of the coin
     */
    public PublicKey recipient;

    /**
     * Amount of coins owned.
     */
    public float value;

    /**
     *  ID of transaction that output was created in.
     */
    public String parentTransactionId;

    //Constructor
    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId)
    {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = DigestUtils.sha256Hex(StringUtil.getStringFromKey(recipient)+Float.toString(value)+parentTransactionId);
    }

    /**
     * Check for coin ownership and authenticity.
     * @param publicKey Public key
     * @return
     */
    public boolean isMine(PublicKey publicKey)
    {
        return (publicKey == this.recipient);
    }
}
