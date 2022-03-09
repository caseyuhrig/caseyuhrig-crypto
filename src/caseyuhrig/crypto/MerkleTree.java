package caseyuhrig.crypto;

import java.util.ArrayList;


public final class MerkleTree extends ArrayList<byte[]>
{
    public MerkleTree()
    {
        super();
    }


    public byte[] getMerkleRoot()
    {
        return generateTree(this).getHash();
    }


    private static byte[] hash(final byte[] hash)
    {
        return CryptoUtils.SHA256(hash);
    }


    private static MerkleNode generateTree(final ArrayList<byte[]> dataBlocks)
    {
        final ArrayList<MerkleNode> childNodes = new ArrayList<>();
        for (final byte[] message : dataBlocks)
        {
            childNodes.add(new MerkleNode(null, null, hash(message)));
        }
        return buildTree(childNodes);
    }


    private static MerkleNode buildTree(ArrayList<MerkleNode> children)
    {
        ArrayList<MerkleNode> parents = new ArrayList<>();
        while (children.size() != 1)
        {
            int index = 0;
            final int length = children.size();
            while (index < length)
            {
                final MerkleNode leftChild = children.get(index);
                MerkleNode rightChild = null;
                if ((index + 1) < length)
                {
                    rightChild = children.get(index + 1);
                }
                else
                {
                    rightChild = new MerkleNode(null, null, leftChild.getHash());
                }
                final byte[] parentHash = hash(CryptoUtils.concat(leftChild.getHash(), rightChild.getHash()));
                parents.add(new MerkleNode(leftChild, rightChild, parentHash));
                index += 2;
            }
            children = parents;
            parents = new ArrayList<>();
        }
        return children.get(0);
    }
}
