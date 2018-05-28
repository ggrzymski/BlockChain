package components;

import main.BlockChain;
import org.apache.commons.codec.digest.DigestUtils;
import util.StringUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction
{
    /**
     * Hash of the transaction
     */
    public String transactionId;

    /**
     * Public address key of the sender
     */
    public PublicKey sender;

    /**
     * Public address key of the receiver.
     */
    public PublicKey recipient;

    /**
     * Amount of funds to be transferred.
     */
    public float value;

    /**
     * A cryptographic signature that proves the owner of the address is the one sending this transaction and that the data has not been changed.
     */
    private byte[] signature;

    /**
     * References to previous transactions that prove the sender has funds to send.
     */
    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

    /**
     * Shows the amount that relevant addresses received in the transaction.
     */
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    /**
     * A rough count of how many transactions have been generated.
     */
    private static int sequence = 0;

    public Transaction(PublicKey sender, PublicKey receiver, float value,  ArrayList<TransactionInput> inputs)
    {
        this.sender = sender;
        this.recipient = receiver;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction()
    {
        if(verifiySignature() == false)
        {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        for(TransactionInput i : inputs)
        {
            i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
        }

        if(getInputsValue() < BlockChain.minimumTransaction)
        {
            System.out.println("#Transaction Inputs too small: " + getInputsValue());
            return false;
        }

        float leftOver = getInputsValue() - value;
        transactionId = calulateHash();
        outputs.add(new TransactionOutput(this.recipient, value,transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver,transactionId));

        for(TransactionOutput o : outputs)
        {
            BlockChain.UTXOs.put(o.id , o);
        }

        for(TransactionInput i : inputs)
        {
            if(i.UTXO == null)
            {
                continue;
            }

            BlockChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue()
    {
        float total = 0;

        for(TransactionInput i : inputs)
        {
            if(i.UTXO == null)
            {
                continue;
            }

            total += i.UTXO.value;
        }

        return total;
    }

    public float getOutputsValue()
    {
        float total = 0;

        for(TransactionOutput o : outputs)
        {
            total += o.value;
        }

        return total;
    }

    /**
     * This calculates the transaction hash (which will be used as the Id)
     * @return
     */
    private String calulateHash()
    {
        // Sequence is increased to avoid 2 identical transactions having the same hash.
        this.sequence++;

        String encodedSenderkey = StringUtil.getStringFromKey(this.sender);
        String encodedReceiverkey = StringUtil.getStringFromKey(this.recipient);

        return DigestUtils.sha256Hex(encodedSenderkey+encodedReceiverkey+Float.toString(this.value) + this.sequence);
    }

    /**
     * Signs all the data that should not be tampered with.
     */
    public void generateSignature(PrivateKey privateKey)
    {
        String data = StringUtil.getStringFromKey(this.sender) + StringUtil.getStringFromKey(this.recipient) + Float.toString(this.value);
        this.signature = StringUtil.applyECDSASignature(privateKey,data);
    }

    /**
     * Verifies the signed data has not been tampered with.
     * @return
     */
    public boolean verifiySignature()
    {
        String data = StringUtil.getStringFromKey(this.sender) + StringUtil.getStringFromKey(this.recipient) + Float.toString(this.value);
        return StringUtil.verifyECDSASignature(this.sender, data, this.signature);
    }
}
