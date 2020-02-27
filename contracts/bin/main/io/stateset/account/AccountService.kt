package io.stateset.account


import co.paralleluniverse.fibers.Suspendable
import net.corda.core.concurrent.CordaFuture
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.AnonymousParty
import net.corda.core.identity.Party
import net.corda.core.internal.concurrent.asCordaFuture
import net.corda.core.internal.concurrent.doneFuture
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.serialization.SerializeAsToken
import net.corda.core.serialization.SingletonSerializeAsToken
import java.security.PublicKey
import java.util.*
import java.util.concurrent.CompletableFuture

@CordaService
interface AccountService : SerializeAsToken {

    // Accounts which the calling node hosts.
    fun myAccounts(): List<StateAndRef<Account>>

    // Returns all accounts, including those hosted by other nodes.
    fun allAccounts(): List<StateAndRef<Account>>

    // Creates a new account and returns the AccountInfo StateAndRef.
    fun createAccount(accountName: String): CordaFuture<StateAndRef<Account>>

    // Overload for creating an account with a specific account ID.
    fun createAccount(accountName: String, accountId: UUID):
            CordaFuture<StateAndRef<Account>>

    // Creates a new KeyPair, links it to the account and returns the publickey.
    fun freshKeyForAccount(accountId: UUID): AnonymousParty

    // Returns all the keys used by the account specified by the account ID.
    fun accountKeys(accountId: UUID): List<PublicKey>

    // Returns the AccountInfo for an account name or account ID.
    fun accountInfo(accountId: UUID): StateAndRef<Account>?

    // Returns the AccountInfo for a given owning key
    fun accountInfo(owningKey: PublicKey): StateAndRef<Account>?

    // The assumption here is that Account names are unique at the node level but are not
    // guaranteed to be unique at the network level. The host Party can be used to
    // de-duplicate account names at the network level.
    fun accountInfo(accountName: String): StateAndRef<Account>?

    // Returns the Party which hosts the account specified by account ID.
    fun hostForAccount(accountId: UUID): Party?

    // Allows the account host to perform a vault query for the specified account ID.
    fun ownedByAccountVaultQuery(
            accountIds: List<UUID>,
            queryCriteria: QueryCriteria
    ): List<StateAndRef<*>>

    fun broadcastedToAccountVaultQuery(
            accountIds: List<UUID>,
            queryCriteria: QueryCriteria
    ): List<StateAndRef<*>>

    fun ownedByAccountVaultQuery(
            accountId: UUID,
            queryCriteria: QueryCriteria
    ): List<StateAndRef<*>>

    fun broadcastedToAccountVaultQuery(
            accountId: UUID,
            queryCriteria: QueryCriteria
    ): List<StateAndRef<*>>

    // Updates the account info with new account details. This may involve creating a
    // new account on another node with the new details. Once the new account has
    // been created, all the states can be moved to the new account.
    fun moveAccount(currentInfo: StateAndRef<Account>, newInfo: Account)

    // De-activates the account.
    fun deactivateAccount(accountId: UUID)

    // Sends AccountInfo specified by the account ID, to the specified Party. The
    // receiving Party will be able to access the AccountInfo from their AccountService.
    fun shareAccountInfoWithParty(accountId: UUID, party: Party): CordaFuture<Unit>

    fun <T : ContractState> broadcastStateToAccount(accountId: UUID, state: StateAndRef<T>): CordaFuture<Unit>
}