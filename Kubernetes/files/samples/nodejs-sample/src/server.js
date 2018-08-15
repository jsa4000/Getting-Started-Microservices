var http = require('http');
var os = require("os");

var handleRequest = function(request, response) {
  var hostname = os.hostname();
  console.log('Received request for URL: ' + request.url);
  response.writeHead(200, {'Content-Type': 'text/html'});
  response.write('<h1>Hello World!</h1>');
  response.write('<h1>HOSTNAME: '  + hostname + '</h1>');
  response.end();
};
var www = http.createServer(handleRequest);
www.listen(8080);