pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh './gradlew build'
      }
    }
    stage('tests') {
      steps {
        sh './gradlew test'
      }
    }
    stage('artifact') {
      steps {
        sh './gradlew release'
      }
    }
  }
}