package ru.hse.crawler;

import org.apache.commons.validator.routines.UrlValidator;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class WebSpider {

    private final Set<URL> links = new HashSet<>();
    private final UrlValidator urlValidator = new UrlValidator();

    private WebSpider(final URL startURL) {
        crawl(initURLS(startURL));
    }

    private void crawl(final Set<URL> urls) {
        urls.removeAll(this.links);
        if(!urls.isEmpty()) {

            final Set<URL> newURLS = new HashSet<>();
            try {
                this.links.addAll(urls);
                for(final URL url : urls) {
                    System.out.print(url.toString() + " - ");

                    final Document document;

                    try {
                        document = Jsoup.connect(url.toString()).get();
                    } catch (final UnsupportedMimeTypeException | HttpStatusException ignored) {
                        System.out.println(new ArrayList<>());
                        continue;
                    }

                    final Elements linksOnPage = document.select("[href]");
                    List<String> listOfHrefs = new ArrayList<>();

                    for(final Element element : linksOnPage) {
                        final String urlText = element.attr("abs:href");
                        try {
                            if (urlValidator.isValid(urlText)) {
                                listOfHrefs.add(urlText);
                                final URL discoveredURL = new URL(urlText);
                                newURLS.add(discoveredURL);
                            }
                        } catch (final MalformedURLException ignored) {}
                    }
                    System.out.println(listOfHrefs);
                }
            } catch(final IOException e) {
                System.err.println(e.getMessage());
            }
            crawl(newURLS);
        }
    }

    private Set<URL> initURLS(final URL startURL) {
        return Collections.singleton(startURL);
    }

    public static void main(String[] args) throws IOException {

        String start = "https://www.jetbrains.com/";
        String startMalformedURLException = "https://www.jetbrains.com/lp/devecosystem-2021/";
        String startNotEmpty = "https://www.jetbrains.com/products/#lang=js";
        String startHttpStatusException = "https://cloud.typography.com/7463094/7169552/css/fonts.css";
        String startEmptyList = "https://www.jetbrains.com/favicon-16x16.png";

        final WebSpider spider = new WebSpider(new URL(start));

    }

}
