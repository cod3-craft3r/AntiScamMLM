import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class inMemory {
    static HashMap<String, HashSet<String>> map = new HashMap<>();

    public static boolean add_member(String name) {
        if(!map.containsKey(name)) {
            HashSet<String> s = new HashSet<>();
            map.put(name, s);
        }

        if(map.containsKey(name))
            return true;
        return false;
    }
    
    public static void add_referal(String name, String referal) {
        Scanner in = new Scanner(System.in);

        if(map.containsKey(name)) {
            HashSet<String> lst = map.get(name);

            {
                boolean token = add_member(referal);
                if(!token)
                    System.out.println("some error occurred while adding "+referal);
                lst.add(referal);
                map.put(name, lst);
                {
                    HashSet<String> temp = map.get(name);
                    if(temp.size() == 5) {
                        System.out.println("member now has 5 referals, they're now out of the system...");
                        map.remove(name, map.get(name));
                    }
                }
            }
        }
        else {
            System.out.println("this member doesn't exist in the system");
            System.out.print("do you wish to add this member?(yes/no) ");
            String ans = in.nextLine();

            if(ans.equals("yes")) {
                if(add_member(name)) {
                    HashSet<String> lst = new HashSet<>();
                    lst.add(referal);
                    map.put(name, lst);
                }
            }
            else if(ans.equals("no"))
                in.close();
            else if(!ans.equals("yes") && !ans.equals("no")) {
                in.close();
                return;
            }
        }
    }

    public static void main(String[] args) {
        add_member("arpit");

        String refs[] = {"avishake", "abhinav", "apoorv", "ankit"};

        add_member("arpit");

        for (String ref : refs) {
            add_referal("arpit", ref);
        }

        System.out.println(map);
        add_referal("arpit", "hari");

        System.out.println(map);
    }
}