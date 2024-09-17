To connect to both PostgreSQL and IBM Db2 databases using Python, you'll need to use two different libraries: `psycopg2` for PostgreSQL and `ibm_db` for IBM Db2.

### Prerequisites:

1. **Install the Required Libraries:**
   Make sure you have both `psycopg2` (for PostgreSQL) and `ibm_db` (for Db2) installed. You can install them using pip:

   ```sh
   pip install psycopg2-binary
   pip install ibm_db
   ```

### Python Script to Connect to PostgreSQL and IBM Db2:

Here's a Python script that connects to both databases, fetches data from tables, and allows you to perform comparisons:

```python
import psycopg2
import ibm_db

# PostgreSQL connection details
pg_host = "your_postgres_host"
pg_port = "5432"
pg_database = "your_postgres_database"
pg_user = "your_postgres_user"
pg_password = "your_postgres_password"

# Db2 connection details
db2_database = "your_db2_database"
db2_hostname = "your_db2_host"
db2_port = "50000"  # Replace with your Db2 port
db2_protocol = "TCPIP"
db2_uid = "your_db2_user"
db2_pwd = "your_db2_password"

try:
    # Connect to PostgreSQL
    pg_conn = psycopg2.connect(
        host=pg_host,
        port=pg_port,
        database=pg_database,
        user=pg_user,
        password=pg_password
    )
    pg_cursor = pg_conn.cursor()
    print("Connected to PostgreSQL database")

    # Example query to fetch data from PostgreSQL
    pg_cursor.execute("SELECT * FROM your_postgres_table LIMIT 10;")
    pg_data = pg_cursor.fetchall()
    print("PostgreSQL Data:")
    for row in pg_data:
        print(row)

    # Connect to Db2
    db2_conn_str = (
        f"DATABASE={db2_database};"
        f"HOSTNAME={db2_hostname};"
        f"PORT={db2_port};"
        f"PROTOCOL={db2_protocol};"
        f"UID={db2_uid};"
        f"PWD={db2_pwd};"
    )
    
    db2_conn = ibm_db.connect(db2_conn_str, "", "")
    print("Connected to Db2 database")

    # Example query to fetch data from Db2
    db2_sql = "SELECT * FROM your_db2_table FETCH FIRST 10 ROWS ONLY"
    db2_stmt = ibm_db.exec_immediate(db2_conn, db2_sql)
    db2_row = ibm_db.fetch_assoc(db2_stmt)

    print("Db2 Data:")
    while db2_row:
        print(db2_row)
        db2_row = ibm_db.fetch_assoc(db2_stmt)

except Exception as e:
    print("An error occurred:", e)

finally:
    # Close PostgreSQL connection
    if 'pg_conn' in locals():
        pg_cursor.close()
        pg_conn.close()
        print("PostgreSQL connection closed")

    # Close Db2 connection
    if 'db2_conn' in locals() and db2_conn:
        ibm_db.close(db2_conn)
        print("Db2 connection closed")
```

### Explanation:

1. **PostgreSQL Connection (`psycopg2`):**
   - This part of the script connects to the PostgreSQL database using `psycopg2`.
   - Replace placeholders like `your_postgres_host`, `your_postgres_database`, etc., with your actual PostgreSQL connection details.
   - Fetches data from a PostgreSQL table using an SQL query.

2. **Db2 Connection (`ibm_db`):**
   - This part connects to the IBM Db2 database using `ibm_db`.
   - Similarly, replace placeholders like `your_db2_database`, `your_db2_host`, etc., with your actual Db2 connection details.
   - Executes a query to fetch data from a Db2 table.

3. **Error Handling and Cleanup:**
   - The script includes `try` and `finally` blocks to handle exceptions and ensure connections are properly closed.

### Running the Script:

- Save the script to a Python file, e.g., `db_compare.py`.
- Run the script in your terminal or command prompt:

   ```sh
   python db_compare.py
   ```

### Notes:

- Ensure that both databases are accessible from the machine running this script and that you have the correct credentials.
- Adjust the SQL queries (`pg_cursor.execute` and `db2_sql`) to match the tables and columns you want to query.
- Make sure any necessary client software or drivers are installed and configured, especially for IBM Db2 (`ibm_db` might require a Db2 client or specific drivers).

By using this script, you can connect to both PostgreSQL and IBM Db2 databases, fetch data, and perform comparisons as needed.
