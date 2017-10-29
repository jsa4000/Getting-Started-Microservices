#tool nuget:?package=GitVersion.CommandLine&version=3.6.5
#tool nuget:?package=Wyam&version=1.0.0
#tool nuget:?package=NUnit.ConsoleRunner&version=3.4.0

#addin nuget:?package=Cake.Wyam&version=1.0.0
#addin nuget:?package=Cake.Incubator&version=1.6.0

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

//Define Version from Git
GitVersion versionInfo = null;

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
      
      /* 
      MSBuild(solution, new MSBuildSettings 
      {
        Verbosity = Verbosity.Minimal,
        ToolVersion = MSBuildToolVersion.VS2017,
        Configuration = configuration,
        ArgumentCustomization = args => args.Append("/p:SemVer=" + versionInfo.NuGetVersionV2)
      });
      */
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

Task("GitVersionInfo")
    .IsDependentOn("Doc-Build")
    .Does(() =>
{
    // Get the Current Info from the current repository
    versionInfo = GitVersion(new GitVersionSettings { RepositoryPath = "." });
    // Dump the info into screen
    Information("{0}", versionInfo.Dump());
});

Task("Preview")
    .Does(() =>
{
    Wyam(new WyamSettings
    {
        Preview = true
    });
});

//////////////////////////////////////////////////////////////////////
// TASK TARGETS
//////////////////////////////////////////////////////////////////////

Task("Default")
    .IsDependentOn("SetVersionInfo");

//////////////////////////////////////////////////////////////////////
// EXECUTION
//////////////////////////////////////////////////////////////////////

RunTarget(target);
