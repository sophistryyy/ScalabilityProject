document.getElementById('getStockTransactionsBtn').addEventListener('click', getStockTransactions);

const token = localStorage.getItem('token'); // Assuming the token is stored in localStorage

function getStockTransactions() {
    fetch('/getStockTransactions', {
        method: 'GET',
        headers: {
            'token': token
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch stock transactions');
        }
        return response.json();
    })
    .then(data => displayStockTransactions(data.data))
    .catch(error => console.error('Error:', error));
}

function displayStockTransactions(transactions) {
    const container = document.getElementById('stockTransactionsContainer');
    container.innerHTML = ''; // Clear previous data

    const list = document.createElement('ul');
    transactions.forEach(transaction => {
        const item = document.createElement('li');
        item.textContent = `Transaction ID: ${transaction.stock_tx_id}, Stock ID: ${transaction.stock_id}, ` + 
                           `Type: ${transaction.is_buy ? 'Buy' : 'Sell'}, Status: ${transaction.order_status}, ` +
                           `Price: $${transaction.stock_price}, Quantity: ${transaction.quantity}, ` +
                           `Timestamp: ${transaction.time_stamp}`;
        list.appendChild(item);
    });

    container.appendChild(list);
}