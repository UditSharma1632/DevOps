pipeline{
    agent any

    environment {
        SONAR_TOKEN = credentials('Sonar-Cloud-Token') 
    }
    
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

        stage('SonarCloud Analysis') {
            steps {
                script {
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

}
