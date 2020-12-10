
String basePath = 'multiBranch'
String jobDisplayName = 'NodeJS Multibranch Pipeline'
String jobDescription = 'Multi-branch pipeline job for NodeJS microservice' 
String jobRepository = 'https://github.com/jsantosa-minsait/template-service-nodejs.git'
String jobScriptPath = 'jenkins/Jenkinsfile'

folder(basePath) {
    description 'This example shows how to create multibranch jobs.'
}

multibranchPipelineJob("$basePath/nodejs-multibranch-pipeline") {
    displayName "$jobDisplayName"
    description "$jobDescription"
    branchSources {  
        git {
            id('nodejs-multibranch-pipeline')
            remote("$jobRepository")
            credentialsId('bab13867-4a3f-4aed-8b67-991846519b60')
            includes('(main|develop|release-.*|feature-.*|hotfix-.*)')
        }
    }
    configure {
        def traits = it / sources / data / 'jenkins.branch.BranchSource' / source / traits
        traits << 'jenkins.plugins.git.traits.BranchDiscoveryTrait' {}
    }
    triggers {
        cron("* * * * *")
    }
    factory {
        workflowBranchProjectFactory {
            scriptPath("$jobScriptPath")
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(-1)
        }
    }
}

