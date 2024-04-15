import csv
import random
import string

def generate_random_credentials():
    username = ''.join(random.choices(string.ascii_lowercase + string.digits, k=16))
    password = ''.join(random.choices(string.ascii_letters + string.digits, k=16))
    name = ''.join(random.choices(string.ascii_uppercase, k=6))
    return username, password, name

def create_csv(filename, num_entries):
    with open(filename, 'w', newline='') as csvfile:
        fieldnames = ['username', 'password', 'name']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        
        writer.writeheader()
        for _ in range(num_entries):
            username, password, name = generate_random_credentials()
            writer.writerow({'username': username, 'password': password, 'name': name})

if __name__ == "__main__":
    num_entries = int(input("Enter the number of entries: "))
    create_csv("generated_credentials.csv", num_entries)
    print(f"{num_entries} entries have been generated and saved to 'generated_credentials.csv'.")