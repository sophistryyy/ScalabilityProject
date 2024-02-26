document.addEventListener('DOMContentLoaded', function() {
    const createStockForm = document.getElementById('createStockForm');
    const addStockToUserForm = document.getElementById('addStockToUserForm');

    // Retrieve the JWT token from storage
    const token = localStorage.getItem('token'); // Or sessionStorage.getItem('token');
    console.log('JWT Token:', token);


    createStockForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const stockName = document.getElementById('stockName').value;
        console.log('JWT Token:', token);

        fetch('/createStock', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'token': token  // Include the JWT token in the Authorization header
            },
            body: JSON.stringify({ stock_name: stockName }),
        })
        .then(response => response.json())
        .then(data => {
            if (data.success === true) {
                alert('Stock created successfully! Stock ID: ' + data.data.stock_id);
                document.getElementById('stockId').value = data.data.stock_id; // Autofill stock ID for convenience
            } else {
                throw new Error('Failed to create stock. ' + data.message);
            }
        })
        .catch(error => {
            alert(error.message);
        });
    });

    addStockToUserForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const stockId = document.getElementById('stockId').value;
        const quantity = document.getElementById('quantity').value;

        fetch('/addStockToUser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'token': token  // Include the JWT token in the Authorization header
            },
            body: JSON.stringify({ stock_id: parseInt(stockId, 10), quantity: parseInt(quantity, 10) }),
        })
        .then(response => response.json())
        .then(data => {
            if (data.success === true) {
                alert('Stock added to portfolio successfully!');
            } else {
                throw new Error('Failed to add stock to portfolio. ' + data.message);
            }
        })
        .catch(error => {
            alert(error.message);
        });
    });
});