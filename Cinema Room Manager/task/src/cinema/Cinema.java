package cinema;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class AlreadyBookedException extends Exception {
    AlreadyBookedException() {
        super("That ticket has already been purchased!");
    }
}

public class Cinema {
    public static char[][] seats;
    public static final char FREE = 'S';
    public static final char BOOKED = 'B';
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initCinema();
        boolean run = true;
        while (run) {
            System.out.println("""
                                        
                    1. Show the seats
                    2. Buy a ticket
                    3. Statistics
                    0. Exit
                    """);
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> showTheSeats();
                case 2 -> buyTicket();
                case 3 -> showStatistics();
                case 0 -> run = false;
                default -> System.out.println("wrong choice");
            }
        }
    }

    public static void showStatistics() {
        String numberOfPurchasedTickets = "Number of purchased tickets: %d";
        String percentage = "Percentage: %.2f%%";
        String income = "Current income: $%d";
        String totalIncome = "Total income: $%d";
        System.out.printf((numberOfPurchasedTickets) + "%n", purchasedTickets());
        System.out.printf((percentage) + "%n", purchasedPercentage());
        System.out.printf((income) + "%n", getIncome());
        System.out.printf((totalIncome) + "%n", getTotalIncome());
    }

    public static int purchasedTickets() {
        return (int) getSeatsStream().filter(seat -> seat == BOOKED).count();
    }

    public static double purchasedPercentage() {
        return ((double) purchasedTickets() / (getRowsNumber() * getSeatsInRow())) * 100.0;
    }

    public static int getIncome() {
        String bookedRegex = String.valueOf(BOOKED);
        Pattern bookedPattern = Pattern.compile(bookedRegex);
        int income = 0;
        for (int rowNumber = 1; rowNumber <= getRowsNumber(); rowNumber++) {
            String rowString = String.valueOf(seats[rowNumber - 1]);
            Matcher matcher = bookedPattern.matcher(rowString);
            if (rowString.contains(bookedRegex)) {
                int price = getPrice(rowNumber);
                while (matcher.find()) {
                    income += price;
                }
            }
        }
        return income;
    }

    public static int getTotalIncome() {
        int total = 0;
        for (int rowNumber = 1; rowNumber <= getRowsNumber(); rowNumber++) {
            total += getPrice(rowNumber) * getSeatsInRow();
        }
        return total;
    }


    public static Stream<Character> getSeatsStream() {
        return Arrays.stream(seats).flatMap(row -> new String(row).chars().mapToObj(i -> (char) i));
    }

    public static int getPrice(int row) {
        int rowsNumber = getRowsNumber();
        int seatsInRow = getSeatsInRow();
        int totalSeats = rowsNumber * seatsInRow;
        if (totalSeats <= 60) {
            return 10;
        } else {
            int firstHalf = rowsNumber / 2;
            return (row <= firstHalf) ? 10 : 8;
        }
    }

    public static int getRowsNumber() {
        return seats.length;
    }

    public static int getSeatsInRow() {
        return seats[0].length;
    }

    public static void showTheSeats() {
        String space = " ";
        String cinema = "Cinema:";
        System.out.println(cinema);
        int rowsNumber = getRowsNumber();
        int seatsInRow = getSeatsInRow();
        for (int row = 0; row <= rowsNumber; row++) {
            for (int seatNumber = 0; seatNumber <= seatsInRow; seatNumber++) {
                if (row == 0 && seatNumber == 0) {
                    System.out.print(space);
                } else if (row == 0) {
                    //seats
                    System.out.print(seatNumber);
                } else if (seatNumber == 0) {
                    //rows
                    System.out.print(row);
                } else {
                    System.out.print(seats[row - 1][seatNumber - 1]);
                }
                String next = seatNumber == seatsInRow ? "\n" : space;
                System.out.print(next);
            }
        }
    }

    public static void buyTicket() {
        String errorWrongCoordinates = "Wrong input!";
        String suggestion = "please enter different seat coordinates";
        while (true) {
            int chosenRow;
            int chosenSeatNumber;
            System.out.println("Enter a row number:");
            chosenRow = scanner.nextInt();
            System.out.println("Enter a seat number in that row:");
            chosenSeatNumber = scanner.nextInt();
            try {
                if (seats[chosenRow - 1][chosenSeatNumber - 1] == BOOKED) {
                    throw new AlreadyBookedException();
                }
                seats[chosenRow - 1][chosenSeatNumber - 1] = BOOKED;
                System.out.printf("Ticket price: $%d%n", getPrice(chosenRow));
                break;
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                System.out.println(errorWrongCoordinates);
            } catch (AlreadyBookedException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(suggestion);
        }
    }

    public static void initCinema() {
        int rowsNumber;
        int seatsNumber;
        System.out.println("Enter the number of rows:");
        rowsNumber = scanner.nextInt();
        System.out.println("Enter the number of seats in each row:");
        seatsNumber = scanner.nextInt();
        assert (rowsNumber <= 9 && seatsNumber <= 9) : "too many seats";
        seats = new char[rowsNumber][seatsNumber];
        Arrays.stream(seats).forEach(row -> Arrays.fill(row, FREE));
    }
}