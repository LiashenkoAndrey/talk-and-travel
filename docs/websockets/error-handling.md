# Error Handling in WebSocket requests

User can receive messages about errors, occurred during
their websocket requests. To do so, they need to subscribe 
to `/user/{userId}/errors` endpoint. Every user will receive
errors related to them and do not receive errors from other
users.

examples of error responses:
```json
{
  "message": "Chat with id 1 not found",
  "httpStatus": "NOT_FOUND",
  "timestamp": "2024-08-16T17:26:50.7608019+03:00"
}
```

```json
{
  "message": "User with id 1 already JOINED the chat 485",
  "httpStatus": "CONFLICT",
  "timestamp": "2024-08-16T17:26:57.6660072+03:00"
}
```
Also watch demo video https://youtu.be/9ZwxjuiLS9Y