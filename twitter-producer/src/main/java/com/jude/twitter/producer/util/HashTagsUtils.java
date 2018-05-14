package com.jude.twitter.producer.util;

import java.util.*;
import java.util.regex.*;

public class HashTagsUtils {
    private static final String Regex_Pattern = "#\\w+";
    private static final Pattern HASH_TAG_PATTERN = Pattern.compile(Regex_Pattern);

    public static Iterator<String> hashTagsFromTweet(String text) {
        List<String> hashTags = new ArrayList<>();
        Matcher matcher = HASH_TAG_PATTERN.matcher(text);
        while (matcher.find()) {
            String handle = matcher.group();
            hashTags.add(handle);
        }
        return hashTags.iterator();
    }
}
