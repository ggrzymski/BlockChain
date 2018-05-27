package components;

import org.apache.commons.codec.digest.DigestUtils;
import util.StringUtil;

import java.util.Date;

/**
 * Each block does not just contain the hash of the block before it, but its own hash is also calculated from the previous hash.
 * If the previous block’s data is changed, then the previous block’s hash will change and affect all the hashes of the proceeding blocks.
 * Calculating and comparing the hashes allow us to see if a blockchain is invalid.
 */
public class Block {
    /**
     * Digital Signature of current block. Its own hash is calculated from the previous hash.
     */
    private String hash;

    /**
     * Digital signature of the previous hash
     */
    private String previousHash;

    /**
     * Transaction data
     */
    private String data;

    /**
     * Timestamp of transaction in miliseconds since 1970.
     */
    private long timeStamp;

    /**
     * The "nonce" in a bitcoin block is a 32-bit (4-byte) field whose value is set so that the hash of the block will contain a run of leading zeros.
     * The number of zero bits required is set by the difficulty.
     */
    private int nonce = 0;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = StringUtil.calculateHash(this);
    }

    /**
     * The mineBlock() method takes in a difficulty which represents the number of 0’s that must be solved for.
     * Low difficulty like 1 or 2 can be solved nearly instantly on most computers. For testing purposes, keep it around 4-6.
     *
     * @param difficulty
     */
    public void mineBlock(int difficulty)
    {
        /* The target is a number which the hash of a block header must be less than or equal to in order for that block to be considered valid.
         * This target number, when represented as a 256 bit number, has several leading zeros.
         */
        String target = new String(new char[difficulty]).replace('\0', '0');

        while(!this.hash.substring(0, difficulty).equals(target))
        {
            this.nonce ++;
            this.hash = StringUtil.calculateHash(this);
        }

        System.out.println("Block Mined!!! : " + hash);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getNonce() {
        return nonce;
    }

}
