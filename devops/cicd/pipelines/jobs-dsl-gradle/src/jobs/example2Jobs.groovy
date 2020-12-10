import com.example.builder.GradleJobBuilder

String basePath = 'example2'
List developers = ['dev1@example.com', 'dev2@example.com']

folder(basePath) {
    description 'This example shows how to create jobs using Job builders.'
}

new GradleJobBuilder()
    .name("$basePath/gradle-project1")
    .description('An example using a job builder for a Gradle project.')
    .ownerAndProject('myorg/project1')
    .emails(developers)
    .build(this)

new GradleJobBuilder()
    .name("$basePath/gradle-project2")
    .description('Another example using a job builder for a Gradle project.')
    .ownerAndProject('myorg/project2')
    .emails(developers)
    .build(this)