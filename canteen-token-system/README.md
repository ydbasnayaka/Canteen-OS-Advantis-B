# Canteen Token Management System

A professional web-based application for managing canteen meal tokens with QR code generation and scanning capabilities.

## Features

### Core Functionality
- **Employee Management**: Complete CRUD operations for employee database
- **Token Generation**: QR code-based meal tokens linked to employee IDs
- **Security Desk**: Check-in employees and issue tokens for different meal types
- **Canteen Counter**: Scan QR codes to redeem tokens
- **Token History**: View and filter all token transactions

### Meal Types Supported
- Breakfast (7:00 AM - 10:00 AM)
- Lunch (12:00 PM - 3:00 PM)
- Dinner (7:00 PM - 10:00 PM)
- Snacks (4:00 PM - 6:00 PM)

### Token Status
- **ISSUED**: Token generated but not yet redeemed
- **REDEEMED**: Token successfully scanned at canteen
- **EXPIRED**: Token past valid time
- **CANCELLED**: Token cancelled by admin

## Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (embedded, can be switched to MySQL/PostgreSQL)
- **ZXing** (QR Code generation)

### Frontend
- **HTML5**
- **CSS3** (with modern styling)
- **JavaScript** (vanilla, no frameworks)
- **Thymeleaf** (template engine)
- **Font Awesome** (icons)
- **html5-qrcode** (QR scanning)

## Project Structure

```
canteen-token-system/
├── src/
│   ├── main/
│   │   ├── java/com/canteen/
│   │   │   ├── CanteenTokenSystemApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── EmployeeRestController.java
│   │   │   │   └── TokenRestController.java
│   │   │   ├── service/
│   │   │   │   ├── EmployeeService.java
│   │   │   │   ├── TokenService.java
│   │   │   │   └── QRCodeService.java
│   │   │   ├── repository/
│   │   │   │   ├── EmployeeRepository.java
│   │   │   │   └── TokenRepository.java
│   │   │   ├── model/
│   │   │   │   ├── Employee.java
│   │   │   │   └── Token.java
│   │   │   └── dto/
│   │   │       ├── EmployeeDTO.java
│   │   │       ├── TokenDTO.java
│   │   │       └── ApiResponse.java
│   │   ├── resources/
│   │   │   ├── templates/
│   │   │   │   ├── dashboard.html
│   │   │   │   ├── security-desk.html
│   │   │   │   ├── canteen-counter.html
│   │   │   │   ├── employee-management.html
│   │   │   │   └── token-history.html
│   │   │   ├── static/
│   │   │   │   ├── css/
│   │   │   │   │   ├── style.css
│   │   │   │   │   └── pages.css
│   │   │   │   ├── js/
│   │   │   │   │   ├── main.js
│   │   │   │   │   ├── security-desk.js
│   │   │   │   │   ├── canteen-counter.js
│   │   │   │   │   ├── employee-management.js
│   │   │   │   │   └── token-history.js
│   │   │   │   └── images/
│   │   │   │       └── default-avatar.svg
│   │   │   └── application.properties
│   │   └── test/
│   └── pom.xml
└── README.md
```

## How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Steps

1. **Navigate to project directory**
   ```bash
   cd canteen-token-system
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   
   Open your browser and go to: `http://localhost:8080`

### Application URLs

| Page | URL |
|------|-----|
| Dashboard | http://localhost:8080/ |
| Security Desk | http://localhost:8080/security |
| Canteen Counter | http://localhost:8080/canteen |
| Employee Management | http://localhost:8080/employees |
| Token History | http://localhost:8080/history |
| H2 Console | http://localhost:8080/h2-console |

### H2 Database Console
- **JDBC URL**: `jdbc:h2:mem:canteendb`
- **Username**: `admin`
- **Password**: `admin123`

## API Endpoints

### Employee APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/employees | Get all employees |
| GET | /api/employees/{id} | Get employee by ID |
| GET | /api/employees/emp-id/{employeeId} | Get employee by employee ID |
| GET | /api/employees/active | Get active employees |
| GET | /api/employees/search?query={name} | Search employees |
| POST | /api/employees | Create new employee |
| PUT | /api/employees/{id} | Update employee |
| DELETE | /api/employees/{id} | Delete employee |

### Token APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/tokens/issue | Issue new token |
| POST | /api/tokens/redeem | Redeem token by QR |
| POST | /api/tokens/redeem-by-number | Redeem by token number |
| GET | /api/tokens/{tokenNumber} | Get token details |
| GET | /api/tokens/today | Get today's tokens |
| GET | /api/tokens/today/{status} | Get tokens by status |
| GET | /api/tokens/stats/today | Get today's stats |
| POST | /api/tokens/cancel | Cancel token |

## Usage Guide

### 1. Adding Employees
1. Go to **Employee Management** page
2. Click "Add Employee" button
3. Fill in employee details (Employee ID, Name, Department, etc.)
4. Click "Save Employee"

### 2. Issuing Tokens (Security Desk)
1. Go to **Security Desk** page
2. Enter Employee ID and click "Search"
3. Select meal type (Breakfast, Lunch, Dinner, Snacks)
4. Click "Issue Token"
5. Print the token with QR code

### 3. Redeeming Tokens (Canteen Counter)
1. Go to **Canteen Counter** page
2. Start the QR scanner
3. Scan the token QR code
4. Verify token details
5. Click "Confirm Redemption"
6. Or enter token number manually and redeem

### 4. Viewing History
1. Go to **Token History** page
2. Use filters to search by date, meal type, status
3. View detailed token information

## Configuration

### Database Configuration
Edit `src/main/resources/application.properties`:

```properties
# For H2 (default)
spring.datasource.url=jdbc:h2:mem:canteendb

# For MySQL
# spring.datasource.url=jdbc:mysql://localhost:3306/canteendb
# spring.datasource.username=root
# spring.datasource.password=password
```

### Server Port
```properties
server.port=8080
```

## Screenshots

### Dashboard
- Overview statistics
- Quick action buttons
- Meal type distribution

### Security Desk
- Employee search
- Token issuance form
- QR code display
- Today's issued tokens

### Canteen Counter
- QR code scanner
- Manual token entry
- Token verification
- Redemption confirmation

### Employee Management
- Employee list with search
- Add/Edit employee modal
- Employee details view

## Future Enhancements

- [ ] User authentication and role-based access
- [ ] Email notifications for token issuance
- [ ] Mobile app for employees
- [ ] Integration with biometric systems
- [ ] Advanced reporting and analytics
- [ ] Multi-canteen support
- [ ] Menu management
- [ ] Feedback system

## License

This project is open source and available for personal and commercial use.

## Support

For issues or questions, please contact the development team.

---

**Developed with ❤️ using Java, Spring Boot, and modern web technologies**
