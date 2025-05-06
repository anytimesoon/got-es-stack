# Getting started

Ensure you have docker compose installed and run

```
docker compose up -d
```

While the images are downloading, import the postman collection. Once the docker stack is running and stable, run the postman request called `initialize elastic search`.

Running all the tests in the postman `Full Inegration tests` collection sequentially. These are not automated and need to be run manually one by one. 
# Data flow diagram
![image](https://github.com/user-attachments/assets/b9df7c9f-5fc5-4d71-a24d-0a0e9bfb3191)

# Considerations

## Data modelling
For there to be a single search endpoint capable of returning either an actor or a character along with its counterpart, there would need to be a single elastic search index containing all actor and character data. 

The SQL database schema requires these two types to each have their own table, presenting a technical challenge where these need to be kept in sync.

## Database replication
The two main ways appear to be triggers to change data capture. For this project, I decided to use CDC due it the fast speed and low impact on database performance.

Debezian is a tool designed to read the postgres wal, convert them to json and push them to a queue.

## Data mutation
My initial attempt was to use a tool like logstash to push ths received messages to elastic search. After some attempts, I found that it's built in data mutation was not powerful enough to produce what was required. Specifically, it was impossible to join an actor to a character after they had been created.

My solution to this was to build a dedicated consumer. The design is a bit thrown together due to time constraints, but it performs all the required tasks. Before making any writes to elastic search, it tries to find an existing entry. This is to ensure no duplication of data occurs. After that, it either creates, updates or deletes the appropriate document.

It would require more testing before releasing, as I think it might behave funnily with some edge cases.

