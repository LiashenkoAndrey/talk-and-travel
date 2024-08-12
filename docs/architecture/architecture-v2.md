# Architecture V2

Old architecture didn't allow to meet some requirements:

- create different rooms according to interests;
- send a private message, show unread messages;
- to be able to reply to the messages of another user in the chat.

Country class had two purposes: it represented a country itself with name and
flagCode, and in same time it played a Chat role.

Now we extracted Chat class from Country and separated them.
But we still follow the main idea of the App - that Country is a main element.
Country is a main unit, representing a topic of individual chat.
Every Country has its own chat, that is described as Main Country Chat.
Chat's name equals Country name. Also, Country have an ability to have many
chats with different topics. But for now we stick with "One Country have One Chat".

So Chat class has its name, description, type (GROUP or PRIVATE), creation date,
List of users (participants), List of Messages and List of Events.

Group chat allows many user to join.
Private chat is one-to-one chat (allows 2 users join the chat)

Message class has content (text), creation date, sender (user who sends this message),
chat (chat, which this message is sent to), repliedMessage (other message on which current message
replies).

Event class has EventType (JOIN, LEAVE, START_TYPING, STOP_TYPING) to describe what happened,
user (who is sending this event), chat(where event is sent), eventTime.

User also has special tables in database, where their connections (subscriptions) with Countries and
Chats are stored.

It means that Chat exists without any user knows about it. Chat just exists. Chat do not belong to
someone (user).
User may JOIN the chat. If user joins the chat, they are added into list of Chat's users (
participants),
and connections of this user and this chat (and country, which this chat belongs to) is stored in
database.
Yes, chat should belong to some country. Now every chat should belong to some country.
I explain this as App's feature. This app encourages communications between people inside a topic of
some
Country. So for now, every Chat belongs to some country. Chat can't exist beyond country.

So, when user JOIN the chat, they appear in Chats users list, and connections between this user and
this chat (and country)
are persisted.
