package org.example;

import java.io.File;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        Word word = new Word();

        long start = System.currentTimeMillis();
        File file = new File(args[0]);
        List<Set<String>> groups = word.readFile(file);
        word.writeToFile(groups);
        long finish = System.currentTimeMillis();
        long result = finish - start;

        System.out.println("Время выполнения, мс: " + result);
    }

}