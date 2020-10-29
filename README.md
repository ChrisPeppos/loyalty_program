# Loyalty Program

We have a loyalty program for our customers. We give them points every time they spend with us. Later the customer is allowed to use the loyalty points by converting them into money.

As soon as you RUN the application you will get a list of options:
  
  * 1 Add new customer
  * 2 Add new transaction
  * 3 See customer's data
  * 4 Update loyalty points.
  * 5 Exit
  
**Option 1**:
  adds a new customer to the list of customers and automatically provides it with a CustomerId (unique and incremental)
  
**Option 2**:
  Input -> Customer ID, Transaction Amount, Date of transaction(transaction date was made manual in order to be able to add a range of dates and not wait for a whole week)
  adds a new transaction to the list of transactions and automatically provides it with a TransactionId(unique and incremental)
  also has CustomerId field in order to keep track of multiple transactions by the same Customer
  
**Option 3**:
  Input -> Customer ID
  You can see on the console the data of a specific customer like current pending and available points, date of the last transaction and the amount of consecutive days with      transactions
  
**Option 4**:
  Input -> Customer ID
  Checks if the Customer if aligible for upgrading its Pending points to Available points and if the Customer does then it automatically upgrades them
  Besides this it also checks if the Customer made any transactions in the last 5 weeks and if not, all loyalty points are reset to 0
  
**Option 5**:
  Exits the application
