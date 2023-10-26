pipeline{
    agent any

    environment {
        SONAR_TOKEN = credentials('Sonar-Cloud-Token')
        projectName = "SpringBootReactive"
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_ARTIFACT_VERSION_PREFIX = "1.33."
        NEXUS_URL = "Nexus-LB-1617573302.ap-south-1.elb.amazonaws.com"
        NEXUS_REPOSITORY = "spring-boot-reactive"
        NEXUS_CREDENTIAL_ID = "Nexus-Creds"
    }
    
    tools{
        maven "MAVEN3"
        jdk "AmazonJDK"
    }

    stages{
        stage('Checkout Source Code'){
            steps{
                git branch: 'develop', url: 'https://github.com/UditSharma1632/SpringBootReactiveCRUD.git'
            }
        }

        stage('Build'){
            steps{
                sh 'mvn clean package'
            }
        }

        stage('Unit Test'){
            steps{
                sh 'mvn test'
            }
        }

        stage('Checkstyle Analysis'){
            steps {
                sh 'mvn checkstyle:checkstyle'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarCloud'){
                    def scannerArgs = [
                        "mvn", "sonar:sonar",
                        "-Dsonar.projectKey=UditSharma1632_SpringBootReactiveCRUD",
                        "-Dsonar.organization=uditsharma1632",
                        "-Dsonar.host.url=https://sonarcloud.io",
                        "-Dsonar.login=\$SONAR_TOKEN"
                    ]
                    sh script: scannerArgs.join(' '), returnStatus: true
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 3, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to Quality Gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }

        stage('Upload Artifact'){
            steps{
                nexusArtifactUploader(
                nexusVersion: env.NEXUS_VERSION,
                protocol: env.NEXUS_PROTOCOL,
                nexusUrl: env.NEXUS_URL,
                groupId: 'com.reactive',
                version: env.NEXUS_ARTIFACT_VERSION_PREFIX + env.BUILD_ID,
                repository: env.NEXUS_REPOSITORY,
                credentialsId: env.NEXUS_CREDENTIAL_ID,
                artifacts: [
                    [artifactId: "${projectName}",
                    classifier: '',
                    file: 'target/SpringBootReactiveCRUD-0.0.1-SNAPSHOT.jar',
                    type: 'jar']
                    ]
                )
                
            }
        }

        stage('Deploy jar to WebServer') {
            steps {
                ansiblePlaybook(
                credentialsId: 'web-server-ssh',
                disableHostKeyChecking: true,
                colorized: true,
                installation: 'ansible',
                inventory: 'inventory.inv',
                playbook: 'playbook.yml',
               )
            }
        }

    }
}

