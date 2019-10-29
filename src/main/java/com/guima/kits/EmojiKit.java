package com.guima.kits;

/**
 * Created by Ran on 2018/7/29.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微信过滤表情
 * @author hsw
 *
 */
public class EmojiKit {

    public static boolean isEmoji(String source) {
        if (Kit.isNull(source)) {
            return false;
        }
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(source);
        if (emojiMatcher.find()) {
            return true;
        }
        return false;
    }
}
