package ru.hse.crawler;

import org.apache.commons.validator.routines.UrlValidator;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class SimpleCrawler {

    private final UrlValidator urlValidator = new UrlValidator();
    private final Queue<URL> queue = new LinkedList<>();
    private final Set<URL> visited = new HashSet<>();

    public SimpleCrawler(URL url) {
        queue.add(url);
        dfs();
    }

    private void dfs() {
        while(!queue.isEmpty()) {
            URL url = queue.poll();
            try {
                if (!visited.contains(url)) {
                    visited.add(url);
                    System.out.print(url.toString() + " - ");

                    final Document document;
                    try {
                        document = Jsoup.connect(url.toString()).get();
                    } catch (UnsupportedMimeTypeException | HttpStatusException ignored) {
                        System.out.println(new ArrayList<>());
                        continue;
                    }

                    List<String> listOfUrls = new ArrayList<>();

                    for(var elem : document.select("[href]")) {
                        String currUrlText = elem.attr("abs:href");
                        try {
                            if (urlValidator.isValid(currUrlText)) {
                                listOfUrls.add(currUrlText);
                                queue.add(new URL(currUrlText));
                            }
                        } catch (MalformedURLException ignored) {}
                    }
                    System.out.println(listOfUrls);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        String start = "https://www.jetbrains.com/";
        SimpleCrawler crawler = new SimpleCrawler(new URL(start));

    }
}
