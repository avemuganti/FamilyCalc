package familycalc;

import java.util.Arrays;
import java.util.Scanner;

public class FamilyCalc {

    public static void main(String[] args) {

        Scanner kb = new Scanner(System.in);
        System.out.print("Enter a person's name: ");
        String name = kb.nextLine();
        System.out.print("How is " + name + " related to you?: ");
        String relation = kb.nextLine();
        System.out.println(computeRelation(relation));

    }

    private static String computeRelation(String relation) {

        //Allowed terms: mother/father<0><x>, brother/sister<1>, son/daughter<2><x>.
        //Created terms: cousin<3><x>, aunt/uncle<4><x>, niece/nephew<5><x>
        relation = relation.trim().toLowerCase();
        String[] relationSplit = relation.split(" ");
        // gender true is male, gender false is female.
        boolean gender = relationSplit[relationSplit.length - 1].equals("son") || relationSplit[relationSplit.length - 1].equals("brother") || relationSplit[relationSplit.length - 1].equals("father");
        int[][] values = new int[2][relationSplit.length];
        for (int i = 0; i < relationSplit.length; i++) {
            values[0][i] = relationSplit[i].contains("mother") || relationSplit[i].contains("father") ? 0 : (relationSplit[i].contains("brother") || relationSplit[i].contains("sister") ? 1 : 2);
        }
        if (Arrays.toString(values[0]).contains("0, 2") || Arrays.toString(values[0]).contains("1, 1") || Arrays.toString(values[0]).contains("2, 0") || Arrays.toString(values[0]).contains("2, 1, 0")) {
            return "ERROR";
        }
        // ALL COUSINS
        while (Arrays.toString(values[0]).contains("0, 1, 2")) {
            values = searchAndReplace(values, new int[]{0, 1, 2}, 3, 0, false);
        }
        while (Arrays.toString(values[0]).contains("0, 3, 2")) {
            values = searchAndReplace(values, new int[]{0, 3, 2}, 3, 1, true);
        }
        // ALL GRANDPARENTS
        while (Arrays.toString(values[0]).contains("0, 0")) {
            values = searchAndReplace(values, new int[]{0, 0}, 0, 0, true);
        }
        // ALL GRANDKIDS
        while (Arrays.toString(values[0]).contains("2, 2")) {
            values = searchAndReplace(values, new int[]{2, 2}, 2, 0, true);
        }
        // ALL AUNTS/UNCLES
        while (Arrays.toString(values[0]).contains("0, 1")) {
            values = searchAndReplace(values, new int[]{0, 1}, 4, 0, false);
        }
        // ALL NIECES/NEPHEWS
        while (Arrays.toString(values[0]).contains("1, 2")) {
            values = searchAndReplace(values, new int[]{1, 2}, 5, 1, false);
        }
        String output = "";
        if (values[0].length == 1) {
            switch (values[0][0]) {
                case 0:
                    output = gender ? "father" : "mother";
                    if (values[1][0] >= 1) {
                        output = "grand" + output;
                    }
                    for (int i = 1; i < values[1][0]; i++) {
                        output = "great " + output;
                    }
                    break;
                case 1:
                    output = gender ? "brother" : "sister";
                    break;
                case 2:
                    output = gender ? "son" : "daughter";
                    if (values[1][0] >= 1) {
                        output = "grand" + output;
                    }
                    for (int i = 1; i < values[1][0]; i++) {
                        output = "great " + output;
                    }
                    break;
                case 3:
                    int prefixNum = values[1][0] + 1;
                    String prefix = prefixNum % 10 == 1 && prefixNum % 100 != 11 ? prefixNum + "st" : (prefixNum % 10 == 2 && prefixNum % 100 != 12 ? prefixNum + "nd" : (prefixNum % 10 == 3 && prefixNum % 100 != 13 ? prefixNum + "rd" : prefixNum + "th"));
                    output = prefix + " cousin";
                    break;
                case 4:
                    output = gender ? "uncle" : "aunt";
                    if (values[1][0] >= 1) {
                        output = "grand " + output;
                    }
                    for (int i = 1; i < values[1][0]; i++) {
                        output = "great " + output;
                    }
                    break;
                default:
                    output = gender ? "nephew" : "niece";
                    if (values[1][0] >= 1) {
                        output = "grand " + output;
                    }
                    for (int i = 1; i < values[1][0]; i++) {
                        output = "great " + output;
                    }
                    break;
            }
        } else {
            if (Arrays.toString(values[0]).equals("[0, 3]")) {
                int prefixNum = values[1][1] + 1;
                String prefix = prefixNum % 10 == 1 && prefixNum % 100 != 11 ? prefixNum + "st" : (prefixNum % 10 == 2 && prefixNum % 100 != 12 ? prefixNum + "nd" : (prefixNum % 10 == 3 && prefixNum % 100 != 13 ? prefixNum + "rd" : prefixNum + "th"));
                String suffix = " removed";
                int suffixNum = values[1][0] + 1;
                suffix = suffixNum == 1 ? "once" + suffix : (suffixNum == 2 ? "twice" + suffix : (suffixNum == 3 ? "thrice" + suffix : suffixNum + " times" + suffix));
                output = prefix + " cousin " + suffix;
            } else if (Arrays.toString(values[0]).equals("[3, 2]")) {
                int prefixNum = values[1][0] + 1;
                String prefix = prefixNum % 10 == 1 && prefixNum % 100 != 11 ? prefixNum + "st" : (prefixNum % 10 == 2 && prefixNum % 100 != 12 ? prefixNum + "nd" : (prefixNum % 10 == 3 && prefixNum % 100 != 13 ? prefixNum + "rd" : prefixNum + "th"));
                String suffix = " removed";
                int suffixNum = values[1][1] + 1;
                suffix = suffixNum == 1 ? "once" + suffix : (suffixNum == 2 ? "twice" + suffix : (suffixNum == 3 ? "thrice" + suffix : suffixNum + " times" + suffix));
                output = prefix + " cousin " + suffix;
            } else {
                return "This is a bug. Please fix.";
            }
        }
        return output;

    }

    private static int[][] searchAndReplace(int[][] values, int[] srch, int repl, int indAug, boolean aug) {

        int[][] temp = new int[2][values[0].length - srch.length + 1];
        if (Arrays.toString(values[0]).contains(Arrays.toString(srch).substring(1, Arrays.toString(srch).length() - 1))) {
            for (int i = 0; i < values[0].length - srch.length + 1; i++) {
                if (getIndexOfArray(values[0], srch) == i) {
                    for (int j = 0; j < temp[0].length; j++) {
                        if (j < i) {
                            temp[0][j] = values[0][j];
                            temp[1][j] = values[1][j];
                        } else if (j == i) {
                            temp[0][j] = repl;
                            temp[1][j] = values[1][i + indAug] + (aug ? 1 : 0);
                        } else {
                            temp[0][j] = values[0][j + srch.length - 1];
                            temp[1][j] = values[1][j + srch.length - 1];
                        }
                    }
                }
            }
        } else {
            return values;
        }
        return temp;

    }

    private static int getIndexOfArray(int[] big, int[] small) {

        return Arrays.toString(big).substring(1, Arrays.toString(big).length() - 1).indexOf(Arrays.toString(small).substring(1, Arrays.toString(small).length() - 1)) / 3;

    }

}
