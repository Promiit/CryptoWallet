# CryptoWallet
Android wallet to store Ethereum addresses.

Currently offers functionality to generate Ethereum addresses and view balances.

# Technologies 
Android Application written in Java using Volley library to make API calls and SQLite database to store user addresses.

Backend is a work-in-progress. Uses Node.js and Express to create an API endpoint to view address balances, connects to an Ethereum Node via JSON RPC calls.

Note: Requires an Ethereum Node with a JSON RPC server to connect to (by default on localhost at port 3334)

TODO: Working on adding functionality for viewing transaction history, and unlocking wallet securely in order to send transactions


See it in action!


<center><img src="/example.gif" width="30%" height="30%"/></center>
