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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RS
 */
public class Huffman {

    /**
     * @param args the command line arguments
     */
    /*final static Comparator<Node> comparator = new Node();
    static PriorityQueue<Node> nodes = new PriorityQueue<>(comparator);*/
    static PriorityQueue<Node> nodes = new PriorityQueue<>((o1, o2) -> (o1.value < o2.value) ? -1 : 1);
    static TreeMap<Character, String> codes = new TreeMap<>();
    static HashMap<Character, String> lookUpFile = new HashMap<>();
    static String text = "";
    static String encodedBinary = "";
    static String encodedString = "";
    static String decoded = "";
    static TreeMap<Character, Integer> frequency = new TreeMap<>();
    static BufferedWriter compressOutput = null;
    static BufferedWriter decompressOutput = null;
    static int compressedLength = 0;
    static int uncompressedLength = 0;

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        int decision = 0;
        Scanner console = new Scanner(System.in);
        while (decision != 4) {
            System.out.println("\n---- Menu ----\n"
                    + "-- [1] to compress\n"
                    + "-- [2] to decompress\n"
                    + "-- [3] to compress folder\n"
                    + "-- [4] exit\n");
            decision = Integer.parseInt(console.nextLine());
            long startTime = System.currentTimeMillis();
            long endTime = 0;
            if (decision == 1 || decision == 2 || decision == 3) {
                if (decision == 3) {
                    Scanner s3 = new Scanner(System.in);
                    System.out.println("Enter The Full Folder Path:");
                    String x = s3.nextLine();
                    System.out.println("Enter output file path \n");
                    String outputFile = console.nextLine();
                    File[] files = new File(x).listFiles();
                    compressOutput = new BufferedWriter(new FileWriter(new File(outputFile), false));
                    for (int i = 0; i < files.length; i++) {
                        compressOutput.append("\n file Number " + (i + 1) + "\n");
                        System.out.println("File Number " + (i + 1) + "\n");
                        Scanner scanner = new Scanner(new FileReader(files[i]));
                        handleEncodingNewText(scanner, decision);
                        endTime = System.currentTimeMillis();
                        System.out.println("compression ratio=" + (float) ((float) compressedLength / uncompressedLength));
                        compressOutput.append("\nEND of file\n");
                    }

                    compressOutput.close();
                }
                if (decision == 1) {
                    System.out.println("enter file path \n");
                    String filePath = console.nextLine();
                    //  String filePath = "C:\\Users\\RS\\Desktop\\Huffman\\input.txt";
                    Scanner scanner = new Scanner(new File(filePath));
                    System.out.println("enter output file path \n");
                    String outputFile = console.nextLine();
                    // outputFile = outputFile.concat(".txt");                
                    //  String outputFile = "C:\\Users\\RS\\Desktop\\Huffman\\q.txt";             
                    compressOutput = new BufferedWriter(new FileWriter(new File(outputFile), false));
                    text = new String(Files.readAllBytes(Paths.get(filePath)));
                    handleEncodingNewText(scanner, decision);
                    endTime = System.currentTimeMillis();
                    System.out.println("compression ratio=" + (float) ((float) compressedLength / uncompressedLength));
                    compressOutput.close();

                } else if (decision == 2) {
                    System.out.println("enter file path \n");
                    String filePath = console.nextLine();
                    //   String filePath="C:\\Users\\RS\\Desktop\\Huffman\\q.txt";
                    Scanner scanner = new Scanner(new File(filePath));
                    handleDecodingNewText(scanner);
                    endTime = System.currentTimeMillis();
                    System.out.println("compression ratio=" + (float) ((float) compressedLength / uncompressedLength));

                }

                System.out.println("Time=" + (endTime - startTime));

            } else if (decision != 4) {
                System.out.println("Try Again");
                continue;
            }
        }
    }

    private static boolean handleEncodingNewText(Scanner scanner, int decision) {
        if (decision == 3) {
            text = "";
            while (scanner.hasNextLine()) {
                text += scanner.nextLine();
                text += "\n";
            }
        }
        //System.out.println("Text to Encode: " + text);
        frequency.clear();
        nodes.clear();
        codes.clear();
        decoded = "";
        encodedBinary = "";
        encodedString = "";
        System.out.println("before calculate frequency ");
        calculateCharIntervals(nodes);
                System.out.println("After calculate frequency ");

        buildTree(nodes);
                        System.out.println("After build tree");

        try {
            compressOutput.append("--- Printing Codes ---\n");
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }  
      generateCodes(nodes.peek(), "");
                              System.out.println("After generate codes");

              try {
            compressOutput.append("end of codes\n");
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }  
       // printCodes();
        encodeText();
                                      System.out.println("After encode text");
        return false;
    }

    private static void encodeText() {
        encodedBinary = "";
        encodedString = "";
        compressedLength = 0;
        int extraZeros = 0;
        int t=text.length();
         uncompressedLength = t ;
        for (int i = 0; i <t; i++) {
            encodedBinary += codes.get(text.charAt(i));
        }
        System.out.println("string converted to binary");
        int b=encodedBinary.length();
        for (int i = 0; i < b; i += 8) {
            if ((b - i) > 8) {
                encodedString += ((char) Integer.parseInt(encodedBinary.substring(i, i + 8), 2));
            } else {
                String remain = encodedBinary.substring(i);
                extraZeros = 8 - remain.length();
                for (int j = 0; j < extraZeros; j++) {
                    remain += '0';
                }
                encodedString += (char) Integer.parseInt(remain, 2);
            }
        }
                System.out.println("binary converted to strange string");
        
        try {
            compressOutput.append("extra Zeros=" + extraZeros + "\n");
            compressOutput.append("end of header\n");
            compressOutput.append(encodedString);
            compressedLength = encodedString.length();
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void handleDecodingNewText(Scanner scanner) throws IOException {
        Scanner console = new Scanner(System.in);
        do {
            System.out.println("enter output file path \n");
            //String outputFile="C:\\Users\\RS\\Desktop\\Huffman\\z.txt";

            String outputFile = console.nextLine();
            //     outputFile = outputFile.concat(".txt");
            decompressOutput = new BufferedWriter(new FileWriter(new File(outputFile), false));
            frequency.clear();
            nodes.clear();
            codes.clear();
            decoded = "";
            encodedBinary = "";
            encodedString = "";
            int headerEnd = 0;
            int startCodes = 0;
            int extraZeros = 0;
            int endOfCodes = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("END of file")) {
                    break;
                }

                if (line.equals("--- Printing Codes ---")) {
                    startCodes = 1;
                    continue;
                }
                if (line.equals("end of codes")) {
                    endOfCodes = 1;
                    continue;
                }
                if (line.contains("extra Zeros")) {
                    extraZeros = Character.getNumericValue(line.charAt(line.length() - 1));
                    continue;
                }


                if (startCodes == 1 && endOfCodes == 0) {
                         char c;
                    String[] words = line.split(":");
                    c = (char) Integer.parseInt(words[0].trim());
                    lookUpFile.put(c, words[1].trim());
                }

                if (line.equals("end of header")) {
                    headerEnd = 1;
                    continue;
                }
                if (headerEnd == 1) {
                    encodedString += line;
                }
            }
            System.out.println("finish reading header");
            int encodedStringLength = encodedString.length();
            for (int i = 0; i < encodedStringLength; i++) {
                if (i == encodedStringLength - 1) {
                    char c = encodedString.charAt(i);
                    String cbinary = String.format("%08d", Integer.parseInt(Integer.toBinaryString(c)));
                    int remain = 8 - extraZeros;
                    for (int j = 0; j < remain; j++) {
                        encodedBinary += cbinary.charAt(j);
                    }
                } else {
                    char c = encodedString.charAt(i);
                    String cbinary = String.format("%08d", Integer.parseInt(Integer.toBinaryString(c)));
                    encodedBinary += cbinary;

                }
            }
            System.out.println("finish encoded Binary");
            decodeText(encodedBinary);
            compressedLength = encodedString.length();
            decompressOutput.close();
        } while (scanner.hasNextLine());
    }

    private static void decodeText(String compressed) {
  String s = "";
  int c=compressed.length();
         for (int i = 0; i < c; i++) {
            s += compressed.charAt(i);
            for (Map.Entry<Character, String> entry : lookUpFile.entrySet()) {
                char key = entry.getKey();
                String value = entry.getValue();
                if (s.compareTo(value) == 0) {
                    decoded+= key;
                    s = "";
                    break;
                }
            }
        }
        try {

            decompressOutput.append(decoded);
            uncompressedLength = decoded.length();
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void buildTree(PriorityQueue<Node> vector) {
        if (vector.size() == 1) {
            vector.add(new Node(1, '\0'));
        }
        while (vector.size() > 1) {
            vector.add(new Node(vector.poll(), vector.poll()));
        }
    }

    private static void printCodes() {
        try {
            compressOutput.append("--- Printing Codes ---\n");
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
        codes.forEach((k, v) -> {
            try {
                compressOutput.append((int)k + " : " + v + "\n");
            } catch (IOException ex) {
                Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        try {
            compressOutput.append("end of codes\n");
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void calculateCharIntervals(PriorityQueue<Node> vector) {
        int l=text.length();
        for (int i = 0; i < l; i++) {
            if (!frequency.containsKey(text.charAt(i))) {
                frequency.put(text.charAt(i), 1);
            } else {
                frequency.put(text.charAt(i), frequency.get(text.charAt(i)) + 1);
            }
        }
        for (Character c : frequency.keySet()) {
                 nodes.add(new Node(frequency.get(c), c));                
        }
    }

    private static void generateCodes(Node node, String s) {
        if (node != null) {

            if (node.left != null) {
                generateCodes(node.left, s + "0");
            }
            if (node.right != null) {
                generateCodes(node.right, s + "1");
            }

            if (node.left == null && node.right == null) {
                codes.put(node.character, s);
                        try {
                compressOutput.append((int)node.character + " : " + s + "\n");
            } catch (IOException ex) {
                Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                return;
            }
        }
    
}
