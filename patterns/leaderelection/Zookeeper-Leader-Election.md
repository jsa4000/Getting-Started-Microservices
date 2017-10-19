ZooKeeper Leader Election
========================

To complete my M.S.c in Informatics Engineering at Lisbon University, I was part of the Navigators research team [(navigators.di.fc.ul.pt](http://navigators.di.fc.ul.pt)). The investigation project was ReD ([http://red.lsd.di.uminho.pt](http://red.lsd.di.uminho.pt)) and my thesis consists of Coordination Services for Replicated Databases.

To achive my goals, I implemented a leader election service with the help of ZooKeeper, based on one of it recipes ([http://zookeeper.apache.org/doc/trunk/recipes.html](http://zookeeper.apache.org/doc/trunk/recipes.html)).

ZooKeeper
---------

ZooKeeper is a service for coordinating processes of distributed applications. It is often a part of critical infrastructure and it aims to provide a simple and high performance kernel for building complex coordination primitives.

See also: [Apache ZooKeeper](http://zookeeper.apache.org/)

Instructions
------------

1.
Edit files `/config/zoo1.cfg`, `zoo2.cfg` and `zoo3.cfg`. Locate the line `dataDir=zoodb/server**X**`. Replace `zoodb/server**X**` with the full path to the corresponding "database" folders. In my example that would be `zoodb/server1`, `zoodb/server2` and `zoodb/server3`.

2.
Locate the classes `zookeeper.server.ZooKeeperServerOne`, `ZooKeeperServerTwo`, `ZooKeeperServerThree`. At line 22: `qpc.parse("config/zoo1.cfg");` replace `config/zoo1.cfg` with full path for each `zooX.cfg` file.

3.
Initialize ZooKeeper servers: `java -cp bin/:lib/log4j-1.2.16.jar:lib/zookeeper-3.3.1.jar zookeeper.server.ZooKeeperServerOne` or `./zkserver`. Note: It is possible to run ZooKeeper with only one server, but if you want you can also start `ZooKeeperServerTwo` and/or `ZooKeeperServerThree`.

That's it. You now have a running ZooKeeper server, ready to receive requests.

Contact
-------

Please, feel free to contact me with any question at:

[@ruiposse](https://twitter.com/#!/ruiposse)

ruiposse [at] gmail [dot] com

<hr>

###### © Rui Posse 2011. ######