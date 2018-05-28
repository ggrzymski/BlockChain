package components;

/**
 * Transaction inputs are references to previous transaction outputs. The sender references that he/she previously received one bitcoin.
 * This class will be used to reference TransactionOutputs that have not yet been spent.
 */
public class TransactionInput
{
    /**
     * Used to find the relevant TransactionOutput, allowing miners to check for ownership.
     */
    public String transactionOutputId;

    /**
     *  Unspent Transaction Output (Wallet Balance)
     */
    public TransactionOutput UTXO;


    public TransactionInput(String transactionOutputId)
    {
        this.transactionOutputId = transactionOutputId;
    }
}
