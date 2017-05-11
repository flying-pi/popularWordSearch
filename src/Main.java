import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        WordGetter getter = new WordGetter();
        Path path = FileSystems.getDefault().getPath("/Users/yurabraiko/dev/java/LearningWordSelectTest/textSource");

        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(s -> getter.appendString(s));
        } catch (IOException e) {
        }
        getter.getWords();
    }
}
