/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * All the console input reading lives here so it's not scattered across
 * ConsoleUI. Every method loops until the user actually types something
 * valid, so by the time a value gets back to the caller it's already
 * guaranteed to be well-formed - the rest of the program doesn't have to
 * double-check it.
 * 
 * Regex validation logic drafted with assistance from Claude AI; modified to
 * use InvalidInputException — see AI Interaction Log.
 *
 * @author Vikram Bugata
 */
public final class InputValidator {

    // fairly loose checks - not trying to be a full RFC-compliant email/phone
    // validator, just enough to catch obvious typos
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\-\\s]{6,15}$");

    private InputValidator() {
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            // commas aren't allowed since our files are comma-separated -
            // letting one through would silently corrupt the saved data
            if (!input.isEmpty() && input.indexOf(',') < 0) {
                return input;
            }
            System.out.println("  -> Please enter a non-empty value (commas are not allowed).");
        }
    }

    public static String readEmail(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (EMAIL_PATTERN.matcher(input).matches()) {
                return input;
            }
            System.out.println("  -> That does not look like a valid email address, e.g. name@example.com");
        }
    }

    public static String readPhone(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (PHONE_PATTERN.matcher(input).matches()) {
                return input;
            }
            System.out.println("  -> Please enter a valid phone number (digits, spaces, +, -, () only).");
        }
    }

    // used for anything numeric with a sensible range - year level, credit
    // points, capacity, menu choices, etc.
    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("  -> Please enter a whole number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  -> That is not a whole number. Please try again.");
            }
        }
    }

    // same idea as readInt but for GPA / grades, which need decimals
    public static double readDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value < min || value > max) {
                    System.out.println("  -> Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  -> That is not a valid number. Please try again.");
            }
        }
    }

    // for things like status (ACTIVE/SUSPENDED/...) where only a fixed list
    // of values makes sense - case-insensitive so "active" still works
    public static String readOneOf(Scanner scanner, String prompt, String... allowedValues) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            for (String allowed : allowedValues) {
                if (allowed.equalsIgnoreCase(input)) {
                    return allowed.toUpperCase();
                }
            }
            System.out.println("  -> Please enter one of: " + String.join(", ", allowedValues));
        }
    }

    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("Yes")) {
                return true;
            }
            if (input.equalsIgnoreCase("N") || input.equalsIgnoreCase("No")) {
                return false;
            }
            System.out.println("  -> Please answer Y or N.");
        }
    }

    // just reuses readInt - a menu choice is really just an int in a range (0 to however many options there are)
    public static int readMenuChoice(Scanner scanner, String prompt, int min, int max) {
        return readInt(scanner, prompt, min, max);
    }
}
