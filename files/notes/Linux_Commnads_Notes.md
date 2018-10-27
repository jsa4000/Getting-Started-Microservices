# Basic Linux Commands

~$ man [command] 	Help about a command. ej man ifconfig
~$ cd /   			Back to the "/" or Root folder.
~$ pwd				Print Working Directory.
~$ ls				List files and directories in current directory
	ej. $ ls -ls        	These parameters stands for: -l detailed list and -s for size
~$ printenv			Print the enviroment variables configured in the System
~$ cat				To see file/output content 
~$ more				To see file/output content allowing basic scrolling.
~$ less				To see file/output content allowing advanced scrolling (better that "more")
~$ vi 				Simple editor to modify and edit files using the terminal
~$ touch				This command is used to create files in batch or modify dates in files, not content.
~$ grep				Look for a particular word in the output/file specified.
~$ ifconfig			See Network configurations and Adpaters	
	Note: In Ubuntu it is neccesary to be installed first: $ sudo apt install net-tools
~$ netstat			Look the current connections established in the System. TCP,UDP, etc..
	ej. sudo netstat -tupan 	-t stand for tcp conncetions, -u for udp, -p process involved, etc..
~$ locate			Search for any file in the device. For the search it will look into a cached database
~$ which [program]	Search for the binary executable for the program
~$ ping				Do a Ping to the requested Ip or Address. It can be used a counter -c 5
	ej. $ ping -c 5 www.google.com
~$ wget				Download files from networks such as the internet. Can be used to recursively download files
~$ curl				Transfer or Download data from or to a server, using one of the supported protocols (FTP, FTPS, HTTP, HTTPS, IMAP..
~$ uname -a			See current Linux distribution and Kernel version Info.	



2. Outputs and Pipes

~$ printenv | less		   This will redirect the output from printenv to less command
~$ printenv > output.txt    This will create a new file with the content of the printenv out
~$ printenv >> output.txt   This will append the content of the printenv out into the file 


Change IP Address of an Adapter
~$ sudo ifconfig eth0 192.168.0.1 netmask 255.255.255.0

~$ sudo apt-get update

Running sudo apt-get update simply makes sure your list of packages from all repositories and PPA's is up to date. If you do not run this command, you could be getting older versions of various packages you are installing, or worse, dependency issues. If you have just added a PPA and have not updated, nothing from the PPA will work at all as you do not have a package list from that PPA or repository.