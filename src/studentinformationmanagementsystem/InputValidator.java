/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 * @author Vikra
 */
public final class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\-\\s]{6,15}$");

    private InputValidator() {
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
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

    public static int readMenuChoice(Scanner scanner, String prompt, int min, int max) {
        return readInt(scanner, prompt, min, max);
    }
}
