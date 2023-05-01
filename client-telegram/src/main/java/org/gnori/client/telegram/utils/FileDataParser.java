package org.gnori.client.telegram.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
/**
 * Utils for parsing data from file
 */
public class FileDataParser {
    public static String getTitleFromContent(String content){
        var regexPatternForTitle = Pattern.compile("###(.*)###");
        Matcher matcherTitle = regexPatternForTitle.matcher(content);
        if(matcherTitle.find()){
            return matcherTitle.group(1);
        }
        return null;
    }
    public static Integer getCountForRecipientFromContent(String content){
        var regexPatternForTitle = Pattern.compile("===(\\d+)===");
        Matcher matcherTitle = regexPatternForTitle.matcher(content);
        if(matcherTitle.find()){
            return Integer.valueOf(matcherTitle.group(1));
        }
        return null;
    }
    public static List<String> getRecipientsFromContent(String content){
        var regexPatternForTitle = Pattern.compile(":::((.*|\\n|\\r)+):::");
        Matcher matcherTitle = regexPatternForTitle.matcher(content);
        if(matcherTitle.find()){
            return Arrays.stream(matcherTitle.group(1).split(",")).map(String::trim).collect(Collectors.toList());
        }
        return null;
    }
    public static String getTextFromContent(String content){
        var regexPatternForText = Pattern.compile("///((.*|\\n|\\r)+)///");

        Matcher matcherText = regexPatternForText.matcher(content);
        if(matcherText.find()){
            return matcherText.group(1);
        }
        return null;
    }

    public static String getSentDateFromContent(String content){
        var regexPatternForTitle = Pattern.compile("---(.*)---");
        Matcher matcherTitle = regexPatternForTitle.matcher(content);
        if(matcherTitle.find()){
            return matcherTitle.group(1);
        }
        return null;
    }
}
