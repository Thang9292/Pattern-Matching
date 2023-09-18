import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Your implementations of various string searching algorithms.
 *
 * @author Thang Huynh
 * @version 1.0
 */
public class PatternMatching {

    /**
     * Knuth-Morris-Pratt (KMP) algorithm relies on the failure table (also
     * called failure function). Works better with small alphabets.
     *
     * Uses buildFailureTable() method
     *
     * @param pattern    the pattern you are searching for in a body of text
     * @param text       the body of text where you search for pattern
     * @param comparator to check if characters are equal
     * @return list containing the starting index for each match found
     * @throws java.lang.IllegalArgumentException if the pattern is null or has
     *                                            length 0
     * @throws java.lang.IllegalArgumentException if text or comparator is null
     */
    public static List<Integer> kmp(CharSequence pattern, CharSequence text,
                                    CharacterComparator comparator) {
        //The Exceptions
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("The patterned entered was null or has length 0");
        }
        if (text == null || comparator == null) {
            throw new IllegalArgumentException("The text entered was null or the comparator was null");
        }

        int j = 0; //Pattern Index
        int k = 0; //Text Index
        int n = text.length();
        int m = pattern.length();
        List<Integer> theList = new ArrayList<>();

        if (m > n) {
            return theList;
        }

        //Builds Failure Table
        int[] f = buildFailureTable(pattern, comparator);

        while (k < n) {
            if (m - j > n - k) {
                return theList;
            }
            if (comparator.compare(pattern.charAt(j), text.charAt(k)) == 0) {
                if (j == m - 1) {
                    theList.add(k - j); // Pattern Found
                    j = f[j];
                    k++;
                } else {
                    j++;
                    k++;
                }
            } else if (j == 0) {
                k++;
            } else {
                j = f[j - 1];
            }
        }
        return theList;
    }

    /**
     * Builds failure table that will be used to run the Knuth-Morris-Pratt
     * (KMP) algorithm.
     *
     * The table built should be the length of the input text.
     *
     * Note that a given index i will contain the length of the largest prefix
     * of the pattern indices [0..i] that is also a suffix of the pattern
     * indices [1..i]. This means that index 0 of the returned table will always
     * be equal to 0
     *
     * Ex. pattern = ababac
     *
     * table[0] = 0
     * table[1] = 0
     * table[2] = 1
     * table[3] = 2
     * table[4] = 3
     * table[5] = 0
     *
     * If the pattern is empty, return an empty array.
     *
     * @param pattern    a pattern you're building a failure table for
     * @param comparator  to check if characters are equal
     * @return integer array holding your failure table
     * @throws java.lang.IllegalArgumentException if the pattern or comparator is null
     */
    public static int[] buildFailureTable(CharSequence pattern,
                                          CharacterComparator comparator) {
        //The Exceptions
        if (pattern == null || comparator == null) {
            throw new IllegalArgumentException("The pattern or comparator entered was null");
        }

        int m = pattern.length();
        int[] f = new int[m];
        f[0] = 0;
        int i = 0;
        int j = 1;

        while (j < m) {
            if (comparator.compare(pattern.charAt(i), pattern.charAt(j)) == 0) {
                f[j] = i + 1;
                i++;
                j++;
            } else if (i == 0) {
                f[j] = 0;
                j++;
            } else {
                i = f[i - 1];
            }
        }
        return f;
    }

    /**
     * Boyer Moore algorithm that relies on last occurrence table. Works better
     * with large alphabets.
     *
     * Uses buildLastTable() method 
     *
     *
     * @param pattern    the pattern you are searching for in a body of text
     * @param text       the body of text where you search for the pattern
     * @param comparator  to check if characters are equal
     * @return list containing the starting index for each match found
     * @throws java.lang.IllegalArgumentException if the pattern is null or has length 0
     * @throws java.lang.IllegalArgumentException if text or comparator is null
     */
    public static List<Integer> boyerMoore(CharSequence pattern,
                                           CharSequence text,
                                           CharacterComparator comparator) {
        //The Exceptions
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("The patterned entered was null or has length 0");
        }
        if (text == null || comparator == null) {
            throw new IllegalArgumentException("The text entered was null or the comparator was null");
        }

        int n = text.length();
        int m = pattern.length();
        List<Integer> theList = new ArrayList<>();

        if (m > n) {
            return theList;
        }

        //Builds Last Occurrence Table
        Map<Character, Integer> last = buildLastTable(pattern);

        int i = 0;
        while (i < text.length() - pattern.length() + 1) {
            int j = pattern.length() - 1;
            while (j >= 0 && comparator.compare(text.charAt(i + j), pattern.charAt(j)) == 0) {
                j = j - 1;
            }
            if (j == -1) {
                theList.add(i); //Pattern Found
                i++;
            } else { //Pattern Not Found
                int shift = last.getOrDefault(text.charAt(i + j), -1);
                if (shift < j) {
                    i = (i + (j - shift));
                } else {
                    i = i + 1;
                }
            }
        }
        return theList;
    }

    /**
     * Builds last occurrence table that will be used to run the Boyer Moore
     * algorithm.
     *
     * Note that each char x will have an entry at table.get(x).
     * Each entry should be the last index of x where x is a particular
     * character in your pattern.
     * If x is not in the pattern, then the table will not contain the key x,
     *
     * Ex. pattern = octocat
     *
     * table.get(o) = 3
     * table.get(c) = 4
     * table.get(t) = 6
     * table.get(a) = 5
     * table.get(everything else) = null, which you will interpret in
     * Boyer-Moore as -1
     *
     * If the pattern is empty, return an empty map.
     *
     * @param pattern a pattern you are building last table for
     * @return a Map with keys of all of the characters in the pattern mapping
     * to their last occurrence in the pattern
     * @throws java.lang.IllegalArgumentException if the pattern is null
     */
    public static Map<Character, Integer> buildLastTable(CharSequence pattern) {
        //The Exception
        if (pattern == null) {
            throw new IllegalArgumentException("The pattern entered was null");
        }
        int m = pattern.length();
        Map<Character, Integer> last = new HashMap<>();

        for (int i = 0; i < m; i++) {
            last.put(pattern.charAt(i), i);
        }
        return last;
    }

    /**
     * Prime base used for Rabin-Karp hashing.
     */
    private static final int BASE = 113;

    /**
     * Runs the Rabin-Karp algorithm. This algorithms generates hashes for the
     * pattern and compares this hash to substrings of the text before doing
     * character by character comparisons.
     *
     * When the hashes are equal and you do character comparisons, compare
     * starting from the beginning of the pattern to the end, not from the end
     * to the beginning.
     *
     * The formula for it is:
     *
     * sum of: c * BASE ^ (pattern.length - 1 - i)
     *   c is the integer value of the current character, and
     *   i is the index of the character
     *
     * Assume that all powers and calculations CAN be done without
     * overflow. 
     *
     * Ex. Hashing "bunn" as a substring of "bunny" with base 113
     * = (b * 113 ^ 3) + (u * 113 ^ 2) + (n * 113 ^ 1) + (n * 113 ^ 0)
     * = (98 * 113 ^ 3) + (117 * 113 ^ 2) + (110 * 113 ^ 1) + (110 * 113 ^ 0)
     * = 142910419
     *
     * Updating the hash from one substring to the next substring must be O(1). 
     * To update the hash, subtract the oldChar times BASE raised to the length - 1, multiply by
     * BASE, and add the newChar as shown by this formula:
     * (oldHash - oldChar * BASE ^ (pattern.length - 1)) * BASE + newChar
     *
     * Ex. Shifting from "bunn" to "unny" in "bunny" with base 113
     * hash("unny") = (hash("bunn") - b * 113 ^ 3) * 113 + y
     *              = (142910419 - 98 * 113 ^ 3) * 113 + 121
     *              = 170236090
     *
     * Keep in mind that calculating exponents is not O(1) in general, so we
     * need to keep track of what BASE^(m - 1) is for updating the hash.
     *
     *
     * @param pattern    a string you're searching for in a body of text
     * @param text       the body of text where you search for pattern
     * @param comparator  to check if characters are equal
     * @return list containing the starting index for each match found
     * @throws java.lang.IllegalArgumentException if the pattern is null or has length 0
     * @throws java.lang.IllegalArgumentException if text or comparator is null
     */
    public static List<Integer> rabinKarp(CharSequence pattern,
                                          CharSequence text,
                                          CharacterComparator comparator) {
        //The Exceptions
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("The patterned entered was null or has length 0");
        }
        if (text == null || comparator == null) {
            throw new IllegalArgumentException("The text entered was null or the comparator was null");
        }

        List<Integer> theList = new ArrayList<>();
        int m = pattern.length(); //Length of the Pattern
        int n = text.length(); //Length of the Text
        if (m > n) {
            return theList;
        }

        //Create Initial Hashes
        int sumOfPattern = 0;
        int sumOfText = 0;
        for (int i = m - 1; i >= 0; i--) {
            // (i) is the index of starting at the end of the pattern
            int x = pattern.charAt(i) * power(BASE, m - 1 - i);
            int y = text.charAt(i) * power(BASE, m - 1 - i);
            sumOfPattern = sumOfPattern + x;
            sumOfText = sumOfText + y;
        }


        int i = 0; //Index of Text currently
        while (i < n - m + 1) {

            if (sumOfPattern == sumOfText) { //matching hashcode
                //Compares characters in the Pattern vs Text
                int y = 0;
                while (y < pattern.length() && comparator.compare(pattern.charAt(y), text.charAt(i + y)) == 0)  {
                    y++;
                }
                if (y == m) {
                    theList.add(i);
                }
            }
            i++;
            if (i < n - m + 1) {
                sumOfText = updateHash(sumOfText, BASE, m, text.charAt(i - 1), text.charAt(i - 1 + m));
            }
        }
        return theList;

    }

    /**
     * A helper method that updates the Hash of the text at O(1) time
     *
     * @param oldHash       the hash to be updated
     * @param base          the base of our hashing operations
     * @param patLength     the length of the pattern
     * @param oldCharacter  the text character that needs to be subtracted
     * @param newCharacter  the text character that needs to be added
     * @return the updated hash
     */
    private static int updateHash(int oldHash, int base, int patLength, char oldCharacter, char newCharacter) {
        return (oldHash - (oldCharacter * power(base, patLength - 1))) * base + newCharacter;
    }

    /**
     * A helper method that calculates powers
     *
     * @param base      the number being raised to a power
     * @param exponent  the power itself
     * @return the computed power
     * @throws java.lang.IllegalArgumentException if base or exponent is negative
     */
    private static int power(int base, int exponent) {
        if (base < 0 || exponent < 0) {
            throw new IllegalArgumentException("The base or exponent entered has to be non-negative");
        }
        int result = base;
        if (exponent == 0) {
            return 1;
        } else if (base == 1) {
            return 1;
        } else if (base == 0) {
            return 0;
        } else {
            for (int i = 1; i < exponent; i++) {
                result *= base;
            }
        }
        return result;
    }
}
