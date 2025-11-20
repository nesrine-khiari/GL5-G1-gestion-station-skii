FROM jenkins/jenkins:lts

USER root

# Add Debian 12 (Bookworm) sources (optional if needed)
RUN echo "deb http://deb.debian.org/debian bookworm main contrib non-free" \
    > /etc/apt/sources.list.d/bookworm.list

# Install Java 17, Maven, Git, Docker CLI, and required packages
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    maven \
    git \
    docker.io \
    sudo \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Add Jenkins user to the docker group
RUN usermod -aG docker jenkins

# Optional: allow sudo without password for Jenkins user
RUN echo "jenkins ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

# Switch back to Jenkins user
USER jenkins