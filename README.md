# Shiftly

A simple time tracking application for managing work and break events.

## Features

- **Event Management**: Create, read, update, and delete work & break events
- **Time Tracking**: Track events manually or use the built-in time tracker
- **Dashboard**: Visualize your work and break time
- **Export**: Generate PDF reports of your working events

## Tech Stack

- **Backend**: Java with Spring Boot
- **Frontend**: Vue 3
- **Database**: SQLite

## Getting Started

### Prerequisites

- Java 17
- Node.js and npm
- Gradle

### Installation

1. Clone the repository
```bash
git clone https://github.com/DavidMuth/shiftly.git
cd shiftly
```

2. Start the backend
```bash
cd shiftly-backend/shiftly
./gradlew bootRun
```

3. Start the frontend
```bash
cd shiftly-frontend
npm install
npm run dev
```

## License

MIT
