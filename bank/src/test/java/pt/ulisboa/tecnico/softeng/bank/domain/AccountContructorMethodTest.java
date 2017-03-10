package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class AccountContructorMethodTest {
	Bank bank, bank1;
	Client client;

	@Before
	public void setUp() {
		this.bank = new Bank("Money", "BK01");
		this.client = new Client(this.bank, "António");
	}

	@Test
	public void success() {
		Account account = new Account(this.bank, this.client);

		Assert.assertEquals(this.bank, account.getBank());
		Assert.assertTrue(account.getIBAN().startsWith(this.bank.getCode()));
		Assert.assertEquals(this.client, account.getClient());
		Assert.assertEquals(0, account.getBalance());
		Assert.assertEquals(1, this.bank.getNumberOfAccounts());
		Assert.assertTrue(this.bank.hasClient(this.client));
	}

	@Test(expected=BankException.class)
	public void nullClient() {
		Account account = new Account(this.bank, null);
	}

	@Test(expected=BankException.class)
	public void nullBank() {
		Account account = new Account(null, this.client);
	}

	@Test(expected=BankException.class)
	public void notClient() {
		this.bank1 = new Bank("Moneyz", "BK21");
		Account account = new Account(this.bank1, this.client);
	}


	@After
	public void tearDown() {
		Bank.banks.clear();
	}

}
