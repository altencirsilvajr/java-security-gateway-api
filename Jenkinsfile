pipeline {
  agent any
  tools { jdk 'temurin-25'; nodejs 'node-24' }
  stages {
    stage('Backend') { steps { sh './mvnw --batch-mode verify' } }
    stage('Frontend') {
      steps {
        dir('frontend') {
          sh 'npm ci'
          sh 'npm run test:ci'
          sh 'npm run build'
        }
      }
    }
  }
}
