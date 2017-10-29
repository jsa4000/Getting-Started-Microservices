# Cake Build

## 1. Install the bootstrapper

The bootstrapper is used to download Cake and the tools required by the build script. This is (kind of) an optional step, but recommended since it removes the need to store binaries in the source code repository.

Install the bootstrapper by downloading it from the Cake Resources repository:

- Windows

   Open a new PowerShell window and run the following command.

	  Invoke-WebRequest https://cakebuild.net/download/bootstrapper/windows -OutFile build.ps1

- Linux

   Open a new shell and run the following command.

	  curl -Lsfo build.sh https://cakebuild.net/download/bootstrapper/linux

- OS X

   Open a new shell and run the following command.

	  curl -Lsfo build.sh https://cakebuild.net/download/bootstrapper/osx

## 2. Create a Cake script

Add a cake script called build.cake to the same location as the bootstrapper script that you downloaded.

	var target = Argument("target", "Default");

	Task("Default")
	  .Does(() =>
	{
	  Information("Hello World!");
	});

    Task("CustomTask")
	  .Does(() =>
	{
	  Information("Hello World!");
	});

	RunTarget(target);

## 3. Run the Cake script

Now you should be able to run your Cake script by invoking the bootstrapper.

- Windows

	./build.ps1

	./build.ps1 -target CustomTask

   > If script execution fail due to the execution policy, you might have to tell PowerShell to allow running scripts. You do this by changing the execution policy. 
   
   		Set-ExecutionPolicy RemoteSigned

- Linux/OS X

   To be able to execute the bash script on Linux or OS X you should give the owner of the script permission to execute it.

	chmod +x build.sh

  When this has been done, you should be able to run your Cake script by invoking the bootstrapper.

	./build.sh

## Note

If you are downloading the build.sh file on a Windows machine (for example, you are using something like Travis CI to run your Linux/OS builds) you can give the script permission to execute using the following command:

	git update-index --add --chmod=+x build.sh
	
If there are problems dealing with references between Nugets and Cake. They can be omited by using the following argument:

	./build.ps1 --settings_skipverification=true



