import com.sun.tools.javac.util.Pair;

import java.util.*;

/**
 * Created by yurabraiko on 11.05.17.
 */
public class WordGetter {
    Map<String, Double> wordFrequency = new HashMap<String, Double>();
    double maxWordCount = 0;
    double total;

    public void appendString(String str) {
        str = clear(str);
        Arrays.stream(str.split(" ")).forEach(s -> {
            if (s.length() < 2)
                return;
            s = s.toLowerCase();
            double frequency = 0;
            wordFrequency.putIfAbsent(s, frequency);
            frequency = wordFrequency.get(s) + 1;
            wordFrequency.put(s, frequency);
            if (frequency > maxWordCount) {
                maxWordCount = frequency;
            }
            total++;
        });
    }

    private String clear(String str) {
        return str
                .replaceAll("[^A-Za-z]", " ")
                .replaceAll("  ", " ")
                .replaceAll("\\b\\w{1,2}\\b\\s?", "")
                ;
    }

    private Map<String, Double> normolize(Map<String, Double> words) {
        Map<String, Double> result = new HashMap<>();
        double k = getNormolizeScaleK();
        words.forEach((s, aDouble) -> result.put(s, (aDouble * k) / total));
        Map<String, Double> sortedResult = Utils.sortByValue(result, (o1, o2) -> {
            if (o1.equals(o2))
                return 0;
            return o1 > o2 ? -1 : 1;
        });
        return sortedResult;
    }

    private double getNormolizeScaleK() {
        return (double) ((long) (total / maxWordCount));
    }

    private double mean(Map<String, Double> words) {
        final double[] result = {0};
        words.forEach((s, aDouble) -> result[0] += aDouble);
        return result[0] / ((double) words.size());
    }

    void getWords() {
        Map<String, Double> words = normolize(wordFrequency);
        double mean = mean(words);
        double d = getDispersion(words, mean);
        words = removeLeftSideOutDispersion(mean, d, words);
        mean = mean(words);
        d = getDispersion(words, mean);
        Pair<Map<String, Double>, Map<String, Double>> splitResult = splitWithE(mean, d, words);

//        splitResult.fst.forEach((s1, aDouble) -> System.out.println(s1));

        getSortedWordListAroundMean(splitResult.fst).forEach(System.out::println);
    }

    private Pair<Map<String, Double>, Map<String, Double>> splitWithE(double mean, double dispersion, Map<String, Double> input) {
        Map<String, Double> group = new HashMap<>();
        Map<String, Double> source = new HashMap<>();
        input.forEach((s, aDouble) -> {
            if (Math.abs(aDouble - mean) < dispersion) {
                group.put(s, aDouble);
            } else {
                source.put(s, aDouble);
            }
        });

        return new Pair<>(group, source);
    }

    private Pair<Map<String, Double>, Map<String, Double>> splitBySize(double mean, Map<String, Double> input) {
        Map<String, Double> left = new HashMap<>();
        Map<String, Double> right = new HashMap<>();
        input.forEach((s, aDouble) -> {
            if (aDouble < mean) {
                left.put(s, aDouble);
            } else {
                right.put(s, aDouble);
            }
        });
        return new Pair<>(left, right);
    }

    private Map<String, Double> removeLeftSideOutDispersion(double mean, double d, Map<String, Double> input) {
        HashMap<String, Double> result = new HashMap<>();
        input.forEach((s, aDouble) -> {
            if (aDouble > mean + d || aDouble < mean - d)
                return;
            result.put(s, aDouble);
        });
        return result;
    }

    private double getDispersion(Map<String, Double> words, double mean) {
        final double[] sTemp = {0};
        words.forEach((s1, aDouble) -> sTemp[0] += Math.pow((mean - aDouble), 2));
//        return Math.sqrt(sTemp[0] / (words.size() - 1));
        return Math.sqrt(sTemp[0] / (words.size() - 1));
    }

    /**
     * @param words sorted by frequency words
     * @return
     */
    private List<String> getSortedWordListAroundMean(Map<String, Double> words) {
        List<String> result = new LinkedList<>();
        Object[] wordsName = words.keySet().toArray();
        int mean = wordsName.length / 2;
        int leftPos;
        int rightPos;
        for (int i = 0; i < wordsName.length - mean && mean - i >= 0; i++) {
            leftPos = i;
            rightPos = wordsName.length - 1 - i;
            if (leftPos == rightPos)
                break;
            result.add(0,(String) wordsName[leftPos]);
            result.add(0,(String) wordsName[rightPos]);
        }
        return result;
    }
}
