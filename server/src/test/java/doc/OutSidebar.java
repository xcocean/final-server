package doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author lingkang
 * @date 2023/3/30
 */
public class OutSidebar {
    public static void main(String[] args) {
        String md = "C:\\Users\\Administrator\\Desktop\\project\\git\\final-server\\doc\\md";

        File file = new File(md);
        if (!file.isDirectory()) {
            System.out.println("不是目录");
            return;
        }

        List<String> list = new ArrayList<>();
        for (File m : file.listFiles()) {
            list.add(m.getName());

        }
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int a = getSort(o1);
                int b = getSort(o2);
                return Integer.compare(a, b);
            }
        });
        for (String m : list) {
            String[] name = m.split("\\.");
            System.out.println("  * [" + name[1] + "](md/" + m + ")");
        }

        System.out.println("--------------------------------------------------------------------");
        for (String m : list) {
            System.out.println("'md/"+m+"',");
        }
    }

    private static int getSort(String str) {
        return Integer.parseInt(str.split("\\.")[0]);
    }
}
