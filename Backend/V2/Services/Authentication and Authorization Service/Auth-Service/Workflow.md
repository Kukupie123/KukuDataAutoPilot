# Database setup and flow

## Connection and optional initial processing.
To connect a database we first need to have it's driver mentioned in the maven.
Next we need to create a connection factory which will create a connection to the database
[Connection Factory Snippet](./src/main/java/dev/kukukodes/KDAP/Auth/Service/config/DbConfig.java)
We mark it as bean and set it to be active in "test" profile.

Next we might want to do some post connection initialization. In my case I wanted to make sure the table schema is created.
To initialize the database after it has been connected we override existing ```ConnectionFactoryInitializer``` by creating our own bean
[ConnectionFactoryInitializer Snippet](./src/main/java/dev/kukukodes/KDAP/Auth/Service/config/DbConfig.java)

## Creating repositories
Now that we have connected and initialized our database connection we now will create repositories.
We can do so by annotating a class with ```@Repository``` and then use ```R2dbcEntityTemplate``` to easily do queries.
But first, make sure to create a class to represent the row of your table and annotate it with ```@Table("TableName")```. 
```java
@Table("Users") //Important: This helps R2DBC repository know which table to look for
@Data
public class UserDbLevel {

    @Id
    private Integer id;

    @Column("userID")
    private String userID;

    @Column("passwordHash")
    private String passwordHash;
    
}
```
This is very important
R2DBC uses the name specified in ```@Table``` to determine which table to query to.
If you want to have all core queries then you can make use of ```ReactiveCrudRepository```.

```java
public interface IUserRepository extends ReactiveCrudRepository<EntityModel, IDType> {
    //leave it empty or
    @Override
    @Query(("Optional Query param incase you wanna override the query statement"))
    Mono<EntityModel> findByID(int id);
}
```
We created our repository by having an interface which has all functions required mentioned
[UserRepository Interface](./src/main/java/dev/kukukodes/KDAP/Auth/Service/repo/IUserRepository.java)
And then implemented a test class which will only be active for test profile
[UserRepository Test Implementation](./src/main/java/dev/kukukodes/KDAP/Auth/Service/repo/UserRepositoryTest.java)

# Custom Authentication with basic id and password

When it comes to authorization we have filters. We just have to override the existing filters. There are many filters and we can choose which filter to override.
There are many ways to do this. In our case we decided to implement our own ```ReactiveAuthenticationManager```. This 
