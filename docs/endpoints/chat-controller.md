# Chat endpoints

## 1. GET `/api/chats` find all chats.

returns `List<ChatInfoDto>` in paged view.
So here you can use page request params:

- size=5
- page=0
- sort=name,asc

*response*

```json
{
  "content": [
    {
      "id": 724,
      "name": "Zimbabwe",
      "description": "Zimbabwe main chat",
      "chatType": "GROUP",
      "creationDate": "2024-08-07T13:29:36.256842",
      "usersCount": 0,
      "messagesCount": 0,
      "eventsCount": 0
    },
    {
      "id": 723,
      "name": "Zambia",
      "description": "Zambia main chat",
      "chatType": "GROUP",
      "creationDate": "2024-08-07T13:29:36.255841",
      "usersCount": 0,
      "messagesCount": 0,
      "eventsCount": 0
    },
    {
      "id": 721,
      "name": "Yemen",
      "description": "Yemen main chat",
      "chatType": "GROUP",
      "creationDate": "2024-08-07T13:29:36.251841",
      "usersCount": 0,
      "messagesCount": 0,
      "eventsCount": 0
    },
    {
      "id": 668,
      "name": "Western Sahara",
      "description": "Western Sahara main chat",
      "chatType": "GROUP",
      "creationDate": "2024-08-07T13:29:36.186841",
      "usersCount": 0,
      "messagesCount": 0,
      "eventsCount": 0
    },
    {
      "id": 719,
      "name": "Wallis and Futuna",
      "description": "Wallis and Futuna main chat",
      "chatType": "GROUP",
      "creationDate": "2024-08-07T13:29:36.249841",
      "usersCount": 0,
      "messagesCount": 0,
      "eventsCount": 0
    }
  ],
  "page": {
    "size": 5,
    "number": 0,
    "totalElements": 240,
    "totalPages": 48
  }
}
```

## 2. GET to `/api/chats/{country}/main` Get Main Country Chat

where {country} is country name. It is unique for every country.
*response*

```json
{
  "id": 485,
  "name": "Aruba",
  "description": "Aruba main chat",
  "chatType": "GROUP",
  "creationDate": "2024-08-07T13:29:35.901908",
  "usersCount": 4,
  "messages": [
    {
      "id": 1,
      "content": "aaaaaa",
      "creationDate": "2024-08-07T13:48:47",
      "senderId": 1,
      "chatId": 485,
      "repliedMessageId": null
    },
    {
      "id": 3,
      "content": "hello world",
      "creationDate": "2024-08-07T19:17:59.71498",
      "senderId": 2,
      "chatId": 485,
      "repliedMessageId": null
    }
  ],
  "events": [
    {
      "id": 1,
      "authorId": 1,
      "chatId": 485,
      "messageType": "JOIN",
      "eventTime": "2024-08-07T13:49:42"
    },
    {
      "id": 6,
      "authorId": 1,
      "chatId": 485,
      "messageType": "LEAVE",
      "eventTime": "2024-08-08T13:08:09.046588"
    },
    {
      "id": 14,
      "authorId": 1,
      "chatId": 485,
      "messageType": "START_TYPING",
      "eventTime": "2024-08-08T13:31:15.99139"
    },
    {
      "id": 16,
      "authorId": 1,
      "chatId": 485,
      "messageType": "STOP_TYPING",
      "eventTime": "2024-08-08T13:31:33.033481"
    }
  ]
}
```

## 3. GET to `/api/chats/{chatId}/user-count` findUserCount

returns number of users in chat (participants)

```json
4
```

## 4. GET to `/api/chats/user/{userId}` findUserChats

Find all chats that related to user (user JOINed to that chats).
*response*

```json
[
  {
    "id": 9,
    "name": "Aruba",
    "description": "Aruba main chat",
    "chatType": "GROUP",
    "creationDate": "2024-08-07T13:29:35.901908",
    "usersCount": 5,
    "messagesCount": 9,
    "eventsCount": 17
  },
  {
    "id": 10,
    "name": "Afghanistan",
    "description": "Afghanistan main chat",
    "chatType": "GROUP",
    "creationDate": "2024-08-07T13:29:35.916134",
    "usersCount": 1,
    "messagesCount": 0,
    "eventsCount": 1
  },
  {
    "id": 11,
    "name": "Angola",
    "description": "Angola main chat",
    "chatType": "GROUP",
    "creationDate": "2024-08-07T13:29:35.918956",
    "usersCount": 1,
    "messagesCount": 0,
    "eventsCount": 1
  }
]
```

## 5. GET to `/api/chats/{chatId}/users` findUsersByChatId

returns a list of participants (users, who joined the chat)
*response*

```json
[
  {
    "id": 2,
    "userName": "srtfdftg",
    "userEmail": "tomas@i.ua",
    "about": null
  },
  {
    "id": 3,
    "userName": "Regina",
    "userEmail": "regina@gmail.com",
    "about": null
  },
  {
    "id": 4,
    "userName": "Regi",
    "userEmail": "regi@gmail.com",
    "about": null
  },
  {
    "id": 5,
    "userName": "test test ",
    "userEmail": "test@test.com",
    "about": null
  },
  {
    "id": 1,
    "userName": "admin",
    "userEmail": "admin@2t.com",
    "about": "about me"
  }
]
```

## 6. GET to `/api/chats/{chatId}/messages?page=0&size=5&sort=creationDate,desc` getChatMessagesOrderedByDate
returns Page of chat's messages, ordered by creation date
```json
{
    "content": [
        {
            "id": 13,
            "content": "new message",
            "creationDate": "2024-08-09T13:18:02.410284",
            "senderId": 1,
            "chatId": 485,
            "repliedMessageId": null
        },
        {
            "id": 12,
            "content": "new message",
            "creationDate": "2024-08-09T13:16:28.971299",
            "senderId": 1,
            "chatId": 485,
            "repliedMessageId": null
        },
        {
            "id": 11,
            "content": "hello world12",
            "creationDate": "2024-08-07T19:27:44.302696",
            "senderId": 2,
            "chatId": 485,
            "repliedMessageId": null
        },
        {
            "id": 10,
            "content": "hello world12",
            "creationDate": "2024-08-07T19:23:07.859707",
            "senderId": 2,
            "chatId": 485,
            "repliedMessageId": null
        },
        {
            "id": 9,
            "content": "hello world",
            "creationDate": "2024-08-07T19:22:33.229864",
            "senderId": 2,
            "chatId": 485,
            "repliedMessageId": null
        }
    ],
    "page": {
        "size": 5,
        "number": 0,
        "totalElements": 9,
        "totalPages": 2
    }
}
```