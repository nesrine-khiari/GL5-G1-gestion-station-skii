pipeline {
    agent any

    environment {
        // Docker Hub Configuration
        DOCKER_HUB_USERNAME = 'houcemhbiri'
        DOCKER_IMAGE_NAME = 'gestion-station-ski'
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_CREDENTIALS = 'dockerhub'

        // Docker registry configuration
        registry = "${DOCKER_HUB_USERNAME}/${DOCKER_IMAGE_NAME}"
        registryCredential = 'dockerhub'
        dockerImage = ''

        // Project folder inside repository
        PROJECT_DIR = 'gestion-station-skii'

        // SonarQube Configuration
        SONAR_HOST_URL = 'http://sonarqube:9000'
        SONARQUBE_TOKEN = credentials('SonarQubeToken')

        // Nexus Configuration
        NEXUS_VERSION = 'nexus3'
        NEXUS_PROTOCOL = 'http'
        NEXUS_URL = 'nexus:8081'
        NEXUS_REPOSITORY = 'maven-releases'
        NEXUS_CREDENTIAL_ID = 'nexus-credentials'

        // AWS & Kubernetes Configuration
        terraformDir = 'terraform'
        awsCredentialsId = 'aws-credentials'
        AWS_REGION = 'us-east-1'
        EKS_CLUSTER_NAME = 'mykubernetes'
    }

    tools {
        maven 'Maven'
    }

    stages {
        stage('CHECKOUT GIT') {
            steps {
                script {
                    echo '📥 Checking out code from Git'
                    git branch: 'feature/skier',
                        url: 'https://github.com/nesrine-khiari/GL5-G1-gestion-station-skii.git'
                }
            }
        }

        stage('MVN CLEAN') {
            steps {
                script {
                    echo '🧹 Cleaning Maven project'
                    dir("${PROJECT_DIR}") {
                        sh 'mvn clean'
                    }
                }
            }
        }

        stage('ARTIFACT CONSTRUCTION') {
            steps {
                script {
                    echo '🏗️ ARTIFACT CONSTRUCTION...'
                    dir("${PROJECT_DIR}") {
                        sh 'mvn package -Dmaven.test.skip=true -P test-coverage'
                    }
                }
            }
        }

        stage('UNIT TESTS') {
            steps {
                script {
                    echo '🧪 launching Unit Tests...'
                    dir("${PROJECT_DIR}") {
                        sh 'mvn test'
                    }
                }
            }
            post {
                always {
                    junit "**/target/surefire-reports/*.xml"
                }
            }
        }

        stage('MVN SONARQUBE') {
            steps {
                script {
                    echo '📊 Running SonarQube analysis'
                    dir("${PROJECT_DIR}") {
                        sh "mvn sonar:sonar -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONARQUBE_TOKEN}"
                    }
                }
            }
        }

        stage('PUBLISH TO NEXUS') {
            steps {
                script {
                    echo '📦 Publishing artifact to Nexus'
                    dir("${PROJECT_DIR}") {
                        sh 'mvn deploy -DskipTests'
                    }
                }
            }
        }

        stage('BUILDING OUR IMAGE') {
            steps {
                script {
                    echo '🐳 Building Docker image'
                    dir("${PROJECT_DIR}") {
                        dockerImage = docker.build registry + ":$BUILD_NUMBER"
                        // Also tag as latest
                        sh "docker tag ${registry}:${BUILD_NUMBER} ${registry}:latest"
                    }
                }
            }
        }

        stage('DEPLOY OUR IMAGE') {
            steps {
                script {
                    echo '📤 Pushing Docker image to Docker Hub'
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push()
                        // Push latest tag as well
                        sh "docker push ${registry}:latest"
                    }
                }
            }
        }

        stage('CONFIGURE AWS CREDENTIALS') {
            steps {
                script {
                    echo '🔑 Configuring AWS Credentials'
                    withCredentials([file(credentialsId: awsCredentialsId, variable: 'AWS_CREDENTIALS_FILE')]) {
                        // Read and parse AWS credentials file
                        def awsCredentials = readFile(AWS_CREDENTIALS_FILE).trim().split("\n")

                        // Extract credentials
                        env.AWS_ACCESS_KEY_ID = awsCredentials.find { it.startsWith("aws_access_key_id") }.split("=")[1].trim()
                        env.AWS_SECRET_ACCESS_KEY = awsCredentials.find { it.startsWith("aws_secret_access_key") }.split("=")[1].trim()

                        // Session token (for learner lab)
                        def sessionTokenLine = awsCredentials.find { it.startsWith("aws_session_token") }
                        if (sessionTokenLine) {
                            env.AWS_SESSION_TOKEN = sessionTokenLine.split("=")[1].trim()
                        }

                        echo "✅ AWS credentials configured successfully"
                        echo "AWS Access Key ID: ${env.AWS_ACCESS_KEY_ID[0..10]}***"

                        // Test AWS connection
                        sh """
                            export AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}
                            export AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}
                            export AWS_SESSION_TOKEN=${env.AWS_SESSION_TOKEN}
                            export AWS_DEFAULT_REGION=${AWS_REGION}

                            echo "Testing AWS connection..."
                            aws sts get-caller-identity
                        """
                    }
                }
            }
        }

        stage('SETUP TERRAFORM') {
            steps {
                script {
                    echo '🏗️ Setting up infrastructure with Terraform'

                    // Check if terraform directory exists
                    if (!fileExists(terraformDir)) {
                        error "❌ Terraform directory '${terraformDir}' not found!"
                    }

                    dir(terraformDir) {
                        sh """
                            export AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}
                            export AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}
                            export AWS_SESSION_TOKEN=${env.AWS_SESSION_TOKEN}
                            export AWS_DEFAULT_REGION=${AWS_REGION}

                            echo "Initializing Terraform..."
                            terraform init

                            echo "Validating Terraform configuration..."
                            terraform validate

                            echo "Planning Terraform changes..."
                            terraform plan

                            echo "Applying Terraform configuration..."
                            terraform apply -auto-approve

                            echo "Terraform outputs:"
                            terraform output
                        """
                    }
                }
            }
        }

        stage('UPDATE KUBECONFIG') {
            steps {
                script {
                    echo '☸️ Updating kubeconfig for EKS cluster'
                    sh """
                        export AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}
                        export AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}
                        export AWS_SESSION_TOKEN=${env.AWS_SESSION_TOKEN}
                        export AWS_DEFAULT_REGION=${AWS_REGION}

                        # Update kubeconfig
                        aws eks update-kubeconfig --name ${EKS_CLUSTER_NAME} --region ${AWS_REGION}

                        # Verify connection
                        echo "Verifying cluster connection..."
                        kubectl cluster-info

                        echo "Checking nodes..."
                        kubectl get nodes
                    """
                }
            }
        }

        stage('DEPLOY TO AWS KUBERNETES') {
            steps {
                script {
                    echo '🚀 Deploying application to AWS EKS'

                    // Check if Kubernetes manifests exist
                    if (!fileExists('k8s-deployment.yaml')) {
                        error "❌ k8s-deployment.yaml not found!"
                    }

                    sh """
                        export AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}
                        export AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}
                        export AWS_SESSION_TOKEN=${env.AWS_SESSION_TOKEN}
                        export AWS_DEFAULT_REGION=${AWS_REGION}

                        # Update image tag in deployment
                        sed -i "s|image: houcemhbiri/gestion-station-ski:.*|image: ${registry}:${BUILD_NUMBER}|g" k8s-deployment.yaml

                        # Apply Kubernetes manifests
                        echo "Applying Kubernetes deployment..."
                        kubectl apply -f k8s-deployment.yaml

                        # Wait for deployment to be ready
                        echo "Waiting for deployment to be ready..."
                        kubectl rollout status deployment/gestion-station-ski --timeout=10m

                        # Show deployment status
                        echo "=== DEPLOYMENT STATUS ==="
                        kubectl get deployments

                        echo "=== PODS STATUS ==="
                        kubectl get pods

                        echo "=== SERVICES STATUS ==="
                        kubectl get services

                        echo "=== APPLICATION URL ==="
                        kubectl get service gestion-station-ski-service -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'
                        echo ""
                    """
                }
            }
        }

        // Optional: Destroy infrastructure (uncomment when needed)
        /*
        stage('TEARDOWN TERRAFORM') {
            steps {
                script {
                    echo '💣 Destroying infrastructure'

                    // First, delete Kubernetes resources
                    sh """
                        export AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}
                        export AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}
                        export AWS_SESSION_TOKEN=${env.AWS_SESSION_TOKEN}
                        export AWS_DEFAULT_REGION=${AWS_REGION}

                        echo "Deleting Kubernetes resources..."
                        kubectl delete -f k8s-deployment.yaml --ignore-not-found=true

                        echo "Waiting for LoadBalancer to be deleted..."
                        sleep 60
                    """

                    // Then destroy Terraform infrastructure
                    dir(terraformDir) {
                        sh """
                            export AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}
                            export AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}
                            export AWS_SESSION_TOKEN=${env.AWS_SESSION_TOKEN}
                            export AWS_DEFAULT_REGION=${AWS_REGION}

                            terraform destroy -auto-approve
                        """
                    }
                }
            }
        }
        */
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
            echo "Docker image: ${registry}:${BUILD_NUMBER}"
            echo "Application deployed to AWS EKS cluster: ${EKS_CLUSTER_NAME}"
            echo "Get application URL with: kubectl get service gestion-station-ski-service"
        }
        failure {
            echo '❌ Pipeline failed!'
            echo 'Check the logs above for error details.'
        }
        always {
            echo '🧹 Cleaning up workspace'
            cleanWs()
        }
    }
}
