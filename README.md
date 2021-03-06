# SQL-Editor-GUI-Java-Swing

A light weight Sql GUI tool used visualize database and perform operations.

**Features:**

1. Listing tables
2. Listing of all rows and columns of a table
3. INSERT, UPDATE, DELETE and SEARCH operations on a table
4. If a table has foreign_key, on row selection it also displays foreign tables and displays the rows thorugh which relation is made.
5. Filtering columns


<image src="samples/connection.png" width= 400>


<image src="samples/table1.png" width= 600>


<image src="samples/table2.png" width= 600>


<image src="samples/add.png" width= 400>


<image src="samples/search_filter.png" width= 400>


**Note:**

Connection URL basically represents the connection string used by **java.sql.DriverManager** to get connection. This includes database(mysql/sqlserver/oracle), host url/ip, port and database name. Example connection strings can easily be found on internet. If you are unable to connect to database, bad connection string might be the cause.


**What's used:**

1. Language: JAVA
2. Toolkit: Swing
3. Architecture: MVC
4. Driver: JDBC


**What's next:**

1. Filtering to be done using query. Currently the columns are filtered out locally.
2. Sorting based on columns
3. An interface that allows to execute custom query.


