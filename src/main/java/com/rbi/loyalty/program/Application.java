package com.rbi.loyalty.program;

import com.rbi.loyalty.program.models.Customer;
import com.rbi.loyalty.program.models.Transaction;
import com.rbi.loyalty.program.services.LoyaltyProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;


@SpringBootApplication
@ComponentScan(basePackages={"com.rbi"})
public class Application implements CommandLineRunner, DateValidator {

	@Autowired
	LoyaltyProgramService loyaltyProgramService;

	//Spring Boot will automagically wire this object using application.properties:
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		Transaction transaction = new Transaction();
		Integer menuOption = null;

		do {
			System.out.println("What would you like to do?\n\n\t1. Add new customer\n\t2. Add new transaction\n\t3. See customer's data\n\t4. Update loyalty points.\n\t5. Exit");
			menuOption = scanner.nextInt();

			switch(menuOption){
				case 1: {
					//Create the database table:
					loyaltyProgramService.addNewCustomer(loyaltyProgramService.customerMap.isEmpty() ? (long) 100000000 : (loyaltyProgramService.customerMap.lastKey() + 1));
					Customer customer = loyaltyProgramService.customerMap.get(loyaltyProgramService.customerMap.lastKey());
				}
					break;
				case 2: {
					Long customerId = null;

					do {
						System.out.println("Please enter customer ID: ");
						customerId = scanner.nextLong();
						if(!loyaltyProgramService.customerExists(customerId)){
							System.out.println("Customer does not exist, please enter correct customer ID:");
						}
					} while (!loyaltyProgramService.customerExists(customerId));
					System.out.print("Please enter amount spent: ");
					while(!scanner.hasNextDouble()){
						System.out.print("You must enter a valid number! Try again: ");
						scanner.next();
					}
					Double amountSpent = scanner.nextDouble();
					String transactionDate = null;

					do {
						System.out.println("Please add transaction date -> format: yyyy-mm-dd");
						transactionDate = scanner.next();
					} while(!isValid(transactionDate));

					transaction.setTransactionAmount(amountSpent);
					transaction.setTransactionDate(LocalDate.parse(transactionDate));
					transaction.setCustomerId(customerId);
					transaction.setTransactionId(loyaltyProgramService.transactionMap.isEmpty() ? (long) 000000001 : (loyaltyProgramService.transactionMap.lastKey() + 1));
					loyaltyProgramService.newTransaction(transaction, customerId);
					loyaltyProgramService.checkPointsUpgradeStatus(customerId, false);
				}
				break;
				case 3: {
					Long customerId = null;

					do {
						System.out.println("Please enter customer ID: ");
						customerId = scanner.nextLong();
						if(!loyaltyProgramService.customerExists(customerId)){
							System.out.println("Customer does not exist, please enter correct customer ID:");
						}
					} while (!loyaltyProgramService.customerExists(customerId));

					System.out.println(loyaltyProgramService.getCustomer(customerId));

				}
				break;
				case 4: {
					Long customerId = null;

					do {
						System.out.println("Please enter customer ID: ");
						customerId = scanner.nextLong();
						if(!loyaltyProgramService.customerExists(customerId)){
							System.out.println("Customer does not exist, please enter correct customer ID:");
						}
					} while (!loyaltyProgramService.customerExists(customerId));

					if(loyaltyProgramService.checkPointsUpgradeStatus(customerId, true)){
						loyaltyProgramService.getCustomer(customerId).setAvailablePoints(loyaltyProgramService.getCustomer(customerId).getPendingPoints());
						loyaltyProgramService.getCustomer(customerId).setPendingPoints(0);
					} else {
						System.out.println("Customer not eligible for pendingPoints upgrade");
					}

				}
				break;
				case 5: break;
				default: System.out.println("Invalid selection, please select options 1, 2 or 3.");
			}
		} while(menuOption != 5);

	}

	@Override
	public boolean isValid(String dateStr) {
		try {
			LocalDate.parse(dateStr);
		} catch (DateTimeParseException e)  {
			return false;
		}
		return true;
	}
}
