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
output "worker_sg_id" {
  description = "ID du Security Group des nœuds du cluster EKS"
  value       = aws_security_group.eks_worker_sg.id
}

# ------------------------------
# Node Group
# ------------------------------
output "node_group_id" {
  description = "ID du groupe de nœuds EKS"
  value       = aws_eks_node_group.worker_nodes.id
}

output "node_group_arn" {
  description = "ARN du groupe de nœuds EKS"
  value       = aws_eks_node_group.worker_nodes.arn
}

output "node_group_status" {
  description = "Statut du groupe de nœuds EKS"
  value       = aws_eks_node_group.worker_nodes.status
}

# ------------------------------
# IAM Roles
# ------------------------------
output "node_group_role_arn" {
  description = "ARN du rôle IAM pour le groupe de nœuds"
  value       = aws_iam_role.eks_nodegroup_role.arn
}

# ------------------------------
# Cluster EKS existant
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

output "cluster_certificate_authority_data" {
  description = "Certificate authority data du cluster EKS"
  value       = data.aws_eks_cluster.existing.certificate_authority[0].data
}

output "cluster_version" {
  description = "Version du cluster EKS"
  value       = data.aws_eks_cluster.existing.version
}
