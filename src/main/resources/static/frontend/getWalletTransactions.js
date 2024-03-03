document.getElementById('getWalletTransactionsBtn').addEventListener('click', getWalletTransactions);

const token = localStorage.getItem('token'); // Assuming the token is stored in localStorage

function getWalletTransactions() {
    fetch('/getWalletTransactions', {
        method: 'GET',
        headers: {
            'token': token  // Use this if your endpoint requires authentication
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch wallet transactions');
        }
        return response.json();
    })
    .then(data => displayWalletTransactions(data.data))
    .catch(error => console.error('Error:', error));
}

function displayWalletTransactions(transactions) {
    const container = document.getElementById('walletTransactionsContainer');
    container.innerHTML = ''; // Clear previous data

    const list = document.createElement('ul');
    transactions.forEach(transaction => {
        const item = document.createElement('li');
        item.textContent = `Transaction ID: ${transaction.wallet_tx_id}, ` + 
                           `Stock Transaction ID: ${transaction.stock_tx_id || 'N/A'}, ` +
                           `Type: ${transaction.is_debit ? 'Debit' : 'Credit'}, ` +
                           `Amount: $${transaction.amount}, ` +
                           `Timestamp: ${transaction.time_stamp}`;
        list.appendChild(item);
    });

    container.appendChild(list);
}