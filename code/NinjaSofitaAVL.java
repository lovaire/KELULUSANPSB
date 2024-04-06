import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class NinjaSofitaAVL {
    private static InputReader in;
    static PrintWriter out;
    static AVLTree tree = new AVLTree();

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int numOfInitialNode = in.nextInt();
        for (int i = 0; i < numOfInitialNode; i++) {
            int value = in.nextInt();
            tree.root = tree.insertNode(tree.root, value);
        }

        int numOfQueries = in.nextInt();
        for (int i = 0; i < numOfQueries; i++) {
            String query = in.next();
            if (query.equals("I")) {
                int value = in.nextInt();
                tree.root = tree.insertNode(tree.root, value);
            } else if (query.equals("S")) {
                int value = in.nextInt();
                int height = tree.searchNode(tree.root, value);
                out.println(height);
            } else if (query.equals("R")) {
                int range = tree.treeRange(tree.root);
                out.println(range);
            } else if (query.equals("H")) {
                int height = tree.getHeight(tree.root);
                out.println(height);
            }
        }
        out.close();
    }

    // taken from https://codeforces.com/submissions/Petr DO NOT MODIFY
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }
    }
}

class Node {
    int key;
    int height;
    Node left;
    Node right;

    Node(int key) {
        this.key = key;
        this.height = 1;
    }
}

class AVLTree {
    Node root;

    Node rotateRight(Node node) {
        Node newRoot = node.left;
        node.left = newRoot.right;
        newRoot.right = node;
        node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
        newRoot.height = Math.max(getHeight(newRoot.left), node.height) + 1;
        return newRoot;
    }

    Node rotateLeft(Node node) {
        Node newRoot = node.right;
        node.right = newRoot.left;
        newRoot.left = node;
        node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
        newRoot.height = Math.max(getHeight(newRoot.right), node.height) + 1;
        return newRoot;
    }

    Node rightLeftDoubleRotation(Node node) {
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }

    Node leftRightDoubleRotation(Node node) {
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }

    Node insertNode(Node node, int key) {
        if (node == null) {
            return new Node(key);
        }

        if (key < node.key) {
            node.left = insertNode(node.left, key);
        } else if (key > node.key) {
            node.right = insertNode(node.right, key);
        } else {
            return node; // Duplicate keys are not allowed
        }

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        int balance = getBalance(node);

        if (balance > 1 && key < node.left.key) {
            return rotateRight(node);
        }

        if (balance < -1 && key > node.right.key) {
            return rotateLeft(node);
        }

        if (balance > 1 && key > node.left.key) {
            return leftRightDoubleRotation(node);
        }

        if (balance < -1 && key < node.right.key) {
            return rightLeftDoubleRotation(node);
        }

        return node;
    }

    int searchNode(Node node, int key) {
        if (node == null) {
            return -1;
        }

        if (key == node.key) {
            return node.height;
        }

        if (key < node.key) {
            return searchNode(node.left, key);
        } else {
            return searchNode(node.right, key);
        }
    }

    int treeRange(Node node) {
        if (node == null) {
            return 0;
        }

        int max = findMax(node);
        int min = findMin(node);
        return max - min;
    }

    int findMax(Node node) {
        if (node.right == null) {
            return node.key;
        }
        return findMax(node.right);
    }

    int findMin(Node node) {
        if (node.left == null) {
            return node.key;
        }
        return findMin(node.left);
    }

    // Utility function to get height of node
    int getHeight(Node node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    // Utility function to get balance factor of node
    int getBalance(Node node) {
        if (node == null) {
            return 0;
        }
        return getHeight(node.left) - getHeight(node.right);
    }
}