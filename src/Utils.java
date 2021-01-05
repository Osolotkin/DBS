public class Utils {

    public static final char NEW_LINE = '\n';
    public static final char TAB = '\t';
    public static final String OS = System.getProperty("os.name");

    public static void writeError(String str) {

        System.out.print(Colors.YELLOW);
        System.out.println(str + NEW_LINE);
        System.out.print(Colors.RESET);

    }

    public static void writeInfo(String str) {

        System.out.print(Colors.GREEN);
        System.out.println(str + NEW_LINE);
        System.out.print(Colors.RESET);

    }

    public static int str2int(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double str2double(String str) {
        try {
            if (str == null) return 0;
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String strarr2str(String str[]) {
        return strarr2str(str, ' ');
    }

    public static String strarr2str(String str[], char delimiter) {
        return strarr2str(str, delimiter, Character.MIN_VALUE);
    }

    public static String strarr2str(String str[], char delimiter, char wrapper) {
        if (str.length < 1) return null;

        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < str.length - 1; i++) {
            sb.append(wrapper + str[i] + wrapper + delimiter);
        }
        sb.append(str[i]);
        return sb.toString();
    }

    // let we have two arrays of same length A and B, function connect A[n] with B[n], for n > 0 && n < A.length,
    // using delimiter in such way - A[n] + delimiterAB[n] + B[n] = C[n], and then connect each element of C to one String
    // with another delimiterC
    public static String strarrs2str(String A[], String B[], String delimiterAB[], String delimiterC) {
        return strarrs2str(A, B, delimiterAB, delimiterC, new char[A.length], new char[A.length]);
    }

    public static String strarrs2str(String A[], String B[], String delimiterAB[], String delimiterC, char[] wrapperA, char[] wrapperB) {
        if (A.length < 1 || A.length > B.length) return null;
        if (A.length > wrapperA.length || A.length > wrapperB.length) return null;

        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < A.length - 1; i++) {
            sb.append(wrapperA[i] + A[i] + wrapperA[i] + delimiterAB[i] + wrapperB[i] + B[i] + wrapperB[i] + delimiterC);
        }
        sb.append(wrapperA[i] + A[i] + wrapperA[i] + delimiterAB[i] + wrapperB[i] + B[i] + wrapperB[i]);
        return sb.toString();
    }

    // again copy paste, but i guess its better than creating array if not needed
    public static String strarrs2str(String A[], String B[], String delimiterAB, String delimiterC) {
        return strarrs2str(A, B, delimiterAB, delimiterC, new char[A.length], new  char[A.length]);
    }

    public static String strarrs2str(String A[], String B[], String delimiterAB, String delimiterC, char[] wrapperA, char[] wrapperB) {
        if (A.length < 1 || A.length > B.length) return null;
        if (A.length > wrapperA.length || A.length > wrapperB.length) return null;

        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < A.length - 1; i++) {
            sb.append(wrapperA[i] + A[i] + wrapperA[i] + delimiterAB + wrapperB[i] + B[i] + wrapperB[i] + delimiterC);
        }
        sb.append(wrapperA[i] + A[i] + wrapperA[i] + delimiterAB + wrapperB[i] + B[i] + wrapperB[i]);
        return sb.toString();
    }

    public static String[][] pickNotEmptyPairs(String[][] str, char emptyChar) {
        if (str.length < 2) return null;

        for (int i = 1; i < str.length; i++) {
            if (str[0].length > str[i].length) return null;
        }

        String[] pairs = new String[str.length * str[0].length];
        int k = 0;
        for (int i = 0; i < str[0].length; i++) {
            boolean isEmpty = false;
            for (int j = 0; j < str.length; j++) {
                isEmpty = isEmpty || str[j][i].length() == 1 && str[j][i].charAt(0) == emptyChar;
            }
            if (isEmpty) continue;

            for (int j = 0; j < str.length; j++) {
                pairs[k] = str[j][i];
                k++;
            }
        }

        String[][] pairsFinal = new String[str.length][k / str.length];
        for (int i = 0; i < pairsFinal[0].length; i++) {
            for (int j = 0; j < str.length; j++) {
                pairsFinal[j][i] = pairs[str.length * i + j];
            }
        }

        return pairsFinal;
    }

    public static String[] fillTheLength(String[] arr, int length, String valToFill) {
        if (arr.length >= length || length < 1) return arr;

        String[] newArr = new String[length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = arr[i];
        }
        for (int i = arr.length; i < newArr.length; i++) {
            newArr[i] = valToFill;
        }

        return newArr;
    }

    public class Colors {
        // Reset
        public static final String RESET = "\033[0m";   // Text Reset

        public static final String GREEN = "\033[92m";  // GREEN
        public static final String YELLOW = "\033[93m"; // YELLOW
    }

}
