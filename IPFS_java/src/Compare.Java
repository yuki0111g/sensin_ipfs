import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Compare {
    public static byte[] calculateHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, n);
            }
        }
        return digest.digest();
    }

    public static boolean compareFiles(File file1, File file2) throws IOException, NoSuchAlgorithmException {
        byte[] hash1 = calculateHash(file1);
        byte[] hash2 = calculateHash(file2);
        return MessageDigest.isEqual(hash1, hash2);
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        File file1 = new File("targetDir\\test.txt");
        File file2 = new File("targetDir\\test2.txt");

        boolean areEqual = compareFiles(file1, file2);
        System.out.println("ファイルは" + (areEqual ? "同じ" : "異なる"));
    }
}