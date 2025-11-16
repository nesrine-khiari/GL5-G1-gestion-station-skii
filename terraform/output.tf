output "cluster_endpoint" {
  description = "L'endpoint du cluster EKS"
  value       = aws_eks_cluster.my_cluster.endpoint
}

output "cluster_name" {
  description = "Le nom du cluster EKS"
  value       = aws_eks_cluster.my_cluster.name
}

output "cluster_role_arn" {
  description = "L'ARN du rôle IAM du cluster EKS (LabRole utilisé pour cluster et nodes)"
  value       = aws_eks_cluster.my_cluster.role_arn
}

output "cluster_security_group_id" {
  description = "Security group ID du cluster EKS"
  value       = aws_security_group.eks_cluster_sg.id
}

output "worker_security_group_id" {
  description = "Security group ID des worker nodes"
  value       = aws_security_group.eks_worker_sg.id
}

output "kubeconfig_command" {
  description = "Commande pour mettre à jour kubeconfig"
  value       = "aws eks update-kubeconfig --region ${var.aws_region} --name ${aws_eks_cluster.my_cluster.name}"
}
