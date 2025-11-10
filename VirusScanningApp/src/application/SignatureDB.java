package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SignatureDB {

    private static final String SIGNATURE_FILE = "signatures.txt";
    private static final List<String> signatures = new ArrayList<>();

    static {
        try (Scanner sc = new Scanner(new File(SIGNATURE_FILE))) {
            while (sc.hasNextLine()) {
                signatures.add(sc.nextLine().trim());
            }
        } catch (Exception e) {
            System.out.println("Signature file not found, using default.");
        }
    }

    public static boolean isVirus(String fileName) {
        for (String sig : signatures) {
            if (fileName.contains(sig)) return true;
        }
        return false;
    }

    public static List<String> getSignatures() {
        return signatures;
    }
}
