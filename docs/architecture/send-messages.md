# Send Messages

Sending messages is working on WebSockets using STOMP protocol and MessageBroker.

1. Step one is to establish WebSocket connection between client (frontend app) and backend server.
   to do so, use Handshake url https://talk-and-travel.online:443/ws
2. Server exposes many endpoints which client may subscribe and then receive incoming messages.
   To receive messages from some chat, client should subscribe to `/countries/{chatId}/messages`
3. To send message to some chat's endpoint, client should send WebSocket request to `/chat/messages`
   with payload

```json
{
  "content": "Hello world!",
  "chatId": 1,
  "senderId": 1
} 
```

When someone sends message, all clients that are subscribed to endpoint with that 'chatId' will
receive that message.