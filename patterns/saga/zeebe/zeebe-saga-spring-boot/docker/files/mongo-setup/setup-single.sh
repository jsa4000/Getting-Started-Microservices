#!/bin/bash

MONGODB=mongo

if [ -z "${MONGO_REPLICASET_HOST}" ]; then
      MONGO_REPLICASET_HOST=localhost
fi

echo "**********************************************" ${MONGODB}
echo "Waiting for startup.."
until curl http://${MONGODB}:27017/serverStatus\?text\=1 2>&1 | grep uptime | head -1; do
 printf '.'
 sleep 1
done

echo SETUP.sh time now: `date +"%T" `
mongo --host ${MONGODB}:27017 --username ${MONGO_INITDB_ROOT_USERNAME} --password ${MONGO_INITDB_ROOT_PASSWORD} <<EOF
var cfg = {
   "_id": "rs0",
   "protocolVersion": 1,
   "version": 1,
   "members": [
       {
           "_id": 0,
           "host": "${MONGO_REPLICASET_HOST}:27017"
       }
   ]
};
rs.initiate(cfg, { force: true });
EOF