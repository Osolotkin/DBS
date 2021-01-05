import java.io.*;

// not a fancy way, but at least it works somehow
public class Config {

    private static final String cfgPath = "config.cfg";

    public static boolean VERBOSE = true;
    public static int DEFAULT_TOP_ROWS = 100;
    public static int DEFAULT_COLUMN_WIDTH = 12;
    public static char SKIP_CHAR = '-';
    public static Database.Align ALIGNMENT = Database.Align.LEFT;

    public static int load() {
        try (BufferedReader br = new BufferedReader(new FileReader(cfgPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line.trim();
                String args[] = line.split("\\s+", 2);
                if (args.length < 2) continue;
                args[1] = args[1].trim();

                if (args[0].equalsIgnoreCase("verbose")) {
                    VERBOSE = Boolean.parseBoolean(args[1]);
                } else if (args[0].equalsIgnoreCase("default_top_rows")) {
                    DEFAULT_TOP_ROWS = Utils.str2int(args[1]);
                } else if (args[0].equalsIgnoreCase("default_column_width")) {
                    DEFAULT_COLUMN_WIDTH = Utils.str2int(args[1]);
                } else if (args[0].equalsIgnoreCase("skip_char")) {
                    SKIP_CHAR = args[1].charAt(0);
                } else if (args[0].equalsIgnoreCase("ALIGNMENT")) {
                    if (args[1].equalsIgnoreCase("RIGHT")) {
                        ALIGNMENT = Database.Align.RIGHT;
                    } else if (args[1].equalsIgnoreCase("CENTER")) {
                        ALIGNMENT = Database.Align.CENTER;
                    } else if (args[1].equalsIgnoreCase("LEFT")) {
                        ALIGNMENT = Database.Align.LEFT;
                    }
                }

            }

            return 0;
        } catch (IOException e){
            e.printStackTrace();
            return 1;
        }
    }

}
