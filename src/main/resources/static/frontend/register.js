document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registerForm');
    
    form.addEventListener('submit', function(e) {
        e.preventDefault(); // Prevent default form submission

        // Collect user input from the form
        const registrationData = {
            user_name: document.getElementById('username').value,
            password: document.getElementById('password').value,
            name: document.getElementById('name').value,
        };

        // Send the registration data to the server via POST request
        fetch('/register', { // Ensure this matches your Spring Boot application's endpoint
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(registrationData),
        })
        .then(response => {
            if (!response.ok) {
                // If the server response is not ok, throw an error
                throw new Error('Registration request failed with status: ' + response.status);
            }
            return response.json(); // Parse the JSON of the response
        })
        .then(data => {
            if (data.success === true) {
                // If the response contains success: "true", handle successful registration
                alert('Registration successful!');
                window.location.href = '/frontend/login.html'; // Redirect user to the login page
            } else {
                // If the success property does not equal "true", show an error message
                throw new Error(data.message || 'Registration failed. Please try again.');
            }
        })
        .catch(error => {
            console.error('Error during registration:', error);
            alert(error.message); // Display the error message to the user
        });
    });
});