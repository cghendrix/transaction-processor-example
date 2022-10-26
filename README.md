### Backend Engineering Exercise 
This is example code I used for a previous interview to show my engineering abilities. 
This is for a fictional credit card company that processes transactions and applies rewards to a merchant account.

#### Problem statement
A credit card rewards company is introducing a 3x point bonus on dining transactions, but the current 
transaction processing mechanism still only awards 1x points.

Additionally, when calling the external point bonus API, calls randomly error out.
This causes issues with the reconciliation processes.

#### Objectives
* Input data is appropriately cleansed to comply with database constraints
* Point calculation mechanism is implemented and awards 3x points on all dining charges 
* Client invoking the Bonus API is improved to compensate for intermittent failures

#### Specifications
* All transaction data is stored in `src/main/resources/TRANSACTIONS.csv`
* Database schema is defined in `src/main/resources/db/migration/`
* Dining category is defined by `MCC` column value being equal to `5812`
* Point values should be rounded **down** from transaction amounts
* Bonus API will error out at most once on every other call

#### Testing
* To run the tests simply run `mvn test`
