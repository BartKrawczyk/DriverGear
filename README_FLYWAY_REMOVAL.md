# Flyway Removal Documentation

## Overview

This document explains the removal of Flyway from the DriverGear project and provides guidance on how database schema changes should be managed going forward.

## Changes Made

The following changes were made to remove Flyway from the project:

1. **Entity Modifications**:
   - Updated `PositionClothingItem` entity to include `columnDefinition = "DECIMAL(10,2) NOT NULL DEFAULT 0.00"` for the `compensationAmount` field
   - This ensures that Hibernate's schema generation creates the column with the correct DEFAULT constraint

2. **Configuration Changes**:
   - Removed Flyway dependencies from `pom.xml`
   - Removed Flyway configuration from `application.properties`
   - Changed Hibernate's `ddl-auto` setting from `validate` to `create-drop` (for development) or `update` (for production)
   - Set `spring.sql.init.mode=never` to disable schema.sql execution when the file is empty

3. **Code Cleanup**:
   - Removed the `DatabaseConfig` class that was used to manage the circular dependency between Flyway and EntityManagerFactory

## Why Flyway Was Removed

Flyway was initially added to the project to handle specific database schema changes that Hibernate's auto-schema generation couldn't handle well, particularly adding a DEFAULT constraint to the `compensation_amount` column in the `position_clothing_items` table.

However, this can be achieved more simply by:
1. Using the `columnDefinition` attribute in the entity class
2. Configuring Hibernate to generate/update the schema

Removing Flyway simplifies the project configuration and eliminates the circular dependency issue between Flyway and EntityManagerFactory.

## Managing Database Schema Changes Going Forward

### Development Environment

For development, use Hibernate's `create-drop` mode:
```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

This will recreate the schema each time the application starts, which is useful during development.

### Production Environment

For production, use Hibernate's `update` mode:
```properties
spring.jpa.hibernate.ddl-auto=update
```

This will update the schema as needed without dropping existing tables.

### Adding Column Constraints

When adding constraints like DEFAULT values to columns:

1. Use the `columnDefinition` attribute in the entity class:
   ```java
   @Column(nullable = false, columnDefinition = "DECIMAL(10,2) NOT NULL DEFAULT 0.00")
   private BigDecimal amount = BigDecimal.ZERO;
   ```

2. Always initialize fields with default values in the entity class:
   ```java
   private BigDecimal amount = BigDecimal.ZERO;
   ```

3. Implement custom setters to handle null values:
   ```java
   public void setAmount(BigDecimal amount) {
       this.amount = amount != null ? amount : BigDecimal.ZERO;
   }
   ```

### Complex Schema Changes

For complex schema changes that Hibernate can't handle:

1. Use `schema.sql` files in the `src/main/resources` directory
2. Set `spring.sql.init.mode=always` in `application.properties` when the schema.sql file contains actual SQL statements
3. Keep `spring.sql.init.mode=never` when the schema.sql file is empty to avoid errors
4. Document the changes in the codebase

## Conclusion

By removing Flyway and properly configuring Hibernate, we've simplified the project while maintaining the ability to manage database schema changes effectively. This approach is more aligned with the JPA/Hibernate paradigm and eliminates configuration complexity.
