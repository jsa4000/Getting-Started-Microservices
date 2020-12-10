
String basePath = 'multiBranch'
String jobDisplayName = 'Another Multibranch Pipeline'
String jobDescription = 'Multi-branch pipeline job for general purposes'
String jobRepository = 'https://github.com/jsantosa-minsait/template-library-java.git'
String jobScriptPath = 'jenkins/Jenkinsfile'

folder(basePath) {
    description 'This example shows how to create multibranch jobs.'
}

multibranchPipelineJob("$basePath/gradle-example5-multibranch") {
    displayName "$jobDisplayName"
    description "$jobDescription"
    branchSources {  
        branchSource {
            source {
                git {
                    id('gradle-example5-multibranch')
                    remote("$jobRepository")
                    credentialsId('bab13867-4a3f-4aed-8b67-991846519b60')
                    traits {
                        headWildcardFilter {
                            includes('(main|develop|release-.*|feature-.*|hotfix-.*)')
                            excludes('')
                        }
                    }
                }
            }
            strategy {
                defaultBranchPropertyStrategy {
                    props {
                        noTriggerBranchProperty()
                    }
                }
            }
        }
    }
    configure {
        def traits = it / sources / data / 'jenkins.branch.BranchSource' / source / traits
        traits << 'jenkins.plugins.git.traits.BranchDiscoveryTrait' {}
    }
    triggers {
        periodic(2)
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

