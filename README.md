# URL Shortener (Spring Boot + MySQL)

A lightweight URL Shortener built using Spring Boot.  
It converts long URLs into short codes, stores them in MySQL, and redirects instantly when the short URL is opened.

---

## 🚀 Features

- Generate short URLs for any long link  
- Redirect short URL → original URL  
- Track click counts  
- View stored URL details  
- Delete a short URL  
- MySQL-backed storage  
- Clean REST API design  

---

## 📌 API Endpoints

### 1. Create Short URL  
**POST** `/api/shorten`

Request Body:
```json
{
  "longUrl": "https://www.youtube.com"
}
```

Example Response:
```json
{
  "shortUrl": "http://localhost:8080/rqEDAfp",
  "shortCode": "rqEDAfp"
}
```

---

### 2. Redirect to Original URL  
**GET** `/{shortCode}`  

Example:  
`http://localhost:8080/rqEDAfp` → redirects to YouTube.

---

### 3. Get URL Info  
**GET** `/api/info/{shortCode}`  

Response:
```json
{
  "longUrl": "https://www.youtube.com",
  "shortCode": "rqEDAfp",
  "clickCount": 5,
  "createdAt": "2025-12-09T14:22:01"
}
```

---

### 4. Delete a Short URL  
**DELETE** `/api/{shortCode}`  

Deletes the entry from the database.

---

## 🛠 Tech Stack

- Java 17  
- Spring Boot 4  
- Spring Web  
- Spring Data JPA  
- MySQL  
- Hibernate  
- Lombok  
- Thunder Client / Postman  

---

## ⚙️ How to Run the Project

### 1. Create MySQL Database
```sql
CREATE DATABASE url_shortener;
```

### 2. Update `application.properties`
```properties
server.port=8080
spring.application.name=urlshortener

spring.datasource.url=jdbc:mysql://localhost:3306/url_shortener?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

app.base-url=http://localhost:8080
```

### 3. Start the Backend
```bash
mvn spring-boot:run
```

### 4. Test the API  
Use Thunder Client or Postman.

---

## 📸 Example Screenshot  
(Add your screenshot showing POST request and response.)

---

## 🔮 Future Improvements

- Custom aliases (user-defined short codes)  
- Expiration time for links  
- Analytics dashboard  
- Rate limiting  

---

## 👩‍💻 Author

**Isha Gupta**  
Built while learning Spring Boot & backend system design.
