package components;

import main.BlockChain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet
{
    /**
     * Private key is used to sign transactions so that nobody can spend coins other than the owner of the private key.
     */
    public PrivateKey privateKey;

    /**
     * Public key will act as an address. Bundled with the transaction as it can be used to verify the signature is valid and data has not been tampered with.
     */
    public PublicKey publicKey;

    public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

    public Wallet()
    {
        this.generateKeyPair();
    }

    /**
     * Generates the public and private keys of the wallet through Elliptic-Curve Cryptograhy

     */
    private void generateKeyPair()
    {
        try
        {
            // Returns a KeyPairGenerator object that generates public/private key pairs for the Edwards-Curve Digital Signature Algorithm with Bouncy Castle provider.
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");

            // Pseudo RNG that uses the SHA1 hash function to generate a stream of random numbers.
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            // Parameter Spec with prime192v1 curve
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random);

            KeyPair keyPair = keyGen.generateKeyPair();

            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public float getBalance()
    {
        float total = 0;

        for (Map.Entry<String, TransactionOutput> item: BlockChain.UTXOs.entrySet())
        {
            TransactionOutput UTXO = item.getValue();

            if(UTXO.isMine(publicKey))
            {
                UTXOs.put(UTXO.id,UTXO);
                total += UTXO.value ;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey _recipient, float value )
    {
        if(getBalance() < value)
        {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;

        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet())
        {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);

        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs)
        {
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }

    public PrivateKey getPrivateKey()
    {
        return privateKey;
    }

    public PublicKey getPublicKey()
    {
        return publicKey;
    }
}
