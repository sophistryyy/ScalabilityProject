document.getElementById('getWalletBalanceBtn').addEventListener('click', getWalletBalance);
document.getElementById('addMoneyBtn').addEventListener('click', addMoneyToWallet);
document.getElementById('getStockPortfolioBtn').addEventListener('click', getStockPortfolio);
document.getElementById('placeStockOrderBtn').addEventListener('click', placeStockOrder); // Add this line
document.getElementById('getStockPricesBtn').addEventListener('click', getStockPrices); // Add this line
document.getElementById('cancelStockOrderBtn').addEventListener('click', cancelStockOrder); // Add this line

const token = localStorage.getItem('token'); // Assuming the token is stored in localStorage
const username = localStorage.getItem('username'); // Assuming username is stored in localStorage
document.getElementById('usernameDisplay').textContent = username; 

function getWalletBalance() {
    fetch('/getWalletBalance', {
        method: 'GET',
        headers: {
            'token': token 
        }
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('walletBalance').textContent = 'Wallet Balance: ' + data.data.balance;
    })
    .catch(error => console.error('Error:', error));
}

function addMoneyToWallet() {
    const amount = document.getElementById('amountToAdd').value;
    fetch('/addMoneyToWallet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'token': token 
        },
        body: JSON.stringify({ amount: parseInt(amount) })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Money added successfully!');
            getWalletBalance(); // Refresh balance
        } else {
            alert('Error adding money: ' + data.message);
        }
    })
    .catch(error => console.error('Error:', error));
}

function getStockPortfolio() {
    fetch('/getStockPortfolio', {
        method: 'GET',
        headers: {
            'token': token 
        }
    })
    .then(response => response.json())
    .then(data => {
        const portfolioList = document.getElementById('stockPortfolio');
        portfolioList.innerHTML = ''; // Clear previous entries
        data.data.forEach(entry => {
            const li = document.createElement('li');
            li.textContent = `Stock ID: ${entry.stock_id}, Name: ${entry.stock_name}, Quantity: ${entry.quantity_owned}`;
            portfolioList.appendChild(li);
        });
    })
    .catch(error => console.error('Error:', error));
}

function placeStockOrder() {
    const stockId = document.getElementById('stockId').value;
    const isBuy = document.getElementById('isBuy').value === 'true';
    const orderType = document.getElementById('orderType').value;
    const quantity = document.getElementById('quantity').value;
    const price = document.getElementById('price').value; // This can be left empty for market orders

    fetch('/placeStockOrder', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'token': token
        },
        body: JSON.stringify({
            stock_id: parseInt(stockId),
            is_buy: isBuy,
            order_type: orderType,
            quantity: parseInt(quantity),
            price: parseInt(price) || undefined // Send undefined if price is not applicable
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Stock order placed successfully!');
        } else {
            alert('Error placing stock order: ' + data.message);
        }
    })
    .catch(error => console.error('Error:', error));
}

function getStockPrices() {
    fetch('/getStockPrices', {
        method: 'GET',
        headers: {
            'token': token  // Assuming token is required for this endpoint
        }
    })
    .then(response => response.json())
    .then(data => {
        const stockPricesList = document.getElementById('stockPricesList');
        stockPricesList.innerHTML = ''; // Clear previous entries
        data.data.forEach(entry => {
            const li = document.createElement('li');
            li.textContent = `Stock ID: ${entry.stock_id}, Name: ${entry.stock_name}, Current Price: $${entry.current_price}`;
            stockPricesList.appendChild(li);
        });
    })
    .catch(error => console.error('Error:', error));
}

function cancelStockOrder() {
    const transactionId = document.getElementById('cancelStockOrderId').value;

    fetch('/cancelStockTransaction', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'token': token  // Assuming the endpoint requires authentication
        },
        body: JSON.stringify({
            stock_tx_id: parseInt(transactionId)
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Stock order canceled successfully!');
        } else {
            alert('Error canceling stock order: ' + data.message);
        }
    })
    .catch(error => console.error('Error:', error));
}


