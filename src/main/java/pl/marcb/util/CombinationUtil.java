package pl.marcb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinationUtil {
    private List<List<String>> resultCombinations = new ArrayList<>();

    private CombinationUtil() {
    }

    public static List<List<String>>  getCombinations(List<String> list) {
        return new CombinationUtil().getCombinationsForList(list);
    }

    private List<List<String>>  getCombinationsForList(List<String> list) {
        resultCombinations = new ArrayList<>();
        String[] array = new String[list.size()];
        array = list.toArray(array);

        for (int i = 1; i < list.size(); i++) {
            combinations(array, i, 0, new String[i]);
        }
        return resultCombinations;
    }

    private void combinations(String[] arr, int len, int startPosition, String[] result){
        if (len == 0){
            resultCombinations.add(new ArrayList<>(Arrays.asList(result)));
            return;
        }
        for (int i = startPosition; i <= arr.length-len; i++){
            result[result.length - len] = arr[i];
            combinations(arr, len-1, i+1, result);
        }
    }
}
