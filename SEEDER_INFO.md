# Data Seeder Documentation

## Overview
The `DataSeeder` class automatically seeds your database with initial data for users, roles, and permissions when the application starts.

## What Gets Seeded

### 1. Permissions (26 total)
All permissions from the `PermissionName` enum are created with appropriate descriptions:

#### Fournisseur (Supplier) Permissions
- `FOURNISSEUR_CREATE` - Create supplier
- `FOURNISSEUR_UPDATE` - Update supplier
- `FOURNISSEUR_DELETE` - Delete supplier
- `FOURNISSEUR_READ` - Read supplier

#### Produit (Product) Permissions
- `PRODUIT_CREATE` - Create product
- `PRODUIT_UPDATE` - Update product
- `PRODUIT_DELETE` - Delete product
- `PRODUIT_READ` - Read product
- `PRODUIT_CONFIGURE_SEUIL` - Configure product threshold

#### Commande (Order) Permissions
- `COMMANDE_CREATE` - Create order
- `COMMANDE_UPDATE` - Update order
- `COMMANDE_VALIDATE` - Validate order
- `COMMANDE_CANCEL` - Cancel order
- `COMMANDE_RECEIVE` - Receive order
- `COMMANDE_READ` - Read order

#### Stock Permissions
- `STOCK_READ` - Read stock
- `STOCK_VALORISATION` - Stock valuation
- `STOCK_HISTORIQUE` - Stock history

#### Bon de Sortie (Exit Voucher) Permissions
- `BON_SORTIE_CREATE` - Create exit voucher
- `BON_SORTIE_VALIDATE` - Validate exit voucher
- `BON_SORTIE_CANCEL` - Cancel exit voucher
- `BON_SORTIE_READ` - Read exit voucher

#### Management Permissions
- `USER_MANAGE` - Manage users
- `AUDIT_READ` - Read audit logs

### 2. Roles (4 roles with specific permissions)

#### ADMIN
- **Description**: Administrator with full access
- **Permissions**: ALL (26 permissions)

#### RESPONSABLE_ACHATS (Purchasing Manager)
- **Description**: Purchasing manager
- **Permissions**:
  - All Fournisseur permissions (except DELETE)
  - PRODUIT_READ, PRODUIT_CONFIGURE_SEUIL
  - All Commande permissions
  - All Stock permissions

#### MAGASINIER (Warehouse Keeper)
- **Description**: Warehouse keeper
- **Permissions**:
  - PRODUIT_READ
  - COMMANDE_READ, COMMANDE_RECEIVE
  - STOCK_READ, STOCK_HISTORIQUE
  - All Bon de Sortie permissions (except CANCEL)

#### CHEF_ATELIER (Workshop Manager)
- **Description**: Workshop manager
- **Permissions**:
  - PRODUIT_READ
  - STOCK_READ
  - BON_SORTIE_CREATE, BON_SORTIE_READ

### 3. Users (4 default users)

| Username | Email | Password | Full Name | Role |
|----------|-------|----------|-----------|------|
| admin | admin@tricol.com | admin123 | System Administrator | ADMIN |
| responsable | responsable@tricol.com | responsable123 | Jean Dupont | RESPONSABLE_ACHATS |
| magasinier | magasinier@tricol.com | magasinier123 | Marie Martin | MAGASINIER |
| chef_atelier | chef.atelier@tricol.com | chef123 | Pierre Bernard | CHEF_ATELIER |

## How It Works

The seeder runs automatically when the application starts (implements `CommandLineRunner`). It follows this sequence:

1. **Seeds Permissions**: Creates all 26 permissions if they don't exist
2. **Seeds Roles**: Creates all 4 roles with their respective permissions if they don't exist
3. **Seeds Users**: Creates 4 default users if they don't exist

Each step checks if data already exists to avoid duplication on subsequent application restarts.

## Security

- All passwords are encrypted using BCryptPasswordEncoder before being stored
- Users are enabled by default and not locked
- The admin user has full access to all system features

## Testing

You can test the seeded data by:

1. Starting the application
2. Check the logs for seeding messages
3. Login with any of the default users:
   ```
   Username: admin
   Password: admin123
   ```

## Customization

To modify the seeder:
- Add/remove permissions in `PermissionName` enum
- Adjust role permissions in the `seedRoles()` method
- Add more default users in the `seedUsers()` method
- Modify user credentials (remember to change in production!)

## Production Considerations

⚠️ **IMPORTANT**: Before deploying to production:
1. Change all default passwords
2. Consider removing or disabling test users
3. Add environment-specific configuration for different user sets
4. Use strong, unique passwords
5. Consider using a separate migration tool for production data

