#tool nuget:?package=Wyam&version=1.0.0
#addin nuget:?package=Cake.Wyam&version=1.0.0
#tool nuget:?package=NUnit.ConsoleRunner&version=3.4.0
//////////////////////////////////////////////////////////////////////
// ARGUMENTS
//////////////////////////////////////////////////////////////////////

var target = Argument("target", "Default");
var configuration = Argument("configuration", "Release");

//////////////////////////////////////////////////////////////////////
// PREPARATION
//////////////////////////////////////////////////////////////////////

// Define directories.
var buildDir = "./src/**/bin/" + configuration;

//////////////////////////////////////////////////////////////////////
// TASKS
//////////////////////////////////////////////////////////////////////

Task("Clean")
    .Does(() =>
{
    CleanDirectories(buildDir);
});

Task("Restore-NuGet-Packages")
    .IsDependentOn("Clean")
    .Does(() =>
{
    NuGetRestore("./src/BaseWebApi.sln");
});

Task("Build")
    .IsDependentOn("Restore-NuGet-Packages")
    .Does(() =>
{
    if(IsRunningOnWindows())
    {
      // Use MSBuild
      MSBuild("./src/BaseWebApi.sln", settings =>
        settings.SetConfiguration(configuration));
    }
    else
    {
      // Use XBuild
      XBuild("./src/BaseWebApi.sln", settings =>
        settings.SetConfiguration(configuration));
    }
});

Task("Run-Unit-Tests")
    .IsDependentOn("Build")
    .Does(() =>
{
    NUnit3("./src/**/bin/" + configuration + "/*.Test.dll", new NUnit3Settings {
        NoResults = true
        });
});

Task("Doc-Build")
    .IsDependentOn("Run-Unit-Tests")
    .Does(() =>
{
    Wyam(new WyamSettings
    {
        Recipe = "Docs",
        Theme = "Samson",
        UpdatePackages = true
    });
});

//////////////////////////////////////////////////////////////////////
// TASK TARGETS
//////////////////////////////////////////////////////////////////////

Task("Default")
    .IsDependentOn("Doc-Build");

//////////////////////////////////////////////////////////////////////
// EXECUTION
//////////////////////////////////////////////////////////////////////

RunTarget(target);
