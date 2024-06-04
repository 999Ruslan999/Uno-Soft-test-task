package org.example;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class Word {

    public String value;

    public int number;

    public Word() {
    }

    public Word(String value, int number) {
        this.value = value;
        this.number = number;
    }

    /*
    Метод считывает файл, разделяет строки убирает лишние знаки, нулевые элементы и возвращает множество строк
     */
    public List<Set<String>> readFile(File file) {

        List<Map<String, Integer>> words = new ArrayList<>();

        Map<Integer, Integer> mergedGroup = new HashMap<>();

        List<Set<String>> groups = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] numbers = line.split(";");

                for (int i = 0; i < numbers.length; i++) {

                    if (numbers[i].startsWith("\"") && numbers[i].endsWith("\"") && numbers[i].startsWith(";") && numbers[i].endsWith(";") && numbers[i].length() > 1) {

                        numbers[i] = numbers[i].substring(1, numbers[i].length() - 1);
                    }
                }

                if (!Arrays.stream(numbers).allMatch(str -> NumberUtils.isParsable(str) || str.isEmpty())) {
                    continue;
                }

                TreeSet<Integer> found = new TreeSet<>();
                List<Word> newWords = new ArrayList<>();

                for (int i = 0; i < numbers.length; i++) {

                    String number = numbers[i];

                    if (words.size() == i) {
                        words.add(new HashMap<>());
                    }

                    if (number.isEmpty()) {
                        continue;
                    }

                    Integer groupNumber = words.get(i).get(number);


                    if (groupNumber != null) {

                        while (mergedGroup.containsKey(groupNumber)) {
                            groupNumber = mergedGroup.get(groupNumber);
                        }
                        found.add(groupNumber);

                    } else {
                        newWords.add(new Word(number, i));
                    }
                }
                int groupNumber;

                if (found.isEmpty()) {
                    groupNumber = groups.size();
                    groups.add(new HashSet<>());
                } else {
                    groupNumber = found.first();
                }
                for (Word word : newWords) {
                    words.get(word.number).put(word.value, groupNumber);
                }
                for (int mergeGroupNumber : found) {
                    if (mergeGroupNumber != groupNumber) {
                        mergedGroup.put(mergeGroupNumber, groupNumber);
                        groups.get(groupNumber).addAll(groups.get(mergeGroupNumber));
                        groups.set(mergeGroupNumber, null);
                    }
                }
                groups.get(groupNumber).add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Проблема с чтением файла!");
        }
        groups.removeAll(Collections.singleton(null));

        return groups;
    }

    /*
    Метод принимает множество строк, фильтрует множества, сортирует и записывает данные в файл
     */
    public void writeToFile(List<Set<String>> groups) {

        long count = groups.stream()
                .filter(x -> x.size() > 1)
                .count();

        groups.sort((o1, o2) -> o2.size() - o1.size());

        File file = new File("output.txt");

        int number = 0;

        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write("Группы более чем с одним элементом: " + count + "\n");

            for (Set<String> group : groups) {
                number++;
                writer.write("Группа " + number + "\n");

                for (String line : group) {

                    writer.write(line + "\n");
                }

            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

}
