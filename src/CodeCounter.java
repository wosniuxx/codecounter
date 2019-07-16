import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @description: 代码统计行数 不使用jar包
 * 给入指定路径。遍历该该路径下所有文件
 * 将所有文件汇总，开始遍历代码
 * 按照除了空格，空行，注释等之外的代码均为有效代码，进行统计
 *
 * 使用方法
 * ```
 * javac CodeCounter.java
 * java CodeCounter
 * 根据提示输入
 * ```
 *
 * @author: Niu Haoxuan
 * @create: 2019-07-13 06:50
 **/
public class CodeCounter {
  // 代码行数
  private static long codeLines = 0;
  // 空行行数
  private static long blankLines = 0;
  // 注释行数
  private static long commentLines = 0;
  // 文件数
  private static Set<File> fileSet = new HashSet<>();

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("请输入文件名称或文件夹路径");
    String path = sc.nextLine();
    System.out.println("请输入请输入需要统计的文件类型，不输入默认全部统计");
    String codeType = sc.nextLine();

    codeType = Objects.nonNull(codeType) && (!codeType.trim()
        .isEmpty()) ? codeType.trim() : null;

    File originFile = new File(path);
    if (!originFile.exists()) {
      throw new RuntimeException("文件路径不存在");
    }

    getFile(originFile, codeType);
    fileSet.forEach(CodeCounter::countStart);

    System.out.println("有效代码行数 is " + codeLines);
    System.out.println("空行数 is " + blankLines);
    System.out.println("注释数 is " + commentLines);
    System.out.println("源文件总行数 is " + (codeLines + blankLines + commentLines));
  }

  // 获取所有文件
  private static void getFile(File originFile, String codeType) {
    File[] childFiles = originFile.listFiles();
    if (Objects.isNull(childFiles)) {
      childFiles = new File[]{originFile};
    }
    Stream.of(childFiles)
        .forEach(file -> {
          if (file.isDirectory()) {
            getFile(file, codeType);
          } else if (Objects.isNull(codeType) || file.getName()
              .endsWith(codeType)) {
            fileSet.add(file);
          }
        });
  }

  // 统计代码行数
  private static void countStart(File file) {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));
      String line;
      boolean flag = false;
      while (Objects.nonNull(line = br.readLine())) {
        line.trim();
        // 匹配空格
        if (line.matches("^[ ]*$")) {
          blankLines++;
        }
        // 匹配注释
        else if (line.startsWith("//")) {
          commentLines++;
        } else if (line.startsWith("/*") && line.endsWith("*/")) {
          commentLines++;
        } else if (line.startsWith("/*") && !line.endsWith("*/")) {
          commentLines++;
          flag = true;
        } else if (flag) {
          commentLines++;
          if (line.endsWith("*/")) {
            flag = false;
          }
        }
        // 匹配有效代码行数
        else {
          codeLines++;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (Objects.nonNull(br)) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}