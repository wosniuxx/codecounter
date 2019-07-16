package advance;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @description: 路径规划
 * <p>
 * 我们有一个有向无环图，权重在节点上。
 * 需求：从一个起点开始，找到一条节点权重之和最大的最优路径。
 * 输入: n个节点，m个路径，起点
 * 输出: 最优路径的权重值之和
 * <p>
 * 举例:
 * 3个节点与权重: A=1, B=2, C=2
 * 3条路径: A->B, B->C, A->C
 * 起点: A
 * 输出: 5 (最优路径是 A->B->C ， 权重之和是 1+2+2=5)
 * <p>
 * 请考虑算法效率尽可能高一些，考虑异常情况（比如输入的图有环路）要避免死循环或者崩溃。
 * @author: Niu Haoxuan
 * @create: 2019-07-15 20:12
 **/
public class PathDesignSingle {

  public static void main(String[] args) {
    Node a = new Node("a", 1);
    Node b = new Node("b", 2);
    Node c = new Node("c", 2);
    Node d = new Node("d", 8);
    Node[] nodes = {a, b, c, d};
    double[][] matrix = {
        {0, 1, 1, 1}, {0, 0, 1, 1}, {0, 0, 0, 1}, {0, 0, 0, 0}
    };

    PathDesignSingle.Graph graph = new PathDesignSingle.Graph(matrix, nodes, 0);
    System.out.println("最大权重路径是：" + graph.getMaxWeight(graph.startSearch())
        .orElse(0));
  }

  public static class Node {
    String nodeName;
    int weight;

    public String getNodeName() {
      return nodeName;
    }

    public void setNodeName(String nodeName) {
      this.nodeName = nodeName;
    }

    public int getWeight() {
      return weight;
    }

    public void setWeight(int weight) {
      this.weight = weight;
    }

    public Node(String nodeName, int weight) {
      this.nodeName = nodeName;
      this.weight = weight;
    }
  }

  public static class Graph {
    // 邻接矩阵
    private double[][] matrix;
    // 初始顶点在数组位置
    private int startIndex;
    // 全部端点
    private Node[] nodes;
    // 顶点的数目
    private int nodeNum;
    // 当前结点是否还有下一个结点，判断递归是否结束的标志
    private boolean noNext = false;
    // 所有路径的结果集
    private List<List<Node>> result = new ArrayList<>();

    public Graph(double[][] matrix, Node[] nodes, int startIndex) {
      if (matrix.length != matrix[0].length) {
        throw new IllegalArgumentException("该邻接矩阵不是方阵");
      }
      if (matrix.length != nodes.length) {
        throw new IllegalArgumentException("结点数量和邻接矩阵大小不一致");
      }
      this.nodes = nodes;
      this.matrix = matrix;
      this.startIndex = startIndex;
      this.nodeNum = nodes.length;
    }

    public List<List<Node>> startSearch() {
      IntStream.range(0, countPathNumber())
          .forEach(value -> {
            // 用于存储遍历过的点
            List<Node> path = new LinkedList<>();
            noNext = false;
            // 开始遍历
            DFS(0, path);
            // 保存结果
            result.add(path);
          });
      return result;
    }

    private void DFS(int begin, List<Node> path) {
      // 将当前结点加入记录队列
      path.add(nodes[begin]);
      // 标记回滚位置
      int rollBackNum = -1;
      // 遍历相邻的结点
      for (int i = 0; i < nodeNum; i++) {
        if ((matrix[begin][i] > 0)) {
          // 临时加入相邻结点，试探新的路径是否已遍历过
          path.add(nodes[i]);
          if (containBranch(result, path)) {
            // 路径已存在，将相邻结点再移出记录队伍
            path.remove(nodes[i]);
            // 记录相邻点位置，用于循环结束发现仅有当前一个相邻结点时回滚事件
            rollBackNum = i;
            // 寻找下一相邻结点
            return;
          } else {
            // 路径为新路径，准备进入递归，将相邻结点移出记录队伍，递归中会再加入，防止重复添加
            path.remove(nodes[i]);
            // 递归
            DFS(i, path);
          }
        }
        // 终止递归
        if (noNext) {
          return;
        }
      }

      // 当前结点没有相邻结点，设置flag以结束递归
      noNext = true;
    }

    public Optional<Integer> getMaxWeight(List<List<Node>> lists) {
      return lists.stream()
          .map(currentPath -> currentPath.stream()
              .mapToInt(Node::getWeight)
              .sum())
          .max(Integer::compareTo);
    }

    // 计算从头节点开始，有多少路径
    private int countPathNumber() {
      int[] numberArray = new int[nodeNum];
      IntStream.range(0, nodeNum)
          .forEach(value -> {
            if (matrix[value][startIndex] > 0) {
              numberArray[value]++;
            }
          });

      return (int) IntStream.range(0, nodeNum)
          .filter(value -> numberArray[value] > 1)
          .count() + 1;
    }

    // 判断当前路径是否被已有路径的结果集合所包含
    private boolean containBranch(List<List<Node>> nodeLists, List<Node> edges) {
      return nodeLists.stream()
          .anyMatch(record -> record.containsAll(edges));
    }

  }


}
