package com.company;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
        // читаем контент из основного источника
        String book = Files.readString(Paths.get("src/com/company/resources/doc.txt"), Charset.defaultCharset()).toUpperCase(Locale.ROOT);
        List<String> keyWords = Files.readAllLines(Paths.get("src/com/company/resources/slang.txt"), Charset.defaultCharset());

        // нормализуем данные
        final String normalizedBook = book.replace("\r\n", " ").toUpperCase(Locale.ROOT);

        Map<String, Integer> wordCounterMap = new HashMap<>();
        final List<String> matches = new ArrayList<>();

        // поиск всех вхождений ключевых фраз в источник
        for (String keyWord : keyWords) {
            // паттерн с look ahead и negative look behind подходом
            final Matcher m = Pattern.compile("[.!?](?:(?!\\s" + keyWord + "\\s|[.!?]).)*\\s" + keyWord + "\\s(?:(?![.!?]).)*[.!?]")
                    .matcher(normalizedBook);
            int counter = 0;
            while (m.find()) {
                matches.add(m.group(0));
                counter++;
            }
            // подсчитываем количество вхождений каждого ключевого слова
            wordCounterMap.put(keyWord, counter);
        }

        // собираем результат из списка ключевых слов в контексте предложения и статистики по ключевым словам
        StringBuilder sb = new StringBuilder();
        matches.stream().distinct()
                .map(s -> s.substring(1))
                .forEach(s -> sb.append(s).append("\n"));
        sb.append("\n------------------------------------------\nSuperStatistics\n");
        wordCounterMap.forEach((key, value) -> sb.append(key).append(" - ").append(value).append("\n"));

        // запись результата в txt файл
        Path file = Paths.get("src/com/company/resources/result.txt");
        Files.writeString(file, sb.toString());
    }
}