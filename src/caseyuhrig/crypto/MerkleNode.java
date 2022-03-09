package caseyuhrig.crypto;

public class MerkleNode
{
    private MerkleNode left;
    private MerkleNode right;
    private byte[] hash;


    public MerkleNode(final MerkleNode left, final MerkleNode right, final byte[] hash)
    {
        this.left = left;
        this.right = right;
        this.hash = hash;
    }


    public MerkleNode getLeft()
    {
        return left;
    }


    public void setLeft(final MerkleNode left)
    {
        this.left = left;
    }


    public MerkleNode getRight()
    {
        return right;
    }


    public void setRight(final MerkleNode right)
    {
        this.right = right;
    }


    public byte[] getHash()
    {
        return hash;
    }


    public void setHash(final byte[] hash)
    {
        this.hash = hash;
    }
}