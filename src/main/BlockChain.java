package main;

import com.google.gson.GsonBuilder;
import components.Block;
import util.StringUtil;

import java.util.ArrayList;

public class BlockChain
{
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 5;

    public static void main(String [] args)
    {
        //add our blocks to the blockchain ArrayList:

        blockchain.add(new Block("Hi im the first block", "0"));
        System.out.println("Trying to Mine block 1... ");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("Yo im the second block",blockchain.get(blockchain.size()-1).getHash()));
        System.out.println("Trying to Mine block 2... ");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("Hey im the third block",blockchain.get(blockchain.size()-1).getHash()));
        System.out.println("Trying to Mine block 3... ");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlockchain is Valid: " + isChainValid(blockchain));

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
    }

    /**
     * Validates the blockchain for erroneous hash values. This method will need to check the hash variable is actually equal to the calculated hash,
     * and the previous block’s hash is equal to the previousHash variable. It will also check  whether the block's leading hash values is equal to the target.
     * @param blockchain
     * @return
     */
    public static boolean isChainValid(ArrayList<Block> blockchain)
    {
        boolean isValid = true;

        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for(int i =1; i<blockchain.size(); i++)
        {
            Block curBlock = blockchain.get(i);

            if(!StringUtil.calculateHash(curBlock).equals(curBlock.getHash()))
            {
                isValid = false;
            }

            Block prevBlock = blockchain.get(i-1);

            if(!prevBlock.getHash().equals(curBlock.getPreviousHash()))
            {
                isValid = false;
            }

            if(!curBlock.getHash().substring(0, difficulty).equals(hashTarget))
            {
                isValid = false;
            }
        }

        return isValid;
    }
}
