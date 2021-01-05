import java.sql.*;
import java.util.ArrayList;

public class Database {

    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=TMP;integratedSecurity=true;";
    private static final String USERNAME = "LAPTOP-JBI0QNHU\\JustAnUser";
    private static final String PASSWORD = "";

    private Connection connection;

    // returns 0 if no error
    // returns 1 if could not connect
    public int connect() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return 0;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }
    }

    //============================================================================================================\\
    //======================================== kind of general queries ===========================================\\
    //============================================================================================================\\

    public boolean existTable(String name) {
        try {
            String query = "SELECT top 1 * FROM " + name;
            connection.createStatement().executeQuery(query);
            return true;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return false;
        }
    }

    public String[] getColNames(String tableName) {
        try {

            String query = "SELECT top 1 * FROM " + tableName;
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData md = rs.getMetaData();

            int colCount = md.getColumnCount();
            String[] colNames = new String[colCount];
            for (int i = 0; i < colCount; i++) {
                colNames[i] = md.getColumnName(i + 1);
            }

            return colNames;

        } catch (SQLException e) {

            if (Config.VERBOSE) e.printStackTrace();
            return null;

        }
    }

    public String[][] getColNamesAndTypes(String tableName) {
        try {

            String query = "SELECT top 1 * FROM " + tableName;
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData md = rs.getMetaData();

            int colCount = md.getColumnCount();
            String[][] colNamesAndTypes = new String[2][colCount];
            for (int i = 0; i < colCount; i++) {
                colNamesAndTypes[0][i] = md.getColumnName(i + 1);
                colNamesAndTypes[1][i] = md.getColumnTypeName(i + 1);
            }

            return colNamesAndTypes;

        } catch (SQLException e) {

            if (Config.VERBOSE) e.printStackTrace();
            return null;

        }
    }

    // condition[0] - colNames
    // condition[1] - values
    // condition[2] - types
    // condition[3] - operation (>, <, =, etc.), if is null, = will be used in all cases
    public String[][] getRows(String tableName, String[][] condition) {

        if (condition[0].length < 1 || condition[0].length > condition[1].length) return null;

        try {

            String query;
            applyTypes(condition[1], condition[2]);
            if (condition.length > 3 && condition[3].length >= condition[0].length) {
                query = "SELECT * FROM " + tableName + " WHERE " + Utils.strarrs2str(condition[0], condition[1], condition[3], " AND ");
            } else {
                query = "SELECT * FROM " + tableName + " WHERE " + Utils.strarrs2str(condition[0], condition[1], " = ", " AND ");
            }
            // System.out.println(query);
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData md = rs.getMetaData();

            int colCount = md.getColumnCount();

            String[] row = new String[colCount];
            ArrayList<String[]> rows = new ArrayList<String[]>();
            int i;
            while (rs.next()) {
                for (i = 0; i < colCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                rows.add(row.clone());
            }

            String[][] rowsArr = new String[rows.size()][colCount];
            for (i = 0; i < rows.size(); i ++) {
                rowsArr[i] = rows.get(i);
            }

            return rowsArr;

        } catch (SQLException e) {

            if (Config.VERBOSE) e.printStackTrace();
            return null;

        }
    }

    // condition[0] - colNames
    // condition[1] - values
    // condition[2] - types
    public int insertRow(String tableName, String[][] condition) {

        try {

            applyTypes(condition[1], condition[2]);

            String query = "INSERT INTO " + tableName + " (" + Utils.strarr2str(condition[0], ',') + ") VALUES (" + Utils.strarr2str(condition[1], ',') + ")";
            connection.createStatement().executeUpdate(query);

            return 0;

        } catch (SQLException e) {

            if (Config.VERBOSE) e.printStackTrace();
            return 1;

        }

    }

    // condition[0] - colNames
    // condition[1] - values
    // condition[2] - types
    // condition[3] - operation (>, <, =, etc.), if is null, = will be used in all cases
    public int deleteRows(String tableName, String[][] condition) {

        if (condition[0].length < 1 || condition[0].length > condition[1].length) return 2;

        try {

            String query;
            applyTypes(condition[1], condition[2]);
            if (condition.length > 3 && condition[3].length >= condition[0].length) {
                query = "DELETE FROM " + tableName + " WHERE " + Utils.strarrs2str(condition[0], condition[1], condition[3], " AND ");
            } else {
                query = "DELETE FROM " + tableName + " WHERE " + Utils.strarrs2str(condition[0], condition[1], " = ", " AND ");
            }
            connection.createStatement().executeUpdate(query);

            return 0;

        } catch (SQLException e) {

            if (Config.VERBOSE) e.printStackTrace();
            return 1;

        }

    }

    //============================================================================================================\\
    //============================================= special queries ==============================================\\
    //============================================================================================================\\

    public int printTreatmentCases(int rows, int width) {

        try {

            String query =
                    "SELECT \n" +
                    "\tb.first_name AS DOCTOR_first_name, \n" +
                    "\tb.last_name AS DOCTOR_last_name, \n" +
                    "\tc.name AS PET_name,\n" +
                    "\tc.type AS PET_type,\n" +
                    "\td.name AS PROC_name,\n" +
                    "\tcast(d.price as float) / 10 AS [PROC_price (Kč)]\n" +
                    "FROM treatment a\n" +
                    "JOIN doctor b on a.doctor_id = b.id\n" +
                    "JOIN pet c on a.doctor_id = c.id\n" +
                    "JOIN [procedure] d on a.procedure_id = d.id";
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);

            if (renderResultSet(rs, rows, width) != 0) return 2;

            return 0;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }

    }

    public int printProceuresInfo(int petId, int rows, int width) {

        try {

            String query = "SELECT * FROM pet WHERE id = " + petId;
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);

            System.out.println("Chosen pet:");
            if (renderResultSet(rs, 1, width) != 0) return 2;

            query =
                    "SELECT\n" +
                    "\tid, \n" +
                    "\tname, \n" +
                    "\tdescription,\n" +
                    "\tconvert(TIME(0), DATEADD(ms, duration, 0)) AS [duration (DD:HH:SS)],\n" +
                    "\tcast(price as float) / 10 AS [price (Kč)]\n" +
                    "FROM treatment a\n" +
                    "JOIN [procedure] b on a.procedure_id = b.id AND a.pet_id = " + petId;
            st = connection.createStatement();

            System.out.println("It's procedures:");
            rs = st.executeQuery(query);
            if (renderResultSet(rs, rows, width) != 0) return 2;

            return 0;

        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }

    }

    public int changePetOwner(int petId, int newOwnerId) {

        try {
            String query = "UPDATE pet SET owner_id = " + newOwnerId + " WHERE id = " + petId;
            connection.createStatement().executeUpdate(query);
            return 0;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }

    }

    public int printReceipt(int ownerId) {

        try {

            String query = "SELECT \n" +
                    "\tpet_owner.first_name, \n" +
                    "\tpet_owner.last_name, \n" +
                    "\tpet.id AS pet_id, \n" +
                    "\tpet.name AS pet_name, \n" +
                    "\t[procedure].name AS [procedure_name], \n" +
                    "\tcast([procedure].price as float) / 10 AS [price (Kč)] \n" +
                    "FROM pet_owner \n" +
                    "LEFT JOIN pet ON pet.owner_id = pet_owner.id\n" +
                    "LEFT JOIN treatment ON treatment.pet_id = pet.id\n" +
                    "LEFT JOIN [procedure] ON [procedure].id = treatment.procedure_id\n" +
                    "WHERE pet_owner.id = " + ownerId + " \n" +
                    "ORDER BY pet_name";
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);

            // doing first iteration outside the loop as we need to print owner name and dont wanna ask all the time, if
            // it was already printed
            System.out.println();

            final String doubleTAB = Utils.TAB + "" + Utils.TAB;
            double sum = 0;

            rs.next();

            System.out.println("Owner name: " + rs.getString(1) + " " + rs.getString(2));
            System.out.println();

            System.out.println(Utils.TAB + "Pet name: " + rs.getString(4));

            double price = Utils.str2double(rs.getString(6));
            System.out.println(doubleTAB + "Procedure name: " + rs.getString(5) + ", price: " + price + " Kč");
            sum += price;

            String petId = rs.getString(3);
            while (rs.next()) {
                String tmp = rs.getString(3);
                if (!tmp.equals(petId)) {
                    System.out.println();
                    System.out.println(Utils.TAB + "Pet name: " + rs.getString(4));
                    petId = tmp;
                }

                price = Utils.str2double(rs.getString(6));
                System.out.println(doubleTAB + "Procedure name: " + rs.getString(5) + ", price: " + price + " Kč");
                sum += price;
            }

            System.out.println();
            System.out.println("Summa: " + sum + " Kč");
            System.out.println();

            return 0;

        } catch (SQLException e) {

            if (Config.VERBOSE) e.printStackTrace();
            return 1;

        }

    }

    //============================================================================================================\\
    //============================================ requested queries =============================================\\
    //============================================================================================================\\

    // (SELECT inside SELECT)
    public int printPetTreatmentCount(int rows, int width) {

        try {
            String query = "SELECT name, treatment_count = (SELECT COUNT(pet_id) FROM treatment WHERE treatment.pet_id = pet.id) FROM pet ORDER BY treatment_count DESC";
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);
            if (renderResultSet(rs, rows, width) != 0) return 2;

            return 0;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }

    }

    // (SELECT inside FROM)
    public int printProceduresDrugCountInt(int min, int max, int rows, int width) {

        try {
            String query = "SELECT [procedure].name, drug_count\n" +
                    "FROM (\n" +
                    "\tSELECT procedure_id, count(drug_id) AS drug_count \n" +
                    "\tFROM procedure_drug \n" +
                    "\tGROUP BY procedure_id \n" +
                    ") AS procedure_drug_count\n" +
                    "LEFT JOIN [procedure] ON [procedure].id = procedure_id\n" +
                    "WHERE drug_count > " + min + " AND drug_count < " + max + "\n" +
                    "ORDER BY drug_count DESC";
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);
            if (renderResultSet(rs, rows, width) != 2) return 2;

            return 0;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }

    }

    // (SELECT inside WHERE)
    // true - prints procedures above and equals to the average price
    // false - prints procedures lower than average price
    public int printProceduresAverPrice(boolean isAbove, int rows, int width) {

        try {
            String query = "SELECT id, name, cast(price as float) / 10 AS [price (Kč)] FROM [procedure] WHERE price " + ((isAbove) ? ">=" : "<") + " (SELECT AVG(price) FROM [procedure])";
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);
            if (renderResultSet(rs, rows, width) != 0) return 2;


            return 0;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }

    }

    // (usage of GROUP BY)
    public int printPetOwnersPetCount(int rows, int width) {

        try {
            String query =
                    "SELECT first_name, last_name, pet_count\n" +
                    "FROM (\n" +
                    "\tSELECT owner_id, count(owner_id) AS pet_count FROM pet GROUP BY owner_id\n" +
                    ") AS id_count\n" +
                    "LEFT JOIN pet_owner ON owner_id = pet_owner.id\n" +
                    "ORDER BY pet_count DESC";
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);
            if (renderResultSet(rs, rows, width) != 2) return 2;


            return 0;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }

    }

    // (set operation)
    public int printNoActionDoctors(int rows, int width) {

        try {
            String query = "SELECT id, first_name, last_name \n" +
                    "FROM doctor\n" +
                    "WHERE id IN (SELECT DISTINCT id FROM doctor EXCEPT (SELECT DISTINCT doctor_id AS id FROM treatment))";
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);
            if (renderResultSet(rs, rows, width) != 0) return 2;

            return 0;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }
    }

    // (LEFT JOIN)
    public int printDoctorsForThePet(int petId, int rows, int width) {

        try {
            String query = "SELECT first_name, last_name FROM doctor LEFT JOIN treatment ON doctor_id = id WHERE pet_id = " + petId;
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);
            if (renderResultSet(rs, rows, width) != 0) return 2;

            return 0;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }

    }

    //============================================================================================================\\
    //====================================== other sometimes useful stuff ========================================\\
    //============================================================================================================\\

    public void renderRows(String rows[][], String[] colNames, int width) {

        Align align = Align.LEFT;

        System.out.println();

        if (colNames != null) {
            System.out.print(" | ");
            for (int i = 0; i < colNames.length; i++) {
                printString(colNames[i], width, align);
                System.out.print(" | ");
            }
            System.out.println();

            System.out.print(" ");
            System.out.print(new String(new char[colNames.length * (width + 3) + 1]).replace('\0', '-'));
            System.out.println();
        }

        for (int i = 0; i < rows.length; i++) {
            System.out.print(" | ");
            for (int j = 0; j < rows[i].length; j++) {
                printString(rows[i][j] + " ", width, align);
                System.out.print(" | ");
            }
            System.out.println();
        }

        System.out.println();

    }

    // returns 0 if no error
    // returns 1 if query error, so maybe wrong name or number of columns
    // width number of chars in one col, default offset doesnt count
    public int renderTable(String name, int rows, int width) {

        if (rows < 1) rows = 1;
        if (width < 3) width = 3;

        try {
            String query = "SELECT top " + rows + " * FROM " + name;
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(query);
            if (renderResultSet(rs, rows, width) != 0) return 2;

            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }

    }

    public int renderResultSet(ResultSet rs, int rows, int width) {

        try {
            ResultSetMetaData md = rs.getMetaData();

            int colCount = md.getColumnCount();

            Align align = Align.LEFT;

            // just to be sure that we wont draw shifted table
            System.out.println();

            // drawing columns names
            System.out.print(" | ");
            for (int i = 0; i < colCount; i++) {
                printString(md.getColumnName(i + 1), width, align);
                System.out.print(" | ");
            }
            System.out.println();

            // drawing delimiter
            System.out.print(" ");
            System.out.print(new String(new char[colCount * (width + 3) + 1]).replace('\0', '-'));
            System.out.println();

            // drawing all requested rows
            int itr = 0;
            while (rs.next() && itr < rows) {
                System.out.print(" | ");
                for (int i = 0; i < colCount; i++) {
                    printString(rs.getString(i + 1), width, align);
                    System.out.print(" | ");
                }
                System.out.println();
                itr++;
            }

            System.out.println();

            return 0;
        } catch (SQLException e) {
            if (Config.VERBOSE) e.printStackTrace();
            return 1;
        }

    }

    private void printString(String str, int width, Align align) {

        int len = str.length();
        if (len > width) {
            String tmp = (width - 3 < 0) ? "" : str.substring(0, width - 3);
            System.out.print(tmp + "...");
            return;
        }

        int offset = width - len;

        if (align == Align.CENTER) {
            offset = (offset) / 2;

            if (offset > 0)
                System.out.format("%" + offset + "s", "");
            System.out.print(str);
            if (offset > 0)
                System.out.format("%" + offset + "s", "");

            if ((width - len) % 2 == 1) System.out.format(" ");
            return;
        }

        if (align == Align.LEFT) {
            System.out.print(str);
            if (offset > 0)
                System.out.format("%" + offset + "s", "");
            return;
        }

        if (offset > 0)
            System.out.format("%" + offset + "s", "");
        System.out.print(str);

    }

    public enum Align {
        LEFT,
        RIGHT,
        CENTER
    }


    private char typeToChar(String type) {

        if (type.contains("char")) return '\'';
        else if (type.contains("text")) return '\'';
        else return '\0';

    }

    private void applyTypes(String[] arr, String[] types) {

        if (arr == null || types == null || arr.length > types.length) return;

        for (int i = 0; i < arr.length; i++) {
            char wrapper = typeToChar(types[i]);
            arr[i] = wrapper + arr[i] + wrapper;
        }

    }

}
