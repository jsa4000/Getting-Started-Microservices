# Observable Framewrok

[TMForum - ProductCatalog](https://raw.githubusercontent.com/tmforum-apis/TMF620_ProductCatalog/master/TMF620_Product_Catalog_Management.admin.swagger.json)

Here are the steps:

    Export Swagger JSON into a file on your drive. This JSON should be published on your server at the following URI: /swagger/docs/v1
    Go to http://editor.swagger.io/#/
    On the top left corner, select File-> Import File... Point to the local Swagger JSON file you exported in step #1 to open in the Swagger Editor
    Select Generate Client -> Swagger YAML option from the menu
    It will generate the YAML that you can validate at http://www.yamllint.com/ site


## Testing

To launch your application's tests, run:

```
mvn verify
```

For more information, refer to the [Running tests page][].

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./mvnw clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar
```

or

For more information, refer to the [Code quality page][].