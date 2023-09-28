pipeline{
    agent any

    // environment {
    //     SONAR_TOKEN = credentials('YOUR_SONAR_TOKEN_ID')
    //     SONAR_PROJECT_KEY = 'your-project-key'
    //     SONAR_ORGANIZATION = 'your-organization-key'
    //     SONAR_HOST_URL = 'https://sonarqube.example.com' // Update with your SonarQube server URL
    // }

    tools{
        maven "MAVEN3"
        jdk "AmazonJDK"
    }

    stages{
        stage('Checkout Source Code'){
            steps{
                git branch: 'master', url: 'https://github.com/UditSharma1632/SpringBootReactiveCRUD.git'
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

        stage('Sonar Analysis') {
            environment {
                scannerHome = tool 'SonarCloud'
            }
            steps {
               withSonarQubeEnv('sonar') {
                   sh '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=UditSharma1632_SpringBootReactiveCRUD \
                   -Dsonar.projectName=SpringBootReactiveCRUD \
                   -Dsonar.sources=src/ \
                   -Dsonar.java.binaries=target/classes/com/reactive/springbootreactivecrud/ \
                   -Dsonar.junit.reportsPath=target/surefire-reports/ \
                   -Dsonar.jacoco.reportsPath=target/jacoco.exec \
                   -Dsonar.java.checkstyle.reportPaths=target/checkstyle-result.xml'''
              }
            }
        }

    }

}
