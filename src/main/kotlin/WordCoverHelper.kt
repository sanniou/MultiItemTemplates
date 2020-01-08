import java.util.regex.Matcher
import java.util.regex.Pattern

const val LAYOUT_PREFIX = "item_"
const val ITEM_SUFFIX = "Item"

fun camel2Underline(line: String): String {
    var itemName = line.replace(ITEM_SUFFIX, "")
    if (itemName.isBlank()) {
        return LAYOUT_PREFIX + ""
    }
    itemName = itemName.first().toUpperCase() + (itemName.substring(1))
    val sb = StringBuilder()
    val pattern = Pattern.compile("[A-Z]+([a-z\\d]+)?")
    val matcher = pattern.matcher(itemName)
    while (matcher.find()) {
        val word = matcher.group()
        sb.append(word.toLowerCase())
        sb.append(if (matcher.end() == itemName.length) "" else "_")
    }
    return LAYOUT_PREFIX + sb.toString()
}

fun underline2Camel(line: String): String {
    val layoutName = line.replace(LAYOUT_PREFIX, "")
    if (layoutName.isBlank()) {
        return "" + ITEM_SUFFIX
    }
    val sb = StringBuffer()
    val pattern = Pattern.compile("([A-Za-z\\d]+)(_)?")
    val matcher: Matcher = pattern.matcher(layoutName)
    //匹配正则表达式
    while (matcher.find()) {
        val word: String = matcher.group()
        //当是true 或则是空的情况
        if (matcher.start() == 0) {
            sb.append(Character.toLowerCase(word[0]))
        } else {
            sb.append(Character.toUpperCase(word[0]))
        }
        val index = word.lastIndexOf('_')
        if (index > 0) {
            sb.append(word.substring(1, index).toLowerCase())
        } else {
            sb.append(word.substring(1).toLowerCase())
        }
    }
    return sb.toString() + ITEM_SUFFIX
}