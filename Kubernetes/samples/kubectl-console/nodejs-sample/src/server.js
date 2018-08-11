var http = require('http');
var os = require("os");

var handleRequest = function(request, response) {
  var hostname = os.hostname();
  console.log('Received request for URL: ' + request.url);
  response.writeHead(200);
  response.end('Hello World! <br> HOSTNAME:' + hostname);
};
var www = http.createServer(handleRequest);
www.listen(8080);