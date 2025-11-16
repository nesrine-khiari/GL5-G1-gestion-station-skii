# ------------------------------
# VPC
# ------------------------------
output "vpc_id" {
  description = "ID du VPC"
  value       = aws_vpc.my_vpc.id
}

# ------------------------------
# Subnets
# ------------------------------
output "subnet_ids" {
  description = "Liste des subnets créés"
  value       = [
    aws_subnet.subnet1.id,
    aws_subnet.subnet2.id
  ]
}

# ------------------------------
# Security Groups
# ------------------------------
output "cluster_sg_id" {
  description = "ID du Security Group du cluster EKS"
  value       = aws_security_group.eks_cluster_sg.id
}

output "worker_sg_id" {
  description = "ID du Security Group des nœuds du cluster EKS"
  value       = aws_security_group.eks_worker_sg.id
}

# ------------------------------
# Optionnel : cluster EKS existant
# ------------------------------
data "aws_eks_cluster" "existing" {
  name = var.cluster_name
}

data "aws_eks_cluster_auth" "existing" {
  name = var.cluster_name
}

output "cluster_endpoint" {
  description = "Endpoint du cluster EKS existant"
  value       = data.aws_eks_cluster.existing.endpoint
}

output "cluster_name" {
  description = "Nom du cluster EKS existant"
  value       = data.aws_eks_cluster.existing.name
}

output "cluster_role_arn" {
  description = "ARN du rôle IAM du cluster EKS existant"
  value       = data.aws_eks_cluster.existing.role_arn
}
