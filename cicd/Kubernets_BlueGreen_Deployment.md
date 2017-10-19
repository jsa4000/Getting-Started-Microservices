# Kubernets & Blue-Green Development

##1. Create Virtual Machine

Create the Base Virtual Machine Image using - Ubuntu 16.04 ISO

First, Update/Updrage Linux Packages 

	$ sudo apt-get update
	$ sudo apt-get upgrade
	
To change the host name use the following command:

	$ sudo hostname <new_name>

###1.1 Install SSH

Install SSH on Linux 

	$ sudo apt-get install openssh-server openssh-client
	
Install scp to transfering filesbetween machines using SSH:

	$ sudo apt-get install scp

###1.2 Configure SSH in NAT

When NAT Neworks is selected use the following Options in Network -> Advanced -> Port Forwarding

Create a New Entry in Port Forwarding with the following configuration:

	Name: SSH
	Host IP: 127.0.0.1		-> Virtual Machine Host
	Host Port: 2222
	Guest IP: 10.0.2.15		-> Virtual Machine Instance
	Guest Port: 22

Then enable ssh in the Guest VM and use the following command from a bash console:

	$ ssh -p 2222 root@127.0.0.1

To transfer files using scp between guest and host, use the following commands depending on the case:

	$ scp file.txt user@host:/home/user -> To Linux OS User
	$ scp file.txt user@host:/c/temp	-> To windows c:\Temp
	$ scp -r -P 2222 file.txt user@host:/home/user
	$ scp user1@host1:/home/user1/archivo.txt user2@host2:/home/user2/

###1.3 Installing Docker CE
	
Update Dependencies and Install modules needed

	$ sudo apt-get update
	
	$ sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common
	
Add GPG Docker's offcial Key
	
	$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

Set stable repository using the current Linux distribution	
	
	$ sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"

	$ sudo apt-get update
	
Install lastest distribution of Docker CE	
	
	$ sudo apt-get install docker-ce
	
On Production systems, you should install a specific version of Docker CE instead of always using the latest.

	$ apt-cache madison docker-ce
	$ sudo apt-get install docker-ce=<VERSION>
	
Test Docker installation by running a Test image from Docker Hub

	$ sudo docker run hello-world

##2. Create NodeJS Express API

The idea is to create a simple Restful application using NodeJS and ExpressJS

###2.1 Installing Node.js

On Windows to install npm and Node.js follow the Website instruction detailed. 
It can be used installation packages or binaries.

> https://nodejs.org/en/download/

To update npm simply run the following command:

	npm -update

To check the npm version installed run the following command:	

	npm -v

On Ubuntu use the following commands

	$ sudo apt-get install nodejs
	$ sudo apt-get install npm
	$ node -v

###2.2 Create Simple Project

In order to create a Node.js project, follow the next steps:

Create a folder for the project.

	$ mkdir myapp
	$ cd myapp
	
Initialize the Project by creating the *package.json* file. This command will guide you through some steps to configure properly the project.

	$ npm init

Install *express* package locally onto the project (default mode). This will create a directory *node_modules* with all the dependences installed locally. 
> Using the option *--save* will update package.json with the dependence installed for portability.
	
	$ npm install express --save
	
To Install depedencies from a package.json file, use the following command in the root folder:

	$ npm install 
	
Create a file called app.js with the source code.

	$ touch app.js
	
Add the following code (app.js) with a simple Express application. This app will return the name of the current Host.

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
	
	// Quotes are different to include $ constants
	console.log(`Running on http://${HOST}:${PORT}`);

###2.3 Run/Test Project

The *package.json* file should be similar to tis one.

	{
	  "name": "sample_web_api",
	  "version": "1.0.0",
	  "description": "This is a basic example to run a Restful API using express and nodejs",
	  "main": "app.js",
	  "scripts": {:
	  	"start": "node app.js",
	    "launch": "node app.js"
	  },
	  "author": "Author Name <author.name@example.com>",
	  "license": "ISC",
	  "dependencies": {
		"express": "^4.15.4"
	  }
	}

> Ubuntu and debian distribution has renamed "node" into "nodejs" -> "start": "nodejs app.js"
	
There are several ways to run an application:

- Run the application using the manual way:

	$ node app.js
	
- Using the entry point specified in "main"

	$ npm start
	
- Using one of the scripts defined in "scripts"

	$ npm run-script launch
	
	
Test the application using a Web Browser with the following URL. You must see your hostname onto the screen.

	http://127.0.0.1:3000


##3. Create Docker Container

This idea is to dockerize a node.js app into a Docker Container

###3.1 Copy Project to Docker intallation Server

First Copy the entire Project data and dependences, into the desired folder to start creating the Docker container.

Install 7z on Linux to Compress/Uncpress files

	$ sudo apt-get install p7zip-full

Compress the project into a file called "Source.7z"

	$ 7za a myfiles.7z myfiles/

Copy the file (origin) into the desired folder (destination) to create the Docker image:

	$ scp -r -p 2222 Source.7z user@127.0.0.1:/home/user

Unzip the content (use "x" or "e" options and "o" for the Output Folder).

	$ 7za x myfiles.7z -omyfiles


###3.3 Create Docker File

Create an empty docker file

	$ touch Dockerfile
	
Open the file using an editor 

	$ vim Dockerfile
	
Add following content to the file

	FROM node:argon

	# Create app directory
	RUN mkdir -p /usr/src/sample_web_api
	WORKDIR /usr/src/sample_web_api

	# Install app dependencies
	COPY package.json /usr/src/sample_web_api
	RUN npm install

	# Bundle app source
	COPY . /usr/src/sample_web_api

	EXPOSE 3000
	CMD [ "npm", "start" ]

Save the content to the file. Press "ESC" and ":wq", to save and quit.

###3.3 Build Docker Image

Build image using previous Dockerfile using the tagname

	$ sudo docker build -t <your username>/sample_web_api .

Verify image has been correctly built and it is showed in the image list

	$ sudo docker images

###3.4 Run Docker Image

Run current image:

	$ sudo docker run -p 3000:3000 -d <your username>/node-web-app	
	
> Use -d for detached mode
> Use -p por port mapping
	
Print the output of the app

	$ sudo docker ps
	$ sudo docker logs <container id>

In order tu run the image in interactive mode, use the following command:

	$ sudo docker exec -it [container-id] /bin/bash
	
###3.5 Test Docker Image

Install curl on Linux

	$ sudo apt-get install curl):

To test call your app using curl:

	$ curl -i localhost:3000	
	
