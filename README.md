# Social Network Website

A full-stack social network website built using Spring Boot, ReactJS, MongoDB, and MySQL. The platform allows users to connect with each other, share posts, send messages, and manage friendships. 

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Architecture](#architecture)
- [Setup Instructions](#setup-instructions)
- [WebSocket Functionality](#websocket-functionality)
- [Contributing](#contributing)
- [License](#license)

## Features
- User registration and authentication
- Profile management with profile picture and bio updates
- Friend system: send, accept, or reject friend requests
- Posting content (text and images)
- Real-time private messaging using WebSocket
- Admin panel for managing users
- Support system where users can submit tickets

## Technologies Used

### Backend
- **Spring Boot** - for building the backend REST APIs
- **MySQL** - relational database used for managing user data, posts, and friendships
- **MongoDB** - used for managing chat messages and storing chat conversations
- **WebSocket** - enabling real-time messaging functionality

### Frontend
- **ReactJS** - used for building the user interface with dynamic, responsive components
- **WebSocket** - used to handle real-time communications for the chat functionality in the UI

## Architecture
This project follows a layered architecture:
- **Backend (Spring Boot)**: Provides RESTful services for user authentication, post management, and chat features. Manages database connections for both MySQL and MongoDB.
- **Frontend (ReactJS)**: A single-page application (SPA) that consumes backend APIs and provides a dynamic interface for users.
- **Databases**: MySQL handles user data and relational information, while MongoDB stores chat messages and handles communication-related persistence.

## Setup Instructions

### Prerequisites
- JDK 17 or later
- Node.js and npm (for ReactJS)
- MySQL and MongoDB installed locally

### Backend Setup

1. Clone the backend repository:
   ```bash
   git clone https://github.com/hduc1103/SocialApp.git
   cd social-network-backend
   ```

2. Configure the MySQL and MongoDB connection strings in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/dbname
   spring.datasource.username=root
   spring.datasource.password=yourpassword

spring.data.mongodb.uri=mongodb+srv://<yourusername>:<yourpassword>.@cluster*.n8qh0.mongodb.net/db_name?retryWrites=true&w=majority&appName=Cluster*
   ```

3. Build and run the backend service:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend Setup

1. Clone the frontend repository:
   ```bash
   git clone https://github.com/hduc1103/SocialApp.git
   cd UI/Frontend/my-app
   ```

2. Install dependencies and start the frontend development server:
   ```bash
   npm install
   npm start
   ```

3. Open `http://localhost:3000` in your browser to interact with the application.

## WebSocket Functionality
- The chat feature is built using WebSocket and enables real-time messaging between users.
- Messages are stored in MongoDB, and conversations are persisted for future reference.
- Users can initiate a chat with their friends, and the frontend connects to WebSocket at `ws://localhost:8080/chat` for real-time communication.
