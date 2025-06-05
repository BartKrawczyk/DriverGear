# Fix for "Field 'compensation_amount' doesn't have a default value" Error

## Problem Description

The application is encountering an error when adding a clothing allowance:
```
Wystąpił nieoczekiwany błąd podczas dodawania przydziału odzieżowego. Szczegóły: could not execute statement; nested exception is org.hibernate.exception.GenericJDBCException: could not execute statement
```

This error occurs because the `compensation_amount` column in the `position_clothing_items` table is defined as `NOT NULL` but doesn't have a default value. If a null value is somehow passed to the database, it will reject the insert operation.

## Comprehensive Solution

We've implemented a multi-layered solution to ensure this issue is fixed at all levels:

### 1. Entity-Level Fix

The `PositionClothingItem` entity has been updated to include a column definition that specifies a default value:

```java
@Column(nullable = false, columnDefinition = "DECIMAL(10,2) NOT NULL DEFAULT 0.00")
private BigDecimal compensationAmount = BigDecimal.ZERO;  // kwota ekwiwalentu za element
```

This ensures that Hibernate will create the column with a default value when generating the schema.

### 2. Database-Level Fix

For existing databases, we've created a direct SQL script that can be executed immediately:

```sql
ALTER TABLE position_clothing_items MODIFY compensation_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00;
```

This script is available in the project root as `fix_compensation_amount.sql`.

### 3. Migration Management with Flyway

We've added Flyway to manage database migrations in a more reliable way:

1. Added Flyway dependencies to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
   </dependency>
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-mysql</artifactId>
   </dependency>
   ```

2. Configured Flyway in `application.properties`:
   ```properties
   spring.flyway.enabled=true
   spring.flyway.baseline-on-migrate=true
   spring.flyway.locations=classpath:db/migration
   spring.flyway.validate-on-migrate=true
   ```

3. Created a Flyway migration script at `src/main/resources/db/migration/V1__add_default_value_to_compensation_amount.sql`

### 4. Service-Level Defensive Programming

The service layer has been updated to ensure that null compensationAmount values are handled properly:

```java
// Set default value (BigDecimal.ZERO) if compensationAmount is null
item.setCompensationAmount(itemDTO.getCompensationAmount() != null ? 
    itemDTO.getCompensationAmount() : java.math.BigDecimal.ZERO);
```

### 5. Controller-Level Validation

The controller layer validates and sets default values for compensationAmount:

```java
// Ensure compensationAmount is not null (will be set to ZERO if null in service)
if (item.getCompensationAmount() == null) {
    item.setCompensationAmount(BigDecimal.ZERO);
}
```

### 6. Frontend JavaScript Validation

Client-side validation ensures compensationAmount is never empty:

```javascript
// Ensure compensationAmount is never empty and defaults to 0
const compensationAmountInputs = document.querySelectorAll('input[id$=".compensationAmount"]');
compensationAmountInputs.forEach(input => {
  if (!input.value || input.value.trim() === '') {
    input.value = '0';
  }
});
```

## How to Apply the Fix

### Immediate Fix (Database-Level)

1. Execute the `fix_compensation_amount.sql` script directly on your database:
   ```
   mysql -u dbuser -p drivergear < fix_compensation_amount.sql
   ```
   Or use a database management tool like MySQL Workbench to run the script.

### Complete Fix (Application-Level)

1. Update the `PositionClothingItem` entity class with the new column definition.
2. Add Flyway dependencies to `pom.xml`.
3. Configure Flyway in `application.properties`.
4. Ensure the Flyway migration script exists in `src/main/resources/db/migration`.
5. Restart the application. Flyway will automatically execute the migration script.

## Verification

After applying the fix, try adding a new clothing allowance to verify that the error no longer occurs. The application should now handle the compensationAmount field correctly at all levels.
