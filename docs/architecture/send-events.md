# Send Events

Sending Events is working on WebSockets using STOMP protocol and MessageBroker.

1. Step one is to establish WebSocket connection between client (frontend app) and backend server.
   to do so, use Handshake url https://talk-and-travel.online:443/ws
2. Server exposes many endpoints which client may subscribe and then receive incoming messages (
   events).
   To receive Events from some chat, client should subscribe to `/countries/{chatId}/events`
3. Now there are four Event Types: JOIN, LEAVE, START_TYPING, STOP_TYPING.

- JOIN. To send JOIN event, client sends Websocket request to `/chat/events.joinChat` with payload

```json
{
  "authorId": 1,
  "chatId": 4
} 
```

If User not already joined this chat, they will be added to this chat and connections between user
and
chat (and country) will be persisted.
After that all clients that are subscribed to `/countries/{chatId}/events` will receive incoming
message (event)

```json
{
  "id": 37,
  "authorId": 1,
  "chatId": 4,
  "eventType": "JOIN",
  "eventTime": "2024-08-11T08:50:59.607196"
}
```

If some error occurs during this process, client will not receive any messages (and i don't like
this. Client should know if something goes wring).

- LEAVE. To send LEAVE event, client sends Websocket request to `/chat/events.leaveChat` with payload

```json
{
  "authorId": 1,
  "chatId": 4
} 
```

If User was joined this chat, they will be removed from this chat and connections between user
and chat (and country) will be removed.
After that all clients that are subscribed to `/countries/{chatId}/events` will receive incoming
message (event)

```json
{
   "id": 38,
   "authorId": 1,
   "chatId": 4,
   "eventType": "LEAVE",
   "eventTime": "2024-08-11T08:55:05.42974"
}
```


- START_TYPING. To send START_TYPING event, client sends Websocket request to `/chat/events.startTyping` with payload

```json
{
  "authorId": 1,
  "chatId": 3
} 
```

After that all clients that are subscribed to `/countries/{chatId}/events` will receive incoming
message (event)

```json
{
   "id": 39,
   "authorId": 1,
   "chatId": 3,
   "eventType": "START_TYPING",
   "eventTime": "2024-08-11T08:56:53.411722"
}
```


- STOP_TYPING. To send STOP_TYPING event, client sends Websocket request to `/chat/events.stopTyping` with payload

```json
{
  "authorId": 1,
  "chatId": 1
} 
```

After that all clients that are subscribed to `/countries/{chatId}/events` will receive incoming
message (event)

```json
{
   "id": 40,
   "authorId": 1,
   "chatId": 1,
   "eventType": "STOP_TYPING",
   "eventTime": "2024-08-11T08:58:07.633556"
}
```


