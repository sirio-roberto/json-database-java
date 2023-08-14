# JSON Database Client-Server Project
A client-server project for interacting with a JSON-like database through command-based requests.

## Overview
This project implements a client-server architecture to perform various operations on a JSON-like database. The client sends commands in a specified JSON format, and the server processes these commands to perform actions such as getting, setting, and deleting data.

## Client
The client application accepts command-line arguments for defining the type of request, key, and value (if applicable). It can also read commands from a file.

### Usage
To run the client:
```sh
java -jar client.jar -t [type] -k [key] -v [value] -in [file]
```
- `-t`: Type of request (`set`, `get`, `delete`, `exit`).
- `-k`: Key for the request.
- `-v`: Value for the `set` request.
- `-in`: Name of a file located in the `/client/data` folder containing the JSON request.

### Sample Requests
```json
{"type":"get","key":["person"]}
{"type":"set","key":["person"],"value":{"name":"Elon Musk","car":{"model":"Tesla Roadster","year":"2018"}}}
{"type":"delete","key":["person","car","year"]}
{"type":"exit"}
```

## Server
The server application listens for incoming client connections and processes requests. It communicates with a JSON-like database to execute operations based on the client's commands.

### Usage
To run the server:
```sh
java -jar server.jar
```

### Sample Responses
The server responds with JSON objects containing success or error messages:
```json
{"response":"OK","value":null}
{"response":"OK","value":"{\"name\":\"Elon Musk\",\"car\":{\"model\":\"Tesla Roadster\",\"year\":\"2018\"}}"}
{"response":"ERROR","reason":"No such key"}
```

## Dependencies
- `Gson Library`: Used for JSON parsing.

## Getting Started
1. Clone this repository to your local machine.
2. Compile the client and server using the provided `build.sh` script.
3. Run the server and client with appropriate arguments.

## Contributors
- Sírio Júnior