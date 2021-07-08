import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 使用stream时通过Comparator进行快捷排序
 */
public class StreamSort {
    public static void main(String[] args) {
        /**
         * 几种初始化方式
         */
//        // 生成的list不可变
//        List<Integer> list = Arrays.asList(4,9,3,8,22,1,5);
//        // 编译不会报错，运行时会报UnsupportedOperationException
//        list.add(7);
//        // 如果要可变需要用ArrayList包装一下
//        List<Integer> list = new ArrayList<>(Arrays.asList(4,9,3,8,22,1,5));
//        list.add(7);

        /**
         * 生成的也是不可变对象
         */
//        List<Integer> list =  ImmutableList.of(4,9,3,8,22,1,5);
//        list.add(7);

        // Java8 Stream初始化
        List<Integer> list = Stream.of(4,9,3,8,22,1,5).collect(Collectors.toList());
        list.add(7);
        System.out.println(list);

        Collections.sort(list);
        System.out.println(list);

        Collections.sort(list, Collections.reverseOrder());
        System.out.println(list);

        List<Integer> list1 = list.stream().sorted((m,n) -> m -n).collect(Collectors.toList());
        System.out.println(list1);

        List<Integer> list2 = list.stream().sorted((m,n) -> n - m).collect(Collectors.toList());
        System.out.println(list2);

        List<Integer> list3 = list.stream().sorted(Comparator.comparingInt(Integer::intValue)).collect(Collectors.toList());
        System.out.println(list3);

        List<Integer> list4 = list.stream().sorted(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        }).collect(Collectors.toList());
        System.out.println(list4);
    }
}
