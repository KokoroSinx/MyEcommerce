# Sky Take Out Backend (Refactored)

A backend system for a food delivery platform, focusing on real-world engineering improvements.

---

## 📦 Business Flow (End-to-End)

This system simulates a typical food delivery workflow:

1. User browses dishes
2. Adds items to cart
3. Places an order
4. System processes order
5. Merchant prepares food
6. Delivery completes order

Key modules:

- User / Admin management
- Dish & Category management
- Shopping cart
- Order system

---

## 🚀 Deployment (Docker)

The project supports containerized deployment via Docker Compose:

```yaml
version: '3'
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3307:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root
    command: --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7
    ports:
      - "6380:6379"

  rabbitmq:
    image: rabbitmq:management
    ports:
      - "5672:5672"
      - "15672:15672"
