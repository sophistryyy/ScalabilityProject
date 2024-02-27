document.getElementById('getWalletBalanceBtn').addEventListener('click', getWalletBalance);
document.getElementById('addMoneyBtn').addEventListener('click', addMoneyToWallet);
document.getElementById('getStockPortfolioBtn').addEventListener('click', getStockPortfolio);

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