package xaridar.lscript;

/**
 * Simple datatype for holding two parallel values
 * @param <L> - Type of left side of Tuple
 * @param <R> - Type of Right Side of Tuple
 */
public class Tuple<L, R> {
    protected L left;
    protected R right;

    /**
     * Default constructor
     * @param left - value to store in the left side of the tuple
     * @param right - value to store in the right side of the tuple
     */
    public Tuple(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Creates a new Tuple object from the values for the left and right
     * @param left - value to store in the left side of the tuple
     * @param right - value to store in the right side of the tuple
     * @param <L> - Type of left parameter
     * @param <R> - Type of right parameter
     * @return new Tuple object instantiated using the parameters
     */
    public static <L, R> Tuple<L, R> of(L left, R right) {
        return new Tuple<>(left, right);
    }

    /**
     * @return left value of the Tuple
     */
    public L getLeft() {
        return left;
    }

    /**
     * @return right value of the Tuple
     */
    public R getRight() {
        return right;
    }

    /**
     * @param left - value to store in the left side of the tuple
     */
    public void setLeft(L left) {
        this.left = left;
    }

    /**
     * @param right - value to store in the right side of the tuple
     */
    public void setRight(R right) {
        this.right = right;
    }
}
