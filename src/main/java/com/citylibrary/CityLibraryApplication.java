package com.citylibrary;

import com.citylibrary.enums.Status;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loanable;
import com.citylibrary.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class CityLibraryApplication implements CommandLineRunner {

	@Autowired private Library library;
	@Autowired DataService csvDataService;

	private static final Logger logger = LoggerFactory.getLogger(CityLibraryApplication.class);

	public static void main(String[] args) {
		SpringApplication app =  new SpringApplication(CityLibraryApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {

		logger.info("Print current inventory including loaned items");
		//Print current inventory including loaned items
		this.printCurrentInventory();

		// Customer-1 borrows 2 items
		// Customer- 2 borrows 1 item
		this.borrowItems();

		//Prints items borrowed by Customer-1 and Customer-2
		this.printItemsBorrowedByUsers();

		// Prints currently loanable items excluding items borrowed by Customer-1 and Customer-2
		this.printCurrentLoanableInventory();

		//Customer-1 returns an item
		this.returnItems();

		this.printOverDueItems();

	}

	private void returnItems() {
		System.out.println();
		System.out.println("**************************** CUSTOMER-1 RETURNING AN ITEM *************************************");
		System.out.println();

		LibraryItem book = library.getCurrentInventory().get(5);
		System.out.println(book);
		library.returnItem(book);
	}

	private void printCurrentLoanableInventory() {
		System.out.println();
		System.out.println("**************************** CURRENT LOANABLE INVENTORY AFTER FEW LOAN *************************************");
		System.out.println();

		library.getCurrentLoanableInventory().forEach(System.out::println);
	}

	private void printItemsBorrowedByUsers() {
		System.out.println();
		System.out.println("**************************** ITEMS BORROWED BY A CUSTOMER-1 *************************************");
		System.out.println();

		Person customer1 = csvDataService.getCustomerById(1);
		library.getItemBorrowedByUser(customer1).forEach(loan-> System.out.println(loan.getItem()));


		System.out.println();
		System.out.println("**************************** ITEMS BORROWED BY A CUSTOMER-2 *************************************");
		System.out.println();

		Person customer2 = csvDataService.getCustomerById(1);
		library.getItemBorrowedByUser(customer2).forEach(loan-> System.out.println(loan.getItem()));
	}

	private void borrowItems() {

		System.out.println();
		System.out.println("**************************** CUSTOMER-1 BORROWS ITEMS *************************************");
		System.out.println();

		Person customer1 = csvDataService.getCustomerById(1);
		LibraryItem item = csvDataService.getItemsByLibraryId(1);
		LibraryItem item3 = csvDataService.getItemsByLibraryId(5);

		System.out.println(item);
		System.out.println(item3);

		library.borrowItem(customer1,item);
		library.borrowItem(customer1,item3);


		System.out.println();
		System.out.println("**************************** CUSTOMER-2 BORROWS A ITEM *************************************");
		System.out.println();

		Person customer2 = csvDataService.getCustomerById(1);
		LibraryItem item2 = csvDataService.getItemsByLibraryId(3);

		System.out.println(item2);

		library.borrowItem(customer2,item2);

	}

	private void printCurrentInventory() {
		System.out.println("**************************** CURRENT INVENTORY *************************************");
		System.out.println();

		Map<Integer, List<LibraryItem>> mp = library.getCurrentInventory().stream().collect(Collectors.groupingBy((LibraryItem::getItemId)));

		mp.forEach((key, value) -> {
			System.out.println("Item Id:" + key);
			System.out.println("Item Type:" + value.get(0).getType());
			System.out.println("Item Title:" + value.get(0).getTitle());
			System.out.println("Copies Available:" + value.stream().filter(t -> t.getItemStatus().equals(Status.AVAILABLE)).count());
			System.out.println("Currently Loaned:" + value.stream().filter(t -> t.getItemStatus().equals(Status.LOANED)).count());
			System.out.println("List of Library Items:");
			value.forEach(System.out::println);
			System.out.println("************************************************************************************");
		});

	}



	private void printOverDueItems() {
		System.out.println();
		System.out.println("**************************** PRINT OVERDUE ITEMS *************************************");
		System.out.println();

		library.getOverDueItems().forEach(System.out::println);
	}
}
