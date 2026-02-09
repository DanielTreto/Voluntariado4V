#!/bin/sh
set -e

echo "Waiting for database to be ready..."
# We can't easily wait for SQL Server with a simple command without mssql-tools being configured,
# but docker-compose healthcheck already handles this.
# However, adding a small sleep or a loop is a safety net.

echo "Creating database if it doesn't exist..."
php bin/console doctrine:database:create --if-not-exists --no-interaction

echo "Running migrations..."
php bin/console doctrine:migrations:migrate --no-interaction

echo "Database initialization complete."

# Execute the main container command (php-fpm)
exec "$@"
