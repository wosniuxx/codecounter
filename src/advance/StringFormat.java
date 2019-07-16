package advance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description:
 * 我们程序中用到了一个数组 a ，数组的每个元素都是一个字典（map/dict）。
 * 字典的 key/value 都是字符串，字符串中可包含任意字符。
 *
 * 示例:
 *
 *     a[0]["k1"] = "v1"
 *     a[0]["k2"] = "v2"
 *     a[1]["A"] = "XXX"
 *     ...
 *
 * 实际使用过程中，我们自定义了一个基于字符串的存储结构，数组元素之间用“换行”分割，
 * 字典元素之间使用“分号”分割， key/value 之间用“等号”分割。
 * 上述数据序列化之后，应该得到一个字符串：
 *
 *     "k1=v1;k2=v2\nA=XXX"
 *
 * 请实现一个“保存”函数、一个“加载”函数。
 *
 *     text = store(a); //把数组保存到一个字符串中
 *     b = load(text);  //把字符串中的内容读取为字典数组
 *     预期结果 b == a （b与a内容完全一样）
 *
 * 请考虑所有边界情况（任意字符、空内容等），不要出现bug。
 * 在满足上述需求的前提下，可自行增加一些规则和约定。
 *
 *
 * @author: Niu Haoxuan
 * @create: 2019-07-15 19:21
 **/
public class StringFormat {

  public static String store(Map<String, String>[] array) {
    if (Objects.isNull(array)) {
      throw new RuntimeException("param is error");
    }
    List<String> rst = Stream.of(array)
        .map(map -> {
          List<String> rstList = map.entrySet()
              .stream()
              .filter(Objects::nonNull)
              .map(entry -> entry.getKey() + "=" + entry.getValue())
              .collect(Collectors.toList());
          return org.apache.commons.lang3.StringUtils.join(rstList, ";");
        })
        .collect(Collectors.toList());
    return org.apache.commons.lang3.StringUtils.join(rst, "\\n");
  }

  public static Map<String, String>[] load(String text) {
    String[] array = text.split("\\\\n");
    return Stream.of(array)
        .map(str -> Stream.of(str.split(";"))
            .map(entryStr -> entryStr.split("="))
            .filter(strings -> strings.length == 2)
            .collect(Collectors.toMap(entryArray -> entryArray[0], entryArray -> entryArray[1])))
        .toArray((IntFunction<Map<String, String>[]>) Map[]::new);
  }

  public static void main(String[] args) {
    Map<String, String> map1 = new HashMap<>();
    Map<String, String> map2 = new HashMap<>();
    map1.put("k1", "v1");
    map1.put("k2", "v2");
    map2.put("z1", "zv1");
    map2.put("z2", "zv2");

    Map<String, String>[] array = new Map[2];
    array[0] = map1;
    array[1] = map2;


    String formatter = StringFormat.store(array);
    System.out.println("序列化参数---------->" + formatter);
    Map<String, String>[] load = StringFormat.load(formatter);
    System.out.println("参数加载---------->" + Arrays.toString(load));
  }

}
