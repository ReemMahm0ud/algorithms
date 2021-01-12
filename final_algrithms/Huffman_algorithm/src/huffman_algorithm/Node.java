/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman_algorithm;

/**
 *
 * @author Win
 */
import java.util.Comparator;

/**
 *
 * @author RS
 */
public class Node {

    Node left, right;
    int value;
    Character character;

    public Node(int value, Character character) {
        this.value = value;
        this.character = character;
        left = null;
        right = null;
    }

    public Node(Node left, Node right) {
        this.value = left.value + right.value;
        this.character =  '\0';
        if (left.value <= right.value) {
            this.right = right;
            this.left = left;
        } else {
            this.right = left;
            this.left = right;
        }
    }

}

