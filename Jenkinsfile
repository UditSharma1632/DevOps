pipeline{
    agent any
    tools{
        maven "MAVEN3"
        jdk "OpenJDK"
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

    }
}