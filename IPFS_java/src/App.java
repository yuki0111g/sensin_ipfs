import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class App {
    public static void main(String[] args) throws Exception {
        setCustomAttribute("targetDir\\test.txt", "id", "メタデータの中身");
        String metadata = getCustomAttribute("targetDir\\test.txt", "id");
        System.out.println(metadata);

    }

    public static void setCustomAttribute(String filePath, String attrName, String attrValue) throws IOException {
        Path path = Paths.get(filePath);
        UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);

        if (view != null) {
            // 既存の属性を削除してから新しい属性を設定
            // view.delete(attrName); // 属性が存在しない場合はスキップされる
            view.write(attrName, Charset.defaultCharset().encode(attrValue));
            // System.out.println("ファイルにカスタム属性を設定しました: " + attrName + " = " + attrValue);
        } else {
            System.out.println("Custom attribute is not supported in this OS.");
        }
    }

    public static String getCustomAttribute(String filePath, String attrName) throws IOException {
        Path path = Paths.get(filePath);
        UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);

        if (view != null) {
            // 属性のサイズを取得し、バッファを作成
            int size = view.size(attrName);

            ByteBuffer buffer = ByteBuffer.allocate(size);
            view.read(attrName, buffer);
            buffer.flip();
            return Charset.defaultCharset().decode(buffer).toString();
        } else {
            System.out.println("Custom attribute is not supported in this OS.");
            return null;
        }
    }
}
