# Country endpoints

## 1. GET `/api/countries/info` find all countries.
returns List of all Countries in database (near 240 items)
*response*
```json
[
    {
        "name": "Aruba",
        "flagCode": "aw"
    },
    {
        "name": "Afghanistan",
        "flagCode": "af"
    },
    {
        "name": "Angola",
        "flagCode": "ao"
    },
    {
        "name": "Anguilla",
        "flagCode": "ai"
    },
    {
        "name": "Albania",
        "flagCode": "al"
    },
    {
        "name": "Alland Islands",
        "flagCode": "ax"
    },
    {
        "name": "Andorra",
        "flagCode": "ad"
    }
]
```

## 2. GET `/api/countries/{countryName}` find Country By Name.

*response*
```json
{
    "name": "Aruba",
    "flagCode": "aw",
    "chats": [
        {
            "id": 485,
            "name": "Aruba",
            "description": "Aruba main chat",
            "chatType": "GROUP",
            "creationDate": "2024-08-07T13:29:35.901908",
            "usersCount": 5,
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
                },
                {
                    "id": 5,
                    "content": "hello world",
                    "creationDate": "2024-08-07T19:18:15.834352",
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
                    "eventType": "JOIN",
                    "eventTime": "2024-08-07T13:49:42"
                },
                {
                    "id": 8,
                    "authorId": 1,
                    "chatId": 485,
                    "eventType": "LEAVE",
                    "eventTime": "2024-08-08T13:09:38.250979"
                }
            ]
        }
    ]
}
```

## 3. GET `/api/countries/user/{userId}` getAllCountriesByUserId
returns list of Countries users subscribes to.
```json
[
    {
        "name": "Angola",
        "flagCode": "ao"
    },
    {
        "name": "Aruba",
        "flagCode": "aw"
    },
    {
        "name": "Afghanistan",
        "flagCode": "af"
    }
]
```