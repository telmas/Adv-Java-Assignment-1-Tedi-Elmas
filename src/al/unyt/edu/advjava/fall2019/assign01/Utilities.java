package al.unyt.edu.advjava.fall2019.assign01;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Utilities {

    public static List<Map.Entry<String, Long>> getMostFrequentEntries(ConcurrentHashMap<String, Long> hashMap) {
        return getMostFrequentEntries(hashMap, Integer.MAX_VALUE);
    }

    public static List<Map.Entry<String, Long>> getMostFrequentEntries(ConcurrentHashMap<String, Long> hashMap, int topEntriesNumber) {
        return hashMap
                .entrySet()
                .parallelStream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topEntriesNumber)
                .collect(Collectors.toList());
    }

    public static double calculateStandardDeviationOnNumberOfWords(ConcurrentHashMap<String, Long> fileWordCountHashMap) {
        double wordCountMean = fileWordCountHashMap.values().stream().mapToLong(Long::longValue).average().orElse(0d);
        double sum = fileWordCountHashMap.values().stream().mapToDouble(count -> (count - wordCountMean) * (count - wordCountMean)).sum();
        return Math.sqrt(sum / fileWordCountHashMap.size());
    }

    public static double calculateEntropy(ConcurrentHashMap<String, Long> hashMap) {
        return hashMap
                .values()
                .parallelStream()
                .map(count -> (double) count / hashMap
                        .values()
                        .parallelStream()
                        .mapToDouble(Long::doubleValue)
                        .sum())
                .map(frequency -> (frequency * (Math.log(frequency) / Math.log(2))))
                .reduce(0d, Double::sum) * -1;
    }
}
