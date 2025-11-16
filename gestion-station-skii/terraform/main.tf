provider "aws" {
  region = var.aws_region
}

# ------------------------------
# VPC
# ------------------------------

resource "aws_vpc" "my_vpc" {
  cidr_block = var.vpc_cidr
  enable_dns_support = true
  enable_dns_hostnames = true

  tags = {
    Name = "${var.cluster_name}-vpc"
  }
}

# Internet Gateway
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.my_vpc.id

  tags = {
    Name = "${var.cluster_name}-igw"
  }
}

# Route Table
resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.my_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "${var.cluster_name}-public-rt"
  }
}

# Subnet 1
resource "aws_subnet" "subnet1" {
  vpc_id                  = aws_vpc.my_vpc.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "us-east-1a"
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.cluster_name}-subnet1"
  }
}

# Subnet 2
resource "aws_subnet" "subnet2" {
  vpc_id                  = aws_vpc.my_vpc.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "us-east-1b"
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.cluster_name}-subnet2"
  }
}

# Associer route table aux subnets
resource "aws_route_table_association" "rta1" {
  subnet_id      = aws_subnet.subnet1.id
  route_table_id = aws_route_table.public_rt.id
}

resource "aws_route_table_association" "rta2" {
  subnet_id      = aws_subnet.subnet2.id
  route_table_id = aws_route_table.public_rt.id
}

# ------------------------------
# Security Groups
# ------------------------------

# Security group for EKS cluster
resource "aws_security_group" "eks_cluster_sg" {
  name        = "eks-cluster-sg-${var.cluster_name}"
  description = "Security group for EKS cluster ${var.cluster_name}"
  vpc_id      = aws_vpc.my_vpc.id

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "eks-cluster-sg-${var.cluster_name}"
  }
}

# Worker nodes SG
resource "aws_security_group" "eks_worker_sg" {
  name        = "eks-worker-sg-${var.cluster_name}"
  description = "Security group for EKS worker nodes ${var.cluster_name}"
  vpc_id      = aws_vpc.my_vpc.id

  ingress {
    from_port       = 0
    to_port         = 65535
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_cluster_sg.id]
  }

  ingress {
    from_port       = 0
    to_port         = 65535
    protocol        = "tcp"
    cidr_blocks     = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "eks-worker-sg-${var.cluster_name}"
  }
}

# ------------------------------
# EKS Cluster
# ------------------------------

resource "aws_eks_cluster" "my_cluster" {
  name     = var.cluster_name
  role_arn = var.role_arn
  version  = "1.30"

  vpc_config {
    subnet_ids = [
      aws_subnet.subnet1.id,
      aws_subnet.subnet2.id
    ]

    security_group_ids = [
      aws_security_group.eks_cluster_sg.id
    ]
  }

  depends_on = [
    aws_iam_role_policy_attachment.eks_cluster_policy,
    aws_iam_role_policy_attachment.eks_vpc_resource_controller
  ]
}

# ------------------------------
# EKS Node Group
# ------------------------------

resource "aws_eks_node_group" "my_node_group" {
  cluster_name    = aws_eks_cluster.my_cluster.name
  node_group_name = "node-group-1"
  node_role_arn   = var.role_arn

  subnet_ids = [
    aws_subnet.subnet1.id,
    aws_subnet.subnet2.id
  ]

  scaling_config {
    desired_size = 2
    max_size     = 3
    min_size     = 1
  }

  depends_on = [
    aws_eks_cluster.my_cluster
  ]
}

# ------------------------------
# IAM Attachments (required for EKS)
# ------------------------------

resource "aws_iam_role_policy_attachment" "eks_cluster_policy" {
  role       = "LabRole"
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
}

resource "aws_iam_role_policy_attachment" "eks_vpc_resource_controller" {
  role       = "LabRole"
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSVPCResourceController"
}
