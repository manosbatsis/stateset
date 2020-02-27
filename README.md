# Stateset

Stateset is a financial network for digital commerce working capital automation.

```
	   8 Node Network Graph | 28 Edges | 1 Notary
-------------------------------------------------------------------

	 /--------\   /--------\   /--------\                                   
	|	   | |	        | |          |                  
	|  PartyB  | |  PartyC  | |  PartyD  | 
	|          | |	   	| |          |                   
 	 \--------/   \--------/   \--------/

 /--------\	      /--------\	   /--------\
|	   |	     |	        |	  |	     |
|  PartyA  |	     |  Notary  |	  |  PartyE  | 
|	   |	     |	        |	  |	     | 
 \--------/	      \--------/           \--------/

	 /--------\   /--------\   /--------\                                   
	|	   | |	        | |          |                            
	|  PartyH  | |  PartyG  | |  PartyF  | 
	|          | |	        | |          |                             
 	 \--------/   \--------/   \--------/

--------------------------------------------------------------------


```


### Stateset Network Setup


1) Install the Stateset Network locally via Git:

```bash

git clone https://gitlab.com/stateset/stateset

```

2) Deploy the Nodes


```bash

cd stateset && gradlew.bat deployNodes (Windows) OR ./gradlew deployNodes (Linux)

```

3) Run the Nodes

```bash

cd workflows
cd build 
cd nodes
runnodes.bat (Windows) OR ./runnodes (Linux)

```
4) Run the Spring Boot Server

```bash

cd ..
cd ..
cd server
../gradlew.bat bootRun -x test (Windows) OR ../gradlew bootRun -x test

```
The Stateset Network API Swagger will be running at `http://localhost:8080/swagger-ui.html#/`

To change the name of your `organisation` or any other parameters, edit the `node.conf` file and repeat the above steps.

### Joining the Network

Add the following to the `node.conf` file:

`compatibilityZoneUrl="https://stateset.network:8080"`

This is the current network map and doorman server URL

1) Remove Existing Network Parameters and Certificates

```bash

cd build
cd nodes
cd PartyA
rm -rf persistence.mv.db nodeInfo-* network-parameters certificates additional-node-infos

```

2) Download the Network Truststore

```bash

curl -o /var/tmp/network-truststore.jks https://stateset.network:8080//network-map/truststore

```

3) Initial Node Registration

```bash

java -jar corda.jar --initial-registration --network-root-truststore /var/tmp/network-truststore.jks --network-root-truststore-password trustpass

```
4) Start the Node

```bash

java -jar corda.jar

```

#### Node Configuration

Configuration 

- Corda version: Corda 4
- Vault SQL Database: PostgreSQL
- Cloud Service Provider: GCP
- JVM or Kubernetes


### Network States

Customer States are transferred between stakeholders on the network.

#### Accounts

The first state to be deployed on the network is the `Account`. Version 0.1 of the `Account` State has the following structure:

```jsx

// *********
// * Account State *
// *********

data class Account(val accountId: String,
                   val accountName: String,
                   val accountType: TypeOfBusiness,
                   val industry: String,
                   val phone: String,
                   val yearStarted: Int,
                   val annualRevenue: Double,
                   val businessAddress: String,
                   val businessCity: String,
                   val businessState: String,
                   val businessZipCode: String,
                   val controller: Party,
                   val processor: Party ) : ContractState, QueryableState {


```

The Account has the following business `flows` that can be called:

- `CreateAccount` - Create an Account between your organization and a known counterparty on the DSOA
- `TransferAccount` - Transfer the Account between your organization and a counterparty on the DSOA
- `ShareAccount` - Share the Account Data with a counterparty
- `EraseAccount` - Erase the Account Data

#### Contacts

The second state to be deployed on the network is the `Contact`. Version 0.1 of the `Contact` State has the following structure:

```jsx

// *********
// * Contact State *
// *********

data class Contact(val contactId: String,
                   val firstName: String,
                   val lastName: String,
                   val email: String,
                   val phone: String,
                   val controller: Party,
                   val processor: Party,
                   override val linearId: UniqueIdentifier = UniqueIdentifier())


```


The Contact has the following business `flows` that can be called:

- `CreateContact` - Create a Contact between your organization and a known counterparty on the DSOA
- `TransferContact` - Transfer the Contact between your organization and a counterparty on the DSOA
- `ShareContact` - Share the Contact Data with a counterparty
- `EraseContact` - Erase the Contact Data

#### Leads

The third state to be deployed on the network is the `Lead`. Version 0.1 of the `Lead` State has the following structure:

```jsx

// *********
// * Lead State *
// *********

data class Lead(val leadId: String,
                val firstName: String,
                val lastName: String,
                val company: String,
                val title: String,
                val email: String,
                val phone: String,
                val country: String,
                val controller: Party,
                val processor: Party,
                override val linearId: UniqueIdentifier = UniqueIdentifier())


```


The Lead has the following business `flows` that can be called:

- `CreateLead` - Create a Lead between your organization and a known counterparty on the DSOA
- `TransferLead` - Transfer the Lead between your organization and a counterparty on the DSOA
- `ShareLead` - Share the Lead Data with a counterparty
- `EraseLead` - Erase the Lead Data
- `ConvertLead` - Convert a Lead State into an Account State and Contact State


We created the `Carmen Dashboard` to provide the ability for organizations to create `Accounts`, `Contacts`, and `Leads` with counterparties on the network.


#### Cases


```jsx

// *********
// * Case State *
// *********

data class Case(val caseId: String,
                val description: String,
                val caseNumber: String,
                val caseStatus: CaseStatus,
                val casePriority: CasePriority,
                val submitter: Party,
                val resolver: Party,
                override val linearId: UniqueIdentifier = UniqueIdentifier()) 


```

The Case has the following business `flows` that can be called:

- `CreateCase` - Create a Case between your organization and a known counterparty on the DSOA
- `StartCase` - Start on an unstarted Case
- `CloseCase` - Close the Case with a counterparty
- `EscalateCase` - Escalate the Case

#### Agreements


```jsx

// *****************
// * Agreement State *
// *****************

@BelongsToContract(AgreementContract::class)
data class Agreement(val agreementNumber: String,
                     val agreementName: String,
                     val agreementHash: String,
                     val agreementStatus: AgreementStatus,
                     val agreementType: AgreementType,
                     val totalAgreementValue: Int,
                     val party: Party,
                     val counterparty: Party,
                     val agreementStartDate: String,
                     val agreementEndDate: String,
                     val active: Boolean?,
                     val createdAt: String?,
                     val lastUpdated: String?,
                     override val linearId: UniqueIdentifier = UniqueIdentifier()) : ContractState, LinearState, QueryableState {


```

The Agreement has the following business `flows` that can be called:

- `CreateAgreement` - Create an Agreement between your organization and a known counterparty on the DSOA
- `ActivateAgreement` - Activate the Agreement between your organization and a counterparty on the DSOA
- `TerminateAgreement` - Terminate an existing or active agreement
- `RenewAgreement` - Renew an existing agreement that is or is about to expire
- `ExpireAgreement` - Expire a currently active agreement between you and a counterparty

The `Agreement Status` and `Agreement Type` enums are listed as follows:

```jsx


@CordaSerializable
enum class AgreementStatus {
    REQUEST, APPROVAL_REQUIRED, APPROVED, IN_REVIEW, ACTIVATED, INEFFECT, REJECTED, RENEWED, TERMINATED, AMENDED, SUPERSEDED, EXPIRED
}

@CordaSerializable
enum class AgreementType {
    NDA, MSA, SLA, SOW
}

```

#### Loans

```jsx


// *****************
// * Loan State *
// *****************

@BelongsToContract(LoanContract::class)
data class Loan(val loanNumber: String,
                val loanName: String,
                val loanReason: String,
                val loanStatus: LoanStatus,
                val loanType: LoanType,
                val amountDue: Int,
                val amountPaid: Int,
                val amountRemaining: Int,
                val subtotal: Int,
                val total: Int,
                val party: Party,
                val counterparty: Party,
                val dueDate: String,
                val periodStartDate: String,
                val periodEndDate: String,
                val paid: Boolean?,
                val active: Boolean?,
                val createdAt: String?,
                val lastUpdated: String?,
                override val linearId: UniqueIdentifier = UniqueIdentifier()) : ContractState, LinearState, QueryableState {


```

The Loan has the following business `flows` that can be called:

- `CreateLoan` - Create a Loan between your organization and a known counterparty
- `PayLoan` - Pay off a Loan


#### Invoices

```jsx


// *****************
// * Invoice State *
// *****************

@BelongsToContract(InvoiceContract::class)
data class Invoice(val invoiceNumber: String,
                   val invoiceName: String,
                   val billingReason: String,
                   val amountDue: Int,
                   val amountPaid: Int,
                   val amountRemaining: Int,
                   val subtotal: Int,
                   val total: Int,
                   val party: Party,
                   val counterparty: Party,
                   val dueDate: String,
                   val periodStartDate: String,
                   val periodEndDate: String,
                   val paid: Boolean?,
                   val active: Boolean?,
                   val createdAt: String?,
                   val lastUpdated: String?,
                   override val linearId: UniqueIdentifier = UniqueIdentifier()) : ContractState, LinearState, QueryableState {


```

The Invoice has the following business `flows` that can be called:

- `CreateInvoice` - Create a Invoice between your organization and a known counterparty
- `PayInvoice` - Pay an Invoice
- `FactorInvoice` - Factor an Invoice
