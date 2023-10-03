pipeline{
    agent any

    environment {
        SONAR_TOKEN = credentials('Sonar-Cloud-Token')
        projectName = "SpringBootReactive"
        // NEXUS_VERSION = "nexus3"
        // NEXUS_PROTOCOL = "http"
        // NEXUS_URL = "172.31.12.215:8081"
        // NEXUS_REPOSITORY = "vprofile-release"
	    // NEXUS_REPO_ID    = "vprofile-release"
        // NEXUS_CREDENTIAL_ID = "nexuslogin"
        // ARTVERSION = "${env.BUILD_ID}"
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
                sh 'mvn install -Dskiptests'
            }

            post{
                success {
                    echo 'Archiving artifacts now'
                    archiveArtifacts artifacts: '**/*.jar'
                }
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
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: '172.31.12.215:8081',
                        groupId: 'com.reactive',
                        version: "${env.BUILD_ID}-${env.BUILD_TIMESTAMP}",
                        repository: 'spring-boot-reactive',
                        credentialsId: 'Nexus-Creds',
                        artifacts: [
                            [artifactId: '${projectName}',
                            classifier: '',
                            file: '/target/*.jar',
                            type: 'jar']
                        ]
                    )
                }
            }

    }
}

