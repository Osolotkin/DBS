import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private List<Item> items;

    Menu() {

        items = new ArrayList<Item>();

    }

    public void addItem(String name, MExecution me) {
        items.add(new Item(name, items.size() + 1, me));
    }

    private class Item {

        int id;
        String name;

        MExecution me;

        Item(String name, int id, MExecution me) {
            this.name = name;
            this.id = id;
            this.me = me;
        }

        public void execute(String[] args) {
            me.execute(args);
        }

        public void render() {
            System.out.println(id + ") " + name);
        }

    }

    public int checkForInput() {
        Scanner sc = new Scanner(System.in);

        while (true) {

            String[] args = sc.nextLine().split(" ");
            int choice = Utils.str2int(args[0]);
            if (choice > 0 && choice <= items.size()) {
                items.get(choice - 1).execute((args.length > 1) ? Arrays.copyOfRange(args, 1, args.length) : null);
                return choice;
            } else {
                if (!args[0].equals(""))
                    Utils.writeError("Wrong input!");
            }

        }
    }

    public void render() {

        System.out.println();
        for (int i = 0; i < items.size(); i++) {
            items.get(i).render();
        }
        System.out.println();

    }

}

abstract class MExecution {

    protected static final String HELP = "help";

    protected static void writeHelp(String desc, String[] paramsDesc) {
        System.out.println(Utils.NEW_LINE + desc);

        if (paramsDesc != null) {
            for (int i = 0; i < paramsDesc.length; i += 2) {
                System.out.println(Utils.TAB + paramsDesc[i] + ": " + paramsDesc[i + 1]);
            }
        }

        System.out.println();
    }

    protected static boolean nullCheck(String[] args) {
        if (args == null) {
            Utils.writeError("Wrong set of parameters!");
            return true;
        }
        return false;
    }

    public abstract void execute(String[] args);

}
