import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class App {
    public static void main(String[] args) throws Exception {
        // setCustomAttribute("targetDir\\test.txt", "id", "メタデータの中身");
        // String metadata = getCustomAttribute("targetDir\\test.txt", "id");
        // System.out.println(metadata);

        getContents("1AmhV8XkWPve2EYQdkU6vDWT9JLnapdGcpw2M1wUCRZuM4");
        

        Path contentPath = WildcardFileSearch("1AmhV8XkWPve2EYQdkU6vDWT9JLnapdGcpw2M1wUCRZuM4");
        System.out.println(contentPath);

    }

    //メタデータをセット
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

    //メタデータを取得
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

    //コンテンツを取得する。
    public static void getContents(String CID) {
        try {
            // 実行するコマンド
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c",
                    "curl -X POST \"http://127.0.0.1:5001/api/v0/dht/getvalue?cid=" + CID + "\""); 

            // プロセスを開始
            Process process = processBuilder.start();

            // 標準出力を読み取る
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 終了コードを確認
            int exitCode = process.waitFor();
            System.out.println("終了コード: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //getData内に生成されたファイルのpathを取得する
    public static Path WildcardFileSearch(String CID) {
        // 検索するディレクトリ
        Path dir = Paths.get("../getdata"); // 対象のパスを指定

        // 検索するワイルドカードパターン
        String pattern = CID + ".*";

        boolean downloadComplete = false; //ダウンロード終わるまで検索し続ける
        while (!downloadComplete) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, pattern)) {
                // マッチしたファイルを列挙
                if (stream != null) {
                    for (Path entry : stream) {

                        return entry.getFileName();
                    }
                }

            } catch (IOException | DirectoryIteratorException e) {
                e.printStackTrace();
                return null;// 返すpathがない
            }

        }
        return null; // 返すpathがない
    }
}
