package util;

import components.Block;
import org.apache.commons.codec.digest.DigestUtils;

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
}
