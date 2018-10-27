var express = require('express');
var app = express();
var os = require("os");
var hostname = os.hostname();

// Constants
const PORT = 3000;
const HOST = '127.0.0.1';

app.get('/', function (req, res) {
  res.send(hostname);
});

app.listen(PORT, function () {
  console.log('Server Started');
});

console.log(`Running on http://${HOST}:${PORT}`);