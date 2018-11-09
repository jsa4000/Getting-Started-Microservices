# Kafka - Producer/Consumer

## Use Cases

Use cases are going to be split by entities, ``users`` and ``roles``.

### Users

- Create User: the user can **create** an ``user``.
- Modify User: the user can **modify** an ``user``.
- Delete USer: the user can **delete** an ``user``.
- Get User: the user can get the information of an ``user``.
- Get Users: the user can get the list of all ``users``.

### Roles

- Create Role: the user can **create** an ``role``.
- Modify Role: the user can **modify** an ``role``.
- Delete Role: the user can **delete** an ``role``.
- Get Role: the user can get the information of an ``role``.
- Get Roles: the user can get the list of all ``roles``.

## Topics

The pattern that is going to be used is **Event Sourcing**. This way, the events published over the same **entity** are processed in order.

    topic(userEvents): userCreated (id:1) -> userModified (id:1) -> userDeleted (id:1)

Since it is needed to guarantee the order on topics and partitions, The events are going to be partitioned by entities depending on the message key (entity id).

Two **topics** are going to be created on Kafka:

- userEvents
- roleEvents

## Reference

- [Several Event Types in the same topic](https://www.confluent.io/blog/put-several-event-types-kafka-topic/)