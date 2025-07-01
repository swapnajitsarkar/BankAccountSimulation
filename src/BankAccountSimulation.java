import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Transaction class to store transaction details
class Transaction {
    private String type;
    private double amount;
    private double balanceAfter;
    private LocalDateTime timestamp;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s: $%.2f | Balance: $%.2f",
                timestamp.format(formatter), type, amount, balanceAfter);
    }
}

// Base Account class
class Account {
    protected String accountNumber;
    protected String accountHolder;
    protected double balance;
    protected List<Transaction> transactionHistory;

    public Account(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();

        if (initialBalance > 0) {
            transactionHistory.add(new Transaction("INITIAL_DEPOSIT", initialBalance, balance));
        }
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive!");
            return;
        }

        balance += amount;
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance));
        System.out.printf("Deposited $%.2f. New balance: $%.2f%n", amount, balance);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive!");
            return;
        }

        if (amount > balance) {
            System.out.println("Insufficient funds! Current balance: $" + String.format("%.2f", balance));
            return;
        }

        balance -= amount;
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance));
        System.out.printf("Withdrawn $%.2f. New balance: $%.2f%n", amount, balance);
    }

    public double getBalance() {
        return balance;
    }

    public void displayAccountInfo() {
        System.out.println("\n=== Account Information ===");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Holder: " + accountHolder);
        System.out.printf("Current Balance: $%.2f%n", balance);
    }

    public void displayTransactionHistory() {
        System.out.println("\n=== Transaction History ===");
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }
}

// SavingsAccount class inheriting from Account
class SavingsAccount extends Account {
    private double interestRate;
    private double minimumBalance;

    public SavingsAccount(String accountNumber, String accountHolder, double initialBalance, double interestRate) {
        super(accountNumber, accountHolder, initialBalance);
        this.interestRate = interestRate;
        this.minimumBalance = 100.0; // Minimum balance requirement
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive!");
            return;
        }

        if (balance - amount < minimumBalance) {
            System.out.printf("Cannot withdraw! Minimum balance of $%.2f must be maintained.%n", minimumBalance);
            return;
        }

        super.withdraw(amount);
    }

    public void addInterest() {
        double interest = balance * (interestRate / 100);
        balance += interest;
        transactionHistory.add(new Transaction("INTEREST", interest, balance));
        System.out.printf("Interest added: $%.2f. New balance: $%.2f%n", interest, balance);
    }

    @Override
    public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.printf("Interest Rate: %.2f%%%n", interestRate);
        System.out.printf("Minimum Balance: $%.2f%n", minimumBalance);
    }
}

// CheckingAccount class inheriting from Account
class CheckingAccount extends Account {
    private double overdraftLimit;

    public CheckingAccount(String accountNumber, String accountHolder, double initialBalance, double overdraftLimit) {
        super(accountNumber, accountHolder, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive!");
            return;
        }

        if (amount > balance + overdraftLimit) {
            System.out.printf("Withdrawal denied! Maximum available: $%.2f (including overdraft)%n",
                    balance + overdraftLimit);
            return;
        }

        balance -= amount;
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance));
        System.out.printf("Withdrawn $%.2f. New balance: $%.2f%n", amount, balance);

        if (balance < 0) {
            System.out.printf("Warning: Account overdrawn by $%.2f%n", Math.abs(balance));
        }
    }

    @Override
    public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.printf("Overdraft Limit: $%.2f%n", overdraftLimit);
    }
}

// Main BankAccountSimulation class
public class BankAccountSimulation {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Account> accounts = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Welcome to Bank Account Simulation!");

        while (true) {
            displayMainMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    performTransaction();
                    break;
                case 3:
                    viewAccountInfo();
                    break;
                case 4:
                    viewTransactionHistory();
                    break;
                case 5:
                    addInterestToSavings();
                    break;
                case 6:
                    System.out.println("Thank you for using Bank Account Simulation!");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=== Bank Account Simulation ===");
        System.out.println("1. Create Account");
        System.out.println("2. Perform Transaction");
        System.out.println("3. View Account Information");
        System.out.println("4. View Transaction History");
        System.out.println("5. Add Interest to Savings Accounts");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getChoice() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine(); // Clear invalid input
            return -1;
        }
    }

    private static void createAccount() {
        scanner.nextLine(); // Clear buffer

        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine();

        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        // Check if account number already exists
        for (Account account : accounts) {
            if (account.accountNumber.equals(accountNumber)) {
                System.out.println("Account number already exists!");
                return;
            }
        }

        System.out.print("Enter initial balance: $");
        double initialBalance = scanner.nextDouble();

        if (initialBalance < 0) {
            System.out.println("Initial balance cannot be negative!");
            return;
        }

        System.out.println("Select account type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Checking Account");
        System.out.print("Enter choice: ");
        int type = scanner.nextInt();

        Account account = null;

        switch (type) {
            case 1:
                System.out.print("Enter interest rate (%): ");
                double interestRate = scanner.nextDouble();
                account = new SavingsAccount(accountNumber, name, initialBalance, interestRate);
                break;
            case 2:
                System.out.print("Enter overdraft limit: $");
                double overdraftLimit = scanner.nextDouble();
                account = new CheckingAccount(accountNumber, name, initialBalance, overdraftLimit);
                break;
            default:
                System.out.println("Invalid account type!");
                return;
        }

        accounts.add(account);
        System.out.println("Account created successfully!");
    }

    private static void performTransaction() {
        Account account = selectAccount();
        if (account == null) return;

        System.out.println("\nSelect transaction type:");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.print("Enter choice: ");
        int choice = getChoice();

        System.out.print("Enter amount: $");
        double amount = scanner.nextDouble();

        switch (choice) {
            case 1:
                account.deposit(amount);
                break;
            case 2:
                account.withdraw(amount);
                break;
            default:
                System.out.println("Invalid transaction type!");
        }
    }

    private static void viewAccountInfo() {
        Account account = selectAccount();
        if (account != null) {
            account.displayAccountInfo();
        }
    }

    private static void viewTransactionHistory() {
        Account account = selectAccount();
        if (account != null) {
            account.displayTransactionHistory();
        }
    }

    private static void addInterestToSavings() {
        boolean foundSavings = false;

        for (Account account : accounts) {
            if (account instanceof SavingsAccount) {
                foundSavings = true;
                SavingsAccount savingsAccount = (SavingsAccount) account;
                System.out.println("\nAdding interest to account: " + account.accountNumber);
                savingsAccount.addInterest();
            }
        }

        if (!foundSavings) {
            System.out.println("No savings accounts found!");
        }
    }

    private static Account selectAccount() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts available! Please create an account first.");
            return null;
        }

        System.out.println("\nAvailable accounts:");
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, account.accountNumber, account.accountHolder);
        }

        System.out.print("Select account number: ");
        int choice = getChoice();

        if (choice < 1 || choice > accounts.size()) {
            System.out.println("Invalid account selection!");
            return null;
        }

        return accounts.get(choice - 1);
    }
}